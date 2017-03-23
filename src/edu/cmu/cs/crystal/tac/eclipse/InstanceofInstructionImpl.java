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

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Type;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.InstanceofInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x = y instanceof T.
 * 
 * @author Kevin Bierhoff
 *
 */
class InstanceofInstructionImpl extends AbstractAssignmentInstruction<InstanceofExpression> 
implements InstanceofInstruction {

	/**
	 * @param node
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, IEclipseVariableQuery)
	 */
	public InstanceofInstructionImpl(InstanceofExpression node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, Variable, IEclipseVariableQuery)
	 */
	public InstanceofInstructionImpl(InstanceofExpression node, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
	}

	public Type getTestedTypeNode() {
		return getNode().getRightOperand();
	}

	public Variable getOperand() {
		return variable(getNode().getLeftOperand());
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
		ITypeBinding t = getTestedTypeNode().resolveBinding();
		if(t == null)
			return getTarget() + " = " + getOperand() + "instanceof <Type>";
		return getTarget() + " = " + getOperand() + " instanceof " + t.getName();
	}

}
