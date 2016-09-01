package org.processmining.main;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.models.fuzzyminer.causalgraph.FuzzyCausalGraph;
import org.processmining.models.fuzzyminer.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.plugins.fuzzyminer.FuzzyCGMiner;
import org.processmining.plugins.fuzzyminer.FuzzyMinerSettings;
import org.processmining.plugins.fuzzyminer.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.plugins.fuzzyminer.preprocessing.LogPreprocessor;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import java.io.File;
import java.util.List;

/**
 * Created by demas on 18/08/16.
 */

// QUESTIONS: What is the difference between FuzzyCGConfiguration e FuzzyMinerSettings?

public class MainFuzzyCausalGraph {
    private static String LOGFILENAME = "logs/bpi_challenge_2013_incidents.xes";
    private static double SURETHRESHOLD = 0.6;
    private static double QUESTIONMARKTHRESHOLD = 0.5;
    private static double PLACEEVALTHRESHOLD = 0.7;

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

        FuzzyMinerSettings settings = new FuzzyMinerSettings(hMS, SURETHRESHOLD, QUESTIONMARKTHRESHOLD, PLACEEVALTHRESHOLD);
        FuzzyCGMiner miner = new FuzzyCGMiner(preprocessedLog, logInfo, settings);
        FuzzyCausalGraph fCG = miner.mineFCG(settings);
        System.out.println(fCG);
        
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG, preprocessedLog, settings);
        
        System.out.println(fPN);
    }
}
