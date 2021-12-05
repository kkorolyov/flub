package dev.kkorolyov.flub.function

import dev.kkorolyov.flub.function.throwing.ThrowingRunnable

import spock.lang.Specification

class ThrowingRunnableSpec extends Specification {
	ThrowingRunnable<Exception> throwingRunnable = new ThrowingRunnable() {
		@Override
		void runThrowing() throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingRunnable.run()

		then:
		thrown RuntimeException
	}
}
