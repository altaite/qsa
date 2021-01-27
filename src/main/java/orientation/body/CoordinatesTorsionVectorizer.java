package orientation.body;

import altaite.geometry.primitives.AxisAngle;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Quat;
import altaite.geometry.superposition.Superposer;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class CoordinatesTorsionVectorizer {

	private Superposer superposer = new Superposer();
	private Point[] objectInStandardOrientation;

	public CoordinatesTorsionVectorizer(Point[] objectInStandardOrientation) {
		this.objectInStandardOrientation = objectInStandardOrientation;
	}

	// dual quaternion, internal ones, some kind of composition to express angle of the difference?
	public double[] vectorize(Point[] cloud1, Point[] cloud2) {
		Point p1 = Point.center(cloud1);
		Point p2 = Point.center(cloud2);
		Point a = p1.minus(p2);
		Point b = p2.minus(p1);
		Point e1 = express(cloud1, b);
		Point e2 = express(cloud2, a);
		double[] dihedral = dihedral(cloud1,cloud2, a.size()/10);
		double[] v = {e1.x, e1.y, e1.z, e2.x, e2.y, e2.z, dihedral[0], dihedral[1]};
		return v;
	}

	private Point express(Point[] baseOrientation, Point point) {
		superposer.set(baseOrientation, objectInStandardOrientation);
		Quat q = superposer.getQuaternion();
		Point p = q.rotate(point);
		return p;
	}
	
	// works only thanks to TwoObjects being aligned to X
	private double[] dihedral(Point[] cloud1, Point[] cloud2, double distance) {
		superposer.set(cloud1, cloud2);
		Quat q = superposer.getQuaternion();
		AxisAngle aa = q.toAxisAngle();
		double[] v = {distance * aa.getAxis().x * (aa.getAngle()),distance * aa.getAxis().x * (aa.getAngle())};
		//double[] v = {0,0};
		return v;
		
	}

	private Point center(Point[] points) {
		Point center = new Point(0, 0, 0);
		for (Point p : points) {
			center = center.plus(p);
		}
		return center.divide(points.length);
	}
}
