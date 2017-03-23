package ca.uwaterloo.ece.qhanam.slicer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.QualifiedName;

/**
 * Checks whether this ASTNode (Statement) contains a data dependency from
 * the given list.
 * 
 * Strategy:
 * 	BDD & FDDSeed - Track assignments, declarations, objects of method call and arguments of method call.
 * 	FDD & BDDSeed - Track all other variable uses
 * 
 * @author qhanam
 */
public class FDDVisitor extends DependencyVisitor {
	
	/* The list of all possible variables and their aliases at this point in the CFG. */
	protected LinkedList<String> aliases;
	
	/* The slicer options. */
	private List<Slicer.Options> options;
	
	/**
	 * Create DataDependencyVisitor
	 * @param 
	 */
	public FDDVisitor(LinkedList<String> aliases, List<Slicer.Options> options){
		super();
		this.aliases = aliases;
		this.options = options;
	}
	
	/**
	 * Add the variable to the node aliases.
	 */
	public boolean visit(QualifiedName node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveBinding();
		
		if(binding == null){
			if(this.aliases.contains(node.getFullyQualifiedName())){
				this.result = true;
				return false;
			}
		}
		else if(binding instanceof IVariableBinding){
			if(this.aliases.contains(binding.getKey())){
				this.result = true;
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Add the variable to the node aliases.
	 */
	public boolean visit(FieldAccess node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveFieldBinding();
		
		if(binding == null){
			if(this.aliases.contains(node.getName().toString())){
				this.result = true;
				return false;
			}
				
		}
		else if(binding instanceof IVariableBinding){
			if(this.aliases.contains(binding.getKey())){
				this.result = true;
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Add the variable to the node aliases.
	 */
	public boolean visit(SimpleName node){
		/* All we really need from this is the variable binding. */
		IBinding binding = node.resolveBinding();
		
		/* Since we already intercept method calls, we can be sure
		 * that this isn't part of a method call. */
		if(binding == null){
			if(this.aliases.contains(node.getFullyQualifiedName())){
				this.result = true;
				return false;
			}
		}
		else if(binding instanceof IVariableBinding){
			if(this.aliases.contains(binding.getKey())){
				this.result = true;
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * We only need to investigate the right hand side of an assignment.
	 */
	public boolean visit(Assignment node){
		node.getRightHandSide().accept(this);
		/* If the operator uses the original variable, then
		 * we need to add it as well (ie. anything operator except
		 * regular assignment) */
		if(node.getOperator() != Assignment.Operator.ASSIGN)
			node.getLeftHandSide().accept(this);
		return false;
	}
	
	/**
	 * Since we don't get method parameters when we visit a SimpleName, we
	 * get them from the actual method invocation.
	 * 
	 * We visit the argument expressions, but not the method call itself.
	 */
	public boolean visit(MethodInvocation node){
 		List<Expression> args = node.arguments();
		for(Expression arg : args){
			arg.accept(this);
		}
		
		if(node.getExpression() != null)
			node.getExpression().accept(this);
		
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(IfStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 * 
	 * TODO: Are we handling this statement type properly?
	 */
	public boolean visit(DoStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(EnhancedForStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(ForStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		List<Expression> initializers = node.initializers();
		for(Expression initializer : initializers){
			initializer.accept(this);
		}
		/* Don't visit the children. */
		return false;
	}

	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(SwitchStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
	

	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(SynchronizedStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;	
	}
	
	/**
	 * We want to track the variables from the expression only.
	 */
	public boolean visit(WhileStatement node){
		/* Visit the expression part. */
		node.getExpression().accept(this);
		/* Don't visit the children. */
		return false;
	}
}
