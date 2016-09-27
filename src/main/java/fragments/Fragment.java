package fragments;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.biojava.nbio.structure.SVDSuperimposer;
import org.biojava.nbio.structure.StructureException;

import geometry.Point;
import geometry.PointConversion;
import geometry.Transformation;
import spark.clustering.Clusterable;

/**
 *
 * @author Antonin Pavelka
 */
public class Fragment implements Clusterable<Fragment> {

	private static final long serialVersionUID = 1L;
	private Word a_, b_;
	private double[] features_;

	public Fragment(Word a, Word b) {
		a_ = a;
		b_ = b;
		computeFeatures(a, b);
		// System.out.println(features_.length);
	}

	public Fragment switchWords() {
		return new Fragment(b_, a_);
	}

	public Point getCenter() {
		return a_.getCenter().plus(b_.getCenter()).divide(2);
	}

	public double distance(Fragment other) {
		double sum = 0;
		for (int i = 0; i < features_.length; i++) {
			sum += Math.abs(features_[i] - other.features_[i]);
		}
		return sum / features_.length;
	}

	private void computeFeatures(Word a, Word b) {
		List<Double> features = new ArrayList<>();
		Point[] aps = a.getPoints();
		Point[] bps = b.getPoints();
		for (int x = 0; x < aps.length; x++) {
			for (int y = 0; y < x; y++) {
				double d = aps[x].distance(bps[y]);
				features.add(d);
			}
		}
		features_ = new double[features.size()];
		for (int i = 0; i < features_.length; i++) {
			features_[i] = features.get(i);
		}
	}

	public Point[] getPoints() {
		Point[] aps = a_.getPoints();
		Point[] bps = b_.getPoints();
		Point[] ps = new Point[aps.length + bps.length];
		System.arraycopy(aps, 0, ps, 0, aps.length);
		System.arraycopy(bps, 0, ps, aps.length, bps.length);
		return ps;
	}

	public Transformation superpose(Fragment other) {
		Point3d[] ap = PointConversion.getPoints3d(getPoints());
		Point3d[] bp = PointConversion.getPoints3d(other.getPoints());
		// Matrix4d m = SuperPosition.superposeWithTranslation(ap, bp);
		// Transformation t = new Transformation(m);
		try {
			SVDSuperimposer svd = new SVDSuperimposer(ap, bp);
			return new Transformation(svd);
		} catch (StructureException e) {
			throw new RuntimeException(e);
		}
	}

}
