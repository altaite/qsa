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

	private Directories dirs;
	private List<Point[]> list;
	private Superposer superposer = new Superposer();
	private ClosePairsOut closePairs;
	private Io io;

	private boolean INITIALIZE = true;
	private final double THRESHOLD = 3;
	private int SAMPLE_SIZE = 10000;

	public BestDivisionFinder(Directories dirs) {
		this.dirs = dirs;
		this.closePairs = new ClosePairsOut(dirs.getTestClosePairs());
		this.io = new Io(dirs);
	}

	public void run() {
		if (INITIALIZE) {
			list = io.getBiwords(SAMPLE_SIZE);
			findClosePairs();
		} else {
			optimize();
		}
	}

	private void findClosePairs() {
		/*long count = 0;
		long close = 0;*/
		long n = list.size();
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
	}

	private double rmsd(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getRmsd();
	}

	private void optimize() {
		List<Integer> featureIndexes = readFeatureIndexes();
		for (int featureIndex : featureIndexes) {
			evaluateFeature(featureIndex);
		}
	}

	private void evaluateFeature(int featureIndex) {
		FeatureOptimizer fo = new FeatureOptimizer(dirs, featureIndex);
		fo.run();
	}

	private List<Integer> readFeatureIndexes() {
		List<Integer> featureIndexes = new ArrayList<>();
		for (String fileName : dirs.getTestFeatureFiles().list()) {
			try {
				int i = new Integer(fileName);
				featureIndexes.add(i);
			} catch (Exception ex) {

			}
		}
		Collections.sort(featureIndexes);
		return featureIndexes;
	}
}
