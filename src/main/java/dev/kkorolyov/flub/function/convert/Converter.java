package dev.kkorolyov.flub.function.convert;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static dev.kkorolyov.flub.collections.Iterables.append;

/**
 * Converts elements from {@code T} to {@code R}.
 * @param <T> input type
 * @param <R> output type
 */
@FunctionalInterface
public interface Converter<T, R> {
	/**
	 * Generates a converter which converts inputs matching a given test.
	 * @param test filter accepting {@code T}s to convert
	 * @param delegate converts accepted {@code T}s
	 * @param <T> input type
	 * @param <R> output type
	 * @return converter converting {@code T}s matching {@code test} using {@code delegate}
	 */
	static <T, R> Converter<T, Optional<R>> selective(Predicate<? super T> test, Converter<? super T, ? extends R> delegate) {
		return in -> Optional.of(in)
				.filter(test)
				.map(delegate::convert);
	}

	/** @see #reducing(Iterable) */
	@SafeVarargs
	static <T, R> Converter<T, Optional<R>> reducing(Converter<? super T, Optional<R>> delegate, Converter<? super T, Optional<R>>... delegates) {
		return reducing(append(delegate, delegates));
	}
	/**
	 * Generates a converter which converts inputs using the first matching selective delegate.
	 * @param delegates convert {@code T}s
	 * @param <T> input type
	 * @param <R> output type
	 * @return converter converting {@code T}s using the first non-empty-returning converter from {@code delegates}
	 */
	static <T, R> Converter<T, Optional<R>> reducing(Iterable<? extends Converter<? super T, Optional<R>>> delegates) {
		return in -> StreamSupport.stream(delegates.spliterator(), false)
				.map(converter -> converter.convert(in))
				.flatMap(Optional::stream)
				.findFirst();
	}

	/**
	 * Returns a converter which converts with {@code delegate} and throws if it returns an empty optional.
	 */
	static <T, R> Converter<T, R> enforcing(Converter<? super T, Optional<R>> delegate) {
		return t -> delegate.convert(t).orElseThrow(() -> new IllegalArgumentException("cannot convert: " + t));
	}

	/**
	 * Converts a {@code T} to an {@code R}.
	 * @param t input to convert
	 * @return conversion of {@code t} to {@code R} type
	 */
	R convert(T t);

	/**
	 * Converts multiple {@code T}s to {@code R}s.
	 * @param in inputs to convert
	 * @return conversions of all elements in {@code in} to {@code R} type, in input order
	 */
	default Collection<R> convert(Iterable<? extends T> in) {
		return StreamSupport.stream(in.spliterator(), false)
				.map(this::convert)
				.toList();
	}

	/**
	 * @param next next converter
	 * @param <R1> next result type
	 * @return converter which returns the result of converting the results of {@code this} using {@code next}
	 */
	default <R1> Converter<T, R1> andThen(Converter<? super R, ? extends R1> next) {
		return in -> next.convert(convert(in));
	}
}
