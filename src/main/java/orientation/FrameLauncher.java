package orientation;

import altaite.collection.performance.Timer;
import cath.Cath;
import analysis.biword.search.Io;
import global.Parameters;
import global.io.Directories;
import global.io.LineFile;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import structure.ChainId;
import structure.Residue;
import structure.ResidueId;
import structure.SimpleStructure;
import structure.StructureFactory;
import structure.StructureSource;
import structure.set.Adder;
import structure.set.StructureSources;
import structure.set.StructureSourcesOperations;
import structure.set.Structures;
import structure.set.StructuresId;
import util.Time;

public class FrameLauncher {

	private final Directories dirs;
	private final Parameters parameters;
	private final Path frameDir;
	private final Io io;
	private final Cath cath;

	private final boolean FEATURES = false;
	private final boolean OPTIMIZE = true;

	public FrameLauncher(File home) {
		dirs = new Directories(home);
		io = new Io(dirs);
		frameDir = dirs.getFrameDir();
		parameters = Parameters.create(dirs.getParameters());
		cath = new Cath(dirs);
	}

	public void run() {
		createBiwordBag();
	}

	private void createBiwordBag() {

		System.out.println("Reading codes from " + dirs.getTestCodes());

		StructureSources all = getAllStructureSources();
		StructureSources todo = StructureSourcesOperations.subsample(all, 40);

		StandardResidueOrientation sro = createStandardResidueOrientation();

		Structures structures = new Structures(parameters, dirs, cath, new StructuresId("pdb_full"), todo);
		int id = 0;

		List<SmallStructure> simps = new ArrayList<>();
		for (StructureSource source : structures.getSources()) {
			try {
				if (dirs.getTestStopFile().length() > 0) {
					break;
				}
				SimpleStructure structure = structures.create(source, id++);
				SmallStructure ss = new SmallStructure(structure, sro);
				simps.add(ss);
			} catch (Exception ex) {
				ex.printStackTrace();
				saveDone(source);
			} catch (AssertionError ex) {
				ex.printStackTrace();
				saveDone(source);
			}
		}

		Time.start("searches");
		int count = 0;
		for (SmallStructure ss : simps) {
			Walk walk = new Walk(ss, ss);
			walk.align();
			count++;
		}
		System.out.println("Searches: " + count);
		Time.stop("searches");
		Time.print();
		double ms = (double) Time.get("searches").getMiliseconds();
		System.out.println("Per search " + (ms / count));
		/*Random random = new Random(1);
		Time.start("searches");
		int count = 0;
		for (int x = 0; x < frames.size(); x++) {
			Frames xf = frames.get(x);
			for (int y = 0; y <= x; y++) {
				Frames yf = frames.get(x);
				int r = random.nextInt(yf.size());
				Frame q = yf.get(r);
				xf.search(q);
				count++;
			}
		}
		System.out.println("Searches: " + count);
		Time.stop("searches");
		Time.print();
		double ms = (double) Time.get("searches").getMiliseconds();
		System.out.println("Per search " + (ms / count));*/
	}

	private StandardResidueOrientation createStandardResidueOrientation() {
		StructureFactory f = new StructureFactory(dirs, cath);
		try {
			SimpleStructure ss = f.getStructure(0, new StructureSource("1iz7"));
			Residue r = ss.getResidue(ResidueId.createWithoutInsertion(new ChainId("A"), 50));
			StandardResidueOrientation sro = new StandardResidueOrientation(r);
			return sro;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
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

	public static void main(String[] args) {
		FrameLauncher m = new FrameLauncher(new File("d:/t/data/qsa/experiments/frames"));
		m.run();
	}

}
