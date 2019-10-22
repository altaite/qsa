package analysis.statistics.tree;

import analysis.statistics.Io;
import global.Parameters;
import global.io.Directories;
import java.io.File;
import java.nio.file.Path;

/**
 *
 */
public class RangeTreeExperiment {

	private final Directories dirs;
	private final Parameters parameters;
	private final Path biwordsDir;
	private final Io io;

	private final boolean INITIALIZE = true;

	public RangeTreeExperiment(File home) {
		dirs = new Directories(home);
		io = new Io(dirs);
		biwordsDir = dirs.getBiwordsForAnalysisDir();
		parameters = Parameters.create(dirs.getParameters());

	}

	public void run() {
		if (INITIALIZE) {
			computeFeatures();
		} else {
			//findNeighbors();
		}
	}

	private void computeFeatures() {

	}

	/*private void findNeighbors() {
		Random random = new Random(1);
		Features features = new Features(dirs);
		features.getSubset(random);
	}*/
}
