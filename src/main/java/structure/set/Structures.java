package structure.set;

import global.FlexibleLogger;
import global.Parameters;
import global.io.Directories;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import cath.Cath;
import structure.SimpleStructure;
import structure.StructureFactory;
import structure.StructureParsingException;
import structure.StructureSizeFilter;
import structure.StructureSource;

/**
 * TODO probably split into sources collection and a factory. Remove iterator over structures.
 *
 * Stores references (PDB codes or files) to structures and provides corresponding SimpleStructure objects.
 *
 * @author Antonin Pavelka
 */
public class Structures implements Iterable<SimpleStructure> {
// TODO structure database, compute, store, manage ids
// separate from biwords?
// compute both at the same time? yes, speed
// utilize map or list, single file?

	private final Parameters parameters;
	private final Directories dirs;
	private final StructuresId id;
	private final StructureFactory factory;
	private final StructureSources structureSources;
	private int max = Integer.MAX_VALUE;
	private StructureSizeFilter filter;
	private int failed;
	private Adder adder;

	/**
	 * TODO prepare StructureSources before, just pass as argument, no adder here.
	 *
	 * TODO assign ids later, or have them externally? This is too opaque, iterator.
	 */
	public Structures(Parameters parameters, Directories dirs, Cath cath, StructuresId id) {
		this.structureSources = new StructureSources();
		this.parameters = parameters;
		this.dirs = dirs;
		this.id = id;
		this.factory = new StructureFactory(dirs, cath);
		this.adder = new Adder(dirs, structureSources);
	}

	public Structures(Parameters parameters, Directories dirs, Cath cath, StructuresId id,
		StructureSources structureSources) {
		this.structureSources = structureSources;
		this.parameters = parameters;
		this.dirs = dirs;
		this.id = id;
		this.factory = new StructureFactory(dirs, cath);
	}

	public Adder getAdder() {
		return adder;
	}

	public StructuresId getId() {
		assert id != null;
		return id;
	}

	public Collection<StructureSource> getSources() {
		return structureSources.getSources();
	}

	public void setFilter(StructureSizeFilter filter) {
		this.filter = filter;
	}

	public int size() {
		return Math.min(structureSources.size(), max);
	}

	@Deprecated
	public void setMaxSize(int max) {
		this.max = max;
	}

	private SimpleStructure get(int index, int structureId) throws IOException, StructureParsingException {
		StructureSource ref = structureSources.getSource(index);
		SimpleStructure ss = factory.getStructure(structureId, ref);
		return ss;
	}

	public SimpleStructure create(StructureSource source, int structureId)
		throws IOException, StructureParsingException {

		SimpleStructure structure = factory.getStructure(structureId, source);
		if (structure == null) {
			throw new StructureParsingException("Structure " + source + " is null.", false);
		}
		return structure;
	}

	public SimpleStructure getSingle() {
		if (structureSources.size() != 1) {
			throw new RuntimeException("Size must be 1, not " + structureSources.size());
		}
		SimpleStructure structure = iterator().next();
		return structure;
	}

	@Override
	@Deprecated
	public Iterator<SimpleStructure> iterator() {
		return new Iterator<SimpleStructure>() {

			int index = 0;
			int structureId = 0;

			@Override
			public boolean hasNext() {
				return index < size();
			}

			@Override
			public SimpleStructure next() {
				while (hasNext() && index < max) { // return first succesfully initialized structure 
					try {
						SimpleStructure structure = get(index++, structureId);
						if (structure != null
							&& (filter == null || filter.accept(structure))) {
							structureId++;
							return structure;
						}
					} catch (IOException | StructureParsingException ex) {
						failed++;
						FlexibleLogger.error(ex);
					}
				}
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Removals are not supported");
			}
		};
	}

	public int getFailed() {
		return failed;
	}
}
