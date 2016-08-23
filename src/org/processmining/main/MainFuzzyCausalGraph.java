package org.processmining.main;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.confs.FuzzyCGConfiguration;
import org.processmining.models.causalgraph.FuzzyCausalGraph;
import org.processmining.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.fuzzyminer.FuzzyCGMiner;
import org.processmining.fuzzyminer.FuzzyMinerSettings;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import java.io.File;
import java.util.List;

/**
 * Created by demas on 18/08/16.
 */

// QUESTIONS: What is the difference between FuzzyCGConfiguration e FuzzyMinerSettings?

public class MainFuzzyCausalGraph {
    private static String LOGFILENAME = "logs/registrationLog2.xes";
    private static double SURETHRESHOLD = 0.6;
    private static double QUESTIONMARKTHRESHOLD = 0.5;

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
        FuzzyCGConfiguration configuration = new FuzzyCGConfiguration(log);
        configuration.setSureThreshold(SURETHRESHOLD);
        configuration.setQuestionMarkThreshold(QUESTIONMARKTHRESHOLD);
   


        XEventClassifier nameCl = new XEventNameClassifier();
        XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, nameCl);
        HeuristicsMinerSettings hMS = new HeuristicsMinerSettings();
        hMS.setClassifier(nameCl);

        FuzzyMinerSettings settings = new FuzzyMinerSettings(hMS, SURETHRESHOLD, QUESTIONMARKTHRESHOLD);
        FuzzyCGMiner miner = new FuzzyCGMiner(log, logInfo, settings);
        FuzzyCausalGraph fCG = miner.mineFCG(log, configuration);
        miner.printGraph(fCG);
        
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG);
        
        System.out.println(fPN);
    }
}
