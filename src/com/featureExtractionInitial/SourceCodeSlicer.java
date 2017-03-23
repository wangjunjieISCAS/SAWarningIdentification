package com.featureExtractionInitial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.comon.BugLocation;
import com.comon.CodeInfo;
import com.comon.Constants;
import com.comon.MethodBodyLocation;
import com.comon.StaticWarning;
import com.comon.StringTool;
import com.featureExtractionInitial.SourceCodeParserExample.TypeFinderVisitor;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;

public class SourceCodeSlicer {
	public SourceCodeSlicer() {
		
	}
	
	public ArrayList<CodeInfo> analyzeMethod( MethodDeclaration method, String methodName, Integer seedLine ) {
		ArrayList<CodeInfo> slicesCodeList = new ArrayList<CodeInfo>();
		
		/* Check that we are analyzing the correct method. */
		//System.out.println( method.getName() );
		if ( method == null )
			return slicesCodeList;
		if( method.getName().toString().equals( methodName )){
			System.out.println("Generating intra-procedural slice...");
			System.out.flush();
			
			Slicer slicer = new Slicer( Constants.SLICER_DIRECTION, Constants.SLICER_TYPE, Constants.SLICER_OPTIONS );
			List<ASTNode> statements;
			try{
				statements = slicer.sliceMethod( method , seedLine);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				return null;
			}
			
			//如果slicer出现问题的话，只用当前的代码
			if ( statements == null )
				return slicesCodeList;
			/* Print slice statements. */
			System.out.println("\nNodes in slice:" + statements.size() );
			for(ASTNode node : statements){	
				CodeInfo codeInfo = new CodeInfo ( Slicer.getLineNumber( node), node );
				slicesCodeList.add( codeInfo );
				
				//System.out.print(Slicer.getLineNumber(node) + ": " + node.toString());
			}
			
			System.out.println("Finished generating intra-procedural slice.");	
			System.out.flush();
		}
		
		return slicesCodeList;
	}
	
	public HashMap<String, MethodBodyLocation> obtainMethodInfo ( String fileName ){
		//假设一个warning中，标记出来的位置都是位于一个file的
		//String fileName = warning.getBugLocationList().get(0).getClassName();
		
		String content = this.readJavaFile(fileName);
		ASTParser parser = ASTParser.newParser( AST.JLS4 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource( content.toCharArray() );
		CompilationUnit result = (CompilationUnit) parser.createAST( null );
		
		List types = result.types();
		HashMap<String, MethodBodyLocation> methodNameMap = new HashMap<String, MethodBodyLocation>();
		//dup里面存储的是所有相同名字函数的名字
		Set<String> methodNameDup = new HashSet<String>();
		
		Slicer slicer = new Slicer( Constants.SLICER_DIRECTION, Constants.SLICER_TYPE, Constants.SLICER_OPTIONS );
		int index = 1;
		// 取得类型声明
		if ( types.size() > 0 ){
			TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

			MethodDeclaration methodDec[] = typeDec.getMethods();
			for (MethodDeclaration method : methodDec) {
				String methodName = method.getName().toString();
				//System.out.println( "-----------------------------------------" + methodName );
				int lineNumber = slicer.getLineNumber( method );
				MethodBodyLocation methodInfo = new MethodBodyLocation ( method, lineNumber );
				
				if ( methodNameDup.contains( methodName ) ){
					if ( methodNameMap.containsKey( methodName )){
						MethodBodyLocation methodInfoTemp = new MethodBodyLocation ( methodNameMap.get( methodName ).getMethod(), methodNameMap.get( methodName ).getStartLine() );
						methodNameMap.remove( methodName );
					
						methodNameMap.put( methodName + "-" + index, methodInfoTemp );
						index++;
					}
					methodNameMap.put( methodName + "-" + index, methodInfo);
					index++;
				}else{
					methodNameMap.put( methodName, methodInfo );
				}				
				methodNameDup.add( methodName );
				//对于类的构造函数，这里methodName得到的是函数的名字，也就是和类名相同（例如ByteVector）；而在warning文件中，得到的是<init>，所以需要进行转化；
				//而且对于存在多个构造函数的情况，直接put会造成覆盖，需要存在每个构造函数的起止未知，以便后面进行精确get
				//除了构造函数，其他函数也会存在这种情况
				//System.out.println ( "================================================" + methodName );
			}	
		}		
		
		return methodNameMap;
	}
	
	//和
	public HashMap<String, MethodBodyLocation> obtainMethodInfoSimplify ( String fileName ){
		//假设一个warning中，标记出来的位置都是位于一个file的
		//String fileName = warning.getBugLocationList().get(0).getClassName();
		
		String content = this.readJavaFile(fileName);
		ASTParser parser = ASTParser.newParser( AST.JLS4 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource( content.toCharArray() );
		CompilationUnit result = (CompilationUnit) parser.createAST( null );
		
		List types = result.types();
		HashMap<String, MethodBodyLocation> methodNameMap = new HashMap<String, MethodBodyLocation>();
		//dup里面存储的是所有相同名字函数的名字
		Set<String> methodNameDup = new HashSet<String>();
		
		Slicer slicer = new Slicer( Constants.SLICER_DIRECTION, Constants.SLICER_TYPE, Constants.SLICER_OPTIONS );
		int index = 1;
		// 取得类型声明
		if ( types.size() > 0 ){
			TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

			MethodDeclaration methodDec[] = typeDec.getMethods();
			for (MethodDeclaration method : methodDec) {
				String methodName = method.getName().toString();
				//System.out.println( "-----------------------------------------" + methodName );
				int lineNumber = slicer.getLineNumber( method );
				MethodBodyLocation methodInfo = new MethodBodyLocation ( method, lineNumber );
				
				if ( methodNameDup.contains( methodName ) ){
					if ( methodNameMap.containsKey( methodName )){
						MethodBodyLocation methodInfoTemp = new MethodBodyLocation ( methodNameMap.get( methodName ).getMethod(), methodNameMap.get( methodName ).getStartLine() );
						methodNameMap.remove( methodName );
					
						methodNameMap.put( methodName + "-" + index, methodInfoTemp );
						index++;
					}
					methodNameMap.put( methodName + "-" + index, methodInfo);
					index++;
				}else{
					methodNameMap.put( methodName, methodInfo );
				}				
				methodNameDup.add( methodName );
				//对于类的构造函数，这里methodName得到的是函数的名字，也就是和类名相同（例如ByteVector）；而在warning文件中，得到的是<init>，所以需要进行转化；
				//而且对于存在多个构造函数的情况，直接put会造成覆盖，需要存在每个构造函数的起止未知，以便后面进行精确get
				//除了构造函数，其他函数也会存在这种情况
				//System.out.println ( "================================================" + methodName );
			}	
		}		
		
		return methodNameMap;
	}
	
	public ArrayList<CodeInfo> obtainWarningStatementSlices ( StaticWarning warning, String fileName  ){
		HashMap<String, MethodBodyLocation> methodNameMap = this.obtainMethodInfo(fileName );
		
		ArrayList<CodeInfo> slicesCodeListMethod = new ArrayList<CodeInfo>();
		ArrayList<CodeInfo> slicesCodeListOthers = new ArrayList<CodeInfo>();
		
		for ( int i = 0; i < warning.getBugLocationList().size(); i++ ){
			BugLocation bugLoc = warning.getBugLocationList().get(i);
			
			//没有定位到method，可能是field。type，class这种，值保留seed line，不进行slicer
			if ( bugLoc.getRelatedMethodName().equals( "")){
				for ( int j = 0;  j < bugLoc.getCodeInfoList().size() ; j++ ){
					CodeInfo codeInfo = new CodeInfo ( bugLoc.getStartLine()+j, null );
					slicesCodeListOthers.add( codeInfo );			
				}
			}
			else{
				for ( int j = 0;  j < bugLoc.getCodeInfoList().size(); j++ ){
					Integer seedLine = bugLoc.getStartLine() + j;
					String methodName = bugLoc.getRelatedMethodName();
					
					String className = StringTool.obtainClassNameShort( fileName );
					if ( methodName.equals( "<init>"))
						methodName = className;
					MethodDeclaration method = this.obtainMethodDeclaration(methodNameMap, methodName, seedLine);
					if ( method == null )
						continue;
					
					System.out.println( "method to be sliced: " + fileName + "\n" + method + "\n" + seedLine + "\n" + bugLoc.getCodeInfoList().get( j ));
					//System.out.println ( "--------------------------------------------" + method.getName().toString() );
					//目前构造函数中的问题解析不出来
					ArrayList<CodeInfo> codeInfoList = this.analyzeMethod ( method, method.getName().toString(), seedLine );
					//对于没有切片出来的，只保留该行语句
					if ( codeInfoList == null ){
						CodeInfo codeInfo = new CodeInfo ( seedLine, null );
						codeInfoList = new ArrayList<CodeInfo>();
						codeInfoList.add( codeInfo );
					}
						
					slicesCodeListMethod.addAll( codeInfoList );
				}
			}
		}
		
		//优先用从method中得到的
		ArrayList<CodeInfo> refinedSlicesCodeList = new ArrayList<CodeInfo>();
		Set<Integer> codeLineSet = new HashSet<Integer>();
		for ( int i = 0; i < slicesCodeListMethod.size() && i < Constants.MAX_SLICES; i++ ){
			CodeInfo temp = slicesCodeListMethod.get( i );
			if ( codeLineSet.contains( temp.getCodeLine() ))
				continue;
			
			CodeInfo codeInfo = new CodeInfo ( temp.getCodeLine(), temp.getCodeContent() );
			refinedSlicesCodeList.add( codeInfo );
			codeLineSet.add( codeInfo.getCodeLine() );
		}
		for ( int i = 0; i < slicesCodeListOthers.size() && refinedSlicesCodeList.size() <  Constants.MAX_SLICES; i++ ){
			CodeInfo temp = slicesCodeListOthers.get( i );
			if ( codeLineSet.contains( temp.getCodeLine() ))
				continue;
			
			CodeInfo codeInfo = new CodeInfo ( temp.getCodeLine(), temp.getCodeContent() );
			refinedSlicesCodeList.add( codeInfo );
			codeLineSet.add( codeInfo.getCodeLine() );
		}
		
		/*
		ArrayList<CodeInfo> refinedSlicesCodeList = new ArrayList<CodeInfo> (new LinkedHashSet<CodeInfo>(slicesCodeList));
		if ( refinedSlicesCodeList.size() > Constants.MAX_SLICES )
			refinedSlicesCodeList.subList( Constants.MAX_SLICES , refinedSlicesCodeList.size() ).clear();
		*/
		return refinedSlicesCodeList;
	}

	//针对函数名字是构造函数的情况
	public MethodDeclaration obtainMethodDeclaration ( HashMap<String, MethodBodyLocation>  methodNameMap, String methodName, int seedLine ){
		MethodDeclaration method = null;
		//找到比seedLine小，但是最大的method
		int selectedLine  = 0;
		String selectedMethod = "";
		for ( String methodNameTemp: methodNameMap.keySet() ){
			MethodBodyLocation methodInfo = methodNameMap.get( methodNameTemp);
			
			if ( methodNameTemp.contains( "-")){
				int index = methodNameTemp.indexOf( "-");
				String methodNameTrue = methodNameTemp.substring( 0, index );
				
				if ( methodName.equals( methodNameTrue) && methodInfo.getStartLine() <= seedLine && methodInfo.getStartLine() > selectedLine ){
					selectedLine = methodInfo.getStartLine();
					selectedMethod = methodNameTemp;
				}
			}
			else{
				if ( methodNameTemp.equals( methodName )){
					selectedMethod = methodNameTemp;
				}
			}
		}
		
		if ( !selectedMethod.equals( "")){
			method = methodNameMap.get( selectedMethod ).getMethod();
			System.out.println( "======================================" +selectedMethod );
		}
		
		/*
		if ( methodName.equals( "<init>")){
			for ( String methodNameTemp: methodNameMap.keySet() ){
				if ( methodNameTemp.contains( "-")){
					MethodBodyLocation methodInfo = methodNameMap.get( methodNameTemp);
					if ( methodInfo.getStartLine() <= seedLine && methodInfo.getStartLine() > selectedLine ){
						selectedLine =  methodInfo.getStartLine();
						selectedMethod = methodNameTemp;
					}
				}
			}
			
			if ( selectedLine != 0 )
				method = methodNameMap.get( selectedMethod ).getMethod();
		}
		else{
			//System.out.println ( methodNameMap.keySet().toString() );
			if ( methodNameMap.containsKey( methodName ))
				method = methodNameMap.get( methodName ).getMethod();
		}
		*/
		return method;
	}
	
	public String readJavaFile( String filename)  {
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
		
		//String javaFile = "test_files/Test2.java";
		//slicer.obtainMethodInfo( javaFile );
	}
}
