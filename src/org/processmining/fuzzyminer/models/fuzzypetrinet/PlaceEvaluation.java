package org.processmining.fuzzyminer.models.fuzzypetrinet;

import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 22/08/16.
 */
public class PlaceEvaluation<N extends AbstractDirectedGraphNode> implements Runnable {
    private Set<N> placeOutputNodes, placeInputNodes;
    private int acceptedTracesNumber;
    private int currentTokenNumber;
    private XLog log;
    private Map<String, Integer> activityFrequencyMap;
    double prePlaceEvaluationThreshold;
    //boolean outputOccurred;

    public PlaceEvaluation(Set<N> placeOutputNodes, Set<N> placeInputNodes, XLog log, Map<String, Integer> activityFrequencyMap, double prePlaceEvaluationThreshold) {
        this.placeOutputNodes = placeOutputNodes;
        this.placeInputNodes = placeInputNodes;
        this.log = log;
        this.acceptedTracesNumber = 0;
        this.currentTokenNumber = 0;
        this.activityFrequencyMap = activityFrequencyMap;
        this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
        //this.outputOccurred = false;
    }
    
    /*
     * NEW hashCode and equals based only on the input and outputSet!
     */
    public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((placeInputNodes == null) ? 0 : placeInputNodes.hashCode());
		result = prime * result + ((placeOutputNodes == null) ? 0 : placeOutputNodes.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlaceEvaluation other = (PlaceEvaluation) obj;
		if (placeInputNodes == null) {
			if (other.placeInputNodes != null)
				return false;
		} else if (!placeInputNodes.equals(other.placeInputNodes))
			return false;
		if (placeOutputNodes == null) {
			if (other.placeOutputNodes != null)
				return false;
		} else if (!placeOutputNodes.equals(other.placeOutputNodes))
			return false;
		return true;
	}
    

/*
 * OLD hashCode and equals() based on all instanceVariable.
 */
/*    public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + acceptedTracesNumber;
		result = prime * result + ((activityFrequencyMap == null) ? 0 : activityFrequencyMap.hashCode());
		result = prime * result + currentTokenNumber;
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		result = prime * result + ((placeInputNodes == null) ? 0 : placeInputNodes.hashCode());
		result = prime * result + ((placeOutputNodes == null) ? 0 : placeOutputNodes.hashCode());
		long temp;
		temp = Double.doubleToLongBits(prePlaceEvaluationThreshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}



	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlaceEvaluation other = (PlaceEvaluation) obj;
		if (acceptedTracesNumber != other.acceptedTracesNumber)
			return false;
		if (activityFrequencyMap == null) {
			if (other.activityFrequencyMap != null)
				return false;
		} else if (!activityFrequencyMap.equals(other.activityFrequencyMap))
			return false;
		if (currentTokenNumber != other.currentTokenNumber)
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		if (placeInputNodes == null) {
			if (other.placeInputNodes != null)
				return false;
		} else if (!placeInputNodes.equals(other.placeInputNodes))
			return false;
		if (placeOutputNodes == null) {
			if (other.placeOutputNodes != null)
				return false;
		} else if (!placeOutputNodes.equals(other.placeOutputNodes))
			return false;
		if (Double.doubleToLongBits(prePlaceEvaluationThreshold) != Double
				.doubleToLongBits(other.prePlaceEvaluationThreshold))
			return false;
		return true;
	}*/


	@Override
    public String toString() {
        return "PlaceEvaluation{" +
                "placeInputNodes=" + placeInputNodes +
                ", placeOutputNodes=" + placeOutputNodes +
                '}';
    }

  

	/*
     * This methods is for optimizing large cluster. It pre-evaluates a specific inputSet and outputSet set and analyzes if
     * it is a good candidate if:
     * 				AbsoluteValue[Sum_i(InputSet(i)) - Sum_j(OutputSet(j))] / Sum_i(InputSet(i)) + Sum_j(OutputSet(j))  <  prePlaceEvaluationThreshold
     */
    public boolean preEvaluate() {
    	double inputSum = 0;
    	double outputSum = 0;
    	for (N node : this.placeInputNodes)
    		inputSum += this.activityFrequencyMap.get(node.getLabel());
    	for (N node : this.placeOutputNodes)
    		outputSum += this.activityFrequencyMap.get(node.getLabel());
    	double divisor = Math.abs(inputSum - outputSum);
    	double dividend = inputSum + outputSum;
    	//System.out.println("Divisor/dividend:" + divisor/dividend);
    	return ((divisor/dividend) < this.prePlaceEvaluationThreshold);
    }
    

    public void replayPlace() {
        // for each trace in the log
    	for (XTrace trace : log) {
    		//this.outputOccurred = false;
    		this.replayPlaceOnTrace(trace);
            // update Accepted traces
    		//if (isCurrentTokenNumberZero() || this.outputOccurred==false)
    		if (isCurrentTokenNumberZero())
    			increaseAcceptedTracesNumber();
    		resetCurrentTokenNumber();
    	}
    }

    public void run() {
    	//if (preEvaluate())
    		replayPlace();
    }
    

   /* public void replayPlaceOnTrace(XTrace trace){

		for (XEvent event : trace) {
			String eventName = XConceptExtension.instance().extractName(event);
			boolean isInput=false, isOutput = false;
			for (N placeOutputNode : getPlaceOutputNodes()) {
				if (placeOutputNode.getLabel().equalsIgnoreCase(eventName))
					isOutput = true;
			}
			for (N placeInputNode : getPlaceInputNodes()) {
				if (placeInputNode.getLabel().equalsIgnoreCase(eventName))
					isInput=true;
			}
			//if it is both input and output: be conservative, i.e., 
			//if we have at least a token, we decrease the token number and return it
			// otherwise, we increase the token number
			if (isInput && isOutput){
				decreaseTokenNumber();
			} else{
				//if it is output increase
				if (isOutput)
					increaseTokenNumber();
				 // if it is input decrease
				if (isInput){
					decreaseTokenNumber();
			        // CHECK: if negative return;
					if (isCurrentTokenNumberNegative())
						return;
				}
					
			}
			
		}
    }*/
    
    public void replayPlaceOnTrace(XTrace trace){

		for (XEvent event : trace) {
			String eventName = XConceptExtension.instance().extractName(event);
			StandardModel eventTransition = XLifecycleExtension.instance().extractStandardTransition(event);
			if (eventTransition==null || eventTransition.equals(XLifecycleExtension.StandardModel.COMPLETE)){
			
				boolean isInput=false, isOutput = false;
				for (N placeOutputNode : getPlaceOutputNodes()) {
					if (placeOutputNode.getLabel().equalsIgnoreCase(eventName))
						isOutput = true;
				}
				for (N placeInputNode : getPlaceInputNodes()) {
					if (placeInputNode.getLabel().equalsIgnoreCase(eventName))
						isInput=true;
				}
				//if it is both input and output: be conservative, i.e., 
				//if we have at least a token, we decrease the token number and return it
				// otherwise, we increase the token number
//				if (isInput && isOutput){
//					if (this.currentTokenNumber>0)
//						decreaseTokenNumber();
//					else 
//						increaseTokenNumber();
//				} else{
				
					//if it is output increase
					if (isInput){
						decreaseTokenNumber();
				        // CHECK: if negative return;
						if (isCurrentTokenNumberNegative())
							return;
					}
					if (isOutput)
						increaseTokenNumber();
					 // if it is input decrease
				//}
			}
			
		}
    }

    public void increaseAcceptedTracesNumber() {
        acceptedTracesNumber++;
    }


    public Set<N> getPlaceInputNodes() {
        return placeInputNodes;
    }

    public Set<N> getPlaceOutputNodes() {
        return placeOutputNodes;
    }

    public int getAcceptedTracesNumber() {
        return acceptedTracesNumber;
    }

    public double evaluateReplayScore() {
        double acceptedTraces = new Double(this.getAcceptedTracesNumber());
        double logS = new Double(log.size());
    	return acceptedTraces/logS;
    }

    public void increaseTokenNumber() {
    	//this.outputOccurred = true;
        this.currentTokenNumber++;
    }

    public void decreaseTokenNumber() {
        this.currentTokenNumber--;
    }
    
    public boolean isCurrentTokenNumberNegative(){
    	return currentTokenNumber<0;
    }
    
    public boolean isCurrentTokenNumberZero(){
    	return currentTokenNumber==0;
    }
    
    public void resetCurrentTokenNumber(){
    	currentTokenNumber = 0;
    }

}
