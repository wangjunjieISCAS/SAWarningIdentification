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
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * A source variable is a variable in three address code that actually existed in
 * the original Java source code. Most of the variables that appear in three
 * address code are temporary variables that exist solely for the purpose of
 * storing intermediate sub-expression values. However, variables that originally
 * existed in source code are translated as variables of this type.
 * 
 * For example, in the following excerpt:<br>
 * <code>int a = 4+5+3+6+9+2+9;</code><br>
 * each of the additions on the right-hand side will be turned into a temporary variable
 * assignment, but the variable 'a' will exist as an instance of this class.
 * @author Kevin Bierhoff
 *
 */
public class SourceVariable extends Variable {

	private IVariableBinding binding;
	private String id;
	private boolean isLocal;

	/**
	 * Creates a new source variable object for a local with the given name and binding.
	 * @param id
	 * @param binding
	 * @param isLocallyDeclared pass <code>true</code> for locally declared variables, 
	 * including formal parameters, <code>false</code> for variables captured from an outer scope.
	 */
	public SourceVariable(String id, IVariableBinding binding, boolean isLocallyDeclared) {
		super();
		if(id == null || binding == null || binding.isEnumConstant() || binding.isField())
			throw new IllegalArgumentException("Illegal source variable initialization args.");
		if(isLocallyDeclared == false && Modifier.isFinal(binding.getModifiers()) == false)
			throw new IllegalArgumentException("Non-final variables must be declared locally: " + binding);
		this.id = id;
		this.binding = binding;
		this.isLocal = isLocallyDeclared;
	}

	/**
	 * We can get the original java source binding. Note that this method returns an
	 * type from the Java AST, not from 3 address code.
	 * @return the binding
	 */
	public IVariableBinding getBinding() {
		return binding;
	}
	
	/**
	 * Indicates whether this source variable is captured from an outer scope.
	 * @return <code>true</code> if this source variable is captured from an outer scope,
	 * <code>false</code> otherwise.
	 */
	public boolean isCapturedFromOuterScope() {
		return isLocal == false;
	}

	@Override
	public <T> T dispatch(IVariableVisitor<T> visitor) {
		return visitor.sourceVar(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((binding == null) ? 0 : binding.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SourceVariable other = (SourceVariable) obj;
		if (binding == null) {
			if (other.binding != null)
				return false;
		} else if (!binding.equals(other.binding))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.Variable#resolveType()
	 */
	@Override
	public ITypeBinding resolveType() {
		return binding.getType();
	}

}
