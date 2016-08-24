package org.processmining.models.fuzzyminer.fuzzypetrinet;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

import java.util.*;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyPetrinet extends PetrinetImpl {
    private Map<String, Transition> labelTransitionsMap;


    public FuzzyPetrinet(String label) {
        super(label);
        this.labelTransitionsMap = new HashMap<>();
    }

    public synchronized SureTransitionsArc addSureTransitionsArc(Transition source, Transition target) {
        return addSureTransitionsArc(source, target, 1);
    }

    public synchronized UncertainTransitionsArc addUncertainTransitionsArc(Transition source, Transition target) {
        return addUncertainTransitionsArc(source, target, 1);
    }

    public synchronized SureTransitionsArc addSureTransitionsArc(Transition source, Transition target, int weight) {
        return addSureTransitionsArcPrivate(source, target, weight);
    }

    public synchronized UncertainTransitionsArc addUncertainTransitionsArc(Transition source, Transition target, int weight) {
        return addUncertainTransitionsArcPrivate(source, target, weight);
    }

    protected synchronized SureTransitionsArc addSureTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // todo: chiamare checkIfPlacesArePresent

            SureTransitionsArc a = new SureTransitionsArc(source, target, weight);
            if (arcs.add(a)) {
                graphElementAdded(a);
                return a;
            } else {
                for (Arc existing : arcs) {
                    if (existing.equals(a)) {
                        existing.setWeight(existing.getWeight() + weight);
                        return (SureTransitionsArc)existing;
                    }
                }
            }
            assert (false);
            return null;
        }
    }

    protected synchronized UncertainTransitionsArc addUncertainTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // todo: chiamare checkIfPlacesArePresent

            UncertainTransitionsArc a = new UncertainTransitionsArc(source, target, weight);
            if (arcs.add(a)) {
                graphElementAdded(a);
                return a;
            } else {
                for (Arc existing : arcs) {
                    if (existing.equals(a)) {
                        existing.setWeight(existing.getWeight() + weight);
                        return (UncertainTransitionsArc)existing;
                    }
                }
            }
            assert (false);
            return null;
        }
    }


    /*
    Given a source node s, it returns all nodes t such that there exists an edge (s, t)
     */
    public synchronized Set<PetrinetNode> getOutputNodes(PetrinetNode source) {
        Set<PetrinetNode> result = new HashSet<>();
        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outgoingEdgesFromSource = this.getOutEdges(source);
        for (PetrinetEdge edge : outgoingEdgesFromSource) {
            result.add((PetrinetNode) edge.getTarget());
        }
        return result;
    }

    /*
    Given a target node t, it returns all nodes s such that there exists an edge (t, s)
     */
    public synchronized Set<PetrinetNode> getInputNodes(PetrinetNode target) {
        Set<PetrinetNode> result = new HashSet<>();
        Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> ingoingEdgesToTarget = this.getInEdges(target);
        for (PetrinetEdge edge : ingoingEdgesToTarget) {
            result.add((PetrinetNode) edge.getSource());
        }
        return result;
    }


    private synchronized Set<Place> checkIfPlacesArePresent(Transition source, Transition target) {
        Set<Place> result = new HashSet<>();
        Set<PetrinetNode> sourceOutputNodes = this.getOutputNodes(source);
        Set<PetrinetNode> targetInputNodes = this.getOutputNodes(source);
        sourceOutputNodes.retainAll(targetInputNodes);
        for (PetrinetNode node : sourceOutputNodes) {
            if (node instanceof Place)
                result.add((Place) node);
        }
        return result;
    }

}
