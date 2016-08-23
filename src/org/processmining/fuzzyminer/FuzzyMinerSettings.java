package org.processmining.fuzzyminer;

import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * Created by demas on 25/07/16.
 */
public class FuzzyMinerSettings {

    private HeuristicsMinerSettings hmSettings;

    // Threshold between 0 and 1 above which the causality is considered certain.
    private double sureThreshold;

    /*
        Threshold between 0 and sureThreshold above which the causality is considered uncertain.
        Notice that questionMarkThreshold must be obviously < Threshold.
    */
    private double questionMarkThreshold;

    private double placeEvalThreshold;

    public FuzzyMinerSettings(HeuristicsMinerSettings hms, double sureThreshold, double questionMarkThreshold, double placeEvalThreshold) {
        this.hmSettings = hms;
        this.sureThreshold = sureThreshold;
        this.questionMarkThreshold = questionMarkThreshold;
        this.placeEvalThreshold = placeEvalThreshold;
    }

    public HeuristicsMinerSettings getHmSettings() {
        return hmSettings;
    }

    public double getSureThreshold() {
        return sureThreshold;
    }

    public double getQuestionMarkThreshold() {
        return questionMarkThreshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FuzzyMinerSettings that = (FuzzyMinerSettings) o;

        if (Double.compare(that.getSureThreshold(), getSureThreshold()) != 0) return false;
        if (Double.compare(that.getQuestionMarkThreshold(), getQuestionMarkThreshold()) != 0) return false;
        if (Double.compare(that.placeEvalThreshold, placeEvalThreshold) != 0) return false;
        return getHmSettings().equals(that.getHmSettings());

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getHmSettings().hashCode();
        temp = Double.doubleToLongBits(getSureThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getQuestionMarkThreshold());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(placeEvalThreshold);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
