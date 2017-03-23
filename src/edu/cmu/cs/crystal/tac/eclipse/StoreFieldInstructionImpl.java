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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Modifier;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.StoreFieldInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;


/**
 * x.f = y, where f is a field.
 * 
 * @author Kevin Bierhoff
 *
 */
class StoreFieldInstructionImpl extends AbstractStoreInstruction 
implements StoreFieldInstruction {
	
	private IEclipseFieldAccess target;

	/**
	 * @param node
	 * @param source The operand being stored.
	 * @param target The field being written to.
	 * @param tac
	 * @see AbstractStoreInstruction#AbstractStoreInstruction(Expression, Variable, IEclipseVariableQuery)
	 */
	public StoreFieldInstructionImpl(
			ASTNode node, 
			Variable source,
			IEclipseFieldAccess target,
			IEclipseVariableQuery tac) {
		super(node, source, tac);
		this.target = target;
	}
	
	public Variable getDestinationObject() {
		return target.getAccessedObject();
	}
	
	public Variable getAccessedObjectOperand() {
		return target.getAccessedObject();
	}

	public String getFieldName() {
		return target.getFieldName().getIdentifier();
	}
	
	public IVariableBinding resolveFieldBinding() {
		return target.resolveFieldBinding();
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

	@Override
	public String toString() {
		if(target.isImplicitThisAccess())
			return "<implicit-this>." + getFieldName() + " = " + getSourceOperand(); 
		return getDestinationObject() + "." + getFieldName() + " = " + getSourceOperand();
	}

}
