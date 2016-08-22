package org.processmining.fuzzycg2fuzzypn;

import org.processmining.models.causalgraph.FuzzyCausalGraph;
import org.processmining.models.causalgraph.FuzzyDirectedGraphNode;
import org.processmining.models.causalgraph.FuzzyDirectedSureGraphEdge;
import org.processmining.models.fuzzypetrinet.Cluster;
import org.processmining.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.DirectedGraph;

import java.util.*;

/**
 * Created by demas on 19/08/16.
 */
public class FuzzyCGToFuzzyPN {

    public static FuzzyPetrinet fuzzyCGToFuzzyPN(FuzzyCausalGraph graph) {
        // We consider only sure edges!
        Set<FuzzyDirectedSureGraphEdge> edges = graph.getSureEdges();

        // Build the clusters
        Set<Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode>> clusters = identifyClusters(edges);


    }



    // Build the clusters by least fixpoint computations, according to the definition on the paper.
    private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<Cluster<E, N>> identifyClusters(Set<E> edges) {
        HashSet<Cluster<E, N>> clusterSet = new HashSet<>();

        // Efficiency: keep a set of edges already analyzed
        Set<E> alreadyAnalyzed = new HashSet<>();

        // for each edge not yet analyzed, create a cluster, as every edge must be contained in a cluster.
        for (E edge : edges) {
             if (alreadyAnalyzed.contains(edge))
                 continue;

            // create the new cluster
            Cluster newCluster = new Cluster();
            // add it to the set of cluster
            clusterSet.add(newCluster);
            // add the current edge to it
            newCluster.addEdge(edge);
            alreadyAnalyzed.add(edge);
            Cluster oldCluster = new Cluster();
            // add all the other edges to the current cluster.
            while (! oldCluster.equals(newCluster)) {
                // 1) oldCluster = new Cluster
                oldCluster.addAllEdges(newCluster.getEdges());

                // 2) Analyze all the edges in OldCluster and add them to newCluster...
                for (E e : (Set<E>) oldCluster.getEdges()) {
                    N source = (N) e.getSource();
                    N target = (N) e.getTarget();
                    Set<E> edgesForSource = getEdgesForNode(source, edges);
                    Set<E> edgesForTarget = getEdgesForNode(target, edges);
                    newCluster.addAllEdges(edgesForSource);
                    newCluster.addAllEdges(edgesForTarget);

                    //Add them to already analyzed, as they are already part of a cluster.
                    alreadyAnalyzed.addAll(edgesForSource);
                    alreadyAnalyzed.addAll(edgesForTarget);
                }
                // ...until there is no other edge to add.
            }
            // Finalize the current cluster as it is stable
            newCluster.updateInputAndOutputNodes();
        }
        return clusterSet;
    }


    // Given a node and a set of edges, it returns all the edges of the set having that node as source or target
    private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesForNode(N node, Set<E> edges) {
        Set<E> result = new HashSet<>();
        for (E edge : edges) {
            if (edge.getSource().equals(node) || edge.getTarget().equals(node))
                result.add(edge);
        }
        return result;
    }

    public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

}
