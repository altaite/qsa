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

	public Division run() {
		float[] feature = features.getFeature(featureIndex);
		System.out.println("feture values " + feature.length);
		float[] rightmostNeighbors = findRightmostNeigbors(feature);
		float min = getMin(feature);
		float max = getMax(feature);
		float step = (max - min) / 100;
		Division best = null;
		for (float a = min + step; a < max; a += step) {
			float b = findRightBorder(a, feature, rightmostNeighbors);
			Division division = evaluate(a, b, feature);
			best = Division.getBetter(best, division);
		}
		return best;
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
	private Division evaluate(float a, float b, float[] feature) {
		int left = 0;
		int right = 0;
		int middle = 0;
		for (int i = 0; i < feature.length; i++) {
			float f = feature[i];
			if (f <= a) {
				left++;
			} else if (b < f) {
				right++;
			} else {
				middle++;
			}

		}
		return new Division(left, right, middle);
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
