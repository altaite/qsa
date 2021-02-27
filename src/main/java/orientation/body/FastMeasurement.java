package orientation.body;

import altaite.collection.performance.Timer;
import altaite.geometry.primitives.Point;
import altaite.geometry.superposition.Superposer;
import java.util.Random;

public class FastMeasurement {

	// TODO how quick is solving the alpha equation?
	// fast solution, draw for random combination and figure it out
	// or memory access of table, plus interpolation spline
	private int n = 10000;
	private int dim = 10;
	private Random random = new Random();
	private Point[][] points = new Point[n][];
	private float[][] vectors = new float[n][dim];
	private TwoBodies[] bodies = new TwoBodies[n];
	private Superposer superposer = new Superposer();
	int segments = 100;
	private float[][][][] table = new float[segments][segments][segments][segments];

	private void run() {

		for (int i = 0; i < n; i++) {
			bodies[i] = new TwoBodies(random, 10);
			points[i] = bodies[i].getPoints();
			for (int d = 0; d < dim; d++) {
				vectors[i][d] = random.nextFloat() * 3;
			}
		}
		for (int i = 0; i < 5; i++) {
			superpositions();
			analytical();
			analyticalArc();
			euclids();
			analyticalFake();
			table();
			//System.out.println("-----");
			//System.out.println();
		}
	}

	private double observe(double sum, double value) {
		sum += value;
		if (sum > 1000) {
			sum = sum - 2000;
		}
		return sum;
	}

	private void superpositions() {
		Timer.start();
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				superposer.set(points[x], points[y]);
				sum = observe(sum, superposer.getRmsd());
			}
		}
		Timer.stop();
		System.out.println("s " + Timer.get());
		System.out.println("observer: " + sum);
		System.out.println("");
	}

	private void euclids() {
		double sum = 0;
		Timer.start();
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum = observe(sum, euclid(x, y));
			}
		}
		Timer.stop();
		System.out.println("e " + Timer.get());
		System.out.println("observer: " + sum);
		System.out.println("");
	}

	private void analyticalFake() {
		Timer.start();
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum = observe(sum, Math.sin(x) / Math.cos(y));
			}
		}
		Timer.stop();
		System.out.println("f " + Timer.get());
		System.out.println("observer: " + sum);
		System.out.println("");
	}

	private void analytical() {
		Timer.start();
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum = observe(sum, bodies[x].angularDistance(bodies[y]));
			}
		}
		Timer.stop();
		System.out.println("ana " + Timer.get());
		System.out.println("observer: " + sum);
		System.out.println("");
	}

	private void analyticalArc() {
		Timer.start();
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum = observe(sum, bodies[x].angularDistanceOld(bodies[y]));
			}
		}
		Timer.stop();
		System.out.println("arc " + Timer.get());
		System.out.println("observer: " + sum);
		System.out.println("");
	}

	private void table() {
		Timer.start();
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum = observe(sum, table[x % segments][y % segments][(x + y) % segments][(x - y) % segments]);
			}
		}
		Timer.stop();
		System.out.println("t " + Timer.get());
		System.out.println("observer: " + sum);
		System.out.println("");
	}

	private float euclid(int x, int y) {
		float[] a = vectors[x];
		float[] b = vectors[y];
		float sum = 0;
		for (int d = 0; d < dim; d++) {
			float diff = a[d] - b[d];
			sum += diff * diff;
		}
		return sum;
	}

	public static void main(String[] args) {
		FastMeasurement m = new FastMeasurement();
		m.run();
	}
}
