package analysis.statistics.bag;

import algorithm.Biword;
import geometry.primitives.Point;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BiwordBagOut {

	DataOutputStream out;

	public BiwordBagOut(File file) {
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void add(Biword biword) {
		Point[] points = biword.getPoints();
		try {
			out.writeInt(points.length);
			for (Point p : points) {
				float[] coords = p.getCoordsAsFloats();
				out.writeFloat(coords[0]);
				out.writeFloat(coords[1]);
				out.writeFloat(coords[2]);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() {
		try {
			out.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
