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

import java.util.Map;
import java.util.Set;

import edu.cmu.cs.crystal.cfg.ICFGNode;
import edu.cmu.cs.crystal.flow.IAbstractLatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;

/**
 * Encapsulates the results of running an analysis.
 * 
 * Package private, because we'd like to avoid this being referenced 
 * throughout Crystal, but different flow analysis library classes might
 * potentially want to use this. 
 * 
 * @author Nels Beckman
 *
 */
public class AnalysisResult<LE, N, OP extends IAbstractLatticeOperations<LE, N>> {

	private final Map<N, Set<ICFGNode<N>>> nodeMap;
	private final Map<ICFGNode<N>, IResult<LE>> labeledResultsAfter;
	private final Map<ICFGNode<N>, IResult<LE>> labeledResultsBefore;
	private final OP lattice;
	
	private final ICFGNode<N> cfgStartNode;
	private final ICFGNode<N> cfgEndNode;
	
	/**
	 * Creates copies of the given maps to encapsulate a new, 
	 * un-modifiable result of an analysis. 
	 * 
	 * @param _nm
	 * @param _lra
	 * @param _lrb
	 * @param _l
	 */
	public AnalysisResult(Map<N, Set<ICFGNode<N>>> _nm,
				Map<ICFGNode<N>, IResult<LE>> _lra,
				Map<ICFGNode<N>, IResult<LE>> _lrb,
				OP _l, ICFGNode<N> _startNode, ICFGNode<N> _endNode) {
		nodeMap = 
			java.util.Collections.unmodifiableMap(
					new java.util.HashMap<N, Set<ICFGNode<N>>>(_nm));
		labeledResultsAfter = 
			java.util.Collections.unmodifiableMap(
					new java.util.HashMap<ICFGNode<N>, IResult<LE>>(_lra));
		labeledResultsBefore =
			java.util.Collections.unmodifiableMap(
					new java.util.HashMap<ICFGNode<N>, IResult<LE>>(_lrb));	
		lattice = _l;
		cfgStartNode = _startNode;
		cfgEndNode = _endNode;
	}

	public Map<N, Set<ICFGNode<N>>> getNodeMap() {
		return nodeMap;
	}

	public Map<ICFGNode<N>, IResult<LE>> getLabeledResultsAfter() {
		return labeledResultsAfter;
	}

	public Map<ICFGNode<N>, IResult<LE>> getLabeledResultsBefore() {
		return labeledResultsBefore;
	}

	public OP getLattice() {
		return lattice;
	}

	public ICFGNode<N> getCfgStartNode() {
		return this.cfgStartNode;
	}

	public ICFGNode<N> getCfgEndNode() {
		return this.cfgEndNode;
	}
	
}
