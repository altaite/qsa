package orientation.body.energy;

import altaite.geometry.superposition.Superposer;
import analysis.Heatmap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import language.Format;
import orientation.body.TwoBodies;
import util.Timer;

public class Energy {

	// test if all points are in global minima
	// take one, scan and minimize
	private int n = 888;
	private int dim = 10;
	private int sampling = 100;
	private float[][] coord = new float[n][dim];
	private TwoBodies[] bodies = new TwoBodies[n];
	private float[][] distance = new float[n][];
	private Random random = new Random(1);

	private void run() {
		initialize();
		optimize();
	}

	private void initialize() {
		for (int i = 0; i < n; i++) {
			TwoBodies b = new TwoBodies(random, 10, true);
			bodies[i] = b;
		}
		for (int i = 0; i < n; i++) {
			for (int d = 0; d < dim; d++) {
				coord[i][d] = random.nextFloat();
			}
		}

		Superposer superposer = new Superposer();
		Timer.start();
		for (int x = 0; x < n; x++) {
			distance[x] = new float[x];
			for (int y = 0; y < x; y++) {
				superposer.set(bodies[x].getPoints(), bodies[y].getPoints());
				float rmsd = (float) superposer.getRmsd();
				distance[x][y] = rmsd;
			}
		}
		Timer.stop();
	}

	// adding objects, increasing continually - skipping initial phase, also testing accuracy better
	// cyclicity?
	// distance? real residues? ... after some finishing and testing with this framework, adding etc.
	// decrease max force or shift, less division
	// more foolproof generation of objects
	// object assignment - knn or ml, and proper test of accuracy
	// try the angle measure
	// adaptive movement, individually by points? set speed to fraction of what seems to move too much
	// some metric, if shifts are small, make it faster?
	// remove and place again problematic ... nope
// generate euclid plus original, let nn correct, or classifier, what is good cutoff, try some 90 rotations, get intuition
// correlate with angles instead, sum of rotation, how about maximum at each side, what does it mean
// saving and continuation?
	// sample partners until force starts to converge
	@Deprecated
	private void optimizeOld() {
		for (int i = 0; i < 10000; i++) {
			double forceSum = 0;
			float maxTension = 0;
			double avgTension = 0;
			int counter = 0;
			//System.out.println("");

			/*	for (int x = 0; x < n; x++) {
				print(coord[x]);
			}*/
			for (int x = 0; x < n; x++) {
				float[] force = zero();
				for (int y = 0; y < n; y++) {
					if (x == y || random.nextInt(sampling) != 0) {
						continue;
					}

					float[] v = minus(coord[y], coord[x]);
					//print(v);
					float t = tension(x, y);
					if (Math.abs(t) > Math.abs(maxTension)) {
						maxTension = t;
					}
					avgTension += Math.abs(t);
					counter++;
					float[] f = scale(v, t / size(v) / 500);
					add(force, f);
				}
				add(coord[x], force);
				forceSum += size(force);
			}
			System.out.println(" " + i + " " + forceSum);
			System.out.println("Max tension " + maxTension);
			System.out.println("Avg tension " + (avgTension / counter));
			if ((i + 1) % 100 == 0) {
				generateHeatmap(i);
			}
		}
	}

	//
	private void optimize() {
		for (int i = 0; i < 10000; i++) {
			double forceSum = 0;
			float maxTension = 0;
			double avgTension = 0;
			int counter = 0;
			for (int x = 0; x < n; x++) {
				optimizeOne(x);
			}
			if ((i + 1) % 100 == 0) {
				generateHeatmap(i);
			}
		}
	}

	private void optimizeOne(int index) {
		float[] force = zero();
		for (int y = 0; y < n; y++) {
			if (index == y || random.nextInt(sampling) != 0) {
				continue;
			}

			float[] v = minus(coord[y], coord[index]);
			float t = tension(index, y);
			float[] f = scale(v, t / size(v) / 500);
			add(force, f);
		}
		add(coord[index], force);
	}

	private void generateHeatmap(int i) {
		File colorFile = new File("d:/t/data/qsa/heatmaps/colors.png");
		Heatmap hm = new Heatmap(0, 0, 2, 2, 1000, 1000, colorFile);
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				if (x == y) {
					continue;
				}
				float rmsd = rmsd(x, y);
				float e = euclidean(x, y);
				hm.add(rmsd, e);
			}
		}
		String index = Format.zeroPad(i, 10);
		Path p = Paths.get("d:/t/data/qsa/orientation/energy_optimization_" + n + "_" + dim + "_" + sampling);
		try {
			Files.createDirectories(p);
			hm.save(p.resolve("energy_" + index + ".png").toFile());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void printAllCoords() {
		System.out.println("COORDS");
		for (int i = 0; i < n; i++) {
			print(coord[i]);
		}
		System.out.println();
	}

	private void print(float[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
		}
		System.out.println();
	}

	private float[] zero() {
		float[] a = new float[dim];
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
		float rmsd = rmsd(x, y);
		float e = euclidean(x, y);
		//System.out.println(rmsd + " <- " + e + "  " + (rmsd - e));
		float t = e - rmsd;
		if (t > 1) {
			t = 1;
		} else if (t < -1) {
			t = -1;
		}
		if (rmsd < 1) { // TODO function? more work in this direction
			// also make big tensions more important?
			return t * 5;
		}
		return t;
	}

	private float rmsd(int x, int y) {
		float rmsd;
		if (y < x) {
			rmsd = distance[x][y];
		} else {
			rmsd = distance[y][x];
		}
		return rmsd;
	}

	private float euclidean(int x, int y) {
		float sum = 0;
		for (int i = 0; i < dim; i++) {
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
