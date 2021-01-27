package orientation.body;

import altaite.geometry.superposition.Superposer;
import altaite.learn.Dataset;
import altaite.learn.MyInstance;
import analysis.Heatmap;
import java.io.File;
import java.util.Random;

public class AnalyzeOrientations {

	private Random random = new Random(1);
	private Superposer superposer = new Superposer();
	File colorFile = new File("d:/t/data/qsa/heatmaps/colors.png");
	Heatmap hm = new Heatmap(0, 0, 2, 30, 1000, 1000, colorFile);

	private void run() {
		CoordinatesTorsionVectorizer vectorizer = new CoordinatesTorsionVectorizer(
			TwoBodies.getBodyInStandardOrientation());

		for (int i = 0; i < 200000; i++) {
			TwoBodies a = new TwoBodies(random, 10);
			TwoBodies b = new TwoBodies(random, 10);
			double[] va  = vectorizer.vectorize(a.getTriangle1(), a.getTriangle2());
			double[] vb = vectorizer.vectorize(b.getTriangle1(), b.getTriangle2());
			superposer.set(a.getPoints(), b.getPoints());
			double rmsd = superposer.getRmsd();
			double fast = euclid(va,vb );
			//System.out.println(rmsd + ", " + fast);
			hm.add(rmsd, fast);
		}
		hm.save(new File("d:/t/data/qsa/orientation/express.png"));
	}

	private double euclid(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double d = a[i] - b[i];
			sum += d * d;
		}
		return Math.sqrt(sum);
	}

	private void runTheory() {
		Dataset dataset = new Dataset();
		Heatmap hm = new Heatmap(0, 0, 2, 5, 1000, 1000, colorFile);
		double maxFast = 0;
		for (int i = 0; i < 100000; i++) {
			TwoBodies a = new TwoBodies(random, 10);
			TwoBodies b = new TwoBodies(random, 10);
			superposer.set(a.getPoints(), b.getPoints());
			double rmsd = superposer.getRmsd();
			double fast = a.distance(b);
			//System.out.println(rmsd + ", " + fast);
			hm.add(rmsd, fast);
			MyInstance instance = new MyInstance(true);
			setFeatures(a, instance);
			setFeatures(b,instance);
			instance.addNumeric(rmsd);
			dataset.add(instance);

		}
		dataset.toArff(new File("d:/t/data/qsa/orientation/theory_pairs.arff"));
		hm.save(new File("d:/t/data/qsa/orientation/theory.png"));
	}
	
	private void setFeatures(TwoBodies a, MyInstance instance) {
		for (double f : a.getCoordinates()) {
			instance.addNumeric(f);
		}
	}

	public static void main(String[] args) {
		AnalyzeOrientations m = new AnalyzeOrientations();
		m.runTheory();
	}
}
