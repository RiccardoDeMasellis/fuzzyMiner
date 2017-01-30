package org.processmining.fuzzyminer.plugins;


/**
 * Created by demas on 25/07/16.
 */
public class FuzzyMinerSettings {


    private double prePlaceEvaluationThreshold;
    
    
    private static double PREPLACEEVALUATIONTHRESHOLD = 0.1;
    private static double PLACEEVALTHRESHOLD = 0.7;
    private static int MAXCLUSTERSIZE = 5;

    
    public double getPrePlaceEvaluationThreshold() {
		return prePlaceEvaluationThreshold;
	}

	public void setPrePlaceEvaluationThreshold(double prePlaceEvaluationThreshold) {
		this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
	}

	private double placeEvalThreshold;
    
    private int maxClusterSize; 
    
    public FuzzyMinerSettings(double prePlaceEvalThreshold, double placeEvalThreshold, int maxClusterSize) {
        this.prePlaceEvaluationThreshold = prePlaceEvalThreshold;
        this.placeEvalThreshold = placeEvalThreshold;
        this.maxClusterSize = maxClusterSize;
    }
    
    public FuzzyMinerSettings(){
    	this(PREPLACEEVALUATIONTHRESHOLD, PLACEEVALTHRESHOLD, MAXCLUSTERSIZE);
    }
    
    public double getPlaceEvalThreshold() {
        return placeEvalThreshold;
    }
    
   
    public int getMaxClusterSize() {
		return maxClusterSize;
	}

	public void setPlaceEvalThreshold(double placeEvalThreshold) {
		this.placeEvalThreshold = placeEvalThreshold;
	}
	
	public void setMaxClusterSize(int maxClusterSize) {
		this.maxClusterSize = maxClusterSize;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuzzyMinerSettings that = (FuzzyMinerSettings) o;

        if (Double.compare(that.getPrePlaceEvaluationThreshold(), prePlaceEvaluationThreshold) != 0) return false;       
        if (Double.compare(that.getPlaceEvalThreshold(), placeEvalThreshold) != 0) return false;
        return (Integer.compare(that.getMaxClusterSize(), maxClusterSize) == 0);

        
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = 1;
        temp = Double.doubleToLongBits(getPrePlaceEvaluationThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));        
        temp = Double.doubleToLongBits(getPlaceEvalThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = (new Integer(getMaxClusterSize())).longValue();
        result = 31 * result + (int) (temp ^ (temp >>> 32));        
        return result;
    }
}
