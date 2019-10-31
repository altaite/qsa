package analysis.biword.search;

import analysis.statistics.bag.BiwordBagIn;
import altaite.geometry.primitives.Point;
import global.io.Directories;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Io {

	private Directories dirs;
	private Map<Integer, DataOutputStream> featuresOut = new HashMap<>();

	public Io(Directories dirs) {
		this.dirs = dirs;
	}

	public List<Point[]> getBiwords() {
		return getBiwords(Integer.MAX_VALUE);
	}

	public List<Point[]> getBiwords(int sampleSize) {
		List<Point[]> list = new ArrayList<>();
		BiwordBagIn bag = new BiwordBagIn(dirs.getTestBiwordBag());
		int i = 0;
		while (bag.hasNext() && i < sampleSize) {
			Point[] points = bag.read();
			list.add(points);
			i++;
		}
		bag.close();
		return list;
	}

	public void writeFeature(int index, double value) {
		DataOutputStream dos = featuresOut.get(index);
		try {
			if (dos == null) {
				File file = dirs.getTestFeatureFile(index);
				dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				featuresOut.put(index, dos);
			}
			dos.writeFloat((float) value);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void closeFeatures() {
		for (DataOutputStream dos : featuresOut.values()) {
			try {
				dos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
