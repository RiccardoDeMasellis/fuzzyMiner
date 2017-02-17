package org.processmining.fuzzyminer.models.causalgraph;

import org.processmining.models.graphbased.AttributeMap;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedSureGraphEdge extends FuzzyDirectedGraphEdge {

    public FuzzyDirectedSureGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target, String value) {
        super(source, target); 
		getAttributeMap().put(AttributeMap.LABEL, value);
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, false);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);        
    }
    
    public FuzzyDirectedSureGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target) {
        super(source, target);
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, false);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);        
    }

}
