package embedding.measure;

import algorithm.Biword;

public class Match {

	private int structure1;
	private int biword1;
	private int structure2;
	private int biword2;

	public Match(int structure1, int biword1, int structure2, int biword2) {
		this.structure1 = structure1;
		this.biword1 = biword1;
		this.structure2 = structure2;
		this.biword2 = biword2;
	}

	public Match(Biword a, Biword b) {
		this(a.getStructureId(), a.getIdWithingStructure(),
			b.getStructureId(), b.getIdWithingStructure());
	}

	@Override
	public boolean equals(Object o) {
		Match other = (Match) o;
		return structure1 == other.structure1
			&& biword1 == other.biword1
			&& structure2 == other.structure2
			&& biword2 == other.biword2;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + this.structure1;
		hash = 67 * hash + this.biword1;
		hash = 67 * hash + this.structure2;
		hash = 67 * hash + this.biword2;
		return hash;
	}
}
