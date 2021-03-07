package dev.kkorolyov.flopple.function.convert

import spock.lang.Specification

class ConverterSpec extends Specification {
	def "converts as chain"() {
		Converter<String, String> a = makeConverter(null, "a")
		Converter<String, String> b = makeConverter("a", "b")
		Converter<String, String> c = makeConverter("b", "c")

		expect:
		a.andThen(b).andThen(c).convert((String) null) == "c"
	}

	private <T, R> Converter<T, R> makeConverter(T expected, R result) {
		return { input -> input == expected ? result : null }
	}
}
