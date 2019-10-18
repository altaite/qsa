package analysis.statistics.features;

import global.io.Directories;

/**
 * Finds optimal split of a give feature.
 */
public class SplitOptimizer {

	private final int featureIndex;
	private final Directories dirs;
	private final float RMSD_THRESHOLD = Float.POSITIVE_INFINITY;
	private final ClosePairs closePairs;
	private final Features features;

	public SplitOptimizer(Directories dirs, Features features, int featureIndex, ClosePairs closePairs) {
		this.dirs = dirs;
		this.featureIndex = featureIndex;
		this.features = features;
		this.closePairs = closePairs;
	}

	public double run() {
		float[] feature = features.getFeature(featureIndex);
		System.out.println("feture values " + feature.length);
		float[] rightmostNeighbors = findRightmostNeigbors(feature);
		float min = getMin(feature);
		float max = getMax(feature);
		float step = (max - min) / 100;
		double bestScore = 0;
		for (float a = min + step; a < max; a += step) {
			float b = findRightBorder(a, feature, rightmostNeighbors);
			if (a < b) {
				//System.out.println("Found borders " + a + " - " + b);
			}
			double score = evaluate(a, b, feature);
			if (score > bestScore) {
				bestScore = score;
			}
		}
		return bestScore;
	}

	private float[] findRightmostNeigbors(float[] feature) {
		float[] rightmostNeighbor = new float[feature.length];
		for (int i = 0; i < rightmostNeighbor.length; i++) {
			rightmostNeighbor[i] = feature[i];
		}
		for (Pair pair : closePairs.getList()) {
			float rmsd = pair.rmsd;
			int a = pair.a;
			int b = pair.b;
			if (rmsd <= RMSD_THRESHOLD) {
				float current = rightmostNeighbor[a];
				float possible = feature[b];
				if (possible > current) {
					rightmostNeighbor[a] = possible;
				}
			}
		}
		/*System.out.println("RIGHTMOST NEIGHBORS");
		for (int i = 0; i < feature.length; i++) {
			if (feature[i] < rightmostNeighbor[i]) {
				System.out.println(feature[i] + " -> " + rightmostNeighbor[i]);
			}
		}
		System.out.println("");
		 */
		return rightmostNeighbor;
	}

	private float findRightBorder(float leftBorder, float[] feature, float[] rightmostNeighbors) {
		float rightBorder = leftBorder;
		for (int i = 0; i < feature.length; i++) {
			if (feature[i] <= leftBorder) { // biword is in the left group (A)
				float right = rightmostNeighbors[i];
				if (rightBorder < right) {
					rightBorder = right;
				}
			}
		}
		return rightBorder;
	}

	/**
	 * Returns the fraction of similarity evaluation that would be saved on average in the evaluated tree node split.
	 */
	private double evaluate(float a, float b, float[] feature) {
		int aSize = 0;
		int bSize = 0;
		int cSize = 0;
		for (int i = 0; i < feature.length; i++) {
			float f = feature[i];
			if (f <= a) {
				aSize++;
			} else if (b < f) {
				bSize++;
			} else {
				cSize++;
			}

		}
		double score = computeScore(aSize, bSize, cSize);
		// TODO return object with min, max, a, b, sizes of sets
		//System.out.println("Sizes: " + aSize + " | " + cSize + " | " + bSize + "   Score: " + score);
		return score;
	}

	private double computeScore(double a, double b, double c) {
		return 2 * a * b / Math.pow(a + b + c, 2);
	}

	private float getMin(float[] values) {
		float min = Float.POSITIVE_INFINITY;
		for (float f : values) {
			if (f < min) {
				min = f;
			}
		}
		return min;
	}

	private float getMax(float[] values) {
		float max = Float.NEGATIVE_INFINITY;
		for (float f : values) {
			if (f > max) {
				max = f;
			}
		}
		return max;
	}

}
