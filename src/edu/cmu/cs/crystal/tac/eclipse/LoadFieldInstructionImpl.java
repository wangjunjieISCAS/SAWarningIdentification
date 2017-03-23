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

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.LoadFieldInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x = y.f, where f is a field.
 * 
 * @author Kevin Bierhoff
 *
 */
class LoadFieldInstructionImpl extends AbstractAssignmentInstruction<ASTNode> 
implements LoadFieldInstruction {
	
	private IEclipseFieldAccess access;
	
	/**
	 * @param node
	 * @param access
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public LoadFieldInstructionImpl(ASTNode node, IEclipseFieldAccess access, IEclipseVariableQuery tac) {
		super(node, tac);
		this.access = access;
	}

	public String getFieldName() {
		return access.getFieldName().getIdentifier();
	}
	
	public IVariableBinding resolveFieldBinding() {
		return access.resolveFieldBinding();
	}
	
	public Variable getSourceObject() {
		return access.getAccessedObject();
	}

	public Variable getAccessedObjectOperand() {
		return access.getAccessedObject();
	}

	public boolean isStaticFieldAccess() {
		return (resolveFieldBinding().getModifiers() & Modifier.STATIC) == Modifier.STATIC;
	}

	@Override
	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		return tf.transfer(this, value);
	}

	@Override
	public <LE> IResult<LE> transfer(ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels, LE value) {
		return tf.transfer(this, labels, value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() { 
		if(access.isImplicitThisAccess()) {
			return getTarget() + " = <implicit-this>." + getFieldName();
		}
		return getTarget() + " = " + getSourceObject() + "." + getFieldName();
	}

}
