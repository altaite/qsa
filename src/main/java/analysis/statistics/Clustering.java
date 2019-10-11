package analysis.statistics;

import algorithm.Biword;
import algorithm.BiwordAlternativeMode;
import algorithm.BiwordedStructure;
import altaite.collection.buffer.BigIn;
import altaite.collection.buffer.BigResource;
import altaite.collection.buffer.map.MapIn;
import embedding.lipschitz.LipschitzEmbedding;
import embedding.lipschitz.object.AlternativePointTuples;
import geometry.superposition.Superposer;
import global.io.Directories;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import structure.StructureSource;

/**
 * POSSIBILITY to remove outliers and save a lot. Focus only on frequent patterns? At least when hit is found, if not,
 * do something crazy. PRINT distribution of clusters.
 *
 * rozprasit dvakrat, 10 000, kazdej na dalsich 10 000 podshluku, zjistit, jestli pouhe rmsd neni dost rychle a
 * granularita postacujicne presna jak teda ukladat vysledky aby se dalo pokracovat
 *
 *
 * figure out if there is some map of files, for dumping this, also biword pairs by pdb here needed for dumping cluster
 * content, buffer of biwords
 *
 */
public class Clustering {

	private int maxStructures = 100 * 10000000;
	private final int SAMPLE_SIZE = 1000000;
	private double threshold = 4;
	private Directories dirs;
	private List<Biword> centers = new ArrayList<Biword>();
	private List<List<Long>> contents = new ArrayList<>(); // content, ids, without center
	private Superposer superposer = new Superposer();

	private int dimensions = 20;
	private int optimizationCycles = 1000;
	private int pairSampleSize = 10000;

	public Clustering(Directories dirs) {
		this.dirs = dirs;
	}

	/**
	 * new kryo?
	 *
	 * first, deal with kryo stack overflows, wtf is that logg the problems better, remove those structures if
	 * unresolvable?
	 *
	 * is this wiser than multiple division, maybe try that first to get some ideas about distibutions
	 *
	 * !!!! Lipschitz vector of big size could work too, can profile how many are beneficial after they are computed
	 *
	 *
	 * MyHashing: million features scale each experimentally on subsamples start with centroid how to retrieve same
	 * hash? scratch it, do rational way lower
	 *
	 * TODO create heuristics to recognize very similar ones, <0.5 probably not so difficult what kind of tree structure
	 * or LSH rethink grid tree, but stopping meaningfully?
	 *
	 * propose various combinations and equations of internal coordinates and base distances pick whatever works best at
	 * given level, aka paper proposal?
	 *
	 * will be efficient, because subsampling
	 *
	 * not grid tree, but simpler split, see the paper
	 */
	public void run(Path dir) {
		MapIn<StructureSource, BiwordedStructure> in = new MapIn<>(dir);
		System.out.println(in.size() + " structures");
		int i = 0;
		long id = 0;
		for (StructureSource ss : in.keySet()) {
			try {
				BiwordedStructure bs = in.get(ss);
				Biword[] biwords = bs.getBiwords();
				for (Biword bw : biwords) {
					//assign(bw, id);   /// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					id++;
				}
				i++;
				System.out.println("structure: " + i + ", biwords: " + biwords.length + ","
					+ " clusters: " + centers.size() + ", biwords " + id);
				if (i > maxStructures) {
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		in.close();
		System.out.println("BIWORDS:  " + id);
		System.out.println("CLUSTERS: " + centers.size());
	}

	private void assign(Biword bw, long id) {
		Integer cluster = findClosestSatisfactory(bw);
		if (cluster == null) {
			centers.add(bw);
			List<Long> content = new ArrayList<>();
			contents.add(content);
			//if (centers.size() % 1000 == 0) {
			//	System.out.println("New center, clusters: " + centers.size());
			//}
		} else {
			List<Long> content = contents.get(cluster);
			content.add(id);
			//System.out.println("Growing " + (content.size() + 1) + ", clusters: " + centers.size());
		}
	}

	private Integer findClosestSatisfactory(Biword query) {
		Integer best = null;
		double bestRmsd = Double.MAX_VALUE;
		for (int i = 0; i < centers.size(); i++) {
			Biword center = centers.get(i);
			double rmsd = rmsd(query, center);
			if (rmsd <= threshold && rmsd < bestRmsd) {
				best = i;
				bestRmsd = rmsd;
			}
		}
		return best;
	}

	private double rmsd(Biword a, Biword b) {
		superposer.set(a.getPoints3d(), b.getPoints3d());
		return superposer.getRmsd();
	}

	private AlternativePointTuples[] sample(long totalBiwords) {
		SortedSet<Integer> indexes = new TreeSet<>();
		if (totalBiwords >= Integer.MAX_VALUE) {
			throw new RuntimeException();
		}
		while (indexes.size() < SAMPLE_SIZE) {
			long r = ThreadLocalRandom.current().nextLong(totalBiwords);
			indexes.add((int) r);
		}

		AlternativePointTuples[] sample = new AlternativePointTuples[SAMPLE_SIZE];

		BigResource resource = new BigResource(dirs.getTestBiwords());
		BigIn<Biword> in = new BigIn(resource);
		int i = 0;
		for (int index : indexes) {
			Biword bw = in.get(index);
			sample[i++] = bw;
		}
		in.close();

		return sample;
	}

	private LipschitzEmbedding buildEmbedding(AlternativePointTuples[] sample) {
		LipschitzEmbedding embedding;
		embedding = new LipschitzEmbedding(sample, dimensions, optimizationCycles, pairSampleSize,
			new BiwordAlternativeMode(true, true));
		return embedding;
	}
	
	private void vectorize() {
		
	}
	
}
