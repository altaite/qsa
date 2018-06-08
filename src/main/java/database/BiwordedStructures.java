package database;

import algorithm.BiwordedStructure;
import java.io.File;
import java.util.Iterator;
import language.database.map.SerializedMap;
import structure.StructureSource;
import structure.set.Structures;

/**
 *
 * @author Antonin Pavelka
 */
public class BiwordedStructures implements Iterable<BiwordedStructure> {

	private SerializedMap<StructureSource, BiwordedStructure> map;

	public BiwordedStructures(Structures structures, File dir, int itemsPerDirectory) {
		map = new SerializedMap<>(dir, itemsPerDirectory, BiwordedStructure.class);
		create(structures);
	}

	public BiwordedStructure get(StructureSource source) {
		return map.getByKey(source);
	}

	public void add(BiwordedStructure structure) {
		map.put(structure.getStructure().getSource(), structure);
	}

	@Override
	public Iterator<BiwordedStructure> iterator() {
		return map.iterator();
	}

	private void create(Structures structures) {
		for (StructureSource source : structures.getSources()) {
			
			
		}
	}
}
