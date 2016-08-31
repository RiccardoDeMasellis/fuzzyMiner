package org.processmining.models.fuzzyminer.fuzzypetrinet;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.plugins.fuzzyminer.fuzzycg2fuzzypn.Utils;

import java.util.HashSet;
import java.util.Iterator;
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
    private Set<PlaceEvaluation<N>> places;


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


    public Set<PlaceEvaluation<N>> evaluateBestPlaces(XLog log) {
        // First generate the possible places
        Set<Set<N>> inputNodesPowerSet = Utils.powerSet(inputNodes);
        Set<Set<N>> outputNodesPowerSet = Utils.powerSet(outputNodes);

        Set<PlaceEvaluation<N>> result = new HashSet<>();

        for (Set<N> outputNodeSet : outputNodesPowerSet) {
            for (Set<N> inputNodeSet : inputNodesPowerSet) {
                if (outputNodeSet.size() != 0 && inputNodeSet.size() != 0) {
                    PlaceEvaluation<N> placeEval = new PlaceEvaluation(outputNodeSet, inputNodeSet, log);
                    this.places.add(placeEval);
                    // Replay the place
                    placeEval.replayPlace();
                    result.add(placeEval);
                }
            }
        }
        return result;
    }


    public Set<PlaceEvaluation<N>> getPlacesAboveThreshold(double threshold) {
        Set<PlaceEvaluation<N>> result = new HashSet<>();
        for (PlaceEvaluation pe : this.places) {
            if (pe.evaluateReplayScore() >= threshold)
                result.add(pe);
        }
        return result;
    }

    public Set<PlaceEvaluation<N>> getNonRedundantPlacesAboveThreshold(double threshold) {
        Set<PlaceEvaluation<N>> aboveThreshold = this.getPlacesAboveThreshold(threshold);
        aboveThreshold.retainAll(getRedundantPlaces(aboveThreshold));
        return aboveThreshold;
    }



    @Override
    public String toString() {
        return "Cluster{" +
                "edges=" + edges +
                ", inputNodes=" + inputNodes +
                ", outputNodes=" + outputNodes +
                '}';
    }


    private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> getRedundantPlaces(Set<PlaceEvaluation<N>> placesAboveThreshold) {
        Set<PlaceEvaluation<N>> toBeDiscarded = new HashSet<>();

        for (PlaceEvaluation<N> p : placesAboveThreshold) {
            if(isRedundant(placesAboveThreshold, p))
                toBeDiscarded.add(p);
        }
        return toBeDiscarded;
    }


    /* To be called AFTER evaluateBestPlaces!
       The method scans this.places and sees if pe is redundant, i.e., if there is a set of placeEvaluation such that:
       the union of input sets is equal to pe input set
       the intersection of input sets is empty
       the union of output sets is equal to pe output set
       the intersection of output sets is empty
     */
    private static <N extends AbstractDirectedGraphNode> boolean isRedundant(Set<PlaceEvaluation<N>> aboveThreshold, PlaceEvaluation<N> pe) {
        // First of all, remove pe from above threshold.
        Set<PlaceEvaluation<N>> otherPlaces = new HashSet<>();
        otherPlaces.addAll(aboveThreshold);
        if (!otherPlaces.remove(pe))
            throw new RuntimeException("The input place evaluation should be contained in the set of place evaluations above the threshold");

        // We need the array to access elements by index (the apache math methods returns an iterator on indices)
        PlaceEvaluation<N>[] otherPlacesArray = (PlaceEvaluation<N>[]) otherPlaces.toArray();


        // We need to try every possible combination of i elements from the set otherPlacesArray.
        for (int i=2; i < otherPlacesArray.length; i++) {

            Iterator<int[]> it = org.apache.commons.math3.util.CombinatoricsUtils.combinationsIterator(otherPlacesArray.length, i);

            // Each next() is an array of indices representing the current choice of elements.
            while (it.hasNext()) {
                int[] indexesArray = it.next();
                Set<PlaceEvaluation<N>> currentCombination = new HashSet<>();

                // Build the set of PlacesEvalutions corresponding to the current choice of elements
                for (int j=0; j<indexesArray.length; j++)
                    currentCombination.add(otherPlacesArray[indexesArray[j]]);

                // Call the method that actually checks the redundancy
                if (checkRedundancy(currentCombination, pe))
                    return true;
            }
        }
        return false;
    }


    private static <N extends AbstractDirectedGraphNode> boolean checkRedundancy(Set<PlaceEvaluation<N>> currentCombination, PlaceEvaluation<N> placeEval) {
        // Check nullity of inputs!
        if (currentCombination==null || currentCombination.size()<2 || placeEval==null)
            throw new RuntimeException("Check the inputs of checkRedundancy!");

        // Build the union and the intersection of inputPlaces and outputPlaces of the currentCombination
        Set<N> intersectionInputPlaces = new HashSet<>();
        Set<N> intersectionOutputPlaces = new HashSet<>();
        Set<N> unionInputPlaces = new HashSet<>();
        Set<N> unionOutputPlaces = new HashSet<>();

        PlaceEvaluation<N>[] currentCombinationArray = (PlaceEvaluation<N>[]) currentCombination.toArray();
        intersectionInputPlaces.addAll(currentCombinationArray[0].getPlaceInputNodes());
        intersectionOutputPlaces.addAll(currentCombinationArray[0].getPlaceOutputNodes());

        for (PlaceEvaluation<N> p : currentCombination) {
            intersectionInputPlaces.retainAll(p.getPlaceInputNodes());
            intersectionOutputPlaces.retainAll(p.getPlaceOutputNodes());
            unionInputPlaces.addAll(p.getPlaceInputNodes());
            unionOutputPlaces.addAll(p.getPlaceOutputNodes());
        }

        if (intersectionInputPlaces.size() != 0 && intersectionOutputPlaces.size() != 0)
            return false;

        return (unionInputPlaces.equals(placeEval.getPlaceInputNodes()) && unionOutputPlaces.equals(placeEval.getPlaceOutputNodes()));
    }

}
