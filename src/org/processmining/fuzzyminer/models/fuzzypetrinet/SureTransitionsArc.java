package org.processmining.fuzzyminer.models.fuzzypetrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 23/08/16.
 */
public class SureTransitionsArc extends TransitionsArc {

    public SureTransitionsArc(Transition source, Transition target, int weight) {
        super(source, target, weight);
    }

    public SureTransitionsArc(Transition source, Transition target) {
        this(source, target, 1);
    }


}
