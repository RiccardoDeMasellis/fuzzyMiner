package org.processmining.models.fuzzypetrinet;

import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by demas on 19/08/16.
 */

/*
    The usage of this class is the following:
    1) Use the constructor to build the cluster;
    2) add all the edges belonging to the cluster (ignoring the inputNodes and outputNodes);
    3) once all the edges have been added, call the methods updateInputNodes and updateOutputNodes which updates the input and output nodes and prevents other edges to be added:
    the cluster is considered stable and hence immutable.
 */
public class Cluster<E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> {
    private Set<E> edges;
    private Set<N> inputNodes, outputNodes;
    private boolean updatable;

    public Cluster() {
        this.edges = new HashSet<>();
        this.inputNodes = null;
        this.outputNodes = null;
        this.updatable = true;
    }

    public synchronized void addEdge(E edge) {
        if (!updatable)
            throw new RuntimeException("Cannot add edges to a stable cluster!");
        this.edges.add(edge);
    }

    public synchronized void addAllEdges(Collection<E> edges) {
        if (!updatable)
            throw new RuntimeException("Cannot add edges to a stable cluster!");
        this.edges.addAll(edges);
    }

    public synchronized void updateInputAndOutputNodes() {
        this.updatable = false;
        for(E edge : this.edges) {
            inputNodes.add((N) edge.getTarget());
            outputNodes.add((N) edge.getSource());
        }
    }


    public Set<E> getEdges() {
        return new HashSet<>(edges);
    }


    public synchronized Set<N> getInputNodes() {
        if (this.updatable = true)
            throw new RuntimeException("The input and output nodes have not yet been computed. Cluster still unstable.");
        return new HashSet<>(inputNodes);
    }


    public synchronized Set<N> getOutputNodes() {
        if (this.updatable = true)
            throw new RuntimeException("The input and output nodes have not yet been computed. Cluster still unstable.");
        return new HashSet<>(outputNodes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cluster<?, ?> cluster = (Cluster<?, ?>) o;

        if (updatable != cluster.updatable) return false;
        if (!getEdges().equals(cluster.getEdges())) return false;
        if (getInputNodes() != null ? !getInputNodes().equals(cluster.getInputNodes()) : cluster.getInputNodes() != null)
            return false;
        return getOutputNodes() != null ? getOutputNodes().equals(cluster.getOutputNodes()) : cluster.getOutputNodes() == null;

    }

    @Override
    public int hashCode() {
        int result = getEdges().hashCode();
        result = 31 * result + (getInputNodes() != null ? getInputNodes().hashCode() : 0);
        result = 31 * result + (getOutputNodes() != null ? getOutputNodes().hashCode() : 0);
        result = 31 * result + (updatable ? 1 : 0);
        return result;
    }
}
