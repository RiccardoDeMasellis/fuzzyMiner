package org.processmining.fuzzycg2fuzzypn;

import org.processmining.fuzzyminer.FuzzyMinerSettings;
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

    public static FuzzyPetrinet fuzzyCGToFuzzyPN(FuzzyCausalGraph graph, FuzzyMinerSettings settings) {
        // We consider only sure edges!
        Set<FuzzyDirectedSureGraphEdge> edges = graph.getSureEdges();

        // Build the clusters
        Set<Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode>> clusters = identifyClusters(edges);


        // Call clusters evaluations

        // For each cluster
            // select the placeEval above the threshold
            // eliminate redundant placeEval

        // build the net

        // remove reduntant places
        
        return null;
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

            // create the new set of edges constituting the new cluster
            Set<E> newCluster = new HashSet<>();

            // add the current edge to it
            newCluster.add(edge);
            alreadyAnalyzed.add(edge);
            Set<E> oldCluster = new HashSet<>();
            // add all the other edges to the current cluster.
            while (! oldCluster.equals(newCluster)) {
                // 1) oldCluster = new Cluster
                oldCluster.addAll(newCluster);

                // 2) Analyze all the edges in OldCluster and add them to newCluster...
                for (E e : oldCluster) {
                    N source = (N) e.getSource();
                    N target = (N) e.getTarget();

                    // Non e cosi. Modificare!

                    Set<E> edgesForSource = getEdgesHavingSourceNode(source, edges);
                    Set<E> edgesForTarget = getEdgesHavingTargetNode(target, edges);
                    newCluster.addAll(edgesForSource);
                    newCluster.addAll(edgesForTarget);

                    //Add them to already analyzed, as they are already part of a cluster.
                    alreadyAnalyzed.addAll(edgesForSource);
                    alreadyAnalyzed.addAll(edgesForTarget);
                }
                // ...until there is no other edge to add.
            }
            // add it to the set of cluster
            clusterSet.add(new Cluster<>(newCluster));
        }
        return clusterSet;
    }


    /*
    Given a source node s and a set of edges, the method returns all edges having s as source.
     */
    private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesHavingSourceNode(N source, Set<E> edges) {
        Set<E> result = new HashSet<>();
        for (E edge : edges) {
            if (edge.getSource().equals(source))
                result.add(edge);
        }
        return result;
    }

    /*
    Given a target node t and a set of edges, the method returns all edges having t as source.
     */
    private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesHavingTargetNode(N target, Set<E> edges) {
        Set<E> result = new HashSet<>();
        for (E edge : edges) {
            if (edge.getTarget().equals(target))
                result.add(edge);
        }
        return result;
    }


}
