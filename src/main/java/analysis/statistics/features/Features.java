package analysis.statistics.features;

import global.io.Directories;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Features {

	private final Directories dirs;
	private final float[][] features;
	private final int[] indexes;

	public Features(Directories dirs) {
		this.dirs = dirs;
		this.indexes = readIndexes();
		this.features = loadFeatures();
	}

	public int[] getIndexes() {
		return indexes;
	}

	private float[][] loadFeatures() {
		float[][] values = new float[indexes.length][];
		for (int i : indexes) {
			values[i] = readFeature(i);
		}
		return values;
	}

	private int[] readIndexes() {
		List<Integer> featureIndexes = new ArrayList<>();
		for (String fileName : dirs.getTestFeatureFiles().list()) {
			try {
				String no = fileName.substring(0, fileName.length() - 4);
				int i = new Integer(no);
				featureIndexes.add(i);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Collections.sort(featureIndexes);
		int[] array = new int[featureIndexes.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = featureIndexes.get(i);
		}
		return array;
	}

	public float[] getFeature(int featureIndex) {
		return features[featureIndex];
	}

	private float[] readFeature(int featureIndex) {
		List<Float> list = new ArrayList<>();
		File file = dirs.getTestFeatureFile(featureIndex);
		try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			while (in.available() > 0 && list.size() < FeatureParameters.sampleSize) {
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
