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

import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * T x.  This node represents the declaration of a variable in the source, i.e. a method
 * parameter or local variable.
 * Notice that temporary and keyword variables do <i>not</i> have an explicit declaration.
 * 
 * @author Kevin Bierhoff
 * @see org.eclipse.jdt.core.dom.VariableDeclaration
 */
public interface SourceVariableDeclaration extends TACInstruction {
	
	/**
	 * Returns the node this instruction is for.  Should be of type
	 * {@link org.eclipse.jdt.core.dom.VariableDeclaration}.  Usually,
	 * one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * @return the node this instruction is for.
	 * @see TACInstruction#getNode()
	 */
	public VariableDeclaration getNode();
	
	/**
	 * Resolves the declared variable's binding.
	 * @return the declared variable's binding.
	 */
	public IVariableBinding resolveBinding();

	/**
	 * Returns the variable being declared.
	 * @return The variable being declared.
	 */
	public SourceVariable getDeclaredVariable();

	/**
	 * Is this variable being declared as the parameter to a catch block?
	 * 
	 * @return <code>true</code> if this variable is the parameter of a catch block.
	 */
	public boolean isCaughtVariable();
	
	/**
	 * Is this variable being declared as part of an enhanced for loop?
	 * 
	 * @return <code>true</code> if this variable is the parameter of an enhanced for loop
	 */
	public boolean isEnhancedForLoopVariable();
	
    /**
	 * Is this variable being declared as a formal parameter to a method?
	 * @return <code>true</code> if this is a formal parameter declaration,
	 * <code>false</code> otherwise.
	 */
	public boolean isFormalParameter();
}
