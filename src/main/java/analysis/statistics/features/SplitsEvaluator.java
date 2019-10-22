package analysis.statistics.features;

import analysis.statistics.Io;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import global.io.Directories;
import java.util.List;
import util.ProgressReporter;

/**
 * Evaluates the expected utility of each possible split of each feature.
 */
public class SplitsEvaluator {

	private final Directories dirs;
	private final Superposer superposer = new Superposer();
	private final Io io;

	private final boolean INITIALIZE = false;
	private final boolean OPTIMIZE = true;

	public SplitsEvaluator(Directories dirs) {
		this.dirs = dirs;
		this.io = new Io(dirs);
	}

	public void run() {
		if (INITIALIZE) {
			findClosePairs();
		}
		if (OPTIMIZE) {
			optimize();
		}
	}

	private void optimize() {
		ClosePairs pairs = loadClosePairs();
		Features features = new Features(dirs);
		Division best = null;
		for (int featureIndex : features.getIndexes()) {
			System.out.println("");
			System.out.println("EVALUATING FEATURE " + featureIndex);

			SplitOptimizer fo = new SplitOptimizer(dirs, features, featureIndex, pairs);
			Division division = fo.run();
			System.out.println(division);
			best = Division.getBetter(best, division);

		}
		System.out.println("BEST DIVISION");
		System.out.println(best);
		System.out.println("");
	}

	private void findClosePairs() {
		List<Point[]> list = io.getBiwords(FeatureParameters.sampleSize);
		/*long count = 0;
		long close = 0;*/
		long n = list.size();
		ClosePairsOut closePairs = new ClosePairsOut(dirs.getTestClosePairs());
		ProgressReporter progress = new ProgressReporter(n * (n - 1) / 2);
		for (int x = 0; x < list.size(); x++) {
			for (int y = 0; y < x; y++) {
				double rmsd = rmsd(list.get(x), list.get(y));
				if (rmsd < FeatureParameters.firstRmsdThreshold) {
					closePairs.add(x, y, rmsd);
					//close++;
				}
				/*count++;
				if (count % 10000 == 0) {
					long n = list.size();
					long total = (n * (n - 1)) / 2;
					long p = count * 100 / total;
					System.out.println(count + " " + total + " <" + close + "> " + p + " %");
				}*/
			}
		}
		closePairs.close();
	}

	private double rmsd(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getRmsd();
	}

	private ClosePairs loadClosePairs() {
		System.out.println("Loading close pairs ...");
		ClosePairs pairs = new ClosePairs();
		ClosePairsIn in = new ClosePairsIn(dirs.getTestClosePairs());
		if (!in.isAvailable()) {
			throw new RuntimeException("close pairs not ready");
		}
		boolean ok = true;
		while (ok) {
			try {
				in.read();
				float rmsd = in.getRmsd();
				int a = in.getA();
				int b = in.getB();
				if (a > b) {
					int temp = a;
					a = b;
					b = temp;
				}
				if (rmsd < FeatureParameters.secondRmsdThreshold) {
					pairs.add(a, b, rmsd);
				}
			} catch (Exception ex) {
				System.out.println("NORMAL END, pairs loaded: " + pairs.getList().size());
				ok = false;
			}
		}
		in.close();
		System.out.println("... done. Loaded pairs " + pairs.getList().size());
		return pairs;
	}

}
