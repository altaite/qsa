package frame;

import altaite.geometry.primitives.Coordinates;
import altaite.geometry.primitives.Point;
import altaite.geometry.search.GridRangeSearch;
import info.laht.dualquat.Quaternion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import structure.Residue;
import structure.SimpleStructure;

public class SmallStructure implements Iterable<SmallResidue> {

	private SmallResidue[] residues;

	public SmallStructure(SimpleStructure simple, StandardResidueOrientation sro) {
		Residue[] fullResidues = simple.getResidueArray();
		residues = new SmallResidue[fullResidues.length];
		assert residues.length < Short.MAX_VALUE;
		GridRangeSearch<Index> grid = createGrid(fullResidues);
		boolean[] isResidueInContact = new boolean[residues.length]; // not in/out, just memory to work with
		for (int i = 0; i < residues.length; i++) {
			short[] neighbors = findNeighbors(fullResidues[i], grid, isResidueInContact);
			residues[i] = convert(fullResidues[i], sro, neighbors);
		}
	}

	private SmallResidue convert(Residue r, StandardResidueOrientation sro, short[] neighbors) {
		Point p = r.getPosition();
		Quaternion orientation = sro.getQuaternion(r);
		SmallResidue si = new SmallResidue(p.x, p.y, p.z, orientation, r.getPhi(), r.getPsi(), neighbors);
		return si;
	}

	// evaluate stability, include only stable ones?
	// sequence avoidance?
	// no equals?
	// boolean[] for hits/residues
	private short[] findNeighbors(Residue residue, GridRangeSearch<Index> grid, boolean[] isResidueInContact) {
		Arrays.fill(isResidueInContact, false);
		double[][] atomCoords = residue.getAtoms();
		List<Index> inContact = new ArrayList<>();
		int count = 0;
		for (double[] ac : atomCoords) {
			grid.nearest(ac, 5, inContact);
			for (Index index : inContact) {
				if (isResidueInContact[index.getResidueIndex()] == false) {
					count++;
					isResidueInContact[index.getResidueIndex()] = true;
				}
			}
		}
		short[] residuesInContact = new short[count];
		int i = 0;
		for (short s = 0; s < isResidueInContact.length; s++) {
			if (isResidueInContact[s]) {
				residuesInContact[i++] = s;
			}
		}
		return residuesInContact;
	}

	private GridRangeSearch<Index> createGrid(Residue[] residues) {
		GridRangeSearch<Index> grid = new GridRangeSearch<>(5);
		List<Index> atoms = new ArrayList<>();
		for (short i = 0; i < residues.length; i++) {
			double[][] atomCoords = residues[i].getAtoms();
			for (double[] ac : atomCoords) {
				atoms.add(new Index(ac[0], ac[1], ac[2], i));
			}
		}
		grid.buildGrid(atoms);
		return grid;
	}

	public int size() {
		return residues.length;
	}

	public SmallResidue[] getResidues() {
		return residues;
	}

	@Override
	public Iterator<SmallResidue> iterator() {
		return Arrays.asList(residues).iterator();
	}
}

class Index implements Coordinates {

	private short index;
	private double[] coords = new double[3];

	public Index(double x, double y, double z, short residueIndex) {
		this.coords[0] = x;
		this.coords[1] = y;
		this.coords[2] = z;
		this.index = residueIndex;
	}

	public double[] getCoords() {
		return coords;
	}

	public short getResidueIndex() {
		return index;
	}

}
