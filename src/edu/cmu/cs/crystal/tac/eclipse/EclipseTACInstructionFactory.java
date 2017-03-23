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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.cmu.cs.crystal.tac.model.BinaryOperation;
import edu.cmu.cs.crystal.tac.model.BinaryOperator;
import edu.cmu.cs.crystal.tac.model.CopyInstruction;
import edu.cmu.cs.crystal.tac.model.SourceVariableReadInstruction;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.UnaryOperator;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * @author Kevin Bierhoff
 *
 */
public class EclipseTACInstructionFactory {
	
	private static final Logger log = Logger.getLogger(EclipseTACInstructionFactory.class.getName());

	public TACInstruction create(
			ArrayAccess node,
			IEclipseVariableQuery eclipseVariableQuery) {
		if(isLoad(node))
			return new LoadArrayInstructionImpl(node, eclipseVariableQuery);
		else
			return null;
	}

	public TACInstruction create(
			ArrayCreation node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new NewArrayInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			ArrayInitializer node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new ArrayInitInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			Assignment node,
			IEclipseVariableQuery eclipseVariableQuery) {
		BinaryOperator operator = null;  // null indicates "normal" assignment
		if(Assignment.Operator.BIT_AND_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.BITWISE_AND;
		else if(Assignment.Operator.BIT_OR_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.BITWISE_OR;
		else if(Assignment.Operator.BIT_XOR_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.BITWISE_XOR;
		else if(Assignment.Operator.DIVIDE_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.ARIT_DIVIDE;
		else if(Assignment.Operator.LEFT_SHIFT_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.SHIFT_LEFT;
		else if(Assignment.Operator.MINUS_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.ARIT_SUBTRACT;
		else if(Assignment.Operator.PLUS_ASSIGN.equals(node.getOperator())) {
			// TODO is this sufficient to distinguish addition and concatenation?
			if(node.resolveTypeBinding().isPrimitive())
				operator = BinaryOperator.ARIT_ADD;
			else
				operator = BinaryOperator.STRING_CONCAT;
		}
		else if(Assignment.Operator.REMAINDER_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.ARIT_MODULO;
		else if(Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.SHIFT_RIGHT;
		else if(Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.SHIFT_UNSIGNED_RIGHT;
		else if(Assignment.Operator.TIMES_ASSIGN.equals(node.getOperator()))
			operator = BinaryOperator.ARIT_MULTIPLY;
		else if(Assignment.Operator.ASSIGN.equals(node.getOperator())) {
			// normal assignment 
			TACInstruction store = createStore(
					node, 
					node.getLeftHandSide(),
					eclipseVariableQuery.variable(node.getRightHandSide()),
					eclipseVariableQuery);
			return store;
		}
		else
			throw new IllegalArgumentException("Unknown assignment operator \"" + node.getOperator() + "\" in: " + node);

		BinaryOperation binop = new EclipseBinaryAssignOperation(
				node, operator, eclipseVariableQuery);
		TACInstruction store = createStore(
				node, 
				node.getLeftHandSide(),
				binop.getTarget(),
				eclipseVariableQuery);
		return new EclipseInstructionSequence(node, new TACInstruction[] { binop, store }, 0, eclipseVariableQuery);
	}
	
	private TACInstruction createStore(
			Expression node,
			Expression targetNode, Variable source, 
			IEclipseVariableQuery eclipseVariableQuery) {
		if(targetNode instanceof ArrayAccess) {
			return new StoreArrayInstructionImpl(
					node, (ArrayAccess) targetNode,
					source,
					eclipseVariableQuery);
		}
		if(targetNode instanceof FieldAccess) {
			FieldAccess target = (FieldAccess) targetNode;
			return new StoreFieldInstructionImpl(
					node,  
					source,
					new EclipseReferenceFieldAccess(target, eclipseVariableQuery),
					eclipseVariableQuery);
		}
		if(targetNode instanceof SuperFieldAccess) {
			SuperFieldAccess target = (SuperFieldAccess) targetNode;
			return new StoreFieldInstructionImpl(
					node,  
					source,
					new EclipseSuperFieldAccess(target, eclipseVariableQuery),
					eclipseVariableQuery);
		}
		if(targetNode instanceof QualifiedName) {
			QualifiedName target = (QualifiedName) targetNode;
			IBinding binding = target.resolveBinding();
			if(binding instanceof IVariableBinding) {
				IVariableBinding vb = (IVariableBinding) binding;
				if(vb.isField()) {
					return new StoreFieldInstructionImpl(
							node, 
							source,
							new EclipseBrokenFieldAccess(target, eclipseVariableQuery),
							eclipseVariableQuery);
				}
			}
		}
		if(targetNode instanceof SimpleName) {
			SimpleName target = (SimpleName) targetNode;
			IBinding binding = target.resolveBinding();
			if(binding instanceof IVariableBinding) {
				IVariableBinding vb = (IVariableBinding) binding;
				if(vb.isField()) {
					// implicit field access on this
					return new StoreFieldInstructionImpl(
							node, 
							source,
							new EclipseImplicitFieldAccess(target, eclipseVariableQuery),
							eclipseVariableQuery);
				}
				else if(!vb.isEnumConstant()) {
					// local
					return new CopyInstructionImpl(node, source,
							true,
							eclipseVariableQuery.variable(targetNode),
							eclipseVariableQuery);
				}
			}
		}
		throw new IllegalArgumentException("Cannot create store for node: " + node);
	}

	public TACInstruction create(
			BooleanLiteral node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new LoadLiteralInstructionImpl(node, node.booleanValue(), eclipseVariableQuery);
	}

	public TACInstruction create(
			CastExpression node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new CastInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			CharacterLiteral node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new LoadLiteralInstructionImpl(node, node.charValue(), eclipseVariableQuery);
	}

	public TACInstruction create(
			ClassInstanceCreation node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new NewObjectInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			ConditionalExpression node, 
			IEclipseVariableQuery query) {
		return new EclipseMergeHelper(node, query);
	}

	public TACInstruction create(
			ConstructorInvocation node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new EclipseThisConstructorCallInstruction(node, eclipseVariableQuery);
	}

	public TACInstruction create(EnhancedForStatement node,
			EclipseTAC eclipseVariableQuery) {
		return new EnhancedForConditionInstructionImpl(node, eclipseVariableQuery);
	}

	/**
	 * This is not the only AST node that could represent a field access
	 * @param node
	 * @param eclipseVariableQuery
	 * @return A TACInstruction for this node, or null if it is not a field load
	 * @see #create(SimpleName, IEclipseVariableQuery)
	 * @see #create(QualifiedName, IEclipseVariableQuery)
	 */
	public TACInstruction create(
			FieldAccess node,
			IEclipseVariableQuery eclipseVariableQuery) {
		if(isLoad(node)) {
			return new LoadFieldInstructionImpl(
					node, 
					new EclipseReferenceFieldAccess(node, eclipseVariableQuery),
					eclipseVariableQuery);
		}
		else
			return null;
	}

	private boolean isLoad(Expression node) {
		if(node.getParent() instanceof Assignment) {
			Assignment parent = (Assignment) node.getParent();
			if(parent.getLeftHandSide() != node)
				return true;
			// node is on the left
			// it's still a load if this is += or something like that.
			return Assignment.Operator.ASSIGN.equals(parent.getOperator()) == false;
		}
		else if((node.getParent() instanceof VariableDeclaration) &&
				(((VariableDeclaration) node.getParent()).getName() == node))
			// node is the name of a declaration --> not a load but a store
			return false;
		else
			return true;
	}

	public TACInstruction create(
			InfixExpression node,
			IEclipseVariableQuery eclipseVariableQuery) {
		BinaryOperator operator = null;
		if(InfixExpression.Operator.AND.equals(node.getOperator())) 
			operator = BinaryOperator.BITWISE_AND;
		else if(InfixExpression.Operator.CONDITIONAL_AND.equals(node.getOperator())) 
			return new EclipseMergeHelper(node, eclipseVariableQuery);
		else if(InfixExpression.Operator.CONDITIONAL_OR.equals(node.getOperator())) 
			return new EclipseMergeHelper(node, eclipseVariableQuery);
		else if(InfixExpression.Operator.DIVIDE.equals(node.getOperator())) 
			operator = BinaryOperator.ARIT_DIVIDE;
		else if(InfixExpression.Operator.EQUALS.equals(node.getOperator())) 
			operator = BinaryOperator.REL_EQ;
		else if(InfixExpression.Operator.GREATER.equals(node.getOperator())) 
			operator = BinaryOperator.REL_GT;
		else if(InfixExpression.Operator.GREATER_EQUALS.equals(node.getOperator())) 
			operator = BinaryOperator.REL_GEQ;
		else if(InfixExpression.Operator.LEFT_SHIFT.equals(node.getOperator())) 
			operator = BinaryOperator.SHIFT_LEFT;
		else if(InfixExpression.Operator.LESS.equals(node.getOperator())) 
			operator = BinaryOperator.REL_LT;
		else if(InfixExpression.Operator.LESS_EQUALS.equals(node.getOperator())) 
			operator = BinaryOperator.REL_LEQ;
		else if(InfixExpression.Operator.MINUS.equals(node.getOperator())) 
			operator = BinaryOperator.ARIT_SUBTRACT;
		else if(InfixExpression.Operator.NOT_EQUALS.equals(node.getOperator())) 
			operator = BinaryOperator.REL_NEQ;
		else if(InfixExpression.Operator.OR.equals(node.getOperator())) 
			operator = BinaryOperator.BITWISE_OR;
		else if(InfixExpression.Operator.PLUS.equals(node.getOperator())) {
			// TODO is this sufficient to distinguish addition and concatenation?
			if(node.resolveTypeBinding().isPrimitive())
				operator = BinaryOperator.ARIT_ADD;
			else
				operator = BinaryOperator.STRING_CONCAT;
		}
		else if(InfixExpression.Operator.REMAINDER.equals(node.getOperator())) 
			operator = BinaryOperator.ARIT_MODULO;
		else if(InfixExpression.Operator.RIGHT_SHIFT_SIGNED.equals(node.getOperator())) 
			operator = BinaryOperator.SHIFT_RIGHT;
		else if(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED.equals(node.getOperator())) 
			operator = BinaryOperator.SHIFT_UNSIGNED_RIGHT;
		else if(InfixExpression.Operator.TIMES.equals(node.getOperator())) 
			operator = BinaryOperator.ARIT_MULTIPLY;
		else if(InfixExpression.Operator.XOR.equals(node.getOperator())) 
			operator = BinaryOperator.BITWISE_XOR;
		else
			throw new IllegalArgumentException("Unknown infix operator \"" + node.getOperator() + "\" in: " + node);
		
		// TODO handle extended argument list!!!
		return new EclipseBinaryInfixOperation(node, operator, eclipseVariableQuery);
	}

	public TACInstruction create(
			InstanceofExpression node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new InstanceofInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			MethodInvocation node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new EclipseNormalCallInstruction(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			NullLiteral node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new LoadLiteralInstructionImpl(node, null, eclipseVariableQuery);
	}

	public TACInstruction create(
			NumberLiteral node,
			IEclipseVariableQuery eclipseVariableQuery) {
		// TODO Parse the number string?
		return new LoadLiteralInstructionImpl(node, node.getToken(), eclipseVariableQuery);
	}

	public TACInstruction create(PostfixExpression node,
			IEclipseVariableQuery eclipseVariableQuery) {
		BinaryOperator operator;
		if(PostfixExpression.Operator.DECREMENT.equals(node.getOperator()))
			operator = BinaryOperator.ARIT_SUBTRACT;
		else if(PostfixExpression.Operator.INCREMENT.equals(node.getOperator())) 
			operator = BinaryOperator.ARIT_ADD;
		else 
			throw new IllegalArgumentException("Unknown postfix operator \"" + node.getOperator() + "\" in: " + node);

		ResultfulInstruction<?> copy = new CopyInstructionImpl(
				node, 
				eclipseVariableQuery.variable(node.getOperand()), 
				false,
				eclipseVariableQuery);
		ResultfulInstruction<?> one = new EclipseLoadDesugaredLiteralInstruction(
				node, 1, eclipseVariableQuery);
		ResultfulInstruction<?> binop = new EclipseBinaryDesugaredOperation(
				node, 
				node.getOperand(), operator, one.getResultVariable(), 
				true, eclipseVariableQuery);
		TACInstruction store = createStore(
				node, node.getOperand(), 
				binop.getResultVariable(), 
				eclipseVariableQuery);
		return new EclipseInstructionSequence(node, new TACInstruction[] { copy, one, binop, store }, 0, eclipseVariableQuery);
	}

	public TACInstruction create(PrefixExpression node,
			IEclipseVariableQuery eclipseVariableQuery) {
		if(PrefixExpression.Operator.COMPLEMENT.equals(node.getOperator())) {
			return new UnaryOperationImpl(node, UnaryOperator.BITWISE_COMPLEMENT, eclipseVariableQuery);
		}
		if(PrefixExpression.Operator.DECREMENT.equals(node.getOperator())) {
			ResultfulInstruction<?> one = new EclipseLoadDesugaredLiteralInstruction(
					node, 1, eclipseVariableQuery);
			ResultfulInstruction<?> binop = new EclipseBinaryDesugaredOperation(
					node, 
					node.getOperand(), BinaryOperator.ARIT_SUBTRACT, one.getResultVariable(), 
					false, eclipseVariableQuery);
			TACInstruction store = createStore(node, node.getOperand(), binop.getResultVariable(), eclipseVariableQuery);
			return new EclipseInstructionSequence(node, new TACInstruction[] { one, binop, store }, 1, eclipseVariableQuery);
		}
		if(PrefixExpression.Operator.INCREMENT.equals(node.getOperator())) {
			ResultfulInstruction<?> one = new EclipseLoadDesugaredLiteralInstruction(
					node, 1, eclipseVariableQuery);
			ResultfulInstruction<?> binop = new EclipseBinaryDesugaredOperation(
					node, node.getOperand(), 
					BinaryOperator.ARIT_ADD, one.getResultVariable(), 
					false, eclipseVariableQuery);
			TACInstruction store = createStore(node, node.getOperand(), binop.getResultVariable(), eclipseVariableQuery);
			return new EclipseInstructionSequence(node, new TACInstruction[] { one, binop, store }, 1, eclipseVariableQuery);
		}
		if(PrefixExpression.Operator.MINUS.equals(node.getOperator())) {
			return new UnaryOperationImpl(node, UnaryOperator.ARIT_MINUS, eclipseVariableQuery);
		}
		if(PrefixExpression.Operator.NOT.equals(node.getOperator())) {
			return new UnaryOperationImpl(node, UnaryOperator.BOOL_NOT, eclipseVariableQuery);
		}
		if(PrefixExpression.Operator.PLUS.equals(node.getOperator())) {
			return new UnaryOperationImpl(node, UnaryOperator.ARIT_PLUS, eclipseVariableQuery);
		}
		throw new IllegalArgumentException("Unknown prefix operator \"" + node.getOperator() + "\" in: " + node);
	}

	public TACInstruction create(QualifiedName node,
			IEclipseVariableQuery eclipseVariableQuery) {
		// careful with the disambiguation of field accesses
		IBinding binding = node.resolveBinding();
		if(binding instanceof IVariableBinding) {
			// expect this to be a field
			IVariableBinding var = (IVariableBinding) binding;
			if(var.isField() || var.isEnumConstant()) {
				if(isLoad(node))
					return new LoadFieldInstructionImpl(
							node, 
							new EclipseBrokenFieldAccess(node, eclipseVariableQuery),
							eclipseVariableQuery);
				else
					return null;
			}
			else
				throw new UnsupportedOperationException("Unexpected variable in qualified name");
		}
		// else expect this to be a package or type
		// in particular, method calls should never be qualified names
		if(binding instanceof IMethodBinding)
			throw new UnsupportedOperationException("Unexpected occurrance of method call as qualified name.");
		return null;
	}

	public TACInstruction create(ReturnStatement node,
			EclipseTAC eclipseVariableQuery) {
		if(node.getExpression() == null)
			return null;
		return new ReturnInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(SimpleName node,
			IEclipseVariableQuery eclipseVariableQuery) {
		// careful with the disambiguation of field accesses
		IBinding binding = node.resolveBinding();
		if(binding instanceof IVariableBinding) {
			IVariableBinding vb = (IVariableBinding) binding;
			if(vb.isField() || vb.isEnumConstant()) {
				if((node.getParent() instanceof QualifiedName) &&
						(((QualifiedName) node.getParent()).getName() == node))
					// trying to avoid double-creation of field accesses through
					// QualifiedName and nested SimpleName
					return null;
				if((node.getParent() instanceof FieldAccess) &&
						(((FieldAccess) node.getParent()).getName() == node))
					// trying to avoid double-creation of field accesses through
					// FieldAccess and nested SimpleName
					return null;
				// implicit field access on this
				if(isLoad(node))
					return new LoadFieldInstructionImpl(
							node,
							new EclipseImplicitFieldAccess(node, eclipseVariableQuery),
							eclipseVariableQuery);
				else
					return null;
			}
			else {
				// local
				if(isLoad(node)) {
					SourceVariableReadInstruction read = new SourceVariableReadImpl(node, 
							eclipseVariableQuery.sourceVariable(vb), eclipseVariableQuery);
					if(AbstractAssignmentInstruction.checkIfCopyNeeded(node) != null) {
						// need to explicitly copy accessed variable
						CopyInstruction copy = new CopyInstructionImpl(
								node, 
								eclipseVariableQuery.sourceVariable(vb),
								false,
								eclipseVariableQuery);
						return new EclipseInstructionSequence(node, 
								new TACInstruction[] { read, copy }, eclipseVariableQuery);
					}
					else
						return read;
				}
				else
					return null;
			}
		}
		if(log.isLoggable(Level.WARNING)
				&& ((binding instanceof ITypeBinding) == false)     // type name
				&& ((binding instanceof IMethodBinding) == false)   // method name
				&& ((binding instanceof IPackageBinding) == false)) // package name
			log.warning("Ignore simple name \"" + node + "\" inside node: " + node.getParent());
		return null;
	}

	public TACInstruction create(
			StringLiteral node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new LoadLiteralInstructionImpl(node, node.getLiteralValue(), eclipseVariableQuery);
	}

	public TACInstruction create(
			SuperConstructorInvocation node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new EclipseSuperConstructorCallInstruction(node, eclipseVariableQuery);
	}

	public TACInstruction create(SuperFieldAccess node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new LoadFieldInstructionImpl(
				node,
				new EclipseSuperFieldAccess(node, eclipseVariableQuery),
				eclipseVariableQuery);
	}

	public TACInstruction create(
			SuperMethodInvocation node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new EclipseSuperCallInstruction(node, eclipseVariableQuery);
	}

	public TACInstruction create(
			ThisExpression node, ThisVariable accessedVariable, 
			IEclipseVariableQuery eclipseVariableQuery) {
		if(AbstractAssignmentInstruction.checkIfCopyNeeded(node) != null) {
			// need to explicitly copy accessed variable
			return new CopyInstructionImpl(
					node, 
					accessedVariable,
					false,
					eclipseVariableQuery);
		}
		// variable accesses don't get instructions (unless inside conditional)
		return null;
	}

	public TACInstruction create(TypeLiteral node,
			IEclipseVariableQuery eclipseVariableQuery) {
		return new DotClassInstructionImpl(node, eclipseVariableQuery);
	}

	public TACInstruction create(VariableDeclaration node,
			IEclipseVariableQuery eclipseVariableQuery) {
		if(node.getParent() instanceof FieldDeclaration) {
			// this can happen in the constructor, where the CFG inlines field initializers
			// need to create a store iff this declaration has an initializer
			if(node.getInitializer() == null)
				return null;
			return new StoreFieldInstructionImpl(
					node, 
					eclipseVariableQuery.variable(node.getInitializer()), 
					new EclipseFieldDeclaration(node, eclipseVariableQuery),
					eclipseVariableQuery);
			
		}
		else {
			// local variable
			SourceVariableDeclarationImpl decl = 
				new SourceVariableDeclarationImpl(node, eclipseVariableQuery);
			if(node.getInitializer() == null)
				return decl;
			// copy result of previous initializer into declared variable
			CopyInstructionImpl init = new CopyInstructionImpl(node, 
					eclipseVariableQuery.variable(node.getInitializer()),
					true,
					eclipseVariableQuery.sourceVariable(decl.resolveBinding()),
					eclipseVariableQuery);
			return new EclipseInstructionSequence(node,
					new TACInstruction[] { decl, init },
					eclipseVariableQuery);
		}
	}

}
