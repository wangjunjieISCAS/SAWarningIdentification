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
package edu.cmu.cs.crystal.bridge;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.util.Copyable;

/**
 * This class is not to be used in new Crystal analyses and is kept for
 * older projects only.
 * 
 * A LatticeElement embodies the analysis knowledge at a particular point in the
 * program.
 * {@link LatticeElementOps} allows using classes implementing this interface
 * in a Crystal flow analysis.  
 * 
 * For simple lattices, this interface may be sufficient.
 * This interface is in particular useful for lattices which have
 * a fixed number of elements in them.
 * More sophisticated lattices are probably easier to implement using
 * {@link edu.cmu.cs.crystal.flow.ILatticeOperations} directly. 
 * Furthermore, lattices based on existing datatypes such as {@link java.util.Set}
 * are more easily implemented with {@link edu.cmu.cs.crystal.flow.ILatticeOperations}.
 * 
 * @author David Dickey
 * @author Jonathan Aldrich
 * 
 * @param <LE>	the LatticeElement implementation that represents the analysis knowledge
 */
public interface LatticeElement<LE extends LatticeElement<LE>> extends Copyable<LE> {
	
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
	 * @param other	The other LE to join with, do not modify.
	 * @param node ASTNode where the two paths were originally forked apart (e.g., if, 
	 * while, try, switch, etc.) or <code>null</code> if this join occurs on a "dummy" node.
	 * @return	the resulting LE that has the combined knowledge
	 */
	public abstract LE join(LE other, ASTNode node);
	
	/**
	 * Compares LatticeElements for precision.  This method is used by
	 * the framework to compare two LatticeElements.
	 *  
	 * @param other	the other LE to compare
	 * @param node ASTNode where the two paths were originally forked apart (e.g., if, 
	 * while, try, switch, etc.) or <code>null</code> if this comparison occurs on a "dummy" node.
	 * @return	true if this is at least as precise.  false if other is more precise
	 */
	public abstract boolean atLeastAsPrecise(LE other, ASTNode node);	
}
