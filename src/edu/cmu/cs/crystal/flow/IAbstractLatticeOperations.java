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

/**
 * Analysis writers do not implement this interface directly; instead, they implement
 * an extending interface that instantiates the node type parameter, <code>N</code> of
 * this interface.
 * This interface allows implementing a worklist algorithm independently from the type
 * of node being transferred over.
 * Implementers of flow analyses over concrete node types should create a sub-interface
 * that instantiates the node type parameter.  
 * {@link ILatticeOperations} is such a sub-interface for flow analyses over Eclise AST nodes.
 * {@link edu.cmu.cs.crystal.flow.worklist.WorklistTemplate} can be used as the basis for 
 * flow analyses over other kinds of nodes.
 * @param <LE> Analysis information being tracked.
 * @param <N> Nodes our lattice information is computed over
 * @author Kevin Bierhoff
 * @since Crystal 3.4.0
 * @see edu.cmu.cs.crystal.flow.worklist.WorklistTemplate
 * @see ILatticeOperations
 */
public interface IAbstractLatticeOperations<LE, N> {

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
	public LE join(LE someInfo, LE otherInfo, N node);
	
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
	public boolean atLeastAsPrecise(LE info, LE reference, N node);
	
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
