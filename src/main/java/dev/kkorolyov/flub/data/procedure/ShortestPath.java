package dev.kkorolyov.flub.data.procedure;

import dev.kkorolyov.flub.data.Graph;
import dev.kkorolyov.flub.data.Graph.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BinaryOperator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

/**
 * Shortest path procedure on a graph.
 * @param <T> graph node type
 * @param <E> graph edge type
 */
@FunctionalInterface
public interface ShortestPath<T, E> {
	/**
	 * Returns a shortest path procedure which:
	 * <pre>
	 * ignores edge weights
	 * uses breadth-first search to visit nodes
	 * has runtime O(V + E), space O(V) (V = number of nodes, E = number of edges)
	 * </pre>
	 */
	static <T> ShortestPath<T, ?> bfs() {
		return (graph, start, end) -> {
			Queue<Node<T, ?>> unseen = new ArrayDeque<>();  // Queue of nodes to visit
			Collection<Node<T, ?>> visited = new HashSet<>();
			Map<T, T> previous = new HashMap<>();  // Previous value in shortest path from start

			Node<T, ?> startNode = graph.get(start);
			Node<T, ?> endNode = graph.get(end);

			if (startNode != null && endNode != null) unseen.add(startNode);
			outer:
			for (Node<T, ?> node = unseen.poll(); node != null; node = unseen.poll()) {
				for (Node<T, ?> outbound : node.getOutbounds()) {
					if (visited.add(outbound)) {
						unseen.add(outbound);
						previous.put(outbound.getValue(), node.getValue());

						if (outbound.equals(endNode)) break outer;
					}
				}
			}
			return backtrack(previous, end);
		};
	}

	/** {@link #dijkstra(BinaryOperator)} with a convenience adder for numerical edges */
	static <T, E extends Number & Comparable<E>> ShortestPath<T, E> dijkstra() {
		return dijkstra(
				(num, num1) -> {
					if (num instanceof Double) {
						return (E) (Double) (num.doubleValue() + num1.doubleValue());
					} else if (num instanceof Float) {
						return (E) (Float) (num.floatValue() + num1.floatValue());
					} else if (num instanceof Long) {
						return (E) (Long) (num.longValue() + num1.longValue());
					} else {
						return (E) (Integer) (num.intValue() + num.intValue());
					}
				}
		);
	}
	/**
	 * Returns a shortest path procedure which:
	 * <pre>
	 * respects edge weights
	 * uses Dijkstra's algorithm to find a path
	 * has runtime O(E log V), space O(V) (V = number of nodes, E = number of edges)
	 * </pre>
	 */
	static <T, E extends Comparable<E>> ShortestPath<T, E> dijkstra(BinaryOperator<E> adder) {
		return (graph, start, end) -> {
			Comparator<E> edgeComparator = nullsLast(naturalOrder());

			Map<Node<T, E>, E> cost = new HashMap<>();  // Nodes in graph mapped to their costs from start node
			PriorityQueue<Node<T, E>> unseen = new PriorityQueue<>(comparing(cost::get, edgeComparator));  // Prioritized queue of nodes to visit
			Map<T, T> previous = new HashMap<>();  // Previous value in shortest path from start

			Node<T, E> startNode = graph.get(start);
			Node<T, E> endNode = graph.get(end);

			if (startNode != null && endNode != null) unseen.add(startNode);
			for (Node<T, E> node = unseen.poll(); node != null; node = unseen.poll()) {
				if (node.equals(endNode)) break;

				for (Node.RelatedNode<T, E> relatedOutbound : node.getOutboundRelations()) {
					E incomingCost = cost.get(node);
					E newCost = (incomingCost == null || relatedOutbound.getEdge() == null) ? relatedOutbound.getEdge() : adder.apply(incomingCost, relatedOutbound.getEdge());

					E oldCost = cost.get(relatedOutbound.getNode());
					if ((oldCost == null && newCost == null) || edgeComparator.compare(newCost, oldCost) < 0) {
						previous.put(relatedOutbound.getNode().getValue(), node.getValue());

						unseen.remove(relatedOutbound.getNode());
						cost.put(relatedOutbound.getNode(), newCost);
						unseen.add(relatedOutbound.getNode());
					}
				}
			}

			return backtrack(previous, end);
		};
	}

	private static <T> List<T> backtrack(Map<T, ? extends T> previous, T end) {
		List<T> result = new ArrayList<>();
		for (T value = previous.containsKey(end) ? end : null; value != null; value = previous.get(value)) {
			result.add(value);
		}
		Collections.reverse(result);

		return result;
	}

	/**
	 * Returns a list of nodes denoting a shortest path in {@code graph} from {@code start} to {@code end}.
	 */
	List<T> apply(Graph<T, E> graph, T start, T end);
}
