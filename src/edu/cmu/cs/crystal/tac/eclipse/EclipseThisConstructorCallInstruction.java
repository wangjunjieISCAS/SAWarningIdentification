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

import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;

import edu.cmu.cs.crystal.tac.model.KeywordVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 *
 */
class EclipseThisConstructorCallInstruction extends
		AbstractConstructorCallInstruction<ConstructorInvocation> {

	public EclipseThisConstructorCallInstruction(
			ConstructorInvocation node,
			IEclipseVariableQuery tac) {
		super(node, tac);
	}

	@Override
	public KeywordVariable getConstructionObject() {
		// this should resolve to the current class
		return implicitThisVariable(resolveBinding());
	}

	@Override
	public boolean isSuperCall() {
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.ConstructorCallInstruction#getArgOperands()
	 */
	@Override
	public List<Variable> getArgOperands() {
		return variables(getNode().arguments());
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.ConstructorCallInstruction#hasEnclosingInstanceSpecifier()
	 */
	@Override
	public boolean hasEnclosingInstanceSpecifier() {
		// this is specific to super-constructor calls
		return false;
	}

	@Override
	public Variable getEnclosingInstanceSpecifier() {
		// this is specific to super-constructor calls
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.ConstructorCallInstruction#resolveBinding()
	 */
	@Override
	public IMethodBinding resolveBinding() {
		return getNode().resolveConstructorBinding();
	}

}
