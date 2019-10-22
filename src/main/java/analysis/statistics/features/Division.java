package analysis.statistics.features;

public class Division {

	public final double left, right, middle; // sizes of sets separated by two thresholds
	private Double score;

	public Division(double left, double right, double middle) {
		this.left = left;
		this.right = right;
		this.middle = middle;
	}

	public double getScore() {
		if (score == null) {
			score = computeScore(left, right, middle);
		}
		return score;
	}

	private double computeScore(double a, double b, double c) {
		return 2 * a * b / Math.pow(a + b + c, 2);
	}

	public static Division getBetter(Division a, Division b) {
		if (a == null && b == null) {
			throw new RuntimeException();
		}
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		if (a.getScore() > b.getScore()) {
			return a;
		} else {
			return b;
		}
	}

	public String toString() {
		return left + " | " + middle + " | " + right + " = " + score;
	}
}
