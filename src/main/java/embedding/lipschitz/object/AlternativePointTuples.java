package embedding.lipschitz.object;

import altaite.geometry.primitives.PointTuple;

/**
 *
 * @author Antonin Pavelka
 */
public interface AlternativePointTuples {

	public PointTuple getCanonicalTuple();

	public PointTuple getAlternative(int index, AlternativeMode alternativeMode);

}
