package org.processmining.fuzzyminer.plugins;

import java.util.HashMap;
import java.util.Map;

import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * Created by demas on 25/07/16.
 */
public class FuzzyCGMinerSettings {
	/*
	 * NB DECIDERE SE AGGIUNGERE ANCHE USEALLCONNECTEDHEURISTICS
	 */
	
	

	// Settings of the original HeuristicsMiner
    private HeuristicsMinerSettings hmSettings;
    
    private double positiveObservationDegreeThreshold;
    private boolean useAllConnectedHeuristics;
   
    // Threshold between 0 and 1 above which the causality is considered certain.
    private double sureThreshold;

    /*
        Threshold between 0 and 1 above which the causality is considered uncertain.
        Notice that questionMarkThreshold must be obviously < Threshold.
    */
    private double questionMarkThreshold;
    
    
    private double causalityWeight;
    
    
    private static double POSITIVEOBSERVATIONDEGREE = 0.3;
    private static double SURETHRESHOLD = 0.6;
    private static double QUESTIONMARKTHRESHOLD = 0.5;
    private static double CAUSALITYWEIGHT = 0.5;
    
    private Map<String, Integer> activityFrequencyMap;

    public FuzzyCGMinerSettings(HeuristicsMinerSettings hms, double positiveObservationDegreeThreshold, 
    		double sureThreshold, double questionMarkThreshold, double causalityWeight) {
        this.hmSettings = hms;
        this.positiveObservationDegreeThreshold = positiveObservationDegreeThreshold;
        this.sureThreshold = sureThreshold;
        this.questionMarkThreshold = questionMarkThreshold;
        this.activityFrequencyMap = new HashMap<>();
        this.causalityWeight = causalityWeight;
    }
    
    public FuzzyCGMinerSettings(){
    	this(new HeuristicsMinerSettings(), POSITIVEOBSERVATIONDEGREE, SURETHRESHOLD, QUESTIONMARKTHRESHOLD, CAUSALITYWEIGHT);
    }
    
       
    public double getCausalityWeight() {
		return causalityWeight;
	}

	public void setCausalityWeight(double causalityWeight) {
		this.causalityWeight = causalityWeight;
	}

	public Map<String, Integer> getActivityFrequencyMap() {
    	return activityFrequencyMap;
    }
    
    public void setActivityFrequencyMap(Map<String, Integer> activityFrequencyMap) {
    	this.activityFrequencyMap = activityFrequencyMap;
    }
    

    public HeuristicsMinerSettings getHmSettings() {
        return hmSettings;
    }


	
	public void setHmSettings(HeuristicsMinerSettings hmSettings) {
		this.hmSettings = hmSettings;
	}
	
	public boolean isUseAllConnectedHeuristics() {
		return useAllConnectedHeuristics;
	}

	public void setUseAllConnectedHeuristics(boolean useAllConnectedHeuristics) {
		this.useAllConnectedHeuristics = useAllConnectedHeuristics;
	}

	public double getPositiveObservationDegreeThreshold() {
		return positiveObservationDegreeThreshold;
	}

	public void setPositiveObservationDegreeThreshold(double positiveObservationDegreeThreshold) {
		this.positiveObservationDegreeThreshold = positiveObservationDegreeThreshold;
	}
	
    public double getSureThreshold() {
        return sureThreshold;
    }

    public double getQuestionMarkThreshold() {
        return questionMarkThreshold;
    }

    
	public void setSureThreshold(double sureThreshold) {
		this.sureThreshold = sureThreshold;
	}

	public void setQuestionMarkThreshold(double questionMarkThreshold) {
		this.questionMarkThreshold = questionMarkThreshold;
	}
	

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuzzyCGMinerSettings that = (FuzzyCGMinerSettings) o;
        if (Double.compare(that.getPositiveObservationDegreeThreshold(), getPositiveObservationDegreeThreshold()) != 0) return false;
        if (Double.compare(that.getSureThreshold(), getSureThreshold()) != 0) return false;
        if (Double.compare(that.getQuestionMarkThreshold(), getQuestionMarkThreshold()) != 0) return false;
        if (Double.compare(that.getCausalityWeight(), getCausalityWeight()) != 0) return false;
        return getHmSettings().equals(that.getHmSettings());

        
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getHmSettings().hashCode();
        temp = Double.doubleToLongBits(getPositiveObservationDegreeThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getSureThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));    
        temp = Double.doubleToLongBits(getQuestionMarkThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));  
        temp = Double.doubleToLongBits(getCausalityWeight());
        result = 31 * result + (int) (temp ^ (temp >>> 32));          
        return result;
    }
}
