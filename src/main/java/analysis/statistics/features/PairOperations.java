package analysis.statistics.features;

import java.util.HashSet;
import java.util.Set;

/**
 * Operations over a specific set of close pairs.
 */
public class PairOperations {

	private Set<Pair> set = new HashSet<>();

	public PairOperations(double threshold) {
		// read from ClosePairs
	}

	/*public boolean areClose() {

	}*/

	class Pair {

		int a, b;
	}
}
