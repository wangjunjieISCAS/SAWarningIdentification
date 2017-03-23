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

/**
 * x = { y1, ..., yn }.
 * 
 * @author Kevin Bierhoff
 * @see org.eclipse.jdt.core.dom.ArrayInitializer
 */
public interface ArrayInitInstruction extends AssignmentInstruction {
	
	/**
	 * Returns the node this instruction is for.  Should be of type
	 * {@link org.eclipse.jdt.core.dom.ArrayInitializer}. Usually,
	 * one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * @return the node this instruction is for.
	 * @see TACInstruction#getNode()
	 */
	public ASTNode getNode();

	/**
	 * Returns the operands initializing the array.  In 
	 * <code>x = { y1, ..., yn }</code>, this would be the
	 * list y1, ..., yn.
	 * @return the operands initializing the array.
	 */
	public List<Variable> getInitOperands();

}
