package frame;

public class Frames {

	private Frame[] frames;
	private SmallStructure structure;

	public Frames(SmallStructure structure) {
		this.structure = structure;
		frames = new Frame[structure.size()];
		int i = 0;
		for (SmallResidue r : structure) {
			frames[i++] = new Frame(r, structure);
		}
	}

	public void search(Frame query) {
		for (int i = 0; i < frames.length; i++) {
			query.computeDistance(frames[i]);
		}
	}

	public Frame get(int i) {
		return frames[i];
	}

	public int size() {
		return frames.length;
	}
}
