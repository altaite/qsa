package util.pymol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Matrix4d;

import fragments.Fragment;
import fragments.FragmentPair;
import fragments.clustering.Cluster;
import geometry.Point;
import io.Directories;
import pdb.PdbLine;
import pdb.Residue;
import pdb.SimpleChain;
import pdb.SimpleStructure;

public class PymolVisualizer {

	private List<Chain> chains = new ArrayList<>();
	private List<Cluster> clusters = new ArrayList<>();
	private List<String> selectionNames = new ArrayList<>();
	private List<List<Residue>> selectionResidues = new ArrayList<>();

	public void add(Chain c) {
		chains.add(c);
	}

	public void add(Cluster c) {
		clusters.add(c);
	}

	public void addSelection(String name, List<Residue> residues) {
		selectionNames.add(name);
		selectionResidues.add(residues);
	}

	public void saveSelections(File f) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			for (int i = 0; i < selectionNames.size(); i++) {
				List<Residue> rs = selectionResidues.get(i);
				String name = selectionNames.get(i);
				bw.write(select(name, getSelection(rs)));
				bw.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void save(SimpleStructure s, File f) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			for (SimpleChain sc : s.getChains()) {
				List<PdbLine> atoms = new ArrayList<>();
				for (Residue r : sc.getResidues()) {
					Point p = r.getPosition();
					PdbLine pl = new PdbLine(r.getAtomSerial(), "CA", "C", "GLY",
							Integer.toString(r.getId().getSequenceNumber()), r.getId().getChain().getId().charAt(0),
							p.getX(), p.getY(), p.getZ());
					atoms.add(pl);
				}
				for (int i = 0; i < atoms.size(); i++) {
					PdbLine pl = atoms.get(i);
					bw.write(pl.getPdbString());
					bw.newLine();
					if (i > 0) {
						int a = atoms.get(i - 1).getAtomSerialNumber();
						int b = atoms.get(i).getAtomSerialNumber();
						bw.write(PdbLine.getConnectString(a, b));
						bw.newLine();
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void save(File pdb, File py) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(pdb));
			for (Chain c : chains) {
				c.save(bw);
			}
			bw.close();
			bw = new BufferedWriter(new FileWriter(py));
			for (Cluster c : clusters) {
				saveCluster(c, bw);
			}
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getSelection(List<Residue> rs, char c) {
		StringBuilder sb = new StringBuilder("sele " + c + ", ");
		for (Residue r : rs) {
			sb.append("(resi " + r.getIndex() + " and chain " + c + ") + ");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private String getSelection(List<Residue> rs) {
		StringBuilder sb = new StringBuilder("");
		for (Residue r : rs) {
			sb.append("(resi " + r.getId().getPdbString() + " and chain " + r.getId().getChain() + ") + ");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private void saveCluster(Cluster c, BufferedWriter bw) throws IOException {
		FragmentPair p = c.getCore();

		// for (FragmentPair p : c.getFragmentPairs()) {
		Fragment[] fs = p.get();
		System.out.println(" d " + fs[0].getCenter().distance(fs[1].getCenter()));
		bw.write(getSelection(fs[0].getResidues(), 'A'));

		bw.newLine();
		bw.write(getSelection(fs[1].getResidues(), 'B'));
		bw.newLine();
		bw.newLine();
		// }
	}

	public static List<String> residuesToSelection(String structureId, Collection<Residue> rs) {
		List<String> l = new ArrayList<>();
		for (Residue r : rs) {
			StringBuilder sb = new StringBuilder();
			sb.append("resi ").append(r.getId().getSequenceNumber()).append(" and chain ")
					.append(r.getId().getChain().getId() + " and " + structureId);
			l.add(sb.toString());
		}
		return l;
	}

	public static String listToSelection(List<String> list) {
		return String.join(" + ", list);
		// sb.deleteCharAt(sb.length() - 1);
		// sb.deleteCharAt(sb.length() - 1);
		// sb.deleteCharAt(sb.length() - 1);

	}

	// cmd.select('a', 'resi 10 and chain B')
	public static String select(String name, String selection) {
		return "cmd.select('" + name + "', '" + selection + "')";
	}

	public static void saveLauncher(File a, File b) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(Directories.createDefault().getLauncher()))) {
			bw.write("cmd.reinitialize()\n");
			bw.write("cmd.load('" + a + "')\n");
			bw.write("cmd.load('" + b + "')\n");
			bw.write("cmd.run('" + Directories.createDefault().getFragmentPairSelections() + "')\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}