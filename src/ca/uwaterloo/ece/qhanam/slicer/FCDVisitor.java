package ca.uwaterloo.ece.qhanam.slicer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
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
import org.eclipse.jdt.core.dom.ReturnStatement;

/**
 * Checks whether this ASTNode (Statement) is control dependent on 
 * the seed statement.
 * @author qhanam
 */
public class FCDVisitor extends DependencyVisitor {
	
	/* The slicer options. */
	private List<Slicer.Options> options;
	
	/* The seed statement. */
	private ASTNode seed;
	
	/**
	 * Create DataDependencyVisitor
	 * @param 
	 */
	public FCDVisitor(List<Slicer.Options> options, ASTNode seed){
		super();
		this.options = options;
		this.seed = seed;
	}

	/**
	 * The seed statement is a control dependency of another statement
	 * if the seed statement affects the whether or not the
	 * statement is executed. We check if the seed statement
	 * is a control statement, then if the statement is contained
	 * in the body of that statement. If the control statement
	 * contains a return statement this also affects everything
	 * downstream.
	 * 
	 * @param node The conditional statement.
	 * @return
	 */
	public void isControlDependency(ASTNode node){
		/* If the seed statement is not a control statement, it cannot
		 * be a control dependency of node. */
		if(!FCDVisitor.isConditional(this.seed)){
			this.result = false;
			return;
		}
		
		/* If there is a return statement in the body, it affects 
		 * everything downstream. */
		ReturnVisitor rv = new ReturnVisitor();
		this.seed.accept(rv);
		if(rv.result){
			this.result = true;
			return;
		}
		
		/* If the seed is the parent of node, then it is a control
		 * dependency of it. */
		if(FCDVisitor.contains(node, seed)){
			this.result = true;
			return;
		}
	}
	
	/**
	 * Determines if this ASTNode is a conditional statement.
	 * Conditional statements include: if,do,while,for,switch
	 * @param node
	 * @return
	 */
	public static boolean isConditional(ASTNode node){
		if(node instanceof IfStatement) return true;
		if(node instanceof DoStatement) return true;
		if(node instanceof EnhancedForStatement) return true;
		if(node instanceof ForStatement) return true;
		if(node instanceof SwitchStatement) return true;
		if(node instanceof WhileStatement) return true;
		return false;
	}
	
	/**
	 * Returns true if the seed contains the node statement.
	 * @param node An ASTNode (should have a body containing statements).
	 * @param seed The seed statement
	 * @return
	 */
	public static boolean contains(ASTNode node, ASTNode seed){
		while(!node.getRoot().equals(node)){
			if(node.equals(seed)) return true;
			node = node.getParent();
		}
		
		return false;
	}
	
	/**
	 * A class to find if there is a return statement within the
	 * expression being visited.
	 */
	private class ReturnVisitor extends DependencyVisitor {
		
		/**
		 * If there is a return statement somewhere in the body of the
		 * conditional statement, then this node is a control dependency.
		 */
		public boolean visit(ReturnStatement node){
			this.result = true;
			return false;
		}
	}
}
