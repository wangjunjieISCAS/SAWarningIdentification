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

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import edu.cmu.cs.crystal.internal.CrystalRuntimeException;
import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.SuperVariable;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.TypeVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This class converts AST nodes from a single method to TAC instructions.
 * A separate instance of this class is required for each method.
 * @author Kevin Bierhoff
 * @see CompilationUnitTACs#getMethodTAC(MethodDeclaration)
 */
public class EclipseTAC implements IEclipseVariableQuery {
	
	/** 
	 * Cache of TAC objects for recently analyzed methods.
	 * Uses {@link WeakHashMap} to avoid memory problems.
	 * 
	 * TODO It's possible that the key ends up preventing ASTs from being garbage-collected. 
	 */
//	private static final WeakHashMap<IMethodBinding, EclipseTAC> tacCache =
//		new WeakHashMap<IMethodBinding, EclipseTAC>();

	/**
	 * Returns the singleton TAC object for the given method.
	 * @param methodDecl
	 * @return the singleton TAC object for the given method.
	 */
//	public static synchronized EclipseTAC getInstance(MethodDeclaration methodDecl) {
//		EclipseTAC tac;
//		IMethodBinding methodBinding = methodDecl.resolveBinding();
//		// try to reuse existing TAC instructions for this method
//		tac = tacCache.get(methodBinding);
//		if(tac == null) {
//			tac = new EclipseTAC(methodBinding);
//			tacCache.put(methodBinding, tac);
//		}
//		return tac;
//	}
	
	/** The method represented with this TAC object. */
	private IMethodBinding method;
	/** Map from AST nodes to the instruction that represents it (populated lazily). */
	private HashMap<ASTNode, TACInstruction> instr;
	/** 
	 * Map from type bindings (for qualified <code>this</code>) 
	 * and <code>null</code> (for unqualified <code>this</code>) to 
	 * {@link ThisVariable <code>this</code>} variables (populated lazily).
	 */
	private HashMap<ITypeBinding, ThisVariable> thisVar;
	/**
	 * Map from qualifiers and <code>null</code> (for unqualified 
	 * <code>super</code> to {@link SuperVariable <code>super</code>}
	 * variables (populated lazily).
	 */
	private HashMap<Name, SuperVariable> superVar;
	/** Map from type and variable bindings to {@link Variable TAC variables}. */
	private HashMap<IBinding, Variable> variables;

	/**
	 * This class converts AST nodes from a single method to TAC instructions.
	 * A separate instance of this class is required for each method.
	 * @param method The method being converted to 3-address code.
	 */
	protected EclipseTAC(IMethodBinding method) {
		super();
		this.method = method;
		instr = new HashMap<ASTNode, TACInstruction>();
		variables = new HashMap<IBinding, Variable>();
		thisVar = new HashMap<ITypeBinding, ThisVariable>();
		superVar = new HashMap<Name, SuperVariable>();
	}
	
	/**
	 * Instruction for a given AST Node.
	 * @param astNode
	 * @return Instruction object for a given ASTNode, or <code>null</code> if none exists.
	 */
	public TACInstruction instruction(ASTNode astNode) {
		if(astNode == null)
			throw new IllegalArgumentException("No node given.");

		if(instr.containsKey(astNode))
			return instr.get(astNode);
		TACInstruction result = createInstruction(astNode);
		instr.put(astNode, result);
		return result;
	}

	public Variable variable(ASTNode astNode) {
		if(astNode == null)
			throw new IllegalArgumentException("No node given.");
		
		// first look for a matching instruction
		// need this order because SimpleName nodes can have instruction
		// (see ConditionalExpression) and be a variable by themselves
		TACInstruction result = instruction(astNode);
		if(result != null) {
			if(result instanceof ResultfulInstruction)
				return ((ResultfulInstruction<?>) result).getResultVariable();
			throw new IllegalArgumentException("AST node has no result: " + astNode);
		}
		
		// concrete-ST weirdnesses...
		if(astNode instanceof ParenthesizedExpression) {
			return variable(((ParenthesizedExpression) astNode).getExpression());
		}
		
		// maybe it's a variable
		if(astNode instanceof Name) {
			IBinding b = ((Name) astNode).resolveBinding();
			return getVariable(b);
		}
		if(astNode instanceof ThisExpression) {
			return getThisVariable((ThisExpression) astNode);
		}
		throw new CrystalRuntimeException("AST node has no result: " + astNode);
	}

	public SourceVariable sourceVariable(IVariableBinding binding) {
		return (SourceVariable) getVariable(binding);
	}
	
	public TypeVariable typeVariable(ITypeBinding binding) {
		return (TypeVariable) getVariable(binding);
	}
	
	/**
	 * Returns the represented method's (unqualified) <code>this</code>.
	 * @return the represented method's (unqualified) <code>this</code>.
	 */
	public ThisVariable thisVariable() {
		ITypeBinding thisBinding = resolveThisType();
		if(thisBinding == null)
			// static method
			return null;
		
		ThisVariable result = thisVar.get(thisBinding);
		if(result == null) {
			// unqualified this
			result = new ThisVariable(this);
			thisVar.put(thisBinding, result);
		}
		return result;
	}
	
	public ThisVariable implicitThisVariable(IBinding accessedElement) {
		if(isStaticBinding(accessedElement))
			throw new IllegalArgumentException("Accessed element is static: " + accessedElement);
		if(isStaticBinding(method))
			throw new IllegalStateException("Access happens in static method: " + accessedElement);
		
		ITypeBinding thisBinding = implicitThisBinding(accessedElement);
		boolean implicitQualifier = thisBinding.equals(method.getDeclaringClass()) == false;
		// TODO can this happen?
		if(thisBinding.getName().equals("") && implicitQualifier) {
			// true if (1) binding is an anonymous class and (2) binding is not the innermost class around the current method
			// not sure how to qualify "this" in this case
			throw new IllegalArgumentException("implicit this resolves not to innermost class: " + accessedElement);
		}
		
		ThisVariable result = thisVar.get(thisBinding);
		if(result == null) {
			if(implicitQualifier)
				// implicitly qualified this
				result = new ThisVariable(this, thisBinding);
			else
				// unqualified this
				result = new ThisVariable(this);
			thisVar.put(thisBinding, result);
		}
		return result;
	}
	
	/**
	 * Returns the type of <code>this</code>, if any.
	 * @return the type of <code>this</code> or <code>null</code> if this is a static method.
	 */
	// TODO should be package-private once variables move into tac.eclipse
	public ITypeBinding resolveThisType() { 
		if(isStaticBinding(method))
			return null;
		return method.getDeclaringClass();
	}
	
	public static boolean isStaticBinding(IBinding binding) {
		return (binding.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
	}
	
	public static boolean isDefaultBinding(IBinding binding) {
		return (binding.getModifiers() & (Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC)) == 0;
	}
	
	/**
	 * Determines the binding for the implicit <code>this</code>
	 * variable of the given accessed element (method or field).
	 * See the Java language specification for the definition of this
	 * algorithm 
	 * (<a href="http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.12.1">
	 * JLS $15.12.1</a>).  
	 * We use (generic) eclipse bindings instead
	 * of field/method names (as described in the JLS) to avoid having
	 * to worry about visibility issues.  
	 * In particular, an invisible
	 * method or field with the same name as <code>accessedElement</code>
	 * must not be considered per JLS.  
	 * (Hopefully) the eclipse binding
	 * mechanism already takes that into account, and we can find the 
	 * right visible binding by comparing eclipse bindings instead of names.
	 * @param accessedElement A method or field binding.
	 * @return the binding for the implicit <code>this</code>
	 * variable of the given accessed element (method or field).
	 */
	private ITypeBinding implicitThisBinding(IBinding accessedElement) {
		boolean isMethod;
		IBinding genericBinding; // generic version of accessedElement to simplify comparison
		if(accessedElement instanceof IMethodBinding) {
			if(((IMethodBinding) accessedElement).isConstructor())
				// constructor is easy because statically bound
				return ((IMethodBinding) accessedElement).getDeclaringClass();
			genericBinding = ((IMethodBinding) accessedElement).getMethodDeclaration();
			isMethod = true;
		}
		else {
			// must be a field...
			if(((IVariableBinding) accessedElement).isField() == false)
				throw new IllegalArgumentException("Invalid element for implicit this: " + accessedElement);
			genericBinding = ((IVariableBinding) accessedElement).getVariableDeclaration();
			isMethod = false;
		}
		
		ITypeBinding scope = method.getDeclaringClass();
		if(scope.isTopLevel())
			// easy: we're in a top-level class
			// this must bind to that class (no lexically enclosing classes exist)
			return scope;
		while(scope != null) {
			if(findElementDeclarationByName(genericBinding, isMethod, scope, false, false) != null)
				return scope;
			scope = scope.getDeclaringClass();
		}
		throw new IllegalArgumentException("Unknown element: " + accessedElement);
	}

	/**
	 * Depth-first search in class and interface hierarchy to find a method or field.
	 * @tag usage.parameter: Clients will usually want to pass <code>false</code> for both flags; they will be used in recursive calls.
	 * @param genericAccessedElement Most generic version of the binding to be searched for.
	 * @param isMethod This must match the type of <code>genericAccessedElement</code>:
	 * <code>true</code> to resolve a method, <code>false</code> to resolve a field.
	 * @param type Type to search.
	 * @param skipPrivate If <code>true</code>, private declarations are ignored.
	 * @param skipPackagePrivate If <code>true</code>, package-private declarations are ignored.
	 * @return First type found in the hierarchy that declares the given method or field.
	 */
	private ITypeBinding findElementDeclarationByName(
			IBinding genericAccessedElement,
			boolean isMethod,
			ITypeBinding type,
			boolean skipPrivate,
			boolean skipPackagePrivate) {
		if(isMethod) {
			for(IMethodBinding b : type.getDeclaredMethods()) {
				if(skipPrivate && Modifier.isPrivate(b.getModifiers()))
					continue; // skip private method
				if(skipPackagePrivate && isDefaultBinding(b))
					continue; // skip package-private method
				if(genericAccessedElement.equals(b.getMethodDeclaration() /* use generic method */)) {
					return type;
				}
			}
		}
		else {
			for(IVariableBinding b : type.getDeclaredFields()) {
				if(skipPrivate && Modifier.isPrivate(b.getModifiers()))
					continue; // skip private field
				if(skipPackagePrivate && isDefaultBinding(b))
					continue; // skip package-private field
				if(genericAccessedElement.equals(b.getVariableDeclaration() /* use generic field */))
					return type;
			}
		}
		
		ITypeBinding result = null;
		if(type.getSuperclass() != null) {
			ITypeBinding t = type.getSuperclass();
			result = findElementDeclarationByName(
					genericAccessedElement, isMethod, t, 
					true /* always skip private declarations in supertypes */,
					! t.getPackage().equals(type.getPackage()) /* skip package-private when leaving current package */);
			if(result != null)
				return result;
		}
		// go though interfaces as well
		for(ITypeBinding i : type.getInterfaces()) {
			// keep going for fields: could be static
			result = findElementDeclarationByName(
					genericAccessedElement, isMethod, i, 
					true /* always skip private declarations in supertypes */,
					! i.getPackage().equals(type.getPackage()) /* skip package-private when leaving current package */);
			if(result != null)
				return result;
		}
		return result;
	}
	
	public SuperVariable superVariable(Name qualifier) {
		ITypeBinding thisType = resolveThisType();
		if(thisType == null || thisType.getSuperclass() == null)
			// static method or no superclass
			return null;
		
		// TODO find super-type binding based on qualifier and accessed element
		SuperVariable result = superVar.get(qualifier);
		if(result == null) {
			result = new SuperVariable(this, qualifier);
			superVar.put(qualifier, result);
		}
		return result;
	}
	
	/**
	 * Returns the variable representing the given binding
	 * (parameter, local, or type variable).
	 * @param binding Parameter, local, or type variable.
	 * @return the variable representing the given binding.
	 */
	private Variable getVariable(IBinding binding) {
		Variable result = variables.get(binding);
		if(result == null) {
			if(binding instanceof IVariableBinding) {
				IVariableBinding vb = (IVariableBinding) binding;
				if(vb.isEnumConstant() || vb.isField()) 
					throw new IllegalArgumentException("Not a local: " + binding);
				// figure out whether it's declared locally
				IMethodBinding declaredIn = vb.getDeclaringMethod();
				while(declaredIn != null && declaredIn != declaredIn.getMethodDeclaration()) {
					declaredIn = declaredIn.getMethodDeclaration();
				}
				result = new SourceVariable(vb.getName(), vb, method.equals(declaredIn));
			}
			else if(binding instanceof ITypeBinding) {
				ITypeBinding tb = (ITypeBinding) binding;
				result = new TypeVariable(tb);
			}
			else 
				throw new IllegalArgumentException("Not a variable: " + binding);
			variables.put(binding, result);
		}
		return result;
	}

	/**
	 * Returns the {@link ThisVariable} for the given <code>this</code>
	 * expression.
	 * @param node
	 * @return the {@link ThisVariable} for the given <code>this</code>
	 * expression.
	 */
	private ThisVariable getThisVariable(ThisExpression node) {
		Name qualifier = node.getQualifier();
		/**
		 * The rule is that the declared, generic type, is the single canonical type.
		 * Parameterized types are actually different type bindings.
		 * @see org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment#createParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType)
		 */
		ITypeBinding generic_type = node.resolveTypeBinding().getTypeDeclaration();
		ThisVariable result = thisVar.get(generic_type);
		if(result == null) {
			// explicitly qualified this
			result = new ThisVariable(this, qualifier);
			thisVar.put(node.resolveTypeBinding().getTypeDeclaration(), result);
		}
		if(result.isImplicit()) {
			// fix up qualifier of previously created implicitly qualified this variable
			result.explicitQualifier(node.getQualifier());
		}
		return result;
	}

	/**
	 * Creates a fresh instruction to represent the given node.
	 * @param astNode Node in the method represented by this TAC object.
	 * @return a fresh instruction to represent the given node.
	 */
	private TACInstruction createInstruction(ASTNode astNode) {
		NewInstructionVisitor v = new NewInstructionVisitor();
		astNode.accept(v);
		return v.getResult();
	}
	
	/**
	 * Double-dispatch visitor to create the right type of instruction
	 * for the visited AST node.  Forwards relevant calls to
	 * {@link EclipseTACInstructionFactory}.  After visiting a node,
	 * the corresponding instruction can be retrieved with
	 * {@link #getResult()}.
	 * @author Kevin Bierhoff
	 * @see EclipseTAC#createInstruction(ASTNode)
	 */
	private class NewInstructionVisitor extends ASTVisitor {
		
		private TACInstruction result;
		private EclipseTACInstructionFactory factory;

		public NewInstructionVisitor() {
			super();
			this.factory = new EclipseTACInstructionFactory();
		}
		
		/**
		 * After a node was visited, this method returns the newly created
		 * instruction representing that node, if any.
		 * @return the newly created instruction representing that node, 
		 * or <code>null</code> for nodes that have no instruction associated
		 * with them.
		 */
		public TACInstruction getResult() {
			return result;
		}
		
		private void noResult() {
			result = null;
		}
		
		/**
		 * Set the new instruction being created.
		 * @param result Must not be <code>null</code>.
		 */
		private void setResult(TACInstruction result) {
			this.result = result;
		}
		
		@Override
		public boolean visit(AnnotationTypeDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(AnnotationTypeMemberDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(AnonymousClassDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ArrayAccess node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ArrayCreation node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ArrayInitializer node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ArrayType node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(AssertStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(Assignment node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(Block node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(BlockComment node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(BooleanLiteral node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(BreakStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(CastExpression node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(CatchClause node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(CharacterLiteral node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ClassInstanceCreation node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(CompilationUnit node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ConditionalExpression node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ConstructorInvocation node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ContinueStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(DoStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(EmptyStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(EnhancedForStatement node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(EnumConstantDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(EnumDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ExpressionStatement node) {
			noResult();  // not an expression; expression is inside
			return false;
		}

		@Override
		public boolean visit(FieldAccess node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(FieldDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ForStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(IfStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ImportDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(InfixExpression node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(Initializer node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(InstanceofExpression node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(Javadoc node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(LabeledStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(LineComment node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(MarkerAnnotation node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(MemberRef node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(MemberValuePair node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(MethodInvocation node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(MethodRef node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(MethodRefParameter node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(Modifier node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(NormalAnnotation node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(NullLiteral node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(NumberLiteral node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(PackageDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ParameterizedType node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ParenthesizedExpression node) {
			noResult();  // visit the parenthesized expression instead
			return false;
		}

		@Override
		public boolean visit(PostfixExpression node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(PrefixExpression node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(PrimitiveType node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(QualifiedName node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(QualifiedType node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ReturnStatement node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(SimpleName node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(SimpleType node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(SingleMemberAnnotation node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(SingleVariableDeclaration node) {
			setResult(factory.create(node, EclipseTAC.this));
			// not an expression, but include an "instruction" indicating the new variable
			return false;
		}

		@Override
		public boolean visit(StringLiteral node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(SuperConstructorInvocation node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(SuperFieldAccess node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(SuperMethodInvocation node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(SwitchCase node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(SwitchStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(SynchronizedStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(TagElement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(TextElement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(ThisExpression node) {
			// may need copy, if used inside conditionals
			// passing needed ThisVariable is a hack to avoid additional method in IEclipseVariableQuery 
			setResult(factory.create(node, EclipseTAC.this.getThisVariable(node), EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(ThrowStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(TryStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(TypeDeclaration node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(TypeDeclarationStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(TypeLiteral node) {
			setResult(factory.create(node, EclipseTAC.this));
			return false;
		}

		@Override
		public boolean visit(TypeParameter node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(VariableDeclarationExpression node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(VariableDeclarationFragment node) {
			setResult(factory.create(node, EclipseTAC.this));
			// not an expression, but include an "instruction" indicating the new variable
			return false;
		}

		@Override
		public boolean visit(VariableDeclarationStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(WhileStatement node) {
			noResult();  // not an expression
			return false;
		}

		@Override
		public boolean visit(WildcardType node) {
			noResult();  // not an expression
			return false;
		} 
	}

}
