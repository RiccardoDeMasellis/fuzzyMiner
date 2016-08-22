package org.processmining.plugins.fuzzyminer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.confs.FuzzyCGConfiguration;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.fuzzyminer.FuzzyMinerSettings;
import org.processmining.models.causalgraph.FuzzyCausalGraph;
import org.processmining.models.causalgraph.FuzzyDirectedGraphEdge;
import org.processmining.models.causalgraph.FuzzyDirectedGraphNode;
import org.processmining.models.causalgraph.FuzzyDirectedSureGraphEdge;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;

import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;

/**
 * Created by demas on 25/07/16.
 */

public class FuzzyCGMiner  extends HeuristicsMiner {

    /*private DoubleMatrix2D uncertainDependencyMeasuresAccepted;
    HNSubSet[] uncertaintyInputSet, uncertaintyOutputSet;*/

    public FuzzyCGMiner(PluginContext context, XLog log, XLogInfo logInfo, FuzzyMinerSettings settings) {
        super(context, log, logInfo, settings.getHmSettings());

        int eventsNumber = this.getMetrics().getEventsNumber();
        /*uncertainDependencyMeasuresAccepted = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        uncertaintyInputSet = new HNSubSet[eventsNumber];
        uncertaintyOutputSet = new HNSubSet[eventsNumber];*/
    }  
	
	public FuzzyCausalGraph mineFCG(XLog log, FuzzyCGConfiguration configuration){
		FuzzyCausalGraph fCG = new FuzzyCausalGraph();
		
        this.keys = new HashMap<String, Integer>();

        // Building activitymappingStructures...
        System.out.println(logInfo.getEventClasses());
        for (XEventClass event : logInfo.getEventClasses(settings.getClassifier()).getClasses()) {
            this.keys.put(event.getId(), event.getIndex());
        }
        activitiesMappingStructures = new ActivitiesMappingStructures(logInfo.getEventClasses(settings.getClassifier()));
        
        

        HeuristicsNet originalNet = this.makeBasicRelations(this.getMetrics());

        int eventsNumber = this.getMetrics().getEventsNumber();
        for(int i=0; i<eventsNumber; i++) {
        	String nodeILabel = activitiesMappingStructures.getActivitiesMapping()[i].getId();
        	FuzzyDirectedGraphNode nodeI = null, nodeJ = null;
        	if ((nodeI=fCG.getNode(nodeILabel))==null){
	        	System.out.println(nodeILabel);
	        	nodeI = new FuzzyDirectedGraphNode(fCG, nodeILabel);
	        	fCG.addNode(nodeI);
        	}
            for (int j=0; j<eventsNumber; j++) {
            	String nodeJLabel = activitiesMappingStructures.getActivitiesMapping()[j].getId();
            	if ((nodeJ=fCG.getNode(nodeJLabel))==null){
            		System.out.println(nodeJLabel);
            		nodeJ = new FuzzyDirectedGraphNode(fCG, nodeJLabel);
            		fCG.addNode(nodeJ);
            	}

                double abdependency = metrics.getABdependencyMeasuresAll(i, j);
                double dependencyAccepted = metrics.getDependencyMeasuresAccepted(i, j);
                if (abdependency>=configuration.getSureThreshold()){
                	fCG.addSureEdge(nodeI, nodeJ);
                    System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } else if (abdependency>=configuration.getQuestionMarkThreshold()){
                	fCG.addUncertainEdge(nodeI, nodeJ);
                    System.out.println("UNCERTAIN"+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } else
                    System.out.println("NOTHING "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                
                /*if (abdependency>= FuzzyMinerPlugin.QUESTIONMARK_THRESHOLD && !(dependencyAccepted>0.0)) {
                    uncertainDependencyMeasuresAccepted.set(i, j, abdependency);
                    this.addUncertaintyInputSet(j, i);
                    this.addUncertaintyOutputSet(i, j);

                    // Add to the net
                    result.setInputSet(i, this.getUncertaintyInputSet(i));
                    result.setOutputSet(i, this.getUncertaintyOutputSet(i));
                }*/
            }
        }
        printGraph(fCG);
		
		return fCG;
	}
	
	public void printGraph (FuzzyCausalGraph graph){
        Set<FuzzyDirectedGraphNode> nodes  = graph.getNodes();
        for (FuzzyDirectedGraphNode node : nodes) {
			System.out.println(node.getId()+" "+node.getLabel());
		}
        System.out.println ("** EDGES **");
        Set<FuzzyDirectedGraphEdge> edges = graph.getEdges();
        for (FuzzyDirectedGraphEdge edge : edges) {
        	if (edge instanceof FuzzyDirectedSureGraphEdge)
        		System.out.println("SURE EDGE "+edge.getSource().getLabel()+" "+edge.getTarget().getLabel()+" "+edge.getLabel());
        	else
        		System.out.println("UNSURE EDGE "+edge.getSource().getLabel()+" "+edge.getTarget().getLabel()+" "+edge.getLabel());
			
		}

	}
	
	

   /* public void addUncertaintyInputSet(int x, int value){ this.uncertaintyInputSet[x].add(value); }
    public void addUncertaintyOutputSet(int x, int value){ this.uncertaintyOutputSet[x].add(value); }

    public HNSet getUncertaintyInputSet(int x){
        HNSet inputH = new HNSet();
        inputH.add(this.uncertaintyInputSet[x]);
        return inputH;
    }
    public HNSet getUncertaintyOutputSet(int x) {
        HNSet outputH = new HNSet();
        outputH.add(this.uncertaintyOutputSet[x]);
        return outputH;
    }*/

}
