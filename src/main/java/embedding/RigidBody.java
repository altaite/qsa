package embedding;

import altaite.geometry.exceptions.CoordinateSystemException;
import altaite.geometry.primitives.CoordinateSystem;
import altaite.geometry.primitives.MatrixRotation;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Versor;
import altaite.geometry.superposition.Superposer;
import info.laht.dualquat.Quaternion;
import java.util.function.Function;
import structure.visual.Graphics;
import structure.visual.PdbLine;
import util.Counter;

/**
 *
 * @author Antonin Pavelka
 */
public class RigidBody {

	private final Point center;
	private final Point[] auxiliary;

	public RigidBody(Point center, Point[] auxiliaryPoints) {
		this.center = center;
		this.auxiliary = auxiliaryPoints;
		center.check();
	}

	public static RigidBody createWithCenter(Point center, Point... auxiliaryPoints) {
		return new RigidBody(center, auxiliaryPoints);
	}

	public static RigidBody create(Point... points) {
		return new RigidBody(Point.average(points), points);
	}

	public double rmsd(RigidBody other) {
		Point[] a = getAllPoints();
		Point[] b = other.getAllPoints();
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i].squaredDistance(b[i]);
		}
		return Math.sqrt(sum / a.length);
	}

	private RigidBody transform(Function<Point, Point> function) {
		Point[] newAuxiliary = new Point[auxiliary.length];
		for (int i = 0; i < auxiliary.length; i++) {
			newAuxiliary[i] = function.apply(auxiliary[i]);
		}
		Point newCenter = function.apply(center);
		return new RigidBody(newCenter, newAuxiliary);
	}

	public RigidBody center() {
		return transform(x -> x.minus(center));
	}

	public RigidBody translate(Point t) {
		return transform(x -> x.plus(t));
	}

	public RigidBody plusFiveX() {
		return translate(new Point(5, 0, 0));
	}

	public RigidBody express(CoordinateSystem system) {
		return transform(x -> system.expresPoint(x));
	}

	public RigidBody rotate(Versor versor) {
		centerIsInOrigin();
		return transform(x -> versor.rotate(x));
	}

	public RigidBody rotate(Quaternion quaternion) {
		centerIsInOrigin();
		return transform(x -> quaternion.toVersor().rotate(x));
	}

	public RigidBody rotate(MatrixRotation matrix) {
		//centerIsInOrigin();
		return transform(x -> matrix.rotate(x));
	}

	public RigidBody average(RigidBody other) {
		Point averagedCenter = center.average(other.center);
		Point[] averagedAuxiliary = new Point[auxiliary.length];
		for (int i = 0; i < auxiliary.length; i++) {
			averagedAuxiliary[i] = auxiliary[i].average(other.auxiliary[i]);
		}
		return new RigidBody(averagedCenter, averagedAuxiliary);
	}

	private void centerIsInOrigin() {
		if (!center.close(new Point(0, 0, 0))) {
			throw new RuntimeException();
		}
	}

	public Point getCenter() {
		return center;
	}

	public Point getFirstAuxiliary() {
		return auxiliary[0];
	}

	public Point getSecondAuxiliary() {
		return auxiliary[1];
	}

	public Point[] getAuxiliaryPoints() {
		return auxiliary;
	}

	public Point[] getAllPoints() {
		Point[] cloud = new Point[auxiliary.length + 1];
		cloud[0] = getCenter();
		for (int i = 0; i < auxiliary.length; i++) {
			cloud[i + 1] = auxiliary[i];
		}
		return cloud;
	}

	public CoordinateSystem getCoordinateSystem() throws CoordinateSystemException {
		if (auxiliary.length != 2) {
			throw new IllegalStateException();
		}
		Point vectorU = Point.vector(center, auxiliary[0]);
		Point vectorV = Point.vector(center, auxiliary[1]);
		return new CoordinateSystem(center, vectorU, vectorV);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Point p : getAllPoints()) {
			sb.append(p);
		}
		return sb.toString();
	}

	public String toPdb(int residueNumber, Counter serialCounter) {
		StringBuilder sb = new StringBuilder();
		int serial = serialCounter.value();
		sb.append(Graphics.pointToPdb(center, residueNumber, "H", serial));
		sb.append(Graphics.pointToPdb(auxiliary[0], residueNumber, "C", serial + 1));
		sb.append(Graphics.pointToPdb(auxiliary[1], residueNumber, "U", serial + 2));
		sb.append(PdbLine.getConnectString(serial, serial + 1)).append("\n");
		sb.append(PdbLine.getConnectString(serial, serial + 2)).append("\n");

		serialCounter.inc();
		serialCounter.inc();
		serialCounter.inc();

		return sb.toString();
	}

	public Versor computeRotation(RigidBody other) {
		Superposer superposer = new Superposer(true); // bodies are in zero origin
		superposer.set(getAuxiliaryPoints(), other.getAuxiliaryPoints());
		Versor versor = superposer.getVersor();
		return versor;
	}

}
