package org.processmining.fuzzyminer.plugins;


/**
 * Created by demas on 25/07/16.
 */
public class FuzzyMinerSettings {


    private double prePlaceEvaluationThreshold;

	private double placeEvalThreshold;
    
	private int maxeEdgeClusterSize; 
    private int maxClusterSize; 
	private boolean maxClusterSizeEnabled;

    
    private static double PREPLACEEVALUATIONTHRESHOLD = 0.1;
    private static double PLACEEVALTHRESHOLD = 0.7;
    private static int MAXEDGECLUSTERSIZE = 6;
    private static int MAXCLUSTERSIZE = 10000;
    private static boolean MAXCLUSTERSIZEENABLED = false;

    
    public double getPrePlaceEvaluationThreshold() {
		return prePlaceEvaluationThreshold;
	}

	public void setPrePlaceEvaluationThreshold(double prePlaceEvaluationThreshold) {
		this.prePlaceEvaluationThreshold = prePlaceEvaluationThreshold;
	}

    
    public FuzzyMinerSettings(double prePlaceEvalThreshold, double placeEvalThreshold, boolean maxClusterSizeEnabled) {
        this.prePlaceEvaluationThreshold = prePlaceEvalThreshold;
        this.placeEvalThreshold = placeEvalThreshold;
        this.maxClusterSizeEnabled = maxClusterSizeEnabled;
        this.maxClusterSize = MAXCLUSTERSIZE;
        this.maxeEdgeClusterSize = MAXEDGECLUSTERSIZE;
    }
    
    public FuzzyMinerSettings(){
    	this(PREPLACEEVALUATIONTHRESHOLD, PLACEEVALTHRESHOLD, MAXCLUSTERSIZEENABLED);
    }
    
    public double getPlaceEvalThreshold() {
        return placeEvalThreshold;
    }
    
   
    public boolean isMaxClusterSizeEnabled() {
		return maxClusterSizeEnabled;
	}

	public void setPlaceEvalThreshold(double placeEvalThreshold) {
		this.placeEvalThreshold = placeEvalThreshold;
	}
	
	public void setMaxClusterSizeEnabled(boolean maxClusterSizeEnabled) {
		this.maxClusterSizeEnabled = maxClusterSizeEnabled;
	}
	
    public int getMaxeEdgeClusterSize() {
		return maxeEdgeClusterSize;
	}

	public int getMaxClusterSize() {
		return maxClusterSize;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuzzyMinerSettings that = (FuzzyMinerSettings) o;

        if (Double.compare(that.getPrePlaceEvaluationThreshold(), prePlaceEvaluationThreshold) != 0) return false;       
        if (Double.compare(that.getPlaceEvalThreshold(), placeEvalThreshold) != 0) return false;
        return (Boolean.compare(that.isMaxClusterSizeEnabled(), maxClusterSizeEnabled) == 0);

        
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
        temp = isMaxClusterSizeEnabled()? 1231:1237;
        result = 31 * result + (int) (temp ^ (temp >>> 32));        
        return result;
    }
}
