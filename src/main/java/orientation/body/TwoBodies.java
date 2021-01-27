package orientation.body;

// aligned at x
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
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

	public TwoBodies(Random random, double distance) {
		//this.random = random;
		torsion = Math.abs(angle(random)); // 1 is interesting
		yzPlaneAngle1 = angle(random) / 2;
		yzPlaneAngle2 = angle(random) / 2;
		yzRotationAngle1 = angle(random) / 2;
		yzRotationAngle2 = angle(random) / 2;
		triangle1 = triangle(-torsion / 2, yzPlaneAngle1, yzRotationAngle1, -distance / 2);
		triangle2 = triangle(torsion / 2, yzPlaneAngle2, yzRotationAngle2, distance / 2);
	}

	public Point[] getPoints() {
		return merge(triangle1, triangle2);
	}

	public double[] getCoordinates() {
		double[] cs = {torsion, yzPlaneAngle1, yzPlaneAngle2, yzRotationAngle1, yzRotationAngle2};
		return cs;
	}

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
