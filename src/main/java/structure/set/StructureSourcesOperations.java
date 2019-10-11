package structure.set;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import structure.StructureSource;

public class StructureSourcesOperations {

	public static StructureSources minus(StructureSources a, StructureSources b) {
		StructureSources c = new StructureSources();
		Set<StructureSource> as = new HashSet<StructureSource>(a.getSources());
		Set<StructureSource> bs = new HashSet<StructureSource>(b.getSources());
		as.removeAll(bs);
		for (StructureSource ss : as) {
			c.add(ss);
		}
		return c;
	}

	public static StructureSources subsample(StructureSources all, int sampleSize) {
		StructureSources sample = new StructureSources();
		List<StructureSource> list = new ArrayList(all.getSources());
		Random random = new Random(1);
		Set<Integer> indexes = new HashSet<>();
		while (indexes.size() < sampleSize) {
			int r = random.nextInt(list.size());
			indexes.add(r);
		}
		for (int i : indexes) {
			sample.add(list.get(i));
		}
		return sample;
	}

}
