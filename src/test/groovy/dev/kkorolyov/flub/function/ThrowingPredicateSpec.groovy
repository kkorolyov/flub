package dev.kkorolyov.flub.function

import dev.kkorolyov.flub.function.throwing.ThrowingPredicate

import spock.lang.Specification

class ThrowingPredicateSpec extends Specification {
	ThrowingPredicate<?, Exception> throwingPredicate = new ThrowingPredicate<Object, Exception>() {
		@Override
		boolean testThrowing(Object o) throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingPredicate.test(new Object())

		then:
		thrown RuntimeException
	}
}
