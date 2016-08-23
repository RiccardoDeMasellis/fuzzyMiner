package org.processmining.models.fuzzypetrinet;

import org.deckfour.xes.model.XLog;
import org.processmining.fuzzycg2fuzzypn.Utils;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

import java.util.*;

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

    private Set<PlaceEvaluation> places;


    public Cluster(Set<E> edges) {
        this.edges = edges;
        this.inputNodes = new HashSet<>();
        this.outputNodes = new HashSet<>();
        computeInputAndOutputNodes();
        this.places = new HashSet<>();
    }

    private void computeInputAndOutputNodes() {
        for(E edge : this.edges) {
            inputNodes.add((N) edge.getTarget());
            outputNodes.add((N) edge.getSource());
        }
    }

    public Set<E> getEdges() {
        return new HashSet<>(edges);
    }


    public Set<N> getInputNodes() {
        return new HashSet<>(inputNodes);
    }


    public Set<N> getOutputNodes() {
        return new HashSet<>(outputNodes);
    }


    public PlaceEvaluation evaluateBestPlace(XLog log) {
        // First generate the possible places
        Set<Set<N>> inputNodesPowerSet = Utils.powerSet(inputNodes);
        Set<Set<N>> outputNodesPowerSet = Utils.powerSet(outputNodes);

        int bestTraceNumberAccepted = 0;

        for (Set<N> outputNodeSet : outputNodesPowerSet) {
            for (Set<N> inputNodeSet : inputNodesPowerSet) {
                if (outputNodeSet.size() != 0 && inputNodeSet.size() != 0) {
                    PlaceEvaluation placeEval = new PlaceEvaluation(outputNodeSet, inputNodeSet, log);
                    this.places.add(placeEval);
                    // Replay the place
                    placeEval.replayPlace();
                    // Update bestTraceNumberAccepted
                    if (placeEval.getAcceptedTracesNumber() >= bestTraceNumberAccepted)
                        bestTraceNumberAccepted = placeEval.getAcceptedTracesNumber();
                }
            }
        }
        // return the places with getAcceptedTracesNumber() = bestTraceNumber
        Set<PlaceEvaluation> result = new HashSet<>();
        for (PlaceEvaluation pe : this.places) {
            if (pe.getAcceptedTracesNumber() == bestTraceNumberAccepted)
                result.add(pe);
        }

        // Strategy for selecting just one!
        return selectPlaceEvaluation(result);
    }


    // Just return a random one!
    // todo: may be redundant?
    public static PlaceEvaluation selectPlaceEvaluation(Set<PlaceEvaluation> bestPlaces) {
        PlaceEvaluation[] arrayBest = (PlaceEvaluation[]) bestPlaces.toArray();
        return arrayBest[0];
    }


}
