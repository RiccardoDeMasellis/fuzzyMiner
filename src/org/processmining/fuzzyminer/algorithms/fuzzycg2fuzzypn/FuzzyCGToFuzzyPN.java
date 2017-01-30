package org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.model.XLog;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyDirectedGraphEdge;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyDirectedGraphNode;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyDirectedSureGraphEdge;
import org.processmining.fuzzyminer.models.fuzzypetrinet.Cluster;
import org.processmining.fuzzyminer.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.fuzzyminer.models.fuzzypetrinet.PlaceEvaluation;
import org.processmining.fuzzyminer.plugins.FuzzyMinerSettings;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;

/**
 * Created by demas on 19/08/16.
 */
public class FuzzyCGToFuzzyPN {

	public static <N extends AbstractDirectedGraphNode> FuzzyPetrinet fuzzyCGToFuzzyPN(FuzzyCausalGraph graph, XLog log, FuzzyMinerSettings pNSettings) {
		FuzzyPetrinet result = new FuzzyPetrinet("minedFuzzyPetrinet");

		// We consider only sure edges!
		Set<FuzzyDirectedSureGraphEdge> edges = graph.getSureEdges();

		// Build the clusters
		Set<Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode>> clusters = identifyClusters(edges);

		//Reduce the clusters, so as each cluster does not contain more than k edges
		System.out.println("*********** Start resizing clusters ***********");
		long startTime = System.currentTimeMillis();
		Set<Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode>> reducedClusters = reduceClusters(clusters, pNSettings.getMaxClusterSize());
		long stopTime = System.currentTimeMillis();
		double elapsedTime = (stopTime - startTime)/60000.0;
		System.out.println("*********** End resizing clusters in time: " + elapsedTime + " mins ***********");

		ExecutorService exec = Executors.newCachedThreadPool();

		System.out.println("*********** Start multithread place evaluations on clusters ***********");
		startTime = System.currentTimeMillis();

		for (Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode> c : reducedClusters) {
			c.setLog(log);
			c.setActivityFrequencyMap(graph.getActivityFrequencyMap());
			c.setPlaceEvaluationThreshold(pNSettings.getPlaceEvalThreshold());
			c.setPrePlaceEvaluationThreshold(pNSettings.getPrePlaceEvaluationThreshold());
			exec.execute(c);
		}

		exec.shutdown();
		try {
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stopTime = System.currentTimeMillis();
		elapsedTime = (stopTime - startTime)/60000.0;
		System.out.println("*********** End multithread place evaluations on clusters in time: " + elapsedTime + " mins ***********");

		/*
		 * I have to remove places that have the same input and output sets automatically removed as I adding them to the same set
		 * and the equals has been defined as having same input and output sets.
		 */
		Set<PlaceEvaluation<FuzzyDirectedGraphNode>> allPlaceEvaluations = new HashSet<>();
		
		for (Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode> c : reducedClusters) {
			for (PlaceEvaluation<FuzzyDirectedGraphNode> pe : c.getPlacesAboveThreshold()) {
					allPlaceEvaluations.add(pe);
			}
		}
		
		/*
		 * Now in allPlaceEvaluation I have all the places that have to be added. Start the computation for nonRedundancy!
		 */
		Set<PlaceEvaluation<FuzzyDirectedGraphNode>> nonRedundantPlaces = computeNonRedundantPlaces(allPlaceEvaluations);
		
		
		/*
		 * Now that I eliminated redundant places I can add the others to the net.
		 */
		for (PlaceEvaluation<FuzzyDirectedGraphNode> pe : nonRedundantPlaces) {
			result.addPlaceFromPlaceEvaluation(pe);
			System.out.println(pe);
			System.out.println(pe.evaluateReplayScore());
		}

		/*
        Add **ALL** transitions of the causal graph to the set of transition of the petrinet.
        (Transition already present will not be added.)
		 */
		for (FuzzyDirectedGraphNode node : graph.getNodes())
			result.addTransition(node.getLabel());

		/* Then add the sure and uncertain arcs between transitions in the net coming from the causal graph
            I do not know which sure transitions have met the threshold thus have been replaced by a place transition,
             but such a check is directly in the method
		 */
		for (FuzzyDirectedGraphEdge edge : graph.getEdges())
			result.addTransitionsArcFromFCGEdge(edge);

		return result;
	}
	
	
	private static <N extends AbstractDirectedGraphNode> Set<PlaceEvaluation<N>> computeNonRedundantPlaces(Set<PlaceEvaluation<N>> allPlaces) {
		Set<PlaceEvaluation<N>> result = new HashSet<>();
		
		// Check the redundancy place by place
		for(PlaceEvaluation<N> pe : allPlaces) {
			if (!(isRedundant(pe, allPlaces)))
				result.add(pe);
		}
		return result;
	}
	
	
	private static <N extends AbstractDirectedGraphNode> boolean isRedundant(PlaceEvaluation<N> pe, Set<PlaceEvaluation<N>> allPlaces) {
		// If pe has a single inputNode or a single OutputNode cannot be redundant!
		if (pe.getPlaceInputNodes().size()==1 || pe.getPlaceOutputNodes().size()==1)
			return false;
		
		// Otherwise, let us compute the possible partitions of the input and those of the output:
		List<List<List<List<N>>>> outputNodePartitions = Utils.getAllPartitions(pe.getPlaceOutputNodes());
		List<List<List<List<N>>>> inputNodePartitions = Utils.getAllPartitions(pe.getPlaceInputNodes());
		
		// I only need to check the bijections from output to inputs which partition sets WITH THE SAME CARDINALITY!
		// Hence, I need to compute the smallest of the two List of List of ...
		int partitionsToCheck = Math.min(outputNodePartitions.size(), inputNodePartitions.size());
		
		//this number cannot be one, given the check on the input and output nodes...
		if (partitionsToCheck == 1)
			throw new RuntimeException("Something wrong with the isRedundant algorithm");
		
		// no need to check the partitions with a single element...
		for (int i=1; i<partitionsToCheck; i++) {
			/* Compute all possible bijections from outputPartition of size i+1 and inputPartition of size i+1!
			 * The bijections are obtained as follows: I leave the outputNode current partition list as it is and I compute all possible permutations
			 * of the input node partition list. The bijection always associate the first element of the first list with the first of the second. 
			 */
			List<List<List<N>>> allOutputPartitionsOfiElements = outputNodePartitions.get(i);
			List<List<List<N>>> allInputPartitionsOfiElements = inputNodePartitions.get(i);
			
			for (int ii=0; ii<allOutputPartitionsOfiElements.size(); ii++) {
				for (int jj=0; jj<allInputPartitionsOfiElements.size(); jj++) {
					/*
					 * WARNING! generatePerm does **SIDE EFFECT** on the argument! Perform a copy first! 
					 */
					for (List<List<N>> currentInputPerm : Utils.generatePerm(new ArrayList<List<N>>(allInputPartitionsOfiElements.get(jj)))) {
						// Now that I have a permutation, I have to check that all placeEval of the current bijection are in allPlaceEvaluation
						boolean allPresent = true;
						
						// I already know how many elements will be in the partition! They are i!
						for (int k=0; k<i; k++) {
							Set<N> currentOutputSet = new HashSet<N>(allOutputPartitionsOfiElements.get(ii).get(k));
							Set<N> currentInputSet = new HashSet<N>(currentInputPerm.get(k));
						
							// Now check if the placeEval is present!
							PlaceEvaluation<N> currentPE = new PlaceEvaluation<N>(currentOutputSet, currentInputSet, null, null, 0);
							if (!allPlaces.contains(currentPE)) {
								allPresent=false;
								break;
							}
						}
						if (allPresent) {
							System.out.println("One redundant place found. OutputSet: " + pe.getPlaceOutputNodes());
							System.out.println("InputSet: " + pe.getPlaceInputNodes());
							System.out.println("It is implicated by OutputSet: " + allOutputPartitionsOfiElements.get(ii));
							System.out.println("And InputSet: " + currentInputPerm);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	
	// Build the clusters by least fixpoint computations, according to the definition on the paper.
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<Cluster<E, N>> identifyClusters(Set<E> edges) {
		HashSet<Cluster<E, N>> clusterSet = new HashSet<>();

		// Efficiency: keep a set of edges already analyzed
		Set<E> alreadyAnalyzed = new HashSet<>();

		// for each edge not yet analyzed, create a cluster, as every edge must be contained in a cluster.
		for (E edge : edges) {
			if (alreadyAnalyzed.contains(edge))
				continue;

			// create the new set of edges constituting the new cluster
			Set<E> newCluster = new HashSet<>();

			// add the current edge to it
			newCluster.add(edge);
			alreadyAnalyzed.add(edge);
			Set<E> oldCluster = new HashSet<>();
			// add all the other edges to the current cluster.
			while (! oldCluster.equals(newCluster)) {
				// 1) oldCluster = new Cluster
				oldCluster.addAll(newCluster);

				// 2) Analyze all the edges in OldCluster and add them to newCluster...
				for (E e : oldCluster) {
					N source = (N) e.getSource();
					N target = (N) e.getTarget();

					Set<E> edgesForSource = getEdgesHavingSourceNode(source, edges);
					Set<E> edgesForTarget = getEdgesHavingTargetNode(target, edges);
					newCluster.addAll(edgesForSource);
					newCluster.addAll(edgesForTarget);

					//Add them to already analyzed, as they are already part of a cluster.
					alreadyAnalyzed.addAll(edgesForSource);
					alreadyAnalyzed.addAll(edgesForTarget);
				}
				// ...until there is no other edge to add.
			}
			// add it to the set of cluster
			clusterSet.add(new Cluster<E, N>(newCluster));
		}
		return clusterSet;
	}


	/*
    Given a source node s and a set of edges, the method returns all edges having s as source.
	 */
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesHavingSourceNode(N source, Set<E> edges) {
		Set<E> result = new HashSet<>();
		for (E edge : edges) {
			if (edge.getSource().equals(source))
				result.add(edge);
		}
		return result;
	}

	/*
    Given a target node t and a set of edges, the method returns all edges having t as source.
	 */
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<E> getEdgesHavingTargetNode(N target, Set<E> edges) {
		Set<E> result = new HashSet<>();
		for (E edge : edges) {
			if (edge.getTarget().equals(target))
				result.add(edge);
		}
		return result;
	}

	/*
	 * Given a set of clusters, analyzes them and if they are too large it splits them 
	 */
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<Cluster<E, N>> reduceClusters(Set<Cluster<FuzzyDirectedSureGraphEdge, FuzzyDirectedGraphNode>> clusters, int k){
		Set<Cluster<E, N>> resizedClusters = new HashSet<>();
		// For each cluster:
		for (Cluster cluster : clusters) {
			//Analyze the clusters
			//System.out.println("**** Start analyzing cluster for resizing " + cluster + " ****");
			//Check if the cluster is larger than k
			if (cluster.getEdges().size()>k){
				resizedClusters.addAll(splitCluster(cluster, k));
			} else 
				resizedClusters.add(cluster);
		}
		return resizedClusters;
	}


	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> double computeIOPartitionMaxPowerSet(Set<Set<E>> sets){
		double maxPowerSet = 0;
		for (Set<E> set : sets) {
			Set<N> iNodes = new HashSet<N>();
			Set<N> oNodes = new HashSet<N>();
			for (E edge : set) {
				iNodes.add((N) edge.getTarget());
				oNodes.add((N) edge.getSource());
			}
			double numberOfReplay = Math.pow(2, iNodes.size()) * Math.pow(2,oNodes.size());
			if (numberOfReplay>maxPowerSet)
				maxPowerSet = numberOfReplay;
		}
		return maxPowerSet;
	}


	/*
	 * Splits the current cluster into size/k clusters
	 */
	public static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> Set<Cluster<E, N>> splitCluster(Cluster<E,N> cluster, int k) {
		//System.out.println("****** Starting splitting a large cluster: it has "+cluster.getEdges().size()+" edges");
		Object[] edges = cluster.getEdges().toArray();

		int partitionNumber = 0;
		try {
			partitionNumber = (edges.length / k);
		} catch (ArithmeticException e1) {
			e1.printStackTrace();
		}

		Set<Cluster<E, N>> result = new HashSet<Cluster<E, N>>();

		// Generate a random set of clusters!
		for (int i=0; i<partitionNumber; i++) {
			Set<E> e = new HashSet<E>();
			for(int j=0; j<k; j++) {
				e.add((E) edges[(i*k)+j]);
			}
			result.add(new Cluster<E, N>(e));
		}

		// if the above division has been rounded down, then add another cluster with the remaning edges
		if ((partitionNumber*k) < edges.length) {
			Set<E> e = new HashSet<E>();
			for (int i=(partitionNumber*k); i<edges.length; i++)
				e.add((E) edges[i]);
			result.add(new Cluster<E, N>(e));
		}

		return result;

		/* //Compute all the partitions of the set of edges 
    	Set<Set<Set<E>>> edgePartitions = Utils.computeCombinations(edges, partitionNumber);
	    System.out.println("****** The partition has "+edgePartitions.size()+" different edge partitions.");

	    Map<Set<Set<E>>,Double> maxIOSizeMap = new HashMap<Set<Set<E>>, Double>();
	     for each edgePartition i compute size(2^(I_i) \times 2^(O_i))), which is a number that is the number of replay I should perform
		 * for each cluster.

	   	for (Set<Set<E>> edgePartition : edgePartitions) {
	   		maxIOSizeMap.put(edgePartition, computeIOPartitionMaxPowerSet(edgePartition));
	   	}

	   	Entry<Set<Set<E>>,Double> minMaxPartition = Collections.min(maxIOSizeMap.entrySet(), new Comparator<Entry<Set<Set<E>>,Double>>(){
	   		public int compare(Entry<Set<Set<E>>,Double> entry1, Entry<Set<Set<E>>,Double> entry2){
	   			return entry1.getValue().compareTo(entry2.getValue());	
	   		}
	   	});
	   	System.out.println("SELECTED PARTITION");
	   	printPartition(minMaxPartition.getKey());

	   	Set<Cluster<E,N>> splittedClusters = new HashSet<>();

	   	for (Set<E> clusterEdges : minMaxPartition.getKey()) {
	   		Cluster<E,N> aSplittedCluster = new Cluster<E,N>(clusterEdges);
	   			splittedClusters.add(aSplittedCluster);
		}

	   	return splittedClusters;*/
	}



	/*
	 * Prints the partition(s) set
	 */
	private static <E extends AbstractDirectedGraphEdge, N extends AbstractDirectedGraphNode> void printPartition(Set<Set<E>> edgePartition){
		Set<Set<N>> vInputNodes = new HashSet<Set<N>>();
		Set<Set<N>> vOutputNodes = new HashSet<Set<N>>();
		for (int i = 0; i < edgePartition.size(); i++) {
			Set<E> set = (Set<E>) edgePartition.toArray()[i]; 
			System.out.print("{");
			for (E e : set) {
				System.out.print(e.getSource().toString()+" "+e.getTarget().toString()+", ");
			}
			System.out.println("}");
			System.out.println("*******");
		}
	}

	private static <E extends AbstractDirectedGraphEdge> void printPartitions(Set<Set<Set<E>>> edgePartitions){
		for (Set<Set<E>> edgePartition : edgePartitions) {
			printPartition(edgePartition);
		}
	} 

}
