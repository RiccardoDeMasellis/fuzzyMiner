package org.processmining.models.fuzzypetrinet;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

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

        //  for each event in the trace
        //if it is input inrease
        // otherwise decrease

        // CHECK: if negative return;

        // update Accepted traces
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
        //// TODO: 22/08/16
    }

    public void increaseTokenNumber() {
        this.currentTokenNumber++;
    }

    public void decreaseTokenNumber() {
        this.currentTokenNumber--;
    }

}
