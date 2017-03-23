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
package edu.cmu.cs.crystal.tac.eclipse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.SuperVariable;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.TypeVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Abstract base class for 3-Address-Code instructions built from Eclipse AST nodes.
 * {@link ITACTransferFunction} lists subclasses that define the different
 * types of instructions.  Additional (abstract and/or package-private) classes 
 * simplify 3-Address-Code generation from AST nodes.  
 * 
 * @author Kevin Bierhoff
 *
 * @param <E> Parameter used internally to precisely type underlying AST node.
 * <i>This parameter has no effect on 3-address-code clients</i> and does therefore
 * not appear in {@link TACInstruction} and derived interfaces.  Instead we
 * define {@link #getNode()} with a more precise return type in interfaces
 * derived from {@link TACInstruction}, where applicable.
 * 
 * @see ITACTransferFunction
 * @see ITACBranchSensitiveTransferFunction
 */
abstract class AbstractTACInstruction<E extends ASTNode> implements TACInstruction {
	
	private final E node;
	
	private final IEclipseVariableQuery tac;

	/**
	 * Create TAC instruction for a given AST node and query object.
	 * @param node AST node this instruction is based on (usually
	 * one instruction per AST node.
	 * @param tac This interface determines variables for
	 * AST nodes.  Usually used to query the variables representing
	 * subexpressions of the given node.
	 */
	public AbstractTACInstruction(E node, IEclipseVariableQuery tac) {
		this.node = node;
		this.tac = tac;
	}

	public E getNode() {
		return node;
	}

	/**
	 * Helper method to access {@link IEclipseVariableQuery#variable(ASTNode)}
	 * to determine the variable representing a given expression.
	 * @param node An expression AST node.
	 * @return Variable representing the result of the given expression.
	 */
	protected Variable variable(Expression node) {
		return tac.variable(node);
	}
	
	/**
	 * Helper method to access {@link IEclipseVariableQuery#variable(IVariableBinding)}
	 * to determine the variable for a given binding.
	 * @param binding An expression AST node.
	 * @return Variable representing the result of the given expression.
	 */
	protected Variable targetVariable(ASTNode node) {
		if(node instanceof Expression)
			return tac.variable(node);
		if(node instanceof VariableDeclaration)
			return tac.sourceVariable(((VariableDeclaration) node).resolveBinding());
		throw new IllegalArgumentException("Node does not have a target: " + node);
	}
	
	/**
	 * Calls {@link #variable(Expression)} for every expression in <code>nodes</code>
	 * and returns the resulting variables in an immutable list.
	 * @param nodes A list of expression AST nodes.
	 * @return Variables representing the results of the given expression expressions.
	 */
	protected List<Variable> variables(List<Expression> nodes) {
		List<Variable> result = new ArrayList<Variable>(nodes.size());
		for(Expression e : nodes) {
			result.add(variable(e));
		}
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * Returns the unqualified <code>this</code> variable for the receiver, if
	 * the surrounding method is an instance method.
	 * @return the unqualified <code>this</code> variable for the receiver, if
	 * the surrounding method is an instance method; <code>null</code> otherwise.
	 */
	protected ThisVariable receiverVariable() {
		return tac.thisVariable();
	}
	
	/**
	 * Helper method to access {@link IEclipseVariableQuery#implicitThisVariable(IBinding)}
	 * to determine the implicit <b>this</b> variable for a method call or field access.
	 * @param accessedElement The element being accessed with an implicit <b>this</b>. 
	 * Must be a {@link IMethodBinding} for a method or constructor
	 * or a {@link IVariableBinding} for a field.
	 * @return Implicit <b>this</b> variable for a method call or field access.
	 */
	protected ThisVariable implicitThisVariable(IBinding accessedElement) {
		return tac.implicitThisVariable(accessedElement);
	}
	
	/**
	 * Helper method to access {@link IEclipseVariableQuery#superVariable(Name)}
	 * to get the <b>super</b> variable (based on its qualifier).
	 * @param qualifier Qualifier for <b>super</b> access; <code>null</code> 
	 * for unqualified <b>super</b>.
	 * @return <b>super</b> variable for the given qualifier.
	 */
	protected SuperVariable superVariable(Name qualifier) {
		return tac.superVariable(qualifier);
	}

	/**
	 * Helper method to access {@link IEclipseVariableQuery#typeVariable(ITypeBinding)}
	 * to get a type variable based on its binding.
	 * @param binding A type binding.
	 * @return Variable representing the given type.
	 */
	protected TypeVariable typeVariable(ITypeBinding binding) {
		return tac.typeVariable(binding);
	}
/*	
	protected List<Variable> initVariables(List<IInitializerNode> nodes) {
		List<Variable> result = new ArrayList<Variable>(nodes.size());
		for(IInitializerNode e : nodes) {
			result.add(variable(e));
		}
		return result;
	}
*/	
	/**
	 * Format list of variables as comma-separated string.
	 * @param args List of variables.
	 * @return Comma-separated string representation of the variable list.
	 */
	protected static String argsString(List<Variable> args) {
		StringBuffer result = new StringBuffer();
		boolean first = true;
		for(Variable x : args) {
			if(first) first = false;
			else result.append(", ");
			result.append(x.toString());
		}
		return result.toString();
	}
	
	/**
	 * Use this method to transfer over an instruction.  This method performs double-dispatch
	 * to call the appropriate <code>transfer</code> method on the transfer function being
	 * passed.
	 * @param <LE> Lattice element used in the transfer function.
	 * @param tf Transfer function.
	 * @param value Incoming lattice value.
	 * @return Outgoing lattice value after transfering over this instruction.
	 */
	public abstract <LE> LE transfer(ITACTransferFunction<LE> tf, LE value);
	
	/**
	 * Use this method to transfer over an instruction.  This method performs double-dispatch
	 * to call the appropriate <code>transfer</code> method on the transfer function being
	 * passed.
	 * @param <LE> Lattice element used in the transfer function.
	 * @param tf Transfer function.
	 * @param labels Branch labels to consider.
	 * @param value Incoming lattice value.
	 * @return Outgoing lattice values for given labels after transfering over this instruction.
	 */
	public abstract <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, 
			List<ILabel> labels, 
			LE value);
	
}
