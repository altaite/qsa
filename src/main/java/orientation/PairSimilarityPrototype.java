package orientation;

import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Versor;
import altaite.geometry.superposition.Superposer;

public class PairSimilarityPrototype {

	int n = 10;
	Point[][] objects = new Point[n][];
	Representation[] representations = new Representation[n];
	private Superposer superposer = new Superposer();

// TODO test by creating pairs with known angles (distort axisting body) and distance diff, see if the procedure produces them correctly first
// first, correlate this distortion to RMSD, low effort
// replace body generator with mutable Body, construct randomly with given pars, or from Body, differential angle and dist exact, direction random
	public PairSimilarityPrototype() {
		PairGenerator g = new PairGenerator();
		for (int i = 0; i < n; i++) {
			objects[i] = g.generatePair();
		}

		for (int i = 0; i < n; i++) {
			representations[i] = new Representation(g.getBase(), objects[i]);
		}

	}

	public double similarity() {
		return 0;
	}

	public void run() {

	}

	public static void main(String[] args) {
		PairSimilarityPrototype m = new PairSimilarityPrototype();
		m.run();
	}
}

class Representation {

	private Versor q;
	private Versor r;

	public Representation(Point[] base, Point[] object) {
		Point[] a = new Point[object.length / 2];
		Point[] b = new Point[object.length / 2];
		for (int i = 0; i < a.length; i++) {
			a[i] = object[i];
		}
		for (int i = 0; i < b.length; i++) {
			b[i] = object[i + a.length];
		}

	}

	public double similarity(Representation other) {
		return 0;
	}
}
