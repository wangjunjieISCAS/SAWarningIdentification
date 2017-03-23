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

import org.eclipse.jdt.core.dom.Expression;

import edu.cmu.cs.crystal.tac.model.BinaryOperator;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 *
 */
class EclipseBinaryDesugaredOperation extends AbstractBinaryOperation<Expression> {

	private Expression operand1node;
	private Variable operand2;

	/**
	 * @param node
	 * @param operator
	 * @param tac
	 */
	public EclipseBinaryDesugaredOperation(Expression node,
			Expression operand1node, BinaryOperator operator, Variable operand2, 
			boolean fresh, IEclipseVariableQuery tac) {
		super(node, operator, fresh, tac);
		this.operand1node = operand1node;
		this.operand2 = operand2;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.BinaryOperation#getOperand1()
	 */
	@Override
	public Variable getOperand1() {
		return variable(operand1node);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.BinaryOperation#getOperand2()
	 */
	@Override
	public Variable getOperand2() {
		return operand2;
	}

}
