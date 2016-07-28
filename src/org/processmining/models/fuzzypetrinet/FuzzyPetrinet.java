package org.processmining.models.fuzzypetrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyPetrinet extends PetrinetImpl {

    public FuzzyPetrinet(String label) {
        super(label);
    }

    public synchronized TransitionsArc addTransitionsArc(Transition source, Transition target) {
        return addTransitionsArc(source, target, 1);
    }

    public synchronized TransitionsArc addTransitionsArc(Transition source, Transition target, int weight) {
        return addTransitionsArcPrivate(source, target, weight);
    }

    protected synchronized TransitionsArc addTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            checkAddEdge(source, target);
            TransitionsArc a = new TransitionsArc(source, target, weight);
            if (arcs.add(a)) {
                graphElementAdded(a);
                return a;
            } else {
                for (Arc existing : arcs) {
                    if (existing.equals(a)) {
                        existing.setWeight(existing.getWeight() + weight);
                        return (TransitionsArc)existing;
                    }
                }
            }
            assert (false);
            return null;
        }
    }
}
