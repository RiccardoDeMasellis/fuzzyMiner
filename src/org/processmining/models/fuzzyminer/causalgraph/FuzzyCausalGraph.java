package org.processmining.models.fuzzyminer.causalgraph;

import org.processmining.models.graphbased.directed.DirectedGraphNode;

import java.util.*;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyCausalGraph extends CausalGraph<FuzzyDirectedGraphNode, FuzzyDirectedGraphEdge> {
	private Map<String, FuzzyDirectedGraphNode> labelNodeMap;

	public FuzzyCausalGraph() {
		super();
		this.labelNodeMap = new HashMap<>();
	}

	/**
	 * We build fuzzyCausalGraph with a strong limitation: we do not ever have two nodes with the same
	 * label.
	 * @param nodeLabel the node to be added
	 * @return a new node with the specified label if there is no other node in the graph with the same label,
	 * otherwise the already existing node.
	 */
	public FuzzyDirectedGraphNode addNode(String nodeLabel) {
		FuzzyDirectedGraphNode alreadyPresent = this.labelNodeMap.get(nodeLabel);
		if (alreadyPresent == null) {
			FuzzyDirectedGraphNode node = new FuzzyDirectedGraphNode(this, nodeLabel);
			super.addNode(node);
			labelNodeMap.put(node.getLabel(), node);
			return node;
		}
		else
			return alreadyPresent;
	}
	
	public void addSureEdge(FuzzyDirectedGraphNode sourceNode, FuzzyDirectedGraphNode targetNode){
		FuzzyDirectedSureGraphEdge sureEdge = new FuzzyDirectedSureGraphEdge(sourceNode, targetNode);
		super.addEdge(sureEdge);
	}
	
	public void addUncertainEdge(FuzzyDirectedGraphNode sourceNode, FuzzyDirectedGraphNode targetNode){
		FuzzyDirectedUncertainGraphEdge sureEdge = new FuzzyDirectedUncertainGraphEdge(sourceNode, targetNode);
		super.addEdge(sureEdge);
	}


	public FuzzyDirectedGraphNode getNode(String label){
		return this.labelNodeMap.get(label);
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
