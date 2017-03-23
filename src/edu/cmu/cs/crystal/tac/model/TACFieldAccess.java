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

/**
 * x.f, i.e., an access to a field.
 * 
 * @author Kevin Bierhoff
 */
public interface TACFieldAccess extends TACInstruction {
	
	/**
	 * Returns the name of the field being accessed.
	 * @return the name of the field being accessed.
	 */
	public String getFieldName();

	/**
	 * Returns the binding of the field being accessed.
	 * @return the binding of the field being accessed.
	 */
	public IVariableBinding resolveFieldBinding();
	
	/**
	 * Indicates whether this is an access to a static field 
	 * (including enum constants, I think).
	 * @return <code>true</code> if a static field is accessed, 
	 * <code>false</code> otherwise.
	 */
	public boolean isStaticFieldAccess();
	
	/**
	 * Returns the object of which this field is a part.
	 * In the expression <code>x.f</code>, this method
	 * returns x.
	 * @return The object being accessed.
	 */
	public Variable getAccessedObjectOperand();

}