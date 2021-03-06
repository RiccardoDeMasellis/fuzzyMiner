package org.processmining.fuzzyminer.models.fuzzypetrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.fuzzyminer.models.causalgraph.FuzzyDirectedGraphEdge;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyDirectedSureGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyPetrinet extends PetrinetImpl {
    private Map<String, Transition> labelTransitionsMap;


    public FuzzyPetrinet(String label) {
        super(label);
        this.labelTransitionsMap = new HashMap<>();
    }

    public synchronized TransitionsArc addTransitionsArcFromFCGEdge(FuzzyDirectedGraphEdge edge) {
        Transition source = this.addTransition(edge.getSource().getLabel());
        Transition target = this.addTransition(edge.getTarget().getLabel());

        if (edge instanceof FuzzyDirectedSureGraphEdge)
            return this.addSureTransitionsArcPrivate(source, target, 1);
        else
            return this.addUncertainTransitionsArcPrivate(source, target, 1);
    }

    public synchronized TransitionsArc addTransitionsArcFromFCGEdge(FuzzyDirectedGraphEdge edge, int weight) {
        Transition source = this.addTransition(edge.getSource().getLabel());
        Transition target = this.addTransition(edge.getTarget().getLabel());

        if (edge instanceof FuzzyDirectedSureGraphEdge)
            return this.addSureTransitionsArcPrivate(source, target, weight);
        else
            return this.addUncertainTransitionsArcPrivate(source, target, weight);
    }

    /**
     * The semantics o the method is the following. It adds a SureTransitionsArc between transition source and target if there is no
     * place already connecting source and target. This because when building the fuzzyNet, we first add all the places and respective
     * transitions, and then add the (sure and uncertain) arcs connecting two transitions. Things are complicated by the presence of
     * the weight, which is not taken into consideration in this first version (all arcs have weight=1). However, to be on the safe side,
     * the method checks if there already is a sureArc connecting the same source and transition but with a different weight, and if
     * this is the case, it sets the new weight as the sum of the two.
     * @param source the source of the sureTransitionArc to be added
     * @param target the target of the sureTransitionArc to be added
     * @param weight the weight of the arc
     * @return the SureTransitionArc if not present or if already present with a different weight. Null if there is already a place
     * connecting source and target.
     */
    private synchronized SureTransitionsArc addSureTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            Set<Place> placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (placesAlreadyAdded.size()==0) {
                SureTransitionsArc a = new SureTransitionsArc(source, target, weight);

                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (SureTransitionsArc) existing;
                        }
                    }
                }
                assert (false);
                return null;
            }
            return null;
        }
    }

    /**
     * The semantics o the method is the following. It adds a UncertainTransitionsArc between transition source and target if there is no
     * place already connecting source and target. This because when building the fuzzyNet, we first add all the places and respective
     * transitions, and then add the (sure and uncertain) arcs connecting two transitions. Things are complicated by the presence of
     * the weight, which is not taken into consideration in this first version (all arcs have weight=1). However, to be on the safe side,
     * the method checks if there already is a sureArc connecting the same source and transition but with a different weight, and if
     * this is the case, it sets the new weight as the sum of the two.
     * @param source the source of the uncertainTransitionArc to be added
     * @param target the target of the uncertainTransitionArc to be added
     * @param weight the weight of the arc
     * @return the uncertainTransitionArc if not present or if already present with a different weight. Null if there is already a place
     * connecting source and target.
     */
    private synchronized UncertainTransitionsArc addUncertainTransitionsArcPrivate(Transition source, Transition target, int weight) {
        synchronized (arcs) {
            // Following check just makes sure that source and target already exist in the net
            checkAddEdge(source, target);

            // Check if there is already a place connecting source and target. If there is, return null
            Set<Place> placesAlreadyAdded = this.checkIfPlacesArePresent(source, target);
            if (placesAlreadyAdded.size()==0) {
                UncertainTransitionsArc a = new UncertainTransitionsArc(source, target, weight);
                if (arcs.add(a)) {
                    graphElementAdded(a);
                    return a;
                } else {
                    for (Arc existing : arcs) {
                        if (existing.equals(a)) {
                            existing.setWeight(existing.getWeight() + weight);
                            return (UncertainTransitionsArc) existing;
                        }
                    }
                }
                assert (false);
                return null;
            }
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
        Set<PetrinetNode> targetInputNodes = this.getInputNodes(target);
        sourceOutputNodes.retainAll(targetInputNodes);
        for (PetrinetNode node : sourceOutputNodes) {
            if (node instanceof Place)
                result.add((Place) node);
        }
        return result;
    }


    /**
     * We build fuzzyPetrinets with a heavy restriction: we do not ever have two transition with the same label. For
     * such a reason, when adding a new transition, we have to check if a transition with the same label is
     * already present.
     * @param label the name of the transition
     * @return a new Transition if a transition with the same label DO NOT already exists, otherwise returns
     * the already present transition.
     */
    public synchronized Transition addTransition(String label) {
        Transition alreadyPresent = this.labelTransitionsMap.get(label);
        if (alreadyPresent == null) {
            Transition newTransition = super.addTransition(label);
            this.labelTransitionsMap.put(label, newTransition);
            return newTransition;
        }
        else {
            return alreadyPresent;
        }
    }


    public synchronized <N extends AbstractDirectedGraphNode> void addPlaceFromPlaceEvaluation(PlaceEvaluation<N> placeEval) {
        // todo: name of the places?
        Place p = this.addPlace(placeEval.toString());
        for (N node : placeEval.getPlaceOutputNodes()) {
            Transition t = this.addTransition(node.getLabel());
            this.addArc(t, p);
        }
        for (N node: placeEval.getPlaceInputNodes()) {
            Transition t = this.addTransition(node.getLabel());
            this.addArc(p, t);
        }
    }

    /**
     *Returns a place named label
     * @param label
     * @return
     */
    public synchronized Place getPlace(String label){
    	Place place = null;
    	Collection<Place> places = super.getPlaces();
    	for (Place itPlace : places) {
			if (itPlace.getLabel().equals(label))
				place = itPlace; 
		}
    	return place;
    }

    /**
     * Returns a transition named label
     * @param label
     * @return
     */
    public synchronized Transition getTransition(String label){
    	Transition transition = null;
    	Collection<Transition> transitions = super.getTransitions();
    	for (Transition itTransition : transitions) {
			if (itTransition.getLabel().equals(label))
				transition = itTransition; 
		}
    	return transition;
    }
    
    /*
    In the following the exact rewriting of methods of AbstractResetInhibitorNet
    JUST TO HAVE RIGHT IN THIS CLASS THE METHOD USEFUL FOR US (as in the superclass there are a plethora of methods)
    not useful.
     */
    public synchronized Place addPlace(String label) {
        return super.addPlace(label);
    }

    public synchronized Arc addArc(Place p, Transition t) {
        return super.addArc(p, t);
    }

    public synchronized Arc addArc(Transition t, Place p) {
        return super.addArc(t, p);
    }

    @Override
    public String toString() {
    	String fuzzyPetriNetString = "*** FUZZY PETRI NET "+this.getLabel()+" *** \n";
    	for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : getEdges()) {
    		if (edge instanceof SureTransitionsArc)
    			fuzzyPetriNetString+=edge.getSource()+" ->- "+edge.getTarget()+"\n";
            if (edge instanceof UncertainTransitionsArc)
    			fuzzyPetriNetString+=edge.getSource()+" ->-? "+edge.getTarget()+"\n";
            else
                fuzzyPetriNetString+=edge.getSource()+" -> "+edge.getTarget()+"\n";
		}
    	return fuzzyPetriNetString;
    }
}
