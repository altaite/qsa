package orientation;

import altaite.geometry.primitives.Point;
import altaite.geometry.superposition.Superposer;
import analysis.Heatmap;
import global.io.LineFile;
import java.io.File;

public class SimilarityAnalysis {

	private int n = 100;
	private Superposer superposer = new Superposer();

	public SimilarityAnalysis() {
	}

	public void run() {
		PairGenerator generator = new PairGenerator();
		Vectorizer vectorizer = new Vectorizer(generator.getBase());

		Point[][] objects = new Point[n][];
		double[][] coordinates = new double[n][];
		for (int i = 0; i < n; i++) {
			objects[i] = generator.generatePair();
			coordinates[i] = vectorizer.getCoordinates(objects[i]);
		}

		Heatmap heatmap = new Heatmap(0, 0, 5, 5, 100, 100, new File("d:/t/data/qsa/heatmaps/colors.png"));
		LineFile lf = new LineFile("d:/t/data/qsa/orientation/rmsd_euclidean.csv");
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < x; y++) {
				double rmsd = rmsd(objects[x], objects[y]);
				double euclidean = euclidean(coordinates[x], coordinates[y]);
				heatmap.add(rmsd, euclidean);
				lf.writeLine(rmsd + "," + euclidean);
			}
		}
		heatmap.save(new File("d:/t/data/qsa/orientation/heatmap.png"));
	}

	private double rmsd(Point[] a, Point[] b) {
		superposer.set(a, b);
		return superposer.getRmsd();
	}

	// start with rotating one triangle, no translation
	// circular coordinates?
	public double euclidean(double[] x, double[] y) {
		double sum = 0;
		for (int dimension = 0; dimension < x.length; dimension++) {
			double difference = x[dimension] - y[dimension];
			sum += difference * difference;
		}
		return Math.sqrt(sum);
	}

	public static void main(String[] args) {
		SimilarityAnalysis a = new SimilarityAnalysis();
		a.run();
	}
}
