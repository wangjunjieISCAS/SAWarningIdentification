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

import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Expression;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.StoreArrayInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x[y] = z.
 * 
 * @author Kevin Bierhoff
 *
 */
class StoreArrayInstructionImpl extends AbstractStoreInstruction 
implements StoreArrayInstruction {
	
	/** The array being written to. */
	private ArrayAccess targetNode;

	/**
	 * @param node
	 * @param targetNode The array being written to.
	 * @param source
	 * @param tac
	 * @see AbstractStoreInstruction#AbstractStoreInstruction(Expression, Variable, IEclipseVariableQuery)
	 */
	public StoreArrayInstructionImpl(Expression node, ArrayAccess targetNode, Variable source, IEclipseVariableQuery tac) {
		super(node, source, tac);
		this.targetNode = targetNode;
	}
	
	public Variable getDestinationArray() {
		return variable(getTargetNode().getArray());
	}
	
	public Variable getAccessedArrayOperand() {
		return variable(getTargetNode().getArray());
	}
	
	public Variable getArrayIndex() {
		return variable(getTargetNode().getIndex());
	}
	
	protected ArrayAccess getTargetNode() {
		return targetNode;
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
		return getDestinationArray() + "[" + getArrayIndex() + "] = " + getSourceOperand();
	}

}
