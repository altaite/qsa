package analysis.statistics;

import analysis.statistics.features.FeatureAnalyzer;
import algorithm.Biword;
import algorithm.BiwordedStructure;
import algorithm.BiwordsFactory;
import altaite.collection.buffer.BigOut;
import altaite.collection.buffer.BigResource;
import altaite.collection.buffer.map.MapIn;
import altaite.collection.buffer.map.MapOut;
import analysis.statistics.bag.BiwordBagOut;
import cath.Cath;
import global.Parameters;
import global.io.Directories;
import global.io.LineFile;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.System.out;
import java.nio.file.Path;
import structure.SimpleStructure;
import structure.StructureSource;
import structure.set.Adder;
import structure.set.StructureSources;
import structure.set.StructureSourcesOperations;
import structure.set.Structures;
import structure.set.StructuresId;

public class BiwordsAnalyzer {

	private Directories dirs;
	private Parameters parameters;
	private Path biwordsDir;

	public BiwordsAnalyzer(File home) {
		dirs = new Directories(home);
		biwordsDir = dirs.getBiwordsForAnalysisDir();
		parameters = Parameters.create(dirs.getParameters());
	}

	public void run() {
		//createBiwordBag();
		//createBiwordedStructures(); // is it download or parsing?
		//extractBiwordsAndSample();
		//cluster();
		analyzeFeatures();
	}

	private void createBiwordBag() {
		Cath cath = new Cath(dirs);

		System.out.println("Reading codes from " + dirs.getTestCodes());

		/*StructureSources all = getAllStructureSources();
		StructureSources done = getDoneStructureSources();
		System.out.println("All: " + all.size());
		System.out.println("Done: " + done.size());
		//StructureSources todo = StructureSourcesOperations.minus(all, done);
		StructureSources todo = all; // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		System.out.println("Todo: " + todo.size());*/
		StructureSources all = getAllStructureSources();
		StructureSources todo = StructureSourcesOperations.subsample(all, 1000);

		// TODO make the initial number correct, one higher than last structure
		Structures structures = new Structures(parameters, dirs, cath, new StructuresId("pdb_full"), todo);

		BiwordBagOut bag = new BiwordBagOut(dirs.getTestBiwordBag());

		int id = 0;
		for (StructureSource source : structures.getSources()) {
			try {
				if (dirs.getTestStopFile().length() > 0) {
					break;
				}
				System.out.println((id++) + " working on " + source + " ...");
				SimpleStructure structure = structures.create(source, id++);
				BiwordsFactory biwordsFactory = new BiwordsFactory(parameters, dirs, structure, parameters.getSkipX(),
					true);
				BiwordedStructure s = biwordsFactory.getBiwords();
				System.out.println("saving ...");
				for (Biword bw : s.getBiwords()) {
					bag.add(bw);
					id++;
				}
				saveDone(source);
				System.out.println("... " + structure.getSource() + "  done. " + id);

				//if (id > 30000000) { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				//	break;
				//}
			} catch (Exception ex) {
				ex.printStackTrace();
				saveDone(source);
			} catch (AssertionError ex) {
				ex.printStackTrace();
				saveDone(source);
			}
		}
		bag.close();
	}

	private void createBiwordedStructures() {
		Cath cath = new Cath(dirs);

		System.out.println("Reading codes from " + dirs.getTestCodes());

		StructureSources all = getAllStructureSources();
		StructureSources done = getDoneStructureSources();
		System.out.println("All: " + all.size());
		System.out.println("Done: " + done.size());
		StructureSources todo = StructureSourcesOperations.minus(all, done);
		System.out.println("Todo: " + todo.size());

		// TODO make the initial number correct, one higher than last structure
		Structures structures = new Structures(parameters, dirs, cath, new StructuresId("pdb_full"), todo);

		MapOut<StructureSource, BiwordedStructure> out = new MapOut<>(biwordsDir, true);

		int id = 0;
		for (StructureSource source : structures.getSources()) {
			try {
				if (dirs.getTestStopFile().length() > 0) {
					break;
				}
				System.out.println((id++) + " working on " + source + " ...");
				SimpleStructure structure = structures.create(source, id++);
				BiwordsFactory biwordsFactory = new BiwordsFactory(parameters, dirs, structure, parameters.getSkipX(),
					true);
				BiwordedStructure s = biwordsFactory.getBiwords();
				System.out.println("saving ...");
				saveMap(s, out);
				saveDone(source);
				System.out.println("... " + structure.getSource() + "  done.");
			} catch (Exception ex) {
				ex.printStackTrace();
				saveDone(source);
			} catch (AssertionError ex) {
				ex.printStackTrace();
				saveDone(source);
			}
		}
		out.close();
	}

	private void saveDone(StructureSource source) {
		LineFile f = new LineFile(dirs.getTestCodesDone());
		f.writeLine(source.toString());
	}

	private StructureSources getAllStructureSources() {
		StructureSources all = new StructureSources();
		Adder adder = new Adder(dirs, all);
		adder.addFromIds(dirs.getTestCodes());
		return all;
	}

	private StructureSources getDoneStructureSources() {
		StructureSources done = new StructureSources();
		if (dirs.getTestCodesDone().exists()) {
			Adder adder = new Adder(dirs, done);
			adder.addFromIds(dirs.getTestCodesDone());
		}
		return done;
	}

	private void saveMap(BiwordedStructure s, MapOut<StructureSource, BiwordedStructure> out) {
		out.put(s.getStructure().getSource(), s);
	}

	private void cluster() {
		Clustering clustering = new Clustering(dirs);
		clustering.run(biwordsDir);
	}
	
	private void analyzeFeatures() {
		FeatureAnalyzer analyzer = new FeatureAnalyzer(dirs);
		analyzer.run();
	}

	// TODO do it above, this is slow? and save it in primitives
	private void extractBiwordsAndSample() {
		BigResource resource = new BigResource(dirs.getTestBiwords());
		BigOut<Biword> out = new BigOut(resource);

		MapIn<StructureSource, BiwordedStructure> in = new MapIn<>(biwordsDir);
		System.out.println(in.size() + " structures");
		long totalBiwords = 0;
		int structureIndex = 0;
		for (StructureSource ss : in.keySet()) {
			try {
				BiwordedStructure bs = in.get(ss);
				Biword[] biwords = bs.getBiwords();
				for (Biword bw : biwords) {
					//out.add(bw);
					totalBiwords++;
				}
				System.out.println((structureIndex++) + ", biwords: " + biwords.length + ","
					+ ", biwords " + totalBiwords);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		out.close();
		in.close();
		System.out.println("BIWORDS:  " + totalBiwords);

		File numberFile = dirs.getTestBiwordNumber();
		try (FileWriter ft = new FileWriter(numberFile)) {
			ft.write(totalBiwords + "");
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
