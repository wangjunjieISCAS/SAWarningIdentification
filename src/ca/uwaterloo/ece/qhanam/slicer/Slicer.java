package ca.uwaterloo.ece.qhanam.slicer;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Stack;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.*;


import edu.cmu.cs.crystal.cfg.eclipse.EclipseCFG;
import edu.cmu.cs.crystal.cfg.ICFGNode;
import edu.cmu.cs.crystal.cfg.ICFGEdge;


/**
 * Creates a program slice based on data or control dependencies.
 * 
 * Default Options (see Slicer.Options enumeration for a description):
 * 	CONTROL_BODY
 * 	RESTRICTIVE
 * 	INCLUDE_SEED
 * 
 * @author qhanam
 *
 */
public class Slicer
{
	private Type type;
	private Direction direction;
	private List<Options> options;
	private LinkedList<ASTNode> statements;
	
	/**
	 * Creates a slicer instance. See ca.uwaterloo.ece.qhanam.slicer.plugin.IntraProcSlicer for sample usage.
	 * @param direction FORWARDS, BACKWARDS or BOTH
	 * @param type The type of dependencies to track (CONTROL or DATA). Data dependencies subsume control dependencies.
	 */
	public Slicer(Direction direction, Type type, Slicer.Options... options) { 
		this.statements = new LinkedList<ASTNode>();
		this.direction = direction;
		this.type = type;
		this.options = new LinkedList<Options> ();
		for(int i = 0; i < options.length; i++){
			this.options.add(options[i]);
		}
	}

	/**
	 * Slices the method on data and/or control dependencies.
	 * @param d The method to slice.
	 * @param seedLine The line number of the seed statement.
	 * @return
	 */
	public List<ASTNode> sliceMethod(MethodDeclaration d, int seedLine) throws Exception{
		Hashtable<Integer,ASTNode> statementPairs = new Hashtable<Integer,ASTNode>();
		LinkedList<String> seedVariables = null;
		
		/* Build the CFG from the method declaration. */
		EclipseCFG cfg = new EclipseCFG(d);
		
		/* Once we have the start node, we can traverse the graph by finding
		 * the output edges and traversing them. */
		ICFGNode<ASTNode> cfgNode = null;
		cfgNode = cfg.getStartNode();
		
		/* Find the seed node in the CFG. */
		cfgNode = Slicer.findSeed(cfgNode, seedLine);
		if(cfgNode == null) return null;
		
		/* Get the list of variables in the seed node. */
		if(this.type == Slicer.Type.DATA) seedVariables = this.getSeedVariables(cfgNode);
		
		/* Since this is the seed, we automatically add it to the slice. (needed here for 
		 * right-hand assignment slicing. */
		Statement seed = getStatement((ASTNode) cfgNode.getASTNode());
		if(!this.options.contains(Slicer.Options.OMIT_SEED))
			statementPairs.put(new Integer(seed.getStartPosition()), seed);
		
		/* Build the control dependency slice. */
		this.statements = computeSlice(cfgNode, seedVariables, statementPairs, seedLine);

		return this.statements;
	}
	
	/**
	 * Computes the slice.
	 * @param cfgNode The node to start with.
	 * @return
	 */
	private LinkedList<ASTNode> computeSlice(ICFGNode<ASTNode> cfgNode, LinkedList<String> seedVariables, 
												Hashtable<Integer, ASTNode> statementPairs, int seedLine) throws Exception{
		LinkedList<ASTNode> statements = new LinkedList<ASTNode>();
		HashSet<ICFGNode<ASTNode>> visited = new HashSet<ICFGNode<ASTNode>>();
		Stack<ICFGNode<ASTNode>> stack = new Stack<ICFGNode<ASTNode>>();
		ASTNode seed = cfgNode.getASTNode();
		
		/* Start by checking the method parameters for data dependencies. */
		if(this.type == Slicer.Type.DATA && this.direction == Slicer.Direction.BACKWARDS){
			MethodDeclaration method = Slicer.getMethod(cfgNode.getASTNode());
			if(method != null){
				List<SingleVariableDeclaration> parameters = method.parameters();
				for(SingleVariableDeclaration parameter : parameters){
					if(seedVariables.contains(parameter.getName().getFullyQualifiedName())){
						statementPairs.put(new Integer(method.getStartPosition()), method);
					}
				}
			}
		}
		
		/* Build the control dependency slice. */
		stack.add(cfgNode);
		
		/* Breadth first search. Add each statement to the list when found. */
		while(!stack.empty()){
			Set<ICFGEdge<ASTNode>> neighbours;
			
			cfgNode = stack.pop();
			ASTNode astNode = (ASTNode) cfgNode.getASTNode();
			Statement statement = getStatement(astNode);
			
			if(astNode instanceof MethodRef){
				System.out.println("MethodRef found: " + astNode);
			}
			
			/* Add the statement to the slice if:
			 * 	1. It isn't in yet.
			 * 	2. We are doing a data dep analysis and it is a data dependency
			 * 	3. We are doing a control dep analysis and it is a control dependency */
			if(statement != null && !(statement instanceof Block) && !statementPairs.containsKey(new Integer(statement.getStartPosition()))) 
			{
				if(this.type == Slicer.Type.CONTROL){
					if(!this.options.contains(Slicer.Options.OMIT_SEED) || Slicer.getLineNumber(statement) != seedLine){
						/* TODO: We might need to specifically handle SwitchCase statements, since some of them aren't in the CFG. 
						 * 		 Because of Data Dependencies, we can't change this in edu.cmu.cs.crystal.cfg.eclipse.EclipseCFG. */
						DependencyVisitor cdv;
						if(this.direction == Slicer.Direction.BACKWARDS) {
							cdv = new BCDVisitor(this.options, seed);
							statement.accept(cdv);
						}
						else if(this.direction == Slicer.Direction.FORWARDS) {
							FCDVisitor fcdv = new FCDVisitor(this.options, Slicer.getStatement(seed));
							fcdv.isControlDependency(statement);
							cdv = fcdv;
						}
						else throw new Exception("Slicer only supports FORWARDS or BACKWARDS directions.");
						
						/* TODO: We should store a list of statements in the visitor so we don't have to hack SwitchCase statements. */
						if(cdv.result) statementPairs.put(new Integer(statement.getStartPosition()), statement);
						for(Statement s : cdv.associatedDependencies){
							statementPairs.put(new Integer(s.getStartPosition()), s);
						}
					}
				}
				else if(this.type == Slicer.Type.DATA){
					if(!this.options.contains(Slicer.Options.OMIT_SEED) || Slicer.getLineNumber(statement) != seedLine){
						DependencyVisitor ddv;
						if(this.direction == Slicer.Direction.BACKWARDS) ddv = new BDDVisitor(seedVariables, this.options);
						else if(this.direction == Slicer.Direction.FORWARDS) ddv = new FDDVisitor(seedVariables, this.options);
						else throw new Exception("Slicer only supports FORWARDS or BACKWARDS directions.");
						statement.accept(ddv);
						if(ddv.result) statementPairs.put(new Integer(statement.getStartPosition()), statement);
					}
				}
			}
			
			if(this.direction == Slicer.Direction.FORWARDS) neighbours = (Set<ICFGEdge<ASTNode>>) cfgNode.getOutputs();
			else if(this.direction == Slicer.Direction.BACKWARDS) neighbours = (Set<ICFGEdge<ASTNode>>) cfgNode.getInputs();
			else throw new Exception("Slicer only supports FORWARDS or BACKWARDS directions.");
			
			for(ICFGEdge<ASTNode> edge : neighbours){
				if(this.direction == Slicer.Direction.FORWARDS && !visited.contains(edge.getSink())){
					stack.push(edge.getSink());
					visited.add(edge.getSink());
				}
				else if(this.direction == Slicer.Direction.BACKWARDS && !visited.contains(edge.getSource())){
					stack.push(edge.getSource());
					visited.add(edge.getSource());
				}
			}
		}
		
		/* Add the statements to the list. */
		for(ASTNode statement : statementPairs.values()){
			statements.add(statement);
		}
		
		return statements;
	}
	
	/**
	 * Returns a list of variables associated with the seed statement.
	 * 
	 * We're only interested in the variables on the right hand
	 * side of an assignment expression...
	 * 
	 * @param cfgNode
	 * @return
	 */
	public LinkedList<String> getSeedVariables(ICFGNode<ASTNode> cfgNode) throws Exception{
		LinkedList<String> seedVariables = new LinkedList<String>();
		
		/* Extract the variables from the seed statement. */
		Statement statement = Slicer.getStatement(cfgNode.getASTNode());
		ASTVisitor seedVisitor;
		if(this.direction == Slicer.Direction.BACKWARDS) seedVisitor = new BDDSeedVisitor(seedVariables, this.options);
		else if(this.direction == Slicer.Direction.FORWARDS) seedVisitor = new FDDSeedVisitor(seedVariables, this.options);
		else throw new Exception("Slicer only supports FORWARDS or BACKWARDS directions.");
		statement.accept(seedVisitor);
		
		return seedVariables;
	}
	
	/** 
	 * Do a DFS on the CFG for the seed node. The resulting CFGNode
	 * is the starting point for our slice.
	 * 
	 * We do a depth-first search so we can see the proper CFGNode 
	 * order when debugging.
	 */
	public static ICFGNode<ASTNode> findSeed(ICFGNode<ASTNode> cfgNode, int seedLine){
		Stack<ICFGNode<ASTNode>> stack = new Stack<ICFGNode<ASTNode>>();
		HashSet<ICFGNode<ASTNode>> visited = new HashSet<ICFGNode<ASTNode>>();
		
		stack.add(cfgNode);
		int line = -1;
		while(!stack.empty()){
			cfgNode = stack.pop();
			ASTNode astNode = (ASTNode) cfgNode.getASTNode();
			
			/* Check if this is the seed statement. */
			Statement statement = Slicer.getStatement(astNode);
			if(statement != null){
				line = Slicer.getLineNumber(statement);
				if(line == seedLine) break;
			}
			
			/* Visit all the neighbors that haven't been visited (for DFS). */
			Set<ICFGEdge<ASTNode>> neighbours = (Set<ICFGEdge<ASTNode>>) cfgNode.getOutputs();
			for(ICFGEdge<ASTNode> edge : neighbours){	
				if(!visited.contains(edge.getSink())){
					stack.push(edge.getSink());
					visited.add(edge.getSink());
				}
			}
		}
		
		/* Check that we actually found a seed statement. */
		if(cfgNode == null || line != seedLine){
			System.out.println("Seed statement not found.");
			return null;
		}
		
		return cfgNode;
	}
	
	/**
	 * Prints an ASTNode according to it's type.
	 */
	public static void printNode(ASTNode node){
		int line = Slicer.getLineNumber(node);
		
		switch(node.getNodeType()){
			case ASTNode.VARIABLE_DECLARATION_STATEMENT:
				System.out.println("Line " +  line + ": " + node.toString());
				List<VariableDeclarationFragment> fragments = ((VariableDeclarationStatement) node).fragments();
				for(VariableDeclarationFragment fragment : fragments){
					System.out.println("\t" + fragment);
					System.out.println("\tVariable declared = " + fragment.getName());
				}
				break;
			default:
				System.out.println("Line " +  line + ": " + node.toString());
		}
	}
	
	/**
	 * Finds the method reference for this ASTNode.
	 * @param astNode The node to start the search at.
	 * @return
	 */
	public static MethodDeclaration getMethod(ASTNode astNode){
		/* Visit parents until we get to the method reference. */
		if(astNode == null) return null;
		while(!(astNode instanceof MethodDeclaration)){
			if(astNode.getParent() == null) return null;
			astNode = astNode.getParent();
		}
		return (MethodDeclaration) astNode;
	}
	
	/**
	 * Finds the statement that contains the ASTNode. Useful
	 * for finding the statement after we've found a variable.
	 */
	public static Statement getStatement(ASTNode statement){
		/* Visit parents until we get to a statement. */
		if(statement == null) return null;
		while(!(statement instanceof Statement)){
			if(statement.getParent() == null) return null;
			statement = statement.getParent();
		}
		return (Statement) statement;
	}
	
	/**
	 * Finds the compilation unit and retrieves the line number
	 * of the given node.
	 * @param node
	 * @return
	 */
	public static int getLineNumber(ASTNode node){
		/* This gives us the starting character position. We need to map
		 * this into the line number using:
		 * int org.eclipse.jdt.core.dom.CompilationUnit.getLineNumber(int position)
		 */
		if(node == null) return -1;
		int characterPosition = node.getStartPosition();
		int line = -1;
		
		/* Search for the compilation unit. */
		ASTNode current = node;
		do{
			current = current.getParent();
		} while(current.getParent() != null && node.getNodeType() != ASTNode.COMPILATION_UNIT);
		
		/* Have we found a compilation unit? */
		if(ASTNode.COMPILATION_UNIT == current.getNodeType()){
			CompilationUnit compUnit = (CompilationUnit) current;
			
			/* Now print the line number. */
			line = compUnit.getLineNumber(characterPosition);
		}
		return line;
	}
	
	public enum Direction {
	    BACKWARDS, FORWARDS
	}
	
	public enum Type{
		CONTROL, DATA
	}
	
	public enum Options { 
		/**
		 * For control expressions (eg. if, for, while statements), include the body for data dependencies
		 */
		CONTROL_BODY,
		
		/**
		 * For control expressions (eg. if, for, while statements), don't look in the body for data dependencies
		 */
		CONTROL_EXPRESSIONS_ONLY,
		
		/**
		 * Leave the seed statement out of the slice.
		 */
		INCLUDE_SEED,
		
		/**
		 * Leave the seed statement out of the slice.
		 */
		OMIT_SEED,
		
		/**
		 * Perform a conservative slice (ie. include method calls in data dependencies)
		 */
		CONSERVATIVE,
		
		/**
		 * Perform a restrictive slice (ie. do not include method calls in data dependencies)
		 */
		RESTRICTIVE,
		
		/**
		 * Treat switch statements as if/else statements.
		 * Example:
		 * 1	switch(n){
		 * 2		case 1:
		 * 3			break;
		 * 4		case 2:
		 * 5		case 3:
		 * 6			i++;
		 * 7	 	case 4:
		 * 8	}
		 * Seed statement = line 6.
		 * 
		 * With this option, control dependencies = lines [1,2,4,5].
		 * Without this option, control dependencies = lines [1,4,5].
		 */
		SWITCH_AS_IF
	}
}
