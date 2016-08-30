package org.processmining.models.fuzzyminer.causalgraph;

import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

/**
 * Created by demas on 27/07/16.
 */
public abstract class FuzzyDirectedGraphEdge extends AbstractDirectedGraphEdge<FuzzyDirectedGraphNode, FuzzyDirectedGraphNode> {

    public FuzzyDirectedGraphEdge(FuzzyDirectedGraphNode source, FuzzyDirectedGraphNode target) {
        super(source, target);
    }
    
}
