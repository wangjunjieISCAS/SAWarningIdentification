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

import org.eclipse.jdt.core.dom.InfixExpression;

import edu.cmu.cs.crystal.tac.model.BinaryOperator;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 *
 */
class EclipseBinaryInfixOperation extends AbstractBinaryOperation<InfixExpression> {

	/**
	 * @param node
	 * @param operator
	 * @param tac
	 */
	public EclipseBinaryInfixOperation(InfixExpression node,
			BinaryOperator operator, IEclipseVariableQuery tac) {
		super(node, operator, tac);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.BinaryOperation#getOperand1()
	 */
	@Override
	public Variable getOperand1() {
		return variable(getNode().getLeftOperand());
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.tac.eclipse.BinaryOperation#getOperand2()
	 */
	@Override
	public Variable getOperand2() {
		return variable(getNode().getRightOperand());
	}
	
}
