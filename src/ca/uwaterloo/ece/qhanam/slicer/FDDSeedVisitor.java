package ca.uwaterloo.ece.qhanam.slicer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * Add any variables or fields that are modified in the seed statement
 * to the list of variables that we need to check for. This class is
 * similar to BDDVisitor (except we are adding aliases instead of 
 * checking for them).
 * 
  * Strategy:
 * 	BDD & FDDSeed - Track assignments, declarations, objects of method call and arguments of method call.
 * 	FDD & BDDSeed - Track all other variable uses
 * 
 * @author qhanam
 */
public class FDDSeedVisitor extends ASTVisitor {
	LinkedList<String> seedVariables;
	List<Slicer.Options> options;
	
	/**
	 * Create a SeedVisitor
	 * @param seedVariables The list that SeedVisitor will fill with the seed variables.
	 */
	public FDDSeedVisitor(LinkedList<String> seedVariables, List<Slicer.Options> options){
		super();
		this.seedVariables = seedVariables;
		this.options = options;
	}
	
	/**
	 * If we are creating a conservative slice, we treat both
	 * objects on which we call methods as well as arguments
	 * for method calls as dependencies. This makes the
	 * assumption that the method will modify the caller
	 * and the arguments.
	 * @param node
	 * @return
	 */
	@Override 
	public boolean visit(MethodInvocation node){
		if(this.options.contains(Slicer.Options.CONSERVATIVE)){
			/* The expression part (eg. 'result.setFast(arg1, arg2)' expression = 'result' */
			Expression expression = node.getExpression();
			this.visitExpression(expression, new NoBindingsMethodVisitor(this.seedVariables));
			
			/* The argument (eg. 'result.setFast(arg1, arg2)' arguments = {'arg1', 'arg2'}. */
			List<Expression> arguments = node.arguments();
			for(Expression argument : arguments){
				this.visitExpression(argument, new NoBindingsAssignmentVisitor(this.seedVariables));
			}
		}
		return false;
	}
	
	/**
	 * This is a data dependency if this is a declaration and the
	 * variable being declared is in the right hand side of the
	 * seed assignment expression.
	 */
	public boolean visit(SingleVariableDeclaration node){
		IBinding binding = node.resolveBinding();
		
		if(binding == null){
			this.seedVariables.add(node.getName().getFullyQualifiedName());		
		}
		else if(binding instanceof IVariableBinding){
			this.seedVariables.add(binding.getKey());
		}
		
		return false;
	}
	
	/**
	 * This is a data dependency if this is a declaration and the
	 * variable being declared is in the right hand side of the
	 * seed assignment expression.
	 */
	public boolean visit(VariableDeclarationFragment node){
		IBinding binding = node.resolveBinding();
		
		if(binding == null){
			this.seedVariables.add(node.getName().getFullyQualifiedName());
		}
		else if(binding instanceof IVariableBinding){
			this.seedVariables.add(binding.getKey());
		}
		
		return false;
	}
	
	/**
	 * This is a data dependency if this is an assignment and the
	 * variable being assigned is in the right hand side of the
	 * seed assignment expression.
	 * 
	 * We only handle cases where the left hand side of the
	 * assignment is a SimpleName, FieldAccess or QualifiedName.
	 */
	public boolean visit(Assignment node){
		Expression lhs = node.getLeftHandSide();
		this.visitExpression(lhs, new NoBindingsAssignmentVisitor(this.seedVariables));
		return false;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(IfStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(DoStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(EnhancedForStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(ForStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			List<Expression> initializers = node.initializers();
			for(Expression initializer : initializers){
				initializer.accept(this);
			}
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}

	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(SwitchStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	

	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(SynchronizedStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * We don't really need anything in particular from this statement,
	 * but since it has an expression and a body, we only want to 
	 * investigate the expression part to determine if it needs to
	 * be in the slice.
	 */
	public boolean visit(WhileStatement node){
		if(this.options.contains(Slicer.Options.CONTROL_EXPRESSIONS_ONLY)){
			/* Visit the expression part. */
			node.getExpression().accept(this);
			/* Don't visit the children. */
			return false;
		}
		else return true;
	}
	
	/**
	 * Vists an expression and checks for aliases.
	 * @param node
	 * @return
	 */
	public void visitExpression(Expression node, ASTVisitor nbv){
		if(node instanceof FieldAccess){
			/* All we really need from this is the variable binding. */
			IBinding binding = ((FieldAccess) node).resolveFieldBinding();
			
			/* Make sure this is a variable.
			 * If we are just analyzing one source file,
			 * we won't have binding info... so do our 
			 * best effort at matching variables. */
			if(binding == null){
				node.accept(nbv);
			}
			else if(binding instanceof IVariableBinding){
				this.seedVariables.add(binding.getKey());
			}
		}
		else if(node instanceof QualifiedName){
			IBinding binding = ((QualifiedName)node).resolveBinding();
			
			if(binding == null){
				node.accept(nbv);
			}
			else if(binding instanceof IVariableBinding){
				this.seedVariables.add(binding.getKey());
			}
		}
		else if(node instanceof SimpleName){
			IBinding binding = ((SimpleName)node).resolveBinding();
			
			if(binding == null){
				node.accept(nbv);
			}
			else if(binding instanceof IVariableBinding){
				this.seedVariables.add(binding.getKey());
			}
		}
	}
	
	/**
	 * A class to find matching fields/variables in expressions when
	 * we don't have field/variable bindings.
	 */
	private class NoBindingsAssignmentVisitor extends ASTVisitor {
		
		/* The list of all possible variables and their aliases at this point in the CFG. */
		private List<String> seedVariables;
		
		/**
		 * Create DataDependencyVisitor
		 * @param 
		 */
		public NoBindingsAssignmentVisitor(List<String> seedVariables){
			super();
			this.seedVariables = seedVariables;
		}
		
		/**
		 * Add this variable to the seed variable list if not already present.
		 */
		public boolean visit(SimpleName node){
			if(!(node.getParent() instanceof MethodInvocation)){
				if(!this.seedVariables.contains(node.getFullyQualifiedName())){
					this.seedVariables.add(node.getFullyQualifiedName());	// Store this variable
					return false; // We no longer need to visit the children.
				}
			}
			return true;
		}
		
		/**
		 * Add this variable to the seed variable list if not already present.
		 */
		public boolean visit(QualifiedName node){
			if(!(node.getParent() instanceof MethodInvocation)){
				if(!this.seedVariables.contains(node.getFullyQualifiedName())){
					this.seedVariables.add(node.getFullyQualifiedName());
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Add this variable to the seed variable list if not already present.
		 */
		public boolean visit(FieldAccess node){
			if(!this.seedVariables.contains(node.getName().toString())){
				this.seedVariables.add(node.getName().toString());
				return false;
			}
			return true;
		}
	}
	
	/**
	 * A class to find matching fields/variables in method expressions when
	 * we don't have field/variable bindings.
	 */
	private class NoBindingsMethodVisitor extends ASTVisitor {
		
		/* The list of all possible variables and their aliases at this point in the CFG. */
		private List<String> seedVariables;
		
		/**
		 * Create DataDependencyVisitor
		 * @param 
		 */
		public NoBindingsMethodVisitor(List<String> seedVariables){
			super();
			this.seedVariables = seedVariables;
		}
		
		/**
		 * Add this variable to the seed variable list if not already present.
		 */
		public boolean visit(SimpleName node){
			if(!this.seedVariables.contains(node.getFullyQualifiedName())){
				this.seedVariables.add(node.getFullyQualifiedName());	// Store this variable
				return false; // We no longer need to visit the children.
			}
			return true;
		}
		
		/**
		 * Add this variable to the seed variable list if not already present.
		 */
		public boolean visit(QualifiedName node){
			if(!this.seedVariables.contains(node.getFullyQualifiedName())){
				this.seedVariables.add(node.getFullyQualifiedName());
				return false;
			}
			return true;
		}
		
		/**
		 * Add this variable to the seed variable list if not already present.
		 */
		public boolean visit(FieldAccess node){
			if(!this.seedVariables.contains(node.getName().toString())){
				this.seedVariables.add(node.getName().toString());
				return false;
			}
			return true;
		}
	}
}
