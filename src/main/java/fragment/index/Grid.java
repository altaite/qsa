package fragment.index;

import algorithm.Biword;
import embedding.Vectorizer;
import grid.sparse.MultidimensionalArray;
import grid.sparse.BufferOfLong;
import structure.VectorizationException;

/**
 * Orthogonal grid for range search of biwords.
 */
public class Grid {

	private double[] globalMin;
	private double[] globalMax;
	private int bracketN;
	//private int biwordN;
	private MultidimensionalArray grid;
	//private BufferOfLong out;
	private float[] box;
	private Vectorizer vectorizer;

	Grid() {

	}

	Grid(Vectorizer vectorizer, int bins, int biwordN, float[] box, double[] globalMin, double[] globalMax) {
		this.vectorizer = vectorizer;
		this.bracketN = bins;
		this.box = box;
		this.globalMin = globalMin;
		this.globalMax = globalMax;
		this.grid = new MultidimensionalArray(vectorizer.getDimensions(), bins, biwordN);
	}

	void insert(Biword bw) throws VectorizationException {
		float[] v = vectorizer.getCoordinates(bw.getCanonicalTuple());
		grid.insert(discretize(v), bw.getId().endcode());
	}

	private void printBoundaries() {
		System.out.println("BOUNDARIES");
		for (int d = 0; d < globalMin.length; d++) {
			System.out.println(globalMin[d] + " - " + globalMax[d] + " | ");
		}
		System.out.println("----");
	}

	public void query(Biword bw, BufferOfLong out) throws VectorizationException {
		float[] vector = vectorizer.getCoordinates(bw.getCanonicalTuple());
		int dim = vector.length;
		float[] min = new float[dim];
		float[] max = new float[dim];
		for (int i = 0; i < dim; i++) {
			min[i] = vector[i] - box[i];
			max[i] = vector[i] + box[i];
		}
		grid.getRange(discretize(min), discretize(max), out);
	}

	private byte[] discretize(float[] x) {
		byte[] indexes = new byte[x.length];
		for (int i = 0; i < x.length; i++) {
			float v = x[i];
			int index = (int) Math.floor((v - globalMin[i]) / (globalMax[i] - globalMin[i]) * bracketN);
			if (index < Byte.MIN_VALUE || index > Byte.MAX_VALUE) {
				throw new RuntimeException();
			}
			indexes[i] = (byte) index;
		}
		return indexes;
	}

	public long size() {
		return grid.size();
	}
}
