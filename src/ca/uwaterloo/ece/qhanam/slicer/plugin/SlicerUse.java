package ca.uwaterloo.ece.qhanam.slicer.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;

public class SlicerUse {
	
	private static final int SEED_LINE = 209;        //11;
	private static final String METHOD = "rewrite";      //"getLaunchConfigurations";
	private static final Slicer.Direction DIRECTION = Slicer.Direction.BACKWARDS;
	private static final Slicer.Type TYPE = Slicer.Type.DATA;
	private Slicer.Options[] options;
	
	HashMap<String, Integer> ASTNodeTypeMap = new HashMap<String, Integer>();
	HashMap<Integer, String> ASTNodeTypeMapRev = new HashMap<Integer, String>();
	
	public SlicerUse() { 
		this.options = new Slicer.Options[]{Slicer.Options.CONTROL_EXPRESSIONS_ONLY};
		
	}
	
	public void analyzeMethod(MethodDeclaration d) {
		/* Check that we are analyzing the correct method. */
		System.out.println( d.getName() );
		if(d.getName().toString().equals(METHOD)){
			System.out.println("Generating intra-procedural slice...");
			//System.out.flush();
			
			Slicer slicer = new Slicer(DIRECTION, TYPE, this.options);
			List<ASTNode> statements;
			try{
				statements = slicer.sliceMethod(d, SEED_LINE);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				return;
			}
			
			/* Print slice statements. */
			System.out.println("\nNodes in slice:" + statements.size() );
			
			for(ASTNode node : statements){
				System.out.print (Slicer.getLineNumber(node) + ": " + node.toString() + "   "  );
				
				System.out.println ( "expression type arguments: " + node.getProperty( "parameters") ) ;
				System.out.println ( "expression expression: " + node.getProperty( "expression") ) ;
				System.out.println ( "expression name: " + node.getProperty( "name") ) ;
			}
			
			System.out.println("Finished generating intra-procedural slice.");	
			//System.out.flush();
		}
		
	}
	
	public void obtainMethodInfo ( String fileName ){
		String content = this.readJavaFile(fileName);
		ASTParser parser = ASTParser.newParser( AST.JLS4 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource( content.toCharArray() );
		parser.setResolveBindings( true );
		CompilationUnit result = (CompilationUnit) parser.createAST( null );
		
		List types = result.types();
		// 取得类型声明
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
		
		MethodDeclaration methodDec[] = typeDec.getMethods();
		
		for (MethodDeclaration method : methodDec) {
			//System.out.println ( method.getName() );
			//System.out.println ( method );
			
			analyzeMethod ( method );
			
			//System.out.println( method );
		}			
	}
	

	private static String readJavaFile(String filename)  {
		FileInputStream reader;
		try {
			reader = new FileInputStream ( new File(filename) );
			byte[] b = new byte[reader.available()];
			reader.read( b, 0, reader.available());
			String javaCode = new String (b);
			return javaCode;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static void main ( String args[] ){
		SlicerUse slicer = new SlicerUse();
		
		String javaFile = "data/test3.java";
		//String javaFile = "data/Test2.java";
		slicer.obtainMethodInfo( javaFile );
	}
}
