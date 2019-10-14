package analysis.statistics.features;

import analysis.statistics.Io;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import global.io.Directories;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import util.ProgressReporter;

public class BestDivisionFinder {

	private final Directories dirs;
	private final Superposer superposer = new Superposer();
	private final Io io;

	private final boolean INITIALIZE = false;
	private final boolean OPTIMIZE = true;
	private final double THRESHOLD = 3;
	public final int SAMPLE_SIZE = 20000;

	public BestDivisionFinder(Directories dirs) {
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

	private void findClosePairs() {
		List<Point[]> list = io.getBiwords(SAMPLE_SIZE);
		/*long count = 0;
		long close = 0;*/
		long n = list.size();
		ClosePairsOut closePairs = new ClosePairsOut(dirs.getTestClosePairs());
		ProgressReporter progress = new ProgressReporter(n * (n - 1) / 2);
		for (int x = 0; x < list.size(); x++) {
			for (int y = 0; y < x; y++) {
				double rmsd = rmsd(list.get(x), list.get(y));
				if (rmsd < THRESHOLD) {
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

	private void optimize() {
		List<Integer> featureIndexes = readFeatureIndexes();
		System.out.println("optimizing features");
		for (int featureIndex : featureIndexes) {
			System.out.println("evaluating feature " + featureIndex);
			evaluateFeature(featureIndex);
		}
	}

	private void evaluateFeature(int featureIndex) {
		FeatureOptimizer fo = new FeatureOptimizer(dirs, featureIndex, SAMPLE_SIZE);
		fo.run();
	}

	private List<Integer> readFeatureIndexes() {
		List<Integer> featureIndexes = new ArrayList<>();
		for (String fileName : dirs.getTestFeatureFiles().list()) {
			try {
				String no = fileName.substring(0, fileName.length() - 4);
				int i = new Integer(no);
				featureIndexes.add(i);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Collections.sort(featureIndexes);
		return featureIndexes;
	}
}
