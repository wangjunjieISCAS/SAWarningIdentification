package ca.uwaterloo.ece.qhanam.slicer;

import java.util.List;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Statement;

public abstract class DependencyVisitor extends ASTVisitor {
	/* We store the result of our analysis here. */
	public boolean result;
	
	/* Change (2014-08-27):
	 * 
	 * Stores a list of dependencies that were found through
	 * this node. These dependencies are statements that are
	 * not handled by a visitor.
	 * 
	 * Example: SwitchCase statements for control dependencies.
	 */
	public List<Statement> associatedDependencies;
	
	/**
	 * Initialize result to false (assume this statement
	 * is not a dependency).
	 */
	public DependencyVisitor(){
		super();
		this.result = false;
		this.associatedDependencies = new LinkedList<Statement>();
	}
}
