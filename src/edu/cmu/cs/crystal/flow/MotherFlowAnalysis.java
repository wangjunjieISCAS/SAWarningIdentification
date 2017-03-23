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
package edu.cmu.cs.crystal.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.cfg.ICFGNode;
import edu.cmu.cs.crystal.flow.worklist.AnalysisResult;
import edu.cmu.cs.crystal.flow.worklist.WorklistFactory;
import edu.cmu.cs.crystal.flow.worklist.WorklistTemplate;
import edu.cmu.cs.crystal.util.Option;
import edu.cmu.cs.crystal.util.Utilities;

/**
 * Abstract base class for flow analyses that implements a worklist algorithm
 * and provides various methods to access analysis results.  Methods are analyzed
 * lazily when results for AST nodes inside a method are requested.
 * 
 * @author David Dickey
 * @author Jonathan Aldrich
 * @author Kevin Bierhoff
 * @author Ciera Jaspan 
 * 
 * @param <LE> the type that represents the analysis knowledge
 */
public abstract class MotherFlowAnalysis<LE> implements IFlowAnalysis<LE> {
	
	public static final Logger log = Logger.getLogger(MotherFlowAnalysis.class.getName());
	
	/**
	 * the resulting lattices for each node in the control flow graph
	 * for the method analyzed last.  Whenever results for an AST node
	 * are requested that are not in the last analyzed method, the current
	 * results are tossed and replaced with results for the method surrounding
	 * that node.
	 */	 
	protected Map<ICFGNode<ASTNode>, IResult<LE>> labeledResultsBefore = new HashMap<ICFGNode<ASTNode>, IResult<LE>>();
	protected Map<ICFGNode<ASTNode>, IResult<LE>> labeledResultsAfter = new HashMap<ICFGNode<ASTNode>, IResult<LE>>();
	
	/**
	 * Information about the method that was analyzed last.  
	 */
	private MethodDeclaration currentMethod;
	private ILatticeOperations<LE> currentLattice;
	
	/**
	 * Map to find CFGNodes corresponding to AST nodes.
	 */
	private Map<ASTNode, Set<ICFGNode<ASTNode>>> nodeMap = new HashMap<ASTNode, Set<ICFGNode<ASTNode>>>();

	private final WorklistFactory factory;

	private ICFGNode<ASTNode> cfgStartNode;

	private ICFGNode<ASTNode> cfgEndNode;
	
	/**
	 * Initializes a fresh flow analysis object.
	 */
	public MotherFlowAnalysis() {
		this.factory = new WorklistFactory();
	}
	
	/**
	 * Use the given progress monitor to cancel subsequent flow analysis runs.
	 * Previously computed results may still be available.
	 * <i>If</i> a monitor is set then subsequent accesses to analysis results 
	 * may through a {@link java.util.concurrent.CancellationException} which, 
	 * if not caught, will abort the current overall Crystal analysis job.
	 * @param monitor Monitor to listen for cancellation or {@link Option#none()}
	 * if worklist runs should not be canceled.
	 */
	public void setMonitor(Option<IProgressMonitor> monitor) {
		this.factory.setMonitor(monitor.isNone() ? null : monitor.unwrap());
	}
	
	@Deprecated
	public LE getResultsBefore(ASTNode node) {
    	return getResultsBeforeCFG(node);
    }
    
	public LE getResultsBeforeCFG(ASTNode node) {
    	LE result = getResultsOrNull(node, false, false);
		return result == null ? currentLattice.bottom() : result;
    }
	
    public LE getResultsBeforeAST(ASTNode node) {
    	LE result = getResultsOrNull(node, false, true);
		return result == null ? currentLattice.bottom() : result;
    }

   @Deprecated
    public LE getResultsAfter(ASTNode node) {
   		return getResultsAfterCFG(node);
    }
    
    public LE getResultsAfterCFG(ASTNode node) {
    	LE result = getResultsOrNull(node, true, false);
    	return result == null ? currentLattice.bottom() : result;
    }
    

    public LE getResultsAfterAST(ASTNode node) {
    	LE result = getResultsOrNull(node, true, true);
    	return result == null ? currentLattice.bottom() : result;
    }


	/**
	 * Retrieves the analysis state for a given node.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @param getAfter  true if we want the results after analyzing the node, false if we want the results before analyzing
	 * @param useAST	true if we want the results for the entire tree of this AST, false if we want the results only before/after
	 *                  the given node and not any subnodes.
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the node.  Will be <code>null</code> if the node doesn't
	 * 					have a corresponding control flow node.
	 */
    protected LE getResultsOrNull(ASTNode node, boolean getAfter, boolean useAST) {
		if(nodeMap.containsKey(node) == false)
			performAnalysisOnSurroundingMethodIfNeeded(node);

		Set<ICFGNode<ASTNode>> cfgnodes = nodeMap.get(node);

    	if(cfgnodes == null || cfgnodes.isEmpty()) {
    		if(log.isLoggable(Level.FINE))
    			log.fine("Unable to get results after node: " + getNodeDebugInfo(node));
    		return null;
    	}
    	else if(cfgnodes.size() == 1) {
			ICFGNode<ASTNode> cfgNode = cfgnodes.iterator().next();
			if (getAfter)
				return getResultAfter(useAST ? cfgNode.getEnd() : cfgNode);
			else
		   		return getResultBefore(useAST ? cfgNode.getStart() : cfgNode);
	   	}
    	else {
	    	HashMap<ICFGNode<ASTNode>, LE> results = new HashMap<ICFGNode<ASTNode>, LE>();
	    	for(ICFGNode<ASTNode> n : cfgnodes) {
	    		ICFGNode<ASTNode> resultNode;
	    		LE result;
	    		if (getAfter) {
	    			resultNode = useAST ? n.getEnd() : n;
	    			result = getResultAfter(resultNode);
	    		}
	    		else {
	    			resultNode = useAST ? n.getStart() : n;
	    			result = getResultBefore(resultNode);
	    		}
	    		
	    		if(result != null)
	    			results.put(resultNode, result);
	    	}
	    	return mergeResults(results, node);
    	}
    }


    public LE getEndResults(MethodDeclaration decl) {
		if (this.currentMethod != decl) {
			performAnalysisOnSurroundingMethodIfNeeded(decl);
		}
		
		// NEB: From looking around there appear to be no
		// outgoing edges from a method declaration, which
		// is what cfgEndNode usually is. Therefore I believe
		// result should be the results before the last node.
		LE result = getResultBefore(this.cfgEndNode);
		return result == null ? currentLattice.bottom() : result;
	}

 	public LE getStartResults(MethodDeclaration decl) {
		if (this.currentMethod != decl) {
			performAnalysisOnSurroundingMethodIfNeeded(decl);
		}
		
		LE result = getResultBefore(this.cfgStartNode);
		return result == null ? currentLattice.bottom() : result;
	}

	private String getNodeDebugInfo(ASTNode node) {
    	return node.toString() + " type: " + ASTNode.nodeClassForType(node.getNodeType()).getName() +
    	  (node.getParent() != null ? 
    			  " parent type: " + ASTNode.nodeClassForType(node.getParent().getNodeType()).getName() :
    				  "");
    }

    protected LE mergeResults(HashMap<ICFGNode<ASTNode>, LE> results, ASTNode node) {
    	if(results.isEmpty())
    		return null; // shortcut
    	LE result = null;
    	for(LE r : results.values()) {
    		if(result == null)
    			result = currentLattice.copy(r);
    		else
    			result = currentLattice.join(result, currentLattice.copy(r), node);
    	}
		return result;
	}

    public IResult<LE> getLabeledResultsBefore(ASTNode node) {
		if(nodeMap.containsKey(node) == false)
			performAnalysisOnSurroundingMethodIfNeeded(node);

		Set<ICFGNode<ASTNode>> cfgnodes = nodeMap.get(node);

    	if(cfgnodes == null || cfgnodes.isEmpty()) {
    		if(log.isLoggable(Level.FINE))
    			log.fine("Unable to get results before node: " + getNodeDebugInfo(node));
    		return new SingleResult<LE>(currentLattice.bottom());
    	}

    	IResult<LE> result; 
    	if(cfgnodes.size() == 1) {
    		result = getLabeledResultBefore(cfgnodes.iterator().next());
    	}
    	else {
	    	HashMap<ICFGNode<ASTNode>, IResult<LE>> results = new HashMap<ICFGNode<ASTNode>, IResult<LE>>();
	    	for(ICFGNode<ASTNode> n : cfgnodes) {
	    		result = getLabeledResultBefore(n);
	    		if(result != null)
	    			results.put(n, result);
	    	}
	    	result = mergeLabeledResults(results);
    	}
    	return result == null ? new SingleResult<LE>(currentLattice.bottom()) : result;
   	}
    
    public IResult<LE> getLabeledResultsAfter(ASTNode node) {
		if(nodeMap.containsKey(node) == false)
			performAnalysisOnSurroundingMethodIfNeeded(node);

		Set<ICFGNode<ASTNode>> cfgnodes = nodeMap.get(node);

    	if(cfgnodes == null || cfgnodes.isEmpty()) {
    		if(log.isLoggable(Level.FINE))
    			log.fine("Unable to get results after node: " + getNodeDebugInfo(node));
    		return new SingleResult<LE>(currentLattice.bottom());
    	}

    	IResult<LE> result;
    	if(cfgnodes.size() == 1) {
    		result = getLabeledResultAfter(cfgnodes.iterator().next());
    	}
    	else {
	    	HashMap<ICFGNode<ASTNode>, IResult<LE>> results = new HashMap<ICFGNode<ASTNode>, IResult<LE>>();
	    	for(ICFGNode<ASTNode> n : cfgnodes) {
	    		result = getLabeledResultAfter(n);
	    		if(result != null)
	    			results.put(n, result);
	    	}
	    	result = mergeLabeledResults(results);
    	}
    	return result == null ? new SingleResult<LE>(currentLattice.bottom()) : result;
   	}

	public IResult<LE> getLabeledEndResult(MethodDeclaration d) {
		if( this.currentMethod != d ) {
			performAnalysisOnSurroundingMethodIfNeeded(d);
		}
		
		IResult<LE> result = getLabeledResultBefore(this.cfgEndNode);
		return result == null ? new SingleResult<LE>(currentLattice.bottom()) : result; 
	}

	public IResult<LE> getLabeledStartResult(MethodDeclaration d) {
		if( this.currentMethod != d ) {
			performAnalysisOnSurroundingMethodIfNeeded(d);
		}
		
		IResult<LE> result = getLabeledResultBefore(this.cfgStartNode);
		return result == null ? new SingleResult<LE>(currentLattice.bottom()) : result; 
	}
    
	/**
	 * Merges the given results into one, possibly <code>null</code>, result.
	 * This method uses {@link IResult#join(IResult)}.
	 * @param results The results to be merged.
	 * @return The result of merging the given results into one, or <code>null</code>
	 * if <code>results</code> is empty or only contains <code>null</code> values.
	 */
	protected IResult<LE> mergeLabeledResults(
			HashMap<ICFGNode<ASTNode>, IResult<LE>> results) {
		IResult<LE> result = null;
		for(IResult<LE> r : results.values()) {
			if(result == null)
				result = r;
			else if(r != null)
				result = result.join(r, currentLattice);
		}
		return result;
	}

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the node, if any.
	 * 
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the node.  Or <code>null</code> if the node doesn't
	 * 					have a corresponding control flow node.
	 */
    protected LE getResultBefore(ICFGNode<ASTNode> node) {
    	return mergeLabeledResult(getLabeledResultBefore(node), node.getASTNode());
    }


    /**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the node, if any.
	 * 
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the node.  Or <code>null</code> if the node doesn't
	 * 					have a corresponding control flow node.
	 */
    protected LE getResultAfter(ICFGNode<ASTNode> node) {
    	return mergeLabeledResult(getLabeledResultAfter(node), node.getASTNode());
    }


    protected LE mergeLabeledResult(IResult<LE> labeledResult, ASTNode node) {
    	if(labeledResult == null)
    		return null;
    	LE result = null;
    	for(ILabel label : labeledResult.keySet()) {
    		if(result == null)
    			result = checkNull(currentLattice.copy(labeledResult.get(label)));
    		else
    			result = checkNull(currentLattice.join(result, checkNull(currentLattice.copy(labeledResult.get(label))), node));
    	}
		return result;
	}

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the node, if any.
	 * 
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					after analyzing the node.  Or <code>null</code> if the node doesn't
	 * 					have a corresponding control flow node.
	 */
    protected IResult<LE> getLabeledResultBefore(ICFGNode<ASTNode> node) {
    	// Retrieve results for the ControlFlowNode
    	if(labeledResultsBefore.containsKey(node))
    		return labeledResultsBefore.get(node);
    	// If results have not yet been collected, collect and retrieve again 
    	performAnalysisOnSurroundingMethodIfNeeded(node.getASTNode());
    	if(labeledResultsBefore.containsKey(node))
    		return labeledResultsBefore.get(node);
		if(log.isLoggable(Level.FINE))
			log.fine("Unable to get results after CFG node [" + node + "]");
		return null;
   	}
    
    /**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the node, if any.
	 * 
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					after analyzing the node.  Or <code>null</code> if the node doesn't
	 * 					have a corresponding control flow node.
	 */
    protected IResult<LE> getLabeledResultAfter(ICFGNode<ASTNode> node) {
    	// Retrieve results for the ControlFlowNode
    	if(labeledResultsAfter.containsKey(node))
    		return labeledResultsAfter.get(node);
    	// If results have not yet been collected, collect and retrieve again 
    	performAnalysisOnSurroundingMethodIfNeeded(node.getASTNode());
    	if(labeledResultsAfter.containsKey(node))
    		return labeledResultsAfter.get(node);
		if(log.isLoggable(Level.FINE))
			log.fine("Unable to get results after CFG node [" + node + "]");
		return null;
   	}
    
    /**
     * Runs worklist algorithm on method the given node is part of.
     * @param node A node that must be inside a method.
     */
    private void performAnalysisOnSurroundingMethodIfNeeded(ASTNode node) {
    	MethodDeclaration decl = Utilities.getMethodDeclaration(node);
    	if (decl != null)
    		switchToMethod(decl);
    }
    
    /**
     * Runs worklist algorithm on given method, if not already analyzed.
     * @param node A node that must be inside a method.
     * 
     * Nels: Caching only works if this was the last method to be analyzed? Is there an assumption
     * that methods are analyzed in order an never returned to?
     */
    protected void switchToMethod(MethodDeclaration methodDecl) {
    	if(methodDecl != currentMethod)
    		performAnalysis(methodDecl);
    }
    
    private void performAnalysis(MethodDeclaration methodDecl) {
    	currentMethod = methodDecl;
    	WorklistTemplate<LE, ASTNode, ILatticeOperations<LE>> worklist = createWorklist(methodDecl);
    	AnalysisResult<LE, ASTNode, ILatticeOperations<LE>> result = worklist.performAnalysis();
    	labeledResultsBefore = result.getLabeledResultsBefore();
    	labeledResultsAfter = result.getLabeledResultsAfter();
    	nodeMap = result.getNodeMap();
    	currentLattice = result.getLattice();
    	cfgStartNode = result.getCfgStartNode();
    	cfgEndNode = result.getCfgEndNode();
    }
    
    protected WorklistTemplate<LE, ASTNode, ILatticeOperations<LE>> createWorklist(MethodDeclaration methodDecl) {
    	IFlowAnalysisDefinition<LE> transferFunction = createTransferFunction(methodDecl);
    	if(transferFunction instanceof IBranchSensitiveTransferFunction)
    		return factory.createBranchSensitiveWorklist(methodDecl, (IBranchSensitiveTransferFunction<LE>) transferFunction);
    	if(transferFunction instanceof ITransferFunction)
    		return factory.createBranchInsensitiveWorklist(methodDecl, (ITransferFunction<LE>) transferFunction);
    	throw new IllegalStateException("Unknown type of transfer function: " + transferFunction);
	}

	protected abstract IFlowAnalysisDefinition<LE> createTransferFunction(MethodDeclaration method);

	/**
     * Returns most recently analyzed method.
     * @return Most recently analyzed method 
     * or <code>null</code> if no method was analyzed yet.
     */
    protected final MethodDeclaration getCurrentMethod() {
		return currentMethod;
	}

    /**
     * Finds the method surrounding a given node and throws an exception
     * if the given node is not inside a method.
     * @param forNode
     * @return Method surrounding given node.  This method does not return
     * <code>null</code> and instead throws a <code>NullPointerException</code>.
     */
	protected static MethodDeclaration findSurroundingMethod(ASTNode forNode) {
		return checkNull(Utilities.getMethodDeclaration(forNode));
	}
	
	/**
	 * This method checks whether results for the given AST node are available.
	 * @param node
	 * @return
	 */
	protected final boolean hasResults(ASTNode node) {
		return nodeMap.containsKey(node);
	}

	
	/**
	 * Returns a given object only if non-<code>null</code>; throws an exception otherwise.
	 * @param <T>
	 * @param o An object
	 * @return The given object, if non-<code>null</code>.
	 * @throws NullPointerException If the given object is null.
	 */
	protected static <T> T checkNull(T o) {
		if(o == null)
			throw new NullPointerException("No value available.");
		return o;
	}
}