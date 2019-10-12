package analysis.statistics.features;

import analysis.statistics.Io;
import geometry.primitives.Point;
import geometry.superposition.Superposer;
import global.io.Directories;
import java.util.List;

public class FeatureAnalyzer {
	
	private Directories dirs;
	private List<Point[]> list;
	private Superposer superposer = new Superposer();
	private ClosePairsOut closePairs;
	private Io io;
	
	private boolean INITIALIZE = false;
	private final double THRESHOLD = 3;
	private int SAMPLE_SIZE = 10000;
	
	public FeatureAnalyzer(Directories dirs) {
		this.dirs = dirs;
		this.closePairs = new ClosePairsOut(dirs.getTestClosePairs());
		this.io = new Io(dirs);
	}
	
	public void run() {
		if (INITIALIZE) {
			list = io.getBiwords(SAMPLE_SIZE);
			findClosePairs();
		}
		
	}
	
	private void findClosePairs() {
		long count = 0;
		long close = 0;
		for (int x = 0; x < list.size(); x++) {
			for (int y = 0; y < x; y++) {
				double rmsd = rmsd(list.get(x), list.get(y));
				if (rmsd < THRESHOLD) {
					closePairs.add(x, y, rmsd);
					close++;
				}
				count++;
				if (count % 10000 == 0) {
					long n = list.size();
					long total = (n * (n - 1)) / 2;
					long p = count * 100 / total;
					System.out.println(count + " " + total + " <" + close + "> " + p + " %");
				}
			}
		}
	}
	
	private double rmsd(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getRmsd();
	}
	
}
