package fragment.serialization;

import algorithm.Biword;
import fragment.word.Word;
import com.esotericsoftware.kryo.Kryo;
import altaite.geometry.primitives.Point;
import structure.Residue;
import structure.ResidueId;
import fragment.FragmentOfPolymer;

/**
 *
 * @author Antonin Pavelka
 */
public class KryoFactory {

	public Kryo getKryoForBiwords() {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		// save few percent of space and some time
		kryo.register(Biword.class);
		kryo.register(Residue.class);
		kryo.register(ResidueId.class);
		kryo.register(Word.class);
		kryo.register(FragmentOfPolymer.class);
		kryo.register(String.class);
		kryo.register(Point.class);
		return kryo;
	}
	
	public Kryo getKryoForIndex() {
		Kryo kryo = new Kryo();				
		return kryo;
	}
}
