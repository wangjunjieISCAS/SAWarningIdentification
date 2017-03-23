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
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.InfixExpression;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.LabeledSingleResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This is just a helper class so the two branches can write to a common target variable
 *
 * @author Kevin Bierhoff
 */
final class EclipseMergeHelper extends AbstractAssignmentInstruction<ASTNode> {

	/**
	 * Constructor for ? :
	 * @param node
	 * @param tac
	 */
	public EclipseMergeHelper(ConditionalExpression node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * Constructor for && and ||
	 * @param node
	 * @param tac
	 */
	public EclipseMergeHelper(InfixExpression node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param tac
	 */
	public EclipseMergeHelper(ASTNode node, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
	}

	@Override
	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		// no explict transfer
		return value;
	}

	@Override
	public <LE> IResult<LE> transfer(ITACBranchSensitiveTransferFunction<LE> tf, List<ILabel> labels, LE value) {
		// no explict transfer
		return new LabeledSingleResult<LE>(value, labels);
	}

}
