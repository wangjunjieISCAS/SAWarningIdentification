package ca.uwaterloo.ece.qhanam.slicer;

import java.util.LinkedList;
import java.util.Hashtable;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class AliasVisitor extends ASTVisitor {
	
	private int seedLine;		// The line number of the seed statement
	private Slicer.Direction direction;	// Indicates we are constructing a backwards slice
	private Hashtable<Integer,LinkedList<String>> aliases;	// TODO: this isn't really a suitable structure for this...
	private LinkedList<String> seedVariables;
	
	public AliasVisitor(int seedLine, Slicer.Direction direction, Hashtable<Integer,LinkedList<String>> aliases, LinkedList<String> seedVariables){
		super();
		this.seedLine = seedLine;
		this.direction = direction;
		this.aliases = aliases;
		this.seedVariables = seedVariables;
	}
	
	/**
	 * We need this for alias analysis.
	 * 
	 * TODO: We need to do this until we've reached a fixed point.
	 * TODO: We need to also include other statements... like variable decalaration. What else?
	 */
	public boolean visit(Assignment node){
		ASTNode statement = Slicer.getStatement(node);
		
		if(node.getLeftHandSide().getNodeType() == ASTNode.SIMPLE_NAME){
			/* Local variable being assigned. */
			SimpleName localVar = (SimpleName) node.getLeftHandSide();
			IBinding binding = localVar.resolveBinding();
			//System.out.println("Assigning local variable " + binding.getKey());
		}
		else if(node.getLeftHandSide().getNodeType() == ASTNode.FIELD_ACCESS){
			/* Field being assigned. */
			FieldAccess fieldVar = (FieldAccess) node.getLeftHandSide();
			IVariableBinding binding = fieldVar.resolveFieldBinding();
			//System.out.println("Assigning field: " + binding.getKey());
		}
		
		if(node.getRightHandSide().getNodeType() == ASTNode.SIMPLE_NAME){
			/* Local variable being read. */
			SimpleName localVar = (SimpleName) node.getLeftHandSide();
			IBinding binding = localVar.resolveBinding();
			//System.out.println("Assigning local variable " + binding.getKey());
		}
		else if(node.getRightHandSide().getNodeType() == ASTNode.METHOD_INVOCATION){
			/* Method return value being read (ie. we don't know what's being returned). */
			MethodInvocation methodInv = (MethodInvocation) node.getRightHandSide();
			IMethodBinding binding = methodInv.resolveMethodBinding();
			//System.out.println("Reading method invocation " + binding.getKey());
		}
		
		//System.out.println("Left hand expression: " + node.getLeftHandSide().toString());
		//System.out.println("Right hand expression: " + node.getRightHandSide().toString());
		return true;
	}
	
	/**
	 * The first thing we do is add the variable to the node aliases if it is
	 * present in a statement.
	 */
	public boolean visit(SimpleName node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveBinding();
		
		/* Make sure this is a variable. */
		if(binding instanceof IVariableBinding){
			LinkedList<String> variables;
			
			/* Get the statement. */
			Statement statement = Slicer.getStatement(node);
			
			if(this.aliases.containsKey(statement)){
				variables = this.aliases.get(statement);
			}
			else{
				variables = new LinkedList<String>();
				this.aliases.put(new Integer(statement.getStartPosition()), variables);
			}
			variables.add(binding.getKey());
		}
		
		return true;
	}
	
	/**
	 * Get the variables for the seed statement.
	 */
	public boolean visitStatement(Statement node){
		/* Get the line number for the statement. */
		int line = Slicer.getLineNumber(node);
		
		if(line == this.seedLine){
			/* We need to keep track of the variables in the seed statement. */
			if(this.seedVariables.isEmpty()){
				SeedVisitor seedVisitor = new SeedVisitor(this.seedVariables);
				node.accept(seedVisitor);
			}
		}
		
		return true;
	}
	
	/**
	 * Add any variables or fields that appear in the seed statement
	 * to the list of variables that we need to check for.
	 * @author qhanam
	 *
	 */
	private class SeedVisitor extends ASTVisitor {
		
		LinkedList<String> seedVariables;
		
		public SeedVisitor(LinkedList<String> seedVariables){
			super();
			this.seedVariables = seedVariables;
		}
		
		/**
		 * The first thing we do is add the variable to the node aliases if it is
		 * present in a statement.
		 */
		public boolean visit(SimpleName node){
			/* All we really need from this is the variable binding. */
			IBinding binding = node.resolveBinding();
			
			/* Make sure this is a variable. */
			if(binding instanceof IVariableBinding){
				seedVariables.add(binding.getKey());
			}
			
			return true;
		}
	}
	
	// TODO: This is annoying... but I can't think of a better way right now
	public boolean visit(AssertStatement node){return this.visitStatement(node);}
	public boolean visit(Block node){return this.visitStatement(node);}
	public boolean visit(BreakStatement node){return this.visitStatement(node);}
	public boolean visit(ConstructorInvocation node){return this.visitStatement(node);}
	public boolean visit(ContinueStatement node){return this.visitStatement(node);}
	public boolean visit(DoStatement node){return this.visitStatement(node);}
	public boolean visit(EmptyStatement node){return this.visitStatement(node);}
	public boolean visit(EnhancedForStatement node){return this.visitStatement(node);}
	public boolean visit(ExpressionStatement node){return this.visitStatement(node);}
	public boolean visit(ForStatement node){return this.visitStatement(node);}
	public boolean visit(IfStatement node){return this.visitStatement(node);}
	public boolean visit(LabeledStatement node){return this.visitStatement(node);}
	public boolean visit(ReturnStatement node){return this.visitStatement(node);}
	public boolean visit(SuperConstructorInvocation node){return this.visitStatement(node);}
	public boolean visit(SwitchCase node){return this.visitStatement(node);}
	public boolean visit(SwitchStatement node){return this.visitStatement(node);}
	public boolean visit(SynchronizedStatement node){return this.visitStatement(node);}
	public boolean visit(ThrowStatement node){return this.visitStatement(node);}
	public boolean visit(TryStatement node){return this.visitStatement(node);}
	public boolean visit(TypeDeclarationStatement node){return this.visitStatement(node);}
	public boolean visit(VariableDeclarationStatement node){return this.visitStatement(node);}
	public boolean visit(WhileStatement node){return this.visitStatement(node);}
}
