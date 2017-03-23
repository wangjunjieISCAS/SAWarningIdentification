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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;

import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.TempVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Abstract x = ?.
 * 
 * @author Kevin Bierhoff
 *
 */
abstract class AbstractAssignmentInstruction<E extends ASTNode> extends ResultfulInstruction<E> 
		implements AssignmentInstruction {
	
	/** 
	 * Variable for the assignment's target; may be set in the constructor or 
	 * discovered lazily when {@link #getTarget()} is called. 
	 */
	private Variable target;
	
	/**
	 * Use this constructor to get default target variable resolution.
	 * @param node
	 * @param tac
	 * @see AbstractTACInstruction#AbstractTACInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public AbstractAssignmentInstruction(E node, IEclipseVariableQuery tac) {
		super(node, tac);
	}
	
	/**
	 * Use this constructor to set target variable explicitly.
	 * @param node
	 * @param target <code>null</code> will result in default variable resolution.
	 * @param tac
	 * @see AbstractTACInstruction#AbstractTACInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public AbstractAssignmentInstruction(E node, Variable target, IEclipseVariableQuery tac) {
		super(node, tac);
		this.target = target;
	}
	
	/**
	 * Use this constructor to get a fresh variable.
	 * @param node
	 * @param fresh <code>true</code> forces fresh variable; 
	 * <code>false</code> will result in default variable resolution.
	 * @param tac
	 * @see AbstractTACInstruction#AbstractTACInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public AbstractAssignmentInstruction(E node, boolean fresh, IEclipseVariableQuery tac) {
		super(node, tac);
		if(fresh)
			this.target = createTemp(node);
	}
	
	public Variable getTarget() {
		if(target == null) 
			// lazy instantiation: this instruction will have a unique target
			// unless one was set explicitly
			target = defaultVariable();
		return target;
	}
	
	protected void setTarget(Variable newTarget) {
		if(target != null)
			throw new IllegalStateException("Target variable already set to " + target);
		target = newTarget;
	}
	
	/**
	 * Determines the result variable for the node represented
	 * by this instruction using <i>default variable resolution</i>.
	 * This means usually that the node is represented with a unique
	 * {@link TempVariable temp}, unless {@link #checkIfCopyNeeded(ASTNode)
	 * a copy is needed}.
	 * <b>Call this method only once per node<b> since it creates fresh
	 * {@link TempVariable temps}.
	 * @return the result variable for the node represented
	 * by this instruction using <i>default variable resolution</i>.
	 */
	private Variable defaultVariable() {
		ASTNode copyFor = checkIfCopyNeeded(getNode());
		if(copyFor != null)
			return targetVariable(copyFor);

		// unique target variable for this instruction
		return createTemp(getNode());
	}
	
	/**
	 * Tests if the result of the given node needs to be copied
	 * into the result of a surrounding node; this is to handle
	 * control flow branches in expressions (? :, &&, ||) properly.
	 * Returns the AST node for which a copy is needed, if any.
	 * @param n
	 * @return The AST node for which a copy is needed; <code>null</code> if no copy is needed.
	 */
	protected static ASTNode checkIfCopyNeeded(ASTNode n) {
		ASTNode p = n.getParent();
		boolean branches = false;
		while(p != null) {
			// skip over parentheses
			if((p instanceof ParenthesizedExpression) 
					// skip over assignments, unless previously hit a branch
					|| (branches == false && assigns(p, n))) {
				n = p;
				p = n.getParent();
				continue;
			}
			
			// find common variable "above" branches
			if(branch(p, n)) {
				branches = true;
				n = p;
				p = n.getParent();
				continue;
			}
			
			// found straight-line code without assignment
			if(branches) return n;
//			if(n instanceof VariableDeclaration) {
//				VariableDeclaration decl = (VariableDeclaration) n;
//				if(n.getParent() instanceof FieldDeclaration)
//					// do *not* copy directly into a field--need a store in between
//					return null;
//				return n;
//			}
			break;
		}
		return null;
	}
	
	/**
	 * Tests if expression n is a branch in the control flow; for convenience we pass n's parent in as well.
	 * @param p n's parent
	 * @param n should be an {@link org.eclipse.jdt.core.dom.Expression <i>expression</i>}.
	 * @return <code>true</code> if n is an expression that immediately precedes a branch in the control flow, 
	 * <code>false</code> otherwise.
	 */
	private static boolean branch(ASTNode p, ASTNode n) {
		if(p instanceof ConditionalExpression)
			return ((ConditionalExpression) p).getExpression() != n;
		if(p instanceof InfixExpression) {
			InfixExpression.Operator op = ((InfixExpression) p).getOperator();
			return InfixExpression.Operator.CONDITIONAL_AND.equals(op)
				|| InfixExpression.Operator.CONDITIONAL_OR.equals(op);
		}
		return false;
	}

	/**
	 * Tests if an n is assigned a value; for convenience we pass n's parent
	 * (which would perform the assignment) in as well.
	 * @param p n's parent
	 * @param n
	 * @return <code>true</code> if p is an assignment to (or initialization of) n, 
	 * <code>false</code> otherwise.
	 */
	private static boolean assigns(ASTNode p, ASTNode n) {
		if(p instanceof Assignment) {
			return Assignment.Operator.ASSIGN.equals(((Assignment) p).getOperator())
				&& ((Assignment) p).getRightHandSide() == n;
		}
		return false;
//		return (p instanceof VariableDeclaration)
//			&& ((VariableDeclaration) p).getInitializer() == n;
	}

	protected Variable getResultVariable() {
		return getTarget();
	}

	/**
	 * Creates a fresh temp variable representing the result of the given node.
	 * @param node
	 * @return a fresh temp variable representing the result of the given node.
	 */
	protected TempVariable createTemp(ASTNode node) {
		// allocate variable object
		// possible memory optimization: use instruction itself to represent temp
		return new TempVariable(node);
	}
	
}
