package analysis.statistics.features;

import java.util.ArrayList;
import java.util.List;

public class ClosePairs {

	private List<Pair> pairs = new ArrayList<>();

	public void add(int a, int b, float rmsd) {
		assert a < b;
		assert rmsd >= 0 && rmsd < 100;
		pairs.add(new Pair(a, b, rmsd));
	}

	public List<Pair> getList() {
		return pairs;
	}
}
