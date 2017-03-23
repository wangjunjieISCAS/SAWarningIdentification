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

import java.util.Set;


/**
 * Interface for mapping branch labels to analysis information.
 * Clients do not usually have to implement this interface.  Instead, use
 * one of the pre-defined implementing classes.
 * 
 * If you want a single lattice value regardless of labels and do not want to track labels, use @link{SingleResult}.
 * If you want a single lattice value but do want to track labels, use @link{LabeledSingleResult}.
 * If you want to provide different lattice values for each label, use @link{LabeledResult}.
 * 
 * @author Kevin Bierhoff
 * 
 * @param <LE>	the type that represents the analysis knowledge
 */
public interface IResult<LE> {

	/**
	 * Clients should not modify the returned value.
	 * Implementers must provide a default value if <code>label</code> is <code>null</code>.
	 * @param label
	 * @return A valid lattice element or <code>null</code> if the label
	 * is unknown.
	 */
	public LE get(ILabel label);
	
	/**
	 * Returns the set of labels mapped by this <code>IResult</code>.
	 * @return The set of labels mapped by this <code>IResult</code>.
	 * This method must not return <code>null</code>
	 */
	public Set<ILabel> keySet();
	
	/**
	 * Clients do not usually call this method.
	 * Implementations join two results "pointwise" by joining lattice elements with 
	 * the same label.  
	 * This method must not modify either <code>IResult</code>
	 * objects passed in.
	 * @param otherResult <code>IResult</code> object to join this <code>IResult</code> with.
	 * @param ops Lattice operations so we can join individual elements.
	 * @return Pointwise joined lattice elements.
	 * 
	 * @see ILatticeOperations#join
	 */
	public IResult<LE> join(IResult<LE> otherResult, IAbstractLatticeOperations<LE, ?> ops);
}
