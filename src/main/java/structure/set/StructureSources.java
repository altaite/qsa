package structure.set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import structure.StructureSource;

/**
 *
 * @author Antonin Pavelka
 */
public class StructureSources {

	private int nextIndex = 0;
	private BiMap<Integer, StructureSource> bimap = HashBiMap.create();

	public void add(StructureSource source) {
		if (nextIndex >= Integer.MAX_VALUE) {
			throw new RuntimeException("Cannot store more than " + nextIndex);
		}
		bimap.put(nextIndex, source);
		nextIndex++;
	}

	public void addAll(Iterable<StructureSource> sources) {
		for (StructureSource source : sources) {
			add(source);
		}
	}

	public int size() {
		return bimap.size();
	}

	public StructureSource getSource(int index) {
		return bimap.get(index);
	}

	public int getIndex(StructureSource source) {
		return bimap.inverse().get(source);
	}

	public Collection<StructureSource> getSources() {
		return bimap.values();
	}

}
