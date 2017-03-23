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

import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.NewObjectInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * x = new C(z1, ..., zn).
 * @author Kevin Bierhoff
 *
 */
class NewObjectInstructionImpl extends AbstractAssignmentInstruction<ClassInstanceCreation> 
implements NewObjectInstruction {

	/**
	 * @param node
	 * @param tac
	 */
	public NewObjectInstructionImpl(ClassInstanceCreation node, IEclipseVariableQuery tac) {
		super(node, tac);
	}

	/**
	 * @param node
	 * @param target
	 * @param tac
	 */
	public NewObjectInstructionImpl(ClassInstanceCreation node, Variable target,
			IEclipseVariableQuery tac) {
		super(node, target, tac);
	}

	public IMethodBinding resolveBinding() {
		return getNode().resolveConstructorBinding();
	}
	
	public boolean isAnonClassType() {
		return getNode().getAnonymousClassDeclaration() != null;
	}

	public List<Variable> getArgOperands() {
		return variables(getNode().arguments());
	}

	public ITypeBinding resolveInstantiatedType() {
		return getNode().resolveTypeBinding();
	}

	public boolean hasOuterObjectSpecifier() {
		if(getNode().getExpression() != null) {
			return true;
		}
		else if(getNode().resolveTypeBinding().isLocal()) {
			// local classes cannot have outer object specifier in Java
			// this is weird though b/c they do capture the outer "this"
			return false; 
		}
		else if(getNode().resolveTypeBinding().isNested()) {
			// non-local non-static nested classes have an explicit outer object, "this"
			return ! Modifier.isStatic(getNode().resolveTypeBinding().getDeclaredModifiers());
		}
		else 
			return false;
	}
	
	public Variable getOuterObjectSpecifierOperand() {
		if(getNode().getExpression() != null) {
			// explicit qualifier -> use it
			return variable(getNode().getExpression());
		}
		else if(hasOuterObjectSpecifier()) {
			// otherwise the qualifier is implicit: "this"
			return receiverVariable();
		}
		else
			return null;
			
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
		String qual = ""; String anon = ""; String type = "<Type>";
		if(hasOuterObjectSpecifier())
			qual = getOuterObjectSpecifierOperand().toString() + ".";
		if(isAnonClassType())
			anon = "<Anon>-";
		if(resolveBinding() != null)
			type = resolveBinding().getName();
		return getTarget() + " = " + qual + "new " + anon + type + "(" + argsString(getArgOperands()) + ")";
	}

}
