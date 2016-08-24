package org.processmining.models.fuzzyminer.causalgraph;

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

	@Override
	public String toString() {
		String graphString = "*** GRAPH *** "+this.getLabel()+"\n";
        Set<FuzzyDirectedGraphNode> nodes  = this.getNodes();
        graphString+= "** NODES **\n";
        for (FuzzyDirectedGraphNode node : nodes) {
        	graphString+= node.getId()+" "+node.getLabel()+"\n";
		}
        graphString+= "** EDGES **";
        Set<FuzzyDirectedGraphEdge> edges = this.getEdges();
        for (FuzzyDirectedGraphEdge edge : edges) {
        	if (edge instanceof FuzzyDirectedSureGraphEdge)
        		graphString+= "SURE EDGE "+edge.getSource().getLabel()+" "+edge.getTarget().getLabel()+" "+edge.getLabel()+"\n";
        	else
        		graphString+= "UNSURE EDGE "+edge.getSource().getLabel()+" "+edge.getTarget().getLabel()+" "+edge.getLabel()+"\n";
			
		}
        return graphString;
	}

	
	
}
