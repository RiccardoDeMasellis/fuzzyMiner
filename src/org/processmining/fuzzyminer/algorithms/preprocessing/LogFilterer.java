package org.processmining.fuzzyminer.algorithms.preprocessing;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.fuzzyminer.plugins.FuzzyMinerSettings;

public class LogFilterer {
	
    //Filtering by activity frequency
	public static XLog filterLogByActivityFrequency(XLog log, XLogInfo logInfo, FuzzyMinerSettings settings){
		Map<String, Integer> activityFrequencyMap = new HashMap<String, Integer>();
		for (XTrace trace : log) {
	        for (XEvent event : trace) {
	            Integer eventIndex = null;
	            String eventKey = logInfo.getEventClasses(settings.getHmSettings().getClassifier()).getClassOf(event).getId();
	            Integer value = activityFrequencyMap.get(eventKey);
	            if (value==null)
	            	value = new Integer(1);
	            else 
	            	value = value+1;
	            activityFrequencyMap.put(eventKey, value);

	        }
		}
		XLog filteredLog = XFactoryRegistry.instance().currentDefault().createLog();
		filteredLog.setAttributes(log.getAttributes());
		for (XTrace trace : log) {
			XTrace filteredTrace = XFactoryRegistry.instance().currentDefault().createTrace();
			filteredTrace.setAttributes(trace.getAttributes());
	        for (XEvent event : trace) {
	        	String eventKey = logInfo.getEventClasses(settings.getHmSettings().getClassifier()).getClassOf(event).getId(); 
	        	if(activityFrequencyMap.get(eventKey)>=settings.getHmSettings().getPositiveObservationThreshold()){
	        		filteredTrace.add(event);
	        	}
	        }
	        if (filteredTrace.size()>0)
	        	filteredLog.add(filteredTrace);
		}
		XLogInfo filteredLogInfo = XLogInfoFactory.createLogInfo(filteredLog, settings.getHmSettings().getClassifier());
		filteredLog.setInfo(settings.getHmSettings().getClassifier(), filteredLogInfo);
		return filteredLog;
		



	}

}
