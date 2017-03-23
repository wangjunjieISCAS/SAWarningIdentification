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
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 *
 */
class EclipseSuperCallInstruction extends AbstractMethodCallInstruction<SuperMethodInvocation> {

	/**
	 * @param node
	 * @param tac
	 */
	public EclipseSuperCallInstruction(SuperMethodInvocation node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param tac
	 */
	public EclipseSuperCallInstruction(SuperMethodInvocation node, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
	}

	@Override
	public String getMethodName() {
		return getNode().getName().getIdentifier();
	}

	@Override
	public Variable getReceiverOperand() {
		if(isStaticMethodCall())
			// not sure if this is possible, but in case super.m() actually refers to a static method
			return typeVariable(resolveBinding().getDeclaringClass());
		return superVariable(getNode().getQualifier());
	}

	@Override
	public boolean isSuperCall() {
		return true;
	}

	public List<Variable> getArgOperands() {
		return variables(getNode().arguments());
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.IEclipseTACInvocation#resolveBinding()
	 */
	public IMethodBinding resolveBinding() {
		return getNode().resolveMethodBinding();
	}

}
