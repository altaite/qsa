package frame;

import altaite.geometry.primitives.Coordinates;

public class AtomCoordsResidueIndex implements Coordinates {

	private short index;
	private double[] coords = new double[3];

	public AtomCoordsResidueIndex(double x, double y, double z, short residueIndex) {
		this.coords[0] = x;
		this.coords[1] = y;
		this.coords[2] = z;
		this.index = residueIndex;
	}

	public double[] getCoords() {
		return coords;
	}

	public short getResidueIndex() {
		return index;
	}

}
