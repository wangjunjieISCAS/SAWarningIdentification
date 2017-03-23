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

/**
 * x = l, an assignment of a literal value to a variable.
 * 
 * Example:<br>
 * <code>a = 4;</code>
 * 
 * @author Kevin Bierhoff
 *
 */
public interface LoadLiteralInstruction extends LoadInstruction {
	
	/**
	 * Returns the node this instruction is for.  Should be of type
	 * {@link org.eclipse.jdt.core.dom.Expression}.  Usually,
	 * one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * @return the node this instruction is for.
	 * @see TACInstruction#getNode()
	 */
	public ASTNode getNode();
	
	/**
	 * This method returns the literal that is being assigned in this 3 address
	 * code statement. The <code>isPrimitive()</code> and <code>isNull()</code>
	 * methods may be helpful here if you are trying to do something clever
	 * based on which literal is actually being assigned.<br>
	 * 
	 * Note also that if you want to otherwise find out what the literal value
	 * is, you are probably going to have to use <code>instanceof</code> tests
	 * on the return value and cast it into something that is actually helpful.
	 * Right now numeric literals are returned as Strings; this may be cleaned
	 * up in the future.  Boolean literals are (as you would expect)
	 * represented as Booleans, null literals as null, and char literals as
	 * Characters.
	 * 
	 * @return the literal node that is being assigned in this 3 address code
	 *         statement.
	 */
	public Object getLiteral();
	
	/**
	 * Indicates the load of a primitive value.
	 * @return <code>true</code> if this is a load of a primitive value, 
	 * <code>false</code> otherwise.
	 */
	public boolean isPrimitive();
	
	/**
	 * Indicates the load of a number such as an <code>int</code>.
	 * Implies {@link #isPrimitive()}
	 * @return <code>true</code> if this is a load of a number,
	 * <code>false</code> otherwise.
	 */
	public boolean isNumber();
	
	/**
	 * Indicates whether this loads the <code>null</code> literal.
	 * @return <code>true</code> if this loads the <code>null</code> 
	 * literal, <code>false</code> otherwise.
	 */
	public boolean isNull();
	
	/**
	 * Indicates whether this loads a {@link java.lang.String string}
	 * literal. 
	 * @return <code>true</code> if this loads a {@link java.lang.String string},
	 * <code>false</code> otherwise.
	 */
	public boolean isNonNullString();

}
