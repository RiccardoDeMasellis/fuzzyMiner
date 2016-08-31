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
import org.processmining.dialogs.fuzzyminer.FuzzyPNDialog;
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

@Plugin(name = "Fuzzy Petri Net Miner", parameterLabels = {"log", "Fuzzy Petri Net Configuration" }, 
	    returnLabels = { "Fuzzy Petri Net", "Fuzzy Causal Graph"}, returnTypes = {FuzzyPetrinet.class, FuzzyCausalGraph.class})
public class FuzzyPNMinerPlugin {

	private Object[] privateFPNMinerPlugin(PluginContext context, XLog log, FuzzyMinerSettings settings) {
		Object[] output = new Object[2];
		
		XLog preprocessedLog = LogPreprocessor.preprocessLog(log);
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(preprocessedLog, settings.getHmSettings().getClassifier());
		FuzzyCGMiner miner = new FuzzyCGMiner(preprocessedLog, logInfo, settings);
		FuzzyCausalGraph fCG = miner.mineFCG(settings);
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG, preprocessedLog, settings);
        
		output[0] = fPN;
		output[1] = fCG;
		return output;

	}
	
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyPNMiner, parameters", requiredParameterLabels = { 0, 1})
	public Object[] configuredFPNMinerPlugin(PluginContext context, XLog log, FuzzyMinerSettings settings) {
		return privateFPNMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyPNMiner, parameters", requiredParameterLabels = { 0 })
	public Object[] defaultFCGMinerPlugin(PluginContext context, XLog log) {
		// Get the default configuration.
		XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);
			
		FuzzyMinerSettings settings = new FuzzyMinerSettings(hMS, 0.8, 0.5, 0.3);
		// Do the heavy lifting.
	    return privateFPNMinerPlugin(context, log, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyPNMiner, dialog", requiredParameterLabels = { 0 })
	public Object[] dialogFPNMinerPlugin(UIPluginContext context, XLog log) {
		// Get the default configuration.
	    FuzzyMinerSettings settings = new FuzzyMinerSettings();
	    
		XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);   
		settings.setHmSettings(hMS);
		
	    // Get a dialog for this configuration.
	    FuzzyPNDialog dialog = new FuzzyPNDialog(context, log, settings);
	    // Show the dialog. User can now change the configuration.
	    InteractionResult result = context.showWizard("Fuzzy Petri Net Settings", true, true, dialog);
	    // User has close the dialog.
	    if (result == InteractionResult.FINISHED) {
			// Do the heavy lifting.
	    	return privateFPNMinerPlugin(context, log, settings);
	    }
	    // Dialog got canceled.
	    return null;
	}	
}
