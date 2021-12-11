package dev.kkorolyov.flub.data.procedure

import dev.kkorolyov.flub.data.Graph

import spock.lang.Shared
import spock.lang.Specification

import java.util.function.BiFunction
import java.util.function.BinaryOperator

class ShortestPathSpec extends Specification {
	static class BfsSpec extends ShortestPathSpec {
		@Shared
		Graph<Integer, Void> graph = new Graph<>()
				.put(1, [2, 3])
				.put(2, [3, 5])
				.put(3, 4)
				.put(4, 5)
				.put(42)

		def "gets empty path on null start"() {
			expect:
			ShortestPath.bfs().apply(graph, null, 1) == []
		}
		def "gets empty path on null end"() {
			expect:
			ShortestPath.bfs().apply(graph, 1, null) == []
		}

		def "gets empty path on non-existent start"() {
			expect:
			ShortestPath.bfs().apply(graph, 0, 1) == []
		}
		def "gets empty path on non-existent end"() {
			expect:
			ShortestPath.bfs().apply(graph, 1, 0) == []
		}
		def "gets empty path on disconnected end"() {
			expect:
			ShortestPath.bfs().apply(graph, 1, 42) == []
		}

		def "gets shortest path"() {
			expect:
			ShortestPath.bfs().apply(graph, 1, 5) == [1, 2, 5]
		}
	}

	static class DijkstraSpec extends ShortestPathSpec {
		@Shared
		Graph<String, Integer> graph = new Graph<>()
				.put('A', 'B', 2)
				.put('A', 'C', 5)
				.put('B', 'C', 2)
				.put('Lonely')

		def "gets empty path on null start"() {
			expect:
			ShortestPath.dijkstra().apply(graph, null, 'A') == []
		}
		def "gets empty path on null end"() {
			expect:
			ShortestPath.dijkstra().apply(graph, 'A', null) == []
		}

		def "gets empty path on non-existent start"() {
			expect:
			ShortestPath.dijkstra().apply(graph, 'Z', 'C') == []
		}
		def "gets empty path on non-existent end"() {
			expect:
			ShortestPath.dijkstra().apply(graph, 'A', 'Z') == []
		}
		def "gets empty path on disconnected end"() {
			expect:
			ShortestPath.dijkstra().apply(graph, 'A', 'Lonely') == []
		}

		def "gets shortest path"() {
			expect:
			ShortestPath.dijkstra().apply(graph, 'A', 'C') == ['A', 'B', 'C']
		}
	}

	static class AStarSpec extends ShortestPathSpec {
		@Shared
		Graph<String, Integer> graph = new Graph<>()
				.put('A', 'B', 2)
				.put('A', 'C', 5)
				.put('B', 'C', 2)
				.put('Lonely')

		@Shared
		BinaryOperator<Integer> adder = Integer::sum
		@Shared
		BiFunction<String, String, Integer> heuristic = { a, b ->
			0
		}

		def "gets empty path on null start"() {
			expect:
			ShortestPath.aStar(adder, heuristic).apply(graph, null, 'A') == []
		}
		def "gets empty path on null end"() {
			expect:
			ShortestPath.aStar(adder, heuristic).apply(graph, 'A', null) == []
		}

		def "gets empty path on non-existent start"() {
			expect:
			ShortestPath.aStar(adder, heuristic).apply(graph, 'Z', 'C') == []
		}
		def "gets empty path on non-existent end"() {
			expect:
			ShortestPath.aStar(adder, heuristic).apply(graph, 'A', 'Z') == []
		}
		def "gets empty path on disconnected end"() {
			expect:
			ShortestPath.aStar(adder, heuristic).apply(graph, 'A', 'Lonely') == []
		}

		def "gets shortest path"() {
			expect:
			ShortestPath.aStar(adder, heuristic).apply(graph, 'A', 'C') == ['A', 'B', 'C']
		}
	}
}
