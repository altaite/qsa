package metric;

import altaite.geometry.primitives.Point;

/**
 *
 * @author Antonin Pavelka
 */
public interface MetricDistance {

	public double distance(Point x, Point y);
}
