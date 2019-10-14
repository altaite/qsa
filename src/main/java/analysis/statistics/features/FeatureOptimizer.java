package analysis.statistics.features;

import global.io.Directories;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureOptimizer {

	private final int featureIndex;
	private final Directories dirs;
	private final float RMSD_THRESHOLD = Float.POSITIVE_INFINITY;
	private final int sampleSize;

	public FeatureOptimizer(Directories dirs, int featureIndex, int sampleSize) {
		this.dirs = dirs;
		this.featureIndex = featureIndex;
		this.sampleSize = sampleSize;
	}

	public void run() {
		float[] feature = readFeature();
		System.out.println("feture values " + feature.length);
		float[] rightmostNeighbors = findRightmostNeigbors(feature);
		float min = getMin(feature);
		float max = getMax(feature);
		float step = (max - min) / 100;
		for (float a = min + step; a < max; a += step) {
			float b = findRightBorder(a, feature, rightmostNeighbors);
			if (a < b) {
				System.out.println("Found borders " + a + " - " + b);
			}
			double value = evaluate(a, b, feature);
		}
	}

	private float[] findRightmostNeigbors(float[] feature) {
		float[] rightmostNeighbor = new float[feature.length];
		for (int i = 0; i < rightmostNeighbor.length; i++) {
			rightmostNeighbor[i] = feature[i];
		}
		ClosePairsIn in = new ClosePairsIn(dirs.getTestClosePairs());
		if (!in.isAvailable()) {
			throw new RuntimeException("close pairs not ready");
		}
		while (in.isAvailable()) {
			in.read();
			float rmsd = in.getRmsd();
			int a = in.getA();
			int b = in.getB();
			if (rmsd <= RMSD_THRESHOLD) {
				float current = rightmostNeighbor[a];
				float possible = feature[b];
				if (possible > current) {
					rightmostNeighbor[a] = possible;
				}
			}
		}
		in.close();
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
		double score = ((double) aSize) * bSize;
		System.out.println("Sizes: " + aSize + " | " + cSize + " | " + bSize + "   Score: " + score);
		return score;
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

	private float[] readFeature() {
		List<Float> list = new ArrayList<>();
		File file = dirs.getTestFeatureFile(featureIndex);
		try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			while (in.available() > 0 && list.size() < sampleSize) {
				float f = in.readFloat();
				list.add(f);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		float[] array = new float[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}
}
