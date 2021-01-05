package frame;

import algorithm.AtomToWord;
import altaite.geometry.primitives.Coordinates;
import altaite.geometry.search.GridRangeSearch;
import fragment.word.Word;
import java.util.ArrayList;
import java.util.List;
import structure.Residue;
import structure.SimpleStructure;

public class NeighborsFactory {

	private GridRangeSearch<Index> grid;
	private int[][] neighbors;

	// TODO remake for atoms->residue
	public NeighborsFactory(SimpleStructure s) {

		grid = new GridRangeSearch<>(5);
		grid.buildGrid(indexes);
	}
	
}
