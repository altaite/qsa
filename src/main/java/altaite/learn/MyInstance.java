package altaite.learn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyInstance {

	private List<Double> list = new ArrayList<>();
	private List<Boolean> isNominal = new ArrayList<>();
	private boolean hasTarget;

	public MyInstance(boolean hasTarget) {
		this.hasTarget = hasTarget;
	}

	public void addNominal(int feature) {
		list.add((double) feature);
		isNominal.add(true);
	}

	public void addNumeric(double feature) {
		list.add(feature);
		isNominal.add(false);
	}

	public boolean isCompatible(MyInstance other) {
		if (list.size() != other.list.size() || isNominal.size() != other.isNominal.size()) {
			return false;
		}
		for (int i = 0; i < isNominal.size(); i++) {
			if (!Objects.equals(isNominal.get(i), other.isNominal.get(i))) {
				return false;
			}
		}
		return true;
	}

	public boolean isNominal(int i) {
		return isNominal.get(i);
	}

	public int size() {
		return list.size();
	}

	public double get(int i) {
		return list.get(i);
	}

	public double getTarget() {
		if (!hasTarget) {
			throw new RuntimeException();
		}
		return list.get(list.size() - 1);
	}

	public double[] getIndependent() {
		double[] a;
		if (hasTarget) {
			a = new double[list.size() - 1];
		} else {
			a = new double[list.size()];
		}
		for (int i = 0; i < a.length; i++) {
			a[i] = list.get(i);
		}
		return a;
	}
}
