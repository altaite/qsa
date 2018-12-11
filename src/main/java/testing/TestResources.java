package testing;

import cath.Cath;
import global.Parameters;
import global.io.Directories;
import java.io.File;
import structure.PdbEntries;
import structure.SimpleStructure;
import structure.StructureFactory;
import structure.StructureSource;

/**
 *
 * @author Antonin Pavelka
 */
public class TestResources {

	private final Directories directories;
	private final Parameters parameters;
	private Cath cath;
	private final PdbEntries entries;
	private int level = 0; // higher value = better, but slower tests (on all PDB entries etc.), 0 = standard routine unit testing

	private static TestResources instance;

	public static TestResources getInstance() {
		if (instance == null) {
			instance = new TestResources();
		}
		return instance;
	}

	private TestResources() {
		File file = new File("d:/t/data/qsa-test");
		if (!file.exists()) {
			file = new File("data");
			System.out.println("Warning: the home directory does not exist, using local " + file);
		}
		System.out.println("Using test directory " + file.getAbsolutePath());
		directories = new Directories(file);
		parameters = Parameters.create(directories.getParameters());
		entries = new PdbEntries(directories.getPdbEntryTypes());
	}

	public Directories getDirectoris() {
		return directories;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public Cath getCath() {
		if (cath == null) {
			cath = new Cath(getDirectoris());
		}
		return cath;
	}

	public PdbEntries getPdbEntries() {
		return entries;
	}

	public SimpleStructure create(StructureSource source) throws Exception {
		StructureFactory structureFactory
			= new StructureFactory(getDirectoris(), getCath());
		SimpleStructure structure = structureFactory.getStructure(0, source);
		return structure;
	}

	public boolean doFullTest() {
		return level > 0;
	}

}
