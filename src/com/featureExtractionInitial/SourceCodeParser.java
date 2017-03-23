package com.featureExtractionInitial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.comon.CodeInfo;
import com.comon.Constants;

import ca.uwaterloo.ece.qhanam.slicer.Slicer;


public class SourceCodeParser {
	
	public HashMap<Integer, HashMap<String, String>> obtainMethodDetails( String fileName, ArrayList<CodeInfo> slicesCodeList ) {
		HashMap<Integer, HashMap<String, String>> codeFeature = new HashMap<Integer, HashMap<String, String>>();    //外层的key 对应在slicesCodeList中的编号
		
		SourceCodeSlicer slicer = new SourceCodeSlicer();
		String fileContent = slicer.readJavaFile( fileName );

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);

		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);

		int index = fileName.lastIndexOf( "//");
		String unitName = fileName.substring( index+2 );
		parser.setUnitName(unitName);

		String[] sources = {fileName.substring( 0,index) };
		String[] classpath = { Constants.JRE_LOCATION };

		parser.setEnvironment( classpath, sources, new String[] { "UTF-8" }, true);
		parser.setSource( fileContent.toCharArray());

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		if (cu.getAST().hasBindingsRecovery()) {
			System.out.println("Binding activated.");
		}
		//System.out.println ( "++++++++++++++++++++++++++++++++++++++++ " + cu.toString() );
		//针对tomcat项目特殊情况的处理
		/*
		if ( cu.toString().contains( "public class AprLifecycleListener")){
			return codeFeature;
		}
		*/
		
		cu.accept( new ASTVisitor ( ) {
			//F1_to_F4
			public boolean visit ( MethodInvocation node ){
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				System.out.println( "MethodInvocation: " + node.toString() );
				HashMap<String, String> featureValue = new HashMap<String, String>();
				
				String F1_callName = node.getName().toString();
				//String F2_callClass = node.getExpression().toString();
				
				List<Expression> arguments = node.arguments();
				String F3_para = "";
				for ( int i = 0; i < arguments.size() ; i++ ){
					ITypeBinding bind = arguments.get( i ).resolveTypeBinding();
					String temp = "";
					if ( bind != null ){
						temp = bind.getName();
					}						
					else{
						temp =  arguments.get(i).toString();
					}
					if ( temp.trim().equals( ""))
						continue;
					if ( i == 0 )
						F3_para += temp;
					else
						F3_para += "+==+" + temp;
				}
				
				//有一些没有返回值的取不到，newNode.getNodeId()。因此有些得不到返回值
				String F2_callClass = "";
				String F4_return = "";
				IMethodBinding bind = node.resolveMethodBinding();
				if ( bind != null){
					F4_return = bind.getReturnType().getName();
					F2_callClass = bind.getDeclaringClass().getName();
				}
				
				featureValue.put( "F1", F1_callName);
				featureValue.put( "F2", F2_callClass );
				featureValue.put( "F3", F3_para );
				/*
				for ( int i = 0; i < F3_para.length; i++ ){
					featureValue.put( "F3-" + i, F3_para[i]);
				}
				*/
				featureValue.put( "F4", F4_return );
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				
				return true;
			}
			
			//F5_F6
			public boolean visit ( ClassInstanceCreation node ){
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				System.out.println( "ClassInstanceCreation: " + node.toString() );
				
				HashMap<String, String> featureValue = new HashMap<String, String>();
				
				ITypeBinding bind = node.resolveTypeBinding();
				String F5_newType = "";
				if ( bind != null && bind.getSuperclass() != null )
					F5_newType = bind.getSuperclass().getBinaryName();       //或者是getName
				
				String F6_newConcreteType = "";
				if ( bind != null )
					F6_newConcreteType = bind.getQualifiedName();
				
				featureValue.put( "F5", F5_newType);
				featureValue.put( "F6", F6_newConcreteType);
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				
				return true;
			}
			
			//F7
			public boolean visit ( ExpressionStatement node ){		
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				System.out.println( "ExpressionStatement: " + node.toString() );
				
				String content = node.toString();
				String F7_binaryOperator = "";
				//从BINARY_OPERATION.length数组的后面往前看，找到匹配的就停止。目前只考虑一个operator的情况
				for ( int i = Constants.BINARY_OPERATION.length-1; i >= 0 ; i-- ){
					if ( content.contains( Constants.BINARY_OPERATION[i])){
						F7_binaryOperator = Constants.BINARY_OPERATION[i];
						break;
					}
				}
				
				if ( F7_binaryOperator.equals( ""))
					return true;
				
				HashMap<String, String> featureValue = new HashMap<String, String>();
				featureValue.put( "F7", F7_binaryOperator);
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				
				return true;
			}
			
			//不完善，newNode.nodeId 这种类型的得不到
			//F8_F9
			public boolean visit ( FieldAccess node){	
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				System.out.println( "FieldAccess: " + node.toString() );
				
				IVariableBinding bind = node.resolveFieldBinding();
				String F8_fieldAccessClass = "";
				String F9_fieldAccessField = "";
				if ( bind != null ){
					if ( bind.getDeclaringClass() != null )
						F8_fieldAccessClass = bind.getDeclaringClass().getName();
					F9_fieldAccessField = bind.getName();
				}
				
				HashMap<String, String> featureValue = new HashMap<String, String>();
				featureValue.put( "F8", F8_fieldAccessClass );
				featureValue.put( "F9", F9_fieldAccessField );
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				
				return true;
			}
			
			//未知这里是否会有问题，可能LineNumber不太一样
			//F10
			public boolean visit(CatchClause node) {
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				System.out.println( "CatchClause: " + node.toString() );
				
				String F10_isClause = "false";
				if ( node.getNodeType() == ASTNode.CATCH_CLAUSE ){
					F10_isClause = "true";
				}
				
				HashMap<String, String> featureValue = new HashMap<String, String>();
				featureValue.put( "F10", F10_isClause );
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				
	            return true;
	        }
			
			//F11_to_F14
			public boolean visit ( FieldDeclaration node){
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				//System.out.println ( "FieldDeclaration: +++++++++++++++++++++++++++++++++++++++++++" + node.toString() );
				String F11_fieldName = "";
				String F12_fieldType = "";
				String F13_fieldVisibility = "";   
				String F14_fieldType = "";     //可能是多种，例如fina static，直接连接，而visibility只可能是一种
				
				List temp = node.fragments();
				for ( int i = 0; i < temp.size(); i++ ) {
					VariableDeclarationFragment frag = (VariableDeclarationFragment) temp.get(i);
					IVariableBinding bind = frag.resolveBinding();
					F11_fieldName = bind.getName();
					F12_fieldType = bind.getType().getName();
					
					String content = bind.getVariableDeclaration().toString();
					for ( int j =0; j < Constants.METHOD_FIELD_VISIBILITY.length; j++ ){
						if ( content.contains( Constants.METHOD_FIELD_VISIBILITY[j] ) )
							F13_fieldVisibility = Constants.METHOD_FIELD_VISIBILITY[j];
					}
					for ( int j =0; j < Constants.METHOD_FIELD_TYPE.length; j++ ){
						if ( content.contains( Constants.METHOD_FIELD_TYPE[j] )){
							if ( F14_fieldType.equals( "")){
								F14_fieldType += Constants.METHOD_FIELD_TYPE[j];
							}else{
								F14_fieldType += "-" + Constants.METHOD_FIELD_TYPE[j];
							}
						}	
					}
				}
				
				HashMap<String, String> featureValue = new HashMap<String, String>();
				featureValue.put( "F11", F11_fieldName );
				featureValue.put( "F12", F12_fieldType );
				featureValue.put( "F13", F13_fieldVisibility );
				featureValue.put( "F14", F14_fieldType );
				
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				return true;
			}
			
			/*
			 * 从 node.toString() 里面抽取，F15,F17
			 * .getReturnType().getName() 或者 getQualifiedName(), F16
			 */
			public boolean visit(MethodDeclaration node){
				int index = SourceCodeParser.containsLineNumber(slicesCodeList, node);
				if ( index == -1 ) {
					return true;
				}
				
				//System.out.println ( "MethodDeclaration: +++++++++++++++++++++++++++++++++++++++++++" + node.toString() );
				String content = node.toString();
				String F15_methodVisibility = "";
				String F16_return = "";
				String F17_methodType = "";			
				
				String[] sentContent = content.split( "\n");
				for ( int i =0; i < sentContent.length; i++ ){
					if ( !F15_methodVisibility.equals( ""))
						break;
					if ( sentContent[i].contains( "/") || sentContent[i].contains( "*") )
						continue;
					for ( int j =0; j < Constants.METHOD_FIELD_VISIBILITY.length; j++ ){
						if ( sentContent[i].contains( Constants.METHOD_FIELD_VISIBILITY[j] ) )
							F15_methodVisibility = Constants.METHOD_FIELD_VISIBILITY[j];
					}
					for ( int j =0; j < Constants.METHOD_FIELD_TYPE.length; j++ ){
						if ( sentContent[i].contains( Constants.METHOD_FIELD_TYPE[j] )){
							if ( F17_methodType.equals( "")){
								F17_methodType += Constants.METHOD_FIELD_TYPE[j];
							}else{
								F17_methodType += "-" + Constants.METHOD_FIELD_TYPE[j];
							}
						}	
					}
				}
				
				
				IMethodBinding bind = node.resolveBinding();
				if ( bind != null ){
					F16_return = bind.getReturnType().getName();
				}
				
				HashMap<String, String> featureValue = new HashMap<String, String>();
				featureValue.put( "F15", F15_methodVisibility );
				featureValue.put( "F16", F16_return );
				featureValue.put( "F17", F17_methodType );
				
				System.out.println ( featureValue.toString() );
				
				if ( codeFeature.containsKey( index )){
					featureValue.putAll( codeFeature.get( index ));
				}
				codeFeature.put( index , featureValue );
				return true;
			}			
		});		
		System.out.println( "End binding.");
		
		return codeFeature;
	}	
	
	public static int containsLineNumber ( ArrayList<CodeInfo> codeInfoList, ASTNode node ){
		int lineNumber = Slicer.getLineNumber( node );
		
		int index = -1;
		for ( int i = 0; i < codeInfoList.size(); i++ ) {
			int codeLine = codeInfoList.get(i).getCodeLine();
			if ( codeLine == lineNumber ){
				index = i;
			}
		}

		return index;
	}
}
