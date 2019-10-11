package analysis.statistics.features;

import algorithm.Biword;
import analysis.statistics.bag.BiwordBagIn;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import global.io.Directories;
import java.util.ArrayList;
import java.util.List;

public class FeatureAnalyzer {

	private Directories dirs;
	private List<Point[]> list;
	private Superposer superposer = new Superposer();
	private final double THRESHOLD = 3;
	private ClosePairs closePairs = new ClosePairs();

	public FeatureAnalyzer(Directories dirs) {
		this.dirs = dirs;
	}

	public void run() {
		readList();
		findClosePairs();
	}

	private void readList() {
		list = new ArrayList<>();
		BiwordBagIn bag = new BiwordBagIn(dirs.getTestBiwordBag());
		while (bag.hasNext()) {
			Point[] points = bag.read();
			list.add(points);
		}
		bag.close();
	}

	private void findClosePairs() {
		for (int x = 0; x < list.size(); x++) {
			for (int y = 0; y < x; y++) {
				double rmsd = rmsd(list.get(x), list.get(y));
				if (rmsd < THRESHOLD) {
					closePairs.add(x, y, rmsd);
				}
			}
		}
	}

	private double rmsd(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getRmsd();
	}

}
