package analysis.statistics.bag;

import altaite.geometry.primitives.Point;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BiwordBagIn {

	DataInputStream in;

	public BiwordBagIn(File file) {
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean hasNext() {
		try {
			return in.available() > 0;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Point[] read() {
		try {
			int n = in.readInt();
			Point[] points = new Point[n];
			for (int i = 0; i < n; i++) {
				float x = in.readFloat();
				float y = in.readFloat();
				float z = in.readFloat();
				points[i] = new Point(x, y, z);
			}
			return points;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() {
		try {
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
