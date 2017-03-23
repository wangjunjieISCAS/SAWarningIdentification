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
package edu.cmu.cs.crystal.tac;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.IFlowAnalysis;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This interface defines methods to map AST data structures to TAC variables.
 * These methods can for instance be used by TAC analysis transfer functions.
 * 
 * NEB: This used to be called ITACAnalysisContext, but I renamed it and added a
 * common method getNode which was not previously part of this interface. 
 * 
 * @author Kevin Bierhoff
 * @author Nels Beckman
 * 
 * @see edu.cmu.cs.crystal.tac.ITACTransferFunction
 * @see edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction
 */
public interface ITACFlowAnalysis<LE> extends IFlowAnalysis<LE> {

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the instruction.
	 * 
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param instr		the {@link TACInstruction} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the instruction.  Or null if the node doesn't
	 * 					have a corresponding control flow node.
	 */
	public LE getResultsBefore(TACInstruction instr);

	/**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the instruction.
	 * 
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param instr		the {@link TACInstruction} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					before analyzing the instruction.  Or null if the node doesn't
	 * 					have a corresponding control flow node.
	 */
	public LE getResultsAfter(TACInstruction instr);

	/**
	 * Retrieves the analysis state that exists <b>before</b> analyzing the instruction.
	 * 
	 * Before is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param instr		the {@link TACInstruction} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					after analyzing the instruction.  Or null if the node doesn't
	 * 					have a corresponding control flow node.
	 */
	public IResult<LE> getLabeledResultsBefore(TACInstruction instr);

	/**
	 * Retrieves the analysis state that exists <b>after</b> analyzing the instruction.
	 * 
	 * After is respective to normal program flow and not the direction of the analysis.
	 * 
	 * @param instr		the {@link TACInstruction} of interest 
	 * @return			the lattice that represents the analysis state 
	 * 					after analyzing the instruction.  Or null if the node doesn't
	 * 					have a corresponding control flow node.
	 */
	public IResult<LE> getLabeledResultsAfter(TACInstruction instr);

	/**
	 * Returns the TAC variable for a given ASTNode <i>after
	 * previously analyzing the method surrounding the given node.</i>
	 * It is the caller's responsibility to make sure to call this
	 * method only when analysis results for the surrounding method
	 * are available.
	 * @param node AST node in the previously analyzed method.
	 * @return The TAC variable for a given ASTNode.
	 */
	public Variable getVariable(ASTNode node);

	/**
	 * Returns the <b>this</b> variable for a given method <i>after
	 * previously analyzing that method.</i>
	 * It is the caller's responsibility to make sure to call this
	 * method only when analysis results for the given method
	 * are available.
	 * @param methodDecl The method for which <b>this</b> is requested.
	 * @return The <b>this</b> variable for the given method. 
	 */
	public ThisVariable getThisVariable(MethodDeclaration methodDecl);

	/**
	 * Returns the implicit <b>this</b> variable for accessing a
	 * given method or field  <i>after previously analyzing the method
	 * surrounding the access.</i>
	 * It is the caller's responsibility to make sure to call this
	 * method only when analysis results for the method surrounding the
	 * access are available.
	 * @param accessedElement
	 * @return the implicit this for the accessed element
	 */
	public ThisVariable getImplicitThisVariable(IBinding accessedElement);

	/**
	 * Returns the variable for a given parameter or local <i>after
	 * previously analyzing the method declaring the parameter or local.</i>
	 * It is the caller's responsibility to make sure to call this
	 * method only when analysis results for the declaring method 
	 * are available.
	 * @param varBinding Binding of a local or parameter.
	 * @return the variable for the given parameter or local.
	 */
	public SourceVariable getSourceVariable(IVariableBinding varBinding);

	/**
	 * Returns for error-reporting purposes
	 * a AST node that surrounds or is represented by
	 * a variable mentioned in a given instruction.
	 * @param x A variable.
	 * @param instruction Instruction that mentions <code>x</code>
	 * @return A AST node that surrounds or is represented by
	 * a variable mentioned in a given instruction.
	 */
	public ASTNode getNode(Variable x, TACInstruction instruction);
}