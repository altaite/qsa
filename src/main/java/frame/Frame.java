package frame;

public class Frame {

	private double distanceEnd = 5;
	private double angleEnd = Math.PI / 6;
	private SmallResidue[][] residues; // [index of point in frame][alternative residues nearby]

	public Frame(SmallResidue r, SmallStructure structure) {
		residues = new SmallResidue[4][3];
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 3; y++) {
				residues[x][y] = r;
			}
		}
		
	}

	public double computeDistance(Frame other) { //try empty first for speed, then try dist for any random pair
		double best = 0;
		for (int i = 0; i < residues.length; i++) {
			for (int x = 0; x < residues[i].length; x++) {
				for (int y = 0; y < other.residues[i].length; y++) {
					SmallResidue a = residues[i][x];
					SmallResidue b = residues[i][y];
					double distance = ramp(distanceEnd, a.getDistance(b));
					double orientationDif = ramp(angleEnd, getOrientationDif(a, b));
					double phiDif = ramp(angleEnd, getAngleDif(a.getPhi(), b.getPhi()));
					double psiDif = ramp(angleEnd, getAngleDif(a.getPsi(), b.getPsi()));
					// normalize 0 - 1
					// linear to threshold for each, optimize threshold values

					// 0 more powerful? 1 should mean identity, 0 bad dissimilarity
					double d = distance * orientationDif * phiDif * psiDif;
					if (d > best) {
						best = d;
					}
				}
			}
		}
		//System.out.println(best);
		return best;
	}

	// 1 for <=0, 0 for >=end, linear inbetween
	private double ramp(double end, double value) {
		if (value <= 0) {
			return 1;
		} else if (value >= end) {
			return 0;
		} else {
			return (end - value) / end;
		}
	}

	private double getOrientationDif(SmallResidue a, SmallResidue b) {
		return 0;
	}

	private double getAngleDif(double a, double b) {
		return 0;
	}

}
