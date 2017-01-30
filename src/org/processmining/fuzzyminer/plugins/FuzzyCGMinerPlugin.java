package org.processmining.fuzzyminer.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogFilterer;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogPreprocessor;
import org.processmining.fuzzyminer.dialogs.FuzzyCGDialog;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Fuzzy Causal Graph Miner", parameterLabels = {"log", "Fuzzy Causal Graph Configuration" }, 
	    returnLabels = {"Fuzzy Causal Graph"}, returnTypes = {FuzzyCausalGraph.class})
public class FuzzyCGMinerPlugin {

	private FuzzyCausalGraph privateFCGMinerPlugin(PluginContext context, XLog log, FuzzyCGMinerSettings settings) {	
		XLog preprocessedLog = LogPreprocessor.preprocessLog(log);
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(preprocessedLog, settings.getHmSettings().getClassifier());
		System.out.println("**** PREPROCESSING OVER ******");
		XLog filteredLog = LogFilterer.filterLogByActivityFrequency(preprocessedLog, logInfo, settings);
		System.out.println("**** FILTERING OVER ******");
		FuzzyCGMiner miner = new FuzzyCGMiner(filteredLog, filteredLog.getInfo(settings.getHmSettings().getClassifier()), settings);
		FuzzyCausalGraph fCG = miner.mineFCG(settings);
		return fCG;

	}
	
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0, 1})
	public FuzzyCausalGraph configuredFPNMinerPlugin(PluginContext context, XLog log, FuzzyCGMinerSettings settings) {
		return privateFCGMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0 })
	public FuzzyCausalGraph defaultFCGMinerPlugin(PluginContext context, XLog log) {
		// Get the default configuration.
		XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);
		
			
		FuzzyCGMinerSettings settings = new FuzzyCGMinerSettings();
		settings.setHmSettings(hMS);
		// Do the heavy lifting.
	    return privateFCGMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, dialog", requiredParameterLabels = { 0 })
	public FuzzyCausalGraph dialogFPNMinerPlugin(UIPluginContext context, XLog log) {
		// Get the default configuration.
	    FuzzyCGMinerSettings settings = new FuzzyCGMinerSettings();
	    
		XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);  
		settings.setHmSettings(hMS);
		
	    // Get a dialog for this configuration.
	    FuzzyCGDialog dialog = new FuzzyCGDialog(context, log, settings);
	    // Show the dialog. User can now change the configuration.
	    InteractionResult result = context.showWizard("Fuzzy Causal Graph Settings", true, true, dialog);
	    // User has close the dialog.
	    if (result == InteractionResult.FINISHED) {
			// Do the heavy lifting.
	    	return privateFCGMinerPlugin(context, log, settings);
	    }
	    // Dialog got canceled.
	    return null;
	}	
}
