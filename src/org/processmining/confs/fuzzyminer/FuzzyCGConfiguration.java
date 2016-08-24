package org.processmining.confs.fuzzyminer;

import org.deckfour.xes.model.XLog;


public class FuzzyCGConfiguration {

	private double sureThreshold;
	private double questionMarkThreshold;
	private String sureThresholdLabel;
	private String questionMarkThresholdLabel;
	
	public FuzzyCGConfiguration(XLog log) {
		setSureThreshold(0.9);
		setQuestionMarkThreshold(0.6);
		setSureThresholdLabel("Threshold for sure arcs");
		setQuestionMarkThresholdLabel("Threshold for unsure arcs");

	}

	public double getSureThreshold() {
		return sureThreshold;
	}

	public void setSureThreshold(double sureThreshold) {
		this.sureThreshold = sureThreshold;
	}

	public double getQuestionMarkThreshold() {
		return questionMarkThreshold;
	}

	public void setQuestionMarkThreshold(double questionMarkThreshold) {
		this.questionMarkThreshold = questionMarkThreshold;
	}

	public void setSureThresholdLabel(String sureThresholdLabel) {
		this.sureThresholdLabel = sureThresholdLabel;
	}

	public void setQuestionMarkThresholdLabel(String questionMarkThresholdLabel) {
		this.questionMarkThresholdLabel = questionMarkThresholdLabel;
	}

	public String getSureThresholdLabel() {
		return sureThresholdLabel;
	}
	
	
	

}
