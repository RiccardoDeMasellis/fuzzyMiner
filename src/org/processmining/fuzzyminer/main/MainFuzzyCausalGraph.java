package org.processmining.fuzzyminer.main;

import java.io.File;
import java.util.List;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogFilterer;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogPreprocessor;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.fuzzyminer.plugins.FuzzyCGMiner;
import org.processmining.fuzzyminer.plugins.FuzzyCGMinerSettings;
import org.processmining.fuzzyminer.plugins.FuzzyMinerSettings;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

/**
 * Created by demas on 18/08/16.
 */

// QUESTIONS: What is the difference between FuzzyCGConfiguration e FuzzyMinerSettings?

public class MainFuzzyCausalGraph {
    private static String LOGFILENAME = "logs/BPIC15_1_3_10.xes";
	//private static String LOGFILENAME = "logs/registrationLog.xes";
    private static double SURETHRESHOLD = 0.8;
    private static double QUESTIONMARKTHRESHOLD = 0.7;
    private static double PARALLELISMTHRESHOLD = 0.1;
    private static double PLACEEVALTHRESHOLD = 0.8;
    private static double POSITIVEOBSERVATIONDEGREE = 0.3;
    private static double PREPLACEEVALUATIONTHRESHOLD = 0.1;
    private static int MAXCLUSTERSIZE = 5;


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
        /*hMS.setPositiveObservationThreshold(0);
        hMS.setUseAllConnectedHeuristics(true);*/

        FuzzyCGMinerSettings cGSettings = new FuzzyCGMinerSettings(hMS, POSITIVEOBSERVATIONDEGREE,SURETHRESHOLD, QUESTIONMARKTHRESHOLD, PARALLELISMTHRESHOLD);
        XLog filteredLog = LogFilterer.filterLogByActivityFrequency(preprocessedLog, logInfo, cGSettings);
        
        
        FuzzyMinerSettings pNSettings = new FuzzyMinerSettings(PREPLACEEVALUATIONTHRESHOLD, PLACEEVALTHRESHOLD, MAXCLUSTERSIZE);
        FuzzyCGMiner miner = new FuzzyCGMiner(filteredLog, filteredLog.getInfo(cGSettings.getHmSettings().getClassifier()), cGSettings);
        System.out.println("*********** Start mining the FuzzyCausalGraph ***********");
        FuzzyCausalGraph fCG = miner.mineFCG(cGSettings);
        System.out.println(fCG);
        
        FuzzyPetrinet fPN = FuzzyCGToFuzzyPN.fuzzyCGToFuzzyPN(fCG, filteredLog, pNSettings);
        
        System.out.println(fPN);
    }
}
