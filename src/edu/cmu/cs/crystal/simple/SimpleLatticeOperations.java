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
package edu.cmu.cs.crystal.simple;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;

/**
 * This is the interface for the operations you must provide on your lattice in order
 * for the dataflow analysis to work.
 * 
 * To make your own lattice operations, simply derive off of this class and templatize it by
 * the type of lattice you are going to use. So, to make operations for a null pointer lattice,
 * we could declare a class like this:
 * 
 * {@code public class NPELatticeOps extends SimpleLatticeOperations<NullPointerLattice>}
 *   
 * @author ciera
 * @author Kevin Bierhoff
 * @since Crystal 3.4.0
 */
public abstract class SimpleLatticeOperations<LE> implements ILatticeOperations<LE> {

	/**
	 * Checks to see if the left side of the operand is more precise (or as precise) as the right side.
	 * 
	 * Notice that in a lattice, the bottom lattice element will be more precise than everything, and top element will be less precise
	 * than everything. That is, {@code atLeastAsPrecise(bottom, x) == true} and {@code atLeastAsPrecise(x, top) == true}.
	 * 
	 * @param left
	 * @param right
	 * @return True if left is more precise (or the same as) right, false if right is more precise
	 * or the two have no precision relationship.
	 */
	abstract public boolean atLeastAsPrecise(LE left, LE right);
	
	/**
	 * Joins the two lattice elements into an element that is as precise as possible while still being less precise than both.
	 * This is a symmetric relationship.
	 * 
	 *  This method
	 * is called by the framework to join different flows together.  For
	 * example: The lattice that follows an If statement must represent
	 * the knowledge from the true and the false paths.  The join is what
	 * combines the analysis of each path together.
	 * 
	 * You may modify "this" and return it, or simply create a new LE
	 * and return it.
	 * 
	 * Notice that:
	 * <ul>
	 * <li> joining anything with bottom gives it back to you ({@code join(bottom, X) == X})
	 * <li> joining anything with top gives you top ({@code join(top, X) == top})
	 * <li> joining anything with itself gives it back to you ({@code join(X, X) == X})
	 * <li> the result of a join is less precise than the operands. If {@code join(A, B) == C},
	 *  then {@code atLeastAsPrecise(A, C) == true} and {@code atLeastAsPrecise(B, C) == true} 
	 * </ul>
	 * 
	 * @param left
	 * @param right DO NOT MODIFY
	 * @return A lattice element which is the join of the parameters.
	 */
	abstract public LE join(LE left, LE right);
	
	/**
	 * @return The lattice element which is the bottom of the lattice.
	 */
	abstract public LE bottom();
	
	/**
	 * Creates a new deep copy of the given analysis information.  
	 * "Deep copy" means that all mutable (changeable)
	 * objects referenced by the original 
	 * must not be referenced by the copy.
	 * 
	 * Notice that if your lattice is immutable (an {@code enum}, for example), then you
	 * can just return {@code this}
	 * 
	 * @param original analysis information to be copied.
	 * @return a copy of the argument that contains no references to mutable objects found in the original.  
	 */
	abstract public LE copy(LE original);

	/**
	 * More complex version of  atLeastAsPrecise. If you find you want to use this,
	 * you should implement {@code ILatticeOperations} instead.
	 * @see ILatticeOperations#atLeastAsPrecise(Object, Object, ASTNode)
	 */
	public boolean atLeastAsPrecise(LE info, LE reference, ASTNode node) {
		return atLeastAsPrecise(info, reference);
	}

	/**
	 * More complex version of join. If you find you want to use this,
	 * you should implement {@code ILatticeOperations} instead.
	 * @see ILatticeOperations#join(Object, Object, ASTNode)
	 */
	public LE join(LE someInfo, LE otherInfo, ASTNode node) {
		return join(someInfo, otherInfo);
	}
}
