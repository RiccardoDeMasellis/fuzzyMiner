package org.processmining.dialogs.fuzzyminer;

import java.awt.Color;
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
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;


public class FuzzyPNDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	//private FuzzyCGConfiguration configuration;
	private FuzzyMinerSettings settings;

	public FuzzyPNDialog(UIPluginContext context, XLog log, 
			final FuzzyMinerSettings settings) {
		super(new GridLayout(5,1));
		this.settings = settings;
		
		final JLabel positiveObservationsLabel = new JLabel();
		positiveObservationsLabel.setText("Positive observation threshold");
		this.add(positiveObservationsLabel);

		Component space = Box.createHorizontalStrut(10);
		this.add(space);
	
		final JSlider positiveObservationsSlider = new JSlider();
		positiveObservationsSlider.setMinimum(0);
		positiveObservationsSlider.setMaximum(100);
		
		int intValue = (int) (settings.getHmSettings().getPositiveObservationThreshold());
		positiveObservationsSlider.setValue(intValue);
		positiveObservationsSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				HeuristicsMinerSettings hMS= settings.getHmSettings();
				hMS.setPositiveObservationThreshold((positiveObservationsSlider.getValue()));
				settings.setHmSettings(hMS);
			}
		});
		positiveObservationsSlider.setMajorTickSpacing(50);
		positiveObservationsSlider.setMinorTickSpacing(10);
		positiveObservationsSlider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0"));
		labelTable.put(50, new JLabel("50"));
		labelTable.put(100, new JLabel("100"));
		positiveObservationsSlider.setLabelTable(labelTable);
		positiveObservationsSlider.setPaintLabels(true);
		this.add(positiveObservationsSlider);
		
		space = Box.createVerticalStrut(20);
		this.add(space);

		final JLabel sureThresholdLabel = new JLabel();
		sureThresholdLabel.setText("Sure arc threshold");
		this.add(sureThresholdLabel);

		space = Box.createHorizontalStrut(10);
		this.add(space);
		
		final JSlider sureThresholdSlider = new JSlider();
		sureThresholdSlider.setMinimum(0);
		sureThresholdSlider.setMaximum(100);
		
		intValue = (int) (settings.getSureThreshold()*100);
		sureThresholdSlider.setValue(intValue);
		sureThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setSureThreshold(sureThresholdSlider.getValue()/100.0);
			}
		});
		sureThresholdSlider.setMajorTickSpacing(50);
		sureThresholdSlider.setMinorTickSpacing(10);
		sureThresholdSlider.setPaintTicks(true);
		labelTable = new Hashtable<Integer, JLabel>();
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
		questionMarkThresholdSlider.setMinorTickSpacing(10);
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
				settings.setQuestionMarkThreshold(questionMarkThresholdSlider.getValue()/100.0);
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
		placeEvaluationThresholdSlider.setMinorTickSpacing(10);
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
				settings.setPlaceEvalThreshold(placeEvaluationThresholdSlider.getValue()/100.0);
			}
		});
		this.add(placeEvaluationThresholdSlider);
	
		space = Box.createVerticalStrut(20);
		this.add(space);

		final JLabel allConnected = new JLabel();
		allConnected.setText("All tasks connected");
		this.add(allConnected);

		space = Box.createHorizontalStrut(10);
		this.add(space);

		JCheckBox connCbx = new JCheckBox();
		connCbx.setBackground(Color.GRAY);
		connCbx.setSelected(settings.getHmSettings().isUseAllConnectedHeuristics());
		connCbx.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				HeuristicsMinerSettings hMS = settings.getHmSettings();
				hMS.setUseAllConnectedHeuristics(connCbx.isSelected());
				settings.setHmSettings(hMS);
			}
		});
		this.add(connCbx);
	}
}
