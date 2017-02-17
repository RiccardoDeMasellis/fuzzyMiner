package org.processmining.fuzzyminer.models.causalgraph;

import org.processmining.models.graphbased.AttributeMap;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedUncertainGraphEdge extends FuzzyDirectedGraphEdge {

    public FuzzyDirectedUncertainGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target, String  value) {
        super(source, target);
		getAttributeMap().put(AttributeMap.LABEL, "[?] "+ value);
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, false);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);

    }
}
