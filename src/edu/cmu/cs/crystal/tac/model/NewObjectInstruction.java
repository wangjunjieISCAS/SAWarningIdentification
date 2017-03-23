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
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * x = new C(z1, ..., zn), including for anonymous inner classes.
 * Notice that the code for anonymous inner classes does not become
 * part of the three-address code representation of the surrounding method.
 * @author Kevin Bierhoff
 * @see org.eclipse.jdt.core.dom.ClassInstanceCreation
 */
public interface NewObjectInstruction extends InvocationInstruction {
	
	/**
	 * Returns the node this instruction is for.  Should be of type
	 * {@link org.eclipse.jdt.core.dom.ClassInstanceCreation}.  Usually,
	 * one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * @return the node this instruction is for.
	 * @see TACInstruction#getNode()
	 */
	public ASTNode getNode();

	/**
	 * Indicates whether this instruction instantiates an anonymous class.
	 * @return <code>true</code> if this instruction instantiates an anonymous class,
	 * <code>false</code> otherwise.
	 */
	public boolean isAnonClassType();
	
	/**
	 * Resolves the instantiated type, which will usually be a class.
	 * This works even for anonymous inner classes: the resolved type will
	 * be the anonymous class type, not the implicitly extended base type. 
	 * @return the instantiated type.
	 * @since Crystal 3.3.8
	 */
	public ITypeBinding resolveInstantiatedType();

	/**
	 * Indicates whether there is an outer object specifier <b>that is permitted
	 * by the Java language syntax</b>.
	 * If this method returns <code>true</code> then 
	 * {@link #getOuterObjectSpecifierOperand()}
	 * will return a non-<code>null</code> variable.
	 * This method returns <code>true</code> even if the outer object is
	 * implicit in the original Java source. 
	 * It turns out that local and anonymous inner classes cannot legally
	 * have an explicit outer object specifier, even though they capture the
	 * surrounding receiver, and so this method returns <code>false</code>
	 * for those classes.
	 * @return <code>true</code> if there is an outer object specifier, 
	 * <code>false</code> otherwise.
	 */
	public boolean hasOuterObjectSpecifier();
	
	/**
	 * Returns the outer object specifier, if any.
	 * A non-<code>null</code> specifier is returned even if it is implicit
	 * in the original Java program (but see {@link #hasOuterObjectSpecifier()}
	 * for discussion of local and anonymous inner classes).
	 * @return the outer object specifier, or <code>null</code> if there is none.
	 * @see #hasOuterObjectSpecifier()
	 */
	public Variable getOuterObjectSpecifierOperand();

}
