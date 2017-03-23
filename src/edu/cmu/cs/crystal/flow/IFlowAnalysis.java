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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * All flow analyses must be able to return the information defined by this
 * interface.
 * 
 * @author Nels Beckman
 *
 * @param <LE>
 */
public interface IFlowAnalysis<LE> {

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the node, including <b>bottom</b>.
	 * 
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @deprecated Use {@link IFlowAnalysis#getResultsBeforeCFG(ASTNode)} instead.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the node.  Will be bottom if the node doesn't
	 * 					have a corresponding control flow node.
	 * @see IFlowAnalysis#getResultsBeforeCFG(ASTNode)
	 */
	@Deprecated
	public LE getResultsBefore(ASTNode node);

	/**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the node, including <b>bottom</b>.
	 * 
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @deprecated use {@link IFlowAnalysis#getResultsAfterCFG(ASTNode)} instead.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the node.  Will be bottom if the node doesn't
	 * 					have a corresponding control flow node.
	 */
 	@Deprecated
	public LE getResultsAfter(ASTNode node);

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the node in the control flow graph.
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * When called on an expression, this returns the lattice that occurs before this expression
	 * is evaluated, but after all sub-expressions are evaluated. For example, when called on an
	 * assignment, it returns the lattice after the left and right sub-expressions are evaluated,
	 * but before the assignment itself takes place.
	 * 
	 * If there are multiple before lattices for this node, they are joined and returned. 
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return	the lattice that represents the analysis state 
	 * 					before analyzing the corresponding node in the control flow graph.
	 *                  Will be bottom if the node doesn't have a corresponding control flow node.
	 *                  
	 *@see IFlowAnalysis#getResultsBeforeAST(ASTNode)
	 *@see IFlowAnalysis#getLabeledResultsBefore(ASTNode)
	 */
	public LE getResultsBeforeCFG(ASTNode node);

	/**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the node in the control flow graph.
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * When called on an node, this returns the lattice that occurs after this node
	 * is evaluated, but after all sub-node are evaluated. In particular, calling this on a while node
	 * will return results after the while node is evaluated, but not after the entire statement in the AST
	 * 
	 * For most expressions, this returns the same result as {@link IFlowAnalysis#getResultsAfterAST(ASTNode)}
	 * 
	 * If there are multiple before lattices for this node, they are joined and returned. 
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return	the lattice that represents the analysis state 
	 * 					before analyzing the corresponding node in the control flow graph.
	 *                  Will be bottom if the node doesn't have a corresponding control flow node.
	 *                  
	 *@see IFlowAnalysis#getResultsAfterAST(ASTNode)
	 *@see IFlowAnalysis#getLabeledResultsAfter(ASTNode)
	 */
	public LE getResultsAfterCFG(ASTNode node);

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the node in the abstract syntax tree.
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * When called on an expression, this returns the lattice that occurs before all sub-expressions are evaluated.
	 * For example, when called on an assignment, it returns the lattice before either the left or right
	 * sub-expressions are evaluated.
	 * 
	 * If there are multiple before lattices for this node, they are joined and returned. 
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return	the lattice that represents the analysis state 
	 * 					before analyzing any node contained by the given node in the abstract syntax tree.
	 *                  Will be bottom if the node doesn't have a corresponding control flow node.
	 *                  
	 *@see IFlowAnalysis#getResultsBeforeCFG(ASTNode)
	 */
	public LE getResultsBeforeAST(ASTNode node);

	/**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the node in the abstract syntax tree.
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * When called on an node, this returns the lattice that occurs after this node
	 * and all sub-nodes are evaluated. In particular, calling this on a while node
	 * will return results after the entire while statement (including all sub-expressions) is evaluated.
	 * 
	 * For most expressions, this returns the same result as {@link IFlowAnalysis#getResultsAfterCFG(ASTNode)}
	 * 
	 * If there are multiple before lattices for this node, they are joined and returned. 
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return	the lattice that represents the analysis state 
	 * 					after analyzing any node contained by the given node in the abstract syntax tree.
	 *                  Will be bottom if the node doesn't have a corresponding control flow node.
	 *                  
	 *@see IFlowAnalysis#getResultsAfterCFG(ASTNode)
	 */
	public LE getResultsAfterAST(ASTNode node);

	 /**
     * Gets the lattice results at the start of a method. This
     * will probably be the same as the results from calling
     * {@link IFlowAnalysisDefinition#createEntryValue(MethodDeclaration)}
     * 
     * @param decl The method declaration to get results for
     * @return The lattice results at the beginning of the method.
     * 
     * @see IFlowAnalysisDefinition#createEntryValue(MethodDeclaration)
     */
 	public LE getStartResults(MethodDeclaration decl);
	
    /**
     * Gets the lattice results at the end of a method. This will
     * join all possible exits from a method, including explicit returns,
     * the default return, and exceptional exits.
     * 
     * @param decl The method declaration to get results for
     * @return The lattice results at the end of the method.
     */
	public LE getEndResults(MethodDeclaration decl);
	
	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the node in the control flow graph.
	 * Works like {@link  IFlowAnalysis#getResultsBeforeCFG(ASTNode)}, except it keeps all the labels
	 * separated in an IResult<LE>.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return	the IResult that represents the analysis state 
	 * 					before analyzing the corresponding node in the control flow graph, separated
	 * 					for each incoming edge.
	 *                  Will be bottom if the node doesn't have a corresponding control flow node.
	 *                  
	 *@see IFlowAnalysis#getResultsBeforeCFG(ASTNode)
	 */
	public IResult<LE> getLabeledResultsBefore(ASTNode node);

	/**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the node in the control flow graph.
	 * Works like {@link  IFlowAnalysis#getResultsAfterCFG(ASTNode)}, except it keeps all the labels
	 * separated in an IResult<LE>.
	 * 
	 * @param node		the {@link ASTNode} of interest 
	 * @return	the IResult that represents the analysis state 
	 * 					after analyzing the corresponding node in the control flow graph, separated
	 * 					for each incoming edge.
	 *                  Will be bottom if the node doesn't have a corresponding control flow node.
	 *                  
	 *@see IFlowAnalysis#getResultsAfterCFG(ASTNode)
	 */
	public IResult<LE> getLabeledResultsAfter(ASTNode node);

	 /**
     * Gets the lattice results at the start of a method.
     * 
     * Theoretically, this will be the same as {@link IFlowAnalysis#getStartResults(MethodDeclaration)} and
     * {@link IFlowAnalysisDefinition#createEntryValue(MethodDeclaration)}. However, it might be different
     * for a system which keeps multiple possible contexts in which the method could be called from based
     * on some specifications.
     * 
     * @param decl The method declaration to get results for
     * @return The analysis results at the beginning of the method, separated by incoming edge.
     * 
     * @see IFlowAnalysisDefinition#createEntryValue(MethodDeclaration)
     * @see IFlowAnalysis#getStartResults(MethodDeclaration)
     */
	public IResult<LE> getLabeledStartResult(MethodDeclaration d);
	
    /**
     * Gets the lattice results at the end of a method. This method should be used when the caller
     * wants to access different possible ending results, such as exceptional exits v. normal returns.
     * 
     * @param decl The method declaration to get results for
     * @return The analysis results at the end of the method.
     * 
     * @see IFlowAnalysis#getEndResults(MethodDeclaration)
     */
	public IResult<LE> getLabeledEndResult(MethodDeclaration d);
}