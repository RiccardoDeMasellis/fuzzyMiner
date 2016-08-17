package org.processmining.models.causalgraph;

import java.util.Set;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyCausalGraph extends CausalGraph<FuzzyDirectedGraphNode, FuzzyDirectedGraphEdge> {
	
	public void addNode(FuzzyDirectedGraphNode node){
		super.addNode(node);
	}
	
	public void addSureEdge (FuzzyDirectedGraphNode sourceNode, FuzzyDirectedGraphNode targetNode){
		FuzzyDirectedSureGraphEdge sureEdge = new FuzzyDirectedSureGraphEdge(sourceNode, targetNode);
		
		super.addEdge(sureEdge);
	}
	
	public void addUncertainEdge (FuzzyDirectedGraphNode sourceNode, FuzzyDirectedGraphNode targetNode){
		FuzzyDirectedUncertainGraphEdge sureEdge = new FuzzyDirectedUncertainGraphEdge(sourceNode, targetNode);
		super.addEdge(sureEdge);
	}
	
	public FuzzyDirectedGraphNode getNode(String label){
		FuzzyDirectedGraphNode searchedNode = null;
		for (FuzzyDirectedGraphNode node : getNodes()) {
			if (node.getLabel().equals(label))
				searchedNode = node;
		}
		return searchedNode;
	}

}
