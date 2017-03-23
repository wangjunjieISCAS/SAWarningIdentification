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

import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayType;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.NewArrayInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x = new T[y1]...[yn] or x = new T[]...[] = z.
 * @author Kevin Bierhoff
 *
 */
class NewArrayInstructionImpl extends AbstractAssignmentInstruction<ArrayCreation> 
implements NewArrayInstruction {
	
	/**
	 * @param node
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, IEclipseVariableQuery)
	 */
	public NewArrayInstructionImpl(ArrayCreation node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, Variable, IEclipseVariableQuery)
	 */
	public NewArrayInstructionImpl(ArrayCreation node, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
	}
	
	public ArrayType getArrayType() {
		return getNode().getType();
	}
	
	public List<Variable> getDimensionOperands() {
		return variables(getNode().dimensions());
	}
	
	public int getUnallocated() {
		return isInitialized() ? 0 : getArrayType().getDimensions();
	}
	
	public int getDimensions() {
		return getArrayType().getDimensions();
	}
	
	public boolean isInitialized() {
		return getNode().getInitializer() != null;
	}
	
	public Variable getInitOperand() {
		return variable(getNode().getInitializer());
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
		String baseType = "<Type>";
		if(getArrayType().getElementType().resolveBinding() != null)
			baseType = getArrayType().getElementType().resolveBinding().getName();
		String result = getTarget() + " = new " + baseType;
		for(Variable x : getDimensionOperands()) 
			result += "[" + x + "]";
		for(int i = 0; i < getUnallocated(); i++)
			result += "[]";
		if(isInitialized() == false)
			return result;
		return result + " = " + getInitOperand().toString();
	}

}
