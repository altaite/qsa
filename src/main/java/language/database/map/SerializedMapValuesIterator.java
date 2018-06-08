package language.database.map;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Antonin Pavelka
 * @param <K>
 * @param <V>
 */
public class SerializedMapValuesIterator<K extends Serializable, V extends Serializable> implements Iterator<V> {

	private SerializedMap<K, V> map;
	private Iterator<K> iterator;

	public SerializedMapValuesIterator(SerializedMap<K, V> map) {
		this.map = map;
		iterator = map.keys().iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public V next() {
		K key = iterator.next();
		return map.getByKey(key);
	}

}
