package org.processmining.models.fuzzyminer.causalgraph;

import org.processmining.models.graphbased.AttributeMap;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedUncertainGraphEdge extends FuzzyDirectedGraphEdge {

    public FuzzyDirectedUncertainGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target) {
        super(source, target);
		getAttributeMap().put(AttributeMap.LABEL, "?");
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);

    }
}
