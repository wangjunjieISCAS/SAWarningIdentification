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
package edu.cmu.cs.crystal.tac.eclipse;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Name;

import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.SuperVariable;
import edu.cmu.cs.crystal.tac.model.TACInstruction;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.TypeVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Interface to query variables
 * from the TAC infrastructure.  Variables are lazily determined
 * by most TAC classes, and this interface is used to do so.
 * 
 * @author Kevin Bierhoff
 * @see EclipseTACInstructionFactory
 * @see TACInstruction
 * @see EclipseAbstractFieldAccess
 */
public interface IEclipseVariableQuery {
	
	/**
	 * Returns the variable representing the result of evaluating the given AST node.
	 * @param astNode Must be a node that evaluates to a value (usually an 
	 * {@link org.eclipse.jdt.core.dom.Expression}.
	 * @return the variable representing the result of evaluating the given AST node.
	 */
	public Variable variable(ASTNode astNode);

	/**
	 * Determines the variable for the given parameter or local variable binding.
	 * @param binding Binding for a parameter or local variable.
	 * @return Variable for the given local variable binding.
	 */
	public SourceVariable sourceVariable(IVariableBinding binding);
	
	/**
	 * Determines the variable for a given type binding.
	 * @param binding Type binding.
	 * @return Variable for the given type binding.
	 */
	public TypeVariable typeVariable(ITypeBinding binding);
	
	/**
	 * Returns the <code>this</code> variable, if the surrounding method is an 
	 * instance method.
	 * @return The <code>this</code> variable, if the surrounding method is an 
	 * instance method, <code>null</code> otherwise.
	 */
	public ThisVariable thisVariable();

	/**
	 * Determines the implicit <b>this</b> variable for a method call or field access.
	 * @param accessedElement The element being accessed with an implicit <b>this</b>. 
	 * Must be a {@link IMethodBinding} for a method or constructor
	 * or a {@link IVariableBinding} for a field.
	 * @return Implicit <b>this</b> variable for a method call or field access.
	 * @throws IllegalArgumentException is given binding is not a constructor, method or field.
	 */
	public ThisVariable implicitThisVariable(IBinding accessedElement);
	
	/**
	 * Determines the <b>super</b> variable, taking a possible qualifier into account.
	 * @param qualifier Qualifier for <b>super</b> access; <code>null</code> 
	 * for unqualified <b>super</b>.
	 * @return <b>super</b> variable for the given qualifier.
	 */
	public SuperVariable superVariable(Name qualifier);

}
