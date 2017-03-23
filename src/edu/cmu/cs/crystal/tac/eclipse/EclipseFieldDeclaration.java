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

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Field access constituted by a field declaration
 * @author Kevin Bierhoff
 * @since 3.3.0
 */
public class EclipseFieldDeclaration extends EclipseAbstractFieldAccess<VariableDeclaration>
		implements IEclipseFieldAccess {

	/**
	 * @param node
	 * @param query
	 */
	public EclipseFieldDeclaration(VariableDeclaration node, IEclipseVariableQuery query) {
		super(node, query);
		if(! (node.getParent() instanceof FieldDeclaration))
			throw new IllegalArgumentException("Not a field declaration: " + node);
	}

	public SimpleName getFieldName() {
		return node.getName();
	}

	public boolean isExplicitSuperAccess() {
		return false;
	}

	public boolean isImplicitThisAccess() {
		return !isStaticFieldAccess();
	}

	public IVariableBinding resolveFieldBinding() {
		return node.resolveBinding();
	}

	@Override
	protected Variable getAccessedInstanceInternal(IVariableBinding field) {
		return query.implicitThisVariable(field);
	}

}
