package org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by demas on 22/08/16.
 */
public class Utils {

    public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
    
    
    public static void main(String[] args) {
    	Set<Integer> set = new HashSet<>();
    	for (int i=0; i<4; i++) {
    		set.add(i);
    	}
    	List<Integer> input = new LinkedList<Integer>(set);
    	System.out.println("The length of the result should be: " + input + "! =" + factorial(input.size()));
    	
    	// Perform a copy first
    	List<Integer> inputCopy = new LinkedList<Integer>(input);
    	List<List<Integer>> result = generatePerm(inputCopy);
    	System.out.println(result);
    	System.out.println("The length of the result is : " + result.size());
    	
    	System.out.println("Input is: " + input);
    	System.out.println("Input is: " + inputCopy);
    }
    
    
    public static int factorial(int n) {
    	if (n==1)
    		return n;
    	return n * factorial(n-1);
    }
    
    
    /*
     * DOES SIDE EFFECT ON THE INPUT!!! PERFORM A COPY BEFORE!
     * Returns all permutations of a given List. It works also with duplicates, checked!
     */
    public static <E> List<List<E>> generatePerm(List<E> original) {
        if (original.size() == 0) { 
          List<List<E>> result = new ArrayList<List<E>>();
          result.add(new ArrayList<E>());
          return result;
        }
        E firstElement = original.remove(0);
        List<List<E>> returnValue = new ArrayList<List<E>>();
        List<List<E>> permutations = generatePerm(original);
        for (List<E> smallerPermutated : permutations) {
          for (int index=0; index <= smallerPermutated.size(); index++) {
            List<E> temp = new ArrayList<E>(smallerPermutated);
            temp.add(index, firstElement);
            returnValue.add(temp);
          }
        }
        return returnValue;
      }
    
    
    
    	/*
    	 * Given a set it returns all partitions of the set. The first level of the List nesting is the number of partitions:
    	 * result.get(0) returns the original set, i.e., the single partition of the set;
    	 * result.get(1) returns all possible couples that partition the original set;
    	 * result.get[2] returns all possible triples that partition the original set...
    	 * ...
    	 * until result.get[originalSet.size()-1] where each partition is a singleton element.
    	 */
        public static <T> List<List<List<List<T>>>> getAllPartitions(Set<T> set) {
        	
        	List<T> list= new ArrayList<T>(set);
        	List<List<List<List<T>>>> result = new ArrayList<>();

            int cnt = 0;
            for(int i=1; i<=list.size(); i++) {
                List<List<List<T>>> ret = helper(list, i);
                cnt += ret.size();
                //System.out.println(ret);
                result.add(i-1, ret);
            }
            //System.out.println("Number of partitions: " + cnt);
            return result;
        }

        // partition f(n, m)
        private static <T> List<List<List<T>>> helper(List<T> ori, int m) {
            List<List<List<T>>> ret = new ArrayList<>();
            if(ori.size() < m || m < 1) return ret;

            if(m == 1) {
                List<List<T>> partition = new ArrayList<>();
                partition.add(new ArrayList<>(ori));
                ret.add(partition);
                return ret;
            }

            // f(n-1, m)
            List<List<List<T>>> prev1 = helper(ori.subList(0, ori.size() - 1), m);
            for(int i=0; i<prev1.size(); i++) {
                for(int j=0; j<prev1.get(i).size(); j++) {
                    // Deep copy from prev1.get(i) to l
                    List<List<T>> l = new ArrayList<>();
                    for(List<T> inner : prev1.get(i)) {
                        l.add(new ArrayList<>(inner));
                    }

                    l.get(j).add(ori.get(ori.size()-1));
                    ret.add(l);
                }
            }

            List<T> set = new ArrayList<>();
            set.add(ori.get(ori.size() - 1));
            // f(n-1, m-1)
            List<List<List<T>>> prev2 = helper(ori.subList(0, ori.size() - 1), m - 1);
            for(int i=0; i<prev2.size(); i++) {
                List<List<T>> l = new ArrayList<>(prev2.get(i));
                l.add(set);
                ret.add(l);
            }

            return ret;
        }
    
    
    public static <T> Set<T> copySet(Set<T> originalSet) {
    	Set<T> result = new HashSet<T>();
    	for (T t : originalSet)
    		result.add(t);
    	return result;
    }
    
}
