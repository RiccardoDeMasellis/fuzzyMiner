package org.processmining.fuzzyminer.models.causalgraph;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedGraphNode extends AbstractDirectedGraphNode {

    private final FuzzyCausalGraph graph;

    public FuzzyDirectedGraphNode(FuzzyCausalGraph graph) {
    	super();
        this.graph = graph;
    }

    public FuzzyDirectedGraphNode(FuzzyCausalGraph graph, String label) {
        this.graph = graph;
		getAttributeMap().put(AttributeMap.LABEL, label);
    }
    
  
    @Override
    public FuzzyCausalGraph getGraph() {
        return this.graph;
    }

	public String getLabel() {
		return super.getLabel();
	}

	//The equals method is inherited from AbstractGraphNode, and it is based on the id of the node!
    
}
