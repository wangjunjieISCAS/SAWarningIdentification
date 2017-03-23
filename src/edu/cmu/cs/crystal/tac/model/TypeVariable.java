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

/**
 * In Java you can make calls that look like the following:<br>
 * <code>System.exit(0);</code><br>
 * Here System is a class, but because out is a static field we access it
 * directly from the class name. In this example, System is a type variable
 * and in three address code System would be represented by an instance of this
 * class.
 * 
 * <i>For some reason static fields are represented as {@link SourceVariable}
 * instances</i> and not as a field access to a type variable.
 * 
 * @author Kevin Bierhoff
 *
 */
public class TypeVariable extends Variable {
	
	private ITypeBinding binding;

	public TypeVariable(ITypeBinding binding) {
		super();
		this.binding = binding;
	}

	/**
	 * Because this class represents a type variable, getType and resolveType both
	 * return the same value; the type represented by this variable.
	 * @return the type
	 */
	public ITypeBinding getType() {
		return binding;
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
		final TypeVariable other = (TypeVariable) obj;
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
		return binding.getQualifiedName();
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.Variable#resolveType()
	 */
	/**
	 * Because this class represents a type variable, getType and resolveType both
	 * return the same value; the type represented by this variable.
	 */
	@Override
	public ITypeBinding resolveType() {
		return binding;
	}

	@Override
	public <T> T dispatch(IVariableVisitor<T> visitor) {
		return visitor.typeVar(this);
	}

}
