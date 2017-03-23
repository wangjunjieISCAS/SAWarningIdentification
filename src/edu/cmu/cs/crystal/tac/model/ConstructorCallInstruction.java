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
import org.eclipse.jdt.core.dom.IMethodBinding;

/**
 * x(y1, ..., yn), where x is "this" or "super".  This instruction
 * can by definition only occur in a constructor.  It does <b>not</b>
 * have to be the first instruction in the constructor if arguments
 * to the constructor call are computed with preceding instructions.
 * @author Kevin Bierhoff
 * @see org.eclipse.jdt.core.dom.ConstructorInvocation
 * @see org.eclipse.jdt.core.dom.SuperConstructorInvocation
 */
public interface ConstructorCallInstruction extends TACInvocation {
	
	/**
	 * Returns the node this instruction is for.  This should be one of the
	 * following types:
	 * <ul>
	 *   <li>{@link org.eclipse.jdt.core.dom.ConstructorInvocation}
	 *   <li>{@link org.eclipse.jdt.core.dom.SuperConstructorInvocation}
	 * </ul> 
	 * Usually, one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * @return the node this instruction is for.
	 * @see TACInstruction#getNode()
	 */
	public ASTNode getNode();
	
	/**
	 * Returns variable for the object being constructed, i.e.,
	 * {@link ThisVariable <code>this</code>} or 
	 * {@link SuperVariable <code>super</code>}.
	 * @return {@link ThisVariable <code>this</code>} or 
	 * {@link SuperVariable <code>super</code>}.
	 */
	public abstract KeywordVariable getConstructionObject();
	
	/**
	 * Indicates whether this is a super-constructor call
	 * or a call to a constructor in the same class as the
	 * surrounding constructor.  Thus, if this method returns
	 * <code>true</code> then {@link #getConstructionObject()}
	 * will return {@link SuperVariable <code>super</code>} and
	 * otherwise {@link ThisVariable <code>this</code>}.
	 * @return <code>true</code> if this is a super-constructor
	 * call, <code>false</code> if this is a call to a constructor
	 * in the same class as the surrounding constructor.
	 */
	public abstract boolean isSuperCall();
	
	public abstract List<Variable> getArgOperands();

	/**
	 * Indicates whether there is an {@link #getEnclosingInstanceSpecifier()
	 * enclosing instance specifier} passed into the constructor call.
	 * {@link #getEnclosingInstanceSpecifier()} will only return
	 * a non-<code>null</code> value if this method returns <code>true</code>. 
	 * @return <code>true</code> if there is an enclosing instance
	 * specifier, <code>false</code> otherwise.
	 */
	public abstract boolean hasEnclosingInstanceSpecifier();
	
	/**
	 * Specifier of an enclosing instance passed into the
	 * constructor, if any.  Please see the Java language
	 * specification for the semantics of providing an
	 * enclosing instance specifier with a constructor
	 * call.
	 * @return an enclosing instance passed into the
	 * constructor or <code>null</code> if there is none.
	 */
	public abstract Variable getEnclosingInstanceSpecifier();
	
	public abstract IMethodBinding resolveBinding();

}
