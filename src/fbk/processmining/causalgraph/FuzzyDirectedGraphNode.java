package fbk.processmining.causalgraph;

import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 27/07/16.
 */
public class FuzzyDirectedGraphNode extends AbstractDirectedGraphNode {

    private final FuzzyCausalGraph graph;

    public FuzzyDirectedGraphNode(FuzzyCausalGraph graph) {
        this.graph = graph;
    }

    @Override
    public FuzzyCausalGraph getGraph() {
        return this.graph;
    }
}
