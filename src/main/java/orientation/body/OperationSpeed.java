package orientation.body;

import util.Time;

public class OperationSpeed {

// TODO: realistic inputs from array (quats in primitives, one protein)
	private static void run() {
		double x = 0.1;
		Time.start("a");
		double y = 0;
		int n = 10000000;
		for (int i = 0; i < n; i++) {
			//x = x * 0.00000000001 + x + 0.001 + Math.sin(x); // 450 ms /10 000 000 = 45 ns
			//x = x * 0.00000000001 + x + 0.001 + Math.sin((x - 0.1) / x); // 526
			//x = x * 0.00000000001 + x + 0.001 + (x - 0.1) / x; // 68
			//x = x * 0.00000000001 + x + 0.001 + Math.sqrt((x - 0.1) / x); // 130
			//x = x * 0.00000000001 + x + 0.001 + Math.acos((x - 0.1) / x); // 4747
			//x = x * 0.00000000001 + x + 0.001; // 35
			
			// superposer: 13760 ms / 49995000 = 2752 ms / 10 000 000
			// 5 sin??? wtf 
			// TRY using the RMSD value
			// do it here, iterativelly? feed points?>
//euclid 83
//table  428
//qcp    14344 49995000
// can sin/cos be avoided, just multiplication of quaternions? does the equation looks like it takes stuff from quat?
// arc algebra?
// draw both arcs on equator and move one by x-rotation
// what is that rotation?
// can it be done with primal a,b,c,d rotations?
		}
		Time.stop("a");
		System.out.println(Time.get("a").getMiliseconds());
		System.out.println(x);
	}

	public static void main(String[] args) {
		while (true) {
			run();
		}
	}
}
