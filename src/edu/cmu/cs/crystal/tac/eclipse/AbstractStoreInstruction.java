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

import edu.cmu.cs.crystal.tac.model.StoreInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This class extends {@link ResultfulInstruction} because assignments
 * in Java have a result that can be accessed by the surrounding expression,
 * the {@link #getSourceOperand() source}.
 * @author Kevin Bierhoff
 */
abstract class AbstractStoreInstruction extends ResultfulInstruction<ASTNode> 
implements StoreInstruction {
	
	private Variable sourceOperand;
	
	/**
	 * @param node
	 * @param sourceOperand The operand being stored.
	 * @param tac
	 * @see ResultfulInstruction#ResultfulInstruction(org.eclipse.jdt.core.dom.ASTNode, IEclipseVariableQuery)
	 */
	public AbstractStoreInstruction(ASTNode node, Variable sourceOperand, IEclipseVariableQuery tac) {
		super(node, tac);
		if(sourceOperand == null)
			throw new IllegalArgumentException("sourceOperand must not be null.");
		this.sourceOperand = sourceOperand;
	}

	public Variable getSourceOperand() {
		return sourceOperand;
	}

	@Override
	protected final Variable getResultVariable() {
		return getSourceOperand();
	}
	
}
