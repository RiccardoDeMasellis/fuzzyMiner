package org.processmining.fuzzyminer.plugins;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.fuzzyminer.dialogs.FuzzyPNDialog;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Fuzzy Petri Net Miner", parameterLabels = {"Fuzzy Causal Graph", "Fuzzy Petri Net Configuration" }, 
	    returnLabels = { "Fuzzy Petri Net"}, returnTypes = {FuzzyPetrinet.class})
public class FuzzyPNMinerPlugin {

	private FuzzyPetrinet privateFPNMinerPlugin(PluginContext context, FuzzyCausalGraph fCG, FuzzyMinerSettings settings) {		
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG, fCG.getLog(), settings);
        fPN = addPlacesAndMarkings(context, fPN);
		return fPN;

	}
	
	/**
	 * Add initial and end place, as well as the initial and final marking
	 * @param context
	 * @param fPN
	 * @return fPN
	 */
	private FuzzyPetrinet addPlacesAndMarkings(PluginContext context, FuzzyPetrinet fPN){
        Place startPlace = fPN.addPlace("start");
        Marking im = new Marking();
        im.add(startPlace);
        Transition startTransition = fPN.getTransition("start");
        fPN.addArc(startPlace, startTransition);
        
        Place endPlace = fPN.addPlace("end");
        Marking fm = new Marking();
        fm.add(endPlace);        
        Transition endTransition = fPN.getTransition("end");
        fPN.addArc(endTransition, endPlace);
               
        context.getProvidedObjectManager().createProvidedObject(
                "Initial marking for " + fPN.getLabel(),
                im, Marking.class, context);
        context.addConnection(new InitialMarkingConnection(fPN, 
                im));

        context.getProvidedObjectManager().createProvidedObject(
                    "Final marking for " + fPN.getLabel(),
                    fm, Marking.class, context);
        context.addConnection(new FinalMarkingConnection(fPN, fm));
        
        
		return fPN;
	}
	
		
	/**
	 * The plug-in variant that runs in any context and requires a configuration.
	 */ 
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyPNMiner, parameters", requiredParameterLabels = { 0, 1})
	public FuzzyPetrinet configuredFPNMinerPlugin(PluginContext context, FuzzyCausalGraph fCG, FuzzyMinerSettings settings) {
		return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	/**
	 * The plug-in variant that runs in any context and uses the default configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyPNMiner, parameters", requiredParameterLabels = { 0 })
	public FuzzyPetrinet defaultFCGMinerPlugin(PluginContext context, FuzzyCausalGraph fCG) {
		// Get the default configuration.
		XEventClassifier nameCl = new XEventNameClassifier();
		HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
		hMS.setClassifier(nameCl);
			
		FuzzyMinerSettings settings = new FuzzyMinerSettings();
		// Do the heavy lifting.
	    return privateFPNMinerPlugin(context, fCG, settings);
	}
	
	/**
	 * The plug-in variant that runs in a UI context and uses a dialog to get the configuration.
	 */
	@UITopiaVariant(affiliation = "FBK", author = "R. De Masellis et al.", email = "r.demasellis|dfmchiara@fbk.eu")
	@PluginVariant(variantLabel = "FuzzyPNMiner, dialog", requiredParameterLabels = { 0})
	public FuzzyPetrinet dialogFPNMinerPlugin(UIPluginContext context, FuzzyCausalGraph fCG) {
		// Get the default configuration.
	    FuzzyMinerSettings settings = new FuzzyMinerSettings();
	     
	    // Get a dialog for this configuration.
	    FuzzyPNDialog dialog = new FuzzyPNDialog(context, fCG, settings);
	    // Show the dialog. User can now change the configuration.
	    InteractionResult result = context.showWizard("Fuzzy Petri Net Settings", true, true, dialog);
	    // User has close the dialog.
	    if (result == InteractionResult.FINISHED) {
			// Do the heavy lifting.
	    	return privateFPNMinerPlugin(context, fCG, settings);
	    }
	    // Dialog got canceled.
	    return null;
	}	
	
	
}
