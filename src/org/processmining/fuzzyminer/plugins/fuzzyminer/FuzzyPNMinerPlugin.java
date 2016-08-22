package org.processmining.fuzzyminer.plugins.fuzzyminer;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.confs.FuzzyPNConfiguration;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.dialogs.FuzzyPNDialog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.causalgraph.FuzzyCausalGraph;
import org.processmining.models.fuzzypetrinet.FuzzyPetrinet;

public class FuzzyPNMinerPlugin {

	@Plugin(name = "Fuzzy Petri Net Miner", parameterLabels = { "Fuzzy Causal Graph", "Fuzzy Petri Net Configuration" }, 
		    returnLabels = { "Fuzzy Petri Net" }, returnTypes = { FuzzyPetrinet.class })
	public class FuzzyCGMinerPlugin {

		private FuzzyPetrinet privateFPNMinerPlugin(PluginContext context, FuzzyCausalGraph fCG, FuzzyPNConfiguration configuration) {
			FuzzyPetrinet fPN = new FuzzyPetrinet("My fuzzy Petrinet");
			return fPN;
			//return FuzzyCausalGraphVisualizer.getVisualizationPanel(fCG, new AnnotatedVisualizationSettings(), new ProgressBarImpl(context));

		}
		
		/**
		 * The plug-in variant that runs in any context and requires a configuration.
		 */ 
		@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
		@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0, 1})
		public FuzzyPetrinet configuredFPNMinerPlugin(PluginContext context, FuzzyCausalGraph fCG, FuzzyPNConfiguration configuration) {
			// Do the heavy lifting.
		    return privateFPNMinerPlugin(context, fCG, configuration);
		}
		
		/**
		 * The plug-in variant that runs in any context and uses the default configuration.
		 */
		@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
		@PluginVariant(variantLabel = "FuzzyCGMiner, parameters", requiredParameterLabels = { 0 })
		public FuzzyPetrinet yourDefaultPlugin(PluginContext context, FuzzyCausalGraph fCG) {
			// Get the default configuration.
		    FuzzyPNConfiguration configuration = new FuzzyPNConfiguration(fCG);
			// Do the heavy lifting.
		    return privateFPNMinerPlugin(context, fCG, configuration);
		}
		
		/**
		 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
		 */
		@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu")
		@PluginVariant(variantLabel = "FuzzyCGMiner, dialog", requiredParameterLabels = { 0 })
		public FuzzyPetrinet yourDefaultPlugin(UIPluginContext context, FuzzyCausalGraph fCG) {
			// Get the default configuration.
		    FuzzyPNConfiguration configuration = new FuzzyPNConfiguration(fCG);
		    // Get a dialog for this configuration.
		    FuzzyPNDialog dialog = new FuzzyPNDialog(context, fCG, configuration);
		    // Show the dialog. User can now change the configuration.
		    InteractionResult result = context.showWizard("Your dialog title", true, true, dialog);
		    // User has close the dialog.
		    if (result == InteractionResult.FINISHED) {
				// Do the heavy lifting.
		    	return privateFPNMinerPlugin(context, fCG, configuration);
		    }
		    // Dialog got canceled.
		    return null;
		}	
	}

	
}
