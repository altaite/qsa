package frame;

import altaite.geometry.primitives.Point;
import altaite.geometry.superposition.Superposer;
import info.laht.dualquat.Quaternion;
import structure.Residue;

public class StandardResidueOrientation {

	private Superposer superposer = new Superposer();
	private Point[] standardPoints;

	public StandardResidueOrientation(Residue standardResidue) {
		standardPoints = extractAndCenter(standardResidue);
	}

	// dont bother now, just do one, 
	/*public StandardResidueOrientation(Residue... standardResidues) {
		Point[][] points = new Point[standardResidues.length][];
		for (int i = 0; i < standardResidues.length; i++) {
			points[i] = extractAndCenter(standardResidues[i]);
		}
		for (int i = 1; i < points.length; i++) {
			superposer.set(points[0], points[i]);
			
		}
	}*/
	public Quaternion getQuaternion(Residue r) {
		Point[] residuePoints = extractAndCenter(r);
		superposer.set(standardPoints, residuePoints);
		Quaternion transformation = superposer.getQuaternion();
		double rmsd = superposer.getRmsd();
		// check rmsd here
		return transformation;
	}

	private Point[] extractAndCenter(Residue r) {
		Point[] points = {r.getC(), r.getN(), r.getO(), r.getCa()};
		Point average = Point.average(points);
		Point[] centered = new Point[points.length];
		for (int i = 0; i < centered.length; i++) {
			centered[i] = points[i].minus(average);
		}
		return centered;
	}
}
