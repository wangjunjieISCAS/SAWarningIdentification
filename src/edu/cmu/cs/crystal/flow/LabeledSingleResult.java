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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * This class maps a set of known labels to a single lattice element. This is different
 * from @link{SingleResult} because it will return a keyset which may contain more labels,
 * so it will preserve the independence of those branches.
 * 
 * @author Kevin Bierhoff
 * 
 * @param <LE>	the type that represents the lattice element
 */
public class LabeledSingleResult<LE> implements IResult<LE> {
	
	private Set<ILabel> labels;
	private LE singleValue;

	/**
	 * Creates a result that maps the given labels to the given lattice element.
	 * @param value The lattice element all given labels will map to.
	 * @param labels The labels known to this result.
	 */
	public static <LE> IResult<LE> createResult(LE value, Collection<ILabel> labels) {
		return new LabeledSingleResult<LE>(value, labels);
	}

	/**
	 * Creates a result that maps the given labels to the given lattice element.
	 * @param value The lattice element all given labels will map to.
	 * @param labels The labels known to this result.
	 */
	public static <LE> IResult<LE> createResult(LE value, ILabel... labels) {
		return new LabeledSingleResult<LE>(value, Arrays.asList(labels));
	}

	/**
	 * Creates a result that maps the given labels to the given lattice element.
	 * @param singleValue The lattice element all given labels will map to.
	 * @param labels The labels known to this result.
	 */
	public LabeledSingleResult(LE singleValue, Collection<ILabel> labels) {
		this.labels = new HashSet<ILabel>(labels.size());
		this.labels.addAll(labels);
		this.labels = Collections.unmodifiableSet(this.labels);
		this.singleValue = singleValue;
	}

	public Set<ILabel> keySet() {
		return labels;
	}

	/**
	 * @return the default value, regardless of the label requested.
	 */
	public LE get(ILabel label) {
		return singleValue;
	}

	public IResult<LE> join(IResult<LE> otherResult, IAbstractLatticeOperations<LE, ?> op) {
		LE otherLattice, thisLattice;
		LabeledResult<LE> mergedResult;
		Set<ILabel> mergedLabels = new HashSet<ILabel>();
		Set<ILabel> otherLabels = otherResult.keySet();
		
		mergedLabels.addAll(keySet());
		mergedLabels.addAll(otherResult.keySet());

		otherLattice = op.copy(otherResult.get(null));
		mergedResult = LabeledResult.createResult(op.join(op.copy(singleValue), otherLattice, null));
		
		for (ILabel label : mergedLabels) {
			if (otherLabels.contains(label) && labels.contains(label)) {
				otherLattice = op.copy(otherResult.get(label));
				thisLattice = op.copy(get(label));
				mergedResult.put(label, op.join(thisLattice, otherLattice, null));
			}
			else if (otherLabels.contains(label)) {
				otherLattice = op.copy(otherResult.get(label));
				mergedResult.put(label, otherLattice);
			}
			else {
				thisLattice = op.copy(get(label));
				mergedResult.put(label, thisLattice);
			}
		}
		
		return mergedResult;
	}
	
}

