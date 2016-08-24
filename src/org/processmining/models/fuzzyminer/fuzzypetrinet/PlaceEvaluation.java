package org.processmining.models.fuzzyminer.fuzzypetrinet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

import weka.classifiers.bayes.net.search.fixed.FromFile;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by demas on 22/08/16.
 */
class PlaceEvaluation<E extends AbstractDirectedGraphEdge> {
    private Set<E> placeInputNodes, placeOutputNodes;
    private int acceptedTracesNumber;
    private int currentTokenNumber;
    private XLog log;

    public PlaceEvaluation(Set<E> placeOutputNodes, Set<E> placeInputNodes, XLog log) {
        this.placeOutputNodes = placeOutputNodes;
        this.placeInputNodes = placeInputNodes;
        this.log = log;
        this.acceptedTracesNumber = 0;
        this.currentTokenNumber = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceEvaluation that = (PlaceEvaluation) o;

        if (!getPlaceInputNodes().equals(that.getPlaceInputNodes())) return false;
        if (!getPlaceOutputNodes().equals(that.getPlaceOutputNodes())) return false;
        return log.equals(that.log);

    }

    @Override
    public int hashCode() {
        int result = getPlaceInputNodes().hashCode();
        result = 31 * result + getPlaceOutputNodes().hashCode();
        result = 31 * result + log.hashCode();
        return result;
    }

    public void replayPlace() {
        // for each trace in the log
    	for (XTrace trace : log) {
    		this.replayPlaceOnTrace(trace);
            // update Accepted traces
    		if (isCurrentTokenNumberZero())
    			increaseAcceptedTracesNumber();
    		resetCurrentTokenNumber();
		}


    }
    

    public void replayPlaceOnTrace(XTrace trace){
		for (XEvent event : trace) {
			String eventName = XConceptExtension.instance().extractName(event);
			System.out.println(eventName);

			//if it is input increase
			for (E placeInputNode : getPlaceInputNodes()) {
				if (placeInputNode.getLabel().equalsIgnoreCase(eventName))
					increaseTokenNumber();
			}
	        // if it is output decrease
			for (E placeOutputNode : getPlaceOutputNodes()) {
				if (placeOutputNode.getLabel().equalsIgnoreCase(eventName))
					decreaseTokenNumber();
		        // CHECK: if negative return;
				if (isCurrentTokenNumberNegative())
					return;
			}
		}
    }

    public void increaseAcceptedTracesNumber() {
        acceptedTracesNumber++;
    }


    public Set<E> getPlaceInputNodes() {
        return placeInputNodes;
    }

    public Set<E> getPlaceOutputNodes() {
        return placeOutputNodes;
    }

    public int getAcceptedTracesNumber() {
        return acceptedTracesNumber;
    }

    public double evaluateReplayScore() {
    	return getAcceptedTracesNumber()/log.size();
    }

    public void increaseTokenNumber() {
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
