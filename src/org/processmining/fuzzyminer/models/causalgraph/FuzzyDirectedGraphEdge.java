package org.processmining.fuzzyminer.models.causalgraph;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

/**
 * Created by demas on 27/07/16.
 */
public abstract class FuzzyDirectedGraphEdge extends AbstractDirectedGraphEdge<FuzzyDirectedGraphNode, FuzzyDirectedGraphNode> {

    public FuzzyDirectedGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target) {
        super(source, target);
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_TECHNICAL);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
    }
    
}
