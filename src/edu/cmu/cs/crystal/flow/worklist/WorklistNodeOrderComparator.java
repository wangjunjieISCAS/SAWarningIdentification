/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,
 * Kevin Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 *
 * This file is part of Crystal.
 *
 * Crystal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Crystal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Crystal.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.flow.worklist;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.crystal.cfg.ICFGEdge;
import edu.cmu.cs.crystal.cfg.ICFGNode;
import edu.cmu.cs.crystal.cfg.IControlFlowGraph;

/**
 * Comparator to be used in ordering nodes in a worklist.  Best analysis performance
 * is achieved using <i>reverse post-order</i>.  
 * {@link #createPostOrderAndPopulateNodeMap(IControlFlowGraph, Map, boolean)} creates
 * a comparator for post-order; reversal should happen by removing nodes from the back
 * of the worklist.
 * @author Kevin Bierhoff
 * @since 3.3.0
 */
public class WorklistNodeOrderComparator implements Comparator<ICFGNode<?>> {
	
	/**
	 * Builds a post-order comparator for the nodes in the given CFG, in which
	 * a node is bigger than all of its successors (predecessors) if <code>isForward</code>
	 * is <code>true</code> (<code>false</code>), and populates a map from
	 * AST nodes to all their corresponding nodes in the given CFG.
	 * @param cfg
	 * @param nodeMap Node map to be populated (will not be cleared).
	 * @param isForward If <code>true</code> the CFG is traversed in the forward
	 * direction, meaning starting from the {@link IControlFlowGraph#getStartNode() start node} 
	 * following {@link ICFGNode#getOutputs() outgoing edges}; if
	 * <code>false</code> the traversal direction is reversed, meaning traversal starts
	 * from the {@link IControlFlowGraph#getEndNode() end node} and follows 
	 * {@link ICFGNode#getInputs() incoming edges}.
	 * @return Post-order comparator for the nodes in the given CFG
	 */
	public static <N> WorklistNodeOrderComparator createPostOrderAndPopulateNodeMap(
			final IControlFlowGraph<N> cfg, 
			final Map<N, Set<ICFGNode<N>>> nodeMap, 
			final boolean isForward) {
		Map<ICFGNode<?>, Integer> order = new HashMap<ICFGNode<?>, Integer>();
		
		// iterative post-order visit / depth-first search (DFS)  
		// "visits" (numbers) nodes after all their children are 
		// visited (ie., when nodes become "black")
		int cur = 0;
		LinkedList<Object> spine = new LinkedList<Object>();
		ICFGNode<N> node = isForward ? cfg.getStartNode() : cfg.getEndNode();
		order.put(node, null); 
		spine.addFirst(node);
		spine.addFirst((isForward ? node.getOutputs() : node.getInputs()).iterator());
		
		newNode:
		while(spine.isEmpty() == false) {
			Iterator<ICFGEdge<N>> it = (Iterator<ICFGEdge<N>>) spine.peek();
			while(it.hasNext()) {
				node = isForward ? it.next().getSink() : it.next().getSource();
				if(order.containsKey(node) == false) {
					order.put(node, null);
					spine.addFirst(node);
					spine.addFirst((isForward ? node.getOutputs() : node.getInputs()).iterator());
					continue newNode;
				}
			}
			spine.removeFirst();
			node = (ICFGNode<N>) spine.removeFirst();
			// number nodes in increasing order
			if(order.put(node, cur++) != null)
				throw new IllegalStateException("Node already visited: " + node);
			// also register node so that we can find it given its AST node
			registerCfgNode(nodeMap, node);
		}
		
//		visitPostOrder(cfg.getStartNode(), order, Integer.MAX_VALUE);
		return new WorklistNodeOrderComparator(order);
	}
	
	/**
	 * Add the given CFG node to the node map.
	 * @param nodeMap
	 * @param cfgNode
	 */
	private static <N> void registerCfgNode(
			Map<N, Set<ICFGNode<N>>> nodeMap,
			ICFGNode<N> cfgNode) {
		N astnode = cfgNode.getASTNode();
		if(astnode == null)
			return;
		Set<ICFGNode<N>> cfgnodes = nodeMap.get(astnode);
		if (cfgnodes == null) {
			cfgnodes = new HashSet<ICFGNode<N>>();
			nodeMap.put(astnode, cfgnodes);
		}
		cfgnodes.add(cfgNode);
	}

	// recursive implementation of depth-first search for use instead of iterative implementation
//	private static int visitPostOrder(ICFGNode node, Map<ICFGNode, Integer> order, int seed) {
//		if(order.containsKey(node))
//			return seed;
//		order.put(node, null);
//		for(ICFGEdge e : node.getOutputs()) {
//			seed = visitPostOrder(e.getSink(), order, seed); 
//		}
//		order.put(node, seed);
//		return --seed;
//	}
	
	/** Maps CFG nodes to a number that indicates their relative position in the order. */
	private Map<ICFGNode<?>, Integer> order;
	
	/**
	 * Create a comparator from the given ordering map.
	 * @param order
	 */
	private WorklistNodeOrderComparator(Map<ICFGNode<?>, Integer> order) {
		this.order = order;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ICFGNode<?> node1, ICFGNode<?> node2) {
		return order.get(node1).compareTo(order.get(node2));
	}

}
