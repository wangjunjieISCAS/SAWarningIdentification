package ca.uwaterloo.ece.qhanam.slicer;

import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jdt.core.dom.*;

/**
 * Checks whether this ASTNode (Statement) contains a data dependency from
 * the given list.
 * @author qhanam
 */
public class BCDVisitor extends DependencyVisitor {
	
	/* The slicer options. */
	private List<Slicer.Options> options;
	
	/* The seed statement. */
	private ASTNode seed;
	
	/**
	 * Create DataDependencyVisitor
	 * @param 
	 */
	public BCDVisitor(List<Slicer.Options> options, ASTNode seed){
		super();
		this.options = options;
		this.seed = seed;
	}

	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 */
	public boolean visit(SwitchCase node){
		if(this.options.contains(Slicer.Options.SWITCH_AS_IF)) {
			return true;
		}
		else{
            this.isControlDependency(node.getParent());
            return false;
		}
	}
	
	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 */
	public boolean visit(IfStatement node){
		this.isControlDependency(node);
		return false;
	}
	
	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 */
	public boolean visit(WhileStatement node){
		this.isControlDependency(node);
		return false;
	}
	
	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 */
	public boolean visit(DoStatement node){
		this.isControlDependency(node);
		return false;
	}
	
	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 */
	public boolean visit(EnhancedForStatement node){
		this.isControlDependency(node);
		return false;
	}
	
	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 */
	public boolean visit(ForStatement node){
		this.isControlDependency(node);
		return false;
	}
	
	/**
	 * Check to see if the seed statement is contained within the body of 
	 * this conditional statement.
	 * 
	 * Because this is a Switch statement, we also have to investigate the 
	 * case statements. We treat them like an if/else statement block.
	 */
	public boolean visit(SwitchStatement node){
		this.isControlDependency(node);

		/* Get SwitchCase statement dependencies. */
		if(this.options.contains(Slicer.Options.SWITCH_AS_IF)) {
            /* Strategy: Use a list to keep track of all SwitchCase statements. When we
             * get to the seed statement, flush the list to the related statement list 
             * and return. If we get to a return statement, flush the list to the related 
             * statement list and continue.
             */
            Queue<Statement> cache = new LinkedList<Statement>();
            List<Statement> ordered = node.statements();
            for(Statement s : ordered){
                if(s instanceof SwitchCase){
                    /* This could be a control dependency. Add it to the cache. */
                    cache.add(s);
                }
                else if(s.equals(this.seed) || s instanceof ReturnStatement){
                    /* Flush the cache. */
                    while(cache.peek() != null){
                        this.associatedDependencies.add(cache.remove());
                    }
                    /* We are done if this is the seed statement. */
                    if(s.equals(this.seed)) return false;
                }
                System.out.println(s.toString());
            }
		}
        return false;
	}
	
	/**
	 * The seed statement is control dependent on another statement
	 * if that statement affects the whether or not the seed
	 * statement is executed. We therefore look at conditional
	 * statements that the seed is enclosed in as well as 
	 * conditional statements that contain return statements.
	 * 
	 * @param node The conditional statement.
	 * @return
	 */
	public void isControlDependency(ASTNode node){
		ReturnVisitor rv = new ReturnVisitor();
		
		/* If this conditional statement contains the seed statement, then
		 * it is a control dependency */
		if(BCDVisitor.contains(node, this.seed)){
			this.result = true;
			return;
		}

		/* If this conditional statement contains a return statement in its
		 * body, then it is a control dependency.
		 */
		node.accept(rv);
		this.result = rv.result;
	}
	
	/**
	 * Returns true if the node contains the seed statement.
	 * @param node An ASTNode (should have a body containing statements).
	 * @param seed The seed statement
	 * @return
	 */
	public static boolean contains(ASTNode node, ASTNode seed){
		int seedPosition = seed.getStartPosition();
		int start = node.getStartPosition();
		int end = start + node.getLength();
		
		/* If this conditional statement contains the seed statement, then
		 * it is a control dependency */
		if(seedPosition >= start & seedPosition <= end)
			return true;
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
