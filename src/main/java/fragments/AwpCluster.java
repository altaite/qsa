package fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import fragments.clustering.RankedResiduePair;
import fragments.clustering.ResiduePairs;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Point3d;
import pdb.Residue;

public class AwpCluster {

	public final int id;
	private List<AwpNode> nodes = new ArrayList<>();
	private AwpClustering clustering;
	// A points to word pair B that was used to add A to the cluster 
	private Map<AwpNode, AwpNode> link = new HashMap<>();
	private Debugger debug = new Debugger();
	Map<Residue, Residue> residuesA = new HashMap<>();
	Map<Residue, Residue> residuesB = new HashMap<>();

	public AwpCluster(int id, AwpNode node, AwpClustering clustering) {
		this.id = id;
		nodes.add(node);
		saveResiduePairing(node);
		debug.add(node);
		this.clustering = clustering;
		this.clustering.add(this);
	}

	public final void saveResiduePairing(AwpNode node) {
		Word[] ws = node.getWords();
		Residue[] ras = ws[0].getResidues();
		Residue[] rbs = ws[1].getResidues();
		int n = ras.length;
		for (int i = 0; i < n; i++) {
			Residue ra = ras[i];
			Residue rb = rbs[i];
			residuesA.put(ra, rb);
			residuesB.put(rb, ra);
		}
	}

	/**
	 * Checks if the node does not assign a word differently than some node of the cluster.
	 *
	 * @return true iff the new word pairing defined by node is consistent with pairings defined by
	 * nodes already in this cluster, i.e. Guarantees
	 */
	public final boolean isConsistent(AwpNode node) {
		Word[] ws = node.getWords(); // new word pairing
		Residue[] ras = ws[0].getResidues(); // word in protein A
		Residue[] rbs = ws[1].getResidues(); // matching word in protein B
		int n = ras.length;
		for (int i = 0; i < n; i++) {
			Residue ra = ras[i];
			Residue rb = rbs[i];
			Residue rbo = residuesA.get(ra); // existing match for word nwa
			if (rbo != null && !rbo.equals(rb)) { // if it was matched and the match is different
				return false; // one word would be paired with two different words
			} // now let's do the same in oposite direction
			Residue rao = residuesB.get(rb);
			if (rao != null && !rao.equals(ra)) {
				return false;
			}
		}
		return true;
	}

	public Debugger getDebugger() {
		return debug;
	}

	public int getId() {
		return id;
	}

	public int sizeInWords() {
		return nodes.size();
	}

	public int sizeInResidues() {
		assert residuesA.size() == residuesB.size();
		return residuesA.size();
	}

	public List<AwpNode> getNodes() {
		return nodes;
	}

	public AwpClustering getClustering() {
		return clustering;
	}

	public void add(AwpCluster other) {
		debug.add(other.debug);
		for (AwpNode n : other.nodes) {
			saveResiduePairing(n);
		}
		this.nodes.addAll(other.nodes);
		for (AwpNode n : other.link.keySet()) {
			link.put(n, other.link.get(n));
		}
	}

	/**
	 * For each AwpNode, remember why was it added. Used later to prevent problems when one word has
	 * ambiguous match (e.g. slightly rotated alpha helix still has good RMSD, but RMSD of 3 words
	 * it connects can be terrible).
	 */
	public void connectWords(AwpNode a, AwpNode b) {
		if (!link.containsKey(a)) {
			link.put(a, b);
		}
		if (!link.containsKey(b)) {
			link.put(b, a);
		}
	}

	public AwpNode getLinked(AwpNode n) {
		return link.get(n);
	}

	public void replaceBy(AwpCluster other) {
		for (AwpNode n : nodes) {
			n.setClusterId(other.getId());
		}
	}

	@Override
	public boolean equals(Object o) {
		AwpCluster other = (AwpCluster) o;
		return id == other.id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return id + ": " + sizeInWords();
	}

	public Residue[][] computeAlignment() {
		ResiduePairs a = new ResiduePairs();
		for (AwpNode awp : nodes) {
			Residue[] x = awp.getWords()[0].getResidues();
			Residue[] y = awp.getWords()[1].getResidues();
			for (int i = 0; i < x.length; i++) {
				Residue xi = x[i];
				Residue yi = y[i];
				a.add(xi, yi, awp.getRmsd());
			}
		}
		Set<Residue> usedX = new HashSet<>();
		Set<Residue> usedY = new HashSet<>();
		List<Residue[]> aln = new ArrayList<>();
		for (RankedResiduePair rrp : a.values()) {
			Residue x = rrp.getX();
			Residue y = rrp.getY();
			if (!usedX.contains(x) && !usedY.contains(y)) {
				usedX.add(x);
				usedY.add(y);
				Residue[] p = {x, y};
				aln.add(p);
			}
		}
		Residue[][] alignment = new Residue[2][aln.size()];
		for (int i = 0; i < aln.size(); i++) {
			alignment[0][i] = aln.get(i)[0];
			alignment[1][i] = aln.get(i)[1];
		}
		return alignment;
	}

	public Point3d[][] getPoints() {
		return null;
	}
}
