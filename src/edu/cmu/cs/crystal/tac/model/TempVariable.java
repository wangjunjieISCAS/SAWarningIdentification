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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * Temporary variables created during the course of translating to three address
 * code are represented by instances of this class.
 * 
 * All sub-expressions in a normal Java expression have their value assigned to
 * a temporary variable and those variables are represented by instance of this class.
 * 
 * For instance, the following Java expression:<br>
 * <code>a = 3 + 4</code><br>
 * would actually be represented by three address code that looks something like the
 * following:<br>
 * <code>x = 3;</code><br>
 * <code>y = 4;</code><br>
 * <code>a = x + y;</code><br>
 * Those variables that are created for the purpose of evaluating sub-expressions (here
 * x and y) are temporary variables, and represented by instance of this class.
 * 
 * @author Kevin Bierhoff
 *
 */
public class TempVariable extends Variable {
	
	private static int temp = 0;
	
	private ASTNode node;
	private String name;

	public TempVariable(ASTNode node) {
		this.node = node;
		this.name = "temp" + (temp++);
	}

	public ASTNode getNode() {
		return node;
	}
	
	@Override
	public <T> T dispatch(IVariableVisitor<T> visitor) {
		return visitor.tempVar(this);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TempVariable other = (TempVariable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String getSourceString() {
		return node.toString();
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.Variable#resolveType()
	 */
	@Override
	public ITypeBinding resolveType() {
		if(node instanceof Expression)
			return ((Expression) node).resolveTypeBinding();
		return null;
	}

}
