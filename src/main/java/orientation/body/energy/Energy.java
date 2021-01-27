package orientation.body.energy;

import altaite.geometry.superposition.Superposer;
import java.util.Arrays;
import java.util.Random;
import orientation.body.TwoBodies;

public class Energy {

	private int n = 10;
	private int d = 3;
	private float[][] coord = new float[n][d];
	private TwoBodies[] bodies = new TwoBodies[n];
	private float[][] distance = new float[n][];
	private Random random = new Random(1);

	private void run() {
		initialize();
		optimize();
	}

	private void initialize() {
		for (int i = 0; i < n; i++) {
			TwoBodies b = new TwoBodies(random, 10);
			bodies[i] = b;
		}
		Superposer superposer = new Superposer();
		for (int x = 0; x < n; x++) {
			distance[x] = new float[x];
			for (int y = 0; y < x; y++) {
				superposer.set(bodies[x].getPoints(), bodies[y].getPoints());
				float rmsd = (float) superposer.getRmsd();
				distance[x][y] = rmsd;
			}

		}
	}

	private void optimize() {
		for (int x = 0; x < n; x++) {
			float[] force = zero();
			for (int y = 0; y < n; y++) {
				float[] v = minus(coord[y], coord[x]);
				float[] f = scale(v, tension(x, y) / size(v));
				add(force, f);
			}
			add(coord[x], force);
		}
	}

	private float[] zero() {
		float[] a = new float[d];
		Arrays.fill(a, 0);
		return a;
	}

	private void add(float[] a, float[] b) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b[i];
		}
	}

	private float[] minus(float[] a, float[] b) {
		float[] result = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] - b[i];
		}
		return result;
	}

	private float[] scale(float[] a, float f) {
		float[] result = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * f;
		}
		return result;
	}

	private float size(float[] a) {
		float size = 0;
		for (int i = 0; i < a.length; i++) {
			size += a[i] * a[i];
		}
		return (float) Math.sqrt(size);
	}

	private float tension(int x, int y) {
		float rmsd;
		if (y < x) {
			rmsd = distance[x][y];
		} else {
			rmsd = distance[y][x];
		}
		float e = euclidean(x, y);
		return rmsd - e;
	}

	private float euclidean(int x, int y) {
		float sum = 0;
		for (int i = 0; i < d; i++) {
			float d = coord[x][i] - coord[y][i];
			sum += d * d;
		}
		return (float) Math.sqrt(sum);
	}

	public static void main(String[] args) {
		Energy e = new Energy();
		e.run();
	}
}
