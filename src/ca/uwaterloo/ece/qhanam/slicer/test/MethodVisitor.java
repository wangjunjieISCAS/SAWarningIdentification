package ca.uwaterloo.ece.qhanam.slicer.test;

import java.util.List;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;

/**
 * Visits the method components of a java file.
 * @author qhanam
 *
 */
public class MethodVisitor extends ASTVisitor
{	
	private String methodName;
	private int seedLine;
	private Slicer.Direction direction;
	private Slicer.Type type;
	private Slicer.Options[] options;
	
	public List<ASTNode> slice;
	
	public MethodVisitor(String methodName, int seedLine, Slicer.Direction direction, Slicer.Type type, Slicer.Options... options){
		this.methodName = methodName;
		this.seedLine = seedLine;
		this.direction = direction;
		this.type = type;
		this.options = options;
		this.slice = new LinkedList<ASTNode>();
	}
	
	@Override
	public boolean visit(CompilationUnit node){
		return true;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		/* If this is the method we want to analyze,
		 * call the analysis. */
		if(node.getName().toString().equals(this.methodName)){
			Slicer slicer = new Slicer(direction, type, options);
			List<ASTNode> statements;
			try{ 
				statements = slicer.sliceMethod(node, seedLine);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				return false;
			}
			if(statements == null) return false;
			
			this.slice = statements;
			
			System.out.println("Slice Results:");
			for(ASTNode statement : statements){
				System.out.println("Node Type: " + statement.getNodeType());
				int line = Slicer.getLineNumber(statement);
				if(statement instanceof Statement)
					System.out.println(line + ": " + statement.toString());		
				if(statement instanceof MethodDeclaration)
					System.out.println(line + ": " + ((MethodDeclaration)statement).getName().toString() + "\n");
			}
		}
		
		return false;
	}
}
