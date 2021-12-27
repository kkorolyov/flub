package dev.kkorolyov.flub.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * A collection of {@code T} elements supporting multiple {@code K} keys/markers on individual elements.
 * Provides for efficient retrieval of all elements associated with a given subset of keys.
 */
public final class SparseMultiset<T, K> implements Iterable<T> {
	private static final ThreadLocal<BitSet> compute = ThreadLocal.withInitial(BitSet::new);

	private final List<T> dense = new ArrayList<>();
	private final Queue<Integer> tombstones = new ArrayDeque<>();

	private final Map<K, BitSet> sparse = new HashMap<>();

	/**
	 * Returns the element at index {@code i}.
	 */
	public T get(int i) {
		return dense.get(i);
	}
	/**
	 * Returns all elements associated with all {@code keys}.
	 */
	public Iterable<T> get(Iterable<? extends K> keys) {
		BitSet fullKey = compute.get();
		fullKey.set(0, dense.size());

		for (K key : keys) {
			BitSet bitSet = sparse.get(key);
			if (bitSet != null) fullKey.and(bitSet);
		}

		return fullKey.stream()
				.mapToObj(dense::get)
				::iterator;
	}

	/**
	 * Adds a new {@code element} and returns its index to use for subsequent modifications.
	 * @throws IllegalArgumentException if {@code element} is {@code null}
	 * @see #remove(int)
	 * @see #put(int, Iterable)
	 * @see #remove(int, Iterable)
	 */
	public int add(T element) {
		if (element == null) throw new IllegalArgumentException("null elements not permitted");

		Integer nextAvailable = tombstones.poll();
		int i = nextAvailable != null ? nextAvailable : dense.size();
		dense.add(element);
		return i;
	}
	/**
	 * Removes element at index {@code i} and returns whether such an element existed.
	 */
	public boolean remove(int i) {
		boolean result = i >= 0 && i < dense.size() && dense.set(i, null) != null;
		if (result) {
			tombstones.add(i);
			for (BitSet bitSet : sparse.values()) {
				bitSet.clear(i);
			}
		}
		return result;
	}

	/**
	 * Associates {@code keys} with element at index {@code i}.
	 */
	public void put(int i, Iterable<? extends K> keys) {
		for (K key : keys) {
			BitSet bitSet = sparse.get(key);
			if (bitSet == null) {
				bitSet = new BitSet(dense.size());
				sparse.put(key, bitSet);
			}
			bitSet.set(i);
		}
	}
	/**
	 * Removes associations to {@code keys} for element at index {@code i}.
	 */
	public void remove(int i, Iterable<? extends K> keys) {
		for (K key : keys) {
			BitSet bitSet = sparse.get(key);
			if (bitSet != null) bitSet.clear(i);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return dense.stream().filter(Objects::nonNull).iterator();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		SparseMultiset<?, ?> o = (SparseMultiset<?, ?>) obj;
		return dense.equals(o.dense) && sparse.equals(o.sparse);
	}
	@Override
	public int hashCode() {
		return Objects.hash(dense, sparse);
	}

	@Override
	public String toString() {
		return "SparseMultiset{" +
				"dense=" + dense +
				", sparse=" + sparse +
				'}';
	}
}
