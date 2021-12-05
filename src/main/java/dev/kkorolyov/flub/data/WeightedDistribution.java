package dev.kkorolyov.flub.data;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A distribution of randomly-selectable weighted values.
 */
public final class WeightedDistribution<T> {
	private final NavigableMap<Integer, T> distribution = new TreeMap<>();
	private int total;

	/**
	 * Adds a weighted value to this distribution.
	 * @param value added value
	 * @param weight value weight relative to this distribution's total weight
	 * @return {@code this}
	 */
	public WeightedDistribution<T> add(T value, int weight) {
		distribution.put(total, value);
		total += weight;

		return this;
	}

	/** @return random value from this distribution; or {@code null} if empty distribution */
	public T get() {
		return total <= 0 ? null : distribution.floorEntry(ThreadLocalRandom.current().nextInt(total)).getValue();
	}

	/** @return number of contained values */
	public int size() {
		return distribution.size();
	}
}
