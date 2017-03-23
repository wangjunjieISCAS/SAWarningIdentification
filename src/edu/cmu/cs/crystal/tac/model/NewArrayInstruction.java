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

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;

/**
 * x = new T[y1]...[yn] or x = new T[]...[] = z.
 * @author Kevin Bierhoff
 * @see org.eclipse.jdt.core.dom.ArrayCreation
 */
public interface NewArrayInstruction extends AssignmentInstruction {
	
	/**
	 * Returns the node this instruction is for.  Should be of type
	 * {@link org.eclipse.jdt.core.dom.ArrayCreation}.  Usually,
	 * one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * @return the node this instruction is for.
	 * @see TACInstruction#getNode()
	 */
	public ASTNode getNode();
	
	/**
	 * Returns the type of the array being created.
	 * @return the type of the array being created.
	 */
	public ArrayType getArrayType();
	
	/**
	 * Returns the list of operands specifying dimensions 
	 * of the allocated array.  In <code>new T[y1]...[yn]</code>
	 * this would be variables y1, ..., yn.  This is only interesting
	 * if {@link #isInitialized()} returns <code>false</code>.
	 * @return the list of operands specifying dimensions 
	 * of the allocated array.
	 */
	public List<Variable> getDimensionOperands();
	
	/**
	 * Returns the number of dimensions in the new array.
	 * @return the number of dimensions in the new array.
	 */
	public int getDimensions();
	
	/**
	 * Returns the number of dimensions that will be initialized
	 * with <code>null</code> cells.
	 * @return the number of dimensions that will be initialized
	 * with <code>null</code> cells.
	 */
	public int getUnallocated();
	
	/**
	 * Indicates whether this array has an initializer
	 * associated with it.  If this method returns <code>true</code>,
	 * the initializer can be retrieved with {@link #getInitOperand()}.
	 * @return <code>true</code> if this array has an initializer,
	 * <code>false</code> otherwise.
	 */
	public boolean isInitialized();
	
	/**
	 * Returns the array initializer, if any.  
	 * @return the array initializer or <code>null</code>
	 * if there is none.
	 * @see #isInitialized() to test whether this array
	 * has an initializer
	 */
	public Variable getInitOperand();

}
