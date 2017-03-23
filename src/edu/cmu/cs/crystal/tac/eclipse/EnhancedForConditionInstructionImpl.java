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

import org.eclipse.jdt.core.dom.EnhancedForStatement;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.EnhancedForConditionInstruction;
import edu.cmu.cs.crystal.tac.model.SourceVariableDeclaration;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * <p>Instruction representing the "has next" test at the top of a Java 5 enhanced <b>for</b>
 * loop.</p>
 * 
 * <code>
 * List<String> strings = ...;
 * for(String s : strings) ...;
 * </code>
 * 
 * <p>Every iteration of an extended <b>for</b> loop such as the one above
 * begins with an implicit test whether
 * the iterated-over {@link java.lang.Iterable} has another element.
 * This implicit test is represented with this instruction.</p>
 * 
 * <p>Notice that the expression that evaluates to the iterated-over 
 * is <i>not</i> represented here and instead is
 * represented with separate instructions.  However, {@link #getIteratedOperand()}
 * returns the variable representing the result of that evaluation.  (This
 * arrangement models that the evaluation of the iterated-over Iterable
 * is not part of the loop.)</p>
 * 
 * <p>The local variable being declared as also not represented with this instruction
 * and instead is represented with a separate {@link SourceVariableDeclaration}.
 * (This latter choice models that the declared variable is only available inside the loop.)</p>
 * 
 * <p>We chose to preserve enhanced <b>for</b> loops with this instruction instead
 * of desugaring it into a regular <b>for</b> loop over a {@link java.util.Iterator}
 * for practical reasons (the Eclipse AST does not do this) and to allow analyses
 * to benefit from the stylized iteration that encoded with enhanced <b>for</b> that,
 * e.g., does not permit modifications to the iterated-over {@link java.lang.Iterable}.</p>
 * 
 * @author Kevin Bierhoff
 * @since 5/27/2008
 * @see java.util.Iterator#hasNext()
 */
class EnhancedForConditionInstructionImpl 
extends AbstractTACInstruction<EnhancedForStatement> 
implements EnhancedForConditionInstruction {

	/**
	 * @param node
	 * @param tac
	 * @see AbstractAssignmentInstruction#AbstractAssignmentInstruction(org.eclipse.jdt.core.dom.ASTNode, IEclipseVariableQuery)
	 */
	public EnhancedForConditionInstructionImpl(EnhancedForStatement node, IEclipseVariableQuery tac) {
		super(node, tac);
	}
	
	public Variable getIteratedOperand() {
		return variable(getNode().getExpression());
	}

	@Override
	public <LE> LE transfer(ITACTransferFunction<LE> tf, LE value) {
		return tf.transfer(this, value);
	}

	@Override
	public <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf,
			List<ILabel> labels, LE value) {
		return tf.transfer(this, labels, value);
	}

	@Override
	public String toString() {
		return "Test if another element in " + getIteratedOperand() + " for enhanced for loop";
	}

}
