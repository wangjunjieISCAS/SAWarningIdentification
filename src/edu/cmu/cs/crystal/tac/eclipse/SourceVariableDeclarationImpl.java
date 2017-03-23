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
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.SourceVariableDeclaration;

/**
 * T x.  This node represents the declaration of a variable in the source, i.e. a method
 * parameter or local variable.
 * Notice that temporary and keyword variables do <i>not</i> have an explicit declaration.
 * 
 * @author Kevin Bierhoff
 *
 */
class SourceVariableDeclarationImpl 
extends AbstractTACInstruction<VariableDeclaration> 
implements SourceVariableDeclaration {

	/**
	 * @param node
	 * @param tac
	 * @see AbstractTACInstruction#AbstractTACInstruction(ASTNode, IEclipseVariableQuery)
	 */
	public SourceVariableDeclarationImpl(VariableDeclaration node, IEclipseVariableQuery tac) {
		super(node, tac);
		IVariableBinding b = node.resolveBinding();
		if(b.isField())
			throw new IllegalArgumentException("Field declaration: " + node);
		if(b.isEnumConstant())
			throw new IllegalArgumentException("Enum declaration: " + node);
	}
	
	public SourceVariable getDeclaredVariable() {
		return (SourceVariable) targetVariable(getNode());
	}

	public IVariableBinding resolveBinding() {
		return getNode().resolveBinding();
	}

	public boolean isCaughtVariable() {
		ASTNode parent = this.getNode().getParent();
		if( parent instanceof CatchClause ) {
			// This is not enough. We must make sure that this variable
			// is being declared inside the declaration part.
			CatchClause catch_clause = (CatchClause)parent;
			if( catch_clause.getException().equals(this.getNode()) ) {
				return true;
			}
		}
		return false;
	}

	public boolean isEnhancedForLoopVariable() {
		ASTNode parent = this.getNode().getParent();
		if (parent instanceof EnhancedForStatement) {
			// This is not enough. We must make sure that this variable
			// is being declared inside the declaration part.
			EnhancedForStatement loop = (EnhancedForStatement) parent;
			if (loop.getParameter().equals(this.getNode()) ) {
				return true;
			}
		}
		return false;
	}


	
	public boolean isFormalParameter() {
		return getNode().getParent() instanceof MethodDeclaration;
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
		return "declare " + getDeclaredVariable();
	}
	
}
