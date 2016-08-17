package org.processmining.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.confs.FuzzyCGConfiguration;
import org.processmining.contexts.uitopia.UIPluginContext;


public class FuzzyCGDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3587595452197538653L;
	@SuppressWarnings("unused")
	private FuzzyCGConfiguration configuration;

	public FuzzyCGDialog(UIPluginContext context, XLog log, 
			final FuzzyCGConfiguration configuration) {
		this.configuration = configuration;

		final JLabel sureThresholdLabel = new JLabel();
		sureThresholdLabel.setText("Sure arc threshold");
		this.add(sureThresholdLabel);

		final JSlider sureThresholdSlider = new JSlider();
		sureThresholdSlider.setMinimum(0);
		sureThresholdSlider.setMaximum(100);
		int intValue = (int) (configuration.getSureThreshold()*100);
		sureThresholdSlider.setValue(intValue);
		sureThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				configuration.setSureThreshold(sureThresholdSlider.getValue());
			}
		});
		this.add(sureThresholdSlider);
		
		final JLabel questionMarkThresholdLabel = new JLabel();
		questionMarkThresholdLabel.setText("Unsure arc threshold");
		this.add(questionMarkThresholdLabel);

		
		final JSlider maybeThresholdSlider = new JSlider();
		maybeThresholdSlider.setMinimum(0);
		maybeThresholdSlider.setMaximum(100);
		intValue = (int) (configuration.getQuestionMarkThreshold()*100);
		maybeThresholdSlider.setValue(intValue);
		maybeThresholdSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				configuration.setQuestionMarkThreshold(maybeThresholdSlider.getValue());
			}
		});
		this.add(maybeThresholdSlider);
	}
}
