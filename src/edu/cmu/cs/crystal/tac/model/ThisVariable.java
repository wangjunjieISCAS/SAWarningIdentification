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
 * This class represents the <code>this</code> keyword, which is treated much like
 * any other variable.
 * 
 * For example, if the original Java source had code that was similar to the following:<br>
 * <code>a = this;</code><br>
 * The right-hand side would be translated as a variable of this type.
 * 
 * @author Kevin Bierhoff
 *
 */
public class ThisVariable extends KeywordVariable {

	private ITypeBinding typeBinding;

	/**
	 * Creates an implicitly qualified <b>this</b> variable.
	 * @param tac
	 * @param typeBinding 
	 */
	public ThisVariable(EclipseTAC tac, ITypeBinding typeBinding) {
		super(tac);
		this.typeBinding = typeBinding;
	}

	/**
	 * Creates an unqualified <b>this</b> variable.
	 * @param tac
	 */
	public ThisVariable(EclipseTAC tac) {
		super(tac);
	}

	/**
	 * Creates a qualified <b>this</b> variable.
	 * @param tac
	 * @param qualifier 
	 */
	public ThisVariable(EclipseTAC tac, Name qualifier) {
		super(tac, qualifier);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.KeywordVariable#getKeyword()
	 */
	@Override
	public String getKeyword() {
		return "this";
	}

	@Override
	public boolean isQualified() {
		return super.isQualified() || isImplicit();
	}

	@Override
	public ITypeBinding resolveType() {
		if(typeBinding != null)
			return typeBinding;
		if(getQualifier() != null)
			return getQualifier().resolveTypeBinding();
		return tac.resolveThisType();
	}

	public boolean isImplicit() {
		// TODO should be package-private with EclipseTAC
		return typeBinding != null;
	}

	public void explicitQualifier(Name qualifier) {
		// TODO should be package-private with EclipseTAC
		if(typeBinding == null) 
			throw new IllegalStateException("Not an implicitly qualified this variable");
		typeBinding = null;
		setQualifier(qualifier);
	}

	@Override
	public <T> T dispatch(IVariableVisitor<T> visitor) {
		return visitor.thisVar(this);
	}

	@Override
	public boolean isUnqualifiedThis() {
		return ! isQualified();
	}

}
