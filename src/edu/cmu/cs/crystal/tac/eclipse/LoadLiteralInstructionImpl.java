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

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.StringLiteral;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.LoadLiteralInstruction;

/**
 * x = l, an assignment of a literal value to a variable.
 * 
 * Example:<br>
 * <code>a = 4;</code>
 * 
 * @author Kevin Bierhoff
 *
 */
class LoadLiteralInstructionImpl extends AbstractAssignmentInstruction<Expression> 
implements LoadLiteralInstruction {
	
	private Object literal;

	/**
	 * @param node
	 * @param literal
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, IEclipseVariableQuery)
	 */
	public LoadLiteralInstructionImpl(Expression node, Object literal, IEclipseVariableQuery tac) {
		super(node, tac);
		this.literal = literal;
	}

	/**
	 * @param node
	 * @param literal
	 * @param target
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, boolean, IEclipseVariableQuery)
	 */
	public LoadLiteralInstructionImpl(Expression node, Object literal, 
			boolean fresh, IEclipseVariableQuery tac) {
		super(node, fresh, tac);
		this.literal = literal;
	}
	
	public Object getLiteral() {
		return literal;
	}
	
	public boolean isPrimitive() {
		return (getNode() instanceof BooleanLiteral) 
			|| (getNode() instanceof CharacterLiteral)
			|| (getNode() instanceof NumberLiteral);
	}
	
	public boolean isNumber() {
		return getNode() instanceof NumberLiteral;
	}
	
	public boolean isNull() {
		return getNode() instanceof NullLiteral;
	}
	
	public boolean isNonNullString() {
		return getNode() instanceof StringLiteral; 
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
		return getTarget() + " = " + getLiteral();
	}

}
