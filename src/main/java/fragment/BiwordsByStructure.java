package fragment;

import algorithm.BiwordedStructure;
import algorithm.BiwordsFactory;
import global.Parameters;
import global.io.Directories;
import language.database.map.SerializedMap;
import structure.SimpleStructure;
import structure.StructureSource;
import structure.set.Structures;

public class BiwordsByStructure {

	private Directories dirs;
	private Parameters parameters;
	private Structures structures;
	private SerializedMap<StructureSource, BiwordedStructure> map;

	public BiwordsByStructure(Structures structures, Parameters parameters, Directories dirs) {
		this.dirs = dirs;
		this.parameters = parameters;
		this.structures = structures;
		compute();
	}

	private void compute() {
		for (SimpleStructure structure : structures) {
			BiwordsFactory biwordsFactory = new BiwordsFactory(parameters, dirs, structure, parameters.getSkipY(), false);
			BiwordedStructure biworded = biwordsFactory.getBiwords();
		}
	}

}
