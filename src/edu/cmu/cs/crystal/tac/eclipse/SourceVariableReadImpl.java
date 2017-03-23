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
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.KeywordVariable;
import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.SourceVariableReadInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This instruction indicates reading a variable that appears in the source program,
 * i.e. the receiver, locals, or parameters.  This instruction is even generated when
 * the source variable appears on the left-hand side of an assignment, to indicate that
 * the variable is being touched.
 * 
 * TODO Figure out if assignment targets should be a "SourceVariableRead" or not.
 *  
 * @author Kevin Bierhoff
 * @see AssignmentInstruction#getTarget()
 */
class SourceVariableReadImpl extends ResultfulInstruction<ASTNode> 
implements SourceVariableReadInstruction {

	private Variable variable;

	/**
	 * @param node
	 * @param variable the variable being read.
	 * @param tac
	 * @see ResultfulInstruction#ResultfulInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public SourceVariableReadImpl(ASTNode node, Variable variable, IEclipseVariableQuery tac) {
		super(node, tac);
		if(! (variable instanceof SourceVariable || variable instanceof KeywordVariable))
			throw new IllegalArgumentException("Not a source or keyword variable: " + variable);
		this.variable = variable;
	}

	public Variable getVariable() {
		return variable;
	}

	@Override
	protected final Variable getResultVariable() {
		return getVariable();
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
		return "read " + variable.toString();
	}

}
