package fbk.processmining.causalgraph;

import org.processmining.models.graphbased.directed.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by demas on 27/07/16.
 */
public abstract class CausalGraph<N extends DirectedGraphNode, E extends DirectedGraphEdge<? extends N, ? extends N>> extends AbstractDirectedGraph<N, E> {

    private Set<N> nodes;

    public CausalGraph() {
        super();
        this.nodes = new HashSet<>();
    }

    public CausalGraph(Set<N> nodes) {
        this();
        this.nodes = nodes;
        for (N node : nodes)
            this.graphElementAdded(node);
    }

    public CausalGraph(Set<N> nodes, Set<E> edges) {
        this(nodes);
        for (E edge : edges) {
            if (this.getNodes().contains(edge.getSource()) && this.getNodes().contains(edge.getTarget()))
                this.graphElementAdded(edge);
            else {
                throw new RuntimeException("You are try to add an edge between nodes that are not in the graph!");
            }
        }
    }

    @Override //TODO
    protected AbstractDirectedGraph<N, E> getEmptyClone() {
        return null;
    }

    @Override //TODO
    protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(DirectedGraph<N, E> graph) {
        return null;
    }

    @Override
    public void removeEdge(DirectedGraphEdge edge) {
        this.graphElementRemoved(edge);
    }

    @Override
    public Set<N> getNodes() {
        return new HashSet<>(this.nodes);
    }

    @Override
    public Set<E> getEdges() {
        HashSet<E> result = new HashSet<>();
        for (DirectedGraphNode node : this.nodes) {
            result.addAll(this.getInEdges(node));
            result.addAll(this.getOutEdges(node));
        }
        return result;
    }

    @Override
    public void removeNode(DirectedGraphNode cell) {
        nodes.remove(cell);
        this.graphElementRemoved(cell);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CausalGraph<?, ?> that = (CausalGraph<?, ?>) o;

        return getNodes().equals(that.getNodes());

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getNodes().hashCode();
        return result;
    }
}
