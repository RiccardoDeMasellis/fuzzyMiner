package org.processmining.fuzzyminer.algorithms.preprocessing;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.*;

import java.io.FileWriter;
import java.util.Date;

public class LogPreprocessor {
	
	public static XLog preprocessLog(XLog log){
		XLog preprocessedLog = XFactoryRegistry.instance().currentDefault().createLog();
		for (XTrace trace : log) {
			XEvent firstEvent = trace.get(0);
			XEvent lastEvent = trace.get(trace.size()-1);
			if (!XConceptExtension.instance().extractName(firstEvent).equalsIgnoreCase("start")){
				trace = addStartEvent(trace, XTimeExtension.instance().extractTimestamp(firstEvent));
			}
			if (!XConceptExtension.instance().extractName(lastEvent).equalsIgnoreCase("end")){
				trace = addEndEvent(trace, XTimeExtension.instance().extractTimestamp(lastEvent));
			}	
			preprocessedLog.add(trace);
		}
		//writeEventLog(preprocessedLog, "./logs/preprocessed.xes");
		
		return preprocessedLog;
	}
	
	
	
	public static XTrace addStartEvent (XTrace trace, Date timestamp){
		XTrace newTrace = XFactoryRegistry.instance().currentDefault().createTrace();
		XEvent startEvent = createInstantaneousEvent("start", timestamp);
		newTrace.add(startEvent);
		for (XEvent event : trace) {
			newTrace.add(event);
		}
		return newTrace; 
	}
	
	public static XTrace addEndEvent (XTrace trace, Date timestamp){
		XEvent endEvent = createInstantaneousEvent("end", timestamp);
		trace.add(endEvent);
		return trace; 
	}
	
	public static XEvent createInstantaneousEvent(String eventName, Date timestamp){
		XEvent event = null; 
		XAttributeMap attributeMap = XFactoryRegistry.instance().currentDefault().createAttributeMap();
		XAttributeTimestamp attrTimestamp = XFactoryRegistry.instance().currentDefault().createAttributeTimestamp(XTimeExtension.KEY_TIMESTAMP, timestamp, XTimeExtension.instance());
		attributeMap.put(XTimeExtension.KEY_TIMESTAMP,attrTimestamp);
		event = XFactoryRegistry.instance().currentDefault().createEvent(attributeMap);
		XConceptExtension.instance().assignName(event, eventName);
		return event;

	}
	
	public static void writeEventLog(XLog log, String outputLogFilePath){
		try {

			FileWriter fW = new FileWriter(outputLogFilePath);
			for (XTrace trace : log) {
				boolean start = true;
				for (XEvent event : trace) {
					String eventName = XConceptExtension.instance().extractName(event);
					if (!start)
						fW.write(";");
					fW.write(eventName);
					start = false;
				}
				fW.write("\n");
			}
			fW.flush();
			fW.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
