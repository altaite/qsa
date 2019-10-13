package analysis.statistics.features;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ClosePairsIn {

	private DataInputStream in;

	private int a, b;
	private float rmsd;

	public ClosePairsIn(File file) {
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isAvailable() {
		try {
			return in.available() > 0;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void read() {
		try {
			a = in.readInt();
			b = in.readInt();
			rmsd = in.readFloat();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public float getRmsd() {
		return rmsd;
	}

	public void close() {
		try {
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
