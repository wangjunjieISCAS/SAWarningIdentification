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

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 *
 */
class EclipseNormalCallInstruction extends AbstractMethodCallInstruction<MethodInvocation> {

	/**
	 * @param node
	 * @param tac
	 */
	public EclipseNormalCallInstruction(MethodInvocation node,
			IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param tac
	 */
	public EclipseNormalCallInstruction(MethodInvocation node,
			Variable target, IEclipseVariableQuery tac) {
		super(node, target, tac);
	}

	@Override
	public boolean isSuperCall() {
		return false;
	}

	@Override
	public Variable getReceiverOperand() {
		if(getNode().getExpression() == null) {
			IMethodBinding method = resolveBinding();
			if(isStaticMethodCall()) {
				// static method--return type variable for surrounding class
				return typeVariable(method.getDeclaringClass());
			}
			else {
				// implicit this
				// the following should return correct this variable
				// use the type of the method being called to disambiguate inner from outer classes
				return implicitThisVariable(method);
			}
		}
		return variable(getNode().getExpression());
	}
	
	public List<Variable> getArgOperands() {
		return variables(getNode().arguments());
	}

	@Override
	public String getMethodName() {
		return getNode().getName().getIdentifier();
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.IEclipseTACInvocation#resolveBinding()
	 */
	public IMethodBinding resolveBinding() {
		return getNode().resolveMethodBinding();
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.InvocationInstruction#toString()
	 */
	@Override
	public String toString() {
		String receiver;
		if(getNode().getExpression() == null) {
			if(isStaticMethodCall())
				receiver = "<static>";
			else
				receiver = "<implicit-this>.";
		}
		else
			receiver = getReceiverOperand() + ".";
		return getTarget() + " = " + receiver + getMethodName() + "(" + argsString(getArgOperands()) + ")";
	}

}
