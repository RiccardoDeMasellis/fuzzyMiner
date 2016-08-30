package org.processmining.plugins.fuzzyminer.preprocessing;

import java.util.Date;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

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
		return event;

	}

}
