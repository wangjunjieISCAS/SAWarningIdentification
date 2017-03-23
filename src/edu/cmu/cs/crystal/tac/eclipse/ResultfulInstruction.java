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

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This is an intermediate helper class that simplifies TAC generation.
 * It should not be used by clients directly.
 * 
 * @author Kevin Bierhoff
 *
 * @see EclipseTAC#variable(ASTNode)
 */
abstract class ResultfulInstruction<E extends ASTNode> extends AbstractTACInstruction<E> {

	/**
	 * Inherited constructor.
	 * @param node
	 * @param tac
	 * 
	 * @see AbstractTACInstruction#AbstractTACInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public ResultfulInstruction(E node, IEclipseVariableQuery tac) {
		super(node, tac);
	}
	
	/**
	 * Returns the variable representing the result of this instruction
	 * that can be used as an operand to a subsequent instruction.
	 * @return Variable representing the result of this instruction.
	 */
	protected abstract Variable getResultVariable();

}
