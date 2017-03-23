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

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.ConstructorCallInstruction;
import edu.cmu.cs.crystal.tac.model.KeywordVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x(y1, ..., yn), where x is "this" or "super".  This instruction
 * can by definition only occur in a constructor.  It does <b>not</b>
 * have to be the first instruction in the constructor if arguments
 * to the constructor call are computed with preceding instructions.
 * @author Kevin Bierhoff
 * @see org.eclipse.jdt.core.dom.ConstructorInvocation
 * @see org.eclipse.jdt.core.dom.SuperConstructorInvocation
 */
abstract class AbstractConstructorCallInstruction<E extends ASTNode> extends AbstractTACInstruction<E> implements ConstructorCallInstruction {
	
	/**
	 * Creates a new constructor call instruction for the given node and query callback.
	 * @param node
	 * @param tac
	 * @see AbstractTACInstruction#AbstractTACInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public AbstractConstructorCallInstruction(
			E node,
			IEclipseVariableQuery tac) {
		super(node, tac);
	}

	public abstract KeywordVariable getConstructionObject();
	
	public abstract boolean isSuperCall();
	
	public abstract List<Variable> getArgOperands();

	public abstract boolean hasEnclosingInstanceSpecifier();
	
	public abstract Variable getEnclosingInstanceSpecifier();
	
	public abstract IMethodBinding resolveBinding();

	@Override
	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		return tf.transfer(this, value);
	}
	
	@Override
	public <LE> IResult<LE> transfer(ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels, LE value) {
		return tf.transfer(this, labels, value);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.InvocationInstruction#toString()
	 */
	@Override
	public String toString() {
		return getConstructionObject() + "(" + argsString(getArgOperands()) + ")";
	}

}
