package org.processmining.fuzzyminer.models.fuzzypetrinet;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Created by demas on 23/08/16.
 */
public class UncertainTransitionsArc extends TransitionsArc {

    public UncertainTransitionsArc(Transition source, Transition target, int weight) {
        super(source, target, weight);
		getAttributeMap().put(AttributeMap.LABEL, "?");
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
    }

    public UncertainTransitionsArc(Transition source, Transition target) {
        this(source, target, 1);
    }
}
