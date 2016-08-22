package org.processmining.models.causalgraph;

import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedGraphNode extends AbstractDirectedGraphNode {

    private final FuzzyCausalGraph graph;
    private final String label;

    public FuzzyDirectedGraphNode(FuzzyCausalGraph graph) {
        this.graph = graph;
        this.label = "";
    }

    public FuzzyDirectedGraphNode(FuzzyCausalGraph graph, String label) {
        this.graph = graph;
        this.label = label;
    }
    
    @Override
    public FuzzyCausalGraph getGraph() {
        return this.graph;
    }

	public String getLabel() {
		return label;
	}

	//The equals method is inherited from AbstractGraphNode, and it is based on the id of the node!
    
}
