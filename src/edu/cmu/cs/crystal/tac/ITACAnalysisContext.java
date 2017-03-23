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
package edu.cmu.cs.crystal.tac;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.SuperVariable;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * This interface defines methods to map AST data structures to TAC variables.
 * These methods can for instance be used by TAC analysis transfer functions.
 * 
 * @author Kevin Bierhoff
 * 
 * @see ITACTransferFunction
 * @see ITACBranchSensitiveTransferFunction
 */
public interface ITACAnalysisContext {

	/**
	 * Returns the TAC variable for a given ASTNode. This ASTNode must
	 * represent something where there is a resulting variable, for example,
	 * a SingleVariableDeclaration will not have a variable as it has no "result".
	 * It is the caller's responsibility to make sure to call this
	 * method only while the method surrounding the given node is analyzed.
	 * @param node AST node in the previously analyzed method.
	 * @return The TAC variable for a given ASTNode, or null if this ASTNode has no result.
	 */
	public Variable getVariable(ASTNode node);

	/**
	 * Returns the <b>this</b> variable for the analyzed method. Returns null if we are analyzing
	 * a static method.
	 * @return The <b>this</b> variable for the analyzed method. 
	 */
	public ThisVariable getThisVariable();
	
	/**
	 * Returns the <b>super</b> variable for the analyzed method, if any.
	 * @return the <b>super</b> variable for the analyzed method or
	 * <code>null</code> if it doesn't exist.
	 */
	public SuperVariable getSuperVariable();

	/**
	 * Returns the variable for a given parameter or local.
	 * It is the caller's responsibility to make sure to call this
	 * method only while the method declaring the parameter or local
	 * is analyzed.
	 * @param varBinding Binding of a local or parameter.
	 * @return the variable for the given parameter or local.
	 */
	public SourceVariable getSourceVariable(IVariableBinding varBinding);
	
	public MethodDeclaration getAnalyzedMethod();

}