package structure;

import java.io.Serializable;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import altaite.geometry.primitives.Point;

/**
 *
 * @author Antonin Pavelka
 *
 * Amino acid residue in protein chain.
 *
 */
public class Residue implements Serializable, Comparable<Residue> {

	private static final long serialVersionUID = 1L;
	private Point position_;
	private final ResidueId id_;
	private final int atomSerial;
	private double[][] atoms;
	private String[] atomNames;
	private PhiPsi torsionAngles;
	private Residue next;
	private Residue previous;
	private final int index; // unique id withing structure, 0 .. n - 1, where n is number of residues
	private String name;

	public Residue() {
		id_ = null;
		atomSerial = 1;
		index = 1;
	}

	public Residue(int index, ResidueId id, int atomSerial, double[] carbonAlpha, double[][] atoms,
		String[] atomNames, PhiPsi torsionAngles, String name) {
		this.index = index;
		this.id_ = id;
		this.atomSerial = atomSerial;
		this.position_ = new Point(carbonAlpha[0], carbonAlpha[1], carbonAlpha[2]);
		this.atoms = atoms;
		this.atomNames = atomNames;
		this.torsionAngles = torsionAngles;
		this.name = name;
	}

	public Residue(Residue r) {
		index = r.index;
		position_ = new Point(r.position_);
		id_ = r.id_;
		atomSerial = r.atomSerial;
		name = r.name;
	}

	public void setNext(Residue r) {
		next = r;
	}

	public void setPrevious(Residue r) {
		previous = r;
	}

	private boolean isWithinAhead(Residue r, int distance) {
		int d = 0;
		Residue q = this;
		while (q != null && d <= distance) {
			if (r.getIndex() == q.getIndex()) { // faster than equals
				return true;
			}
			q = q.next;
			d++;
		}
		return false;
	}

	public boolean isWithin(Residue r, int distance) {
		return this.isWithinAhead(r, distance) || r.isWithinAhead(this, distance);
	}

	public Residue getNext() {
		return next;
	}

	public Residue getPrevious() {
		return previous;
	}

	public boolean isFollowedBy(Residue next) {
		return next.getId().isFollowedBy(getId());
	}

	public int getIndex() {
		return index;
	}

	public ResidueId getId() {
		return id_;
	}

	public int getAtomSerial() {
		return atomSerial;
	}

	public Point getPosition() {
		return position_;
	}

	public Point3d getPosition3d() {
		return new Point3d(position_.x, position_.y, position_.z);
	}

	public double[][] getAtoms() {
		return atoms;
	}

	public double[] getAtom(String name) {
		for (int i = 0; i < atoms.length; i++) {
			if (atomNames[i].equals(name)) {
				return atoms[i];
			}
		}
		return null;
	}

	private Point3d p(double[] c) {
		return new Point3d(c[0], c[1], c[2]);
	}

	public Point getCa() {
		return new Point(getAtom("CA"));
	}

	public Point getC() {
		return new Point(getAtom("C"));
	}

	public Point getN() {
		return new Point(getAtom("N"));
	}

	public double distance(Residue other) {
		return position_.distance(other.position_);
	}

	public double[] getCoords() {
		return position_.getCoords();
	}

	public void transform(Matrix4d m) {
		Point3d x = getPosition3d();
		m.transform(x);
		position_ = new Point(x.x, x.y, x.z);
	}

	@Override
	public boolean equals(Object o) {
		Residue other = (Residue) o;
		return id_.equals(other.id_);
	}

	@Override
	public int hashCode() {
		return id_.hashCode();
	}

	@Override
	public int compareTo(Residue other) {
		return id_.compareTo(other.id_);
	}

	public Double getPhi() {
		return torsionAngles.getPhi();
	}

	public Double getPsi() {
		return torsionAngles.getPsi();
	}

	public String getName() {
		return name;
	}

	public static Residue[] merge(Residue[] a, Residue[] b) {
		Residue[] c = new Residue[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	@Override
	public String toString() {
		return id_.toString();
	}

}
