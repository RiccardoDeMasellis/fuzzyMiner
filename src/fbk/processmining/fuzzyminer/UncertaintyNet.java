package fbk.processmining.fuzzyminer;

import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.heuristics.impl.HeuristicsNetImpl;

/**
 * Created by demas on 25/07/16.
 */
public class UncertaintyNet extends HeuristicsNetImpl {

    private final HNSet[] uncertainInputSets; //input sets
    private final HNSet[] uncertainOutputSets; // output sets

    public UncertaintyNet(ActivitiesMappingStructures activitiesMappingStructures) {
        super(activitiesMappingStructures);
        int size = activitiesMappingStructures.getActivitiesMapping().length;
        this.uncertainInputSets = new HNSet[size];
        this.uncertainOutputSets = new HNSet[size];
    }

    /*
    The input HeuristicNet is a net that has already been mined!
     */
    public UncertaintyNet(HeuristicsNet hn) {
        this(hn.getActivitiesMappingStructures());
        this.activitiesMappingStructures = hn.getActivitiesMappingStructures();
        int size = activitiesMappingStructures.getActivitiesMapping().length;
        for(int i=0; i<size; i++) {
            this.setInputSet(i, hn.getInputSet(i));
            this.setOutputSet(i, hn.getOutputSet(i));
        }
        this.startActivities = hn.getStartActivities();
        this.endActivities = hn.getEndActivities();
        this.fitness = hn.getFitness();
        this.activitiesActualFiring = hn.getActivitiesActualFiring();
        this.arcUsage = hn.getArcUsage();

        //The uncertainty net is then build in FuzzyMiner.mine()
    }


    public boolean setUncertainInputSet(int index, HNSet sets) {
        return setSet(uncertainInputSets, index, sets);
    }
    public boolean setUncertainOutputSet(int index, HNSet sets) {
        return setSet(uncertainOutputSets, index, sets);
    }


}
