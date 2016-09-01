package org.processmining.plugins.fuzzyminer.fuzzycg2fuzzypn;

import org.deckfour.xes.model.XLog;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyCausalGraph;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyDirectedGraphEdge;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyDirectedGraphNode;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyDirectedSureGraphEdge;
import org.processmining.models.fuzzyminer.fuzzypetrinet.Cluster;
import org.processmining.models.fuzzyminer.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.models.fuzzyminer.fuzzypetrinet.PlaceEvaluation;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.plugins.fuzzyminer.FuzzyMinerSettings;

import java.util.*;

/**
 * Created by demas on 19/08/16.
 */
public class FuzzyCGToFuzzyPN {

    public static <N extends AbstractDirectedGraphNode> FuzzyPetrinet fuzzyCGToFuzzyPN(FuzzyCausalGraph graph, XLog log, FuzzyMinerSettings settings) {
        FuzzyPetrinet result = new FuzzyPetrinet("minedFuzzyPetrinet");

        // We consider only sure edges!
        Set<FuzzyDirectedSureGraphEdge> edges = graph.getSureEdges();

        // Build the clusters
        Set<Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode>> clusters = identifyClusters(edges);

        // Prepare the data structure for the nodes to be added
        Set<PlaceEvaluation> placesToBeAdded = new HashSet<>();

        // For each cluster:
        for (Cluster c : clusters) {

            // call clusters evaluations
            c.evaluateBestPlaces(log);
            // select the places above the threshold and add them to the set of places to be added to the fuzzynet
            placesToBeAdded.addAll(c.getNonRedundantPlacesAboveThreshold(settings.getPlaceEvalThreshold()));
        }

        // Build the net. For each PlaceEvaluation in placesToBeAdded, add a place and the respective transitions
        for (PlaceEvaluation<N> pe : placesToBeAdded)
            result.addPlaceFromPlaceEvaluation(pe);

        /* Then add the sure and uncertain arcs between transitions in the net coming from the causal graph
            I do not know which sure transitions have met the threshold thus have been replaced by a place transition,
             but such a check is directly in the method
         */
        for (FuzzyDirectedGraphEdge edge : graph.getEdges())
            result.addTransitionsArcFromFCGEdge(edge);
        
        return result;
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
