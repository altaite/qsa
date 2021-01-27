package orientation;

import java.util.Random;

public class Walk {

	private SmallStructure a;
	private SmallStructure b;
	private SmallResidue[] ars;
	private SmallResidue[] brs;
	private Random random = new Random(1);
	private short x, y; // current walk position
	private double bestScore;
	private short bestNeighbor;

	public Walk(SmallStructure a, SmallStructure b) {
		this.a = a;
		this.b = b;
		this.ars = a.getResidues();
		this.brs = b.getResidues();
	}
	short xStart, yStart;

	public void align() {
		for (yStart = 0; yStart < brs.length; yStart++) {
			xStart = (short) random.nextInt(ars.length);
			walkFrom(); // TODO avoid low neighbor ones, or surface
		}
	}

	// TODO mechanism to burn bridges, no return - boolean[] of residues? plane cutting of visited part?
	// OR residues hold index of last neighbor, algorithm list of modified residues
	private void walkFrom() { // maybe early hard branching to avoid dead end on domain boundary
		x = xStart;
		y = yStart;
		int i = 0;
		while (step() && i < 5) {
			i++;
		}
	}
	
	
	// moves x and y, if no or no good continuation found, sets a flag
	private boolean step() {
		SmallResidue xr = ars[x];
		SmallResidue yr = brs[y];
		short[] xns = xr.getNeighbors();
		for (int i = 0; i < xns.length; i++) {
			SmallResidue xn = ars[xns[i]];
			if (xn.canVisit(xStart)) { // can walk starting from x visit, x = visitorId
				findMostSimilar(xn, yr);
				if (bestScore > 0.5) {
					x = xns[i];
					y = bestNeighbor;
					xn.visit(xStart);
					return true;

				} else {
					return false;
				}
			}
		}
		return false; // end of protein or returned to visited areas
	}

	private void findMostSimilar(SmallResidue chosenX, SmallResidue amongNeighborsOf) {
		short[] yns = amongNeighborsOf.getNeighbors();
		bestScore = 0;
		for (short yn : yns) {
			double score = similarity(chosenX, brs[yn]); // what is reference, last residues I guess
			if (score > bestScore) {
				bestScore = score;
				bestNeighbor = yn;
			}
		}
	}
	// isolate and compare with RMSD, the orientation part
	// similarity of neighbor pairs, but phi/psi not?
	private double similarity(SmallResidue xr, SmallResidue yr) {
		
		
		double dPhi = Math.abs(xr.getPhi() - yr.getPhi());
		double dPsi = Math.abs(xr.getPsi( ) - yr.getPsi());
		xr.getOrientation().multiply(yr.getOrientation());
		return 1 - (dPhi * dPsi / 100000);
	}

	// first just one walk, simplicity
	// then maybe select best from constant number 3 vs n, robustness
	// !!!! just first 3, but preorder them, so that they point in different directions
}
