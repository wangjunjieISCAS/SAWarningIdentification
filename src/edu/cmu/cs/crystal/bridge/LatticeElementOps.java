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
package edu.cmu.cs.crystal.bridge;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;

/**
 * This class is not to be used in new Crystal analyses and is kept for
 * older projects only.
 * 
 * This class provides generic lattice operations for  
 * {@link LatticeElement} implementations.
 * This facilitates using classes implementing {@link LatticeElement}
 * in Crystal flow analyses.  
 * @author Kevin Bierhoff
 * @since Crystal 3.4.0
 */
public class LatticeElementOps<LE extends LatticeElement<LE>> implements ILatticeOperations<LE> {
	
	/**
	 * Create lattice operations for a given bottom element.
	 * Using this method requires less explicit type annotations that using
	 * the constructor directly.
	 * @param <LE> Analysis knowledge implementation class.
	 * @param bottom Bottom element to be returned by {@link #bottom()}.
	 * @return lattice operations for the given bottom element.
	 */
	public static 
	<LE extends LatticeElement<LE>> 
	LatticeElementOps<LE> create(LE bottom) {
		return new LatticeElementOps<LE>(bottom);
	}
	
	private final LE bottom;

	/**
	 * Use {@link LatticeElementOps#create(LatticeElement)} for more compact syntax.
	 * Create lattice operations for a given bottom element.
	 * @param bottom Bottom element to be returned by {@link #bottom()}.
	 */
	public LatticeElementOps(LE bottom) {
		this.bottom = bottom;
	}

	public boolean atLeastAsPrecise(LE info, LE reference, ASTNode node) {
		return info.atLeastAsPrecise(reference, node);
	}

	public LE bottom() {
		return bottom.copy();
	}

	public LE copy(LE original) {
		return original.copy();
	}

	public LE join(LE someInfo, LE otherInfo, ASTNode node) {
		return someInfo.join(otherInfo, node);
	}

}
