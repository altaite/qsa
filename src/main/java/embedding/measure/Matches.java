package embedding.measure;

import java.util.HashSet;
import java.util.Set;

public class Matches {

	private Set<Match> matches = new HashSet<>();

	public void add(Match match) {
		matches.add(match);
	}

	public int size() {
		return matches.size();
	}
}
