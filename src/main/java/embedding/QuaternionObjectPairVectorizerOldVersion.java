package embedding;

import vectorization.dimension.Dimensions;
import altaite.geometry.exceptions.CoordinateSystemException;
import altaite.geometry.primitives.CoordinateSystem;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Versor;
import altaite.geometry.superposition.Superposer;
import language.Pair;
import structure.VectorizationException;
import vectorization.dimension.DimensionOpen;

/**
 *
 * Transforms a pair of rigid bodies into a tuple of real numbers capturing their orientation: Euclidean and Chebyshev
 * distance between the tuples corresponds well to RMSD between two pairs of rigid bodies.
 *
 * Both rigid bodies are required to be similar, at least so that their superposition cannot be ambiguous.
 *
 * @author Antonin Pavelka
 */
public class QuaternionObjectPairVectorizerOldVersion implements ObjectPairVectorizer {

	private static Dimensions dimensions = new Dimensions(
	/*	new DimensionOpen(), new DimensionOpen(), new DimensionOpen(), new DimensionOpen(), // quaternion for object rotations
		new DimensionOpen(),*/ new DimensionOpen(), new DimensionOpen(),/* unit vector in cartesian coordinates representing center-center line in coordinate 
		system of first object (but averaged with superposed second object) */
		new DimensionOpen()
	
	); // distance center-center

	@Override
	public Dimensions getDimensions() {
		return dimensions;
	}

	@Override
	public float[] vectorize(RigidBody b1, RigidBody b2, int imageNumber) throws VectorizationException {
		try {
			return vectorizeUncatched(b1, b2, imageNumber);
		} catch (CoordinateSystemException ex) {
			throw new VectorizationException(ex);
		}
	}

	private float[] vectorizeUncatched(RigidBody b1, RigidBody b2, int imageNumber) throws CoordinateSystemException {
		CoordinateSystem system = computeCoordinateSystem(b1, b2);
		Pair<Point[]> expressed = express(b1, b2, system);
		float[] rotation = getRotation(expressed, imageNumber);
		float[] translation = getTranslation(expressed);
		//return translation;
		return rotation;
		//return merge(rotation, translation);

	}

	private CoordinateSystem computeCoordinateSystem(RigidBody b1, RigidBody b2) throws CoordinateSystemException {
		Pair<Point[]> superposed = getSuperposedPoints(b1, b2);
		Point[] averaged = average(superposed);
		return createSystem(averaged); // positioned essentially at b1		
	}

	/* Superposed onto first. */
	private Pair<Point[]> getSuperposedPoints(RigidBody b1, RigidBody b2) {
		Superposer transformer = getTransformer(b1, b2);
		Point[] x = transformer.getXPoints();
		Point[] y = transformer.getTransformedYPoints();
		return new Pair(x, y);
	}

	/* Superposes the second on the first. */
	private Superposer getTransformer(RigidBody b1, RigidBody b2) {
		Superposer transformer = new Superposer();
		transformer.set(b1.getAllPoints(), b2.getAllPoints());
		transformer.getMatrix();
		return transformer;
	}

	private Point[] average(Pair<Point[]> superposed) {
		Point[] x = superposed._1;
		Point[] y = superposed._2;
		Point[] averaged = new Point[x.length];
		for (int i = 0; i < averaged.length; i++) {
			averaged[i] = x[i].plus(y[i]).divide(2);
		}
		return averaged;
	}

	private CoordinateSystem createSystem(Point[] points) throws CoordinateSystemException {
		Point origin = points[0];
		Point u = Point.vector(origin, points[1]);
		Point v = Point.vector(origin, points[2]);
		return new CoordinateSystem(origin, u, v);
	}

	private Pair<Point[]> express(RigidBody b1, RigidBody b2, CoordinateSystem system) {
		Point[] body1 = b1.getAllPoints();
		Point[] body2 = b2.getAllPoints();
		Pair<Point[]> points = new Pair(new Point[3], new Point[3]);
		for (int i = 0; i < 3; i++) {
			points._1[i] = system.expresPoint(body1[i]);
			points._2[i] = system.expresPoint(body2[i]);
		}
		return points;
	}

	private float[] getRotation(Pair<Point[]> pair, int imageNumber) {
		Superposer transformer = new Superposer();
		transformer.set(pair._1, pair._2);
		Versor versor = transformer.getVersor();
		//System.out.println("aaa " + versor);
		if (imageNumber == 1) {
			versor = versor.negate();
		}

		///System.out.println("vvv " + versor);
		return versor.toFloats();
	}

	private float[] getTranslation(Pair<Point[]> pair) {
		Point otherOrigin = pair._2[0];
		return otherOrigin.getCoordsAsFloats();
	}

	private float[] merge(float[] a, float[] b) {
		float[] c = new float[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			c[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			c[i + a.length] = b[i];
		}
		return c;
	}

	@Override
	public int getNumberOfImages() {
		return 2;
	}
}
