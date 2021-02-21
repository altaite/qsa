package orientation.body;

import altaite.collection.performance.Timer;
import altaite.geometry.primitives.Point;
import altaite.geometry.superposition.Superposer;
import java.util.Random;

public class VectorQcpSpeedComparison {

	// TODO how quick is solving the alpha equation?
	// fast solution, draw for random combination and figure it out
	// or memory access of table, plus interpolation spline
	private int n = 10000;
	private int dim = 10;
	private Random random = new Random();
	private Point[][] points = new Point[n][];
	private float[][] vectors = new float[n][dim];
	private Superposer superposer = new Superposer();
	int segments = 100;
	private float[][][][] table = new float[segments][segments][segments][segments];

	private void run() {

		for (int i = 0; i < n; i++) {
			TwoBodies b = new TwoBodies(random, 10, true);
			points[i] = b.getPoints();
			for (int d = 0; d < dim; d++) {
				vectors[i][d] = random.nextFloat() * 3;
			}
		}
		for (int i = 0; i < 10; i++) {
			superpositions();
			analyticalFake();
			euclids();
			table();
		}
	}

	private void superpositions() {
		Timer.start();
		int count =0;
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				superposer.set(points[x], points[y]);
				sum += superposer.getRmsd();
				count++;
			}
		}
		Timer.stop();
		System.out.println("s " + Timer.get() + " " + count);
		System.out.println(sum);
	}

	private void euclids() {
		Timer.start();
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				euclid(x, y);
			}
		}
		Timer.stop();
		System.out.println("e " + Timer.get());
	}

	private void analyticalFake() {
		Timer.start();
		double sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum *= Math.sin(x) / Math.cos(y);
			}
		}
		Timer.stop();
		System.out.println("a " + Timer.get());
		System.out.println("sum * " + Timer.get());
	}

	private void table() {
		Timer.start();
		float sum = 0;
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				sum += table[x % segments][y % segments][(x+y) % segments][(x-y) % segments];
			}
		}
		Timer.stop();
		System.out.println("t " + Timer.get());
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
		VectorQcpSpeedComparison m = new VectorQcpSpeedComparison();
		m.run();
	}
}
