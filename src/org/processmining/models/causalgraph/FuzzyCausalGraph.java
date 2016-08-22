package org.processmining.models.causalgraph;


import org.processmining.models.graphbased.directed.DirectedGraphNode;

import java.util.Collection;
import java.util.HashSet;
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

	//Get node by label. WARNING: the label is not a key! We should define this better!
	public FuzzyDirectedGraphNode getNode(String label){
		FuzzyDirectedGraphNode searchedNode = null;
		for (FuzzyDirectedGraphNode node : getNodes()) {
			if (node.getLabel().equals(label))
				searchedNode = node;
		}
		return searchedNode;
	}

	public Set<FuzzyDirectedSureGraphEdge> getSureEdges() {
		HashSet<FuzzyDirectedSureGraphEdge> result = new HashSet<>();
		for (DirectedGraphNode node : this.getNodes()) {
			Collection<FuzzyDirectedGraphEdge> inOutEdges = this.getInEdges(node);
			inOutEdges.addAll(this.getOutEdges(node));
			for (FuzzyDirectedGraphEdge edge : inOutEdges) {
				if (edge instanceof FuzzyDirectedSureGraphEdge)
					result.add((FuzzyDirectedSureGraphEdge) edge);
			}
		}
		return result;
	}

}
