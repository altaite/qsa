package language.database.map;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Provides unique path for each integer value so that each directory contains at most itemsPerNode files.
 *
 * @author Antonin Pavelka
 *
 */
public class RadixTree implements Serializable {

	private int size;
	private int itemsPerNode; // radix
	private Map<Integer, TreePath> paths;

	public RadixTree() {
		// for Kryo
	}

	public RadixTree(int itemsPerNode) {
		this.itemsPerNode = itemsPerNode;
		paths = new HashMap<>();
	}

	public TreePath addPath() {
		int leafId = size; // the value for which path is generated
		TreePath path = generatePath(leafId);
		paths.put(leafId, path);
		size++;
		return path;
	}

	public TreePath getPath(int leafId) {
		return paths.get(leafId);
	}

	private TreePath generatePath(int leafId) {
		int core = leafId;
		TreePath path = new TreePath(leafId);
		while (core > 0) {
			int remainder = core % itemsPerNode;
			path.add(remainder);
			core = (core - remainder) / itemsPerNode;
		}
		if (leafId == 0) {
			path.add(0);
		}
		return path;
	}

	public static void main(String[] args) {
		RadixTree tree = new RadixTree(10);
		for (int i = 0; i < 2000; i++) {
			TreePath a = tree.addPath();
			System.out.println(a.getLeafId() + " " + a.getPath(Paths.get("")));
		}
	}

}
