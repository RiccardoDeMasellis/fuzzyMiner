package fbk.processmining.fuzzyminer;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import fbk.processmining.plugin.fuzzyminer.FuzzyMinerPlugin;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.heuristics.impl.HNSubSet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;

import java.util.HashMap;

/**
 * Created by demas on 25/07/16.
 */
public class FuzzyMiner extends HeuristicsMiner {

    private DoubleMatrix2D uncertainDependencyMeasuresAccepted;
    HNSubSet[] uncertaintyInputSet, uncertaintyOutputSet;

    public FuzzyMiner(PluginContext context, XLog log, XLogInfo logInfo, FuzzyMinerSettings settings) {
        super(context, log, logInfo, settings.getHmSettings());

        int eventsNumber = this.getMetrics().getEventsNumber();
        uncertainDependencyMeasuresAccepted = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
        uncertaintyInputSet = new HNSubSet[eventsNumber];
        uncertaintyOutputSet = new HNSubSet[eventsNumber];
    }

    public UncertaintyNet mine() {

        this.keys = new HashMap<String, Integer>();

        // Building activitymappingStructures...
        System.out.println(logInfo.getEventClasses());
        for (XEventClass event : logInfo.getEventClasses(settings.getClassifier()).getClasses()) {
            this.keys.put(event.getId(), event.getIndex());
        }
        activitiesMappingStructures = new ActivitiesMappingStructures(logInfo.getEventClasses(settings.getClassifier()));

        HeuristicsNet originalNet = this.makeBasicRelations(this.getMetrics());

        UncertaintyNet result = new UncertaintyNet(originalNet);

        //Now building the uncertainty-specific of the net.

        int eventsNumber = this.getMetrics().getEventsNumber();
        for(int i=0; i<eventsNumber; i++) {
            for (int j=0; j<eventsNumber; j++) {
                double abdependency = metrics.getABdependencyMeasuresAll(i, j);
                double dependencyAccepted = metrics.getDependencyMeasuresAccepted(i, j);
                if (abdependency>= FuzzyMinerPlugin.QUESTIONMARK_THRESHOLD && !(dependencyAccepted>0.0)) {
                    uncertainDependencyMeasuresAccepted.set(i, j, abdependency);
                    this.addUncertaintyInputSet(j, i);
                    this.addUncertaintyOutputSet(i, j);

                    // Add to the net
                    result.setInputSet(i, this.getUncertaintyInputSet(i));
                    result.setOutputSet(i, this.getUncertaintyOutputSet(i));
                }
            }
        }
        return result;
    }

    public void addUncertaintyInputSet(int x, int value){ this.uncertaintyInputSet[x].add(value); }
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
    }
}
