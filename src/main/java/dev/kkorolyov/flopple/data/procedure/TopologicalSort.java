package dev.kkorolyov.flopple.data.procedure;

import dev.kkorolyov.flopple.data.Graph;
import dev.kkorolyov.flopple.data.Graph.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

/**
 * Topological sorting procedure on a graph.
 * Each procedure returns some topological sorting of a graph as a list of nodes.
 * @param <T> graph node type
 */
@FunctionalInterface
public interface TopologicalSort<T> {
	/**
	 * Returns a topological sorting procedure which:
	 * <pre>
	 * uses depth-first search to visit nodes
	 * throws {@link IllegalStateException} when executed on a cyclic graph
	 * has runtime O(V + E), space O(V) (V = number of nodes, E = number of edges)
	 * </pre>
	 */
	static <T> TopologicalSort<T> dfs() {
		return new TopologicalSort<>() {
			private final Collection<Node<T, ?>> unseen = new HashSet<>();  // Nodes to visit
			private final Collection<Node<T, ?>> visited = new HashSet<>();  // Nodes seen across all visits
			private final Deque<T> sort = new ArrayDeque<>();

			/**
			 * @return topologically-sorted list of nodes
			 * @throws IllegalStateException if the associated graph is not a directed acyclic graph
			 */
			@Override
			public List<T> apply(Graph<T, ?> graph) {
				unseen.addAll(graph.getNodes());
				while (!unseen.isEmpty()) {
					visit(unseen.iterator().next());
				}
				List<T> result = new ArrayList<>(sort);

				clear();

				return result;
			}
			private void visit(Node<T, ?> node) {
				if (!visited.contains(node)) {
					if (!unseen.contains(node)) throw new IllegalStateException("not a directed acyclic graph");

					unseen.remove(node);

					for (Node<T, ?> outbound : node.getOutbounds()) visit(outbound);

					visited.add(node);

					sort.push(node.getValue());
				}
			}

			private void clear() {
				unseen.clear();
				visited.clear();
				sort.clear();
			}
		};
	}

	/**
	 * Returns a list of notes denoting a topological sorting of {@code graph}.
	 */
	List<T> apply(Graph<T, ?> graph);
}
