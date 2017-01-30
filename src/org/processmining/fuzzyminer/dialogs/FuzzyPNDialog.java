package org.processmining.fuzzyminer.dialogs;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.plugins.FuzzyMinerSettings;


public class FuzzyPNDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	//private FuzzyCGConfiguration configuration;
	private FuzzyMinerSettings settings;

	public FuzzyPNDialog(UIPluginContext context, FuzzyCausalGraph fCG, 
			final FuzzyMinerSettings settings) {
		super(new GridLayout(2,1));
		this.settings = settings;
		
		Component space = Box.createHorizontalStrut(10);
		//this.add(space);
		

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
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0%"));
		labelTable.put(50, new JLabel("50%"));
		labelTable.put(100, new JLabel("100%"));
		placeEvaluationThresholdSlider.setLabelTable(labelTable);
		placeEvaluationThresholdSlider.setPaintLabels(true);
		this.add(placeEvaluationThresholdSlider);
		
		int intValue = (int) (settings.getPlaceEvalThreshold()*100);
		placeEvaluationThresholdSlider.setValue(intValue);
		placeEvaluationThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setPlaceEvalThreshold(placeEvaluationThresholdSlider.getValue()/100.0);
			}
		});
		this.add(placeEvaluationThresholdSlider);
	
		space = Box.createVerticalStrut(20);
		this.add(space);
		
		final JLabel maxClusterSizeLabel = new JLabel();
		maxClusterSizeLabel.setText("Max size of clusters");
		this.add(maxClusterSizeLabel);

		space = Box.createHorizontalStrut(10);
		this.add(space);
	
		final JSlider maxClusterSizeSlider = new JSlider();
		
		maxClusterSizeSlider.setMinimum(0);
		maxClusterSizeSlider.setMaximum(30);
		
		intValue = settings.getMaxClusterSize();
		maxClusterSizeSlider.setValue(intValue);
		maxClusterSizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setMaxClusterSize(maxClusterSizeSlider.getValue());
			}
		});
		maxClusterSizeSlider.setMajorTickSpacing(5);
		maxClusterSizeSlider.setMinorTickSpacing(1);
		maxClusterSizeSlider.setPaintTicks(true);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0"));
		labelTable.put(15, new JLabel("15"));
		labelTable.put(30, new JLabel("30"));
		maxClusterSizeSlider.setLabelTable(labelTable);
		maxClusterSizeSlider.setPaintLabels(true);
		this.add(maxClusterSizeSlider);
		
		space = Box.createVerticalStrut(20);
		this.add(space);
		

		/*final JLabel allConnected = new JLabel();
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
				//hMS.setUseAllConnectedHeuristics(connCbx.isSelected());
				settings.setHmSettings(hMS);
			}
		});
		this.add(connCbx);*/
	}
	

}
