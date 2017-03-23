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

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.CopyInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x = y.
 * @author Kevin Bierhoff
 *
 */
class CopyInstructionImpl extends AbstractAssignmentInstruction<ASTNode> 
implements CopyInstruction {

	private Variable operand;
	private boolean operandIsResult;

	/**
	 * Makes the {@link #getTarget() target} be the result.
	 * @param node
	 * @param operand the operand.
	 * @param tac
	 * @see #CopyInstructionImpl(ASTNode, Variable, boolean, IEclipseVariableQuery)
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public CopyInstructionImpl(ASTNode node, Variable operand, IEclipseVariableQuery tac) {
		super(node, tac);
		this.operand = operand;
	}

	/**
	 * @param node
	 * @param operand the operand.
	 * @param operandIsResult <code>true</code> makes the operand be the 
	 * {@link #getResultVariable() result} of this instruction, <code>false</code>
	 * makes the {@link #getTarget() target} be the result (default). 
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public CopyInstructionImpl(ASTNode node, Variable operand, boolean operandIsResult, IEclipseVariableQuery tac) {
		super(node, tac);
		this.operand = operand;
		this.operandIsResult = operandIsResult;
	}

	/**
	 * Makes the {@link #getTarget() target} be the result.
	 * @param node
	 * @param operand the operand.
	 * @param target
	 * @param tac
	 * @see #CopyInstructionImpl(ASTNode, Variable, boolean, Variable, IEclipseVariableQuery)
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(ASTNode, Variable, IEclipseVariableQuery)
	 */
	public CopyInstructionImpl(ASTNode node, Variable operand, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
		this.operand = operand;
	}

	/**
	 * @param node
	 * @param operand the operand.
	 * @param operandIsResult <code>true</code> makes the operand be the 
	 * {@link #getResultVariable() result} of this instruction, <code>false</code>
	 * makes the {@link #getTarget() target} be the result (default).
	 * @param target 
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public CopyInstructionImpl(ASTNode node, Variable operand, boolean operandIsResult, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
		this.operand = operand;
		this.operandIsResult = operandIsResult;
	}

	public Variable getOperand() {
		return operand;
	}

	@Override
	public Variable getResultVariable() {
		if(operandIsResult)
			return operand;
		return super.getResultVariable();
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
		return getTarget() + " = " + getOperand();
	}
	
}
