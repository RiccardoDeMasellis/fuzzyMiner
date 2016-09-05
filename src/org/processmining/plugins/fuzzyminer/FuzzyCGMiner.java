package org.processmining.plugins.fuzzyminer;

import java.util.HashMap;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.confs.fuzzyminer.FuzzyCGConfiguration;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyCausalGraph;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyDirectedGraphEdge;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyDirectedGraphNode;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyDirectedSureGraphEdge;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.plugins.fuzzyminer.FuzzyMinerSettings;
import org.processmining.plugins.fuzzyminer.heuristicminer.HeuristicMinerLight;

/**
 * Created by demas on 25/07/16.
 */

public class FuzzyCGMiner  extends HeuristicMinerLight {
	

    public FuzzyCGMiner(XLog log, XLogInfo logInfo, FuzzyMinerSettings fMSettings) {
        super(log, logInfo, fMSettings.getHmSettings());
    }

    public FuzzyCausalGraph mineFCG(FuzzyMinerSettings fMSettings){
        FuzzyCausalGraph fCG = new FuzzyCausalGraph();

        this.keys = new HashMap<String, Integer>();

        // Building activitymappingStructures...
        //System.out.println(logInfo.getEventClasses());
        for (XEventClass event : logInfo.getEventClasses(settings.getClassifier()).getClasses()) {
            this.keys.put(event.getId(), event.getIndex());
        }
        activitiesMappingStructures = new ActivitiesMappingStructures(logInfo.getEventClasses(settings.getClassifier()));



        HeuristicsNet originalNet = this.makeBasicRelations(this.getMetrics());

        int eventsNumber = this.getMetrics().getEventsNumber();
        for(int i=0; i<eventsNumber; i++) {
            String nodeILabel = activitiesMappingStructures.getActivitiesMapping()[i].getId();
            FuzzyDirectedGraphNode nodeI = null, nodeJ = null;
            nodeI = fCG.addNode(nodeILabel);
            for (int j=0; j<eventsNumber; j++) {
                String nodeJLabel = activitiesMappingStructures.getActivitiesMapping()[j].getId();
                nodeJ = fCG.addNode(nodeJLabel);

                double abdependency = metrics.getABdependencyMeasuresAll(i, j);
                double dependencyAccepted = metrics.getDependencyMeasuresAccepted(i, j);
/*                if (dependencyAccepted>=fMSettings.getSureThreshold()){
                    fCG.addSureEdge(nodeI, nodeJ);
                    System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } else if (dependencyAccepted>=fMSettings.getQuestionMarkThreshold()){
                    fCG.addUncertainEdge(nodeI, nodeJ);
                    System.out.println("UNCERTAIN"+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                }*/
                if (abdependency>=fMSettings.getSureThreshold()){
                    fCG.addSureEdge(nodeI, nodeJ);
                    System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } else if (abdependency>=fMSettings.getQuestionMarkThreshold()){
                    fCG.addUncertainEdge(nodeI, nodeJ);
                    System.out.println("UNCERTAIN"+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } /*else
                    System.out.println("NOTHING "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);*/
            }
        }

        return fCG;
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
