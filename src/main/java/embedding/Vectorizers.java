package embedding;

import algorithm.Biword;
import algorithm.BiwordAlternativeMode;
import algorithm.BiwordedStructure;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import embedding.lipschitz.LipschitzEmbedding;
import fragment.serialization.BiwordLoader;
import fragment.serialization.KryoFactory;
import global.Parameters;
import global.io.Directories;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import probability.sampling.ReservoirSample;
import structure.set.StructuresId;

/**
 *
 * @author Antonin Pavelka
 */
public class Vectorizers {

	private Parameters parameters;
	private Directories dirs;
	private final KryoFactory kryoFactory = new KryoFactory();
	private Map<StructuresId, Vectorizer> vectorizers = new HashMap<>();

	public Vectorizers(Parameters parameters, Directories dirs) {
		this.parameters = parameters;
		this.dirs = dirs;
	}

	public Vectorizer get(StructuresId id, BiwordLoader biwordLoader) {
		File vectorizerFile = dirs.getVectorizer(id);
		Vectorizer vectorizer = vectorizers.get(id);
		if (vectorizer == null) {
			if (vectorizerFile.exists()) {
				vectorizer = load(vectorizerFile);
			} else {
				vectorizer = create(biwordLoader);
				save(vectorizerFile, vectorizer);
			}
		}
		return vectorizer;
	}

	private Vectorizer create(BiwordLoader biwordLoader) {
		ReservoirSample<Biword> sample = new ReservoirSample(parameters.getLipschitzFragmentSampleSize(), 1);
		for (BiwordedStructure biwordedStructure : biwordLoader) {
			Biword[] biwords = biwordedStructure.getBiwords();
			for (Biword biword : biwords) {
				sample.add(biword);
			}
		}
		BiwordAlternativeMode mode = new BiwordAlternativeMode(true, false); // !!! TODO parameters

		Biword[] sampleArray = new Biword[sample.size()];
		sample.getList().toArray(sampleArray);

		LipschitzEmbedding embedding = new LipschitzEmbedding(
			sampleArray,
			parameters.getNumberOfDimensions(),
			parameters.getLipschitzOptimizationCycles(),
			parameters.getLipschitzPairSampleSize(),
			mode);
		return embedding;
	}

	private void save(File f, Vectorizer vectorizer) {
		try (Output output = new Output(new FileOutputStream(f))) {
			Kryo kryo = kryoFactory.getKryoForBiwords();
			kryo.writeObject(output, vectorizer);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private LipschitzEmbedding load(File f) {
		try (Input input = new Input(new FileInputStream(f))) {
			return kryoFactory.getKryoForBiwords().readObject(input, LipschitzEmbedding.class);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
