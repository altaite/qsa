package structure.set;

import global.io.Directories;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import structure.StructureSource;

/**
 *
 * @author Antonin Pavelka
 */
public class Adder {

	private Directories dirs;
	private StructureSources structureSources;
	private final Random random = new Random(1);

	public Adder(Directories dirs, StructureSources sources) {
		this.dirs = dirs;
		this.structureSources = sources;
	}

	public void addFromDir(File dir) throws IOException {
		for (File f : dir.listFiles()) {
			structureSources.add(new StructureSource(f));
		}
	}

	public void addFromFile(File f) {
		structureSources.add(new StructureSource(f));
	}

	public void addFromClusters() {
		try (BufferedReader br = new BufferedReader(new FileReader(dirs.getPdbClusters30()))) {
			String line;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, " ");
				List<String> cluster = new ArrayList<>();
				while (st.hasMoreTokens()) {
					String id = st.nextToken().replace("_", "");
					if (id.length() == 5) {
						cluster.add(id);
					}
				}
				if (!cluster.isEmpty()) {
					structureSources.add(new StructureSource(cluster.get(random.nextInt(cluster.size()))));
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Process text file with ids supported by StructureSource. TODO remove duplicity with Entries
	 */
	public void addFromIds(File file) {
		String line = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				line = line.trim();
				StringTokenizer st = new StringTokenizer(line, " \t");
				if (line.startsWith("#")) {
					continue;
				}
				String code = st.nextToken();
				StructureSource source = new StructureSource(code);
				if (file.getName().equals("pdb_entry_type.txt") && st.hasMoreTokens()) {
					String type = st.nextToken();
					if (type.equals("prot")) {
						structureSources.add(source);
					}
				} else {
					structureSources.add(source);
				}
			}
		} catch (Exception ex) {
			System.err.println(line);
			throw new RuntimeException(ex);
		}
	}

	public void addAll(Collection<StructureSource> sources) {
		this.structureSources.addAll(sources);
	}

	public void addFromPdbCode(String pdbCode) {
		structureSources.add(new StructureSource(pdbCode));
	}

	public void add(StructureSource r) {
		structureSources.add(r);
	}

}
