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
 * This class is the abstract super type of all variables in the three address
 * code representation of a given Java program. Note that the only thing all
 * the different types of variables have is that they all have a type.
 * 
 * There are several different variable types that extend this type and in
 * general are much more interesting than this class. These subclasses include
 * 'temporary' variables in three address code as well as variables that
 * correspond with actual Java source code variables.
 * 
 * @author Kevin Bierhoff
 */
public abstract class Variable {
	
	/**
	 * <code>null</code> or the type binding of this variable.
	 * @see org.eclipse.jdt.core.dom.Expression#resolveTypeBinding
	 */
	public abstract ITypeBinding resolveType();

	/**
	 * Returns a string representing this variable in the source.
	 * @return String representing this variable in the source.
	 */
	public String getSourceString() {
		return toString();
	}
	
	public abstract <T> T dispatch(IVariableVisitor<T> visitor);

	public boolean isUnqualifiedSuper() {
		return false;
	}
	
	public boolean isUnqualifiedThis() {
		return false;
	}
}
