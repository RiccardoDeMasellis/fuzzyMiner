package org.processmining.models.fuzzypetrinet;

import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 28/07/16.
 */
public abstract class TransitionsArc extends Arc {

    public TransitionsArc(Transition source, Transition target, int weight) {
        super(source, target, weight);
    }

    public TransitionsArc(Transition source, Transition target) {
        this(source, target, 1);
    }
}
