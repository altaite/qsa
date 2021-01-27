package orientation.solver;

import altaite.learn.Dataset;
import altaite.learn.MyInstance;

public class Equation {

	private void run() {
		int n = 100000;
		Dataset dataset = new Dataset();
		for (int i = 0; i < n; i++) {
			MyInstance instance = new MyInstance(true);
			double ax = Math.random() * 2 - 1;
			double aw = Math.random() * 2 - 1; // TODO constrain by ax
			double bx = Math.random() * 2 - 1;
			double bw = Math.random() * 2 - 1;
			double bestA = Double.MAX_VALUE;
			Double bestF = null;
			for (double a = 0; a < Math.PI * 2; a += 0.01) {
				double f = equation(a, aw, ax, bw, bx);
				if (f < bestA) {
					bestA = f;
					bestF = f;
				}
			}
			instance.addNumeric(bestF);
			instance.addNumeric(ax);
			instance.addNumeric(aw);
			instance.addNumeric(bx);
			instance.addNumeric(bw);
			dataset.add(instance);
		}
		dataset.toArff("d:/t/data/qsa/orientation/equation.arff");
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
		e.run();
	}
}
