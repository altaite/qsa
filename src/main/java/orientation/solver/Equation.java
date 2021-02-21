package orientation.solver;

import altaite.learn.Dataset;
import altaite.learn.MyInstance;

public class Equation {

	// each query pair will carry parameters of accepting function for the other numnber pair 
	// visualize the border for few
	// solving just for few + realistic problem
	private void run() {
		int n = 100000;
		Dataset dataset = new Dataset();
		for (int i = 0; i < n; i++) {
			MyInstance instance = new MyInstance(true);
			double ax = Math.random() * 2 - 1;
			double aw = Math.random() * 2 - 1; // TODO constrain by ax
			double bx = Math.random() * 2 - 1;
			double bw = Math.random() * 2 - 1;
			Double bestF = Double.MAX_VALUE;
			for (double a = 0; a < Math.PI * 2; a += 0.01) {
				double f = equation(a, aw, ax, bw, bx);
				if (f < bestF) {
					bestF = f;
				}
			}
			instance.addNumeric(ax);
			instance.addNumeric(aw);
			instance.addNumeric(bx);
			instance.addNumeric(bw);
			instance.addNumeric(bestF);
			dataset.add(instance);
		}
		dataset.toArff("d:/t/data/qsa/orientation/equation3.arff");
	}

	// looks wrong
	// recompute the function
	// also look at decomposition by protein?
	
	private void blot() {
		int n = 100000;
		Dataset dataset = new Dataset();
		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				MyInstance instance = new MyInstance(true);
				double ax = 0.2;//Math.random() * 2 - 1;
				double aw = ((double)x) / 50;//Math.random() * 2 - 1; // TODO constrain by ax
				double bx = 0.2;//Math.random() * 2 - 1;
				double bw = ((double)y) / 50;//Math.random() * 2 - 1;
				Double bestF = Double.MAX_VALUE;
				for (double a = 0; a < Math.PI * 2; a += 0.01) {
					double f = equation(a, aw, ax, bw, bx);
					if (f < bestF) {
						bestF = f;
					}
				}
				System.out.print(bestF < Math.PI / 4 ? "o" : " ");
			}
			System.out.println();
		}
	}

	private void runClassification() {
		int n = 100000;
		Dataset dataset = new Dataset();
		for (int i = 0; i < n; i++) {
			MyInstance instance = new MyInstance(true);
			double ax = Math.random() * 2 - 1;
			double aw = Math.random() * 2 - 1; // TODO constrain by ax
			double bx = Math.random() * 2 - 1;
			double bw = Math.random() * 2 - 1;
			Double bestF = Double.MAX_VALUE;
			for (double a = 0; a < Math.PI * 2; a += 0.01) {
				double f = equation(a, aw, ax, bw, bx);
				if (f < bestF) {
					bestF = f;
				}
			}
			instance.addNumeric(ax);
			instance.addNumeric(aw);
			instance.addNumeric(bx);
			instance.addNumeric(bw);
			instance.addNominal(bestF < Math.PI / 4 ? 0 : 1); // 1/4 ~ 45°
			dataset.add(instance);
		}
		dataset.toArff("d:/t/data/qsa/orientation/equation_classification_1.arff");
	}

	private double equation(double a, double aw, double ax, double bw, double bx) {
		double sa = Math.sin(a);
		double ca = Math.cos(a);
		return Math.acos(ca * aw - sa * ax) + Math.acos(ca * bw - sa * bx);
	}

	private double equationOld(double x, double y) {
		return Math.sin(x) + 2 * Math.cos(y);
	}

	public static void main(String[] args) {
		Equation e = new Equation();
		//e.run();
		//e.runClassification();
		e.blot();
	}
}
