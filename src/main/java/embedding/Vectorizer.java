package embedding;

import altaite.geometry.primitives.PointTuple;
import vectorization.dimension.Dimensions;

/**
 *
 * @author Antonin Pavelka
 */
public interface Vectorizer {

	public float[] getCoordinates(PointTuple object);

	public Dimensions getDimensions();

}
