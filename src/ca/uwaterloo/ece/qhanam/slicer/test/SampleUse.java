package ca.uwaterloo.ece.qhanam.slicer.test;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.compiler.IProblem;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;

public class SampleUse {

	/**
	 * An example demonstrating how to use the slicing tool for control and 
	 * data dependencies.
	 * 
	 * GC.java {drawString, 2112}
	 * Test2.java {getLaunchConfigurations, 7}
	 * Scrollable.java {computeTrim, 7}
	 * 
	 * @param args No arguments.
	 */
	public static void main(String[] args) throws Exception {
		
		/* *********
		 * Sample run of 1-0.java
		 * *********/
		String path = "test_files/FastTreeMap.java";
		String method = "clone";
		int seedLine = 225;
		
		CompilationUnit cu = SampleUse.getAST(path);
		List<Slicer.Options> options;
		MethodVisitor methodVisitor;
		
		/* *********
		 * Control slice
		 * *********/
		
		System.out.println("CONTROL SLICE ***********");		
		methodVisitor = new MethodVisitor(method, seedLine, Slicer.Direction.BACKWARDS, Slicer.Type.CONTROL, 
				Slicer.Options.OMIT_SEED);
		cu.accept(methodVisitor);
		
		
		/* *********
		 * Data Slice
		 * *********/
		
		System.out.println("DATA SLICE ***********");		
		methodVisitor = new MethodVisitor(method, seedLine, Slicer.Direction.BACKWARDS, Slicer.Type.DATA, 
				Slicer.Options.CONTROL_EXPRESSIONS_ONLY, 
				Slicer.Options.OMIT_SEED, 
				Slicer.Options.CONSERVATIVE);
		cu.accept(methodVisitor);
		
		
	}
	
	/**
	 * Reads the file and generates an AST.
	 * @param path
	 * @return
	 */
	public static CompilationUnit getAST(String path){
		String sourceCode;
		
		try{
			/* We need the source code in a string. */
			sourceCode = SampleUse.getText(new File(path));
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(sourceCode.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		//parser.setProject(null); // Need to do this for bindings
		//parser.setUnitName("/src/path/to/java/file.java"); // Need to do this for bindings
		parser.setResolveBindings(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		
		/* Print any problems the compiler encountered. */
		IProblem[] problems = cu.getProblems();
		if(problems.length > 0) System.out.println("WARNING: Errors while parsing file!");
		for(int i = 0; i < problems.length; i++){
			System.out.println(problems[i].getMessage());
			System.out.println(problems[i].getSourceLineNumber());
			System.out.println(problems[i].getOriginatingFileName());
		}
		
		return cu;
	}
	
	/**
	 * Read the source code into a String.
	 * @param file
	 * @return
	 */
	public static String getText(File file) throws Exception
	{		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String content = "";
		String line = "";

		while((line = reader.readLine()) != null){
			content += line + "\n";
		}
		
		return content;
	}
}
