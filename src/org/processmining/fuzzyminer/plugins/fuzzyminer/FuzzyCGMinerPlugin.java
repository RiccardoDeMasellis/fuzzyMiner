package org.processmining.fuzzyminer.plugins.fuzzyminer;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.confs.FuzzyCGConfiguration;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogs.FuzzyCGDialog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.impl.ProgressBarImpl;
import org.processmining.fuzzyminer.FuzzyCGMiner;
import org.processmining.fuzzyminer.FuzzyMinerSettings;
import org.processmining.models.causalgraph.FuzzyCausalGraph;
import org.processmining.models.causalgraph.gui.FuzzyCausalGraphVisualization;
import org.processmining.models.causalgraph.gui.FuzzyCausalGraphVisualizer;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Fuzzy Causal Graph Miner", parameterLabels = { "log", "Fuzzy Causal Graph Configuration" }, 
	    returnLabels = { "Fuzzy Causal Graph" }, returnTypes = { FuzzyCausalGraph.class })
public class FuzzyCGMinerPlugin {

	private FuzzyCausalGraph privateFCGMinerPlugin(PluginContext context, XLog log, FuzzyCGConfiguration configuration) {
	    //return configuration.isYourBoolean() ? new YourOutput(input2) : new YourOutput(input2);
		XEventClassifier nameCl = new XEventNameClassifier();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, nameCl);
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);
			
		FuzzyMinerSettings settings = new FuzzyMinerSettings(hMS, 0.8, 0.5, 0.3);
		FuzzyCGMiner miner = new FuzzyCGMiner(log, logInfo, settings);
		FuzzyCausalGraph fCG = miner.mineFCG(log, configuration);
		
		//FuzzyCausalGraphVisualization fCGV = FuzzyCausalGraphVisualizer.getVisualizationPanel(fCG, new AnnotatedVisualizationSettings(), new ProgressBarImpl(context));
		
		return fCG;

	}
	
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0, 1})
	public FuzzyCausalGraph configuredFCGMinerPlugin(PluginContext context, XLog log, FuzzyCGConfiguration configuration) {
		// Do the heavy lifting.
	    return privateFCGMinerPlugin(context, log, configuration);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0 })
	public FuzzyCausalGraph defaultFCGMinerPlugin(PluginContext context, XLog log) {
		// Get the default configuration.
	    FuzzyCGConfiguration configuration = new FuzzyCGConfiguration(log);
		// Do the heavy lifting.
	    return privateFCGMinerPlugin(context, log, configuration);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, dialog", requiredParameterLabels = { 0 })
	public FuzzyCausalGraph yourDefaultPlugin(UIPluginContext context, XLog log) {
		// Get the default configuration.
	    FuzzyCGConfiguration configuration = new FuzzyCGConfiguration(log);
	    // Get a dialog for this configuration.
	    FuzzyCGDialog dialog = new FuzzyCGDialog(context, log, configuration);
	    // Show the dialog. User can now change the configuration.
	    InteractionResult result = context.showWizard("Your dialog title", true, true, dialog);
	    // User has close the dialog.
	    if (result == InteractionResult.FINISHED) {
			// Do the heavy lifting.
	    	return privateFCGMinerPlugin(context, log, configuration);
	    }
	    // Dialog got canceled.
	    return null;
	}	
}
