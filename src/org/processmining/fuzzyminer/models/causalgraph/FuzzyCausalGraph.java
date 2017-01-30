package org.processmining.fuzzyminer.models.causalgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.fuzzyminer.plugins.FuzzyCGMinerSettings;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMetrics;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyCausalGraph extends CausalGraph<FuzzyDirectedGraphNode, FuzzyDirectedGraphEdge> {
	private Map<String, FuzzyDirectedGraphNode> labelNodeMap;
	private Map<String, Integer> activityFrequencyMap;
	private XLog log;
	private ActivitiesMappingStructures activitiesMappingStructures;
	private HeuristicsMetrics metrics;
	private FuzzyCGMinerSettings settings;
	
	public FuzzyCausalGraph() {
		super();
		this.labelNodeMap = new HashMap<>();
		this.activityFrequencyMap = new HashMap<>();
		this.log = null;
	}
	
	public Map<String, Integer> getActivityFrequencyMap() {
		return activityFrequencyMap;
	}

	public void setActivityFrequencyMap(Map<String, Integer> activityFrequencyMap) {
		this.activityFrequencyMap = activityFrequencyMap;
	}
	
	public XLog getLog() {
		return log;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	public FuzzyCGMinerSettings getSettings() {
		return settings;
	}

	public void setSettings(FuzzyCGMinerSettings settings) {
		this.settings = settings;
	}
	
	public ActivitiesMappingStructures getActivitiesMappingStructures() {
		return activitiesMappingStructures;
	}

	public void setActivitiesMappingStructures(ActivitiesMappingStructures activitiesMappingStructures) {
		this.activitiesMappingStructures = activitiesMappingStructures;
	}

	public HeuristicsMetrics getMetrics() {
		return metrics;
	}

	public void setMetrics(HeuristicsMetrics metrics) {
		this.metrics = metrics;
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
		for (FuzzyDirectedGraphEdge edge : this.getEdges()) {
			if (edge instanceof FuzzyDirectedSureGraphEdge)
				result.add((FuzzyDirectedSureGraphEdge)edge);
		}
		return result;
	}

	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		FuzzyCausalGraph newFCG = new FuzzyCausalGraph();
		for (FuzzyDirectedGraphNode node : this.getNodes()) {
			newFCG.addNode(node);
		}
	 	for (FuzzyDirectedGraphEdge edge : this.getEdges()) {
			newFCG.addEdge(edge);
		}
		return newFCG;
	}

	
	/**
	 * It empties the graph associated to this fCG
	 */
	public void emptyGraph() {
		FuzzyCausalGraph clonedGraph;
		try {
			clonedGraph = (FuzzyCausalGraph) this.clone();
			for (FuzzyDirectedGraphNode node : clonedGraph.getNodes()) {
				this.removeNode(node);
			}
			this.labelNodeMap = new HashMap<>();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public String toString() {
		String graphString = "*** GRAPH *** "+this.getLabel()+"\n";
        Set<FuzzyDirectedGraphNode> nodes  = this.getNodes();
        graphString+= "** NODES **\n";
        for (FuzzyDirectedGraphNode node : nodes) {
        	graphString+= node.getId()+" "+node.getLabel()+"\n";
		}
        graphString+= "** EDGES **\n";
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
