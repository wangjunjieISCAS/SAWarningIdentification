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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps each label to a unique value. This class also contains a default value, so if
 * a label is requested which it does not know about, it returns the default.
 * 
 * Of all the IResults, this class provides the most expressiveness.
 * 
 * @author ciera
 * @since Crystal 3.4.0
 * @param <LE> The type which represents the lattice value
 */
public class LabeledResult<LE> implements IResult<LE> {
	private Map<ILabel, LE> labelMap;
	private LE defaultValue;
	
	/**
	 * Create a result for the given labels with the given default value.
	 * @param <LE>
	 * @param labels The labels to create mappings for
	 * @param defaultValue a default value for each label to map to
	 * @return A new result for the given labels with the given default value.
	 */
	public static <LE> LabeledResult<LE> 
	createResult(List<ILabel> labels, LE defaultValue) {
		return new LabeledResult<LE>(labels, defaultValue);
	}
	
	/**
	 * Create a result with the given default value but no labels. Labels can be added
	 * later by calling @link{#put}.
	 * @param <LE>
	 * @param defaultValue a default value for each label to map to
	 * @return A new result with the given default value.
	 */
	public static <LE> LabeledResult<LE> createResult(LE defaultValue) {
		return new LabeledResult<LE>(new LinkedList<ILabel>(), defaultValue);
	}
	
	private LabeledResult(List<ILabel> labels, LE defaultValue) {
		labelMap = new HashMap<ILabel, LE>();
		this.defaultValue = defaultValue;
		for( ILabel lab : labels ) {
			this.labelMap.put(lab, defaultValue);
		}
	}
	
	/**
	 * Add/Change the value of a label
	 * @param label the label to add to this result
	 * @param value the lattice information to map it to
	 */
	public void put(ILabel label, LE value) {
		labelMap.put(label, value);
	}
	
	/**
	 * @return the value which was mapped to this label, or the default value if the label
	 * is unknown
	 */
	public LE get(ILabel label) {
		LE value = labelMap.get(label);
		if (value == null)
			value = defaultValue;
		return value;
	}

	/**
	 * @return a set of all known labels for this result.
	 */
	public Set<ILabel> keySet() {
		return Collections.unmodifiableSet(labelMap.keySet());
	}

	public IResult<LE> join(IResult<LE> otherResult, IAbstractLatticeOperations<LE, ?> op) {
		LE otherLattice, thisLattice;
		LabeledResult<LE> mergedResult;
		Set<ILabel> mergedLabels = new HashSet<ILabel>();
		Set<ILabel> otherLabels = otherResult.keySet();
		
		mergedLabels.addAll(keySet());
		mergedLabels.addAll(otherResult.keySet());

		otherLattice = op.copy(otherResult.get(null));
		mergedResult = LabeledResult.createResult(op.join(op.copy(defaultValue), otherLattice, null));
		
		for (ILabel label : mergedLabels) {
			if (otherLabels.contains(label) && labelMap.containsKey(label)) {
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

