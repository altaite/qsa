package orientation.body;

import altaite.complex.Complex;
import altaite.geometry.primitives.Quat;
import java.util.Random;

public class AngularDistanceBackup {

	private double cx, cy, cz, cw;
	private double arcX, arcY, arcZ;

	public double computeOld(Quat a, Quat b, Quat c, Quat d) {
		multiply(c.x, c.y, c.z, c.w, -a.x, -a.y, -a.z, a.w);

		double twistSizeInverted = 1 / Math.sqrt(cx * cx + cw * cw);
		double twistX = -cx * twistSizeInverted;
		double twistW = cw * twistSizeInverted;

		//System.out.println(twistX);
		// detect if certain twist size, or st, causes problems, and try to solve it later
		//oneSide(0, -1);
		oneSide(twistX, twistW);
		double arX = arcX;
		double arY = arcY;
		double arZ = arcZ;

		multiply(d.x, d.y, d.z, d.w, -b.x, -b.y, -b.z, b.w);
		//oneSide(0, -1);
		oneSide(twistX, twistW);

		/*double twist2SizeInverted = 1 / Math.sqrt(cx * cx + cw * cw);
		double twist2X = -cx * twistSizeInverted;
		double twist2W = cw * twistSizeInverted;*/

		//System.out.println(twistX + " " + twist2X + " " + (twistX + twist2X));
		//if (Math.abs(twistX) > 0.0 ) return 0;
		// arcs need not to sum 
		double dx;
		dx = arcX + arX; // both positive
		double dy = arcY - arY;
		double dz = arcZ - arZ;
		//System.out.println("---");
		double distance = dx * dx + dy * dy + dz * dz;
		return distance;
	}

	public double compute(Quat a, Quat b, Quat c, Quat d) {
		return twistSwing(a, b, c, d);
	}

	private void multiply(double ax, double ay, double az, double aw, double bx, double by, double bz, double bw) {
		cx = ax * bw + aw * bx + ay * bz - az * by;
		cy = aw * by - ax * bz + ay * bw + az * bx;
		cz = aw * bz + ax * by - ay * bx + az * bw;
		cw = aw * bw - ax * bx - ay * by - az * bz;
	}

	private void oneSide(double twistX, double twistW) {
		double x = twistX * cw + twistW * cx;
		double y = twistW * cy - twistX * cz;
		double z = twistW * cz + twistX * cy;
		double w = twistW * cw - twistX * cx;
		if (w < 0) {
			x = -x;
			w = -w;
		}
		w += 1; // sqrt = half angle after normalization
		double yyzz = y * y + z * z;
		double ww = w * w;
		double xx = x * x;
		double sizeInvertedSqr = 1 / (xx + yyzz + ww);
		double wy = w * Math.sqrt(yyzz);
		arcX = (wy + wy) * sizeInvertedSqr;
		double wx = w * x;
		arcY = -(wx + wx) * sizeInvertedSqr;
		arcZ = (ww - yyzz - xx) * sizeInvertedSqr;

		/*Complex a = new Complex(arcZ, arcY);
		Complex b = a.normalize();
		Complex c = a.times(b);*/
		//arcZ = c.re();
		//arcY = c.im();

		//System.out.println(arcX + " " + arcY + " " + arcZ);
		//System.out.println(arcX * arcX + arcY*arcY + arcZ*arcZ);
	}

	public double twistSwingOld(Quat a, Quat b, Quat c, Quat d) {
		Quat ac = c.multiply(a.conjugate());
		Quat twistAc = twistUnoptimized(ac).smallerAngleFormEfficient();
		Quat swingAc = swing(ac, twistAc).smallerAngleFormEfficient();

		Quat bd = d.multiply(b.conjugate());
		Quat twistBd = twistUnoptimized(bd).smallerAngleFormEfficient();
		Quat swingBd = swing(bd, twistBd).smallerAngleFormEfficient();

		// assuming small angle formulation?
		if (swingAc.y * swingBd.y + swingAc.z * swingBd.z < 0) { // angle < PI/2, same x direction of point on the left (or right)
			swingBd = swingBd.conjugate();
		}
		Quat twist = twistAc.multiply(twistBd.conjugate()).smallerAngleForm();
		Quat sum = swingBd.multiply(twist).multiply(swingAc);
		return sum.w;

		// test twist directions up/down, set properly and opposite, also depends on normal yz direction
		// up (swingBd - swingAc) down
	}

	public double twistSwing(Quat a, Quat b, Quat c, Quat d) {
		Quat ac = c.multiply(a.conjugate());
		Quat bd = d.multiply(b.conjugate());

		Quat twistConjugate = twistConjugate(ac);
		ac = twistConjugate.multiply(ac); //swing
		bd = twistConjugate.multiply(bd).smallerAngleFormEfficient();

		ac = alignYZ(ac, bd);

		if (Math.signum(ac.x) != Math.signum(bd.x)) {
			bd = new Quat(-bd.x, bd.y, bd.z, bd.w);
		}

		return bd.multiply(ac).w;
	}

	private Quat twistConjugate(Quat q) {
		double twistSizeInverted = 1.0 / Math.sqrt(q.x * q.x + q.w * q.w); // 1 any diff?
		double twistX = q.x * twistSizeInverted;
		double twistW = q.w * twistSizeInverted;
		Quat twist = new Quat(-twistX, 0, 0, twistW);
		return twist;
	}

	private Quat alignYZ(Quat a, Quat b) {
		double scale = Math.sqrt((a.y * a.y + a.z * a.z) / (b.y * b.y + b.z * b.z));
		return new Quat(a.x, b.y * scale, b.z * scale, a.w);
	}

	// first non-arc solution
	public double twistSwingUnoptimized(Quat a, Quat b, Quat c, Quat d) {
		// small angles
		// align ac.yz to bd.yz
		// if not the same x rotation of the point (signum x), conjugate
		// compose both

		Quat ac = c.multiply(a.conjugate());
		Quat bd = d.multiply(b.conjugate());

		//ac = alignYZ(ac, bd);
		Quat twistAc = twistUnoptimized(ac);
		ac = twistAc.conjugate().multiply(ac).smallerAngleFormEfficient(); //swing
		bd = twistAc.conjugate().multiply(bd).smallerAngleFormEfficient();

		ac = alignYZUnoptimized(ac, bd);

		if (Math.signum(ac.x) != Math.signum(bd.x)) {
			bd = new Quat(-bd.x, bd.y, bd.z, bd.w);
			//bd = bd.conjugate();
		}

		//ac = ac.smallerAngleFormEfficient();
		//bd = bd.smallerAngleFormEfficient();
		return bd.multiply(ac).w;
	}

	private Quat alignYZUnoptimized(Quat a, Quat b) {
		double aSize = Math.sqrt(a.y * a.y + a.z * a.z);
		double bSize = Math.sqrt(b.y * b.y + b.z * b.z);
		double y = b.y / bSize * aSize;
		double z = b.z / bSize * aSize;
		return new Quat(a.x, y, z, a.w);
	}

	private Quat twistUnoptimized(Quat q) {
		double twistSizeInverted = 1 / Math.sqrt(q.x * q.x + q.w * q.w);
		double twistX = q.x * twistSizeInverted;
		double twistW = q.w * twistSizeInverted;
		Quat twist = new Quat(twistX, 0, 0, twistW);
		return twist;
	}

	private double swingXSize() {
		return 0;
		// arc x height
		// do we do this sqrt(ac), or do we stay in rotations?
		// rotations: swingUpAc * twistDiff * swingDownBd
		// is angle between swing planes already in the swings? yes, they do not even need to subtract twist?
		// MAYBE: just ac * bd* or ac* * bd, but make sure twist are opposite (and maybe starting from smaller angles)
		// AND measure longer angle, especially if it is cause by swings? (difficulty: how to tell "caused" by twist, study cases on only twist first (happens?), then when does it flip)
	}	// maybe test with decomposed rotations, add them properly?
	// thought: twist1 swing1 (twist2 swing2)* or whatever, what happens? is this the arcs? swings nicelly eliminate?

	private Quat swing(Quat q, Quat twist) {
		Quat swing = q.multiply(twist.conjugate());
		return swing;
	}

	private void run() {
		Quat q = Quat.createVersor(new Random(1));
		Quat twist = twistUnoptimized(q);
		Quat swing = new Quat(0, q.y, q.z, q.w).normalize();
		System.out.println(q);
		System.out.println(swing.multiply(twist));
	}

	public static void main(String[] args) {
		AngularDistanceBackup ad = new AngularDistanceBackup();
		ad.run();
	}
}
