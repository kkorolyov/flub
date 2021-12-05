package dev.kkorolyov.flub.data.procedure

import dev.kkorolyov.flub.data.Graph

import spock.lang.Specification

class TopologicalSortSpec extends Specification {
	static class DfsSpec extends TopologicalSortSpec {
		Graph<Integer, Void> graph = new Graph<>()
				.put(0, [1, 11])
				.put(2, 3)
				.put(1, 2)
				.put(11, 2)

		def "sorts topologically"() {
			expect:
			[
					[0, 1, 11, 2, 3],
					[0, 11, 1, 2, 3]
			].any { TopologicalSort.dfs().apply(graph) == it }
		}
		def "excepts if topologically-sorting cyclic graph"() {
			when:
			TopologicalSort.dfs().apply(
					new Graph<>()
							.put(0, 0)
			)

			then:
			thrown IllegalStateException
		}
	}
}
