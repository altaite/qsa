package orientation.body;

import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
import altaite.geometry.superposition.Superposer;
import altaite.learn.Dataset;
import altaite.learn.MyInstance;
import analysis.Heatmap;
import java.io.File;
import java.util.Random;
import language.Format;

public class AnalyzeOrientations {

	private Random random = new Random(1);
	private Superposer superposer = new Superposer();
	File colorFile = new File("d:/t/data/qsa/heatmaps/colors.png");

	// once this works: align ghosts to make it symetrical?
	private void runLinear() {

		// second simpler random TwoBodies constructor
		// align to first
		// derive quats
		// subtract tarnsformation
		// measure transformation
		for (int d = 1; d < 20; d++) {
			Heatmap hm = new Heatmap(0, 0, 2, 6, 1000, 1000, colorFile);
			for (int i = 0; i < 200000; i++) {
				TwoBodies a = new TwoBodies(random, 10, true);
				TwoBodies b = new TwoBodies(random, 10, false);
				a.alignByFirst();
				b.alignByFirst();
				superposer.set(a.getPoints(), b.getPoints());
				double rmsd = superposer.getRmsd();
				Point translation = Point.average(b.getTriangle2()).minus(Point.average(a.getTriangle2()));
				superposer.set(a.getTriangle2(), b.getTriangle2());
				Quat rotation = superposer.getQuaternion();
				double fast = sizeOfMotion(translation, rotation, d);
				hm.add(rmsd, fast);
			}
			hm.save(new File("d:/t/data/qsa/orientation/linear_transformation_size/express_" + Format.zeroPad(d, 3) + ".png"));
		}
	}
	// TODO : two angles sum, square sum, rmsd, rmsd-vs-two-angles
	// TODO measure time of 10 euclid vs. triangle superposer, floats, no sqrt
	// GA: probably do not pay off
	// approximate minimum of the two angles function - is NN better than what I have? GA? !!!!!!!!!!!!!!!!!!!
	// trousers again, then GA? check equation first

	// try classifier! even for GA!
	// Equation.java
	// will it beat Energy?
	private double sizeOfMotion(Point translation, Quat rotation, double m) {
		double t = translation.size() / 100 * m;
		double a = 2 * Math.PI - rotation.toAxisAngle().getAngle();
		//System.out.println(a + "     " + t);
		return Math.sin(a / 2) + t;
	}

	/*private void runVectors() {
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
	}*/
	private double euclid(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			double d = a[i] - b[i];
			sum += d * d;
		}
		return Math.sqrt(sum);
	}

	// is the model any good? check all analogies
	// ALTERNATIVE: how much a small rotation around x changes the resulting angle
	// function of arc?
	// old equation, can it be solved geometrically?
	private void runTheory() {
		Dataset dataset = new Dataset();
		Heatmap hm = new Heatmap(0, -3.2 / 3, 2 * Math.PI, 3.2 / 3, 950 * 2, 950, colorFile);
		double maxFast = 0;
		for (int i = 0; i < 10000; i++) {
			//for (int i = 0; i < 1; i++) {
			TwoBodies a = new TwoBodies(random, 10, true);
			TwoBodies b = new TwoBodies(random, 10, true);
			superposer.set(a.getPoints(), b.getPoints());

			//double rmsd = superposer.getRmsd();
			//double fast = a.distance(b);
			double num = a.angularDistanceNumerical(b);
			double fast = a.angularDistance(b);
			/*if (fast < 0.55) {
				System.out.println(fast);
				System.out.println(a);
			}*/
			//System.out.println(rmsd + ", " + fast);
			//hm.add(rmsd, fast);
			//hm.add(rmsd, num);
			hm.add(num, fast);

			//System.out.println(num + " " + fast);
			/*if (num < 0.2 && fast > 1.3) {
				System.out.println(num + " " + fast);
				System.out.println(a);
				System.out.println(b);
				System.out.println("");
			}*/
 /*if (rmsd < 0.6 && num > Math.PI  - 0.1) {
				System.out.println("*");
			} else {
				System.out.println("+");
			}
			for (Point p : a.getPoints()) {
				System.out.println(p);
			}
			System.out.println("--");
			for (Point p : b.getPoints()) {
				System.out.println(p);
			}
			System.out.println("--");
			System.out.println("");*/
			//MyInstance instance = new MyInstance(true);
			//setFeatures(a, instance);
			//setFeatures(b, instance);
			//instance.addNumeric(rmsd);
			//dataset.add(instance);
		}
		//dataset.toArff(new File("d:/t/data/qsa/orientation/theory_fast_pairs.arff"));
		hm.save(new File("d:/t/data/qsa/orientation/theory_fast.png"));
	}

	private void setFeatures(TwoBodies a, MyInstance instance) {
		for (double f : a.getCoordinates()) {
			instance.addNumeric(f);
		}
	}

	public static void main(String[] args) {
		AnalyzeOrientations m = new AnalyzeOrientations();
		m.runTheory();
		//m.runLinear();
	}
}
