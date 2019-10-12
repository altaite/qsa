package analysis.statistics.features;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Storage of a similarity matrix distances that are small.
 */
public class ClosePairsOut {

	private DataOutputStream out;

	public ClosePairsOut(File file) {
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void add(int a, int b, double rmsd) {
		try {
			out.writeInt(a);
			out.writeInt(b);
			out.writeFloat((float) rmsd);
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
