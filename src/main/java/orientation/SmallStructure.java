package orientation;

import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
import altaite.geometry.search.GridRangeSearch;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import structure.Residue;
import structure.SimpleStructure;
import util.pymol.PymolVisualizer;

//TODO replace with dumb search?
//visuzalies - find out how biwords were visualized
public class SmallStructure implements Iterable<SmallResidue> {

	private SmallResidue[] residues;
	private Random random = new Random(1);

	public SmallStructure(SimpleStructure simple, StandardResidueOrientation sro) {
		System.out.println(simple.getSource());
		Residue[] fullResidues = simple.getResidueArray();

		/*for (int i = 0; i < fullResidues.length; i++) {
			System.out.print(i + "(" + fullResidues[i].getId() + ") : ");
		}
		System.out.println("");*/
		residues = new SmallResidue[fullResidues.length];
		assert residues.length < Short.MAX_VALUE;
		GridRangeSearch<AtomCoordsResidueIndex> grid = createGrid(fullResidues);
		boolean[] isResidueInContact = new boolean[residues.length]; // not in/out, just memory to work with
		for (int i = 0; i < residues.length; i++) {

			short[] neighbors = findNeighbors(fullResidues[i], grid, isResidueInContact);
			residues[i] = convert(fullResidues[i], sro, neighbors);
			//System.out.println("neighbors " + neighbors.length);
			/*System.out.print(i + "(" + fullResidues[i].getId() + ") : ");
			for (short s : neighbors) {
				System.out.print(s + "(" + fullResidues[s].getId() + ")");
			}
			System.out.println("");*/
		}
		visualizeNeighbors(simple.getSource().toString(), fullResidues);
	}

	private void visualizeNeighbors(String structureName, Residue[] fullResidues) {
		PymolVisualizer py = new PymolVisualizer();
		for (int i = 0; i < residues.length; i++) {
			String name = fullResidues[i].getId().toString();
			Residue[] selection = new Residue[residues[i].getNeighbors().length];
			for (int k = 0; k < selection.length; k++) {
				short n = residues[i].getNeighbors()[k];
				selection[k] = fullResidues[n];
			}
			py.addSelection(name, selection);
		}
		File d = new File("d:/t/data/qsa/experiments/selections/");
		if (!d.exists()) {
			d.mkdir();
		}
		File f = d.toPath().resolve(structureName + ".py").toFile();
		py.saveSelections(f);
	}

	private SmallResidue convert(Residue r, StandardResidueOrientation sro, short[] neighbors) {
		Point p = r.getPosition();
		Quat orientation = sro.getQuaternion(r);
		shuffle(neighbors);
		SmallResidue si = new SmallResidue(p.x, p.y, p.z, orientation, r.getPhi(), r.getPsi(), neighbors);
		return si;
	}

	private void shuffle(short[] a) {
		for (int i = 0; i < a.length; i++) {
			int randomIndexToSwap = random.nextInt(a.length);
			short temp = a[randomIndexToSwap];
			a[randomIndexToSwap] = a[i];
			a[i] = temp;
		}
	}

	// evaluate stability, include only stable ones?
	// sequence avoidance?
	// no equals?
	// boolean[] for hits/residues
	private short[] findNeighbors(Residue residue, GridRangeSearch<AtomCoordsResidueIndex> grid, boolean[] isResidueInContact) {
		Arrays.fill(isResidueInContact, false);
		double[][] atomCoords = residue.getAtoms();
		List<AtomCoordsResidueIndex> inContact = new ArrayList<>();
		int count = 0;
		for (double[] ac : atomCoords) {
			grid.nearest2(ac, 4, inContact);
			for (AtomCoordsResidueIndex index : inContact) {
				if (isResidueInContact[index.getResidueIndex()] == false) {
					count++;
					isResidueInContact[index.getResidueIndex()] = true;
				}
			}
			inContact.clear();
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

	private GridRangeSearch<AtomCoordsResidueIndex> createGrid(Residue[] residues) {
		GridRangeSearch<AtomCoordsResidueIndex> grid = new GridRangeSearch<>(5);
		List<AtomCoordsResidueIndex> atoms = new ArrayList<>();
		for (short i = 0; i < residues.length; i++) {
			double[][] atomCoords = residues[i].getAtoms();
			for (double[] ac : atomCoords) {
				atoms.add(new AtomCoordsResidueIndex(ac[0], ac[1], ac[2], i));
			}
		}
		grid.buildGrid(atoms);
		System.out.println("atoms " + atoms.size());
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
