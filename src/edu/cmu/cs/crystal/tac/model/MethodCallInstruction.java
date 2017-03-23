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


/**
 * x = y.m(z1, ..., zn), where m is a method and y is possibly a type variable,
 * in the case of a static method call.
 * 
 * @author Kevin Bierhoff
 * @see #isStaticMethodCall() determine whether this is a static method call
 * @see #isSuperCall() determine whether this is a <b>super</b> call.
 * @see ConstructorCallInstruction calls between constructors
 */
public interface MethodCallInstruction extends InvocationInstruction {
	
	/**
	 * Returns the receiver of this call, if any.
	 * @return the receiver of this call, or <code>null</code> if this is a static method call.
	 * @see #isStaticMethodCall()
	 */
	public Variable getReceiverOperand();
	
	/**
	 * Indicates whether this is a super-call
	 * @return <code>true</code> if this is a super-call, <code>false</code> otherwise.
	 * @see org.eclipse.jdt.core.dom.SuperMethodInvocation
	 */
	public boolean isSuperCall();
	
	/**
	 * Indicates whether this is a call to a static method.
	 * @return <code>true</code> if this is a call to a static method, <code>false</code> otherwise.
	 */
	public boolean isStaticMethodCall();
	
	/**
	 * Returns the name of the called method.
	 * @return the name of the called method.
	 */
	public String getMethodName();

}
