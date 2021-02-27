package orientation.body;

import altaite.geometry.primitives.Quat;
import java.util.Random;

public class TwistSwing {

	private Random random = new Random();
	private Quat q = Quat.createVersor(random);

	private void run() {
		double twistSizeInverted = 1 / Math.sqrt(q.x * q.x + q.w * q.w);

		double twistX = q.x * twistSizeInverted;
		double twistW = q.w * twistSizeInverted;
		Quat twist = new Quat(twistX, 0, 0, twistW);
		
		//Quat swing = new Quat(0, q.y,q.z, q.w).normalize();
		
		Quat swing = q.multiply(twist.conjugate());
		
		System.out.println(swing);
		System.out.println(swing.multiply(twist));
		System.out.println(q);
	}

	public static void main(String[] args) {
		TwistSwing ts = new TwistSwing();
		ts.run();
	}

}
