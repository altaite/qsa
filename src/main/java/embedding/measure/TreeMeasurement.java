package embedding.measure;

import algorithm.Biword;
import algorithm.BiwordedStructure;
import cath.Cath;
import embedding.Vectorizer;
import embedding.Vectorizers;
import fragment.biword.BiwordId;
import fragment.biword.BiwordPairFiles;
import fragment.biword.BiwordPairReader;
import fragment.biword.BiwordPairWriter;
import fragment.index.Grids;
import fragment.index.Grid;
import fragment.serialization.BiwordLoader;
import geometry.superposition.Superposer;
import global.Parameters;
import global.io.Directories;
import grid.sparse.BufferOfLong;
import java.io.File;
import java.util.List;
import java.util.Random;
import metric.Chebyshev;
import structure.StructureSource;
import structure.set.Structures;
import structure.set.StructuresId;
import structure.VectorizationException;
import util.Timer;

/**
 *
 * @author Antonin Pavelka
 */
public class TreeMeasurement {

	//private final TestResources resources;
	private final Directories dirs;
	private final Parameters parameters;
	Cath cath;
	private final Random random;
	private final Structures structures;
	private final BiwordLoader biwordLoader;
	private final Grid index;

	public TreeMeasurement() {
		random = new Random(1);
		dirs = new Directories(new File("d:/t/data/qsa"));
		parameters = Parameters.create(dirs.getParameters());
		cath = new Cath(dirs);
		dirs.createJob();
		structures = createStructures();
		Grids indexes = new Grids(parameters, dirs);

		//index = null;
		index = indexes.getGrid(structures); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		biwordLoader = new BiwordLoader(parameters, dirs, structures.getId());
		System.out.println("------------------");

		// TODO no dirs, initialize?
	}

	private void run() throws VectorizationException {
		int query = createRandomQuery();
		BiwordedStructure queryBiwords = biwordLoader.load(query);
		Matches a, b, c;
		a = efficient(queryBiwords);
		b = allQcp(queryBiwords);
		//c = vectors(queryBiwords);
		System.out.println("tree       " + a.size());
		System.out.println("exhaustive " + b.size());
		//System.out.println("vectors    " + c.size());
	}

	private Matches efficient(BiwordedStructure queryBiwords) throws VectorizationException {
		System.out.println("--- EFFICIENT ---");
		Matches matches = new Matches();
		dirs.createTask("tree_test_");
		BufferOfLong buffer = new BufferOfLong();
		System.out.println("total: " + index.size());
		Biword[] biwords = queryBiwords.getBiwords();
		Timer.start();
		BiwordPairWriter writer = new BiwordPairWriter(dirs, structures.size());
		long sum = 0;
		System.out.println("query size: " + biwords.length);
		Biword[] queries = sample(biwords);
		for (Biword x : queries) {
			buffer.clear();
			index.query(x, buffer);
			sum += buffer.size();
			for (int i = 0; i < buffer.size(); i++) {
				long encoded = buffer.get(i);
				BiwordId y = BiwordId.decode(encoded);
				writer.write(x.getIdWithingStructure(), y.getStructureId(), y.getIdWithinStructure());
			}
		}
		writer.close();

		//check identities if they are returned? maybe even content and vectors
		Timer.stop();
		System.out.println("total tree query size " + sum);
		System.out.println("linear comparison size: " + (queries.length * index.size()));
		System.out.println("time1: " + Timer.get() + " for " + biwords.length);
		System.out.println("average size " + (sum / biwords.length));

		Timer.start();
		Superposer superposer = new Superposer();
		long tp = 0;
		long qcp = 0;
		BiwordPairFiles biwordPairFiles = new BiwordPairFiles(dirs);
		for (BiwordPairReader reader : biwordPairFiles.getReaders()) {
			int targetStructureId = reader.getTargetStructureId();
			BiwordedStructure targetBiwords = biwordLoader.load(targetStructureId);
			while (reader.readNextBiwordPair()) {
				int queryBiwordId = reader.getQueryBiwordId();
				int targetBiwordId = reader.getTargetBiwordId();
				Biword x = queryBiwords.get(queryBiwordId);
				Biword y = targetBiwords.get(targetBiwordId);
				superposer.set(x.getPoints3d(), y.getPoints3d());
				double rmsd = superposer.getRmsd();
				qcp++;
				if (rmsd <= parameters.getMaxFragmentRmsd()) {
					//matches.add(new Match(x, y));
					tp++;
				}
			}
		}
		System.out.println("QCP comparisons: " + qcp);
		System.out.println("efficient  hit " + tp);
		Timer.stop();
		System.out.println("tp + fp = " + sum);
		System.out.println("tp      = " + tp);
		System.out.println("time2 " + Timer.get());
		System.out.println();
		return matches;
	}

	private Matches allQcp(BiwordedStructure queryBiwords) {
		System.out.println("--- QCP ---");
		Matches matches = new Matches();
		dirs.createTask("linear_");
		Biword[] biwords = queryBiwords.getBiwords();
		Superposer superposer = new Superposer();
		int hit = 0;
		int total = 0;
		Timer.start();
		for (BiwordedStructure bs : biwordLoader) {
			for (Biword x : sample(biwords)) {
				for (Biword y : bs.getBiwords()) {
					superposer.set(x.getPoints3d(), y.getPoints3d());
					double rmsd = superposer.getRmsd();
					if (rmsd < parameters.getMaxFragmentRmsd()) {
						//matches.add(new Match(x, y));
						hit++;
					}
					total++;
				}
			}
		}
		System.out.println("all hit " + hit);
		Timer.stop();
		System.out.println("hit = " + hit);
		System.out.println("total = " + total);
		System.out.println("time = " + Timer.get());
		System.out.println();
		return matches;
	}

	private Matches vectors(BiwordedStructure queryBiwords) {
		System.out.println("--- VECTORS ---");
		Matches matches = new Matches();

		Vectorizers vectorizers = new Vectorizers(parameters, dirs);
		Vectorizer vectorizer = vectorizers.get(structures.getId(), biwordLoader);

		dirs.createTask("linear_");
		Biword[] biwords = queryBiwords.getBiwords();
		Superposer superposer = new Superposer();
		int hit = 0;
		int total = 0;
		int vectorHit = 0;
		/*
		for (Biword x : sample(biwords)) {
		}
		for (Biword y : bs.getBiwords()) {
		}
		 */ Timer.start();
		for (BiwordedStructure bs : biwordLoader) {
			for (Biword x : sample(biwords)) {
				for (Biword y : bs.getBiwords()) {

					float[] vx = vectorizer.getCoordinates(x.getCanonicalTuple());
					float[] vy = vectorizer.getCoordinates(y.getCanonicalTuple());
					double d = Chebyshev.distance(vx, vy);
					if (d <= parameters.getMaxFragmentRmsd()) {
						superposer.set(x.getPoints3d(), y.getPoints3d());
						double rmsd = superposer.getRmsd();
						vectorHit++;
						if (rmsd < parameters.getMaxFragmentRmsd()) {
							matches.add(new Match(x, y));
							hit++;
						}
					}
					total++;
				}
			}
		}
		System.out.println("all hit " + hit);
		Timer.stop();
		System.out.println("vectorHit = " + vectorHit);
		System.out.println("hit = " + hit);
		System.out.println("total = " + total);
		System.out.println("time = " + Timer.get());
		System.out.println();
		return matches;
	}

	private Structures createStructures() {
		Structures structures = new Structures(
			parameters, dirs, cath, new StructuresId("custom_search1"));
		//structure.setFilter(new StructureSizeFilter(parameters.getMinResidues(), parameters.getMaxResidues()));
		List<StructureSource> list = cath.getHomologousSuperfamilies().getRepresentantSources();
		System.out.println("total structures before sampling " + list.size());
		// sample(list, 1000);
		System.out.println("total structures after sampling " + list.size());
		structures.getAdder().addAll(list);
		return structures;
	}

	private void sample(List<StructureSource> list, int n) {
		while (list.size() > n) {
			int r = random.nextInt(list.size());
			list.remove(r);
		}
	}

	/*
	TODO decrease memory to check we are not on the edge
	consider inmem saving - 2 * ~10 = 20 GB, but only if it will be overall bottleneck
	hierarchy of proteins used in Lipschitz
	*/
	
	private int createRandomQuery() {
		random.nextInt(structures.size());
		random.nextInt(structures.size());
		return random.nextInt(structures.size());
	}

	private Biword[] sample(Biword[] biwords) {
		Biword[] sample = new Biword[1];
		sample[0] = biwords[10];
		return biwords; // !!!
		//return sample;
	}

	public static void main(String[] args) throws Exception {
		TreeMeasurement m = new TreeMeasurement();
		m.run();
	}
}
