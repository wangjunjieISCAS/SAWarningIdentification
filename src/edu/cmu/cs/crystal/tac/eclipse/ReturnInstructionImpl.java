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

import org.eclipse.jdt.core.dom.ReturnStatement;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.ReturnInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 * @since 3.3.2
 */
public class ReturnInstructionImpl extends AbstractTACInstruction<ReturnStatement> 
		implements ReturnInstruction {

	/**
	 * Creates a return instruction for the given return statement, which must return a value.
	 * @param node Return statement with non-<code>null</code> expression.
	 * @param tac
	 */
	public ReturnInstructionImpl(ReturnStatement node, IEclipseVariableQuery tac) {
		super(node, tac);
		if(node.getExpression() == null)
			throw new IllegalArgumentException("Explicit return instructions only for actual values.");
	}
	
	public Variable getReturnedVariable() {
		return variable(getNode().getExpression());
	}

	@Override
	public <LE> LE transfer(
			ITACTransferFunction<LE> tf, LE value) {
		return tf.transfer(this, value);
	}

	@Override
	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels,
			LE value) {
		return tf.transfer(this, labels, value);
	}
	
	@Override
	public String toString() {
		return "return " + getReturnedVariable().toString();
	}

}
