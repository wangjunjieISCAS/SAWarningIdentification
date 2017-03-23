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
package edu.cmu.cs.crystal.flow;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * This interface contains operations common to any transfer function
 * accepted by Crystal's flow analysis framework:
 * <ul>
 * <li>A direction: {@link #getAnalysisDirection()}
 * <li>Lattice operations: {@link #createLatticeOperations(MethodDeclaration)}
 * <li>Entry lattice value: {@link #createEntryValue(MethodDeclaration)}
 * </ul>
 * These will be called once whenever another method needs to be analyzed.
 * Extending interfaces will define methods for transferring over a specific
 * kind of node, such as AST nodes. 
 *
 * @author Kevin Bierhoff
 *
 * @param <LE> Analysis information being tracked as "lattice elements".
 */
public interface IFlowAnalysisDefinition<LE> {

	/**
	 * Gets the lattice operations for computing flow analysis
	 * results for a given method.
	 * Crystal uses the result of this method to compare and join intermediate
	 * results.
	 * @return lattice operations to be used for computing flow analysis
	 * results for a given method.
	 */
	public ILatticeOperations<LE> getLatticeOperations();
	
	/**
	 * Creates entry analysis information for analyzing a given method.
	 * Crystal's flow analysis uses the result of this method as the incoming
	 * analysis information to transfer over the first (or last, for backwards
	 * analyses) instruction in the given method.
	 * @param method the method to create the lattice operations for
	 * @return entry analysis information for analyzing a given method.
	 */
	public LE createEntryValue(MethodDeclaration method);

	/**
	 * Informs Crystal in which direction to perform the analysis.
	 * 
	 * @return the direction of the analysis; never <code>null</code>.
	 */
	public AnalysisDirection getAnalysisDirection();

}