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
package edu.cmu.cs.crystal.tac.model;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Name;

import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;



/**
 * The Super class represents the <code>super</code> keyword. Note that
 * in this sense, <code>super</code> is actually much like a variable.
 * 
 * @author Kevin Bierhoff
 *
 */
public class SuperVariable extends KeywordVariable {

	/**
	 * Creates an unqualified <b>super</b> variable.
	 * @param tac
	 */
	public SuperVariable(EclipseTAC tac) {
		// TODO should be package-private with EclipseTAC
		super(tac);
	}

	/**
	 * Creates an qualified <b>super</b> variable.
	 * @param tac
	 * @param qualifier
	 */
	public SuperVariable(EclipseTAC tac, Name qualifier) {
		// TODO should be package-private with EclipseTAC
		super(tac, qualifier);
	}

	@Override
	public String getKeyword() {
		return "super";
	}

	@Override
	public ITypeBinding resolveType() {
		if(getQualifier() == null)
			return tac.resolveThisType().getSuperclass();
		return getQualifier().resolveTypeBinding().getSuperclass();
	}

	@Override
	public <T> T dispatch(IVariableVisitor<T> visitor) {
		return visitor.superVar(this);
	}

	@Override
	public boolean isUnqualifiedSuper() {
		return ! isQualified();
	}

}
