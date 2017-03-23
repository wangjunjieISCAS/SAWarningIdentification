/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,
 * Kevin Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 *
 * This file is part of Crystal.
 *
 * Crystal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Crystal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Crystal.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.util.Box;
import edu.cmu.cs.crystal.util.Lambda;
import edu.cmu.cs.crystal.util.Option;

/**
 * A collection of methods used to extract useful data from the workspace.
 * These methods are used by the framework and should not be used by users
 * of the framework.
 * 
 * You can access must of the data collected from these methods via the
 * Crystal class.
 * 
 * @author David Dickey
 *
 */
public class WorkspaceUtilities {

	private static final Logger log = Logger.getLogger(WorkspaceUtilities.class.getName());

	/**
	 * Traverses the workspace for CompilationUnits.
	 * 
	 * @return	the list of all CompilationUnits in the workspace or
	 * <code>null</code> if no comp units were found.
	 */
	public static List<ICompilationUnit> scanForCompilationUnits() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if(workspace == null) {
			log.warning("No workspace");
			return null;
		}
		IWorkspaceRoot root = workspace.getRoot();
		if(root == null) {
			log.warning("No workspace root");
			return null;
		}
 		IJavaModel javaModel = JavaCore.create(root);
		if(javaModel == null) {
			log.warning("No Java Model in workspace");
			return null;
		}

 		// Get all CompilationUnits
 		return collectCompilationUnits(javaModel);
	}

	/**
	 * A recursive traversal of the IJavaModel starting from the given
	 * element to collect all ICompilationUnits.
	 * Each compilation unit corresponds to each java file.
	 *  
	 * @param javaElement a node in the IJavaModel that will be traversed
	 * @return a list of compilation units or <code>null</code> if no comp units are found
	 */
	public static List<ICompilationUnit> collectCompilationUnits(IJavaElement javaElement) {
		List<ICompilationUnit> list = null, temp = null;
		// We are traversing the JavaModel for COMPILATION_UNITs
 		if(javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
 			list = new ArrayList<ICompilationUnit>();
 			list.add((ICompilationUnit) javaElement);
 			return list;
 		}
 		
		// Non COMPILATION_UNITs will have to be further traversed
		if(javaElement instanceof IParent) {
 	 		IParent parent = (IParent) javaElement;
 	 		
 	 		// Do not traverse PACKAGE_FRAGMENT_ROOTs that are ReadOnly
 	 		// this ignores libraries and .class files
 	 		if(javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT
 	 				&& javaElement.isReadOnly()) {
				return null;
 	 		}
 	 		
 			// Traverse
 	 		try {
	 			if(parent.hasChildren()) {
	 				IJavaElement[] children = parent.getChildren();
					for(int i = 0; i < children.length; i++) {
						temp = collectCompilationUnits(children[i]);
						if(temp != null)
							if(list == null)
								list = temp;
							else
								list.addAll(temp);
					}
	 			}
			} catch (JavaModelException jme) {
				log.log(Level.SEVERE, "Problem traversing Java model element: " + parent, jme);
			}
		} 
		else {
			log.warning("Encountered a model element that's not a comp unit or parent: " + javaElement);
		}
		
 		return list;
	}
	
	/**
	 * Traverses the workspace for CompilationUnits and (optionally) class files.
	 * 
	 * @param   includeClassfiles include compilation units availabile as .class files
	 * @return	the list of all CompilationUnits in the workspace or
	 * <code>null</code> if no comp units were found.
	 */
	public static List<ITypeRoot> scanForCompilationUnits(boolean includeClassfiles) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if(workspace == null) {
			log.warning("No workspace");
			return null;
		}
		IWorkspaceRoot root = workspace.getRoot();
		if(root == null) {
			log.warning("No workspace root");
			return null;
		}
 		IJavaModel javaModel = JavaCore.create(root);
		if(javaModel == null) {
			log.warning("No Java Model in workspace");
			return null;
		}

 		// Get all CompilationUnits
 		return collectCompilationUnits(javaModel, includeClassfiles);
	}

	/**
	 * A recursive traversal of the IJavaModel starting from the given
	 * element to collect all ITypeRoots, optionally including .class files available
	 * in libraries.
	 * If the given element is a type root it will be included no matter what.
	 * Each compilation unit corresponds to each java file.
	 *  
	 * @param javaElement a node in the IJavaModel that will be traversed
	 * @param includeArchives whether to descend into binary libraries of .class files
	 * @return a list of compilation units or <code>null</code> if no comp units are found
	 */
	public static List<ITypeRoot> collectCompilationUnits(IJavaElement javaElement, 
			boolean includeArchives) {
		List<ITypeRoot> result = new ArrayList<ITypeRoot>();
		collectTypeRoots(result, javaElement, includeArchives);
		return result;
	}
	
	private static void collectTypeRoots(List<? super ITypeRoot> list, IJavaElement javaElement,
			boolean includeArchives) {
		// We are traversing the JavaModel for COMPILATION_UNITs and CLASS_FILEs
 		switch (javaElement.getElementType()) {
 		case IJavaElement.CLASS_FILE:
 		case IJavaElement.COMPILATION_UNIT:
 			list.add((ITypeRoot) javaElement);
 			return;
 		// if the given element is inside a type root, find it
 		case IJavaElement.ANNOTATION:
 		case IJavaElement.FIELD:
 		case IJavaElement.IMPORT_CONTAINER:
 		case IJavaElement.IMPORT_DECLARATION:
 		case IJavaElement.INITIALIZER:
 		case IJavaElement.LOCAL_VARIABLE:
 		case IJavaElement.METHOD:
 		case IJavaElement.PACKAGE_DECLARATION:
 		case IJavaElement.TYPE:
 		case IJavaElement.TYPE_PARAMETER:
 			do {
 				if (javaElement instanceof ITypeRoot) {
 					list.add((ITypeRoot) javaElement);
 					return;
 				}
 				javaElement = javaElement.getParent();
 			} while (javaElement != null);
 			return;
 		}
 		
		// Non COMPILATION_UNITs will have to be further traversed
		if(javaElement instanceof IParent) {
 	 		IParent parent = (IParent) javaElement;
 	 		
 	 		// Do not traverse PACKAGE_FRAGMENT_ROOTs that are ReadOnly
 	 		// this ignores libraries and .class files
 	 		if(! includeArchives
 	 				&& javaElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT
 	 				&& javaElement.isReadOnly()) {
				return;
 	 		}
 	 		
 			// Traverse
 	 		try {
	 			if(parent.hasChildren()) {
	 				IJavaElement[] children = parent.getChildren();
					for(int i = 0; i < children.length; i++)
						collectTypeRoots(list, children[i], includeArchives);
	 			}
			} catch (JavaModelException jme) {
				log.log(Level.SEVERE, "Problem traversing Java model element: " + parent, jme);
			}
		} 
		else {
			log.warning("Encountered a model element that's not a comp unit or parent: " + javaElement);
		}
	}
	
	/**
	 * Goes through a list of compilation units and parses them.  The act of parsing
	 * creates the AST structures from the source code.
	 * 
	 * @param compilationUnits	the list of compilation units to parse
	 * @return	the mapping from compilation unit to the AST roots of each
	 */
	public static Map<ICompilationUnit, ASTNode> parseCompilationUnits(List<ICompilationUnit> compilationUnits) {
		if(compilationUnits == null)
			throw new CrystalRuntimeException("null list of compilation units");
		
		Map<ICompilationUnit, ASTNode> parsedCompilationUnits = new HashMap<ICompilationUnit, ASTNode>();
 		Iterator<ICompilationUnit> iter = compilationUnits.iterator();
 		ICompilationUnit compUnit = null;
 		ASTParser parser = null;
 		ASTNode node = null;
 		for(; iter.hasNext() ;) {
 			compUnit = iter.next();
 	 		parser = ASTParser.newParser(AST.JLS3);
 			parser.setResolveBindings(true);
 			parser.setSource(compUnit);
 			node = parser.createAST(null);
 			parsedCompilationUnits.put(compUnit, node);
 		}
 		return parsedCompilationUnits;
	}
	
	/**
	 * Collects all top level methods from CompilationUnits.
	 * 
	 * (Embedded Methods are currently not collected.)
	 * 
	 * @param compilationUnitToASTNode	the mapping of CompilationUnits to preparsed ASTNodes
	 * @return							the list of all top level methods within the CompilationUnits
	 */
	public static List<MethodDeclaration> scanForMethodDeclarations(Map<ICompilationUnit, ASTNode> compilationUnitToASTNode) {
		if(compilationUnitToASTNode == null)
			throw new CrystalRuntimeException("null map of compilation units to ASTNodes");
		
		// Create an empty list
		List<MethodDeclaration> methodList = new LinkedList<MethodDeclaration>();
		List<MethodDeclaration> tempMethodList; 
		// Get all CompilationUnits and look for MethodDeclarations in each
		Set<ICompilationUnit> compUnits = compilationUnitToASTNode.keySet();
		Iterator<ICompilationUnit> compUnitIterator = compUnits.iterator();
		ICompilationUnit icu;
		for(;compUnitIterator.hasNext();){
			icu = compUnitIterator.next();
			tempMethodList = scanForMethodDeclarationsFromAST(compilationUnitToASTNode.get(icu));
			methodList.addAll(tempMethodList);
		}
		return methodList;
	}
	
	/**
	 * Collects all top level methods from an AST including embedded methods.
	 * 
	 * @param node	the root of an AST
	 * @return		all top level methods within the AST
	 */
	public static List<MethodDeclaration> scanForMethodDeclarationsFromAST(ASTNode node) {
		if(node == null)
			throw new CrystalRuntimeException("AST tree not found from ICompilationUnit");
		
		// Visitor Class
		class MethodFindVisitor extends ASTVisitor {
			List<MethodDeclaration> methodList;
			public MethodFindVisitor(List<MethodDeclaration> inMethodList) {
				methodList = inMethodList;
			}
			// Visit MethodDeclarations
			public boolean visit(MethodDeclaration methodDeclaration) {
				methodList.add(methodDeclaration);
				
				// false returns us back, instead of traversing further down
				return true;
			}
		}

		// Create an empty list, populate methods by traversing using the visitor
		List<MethodDeclaration> methodList = new LinkedList<MethodDeclaration>();
		MethodFindVisitor visitor = new MethodFindVisitor(methodList);
		node.accept(visitor);
		return methodList;
	}

	public static Map<String, ASTNode> scanForBindings(Map<ICompilationUnit, ASTNode> compilationUnitToASTNode) {
		if(compilationUnitToASTNode == null)
			throw new CrystalRuntimeException("null map of compilation units to ASTNodes");
		
		Map<String, ASTNode> bindings = new HashMap<String, ASTNode>();
		// Get all CompilationUnits and look for MethodDeclarations in each
		Set<ICompilationUnit> compUnits = compilationUnitToASTNode.keySet();
		Iterator<ICompilationUnit> compUnitIterator = compUnits.iterator();
		ICompilationUnit icu;
		for(;compUnitIterator.hasNext();){
			icu = compUnitIterator.next();
			ASTNode node = compilationUnitToASTNode.get(icu);
			node.accept(new BindingsCollectorVisitor(bindings));
		}
		return bindings;
	}


	public static Map<String, ASTNode> scanForBindings(ICompilationUnit compUnit, ASTNode node) {
		Map<String, ASTNode> bindings = new HashMap<String, ASTNode>();
		node.accept(new BindingsCollectorVisitor(bindings));
		return bindings;
	}

	/**
	 * Returns the list of compilation units for a given list of file names.
	 * All compilation units that <i>contain</i> one of the given strings are
	 * returned.
	 * @param files List of file names to search for.  They will be compared to
	 * the result of {@link #getWorkspaceRelativeName(IJavaElement)}.
	 * @return List of compilation units for a given list of file names.
	 */
	public static List<ICompilationUnit> findCompilationUnits(List<String> files) {
		List<ICompilationUnit> allCompUnits = WorkspaceUtilities.scanForCompilationUnits();

		int foundCount = 0;
		ICompilationUnit[] resultArray = new ICompilationUnit[files.size()];
		for(ICompilationUnit compUnit : allCompUnits) {
			String relativeName = getWorkspaceRelativeName(compUnit);
			for(int i = 0; i < files.size(); i++) {
				if(relativeName.indexOf(files.get(i)) >= 0) {
					resultArray[i] = compUnit;
					++foundCount;
				}
			}
		}
		
		List<ICompilationUnit> result;
		if(foundCount == files.size())
			result = Arrays.asList(resultArray); 
		else {
			result = new ArrayList<ICompilationUnit>(foundCount);
			for(ICompilationUnit compUnit : resultArray) {
				if(compUnit != null)
					result.add(compUnit);
			}
		}
		return result;
	}

	/**
	 * Walks up the Java model hierarchy and separates the names of encountered
	 * elements by forward slashes
	 * @param element
	 * @return Symbolic name of the given Java element relative to the workspace root 
	 */
	public static String getWorkspaceRelativeName(IJavaElement element) {
		String result = element.getElementName();
		while(element.getParent() != null) {
			element = element.getParent();
			result = element.getElementName() + "/" + result;
		}
		return result;
	}

	/**
	 * Gets the root ASTNode for a compilation unit, with bindings on.
	 * @param compUnit never {@code null}
	 * @return the root ASTNode for a compilation unit, with bindings on. 
	 * @throws IllegalStateException if {@code compUnit} doesn't have a
	 * {@link ITypeRoot#getSource() source attachment} 
	 * @see ASTParser#createAST(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public static ASTNode getASTNodeFromCompilationUnit(ITypeRoot compUnit) {
	 	ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setSource(compUnit);
		try {
			return parser.createAST(/* passing in monitor messes up previous monitor state */ null);
		} catch (IllegalStateException e) {
			log.log(Level.SEVERE, "could not parse " + compUnit, e);
			throw e;
		}
	}

	/**
	 * Given an IType from the model, this method will return the ast node
	 * associated with that type, or null if it doesn't exist.
	 * @return NONE if the ast node associated with this type could not
	 * be found.
	 */
	public static Option<TypeDeclaration> getDeclNodeFromType(final IType type) {
		return findNodeForModel(type, TypeDeclaration.class, 
				new Lambda<TypeDeclaration,Boolean>(){
					public Boolean call(TypeDeclaration i) {
						return i.resolveBinding().getJavaElement().equals(type);
					}
				});
	}
	
	/**
	 * Returns the AST node associated with the given model element, which in this case
	 * is a method.
	 * @param method
	 * @return
	 */
	public static Option<MethodDeclaration> getMethodDeclFromModel(final IMethod method) {
		return findNodeForModel(method, MethodDeclaration.class,
				new Lambda<MethodDeclaration, Boolean>(){
					public Boolean call(MethodDeclaration i) {
						return i.resolveBinding().getMethodDeclaration().getJavaElement().equals(method);
					}
				});
	}
	
	/**
	 * Return the ast node associated with the given model element.
	 * @return
	 */
	private static <NODETYPE extends ASTNode> Option<NODETYPE> 
		findNodeForModel(IMember model_element, final Class<? extends NODETYPE> clazz,
			final Lambda<NODETYPE,Boolean> isCorrectNode) {
		ICompilationUnit comp_unit = model_element.getCompilationUnit();
		ASTNode node = getASTNodeFromCompilationUnit(comp_unit);
		
		// Now, find the corresponding type node 
		final Box<NODETYPE> result = new Box<NODETYPE>(null);
		node.accept(new ASTVisitor() {
			
			@Override
			public void postVisit(ASTNode node) {
				// If this node is a subtype of the node we are
				// interested in, then we can ask the client's
				// function if this is the right instance.
				if( clazz.isAssignableFrom(node.getClass()) ) {
					@SuppressWarnings("unchecked") NODETYPE node2 = (NODETYPE)node;
					if( isCorrectNode.call(node2) ) {
						result.setValue(node2);
					}
				}
			}
		});
		
		if( result.getValue() == null )
			return Option.none();
		else
			return Option.some(result.getValue());
	}
}

class BindingsCollectorVisitor extends ASTVisitor {
	Map<String, ASTNode> bindings = null;
	
	public BindingsCollectorVisitor(Map<String, ASTNode> bindingsIn) {
		bindings = bindingsIn;
	}
	
	protected void addNewBinding(IBinding binding, ASTNode node) {
		if(binding == null)
			return;
		if(bindings == null)
			throw new CrystalRuntimeException("BindingsCollectorVisitor::addNewBinding: Unexpected null mapping");
		if(bindings.containsKey(binding)) {
			throw new CrystalRuntimeException("BindingsCollectorVisitor::addNewBinding: Readding existing binding.  This is a framework error.");
		}
		bindings.put(binding.getKey(), node);
	}
	
	public boolean visit(AnonymousClassDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}	
	public boolean visit(EnumConstantDeclaration node) {
		addNewBinding(node.resolveVariable(), node);
		return true;
	}		
	public boolean visit(EnumDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
	// FieldDeclaration - handled by VariableDeclarationFragment
	public boolean visit(ImportDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}

	public boolean visit(MethodDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
	public boolean visit(PackageDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
	public boolean visit(SingleVariableDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
	public boolean visit(TypeDeclarationStatement node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
	public boolean visit(VariableDeclaration node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
	public boolean visit(VariableDeclarationFragment node) {
		addNewBinding(node.resolveBinding(), node);
		return true;
	}
		
}
