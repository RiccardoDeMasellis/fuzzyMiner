package org.processmining.plugins.fuzzyminer;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.confs.fuzzyminer.FuzzyCGConfiguration;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogs.fuzzyminer.FuzzyCGDialog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyCausalGraph;
import org.processmining.models.fuzzyminer.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.plugins.fuzzyminer.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.plugins.fuzzyminer.preprocessing.LogPreprocessor;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Fuzzy Causal Graph Miner", parameterLabels = {"log", "Fuzzy Causal Graph Configuration" }, 
	    returnLabels = {"Fuzzy Causal Graph" }, returnTypes = {FuzzyCausalGraph.class })
public class FuzzyCGMinerPlugin {

	private FuzzyCausalGraph privateFCGMinerPlugin(PluginContext context, XLog log, FuzzyMinerSettings settings) {
		XLog preprocessedLog = LogPreprocessor.preprocessLog(log);
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(preprocessedLog, settings.getHmSettings().getClassifier());
		FuzzyCGMiner miner = new FuzzyCGMiner(preprocessedLog, logInfo, settings);
		FuzzyCausalGraph fCG = miner.mineFCG(preprocessedLog, settings);
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG, preprocessedLog, settings);

				
		return fCG;

	}
	
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0, 1})
	public FuzzyCausalGraph configuredFCGMinerPlugin(PluginContext context, XLog log, FuzzyMinerSettings settings) {
		return privateFCGMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0 })
	public FuzzyCausalGraph defaultFCGMinerPlugin(PluginContext context, XLog log) {
		// Get the default configuration.
		XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);
			
		FuzzyMinerSettings settings = new FuzzyMinerSettings(hMS, 0.8, 0.5, 0.3);
		// Do the heavy lifting.
	    return privateFCGMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyCGMiner, dialog", requiredParameterLabels = { 0 })
	public FuzzyCausalGraph yourDefaultPlugin(UIPluginContext context, XLog log) {
		// Get the default configuration.
	    FuzzyMinerSettings settings = new FuzzyMinerSettings();
	    
		XEventClassifier nameCl = new XEventNameClassifier();
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, nameCl);
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
