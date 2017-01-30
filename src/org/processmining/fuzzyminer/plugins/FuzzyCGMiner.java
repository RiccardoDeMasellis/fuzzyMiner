package org.processmining.fuzzyminer.plugins;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.fuzzyminer.algorithms.heuristicminer.HeuristicMinerLight;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyDirectedGraphNode;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;

/**
 * Created by demas on 25/07/16.
 */

public class FuzzyCGMiner  extends HeuristicMinerLight {
	

    public FuzzyCGMiner(XLog log, XLogInfo logInfo, FuzzyCGMinerSettings fCGSettings) {
        super(log, logInfo, fCGSettings.getHmSettings());
    }

    public FuzzyCausalGraph mineFCG(FuzzyCGMinerSettings cGSettings){
        FuzzyCausalGraph fCG = new FuzzyCausalGraph();
        fCG.setLog(log);

        this.keys = new HashMap<String, Integer>();

        // Building activitymappingStructures...
        //System.out.println(logInfo.getEventClasses());
        for (XEventClass event : logInfo.getEventClasses(settings.getClassifier()).getClasses()) {
            this.keys.put(event.getId(), event.getIndex());
        }
        activitiesMappingStructures = new ActivitiesMappingStructures(logInfo.getEventClasses(settings.getClassifier()));
        fCG.setActivityFrequencyMap(cGSettings.getActivityFrequencyMap());
        fCG.setMetrics(metrics);
        fCG.setActivitiesMappingStructures(activitiesMappingStructures);
        fCG.setSettings(cGSettings);
        



        HeuristicsNet originalNet = this.makeBasicRelations(this.getMetrics());

        int eventsNumber = this.getMetrics().getEventsNumber();
        //for(int i=0; i<eventsNumber; i++) {
        for(int i=eventsNumber-1; i>=0; i--) {
            String nodeILabel = activitiesMappingStructures.getActivitiesMapping()[i].getId();
            FuzzyDirectedGraphNode nodeI = null, nodeJ = null;
            nodeI = fCG.addNode(nodeILabel);
            //for (int j=0; j<eventsNumber; j++) {
            for (int j=eventsNumber-1; j>=0; j--) {
                String nodeJLabel = activitiesMappingStructures.getActivitiesMapping()[j].getId();
                nodeJ = fCG.addNode(nodeJLabel);

                double abdependency = metrics.getABdependencyMeasuresAll(i, j);
                double dependencyAccepted = metrics.getDependencyMeasuresAccepted(i, j);
                /*if (dependencyAccepted>=fMSettings.getSureThreshold()){
                    fCG.addSureEdge(nodeI, nodeJ);
                    System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } else if (dependencyAccepted>=fMSettings.getQuestionMarkThreshold()){
                    fCG.addUncertainEdge(nodeI, nodeJ);
                    System.out.println("UNCERTAIN"+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                }*/
                if (abdependency>=cGSettings.getSureThreshold()){
                    fCG.addSureEdge(nodeI, nodeJ);
                    System.out.println("SURE "+nodeI.getLabel()+" -> "+nodeJ.getLabel()+" "+abdependency+" "+dependencyAccepted);
                } else if (abdependency>=cGSettings.getQuestionMarkThreshold()){
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
