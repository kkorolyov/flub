package dev.kkorolyov.flub

import dev.kkorolyov.flub.data.FacetedBundle

import spock.lang.Specification

import java.util.concurrent.ThreadLocalRandom

import static java.util.stream.Collectors.toSet

class FacetedBundleSpec extends Specification {
	String key = randString()
	Object element = Mock()

	FacetedBundle<String, Integer, Object> bundle = new FacetedBundle<>()

	def "contains entry if added"() {
		when:
		bundle.put(key, element)

		then:
		bundle.contains(key)
	}
	def "does not contain entry if not added"() {
		expect:
		!bundle.contains(key)
	}

	def "gets entry at key"() {
		when:
		bundle.put(key, element)

		then:
		bundle.get(key).element == element
	}
	def "gets null at unset key"() {
		expect:
		bundle.get(key) == null
	}

	def "gets facet intersection"() {
		int facet = randInt()
		String[] faceted = (0..4).collect { randString() }
		String[] other = (5..50).collect { randString() }

		when:
		faceted.each {
			bundle.put(it, it)
					.addFacets(facet)
		}
		other.each {
			bundle.put(it, it)
					.addFacets(randInt())
		}

		then:
		bundle.stream([facet]).collect(toSet()) == faceted as Set
	}

	def "replaces element at key"() {
		Object newElement = Mock()
		bundle.put(key, element)

		when:
		bundle.put(key, newElement)

		then:
		bundle.get(key).element == newElement
	}

	def "removes element at key"() {
		String otherKey = randString()
		int otherFacet = randInt()
		Object otherElement = Mock()

		bundle.put(key, element)
		bundle.put(otherKey, otherElement)
				.addFacets(otherFacet)

		expect:
		bundle.remove(key)
		!bundle.contains(key)
		bundle.stream([otherFacet]).collect(toSet()) == [otherElement] as Set
	}
	def "removes nothing at unset key"() {
		expect:
		!bundle.remove(key)
	}

	private static String randString() {
		return UUID.randomUUID().toString()
	}
	private static int randInt() {
		return ThreadLocalRandom.current().nextInt()
	}
}
