package orientation.body;

// aligned at x
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
import altaite.geometry.superposition.Superposer;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.Random;

// start generating from coordintes: symetrical x-twist, then rotation by yz vectors
public class TwoBodies {

	//private Random random;
	//
	private double torsion;
	private double yzRotationAngle1, yzRotationAngle2;
	private double yzPlaneAngle1, yzPlaneAngle2;
	//private Quat yzQuat1, yzQuat2;
	//
	private static double sqrt3half = Math.sqrt(3) / 2;
	private static Point[] triangle = new Point[3];
	private Quat quaternion1, quaternion2;

	static {
		triangle[0] = new Point(0, -1, 0);
		triangle[1] = new Point(sqrt3half, 0.5, 0);
		triangle[2] = new Point(-sqrt3half, 0.5, 0);
	}
	//
	private Point[] triangle1, triangle2;

	public String toString() {
		return torsion + " " + yzRotationAngle1 + " " + yzRotationAngle2 + " " + yzPlaneAngle1 + " " + yzPlaneAngle2;
	}

	public Point[] getTriangle1() {
		return triangle1;
	}

	public Point[] getTriangle2() {
		return triangle2;
	}

	public static Point[] getBodyInStandardOrientation() {
		return triangle;
	}

	public TwoBodies(Random random, double distance, boolean zero) {

		torsion = Math.abs(angle(random));
		yzPlaneAngle1 = angle(random);
		yzPlaneAngle2 = angle(random);
		yzRotationAngle1 = angle(random);
		yzRotationAngle2 = angle(random);
		triangle1 = triangle(-torsion / 2, yzPlaneAngle1, yzRotationAngle1, -distance / 2);
		triangle2 = triangle(torsion / 2, yzPlaneAngle2, yzRotationAngle2, distance / 2);

		/*torsion = Math.PI;//Math.abs(angle(random));
		if (zero) {
			torsion = 0;
		}
		yzPlaneAngle1 = 0;//angle(random);
		yzPlaneAngle2 = 0;//angle(random);
		yzRotationAngle1 = 0;//angle(random);
		yzRotationAngle2 = Math.PI;
		triangle1 = triangle(-torsion / 2, yzPlaneAngle1, yzRotationAngle1, -distance / 2);
		triangle2 = triangle(torsion / 2, yzPlaneAngle2, yzRotationAngle2, distance / 2);*/
		// x -1, +1 for ac, bd, 180 rotations
		/*torsion = 0;//Math.abs(angle(random)); 
		if (zero) {
			yzPlaneAngle1 = 1.5933459703049149;//angle(random); 
		} else {

		}

		if (zero) {
			yzPlaneAngle2 = 0.4276493953098391;//angle(random);//;/
		} else {
			yzPlaneAngle2 = 2;//angle(random);//2;//angle(random);
		}
		yzRotationAngle1 = Math.PI;//angle(random);
		yzRotationAngle2 = Math.PI;//angle(random);
		triangle1 = triangle(-torsion / 2, yzPlaneAngle1, yzRotationAngle1, -distance / 2);
		triangle2 = triangle(torsion / 2, yzPlaneAngle2, yzRotationAngle2, distance / 2);*/

 /*if (zero) {
			torsion = 0;//Math.abs(angle(random)); // 1 is interesting
			yzPlaneAngle1 = 0;//angle(random); // converged nicely with / 2
			yzPlaneAngle2 = 0;//angle(random);
			yzRotationAngle1 = 0;
			yzRotationAngle2 = 0;
			triangle1 = triangle(-torsion / 2, yzPlaneAngle1, yzRotationAngle1, -distance / 2);
			triangle2 = triangle(torsion / 2, yzPlaneAngle2, yzRotationAngle2, distance / 2);
		} else {
			torsion = 0;//Math.abs(angle(random)); // 1 is interesting
			yzPlaneAngle1 = Math.PI / 2;//angle(random); // converged nicely with / 2
			yzPlaneAngle2 = 0;//angle(random);
			yzRotationAngle1 = Math.PI / 2;
			yzRotationAngle2 = 0;
			triangle1 = triangle(-torsion / 2, yzPlaneAngle1, yzRotationAngle1, -distance / 2);
			triangle2 = triangle(torsion / 2, yzPlaneAngle2, yzRotationAngle2, distance / 2);
		}*/
		Superposer superposer = new Superposer();
		superposer.set(triangle, triangle1);
		quaternion1 = superposer.getQuaternion();
		superposer = new Superposer();
		superposer.set(triangle, triangle2);
		quaternion2 = superposer.getQuaternion();

		/*System.out.println("--- quats");
		System.out.println(quaternion1);
		System.out.println(quaternion2);
		System.out.println("///");*/
	}

// why rotation addition does not work without angle halving?
	public double angularDistance(TwoBodies other) {
		//Quat ac = other.quaternion1.conjugate().multiply(quaternion1);
		//Quat bd = other.quaternion2.conjugate().multiply(quaternion2);

		Quat ac = quaternion1.to(other.quaternion1);
		Quat bd = quaternion2.to(other.quaternion2);

		//b.multiply(conjugate());
//(-0.7071067811865476, 0.0, 0.0, 0.7071067811865476)
//(-0.7071067811865476, 3.6755750900592285E-17, -4.983985472295708E-17, 0.7071067811865476)
		/*System.out.print("ac = ");
		System.out.println(ac);
		System.out.print("bd = ");
		System.out.println(bd);*/

		// probably can be done by having zero twist on ac, prealignement
		Quat twist = ac.getRotationComponentAboutAxis(new Point(1, 0, 0)).conjugate();
		ac = twist.multiply(ac);
		bd = twist.multiply(bd);
		/*if (ac.w < 0 || bd.w < 0 || ac.w > 0.1 || bd.w > 0.1) {
			return 0;
		}*/
		/*System.out.print("ac = ");
		System.out.println(ac);
		System.out.print("bd = ");
		System.out.println(bd);*/
		if (Math.abs(ac.length() - 1) > 0.00001) {
			throw new RuntimeException();
		}
		if (Math.abs(bd.length() - 1) > 0.00001) {
			throw new RuntimeException();
		}

		// IDEA
		// form in which quat vectors share one x-side, x positive
		// still does not solve it
		// does this work for angles < 90, can it be forced to work beyond
		// problem: see values above
		// TEST - by skipping ac, bd having big angle - still nois
		// TEST a,b,c,d < 90
		ac = ac.smallerAngleForm();
		bd = bd.smallerAngleForm();

		Quat acFull = alignYZ(1, 0, ac);
		Quat bdFull = alignYZ(1, 0, bd);

		// split into x and yz, halve only yz?
		// TODO try to double yz arc angle
		// ALTERNATIVE distance of arcs as if x-rot was cyclical coordinate
		// verify it has max at 90, smallerAngleForm?
		// BETTER WAY quat to arc?
		ac = ac.pow(0.5);
		bd = bd.pow(0.5);

		ac = ac.smallerAngleForm();
		bd = bd.smallerAngleForm();

		/*System.out.print("ac2 = ");
		System.out.println(ac);
		System.out.print("bd2 = ");
		System.out.println(bd);*/
		//bd = alignYZ(ac, bd);
		ac = alignYZ(1, 0, ac);
		bd = alignYZ(1, 0, bd);

		/*System.out.print("ac3 = ");
		System.out.println(ac);
		System.out.print("bd3 = ");
		System.out.println(bd);*/
		Point xArc = ac.rotate(new Point(0, 0, 1));
		Point yArc = bd.rotate(new Point(0, 0, 1));

		//Point xArcFull = acFull.rotate(new Point(0, 0, 1));
		//Point yArcFull = bdFull.rotate(new Point(0, 0, 1));
		//xArc = new Point(xArc.x, xArcFull.y, xArcFull.z);
		//yArc = new Point(yArc.x, yArcFull.y, yArcFull.z);
		/*System.out.println("arcs");
		System.out.println(xArc);
		System.out.println(yArc);*/
		if (Math.signum(xArc.x) == Math.signum(yArc.x)) {
			yArc = new Point(-yArc.x, yArc.y, yArc.z);
		}

		/*xArc = new Point(0.5 * xArc.x, xArc.y, xArc.z);
		yArc = new Point(0.5 * yArc.x, yArc.y, yArc.z);*/

 /*if (Math.signum(ac.x) == Math.signum(bd.x)) {
			bd = new Quat(-bd.x, bd.y, bd.z, bd.w);
		}*/
		//Quat e = bd.conjugate().multiply(ac); // test manually arcs?
		//return e.w; // =cos(angle of rotation)
		//return e.getAngle(); // =cos(angle of rotation)
		//System.out.println("d = " + xArc.distance(yArc));
		double distance = xArc.distance(yArc);
		/*System.out.println("distance " + distance);
		System.out.println(this);
		System.out.println(other);
		System.out.println("");*/
		return distance;
	}
// TODO numerical solution, minimum? - less confusion by rmsd/angular difference
	// just rotate other quaternions by all handmade ones, find minimum

	public double angularDistanceNumerical(TwoBodies other) {
		Quat a = quaternion1;
		Quat b = quaternion2;
		/*System.out.println("base");
		System.out.println(quaternion1.to(other.quaternion1));
		System.out.println(quaternion2.to(other.quaternion2));*/

		double min = Double.POSITIVE_INFINITY;
		for (double angle = 0; angle < 2 * Math.PI; angle += 0.1) {
			Quat r = Quat.createFromAxisAngle(1, 0, 0, angle);
			Quat c = r.multiply(other.quaternion1);
			Quat d = r.multiply(other.quaternion2);
			double dist = a.to(c).getAngle() + b.to(d).getAngle();

			/*System.out.println("dist " + dist + " " + a.to(c).getAngle() + " " + b.to(d).getAngle());
			System.out.println(a.to(c));
			System.out.println(b.to(d));
			System.out.println("");*/
			if (dist < min) {
				min = dist;
			}
		}
		return min;
	}

	public void alignByFirst() {
		Superposer superposer = new Superposer();
		Point center1 = Point.center(triangle1);
		move(center1.negate());
		superposer.set(triangle1, triangle);
		Quat q = superposer.getQuaternion();
		rotate(q);
	}

	private void move(Point v) {
		for (int i = 0; i < triangle1.length; i++) {
			triangle1[i] = triangle1[i].plus(v);
		}
		for (int i = 0; i < triangle1.length; i++) {
			triangle2[i] = triangle2[i].plus(v);
		}
	}

	private void rotate(Quat q) {
		for (int i = 0; i < triangle1.length; i++) {
			triangle1[i] = q.rotate(triangle1[i]);
		}
		for (int i = 0; i < triangle1.length; i++) {
			triangle2[i] = q.rotate(triangle2[i]);
		}
	}

	public Point[] getPoints() {
		return merge(triangle1, triangle2);
	}

	public double[] getCoordinates() {
		double[] cs = {torsion, yzPlaneAngle1, yzPlaneAngle2, yzRotationAngle1, yzRotationAngle2};
		return cs;
	}

	private Quat alignYZ(double ay, double az, Quat b) {
		/*if (ay + b.z < 0.00001 || b.y + b.z < 0.00001) {
			return b;
		}*/
		// y - real axis, z - imaginary axis
		double aSize = Math.sqrt(ay * ay + az * az);
		double bSize = Math.sqrt(b.y * b.y + b.z * b.z);
		if (bSize == 0) {
			return b;
		}
		double f = bSize / aSize;
		Quat bAligned = new Quat( // a conjugate, inverts signs
			b.x,
			ay * f,
			az * f,
			b.w
		);
		double bAlignedSize = Math.sqrt(bAligned.y * bAligned.y + bAligned.z * bAligned.z);
		if (Math.abs(bSize - bAlignedSize) > 0.00001) {
			throw new RuntimeException();
		}
		if (Math.abs(ay / az - bAligned.y / bAligned.z) > 0.00001) { // TODO better test by normalization, signum is ignored
			throw new RuntimeException(ay + " " + az + "   " + bAligned.y + " " + bAligned.z);
		}
		if (Math.abs(ay / aSize - bAligned.y / bSize) > 0.00001) {
			throw new RuntimeException();
		}
		if (Math.abs(az / aSize - bAligned.z / bSize) > 0.00001) {
			throw new RuntimeException();
		}
		//System.out.println(bAligned);
		return bAligned;
	}

	/*private Quat alignYZ(Quat a, Quat b) {
		if (a.y + b.z < 0.00001 || b.y + b.z < 0.00001) {
			return b;
		}
		// y - real axis, z - imaginary axis
		double aSize = Math.sqrt(a.y * a.y + a.z * a.z);
		double bSize = Math.sqrt(b.y * b.y + b.z * b.z);
		double f = bSize / aSize;
		Quat bAligned = new Quat( // a conjugate, inverts signs
			b.x,
			a.y * f,
			a.z * f,
			b.w
		);
		double bAlignedSize = Math.sqrt(bAligned.y * bAligned.y + bAligned.z * bAligned.z);
		if (Math.abs(bSize - bAlignedSize) > 0.00001) {
			throw new RuntimeException();
		}
		if (Math.abs(a.y / a.z - bAligned.y / bAligned.z) > 0.00001) { // TODO better test by normalization, signum is ignored
			throw new RuntimeException(a.y + " " + a.z + "   " + bAligned.y + " " + bAligned.z);
		}
		if (Math.abs(a.y / aSize - bAligned.y / bSize) > 0.00001) {
			throw new RuntimeException();
		}
		if (Math.abs(a.z / aSize - bAligned.z / bSize) > 0.00001) {
			throw new RuntimeException();
		}
		return bAligned;
	}*/
	public double distance(TwoBodies other) {
		double dt = angleDistance(torsion, other.torsion);

		Quat a = yzQuat(yzPlaneAngle1, yzRotationAngle1);
		Quat b = yzQuat(other.yzPlaneAngle1, other.yzRotationAngle1);
		Quat d1 = a.conjugate().multiply(b);
		double angle1 = d1.toAxisAngle().getAngle();
		if (angle1 > Math.PI) {
			angle1 = 2 * Math.PI - angle1;
		}

		a = yzQuat(yzPlaneAngle2, yzRotationAngle2);
		b = yzQuat(other.yzPlaneAngle2, other.yzRotationAngle2);
		Quat d2 = a.conjugate().multiply(b);
		double angle2 = d2.toAxisAngle().getAngle();
		if (angle2 > Math.PI) {
			angle2 = 2 * Math.PI - angle2;
		}

		return Math.sqrt(dt * dt + angle1 * angle1 + angle2 * angle2);

	}

	// test, efficiency
	private double angleDistance(double a, double b) {
		assert a <= Math.PI;
		assert b <= Math.PI;
		assert a >= -Math.PI;
		assert b >= -Math.PI;
		a += Math.PI;
		b += Math.PI;
		double d = Math.abs(a - b);
		if (d > Math.PI) {
			d = 2 * Math.PI - d;
		}
		return d;
	}

	private Point[] triangle(double torsion, double yzPlaneAngle, double yzRotationAngle, double xShift) {
		Point[] t = xRotate(triangle, torsion);
		t = yzRotate(t, yzPlaneAngle, yzRotationAngle);
		t = xShift(t, xShift);
		return t;
	}

	private Point[] yzRotate(Point[] in, double yzPlane, double yzRotation) {
		Point axis = new Point(0, cos(yzPlane), sin(yzPlane)); // I think cos, sin order is arbitrary 
		Quat q = yzQuat(yzPlane, yzRotation);
		Point[] out = new Point[in.length];
		for (int i = 0; i < in.length; i++) {
			out[i] = q.rotate(in[i]);
		}
		return out;
	}

	private Quat yzQuat(double yzPlane, double yzRotation) {
		Point axis = new Point(0, cos(yzPlane), sin(yzPlane)); // I think cos, sin order is arbitrary 
		Quat q = Quat.createFromAxisAngle(axis, yzRotation);
		return q;
	}

	private Point[] xRotate(Point[] in, double torsion) {
		Quat q = Quat.createFromAxisAngle(new Point(1, 0, 0), torsion);
		Point[] out = new Point[in.length];
		for (int i = 0; i < in.length; i++) {
			out[i] = q.rotate(in[i]);
		}
		return out;
	}

	private Point[] xShift(Point[] in, double xShift) {
		Point[] out = new Point[in.length];
		for (int i = 0; i < in.length; i++) {
			out[i] = in[i].plus(new Point(xShift, 0, 0));
		}
		return out;
	}

	private double angle(Random random) {
		return Math.PI * (2 * random.nextDouble() - 1);
	}

	private Point[] merge(Point[] a, Point[] b) {
		Point[] c = new Point[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

}
