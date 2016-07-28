package fbk.processmining.plugin.fuzzyminer;


import fbk.processmining.fuzzyminer.FuzzyMiner;
import fbk.processmining.fuzzyminer.FuzzyMinerSettings;
import fbk.processmining.fuzzyminer.UncertaintyNet;
import org.deckfour.uitopia.api.event.TaskListener;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui.ParametersPanel;

/**
 * Created by demas on 25/07/16.
 */

@Plugin(name = "Mine for a Uncertainty Net",
        level = PluginLevel.PeerReviewed,
        //parameterLabels = {"Log", "Settings", "Log Info"},
        parameterLabels = {"Log", "Settings"},
        //parameterLabels = {"Log"},
        returnLabels = {"Mined Model"},
        returnTypes = {UncertaintyNet.class},
        userAccessible = true,
        categories = { PluginCategory.Discovery },
        help = "Uncertainty Miner to discover a Uncertainty Net.")
public class FuzzyMinerPlugin {

    public static double SURE_THRESHOLD = 0.8;
    public static double QUESTIONMARK_THRESHOLD = 0.8;


    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "R. De Masellis, C. Di Francescomarino", email = "r.demasellis|dfmchiara@fbk.eu", website = "https://shell.fbk.eu", pack = "FuzzyMiner")
    @PluginVariant(variantLabel = "Mine Uncertainty Net using Wizard (1)", requiredParameterLabels = { 0, 1 })
    public static HeuristicsNet run(UIPluginContext context, XLog log) {
        XEventClassifier defaultClassifier = null;
        if (log.getClassifiers().isEmpty()) {
            XEventClassifier nameCl = new XEventNameClassifier();
            XEventClassifier lifeTransCl = new XEventLifeTransClassifier();
            XEventAttributeClassifier attrClass = new XEventAndClassifier(nameCl, lifeTransCl);
            defaultClassifier = attrClass;
        } else {
            defaultClassifier = log.getClassifiers().get(0);
        }

        XLogInfo loginfo = new XLogInfoImpl(log, defaultClassifier, log.getClassifiers());

        ParametersPanel parameters = new ParametersPanel(loginfo.getEventClassifiers());
        parameters.removeAndThreshold();

        TaskListener.InteractionResult result = context.showConfiguration("Heuristics Miner Parameters", parameters);
        if (result.equals(TaskListener.InteractionResult.CANCEL)) {
            context.getFutureResult(0).cancel(true);
        }

        FuzzyMinerSettings fms = new FuzzyMinerSettings(parameters.getSettings(), SURE_THRESHOLD, QUESTIONMARK_THRESHOLD);

        return run(context, log, fms, loginfo);
    }

    @PluginVariant(variantLabel = "Mine Fuzzy Net using Given Settings (2)", requiredParameterLabels = { 0, 1, 2 })
    public static UncertaintyNet run(PluginContext context, XLog log, FuzzyMinerSettings settings, XLogInfo logInfo) {

        FuzzyMiner fm = new FuzzyMiner(context, log, logInfo, settings);
        return fm.mine();
    }
}
