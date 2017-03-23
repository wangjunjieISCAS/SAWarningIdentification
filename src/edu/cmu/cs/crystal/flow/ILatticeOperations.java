/**
 * Copyright (c) 2006-2009 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,    
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

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Implement this interface to provide typical lattice operations.
 * Instances of this interface are used by Crystal flow analyses to compare
 * and join analysis-specific information.
 * This interface is parameterized by the type used to represent values in
 * the lattice.
 * Notice that there are no assumptions being made about the lattice values,
 * which allows using existing types such as {@link java.util.Set}.
 * Crystal encourages including analysis-specific operations into classes
 * implementing this interfaces, but this is not required.
 * 
 * Lattices are defined with four methods:
 * <ul>
 * <li>{@link #bottom()} represents the most precise lattice value.  (There is
 * no direct way of acquiring Top.)
 * <li>{@link #atLeastAsPrecise(Object, Object, ASTNode)} compares two lattice values.
 * <li>{@link #join(Object, Object, ASTNode)} approximates two lattice values
 * by one that is less precise than both given.
 * <li>{@link #copy(Object)} duplicates a lattice value.  This is an implementation
 * device that is needed because flow analyses derive lattice values for new program
 * points from lattice values for program points that were already analyzed.
 * </ul>
 * 
 * @param <LE> Analysis information being tracked.
 * @author Kevin Bierhoff
 * @since Crystal 3.4.0
 */
public interface ILatticeOperations<LE> extends IAbstractLatticeOperations<LE, ASTNode> {
	
	/**
	 * Responsible for returning a lattice that represents no knowledge.
	 * 
	 * @return		the lattice that represents "bottom"
	 */
	public LE bottom();

	/**
	 * Carries out a join on this lattice and another lattice.  This method
	 * is called by the framework to join different flows together.  For
	 * example: The lattice that follows an If statement must represent
	 * the knowledge from the true and the false paths.  The join is what
	 * combines the analysis of each path together.
	 * 
	 * You may modify "this" and return it, or simply create a new LE
	 * and return it.
	 * 
	 * @param someInfo LE to join with <code>otherInfo</code>.
	 * @param otherInfo	The other LE to join with, do not modify.
	 * @param node ASTNode where the two paths were originally forked apart (e.g., if, 
	 * while, try, switch, etc.) or <code>null</code> if this join occurs on a "dummy" node.
	 * @return	the resulting LE that has the combined knowledge
	 */
	public LE join(LE someInfo, LE otherInfo, ASTNode node);
	
	/**
	 * Compares analysis information for precision; more precisely,
	 * determines whether the first argument is at least as precise as the second.  
	 * This method is used by
	 * the framework to compare analysis information.
	 *  
	 * @param info Analysis information to be compared against <code>reference</code>
	 * @param reference	the other LE to compare <code>info</code> with.
	 * @param node ASTNode where the two paths were originally forked apart (e.g., if, 
	 * while, try, switch, etc.) or <code>null</code> if this comparison occurs on a "dummy" node.
	 * @return <code>true</code> if the first argument is at least as precise as the
	 * second; <code>false</code> otherwise, including if the two arguments are incomparable.
	 */
	public boolean atLeastAsPrecise(LE info, LE reference, ASTNode node);
	
	/**
	 * Creates a new deep copy of the given analysis information.  
	 * "Deep copy" means that all mutable (changeable)
	 * objects referenced by the given LE 
	 * must not be referenced by the returned LE.
	 * 
	 * @param original analysis information to be copied.
	 * @return a copy of the argument that contains no references 
	 * to mutable objects found in the original.  
	 */
	public LE copy(LE original);
	
}
