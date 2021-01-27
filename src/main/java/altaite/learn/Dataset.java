package altaite.learn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Dataset {

	private List<MyInstance> instances = new ArrayList<>();

	private final String nl = "\n";

	public void add(MyInstance instance) {
		if (!instances.isEmpty()) {
			if (!instances.get(instances.size() - 1).isCompatible(instance)) {
				throw new RuntimeException("Instances not compatible: " + instances.size());
			}
		}
		instances.add(instance);
	}

	public void toArff(String pathString) {
		toArff(new File(pathString));
	}

	public void toArff(File f) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			List<Set<Integer>> types = getTypes();
			writeHeader(f.getName(), types, bw);
			writeData(types, bw);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void writeHeader(String name, List<Set<Integer>> types, BufferedWriter bw) throws IOException {
		bw.write("@RELATION " + name);
		bw.write(nl);
		for (int i = 0; i < types.size(); i++) {
			bw.write("@ATTRIBUTE ");
			if (i < types.size() - 1) {
				bw.write("f" + i);
			} else {
				bw.write("target");
			}
			Set<Integer> type = types.get(i);
			if (type != null) {
				Iterator<Integer> it = type.iterator();
				bw.write(" {");
				bw.write(Integer.toString(it.next()));
				while (it.hasNext()) {
					bw.write(",");
					bw.write(Integer.toString(it.next()));
				}
				bw.write("}");
			} else {
				bw.write(" NUMERIC");
			}
			bw.write(nl);
		}
		bw.write("@DATA");
		bw.write(nl);

	}

	private void writeData(List<Set<Integer>> types, BufferedWriter bw) throws IOException {
		for (int x = 0; x < instances.size(); x++) {
			MyInstance instance = instances.get(x);
			for (int y = 0; y < instance.size(); y++) {
				if (y != 0) {
					bw.write(",");
				}
				Set<Integer> t = types.get(y);
				if (t != null) {
					bw.write("" + (int) Math.round(instance.get(y)));
				} else {
					bw.write("" + instance.get(y));
				}
			}
			bw.write(nl);
		}
	}

	private List<Set<Integer>> getTypes() {
		List<Set<Integer>> types = new ArrayList<>();
		MyInstance first = instances.get(0);
		for (int i = 0; i < first.size(); i++) {
			types.add(null);
		}
		for (int i = 0; i < first.size(); i++) {
			if (first.isNominal(i)) {
				types.set(i, new TreeSet<>());
			}
		}
		for (int x = 0; x < instances.size(); x++) {
			MyInstance instance = instances.get(x);
			for (int y = 0; y < instance.size(); y++) {
				if (!instance.isNominal(y)) {
					continue;
				}
				double d = instance.get(y);
				int label = (int) Math.round(d);
				if (Math.abs(d - label) > 0.0000001) {
					throw new RuntimeException(label + " " + d);
				}
				types.get(y).add(label);
				//System.out.println("adding " + label);
			}
		}
		return types;
	}

}
