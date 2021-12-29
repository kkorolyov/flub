package dev.kkorolyov.flub.data

import spock.lang.Specification

class SparseMultisetSpec extends Specification {
	SparseMultiset<Object, Integer> multiset = new SparseMultiset()

	def "gets by index"() {
		Object element = Mock()

		int i = multiset.add(element)

		expect:
		multiset.get(i) == element
	}

	def "gets by keys"() {
		int key = 3
		int otherKey = 15
		Object[] forKey = (0..4).collect { Mock(Object) }
		Object[] forOtherKey = (5..50).collect { Mock(Object) }

		when:
		forKey.each {
			multiset.put(multiset.add(it), [key])
		}
		forOtherKey.each {
			multiset.put(multiset.add(it), [otherKey])
		}

		then:
		multiset.get([key]) as Set == forKey as Set
		multiset.get([otherKey]) as Set == forOtherKey as Set
	}
	def "misses by unknown key"() {
		int key = 4

		when:
		multiset.add(Mock(Object))

		then:
		(multiset.get([key]) as Set).empty
	}

	def "removes element at index"() {
		Object element = Mock()
		Object otherElement = Mock()
		int key = 4

		int index = multiset.add(element)
		multiset.put(index, [key])
		multiset.put(multiset.add(otherElement), [key])

		expect:
		multiset.remove(index)
		multiset.get([key]) as Set == [otherElement] as Set
	}
	def "removes nothing at unset index"() {
		expect:
		!multiset.remove(0)
	}
}
