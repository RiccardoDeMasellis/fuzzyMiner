package org.processmining.fuzzyminer.models.causalgraph;

import java.awt.geom.Point2D;

import org.jgraph.graph.GraphConstants;
import org.processmining.models.graphbased.AttributeMap;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedUncertainGraphEdge extends FuzzyDirectedGraphEdge {

    public FuzzyDirectedUncertainGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target, double value1, double value2) {
        super(source, target);
		getAttributeMap().put(AttributeMap.LABEL, "?");
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, false);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		String[] labels = {String.valueOf(value1), String.valueOf(value2)};
		Point2D[] labelPositions = { 
				new Point2D.Double (GraphConstants.PERMILLE/8, -10),
				new Point2D.Double (GraphConstants.PERMILLE*7/8, -10)  }; 
		getAttributeMap().put(AttributeMap.EXTRALABELS, labels);
		getAttributeMap().put(AttributeMap.EXTRALABELPOSITIONS, labelPositions);


    }
	
	
    public FuzzyDirectedUncertainGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target) {
        super(source, target);
		getAttributeMap().put(AttributeMap.LABEL, "?");
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, false);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);

    }
}
