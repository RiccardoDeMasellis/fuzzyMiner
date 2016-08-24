package org.processmining.models.fuzzyminer.fuzzypetrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 23/08/16.
 */
public class UncertainTransitionsArc extends TransitionsArc {

    public UncertainTransitionsArc(Transition source, Transition target, int weight) {
        super(source, target, weight);
    }

    public UncertainTransitionsArc(Transition source, Transition target) {
        this(source, target, 1);
    }
}
