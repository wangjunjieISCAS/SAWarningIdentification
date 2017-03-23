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

import java.util.Collections;
import java.util.Set;


/**
 * This is a degenerate result that maps all labels to a
 * single lattice element and only knows a single label,
 * {@link NormalLabel}.
 * 
 * This is a useful class if you want to return the same lattice information no matter
 * what label is used. It is the least expressive as it does not track labels, but simple.
 * 
 * @author Kevin "The German" Bierhoff
 * 
 * @param <LE>	the type that represents the analysis knowledge
 */
public class SingleResult<LE> implements IResult<LE> {
		private LE singleValue;
	private static final Set<ILabel> normalLabelSet = Collections.singleton((ILabel) NormalLabel.getNormalLabel());

	/**
	 * Create a result that maps all labels to the given lattice element.
	 * @param singleValue The single lattice element all labels will map to.
	 */
	public SingleResult(LE singleValue) {
		this.singleValue = singleValue;
	}

	/**
	 * @return the single value given in initially, no matter what label is requested.
	 */
	public LE get(ILabel label) {
		return singleValue;
	}

	/**
	 * @return a keyset that contains only the normal label.
	 */
	public Set<ILabel> keySet() {
		return normalLabelSet;
	}

	public IResult<LE> join(IResult<LE> otherResult, IAbstractLatticeOperations<LE, ?> op) {
		if (otherResult instanceof SingleResult) {
			return new SingleResult<LE>(
					op.join(op.copy(singleValue), op.copy(((SingleResult<LE>) otherResult).singleValue), null));
		}
		LE otherLattice, mergedLattice;
		LabeledResult<LE> mergedResult;
		
		otherLattice = op.copy(otherResult.get(null));
		mergedResult = LabeledResult.createResult(op.join(op.copy(singleValue), otherLattice, null));
		
		for (ILabel label : mergedResult.keySet()) {
			otherLattice = op.copy(otherResult.get(label));
			mergedLattice = op.join(op.copy(singleValue), otherLattice, null);
			mergedResult.put(label, mergedLattice);
		}
		
		return mergedResult;
	}
}
