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

import org.eclipse.jdt.core.dom.Expression;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.MethodCallInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x = y.m(z1, ..., zn), where m is a method and y is possibly a type variable,
 * in the case of a static method call, or <code>super</code>, in the case
 * of a <b>super</b> call.
 * 
 * @author Kevin Bierhoff
 * @see #isStaticMethodCall() determine whether this is a static method call
 * @see #isSuperCall() determine whether this is a <b>super</b> call.
 */
abstract class AbstractMethodCallInstruction<E extends Expression> 
extends AbstractAssignmentInstruction<E> implements MethodCallInstruction {
	
	/**
	 * @param node
	 * @param args
	 */
	public AbstractMethodCallInstruction(E node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param args
	 */
	public AbstractMethodCallInstruction(E node, Variable target, IEclipseVariableQuery tac) {
		super(node, tac);
	}
	
	public abstract Variable getReceiverOperand();
	
	public abstract boolean isSuperCall();
	
	public boolean isStaticMethodCall() {
		return EclipseTAC.isStaticBinding(resolveBinding());
	}
	
	public abstract String getMethodName();

	@Override
	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		return tf.transfer(this, value);
	}
	
	@Override
	public <LE> IResult<LE> transfer(ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels, LE value) {
		return tf.transfer(this, labels, value);
	}

	@Override
	public String toString() {
		return getTarget() + " = "+ getReceiverOperand() + "." + getMethodName() + "(" + argsString(getArgOperands()) + ")";
	}

}
