package frame;

import info.laht.dualquat.Quaternion;
//import javax.vecmath.Quat4d;

public class SmallResidue {

	private double x;
	private double y;
	private double z;
	private Quaternion orientation; // TODO make immutable insides
	private double phi;
	private double psi;
	private short[] neighbors;

	public SmallResidue(double x, double y, double z, Quaternion orientation, double phi, double psi, short[] neighbors) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.orientation = orientation;
		this.psi = psi;
		this.phi = phi;
		this.neighbors = neighbors;
	}

	public double getDistance(SmallResidue other) {
		double dx = x - other.x;
		double dy = y - other.y;
		double dz = z - other.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getPhi() {
		return phi;
	}

	public double getPsi() {
		return psi;
	}

	// form universal residue, cluster many together
	// represent transformation to universal residue
	// how to compare two transformations, compose a and b^(-1)
	//  WARNING: modifies q1, normalize before?
	public double getAngle(Quaternion q1) {
		if (q1.w > 1) {
			q1.normalize(); // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
		}
		double angle = 2 * Math.acos(q1.w);
		return angle;
		/*double s = Math.sqrt(1 - q1.w * q1.w); // assuming quaternion normalised then w is less than 1, so term always positive.
		if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
			// if s close to zero then direction of axis not important
			double x = q1.x; // if it is important that axis is normalised then replace with x=1; y=z=0;
			double y = q1.y;
			double z = q1.z;
		} else {
			double x = q1.x / s; // normalise axis
			double y = q1.y / s;
			double z = q1.z / s;
		}*/
	}

}
