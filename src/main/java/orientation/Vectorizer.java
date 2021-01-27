package orientation;

import altaite.geometry.primitives.AxisAngle;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
import altaite.geometry.primitives.Versor;
import altaite.geometry.superposition.Superposer;

public class Vectorizer {

	private Point[] standardObject;
	private Superposer superposer = new Superposer();

	// interface? default?
	public Vectorizer(Point[] standardObject) {
		this.standardObject = standardObject;
	}

	public double[] getCoordinates(Point[] body) {
		int n = body.length / 2;
		Point[] a = new Point[n];
		Point[] b = new Point[n];
		for (int i = 0; i < n; i++) {
			a[i] = body[i];
		}
		for (int i = 0; i < n; i++) {
			b[i] = body[i + n];
		}
		return getCoordinates(a, b); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! a a
	}

	/// compare both to RMSD and system based on rotations plus distance after superposition?
	/// intermediate step, for testing and analyses purposes
	public double[] getCoordinates(Point[] r1, Point[] r2) {
		// align first residue to std (triangle) - q
		// subtract r1 from coords of r2
		// apply the rotattion q
		// resulting coords are 3 wanted ones
		// other 3 are axis multiplied by angle of r1 -> r2
		Quat q = align(r1, standardObject);// 
		Point translation = Point.average(r2).minus(Point.average(r1));
		System.out.println("---");
		Point t = q.rotate(translation);
		double translationSize = t.size();
		System.out.println(translation);
		System.out.println(t);
		t = t.normalize();
		Quat rotation = align(r1, r2);
		//System.out.println("w " + rotation.w + " " + rotation.x + " " + rotation.y + " " + rotation.z);
		AxisAngle aa = rotation.toAxisAngle();
		Point axis = aa.getAxis();

		// angle is generated small
		// here is 2pi, which is fine too
		// is there any real problem?
		// test only rotation, or fixed distance
		// !!!!! the logic of axis angle coordinates, cyclical somehow
		// try numerical solution anyway? but how to treat cyclicity
		// we had some idea of quaternion similarity
		// try constructing -vector, opposite angle and see if it is closer?
		// just try both options?
		// test it for rotation only
		// possibly generate two quaternions, derive third, and check coord diff corresponds to angle
		// quat -> two angles, pick smaller - measure of similarity of two quats
		// coords of quats - vector multiplied by angle, two options??? think about the options for a bit
		// angle is circular, axis has minus and different angle
		// dual quaternion, search qsa github for deleted?
		//System.out.println(aa.getAngle()); // WTF, 3 - 6? some standardization? is vector negative? still stragne...
		double angle = aa.getAngle() / Math.PI;
		/*System.out.println("ANGLE " + angle);
		if (angle <= 1) {
			System.out.println("aaaaaa " + angle);
		}
		if (angle > 2) {
			System.out.println("bbbb");
		}*/
		Point r = axis.multiply(angle);
		double[] coords = {t.x, t.y, t.z, r.x, r.y, r.z, translationSize};
		// similarity of opposite axes, asses two angles, or is it possible to make it standard?
		return coords;
	}

	public Quat align(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getQuaternion();
	}

	// derive pow from wikipedia
// do both point manipulations and quats, good for testing	
// TODO transform input into quaternions, first thing
// compute internal quaternion (aq = b, q*a* = b, q* = b*a, q = a*b)
// from that, we have halfway quaternion, express
// std quaternion (1,0,0,0) or st like that
// find halfway->std
// r-r rotate by halfway->std
	public double compute(Point[] a, Point[] b) {

		superposer.set(a, b);
		Quat q = superposer.getQuaternion();
		AxisAngle aa = q.normalize().toAxisAngle();
		AxisAngle halfway = new AxisAngle(aa.getAxis(), aa.getAngle() / 2);
		Versor w = Versor.create(halfway);

		// center a
		// half rotate
		// center a + b
		// align ghost to base
		// apply the rotation to a + b
		return 0;
	}
}
