package org.processmining.dialogs.fuzzyminer;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.confs.fuzzyminer.FuzzyCGConfiguration;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.plugins.fuzzyminer.FuzzyMinerSettings;


public class FuzzyCGDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	//private FuzzyCGConfiguration configuration;
	private FuzzyMinerSettings settings;

	public FuzzyCGDialog(UIPluginContext context, XLog log, 
			final FuzzyMinerSettings settings) {
		super(new GridLayout(3,1));
		this.settings = settings;

		final JLabel sureThresholdLabel = new JLabel();
		sureThresholdLabel.setText("Sure arc threshold");
		this.add(sureThresholdLabel);

		Component space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JSlider sureThresholdSlider = new JSlider();
		sureThresholdSlider.setMinimum(0);
		sureThresholdSlider.setMaximum(100);
		
		int intValue = (int) (settings.getSureThreshold()*100);
		sureThresholdSlider.setValue(intValue);
		sureThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setSureThreshold(sureThresholdSlider.getValue());
			}
		});
		sureThresholdSlider.setMajorTickSpacing(50);
		sureThresholdSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0%"));
		labelTable.put(50, new JLabel("50%"));
		labelTable.put(100, new JLabel("100%"));
		sureThresholdSlider.setLabelTable(labelTable);
		sureThresholdSlider.setPaintLabels(true);
		this.add(sureThresholdSlider);
		
		space = Box.createHorizontalStrut(20);
		this.add(space);
		
		final JLabel questionMarkThresholdLabel = new JLabel();
		questionMarkThresholdLabel.setText("Unsure arc threshold");
		this.add(questionMarkThresholdLabel);

		space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JSlider questionMarkThresholdSlider = new JSlider();
		questionMarkThresholdSlider.setMinimum(0);
		questionMarkThresholdSlider.setMaximum(100);
		
		questionMarkThresholdSlider.setMajorTickSpacing(50);
		questionMarkThresholdSlider.setPaintTicks(true);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0%"));
		labelTable.put(50, new JLabel("50%"));
		labelTable.put(100, new JLabel("100%"));
		questionMarkThresholdSlider.setLabelTable(labelTable);
		questionMarkThresholdSlider.setPaintLabels(true);
		this.add(questionMarkThresholdSlider);
		
		
		intValue = (int) (settings.getQuestionMarkThreshold()*100);
		questionMarkThresholdSlider.setValue(intValue);
		questionMarkThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setQuestionMarkThreshold(questionMarkThresholdSlider.getValue());
			}
		});
		this.add(questionMarkThresholdSlider);

		intValue = (int) (settings.getQuestionMarkThreshold()*100);
		questionMarkThresholdSlider.setValue(intValue);
		questionMarkThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setQuestionMarkThreshold(questionMarkThresholdSlider.getValue());
			}
		});
		this.add(questionMarkThresholdSlider);

		
		space = Box.createVerticalStrut(20);
		this.add(space);
		
		final JLabel placeEvaluationThresholdLabel = new JLabel();
		placeEvaluationThresholdLabel.setText("Place Evaluation Threshold");
		this.add(placeEvaluationThresholdLabel);

		space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JSlider placeEvaluationThresholdSlider = new JSlider();
		placeEvaluationThresholdSlider.setMinimum(0);
		placeEvaluationThresholdSlider.setMaximum(100);
		
		placeEvaluationThresholdSlider.setMajorTickSpacing(50);
		placeEvaluationThresholdSlider.setPaintTicks(true);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0%"));
		labelTable.put(50, new JLabel("50%"));
		labelTable.put(100, new JLabel("100%"));
		placeEvaluationThresholdSlider.setLabelTable(labelTable);
		placeEvaluationThresholdSlider.setPaintLabels(true);
		this.add(placeEvaluationThresholdSlider);
		
		
		intValue = (int) (settings.getPlaceEvalThreshold()*100);
		placeEvaluationThresholdSlider.setValue(intValue);
		placeEvaluationThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setQuestionMarkThreshold(placeEvaluationThresholdSlider.getValue());
			}
		});
		this.add(placeEvaluationThresholdSlider);
	}
}
