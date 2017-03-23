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
package edu.cmu.cs.crystal.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * Assorted utility methods
 * 
 * @author David Dickey
 * @author Nels Beckman
 *
 */
public class Utilities {
	// TODO: implement a method that returns the local variables of a method.
	// 		 it may be more appropriate to put this in the Crystal class.
//	List<ASTNode> getLocals(MethodDeclaration m) {
//		return null;
//	}
	
	private static HashSet<String> elementals = new HashSet<String>();
	static {
		elementals.add("boolean");
		elementals.add("int");
		elementals.add("void");
		elementals.add("char");
		elementals.add("short");
		elementals.add("long");
		elementals.add("double");
		elementals.add("float");
	}
	
	/**
	 * For resolving binary types. Those are of the form QString; and Ljava.lang.String;. 
	 * 
	 */
	public static String resolveBinaryType(IType context, String binaryType) throws JavaModelException {
		return resolveType(context, Signature.toString(binaryType));
	}

	
	/**
	 * To be used instead of IType.resolveType(String). The former returns a String[][], 
	 * which is about the most useless thing in the world. Apparently, the idea is that
	 * resolveType could match to multiple things, so the first dimension is the matches.
	 * The second is really stupid, it's the qualified names. Also, IType.resolveType has 
	 * issues with elemental types, and this method properly handles those as well.
	 * 
	 * @return the first hit from context.resolveType(simpleName) as a fully qualified name, or
	 * simpleName if it was an elemental type, or null if the type could not be resolved.
	 * @throws JavaModelException 
	 */
	public static String resolveType(IType context, String simpleName) throws JavaModelException {
		if (elementals.contains(simpleName))
			return simpleName;
		
		String[][] matches = context.resolveType(simpleName);
		if (matches == null) {
			//we may have a type parameter then
			String[] bound = context.getTypeParameter(simpleName).getBounds();
			if (bound == null)
				return null;
			
			if (bound.length == 0)
				return "java.lang.Object";
			else
				return resolveType(context, bound[0]);
		}
		String pckg = matches[0][0].equals("") ? "" : matches[0][0] + ".";

		return pckg + matches[0][1];
	}

	/**
	 * Takes an ASTNode and creates a more useful textual representation of it.
	 */
	public static String ASTNodeToString(ASTNode node) {
		if(node == null)
			return " [null ASTNode]";
		String prefix = "-";
		String nodeToString = node.toString().replaceAll("\n", "*");
		if(node instanceof Statement)
			prefix = "S";
		else if (node instanceof Expression)
			prefix = "E";	
		else if (node instanceof Modifier)
			prefix = "M";	
		else if (node instanceof Type)
			prefix = "T";
		else if (node instanceof VariableDeclaration)
			prefix = "V";
		else if (node instanceof BodyDeclaration)
			prefix = "D";
		
		return prefix + " [" + node.getClass().getSimpleName() + "] \"" + nodeToString + "\"";
	}

	/**
	 * Is the given type the "void" type?
	 */
	public static boolean isVoidType(ITypeBinding type) {
		return type.isPrimitive() &&
			"void".equals(type.getName());
	}
	
	/**
	 * Converts a modifier flag to a String representation of the modifers.
	 * 
	 * @param modifier	the modifier flag
	 * @return	the textual representation of the modifiers
	 */
	public static String ModifierToString(int modifier) {
		String output = "";
		if(Modifier.isPrivate(modifier))
			output += "private ";
		if(Modifier.isProtected(modifier))
			output += "protected ";
		if(Modifier.isPublic(modifier))
			output += "public ";
		if(Modifier.isAbstract(modifier))
			output += "abstract ";
		if(Modifier.isFinal(modifier))
			output += "final ";
		if(Modifier.isNative(modifier))
			output += "native ";
		if(Modifier.isStatic(modifier))
			output += "static ";
		if(Modifier.isStrictfp(modifier))
			output += "strictfp ";
		if(Modifier.isSynchronized(modifier))
			output += "synchronized ";
		if(Modifier.isTransient(modifier))
			output += "transient ";
		if(Modifier.isVolatile(modifier))
			output += "volatile ";
		return output.trim();
	}
	
	/*
	 * Retrieves the corresponding ASTNode that the binding is 
	 * referencing.
	 * 
	 * @param binding	the binding
	 * @return	the corresponding ASTNode
	 *
	public static ASTNode getASTNode(IBinding binding) {
		return Crystal.getInstance().getASTNodeFromBinding(binding);
	}*/
	
	/**
	 * Finds the method declaration that this node is within.  If
	 * the node does not exist below a method declaration then null
	 * is returned.
	 * 
	 * @param node	the node whose method we wish to find
	 * @return	the method declaration or null if not within one
	 */
	public static MethodDeclaration getMethodDeclaration(ASTNode node) {
		while(node != null) {
			if(node.getNodeType() == ASTNode.COMPILATION_UNIT)
				return null;
			if(node.getNodeType() == ASTNode.METHOD_DECLARATION)
				return (MethodDeclaration) node;
			node = node.getParent();
		}		
		return null;
	}

	public static String methodDeclarationToString(MethodDeclaration md) {
		String output = "";
		output = md.getName() + "(";
		List params = md.parameters();
		if(params != null && params.size() > 0) {
			Iterator i = params.iterator();
			SingleVariableDeclaration svd;
			while(i.hasNext()) {
				svd = (SingleVariableDeclaration) i.next();
				output += svd.toString();
				if(i.hasNext())
					output += ", ";
			}
		}
		output += ")";
		return output;
	}
	
	/**
	 * Not Yet Implemented. Throws a runtime exception, and is
	 * of any type.
	 */
	public static <T> T nyi() {
		return nyi("This code has not yet been implemented.");
	}
	
	/**
	 * Not Yet Implemented. Throws a runtime exception with the
	 * given error message.
	 */
	public static <T> T nyi(String err_msg) {
		throw new RuntimeException(err_msg);
	}
}
