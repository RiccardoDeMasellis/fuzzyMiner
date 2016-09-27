package org.processmining.fuzzyminer.main;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogFilterer;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.fuzzyminer.plugins.FuzzyCGMiner;
import org.processmining.fuzzyminer.plugins.FuzzyMinerSettings;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogPreprocessor;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import java.io.File;
import java.util.List;

/**
 * Created by demas on 18/08/16.
 */

// QUESTIONS: What is the difference between FuzzyCGConfiguration e FuzzyMinerSettings?

public class MainFuzzyCausalGraph {
    private static String LOGFILENAME = "logs/BPIC15_1_3_10.xes";
    private static double SURETHRESHOLD = 0.8;
    private static double QUESTIONMARKTHRESHOLD = 0.7;
    private static double PLACEEVALTHRESHOLD = 0.8;

    public static void main(String args[]) {
        File logFile = new File(LOGFILENAME);
        XParser logFileParser = new XesXmlParser();
        List<XLog> logList = null;
        try {
            logList = logFileParser.parse(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        XLog log = logList.get(0);  
		XLog preprocessedLog = LogPreprocessor.preprocessLog(log);

        XEventClassifier nameCl = new XEventNameClassifier();
        XLogInfo logInfo = XLogInfoFactory.createLogInfo(preprocessedLog, nameCl);
        HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
        hMS.setClassifier(nameCl);
        hMS.setPositiveObservationThreshold(0);
        hMS.setUseAllConnectedHeuristics(true);

        FuzzyMinerSettings settings = new FuzzyMinerSettings(hMS, SURETHRESHOLD, QUESTIONMARKTHRESHOLD, PLACEEVALTHRESHOLD);
        XLog filteredLog = LogFilterer.filterLogByActivityFrequency(preprocessedLog, logInfo, settings);
        
        FuzzyCGMiner miner = new FuzzyCGMiner(filteredLog, filteredLog.getInfo(settings.getHmSettings().getClassifier()), settings);
        System.out.println("*********** Start mining the FuzzyCausalGraph ***********");
        FuzzyCausalGraph fCG = miner.mineFCG(settings);
        System.out.println(fCG);
        
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG, filteredLog, settings);
        
        System.out.println(fPN);
    }
}
