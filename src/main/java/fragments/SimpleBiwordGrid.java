package fragments;

import grid.GridSearch;
import java.util.List;

public class SimpleBiwordGrid {

    private final GridSearch grid;
    private final double[] diffs = {3, 3, 1.2};
    //private final double[] diffs = {5, 5, 1.5};
    private final double[] sizes = {1, 1, 0.4};

    public SimpleBiwordGrid(List<Biword> biwords) {
        grid = new GridSearch(sizes, diffs);
        grid.buildGrid(biwords);
    }

    public List<Biword> search(Biword f) {
        return grid.nearest(f);
    }
}