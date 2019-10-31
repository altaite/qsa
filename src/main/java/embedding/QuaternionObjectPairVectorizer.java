package embedding;

import vectorization.dimension.Dimensions;
import altaite.geometry.exceptions.CoordinateSystemException;
import altaite.geometry.primitives.AxisAngle;
import altaite.geometry.primitives.CoordinateSystem;
import altaite.geometry.primitives.Point;
import altaite.geometry.primitives.Versor;
import altaite.geometry.superposition.Superposer;
import global.io.LineFile;
import language.Pair;
import language.Util;
import structure.VectorizationException;
import util.Counter;
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
public class QuaternionObjectPairVectorizer implements ObjectPairVectorizer {

	private static Dimensions dimensions = new Dimensions(
		new DimensionOpen(), new DimensionOpen(), new DimensionOpen(), new DimensionOpen(), // quaternion for object rotations
		new DimensionOpen(), new DimensionOpen(), new DimensionOpen(),// unit vector in cartesian coordinates representing center-center line in coordinate 	system of first object (but averaged with superposed second object) 
		new DimensionOpen()); // distance center-center

	private LineFile file = new LineFile("e:/data/qsa/visualization/vec01.pdb");
	private Counter serial = new Counter();

	public QuaternionObjectPairVectorizer() {
		file.clean();
	}

	private void save(RigidBody b, int number) {
		file.write(b.toPdb(number, serial));
	}

	private void kill() {
		if (true) {
			System.exit(0);
		}
	}

	@Override
	public int getNumberOfImages() {
		return 2; // quaternion and its image on the oposite side of 4D sphere
	}

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
		//save(b1.center(), 1);
		//save(b2.center(), 2);

		CoordinateSystem system = computeCoordinateSystem(b1, b2);

		Pair<RigidBody> expressed = new Pair(b1.express(system), b2.express(system));
//		Pair<RigidBody> expressed2 = new Pair(b1.center().express(system), b2.center().express(system));
		//save(expressed._1.center().plusFiveX(), 3);
		//save(expressed._2.center().plusFiveX(), 4);
		//kill();
		float[] rotation = getRotation(expressed, imageNumber);
		float[] translation = getTranslation(expressed, imageNumber);
		//return rotation;
		return Util.merge(rotation, translation);
	}

	private CoordinateSystem computeCoordinateSystem(RigidBody b1, RigidBody b2) throws CoordinateSystemException {
		return createSystem(b1.getAllPoints());
		// TODO
		/*Pair<Point[]> superposed = getSuperposedPoints(b1, b2);
		Point[] averaged = average(superposed);
		return createSystem(averaged); // positioned essentially at b1		*/
	}

	private CoordinateSystem createSystem(Point[] points) throws CoordinateSystemException {
		Point origin = points[0];
		Point u = Point.vector(origin, points[1]);
		Point v = Point.vector(origin, points[2]);
		return new CoordinateSystem(origin, u, v);
	}

	private Versor alternate(Versor v, int imageNumber) {
		AxisAngle aa = v.toAxisAngle();
		if (aa.getAngle() > Math.PI) {
			//System.out.println("big " + v);
			v = v.negate();
		}
		//System.out.println("sma " + v);
		if (imageNumber == 1) {
			v = v.negate();
		}
		return v;
	}

	private Versor halfRotation(Versor v) {
		AxisAngle whole = v.toAxisAngle();
		assert whole.getAxis().size() < 1.0001 && whole.getAxis().size() > 0.9999;
		AxisAngle half = new AxisAngle(whole.getAxis(), whole.getAngle() / 2);
		return Versor.create(half);
	}

	// rotation exactly captured by one of two possible versors
	private float[] getRotation(Pair<RigidBody> pair, int imageNumber) {
		//assert imageNumber == 0 || imageNumber == 1;
		Superposer superposer = new Superposer(true);
		superposer.set(pair._2.center().getAllPoints(), pair._1.center().getAllPoints()); // 2 -> 1, does not matter now
		Versor versor = superposer.getVersor();
		versor = alternate(versor, imageNumber);
		return versor.toFloats();
	}

	private float[] getTranslation(Pair<RigidBody> pair, int imageNumber) {
		Point direction = pair._1.getCenter().minus(pair._2.getCenter()); // does not matter if _1 or _2
		Point unit = direction.normalize();
		float quotient = 1f;
		float[] translation = {
			(float) (direction.size()),
			(float) unit.x*quotient,
			(float) unit.y*quotient,
			(float) unit.z*quotient,};
		return translation;

	}

}
