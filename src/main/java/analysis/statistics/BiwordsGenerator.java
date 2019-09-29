package analysis.statistics;

import algorithm.BiwordsFactory;
import api.CommandLineInterface;
import cath.Cath;
import global.Parameters;
import global.io.Directories;
import java.io.File;
import structure.SimpleStructure;
import structure.set.Structures;
import structure.set.StructuresId;

public class BiwordsGenerator {

	private File home;

	public BiwordsGenerator(File home) {
		this.home = home;
	}

	public void generate() {
		Directories dirs = new Directories(home);
		Parameters parameters = Parameters.create(dirs.getParameters());

		Cath cath = new Cath(dirs);

		System.out.println("Reading codes from " + dirs.getTestCodes());

		Structures structures = new Structures(parameters, dirs, cath, new StructuresId("query"));
		structures.getAdder().addFromIds(dirs.getTestCodes());

		for (SimpleStructure structure : structures) {
			BiwordsFactory biwordsFactory = new BiwordsFactory(parameters, dirs, structure, parameters.getSkipX(),
				true);
			biwordsFactory.getBiwords();
		}
	}

}
