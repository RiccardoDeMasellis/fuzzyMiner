package org.processmining.fuzzyminer.models.fuzzypetrinet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn.Utils;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;



/**
 * Created by demas on 19/08/16.
 */

public class Cluster<E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> implements Runnable {
    private Set<E> edges;
    private Set<N> inputNodes, outputNodes;
    private Set<PlaceEvaluation<N>> places;
    
    // new variables for the thread implementation, to be initialized later.
    private XLog log;
    private Map<String, Integer> activityFrequencyMap;
    private double placeEvaluationThreshold;
    private double prePlaceEvaluationThreshold;
    
    private Set<PlaceEvaluation<N>> placesAboveThreshold;


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
    
    

    public void setPrePlaceEvaluationThreshold(double prePlaceEvaluationThreshold) {
		this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
	}

	public void setPlaceEvaluationThreshold(double placeEvaluationThreshold) {
		this.placeEvaluationThreshold = placeEvaluationThreshold;
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
    

	public void setLog(XLog log) {
		this.log = log;
	}

	public void setActivityFrequencyMap(Map<String, Integer> activityFrequencyMap) {
		this.activityFrequencyMap = activityFrequencyMap;
	}


	public Set<PlaceEvaluation<N>> getPlacesAboveThreshold() {
		return placesAboveThreshold;
	}

	private void setPlacesAboveThreshold(Set<PlaceEvaluation<N>> placeAboveThreshold) {
		this.placesAboveThreshold = placeAboveThreshold;
	}

	public void run() {
    	evaluatePlaces();
    	setPlacesAboveThreshold(computePlacesAboveThreshold(placeEvaluationThreshold));
    }


    public void evaluatePlaces() {
        // First generate the possible places
    	System.out.println("Start building powerSets");
    	long startTime = System.currentTimeMillis();
        Set<Set<N>> inputNodesPowerSet = Utils.powerSet(inputNodes);
        Set<Set<N>> outputNodesPowerSet = Utils.powerSet(outputNodes);
        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime-startTime)/60000.0;
        System.out.println("End building powerSets in " + elapsedTime + " mins.");
        
        ExecutorService exec = Executors.newCachedThreadPool();

        for (Set<N> outputNodeSet : outputNodesPowerSet) {
            for (Set<N> inputNodeSet : inputNodesPowerSet) {
                if (outputNodeSet.size() != 0 && inputNodeSet.size() != 0) {
                    PlaceEvaluation<N> placeEval = new PlaceEvaluation(outputNodeSet, inputNodeSet, log, activityFrequencyMap, prePlaceEvaluationThreshold);
                    this.places.add(placeEval);
                    
                    exec.execute(placeEval);
                }
            }
        }
        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    public Set<PlaceEvaluation<N>> computePlacesAboveThreshold(double threshold) {
        Set<PlaceEvaluation<N>> result = new HashSet<>();
        for (PlaceEvaluation pe : this.places) {
            System.out.println("Place evaluation "+pe.toString());
            System.out.println("score: "+pe.evaluateReplayScore()+" threshold: "+threshold);        	
            if (pe.evaluateReplayScore() >= threshold)
                result.add(pe);
        }
        return result;
    }

    
    /*public Set<PlaceEvaluation<N>> getNonRedundantPlacesAboveThreshold(double threshold) {
        Set<PlaceEvaluation<N>> aboveThreshold = this.getPlacesAboveThreshold(threshold);
        //aboveThreshold.removeAll(getRedundantPlaces(aboveThreshold));
        return aboveThreshold;
    }*/


    @Override
    public String toString() {
        return "Cluster{" +
                "edges=" + edges +
                ", inputNodes=" + inputNodes +
                ", outputNodes=" + outputNodes +
                '}';
    }



/*    private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> getRedundantPlaces(Set<PlaceEvaluation<N>> placesAboveThreshold) {
        System.out.println("Redundant place computation started!");
    	Set<PlaceEvaluation<N>> toBeDiscarded = new HashSet<>();

        for (PlaceEvaluation<N> p : placesAboveThreshold) {
            if(isRedundant(placesAboveThreshold, p))
                toBeDiscarded.add(p);
        }
        System.out.println("Redundant place computation ended!");
        return toBeDiscarded;
    }*/


    /* To be called AFTER evaluateBestPlaces!
       The method scans this.places and sees if pe is redundant, i.e., if there is a set of placeEvaluation such that:
       the union of input sets is equal to pe input set
       the intersection of input sets is empty
       the union of output sets is equal to pe output set
       the intersection of output sets is empty
     */
    /*private static <N extends AbstractDirectedGraphNode> boolean isRedundant(Set<PlaceEvaluation<N>> aboveThreshold, PlaceEvaluation<N> pe) {
        // First of all, remove pe from above threshold.
        Set<PlaceEvaluation<N>> otherPlaces = new HashSet<>();
        otherPlaces.addAll(aboveThreshold);
        if (!otherPlaces.remove(pe))
            throw new RuntimeException("The input place evaluation should be contained in the set of place evaluations above the threshold");

        // We need the array to access elements by index (the apache math methods returns an iterator on indices)
        PlaceEvaluation<N>[] otherPlacesArray = (PlaceEvaluation<N>[]) otherPlaces.toArray(new PlaceEvaluation<?>[otherPlaces.size()]);


        // We need to try every possible combination of i elements from the set otherPlacesArray.
        for (int i=2; i < otherPlacesArray.length; i++) {

            Iterator<int[]> it = org.apache.commons.math3.util.CombinatoricsUtils.combinationsIterator(otherPlacesArray.length, i);

            // Each next() is an array of indices representing the current choice of elements.
            while (it.hasNext()) {
                int[] indexesArray = it.next();
                //System.out.println("OtherPlaces number: " + otherPlacesArray.length + ". Current combination: " + Arrays.toString(indexesArray));
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

        PlaceEvaluation<N>[] currentCombinationArray = (PlaceEvaluation<N>[]) currentCombination.toArray(new PlaceEvaluation<?>[currentCombination.size()]);
        intersectionInputPlaces.addAll(currentCombinationArray[0].getPlaceInputNodes());
        intersectionOutputPlaces.addAll(currentCombinationArray[0].getPlaceOutputNodes());

        for (PlaceEvaluation<N> p : currentCombination) {
            intersectionInputPlaces.retainAll(p.getPlaceInputNodes());
            intersectionOutputPlaces.retainAll(p.getPlaceOutputNodes());
            unionInputPlaces.addAll(p.getPlaceInputNodes());
            unionOutputPlaces.addAll(p.getPlaceOutputNodes());
        }

        if (intersectionInputPlaces.size() != 0 || intersectionOutputPlaces.size() != 0)
            return false;
        
        if (unionInputPlaces.equals(placeEval.getPlaceInputNodes()) && unionOutputPlaces.equals(placeEval.getPlaceOutputNodes())){
        	System.out.println("Removed "+placeEval+" because of:");
        	for (PlaceEvaluation<N> placeEvaluation : currentCombinationArray) {
				System.out.println(placeEvaluation);
			}
        }

        return (unionInputPlaces.equals(placeEval.getPlaceInputNodes()) && unionOutputPlaces.equals(placeEval.getPlaceOutputNodes()));
    }*/




    
}
