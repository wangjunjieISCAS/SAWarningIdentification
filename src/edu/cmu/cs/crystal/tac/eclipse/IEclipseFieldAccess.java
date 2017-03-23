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
package edu.cmu.cs.crystal.tac.eclipse;

import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Interface used internally to represent field accesses, x.f.
 * This helps dealing with the different possible representations
 * of field accesses in the Eclipse AST (see {@link org.eclipse.jdt.core.dom.FieldAccess}).
 * 
 * @author Kevin Bierhoff
 *
 */
public interface IEclipseFieldAccess {

	/**
	 * Returns the name of the accessed field.
	 * @return Name of the accessed field.
	 */
	public abstract SimpleName getFieldName();
	
	/**
	 * Resolves the binding for the accessed field.
	 * Bindings can usually be resolved, but the underlying Eclipse
	 * AST admits the possiblity that <code>null</code> is returned.  
	 * @return The binding for the accessed field or <code>null</code> if
	 * the binding could not be resolved.
	 */
	public abstract IVariableBinding resolveFieldBinding();
	
	/**
	 * Returns the variable representing the target of the field access.
	 * The accessed object can be a type or instance variable.
	 * @return the variable representing the target of the field access.
	 */
	public abstract Variable getAccessedObject();
	
	/**
	 * Indicates whether this is an implicit access to a receiver field
	 * (which could actually be a field of an outer class).  Accessing
	 * a static field does <i>not</i> constitute an implicit access to the
	 * receiver in the sense of this method.
	 * @return <code>true</code> if this is an implicit access to a receiver field
	 * and <code>false</code> otherwise.
	 */
	public abstract boolean isImplicitThisAccess();
	
	/**
	 * Indicates whether this is an explicit super-field access, <code>super.f</code>.
	 * The field being accessed may be a static or instance field.
	 * @return <code>true</code> if this is an explicit super-field access,
	 * <code>false</code> otherwise.
	 */
	public abstract boolean isExplicitSuperAccess();

}
