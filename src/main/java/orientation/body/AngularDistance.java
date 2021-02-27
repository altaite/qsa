package orientation.body;

import altaite.geometry.primitives.Quat;

public class AngularDistance {

	double acx;
	double acy;
	double acz;
	double acw;

	double bdx;
	double bdy;
	double bdz;
	double bdw;

	double twistSizeInverted;
	double twistX;
	double twistW;

	double acx2;
	double acy2;
	double acz2;
	double acw2;

	double bdx2;
	double bdy2;
	double bdz2;
	double bdw2;

	double scale;

	// finish library and tell peter?
	// is comparison fair? check what is going with the points inside qcp
	// also compare with approximations, find some, euclid
	
	// finish library and try it for alignment!!!
	// new project, github, or at least wrap up, describe briefly
	// open about doubts 
	
	// TODO set option, query does not need to change, but preserve it
	// also allows set quats and numbers
	// TODO try procedural multiplication?
	// TODO just store input values in public variable? then reuse it instead of acx2
	// count operations
	// precede by energy computation? yes, if it would converge for angles
	// can they be done gradually? or lipschitz? and tree?
	// MAYBE first try it in a simple alignment algorithm
	// expand the most similar pair if above threshold, avoid going back?
	// library? what about distance and phi/psi angles?
	// maybe no search needed here, but euclid possible to speed up
	
	public final double compute(
		double ax, double ay, double az, double aw,
		double bx, double by, double bz, double bw,
		double cx, double cy, double cz, double cw,
		double dx, double dy, double dz, double dw
	) {

		acx = cx * aw - cw * ax - cy * az + cz * ay;
		acy = -cw * ay + cx * az + cy * aw - cz * ax;
		acz = -cw * az - cx * ay + cy * ax + cz * aw;
		acw = cw * aw + cx * ax + cy * ay + cz * az;

		//Quat bd = dmultiply(bconjugate());
		bdx = dx * bw - dw * bx - dy * bz + dz * by;
		bdy = -dw * by + dx * bz + dy * bw - dz * bx;
		bdz = -dw * bz - dx * by + dy * bx + dz * bw;
		bdw = dw * bw + dx * bx + dy * by + dz * bz;

		// ac twist conjugate
		// TODO zero division?
		twistSizeInverted = 1.0 / Math.sqrt(acx * acx + acw * acw); // 1 any diff?
		twistX = -acx * twistSizeInverted;
		twistW = acw * twistSizeInverted;

		// twist conjugate * ac
		//Quat twistConjugate = twistConjugate(ac);
		//ac = twistConjugate.multiply(ac); //swing
		//bd = twistConjugate.multiply(bd).smallerAngleFormEfficient();
		acx2 = twistX * acw + twistW * acx;
		acy2 = twistW * acy - twistX * acz;
		acz2 = twistW * acz + twistX * acy;
		acw2 = twistW * acw - twistX * acx;

		bdx2 = twistX * bdw + twistW * bdx;
		bdy2 = twistW * bdy - twistX * bdz;
		bdz2 = twistW * bdz + twistX * bdy;
		bdw2 = twistW * bdw - twistX * bdx;

		if (bdw2 < 0) {
			bdx2 = -bdx2;
			bdy2 = -bdy2;
			bdz2 = -bdz2;
			bdw2 = -bdw2;
		}

		//	System.out.println(bd.y);
		//	System.out.println(bdy2);
		//	System.out.println("---");
		//ac = alignYZ(ac, bd);
		scale = Math.sqrt((acy2 * acy2 + acz2 * acz2) / (bdy2 * bdy2 + bdz2 * bdz2));
		acy2 = bdy2 * scale;
		acz2 = bdz2 * scale;

		//if (Math.signum(ac.x) != Math.signum(bd.x)) {
		//	bd = new Quat(-bd.x, bd.y, bd.z, bd.w);
		//}
		if (Math.signum(acx2) != Math.signum(bdx2)) {
			bdx2 = -bdx2;
		}

		return bdw2 * acw2 - bdx2 * acx2 - bdy2 * acy2 - bdz2 * acz2;
		//return bd.multiply(ac).w;

	}

	// TODO just pass numbers, store numbers
	public final double computeObject(Quat a, Quat b, Quat c, Quat d) {

		acx = c.x * a.w - c.w * a.x - c.y * a.z + c.z * a.y;
		acy = -c.w * a.y + c.x * a.z + c.y * a.w - c.z * a.x;
		acz = -c.w * a.z - c.x * a.y + c.y * a.x + c.z * a.w;
		acw = c.w * a.w + c.x * a.x + c.y * a.y + c.z * a.z;

		//Quat bd = d.multiply(b.conjugate());
		bdx = d.x * b.w - d.w * b.x - d.y * b.z + d.z * b.y;
		bdy = -d.w * b.y + d.x * b.z + d.y * b.w - d.z * b.x;
		bdz = -d.w * b.z - d.x * b.y + d.y * b.x + d.z * b.w;
		bdw = d.w * b.w + d.x * b.x + d.y * b.y + d.z * b.z;

		// ac twist conjugate
		// TODO zero division?
		twistSizeInverted = 1.0 / Math.sqrt(acx * acx + acw * acw); // 1 any diff?
		twistX = -acx * twistSizeInverted;
		twistW = acw * twistSizeInverted;

		// twist conjugate * ac
		//Quat twistConjugate = twistConjugate(ac);
		//ac = twistConjugate.multiply(ac); //swing
		//bd = twistConjugate.multiply(bd).smallerAngleFormEfficient();
		acx2 = twistX * acw + twistW * acx;
		acy2 = twistW * acy - twistX * acz;
		acz2 = twistW * acz + twistX * acy;
		acw2 = twistW * acw - twistX * acx;

		bdx2 = twistX * bdw + twistW * bdx;
		bdy2 = twistW * bdy - twistX * bdz;
		bdz2 = twistW * bdz + twistX * bdy;
		bdw2 = twistW * bdw - twistX * bdx;

		if (bdw2 < 0) {
			bdx2 = -bdx2;
			bdy2 = -bdy2;
			bdz2 = -bdz2;
			bdw2 = -bdw2;
		}

		//	System.out.println(bd.y);
		//	System.out.println(bdy2);
		//	System.out.println("---");
		//ac = alignYZ(ac, bd);
		scale = Math.sqrt((acy2 * acy2 + acz2 * acz2) / (bdy2 * bdy2 + bdz2 * bdz2));
		acy2 = bdy2 * scale;
		acz2 = bdz2 * scale;

		//if (Math.signum(ac.x) != Math.signum(bd.x)) {
		//	bd = new Quat(-bd.x, bd.y, bd.z, bd.w);
		//}
		if (Math.signum(acx2) != Math.signum(bdx2)) {
			bdx2 = -bdx2;
		}

		return bdw2 * acw2 - bdx2 * acx2 - bdy2 * acy2 - bdz2 * acz2;
		//return bd.multiply(ac).w;

	}
	/*
	private void multiply(double ax, double ay, double az, double aw, double bx, double by, double bz, double bw) {
		cx = ax * bw + aw * bx + ay * bz - az * by;
		cy = aw * by - ax * bz + ay * bw + az * bx;
		cz = aw * bz + ax * by - ay * bx + az * bw;
		cw = aw * bw - ax * bx - ay * by - az * bz;
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
	 */
}
