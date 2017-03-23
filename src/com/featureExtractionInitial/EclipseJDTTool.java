package com.featureExtractionInitial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Binding;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.hierarchy.*;
//import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
//import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

public class EclipseJDTTool {
	
	public void obtainMethodInfo ( String fileName ){
		String content = this.readJavaFile(fileName);
		ASTParser parser = ASTParser.newParser( AST.JLS8 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource( content.toCharArray() );
		CompilationUnit result = (CompilationUnit) parser.createAST( null );
		result.accept( new TypeFinderVisitor() );
		
		
		if ( result.getAST().hasBindingsRecovery()  ){
			System.out.println( "binding recovery!");
		}
		if ( result.getAST().hasResolvedBindings()  ){
			System.out.println( "binding resolved!");
		}
		 
		List types = result.types();
		// 取得类型声明
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

		MethodDeclaration methodDec[] = typeDec.getMethods();
		System.out.println( methodDec.length );
		
		for (MethodDeclaration method : methodDec) {
			IMethodBinding bind = method.resolveBinding();
			bind.getName().toString();
			/*
			IMethod methodII = (IMethod) method.resolveBinding().getJavaElement();
			try {
				System.out.println( methodII.getParameterNames()  );
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			//System.out.println(  method.resolveBinding().getName() );
			
			//System.out.println( "length: " + method.getLength()  +  " " + method.getStartPosition() );
			//System.out.println ( method.getName() );
			//System.out.println ( method );
			//System.out.println ( method.getBody() );
			Block methodBody = method.getBody();
			String[] temp = methodBody.toString().split( "\n");
			System.out.println( temp.length );
			
			List stateList = methodBody.statements();
			for ( int i = 0; i < stateList.size(); i++ ){
				//System.out.println( stateList.get( i ).toString() );
			}
			//System.out.println( stateList.size()  ) ;
			
			System.out.println("=============");
			//System.out.println(method);
		}			
	}
	
	class TypeFinderVisitor extends ASTVisitor{
		 
		public boolean visit(VariableDeclarationStatement node){
			for (Iterator iter = node.fragments().iterator(); iter.hasNext();) {
				System.out.println("------------------");
	 
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) iter.next();
				IVariableBinding binding = fragment.resolveBinding();
	 
				System.out.println("binding variable declaration: " +binding.getVariableDeclaration());
				System.out.println("binding: " +binding);
			}
			return true;
		}
	}
	
	class MethodFinderVisitor extends ASTVisitor{	
		
		public boolean visit( MethodDeclaration node){
			IMethodBinding bind = node.resolveBinding();
			System.out.println( "binding: " + bind.getParameterTypes().toString() );
			return true;
		}
	}
	
	public ArrayList<String> praseJavaFile( String filePath ) throws Exception {
		final ArrayList<String> list = new ArrayList<String>();
		
		String source = this.readJavaFile( filePath);
		
		ASTParser parser = ASTParser.newParser(AST.JLS2);

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        char[] content = source.toCharArray();
        parser.setSource(content);
        parser.setUnitName(filePath);
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
            JavaCore.VERSION_1_7);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        String[] sources = {};
        String[] classPaths = {};
        parser.setEnvironment(classPaths, sources, null, true);
        parser.setResolveBindings(false);
        parser.setCompilerOptions(options);
        parser.setStatementsRecovery(true);

        try {
            final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
            final AST ast = unit.getAST();

            // Process the main body
            try {
                unit.accept(new ASTVisitor() {
                    public boolean visit(CatchClause node) {
                        list.add("CatchClause");
                        return true;
                    }
                    public boolean visit(ClassInstanceCreation node) {
                        //list.add("ClassInstanceCreation");
                        list.add(node.getName().toString());
                        System.out.println ( node.getName().toString() );
                        return true;
                    }
                    
                    public boolean visit(DoStatement node) {
                        list.add("DoStatement");
                        
                        return true;
                    }
                    public boolean visit(EnumConstantDeclaration node) {
                        list.add(node.getName().toString());
                        return true;
                    }
                    public boolean visit(EnumDeclaration node) {
                        list.add("EnumDeclaration");
                        list.add(node.getName().toString());
                        return true;
                    }
                    public boolean visit(ForStatement node) {
                        list.add("ForStatement");
                        
                        return true;
                    }
                    public boolean visit(IfStatement node) {
                        list.add("IfStatement");
                        return true;
                    }
                    public boolean visit(MethodDeclaration node) {
                  
                        list.add(node.getName().toString());
                        return true;
                    }
                    public boolean visit(MethodInvocation node) {
                        list.add(node.getName().toString());
                        return true;
                    }
                    public boolean visit(SwitchCase node) {
                        list.add("SwitchCase");
                        return true;
                    }
                    public boolean visit(SynchronizedStatement node) {
                        list.add("SynchronizedStatement");
                        return true;
                    }
                    public boolean visit(ThisExpression node) {
                        list.add("ThisExpression");
                        return true;
                    }
                    public boolean visit(ThrowStatement node) {
                        list.add("ThrowStatement");
                        return true;
                    }
                    public boolean visit(TryStatement node) {
                        list.add("TryStatement");
                        return true;
                    }
                    public boolean visit(TypeDeclaration node) {
                        list.add(node.getName().toString());
                        return true;
                    }
                    public boolean visit(WhileStatement node) {
                        list.add("WhileStatement");
                        return true;
                    }
                });
            } catch (Exception e) {
                System.out.println("Crashed while processing : " + filePath);
                System.out.println("Problem : " + e.toString());
                e.printStackTrace();
                System.exit(0);
            }
            return list;

        } catch (Exception e) {
            System.out.println("\nError while executing compilation unit : " + e.toString());
            return null;
        }
        
	}
	
	/*
	public void methodCallers ( String fileName ){
		String content = this.readJavaFile(fileName);
		ASTParser parser = ASTParser.newParser( AST.JLS8 );
		parser.setKind( ASTParser.K_COMPILATION_UNIT);
		
		parser.setSource( content.toCharArray() );
		CompilationUnit result = (CompilationUnit) parser.createAST( null );
		
		List types = result.types();
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0);
		
		IType [] typeDeclarationList = (IType[]) typeDec.getTypes();
		 
		SearchEngine searchEngine = new SearchEngine();
		for (IType typeDeclaration : typeDeclarationList) {
		     // get methods list
		     IMethod[] methodList;
			try {
				methodList = typeDeclaration.getMethods();
				
				for (IMethod method : methodList) {
			          final List<String> referenceList = new ArrayList<String>();
			          // check each method.
			          String methodName = method.getElementName();
			          if (!method.isConstructor()) {
			              // Finds the references of the method and record references of the method to referenceList parameter.
			        	  IJavaSearchScope searchScope= SearchEngine.createWorkspaceScope();
			        	  
			        	  CallLocation location = hierarchy.getCallLocation( methodName );
			        	  MethodWrapper [] wrapper = hierarchy.getCalleeRoots(methods);
			        	  
			        	  searchEngine.searchMethodReference(referenceList, method, searchScope);
			          }
			     }
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	*/
	
	public HashSet<IMethod> getCallersOf( IMethod m) {

		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = { m };

		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		HashSet<IMethod> callers = new HashSet<IMethod>();
		for (MethodWrapper mw : methodWrappers) {
			MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
			HashSet<IMethod> temp = getIMethods(mw2);
			callers.addAll(temp);
		}

		return callers;
	}

	HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IMethod> c = new HashSet<IMethod>();
		for (MethodWrapper m : methodWrappers) {
			IMethod im = getIMethodFromMethodWrapper(m);
			if (im != null) {
				c.add(im);
			}
		}
		return c;
	}

	IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
		try {
			IMember im = m.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return (IMethod) m.getMember();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void obtainAST(String fileName ) throws IOException {
		String content = readJavaFile( fileName ); 
		// 创建解析器
		ASTParser parsert = ASTParser.newParser(AST.JLS3);
		parsert.setKind( ASTParser.K_COMPILATION_UNIT);
		// 设定解析器的源代码字符
		parsert.setSource(content.toCharArray());
		// 使用解析器进行解析并返回AST上下文结果(CompilationUnit为根节点)
		CompilationUnit result = (CompilationUnit) parsert.createAST( null );
		
		// 获取类型
		List types = result.types();
		// 取得类型声明
		System.out.println( "number of class: " + types.size() );
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0);

		// ##############获取源代码结构信息#################
		// 引用import
		List importList = result.imports();
		// 取得包名
		PackageDeclaration packetDec = result.getPackage();
		// 取得类名
		String className = typeDec.getName().toString();
		// 取得函数(Method)声明列表
		MethodDeclaration methodDec[] = typeDec.getMethods();
		System.out.println( "method number in files: " + methodDec.length );
		
		// 取得函数(Field)声明列表
		FieldDeclaration fieldDec[] = typeDec.getFields();

		// 输出包名
		System.out.println("包:");
		System.out.println(packetDec.getName());
		// 输出引用import
		System.out.println("引用import:");
		for (Object obj : importList) {
			ImportDeclaration importDec = (ImportDeclaration) obj;
			System.out.println(importDec.getName());
		}
		// 输出类名
		System.out.println("类:");
		System.out.println(className);
		// 循环输出函数名称
		System.out.println("========================");
		System.out.println("函数:");
		for (MethodDeclaration method : methodDec) {
			/*
			 * System.out.println(method.getName());
			 * System.out.println("body:");
			 * System.out.println(method.getBody());
			 * System.out.println("Javadoc:" + method.getJavadoc());
			 * 
			 * System.out.println("Body:" + method.getBody());
			 * 
			 * System.out.println("ReturnType:" + method.getReturnType());
			 */
			System.out.println("=============");
			System.out.println(method);
		}

		// 循环输出变量
		System.out.println("变量:");
		for (FieldDeclaration fieldDecEle : fieldDec) {
			// public static
			for (Object modifiObj : fieldDecEle.modifiers()) {
				Modifier modify = (Modifier) modifiObj;
				System.out.print(modify + "-");
			}
			System.out.println(fieldDecEle.getType());
			for (Object obj : fieldDecEle.fragments()) {
				VariableDeclarationFragment frag = (VariableDeclarationFragment) obj;
				System.out.println("[FIELD_NAME:]" + frag.getName());
			}
		}

	}

	private String readJavaFile(String filename)  {
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
	
	public static void main ( String args[] ) throws Exception{
		
		//String javaFile = "D://java-workstation//lucene2.9.2//src//org//apache//lucene//analysis//NumericTokenStream.java";
		//String javaFile = "D://javancss-32.53//bin//test//Test2.java";
		
		String javaFile = "data/test1.java";
		
		
		EclipseJDTTool tool = new EclipseJDTTool();
		tool.obtainMethodInfo( javaFile );
		//tool.obtainMethodInfo( javaFile );
		//tool.obtainAST( javaFile );
		
		//ArrayList<String> result = tool.praseJavaFile( javaFile );
		//System.out.println( result.toString() );
	}
}
