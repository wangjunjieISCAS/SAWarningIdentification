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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;

/**
 * Lattice operations for the TupleLatticeElement. To use TupleLatticeElement, create a
 * TupleLatticeOperations with the appropriate lattice operations for the value lattice element
 * and the default.
 * 
 * @author ciera
 * @since Crystal 3.4.0
 */
public class TupleLatticeOperations<K, LE> implements ILatticeOperations<TupleLatticeElement<K, LE>> {
	protected final LE theDefault;
	protected final ILatticeOperations<LE> elementOps;

	/**
	 * @param operations The operations for the LE lattice
	 * @param defaultElement The default LE, to be used when we have a 
	 */
	public TupleLatticeOperations(ILatticeOperations<LE> operations, LE defaultElement) {
		theDefault = defaultElement;
		elementOps = operations;
	}
	
	public boolean atLeastAsPrecise(TupleLatticeElement<K, LE> left, TupleLatticeElement<K, LE> right, ASTNode node) {
		Set<K> keys = new HashSet<K>(left.getKeySet());
		keys.addAll(right.getKeySet());

		// elementwise comparison: return false if any element is not atLeastAsPrecise
		for (K key : keys) {
			LE leftLE = left.get(key);
			LE rightLE = right.get(key);
			if (!elementOps.atLeastAsPrecise(leftLE, rightLE, node))
				return false;
		}
		
		return true;
	}

	public TupleLatticeElement<K, LE> bottom() {
		return new TupleLatticeElement<K, LE>(elementOps.bottom(), elementOps.copy(theDefault), null);
	}

	public TupleLatticeElement<K, LE> copy(TupleLatticeElement<K, LE> original) {
		
		if(original.elements == null)
			return new TupleLatticeElement<K, LE>(elementOps.bottom(), elementOps.copy(theDefault), null);
		HashMap<K, LE> elemCopy = new HashMap<K, LE>(original.elements.size());
		for(K x : original.elements.keySet()) {
			LE elementValue = original.elements.get(x);
			elemCopy.put(x, elementOps.copy(elementValue));
		}
		return new TupleLatticeElement<K, LE>(elementOps.bottom(), elementOps.copy(theDefault), elemCopy);
	}

	public TupleLatticeElement<K, LE> join(TupleLatticeElement<K, LE> left,	TupleLatticeElement<K, LE> right, ASTNode node) {
		HashMap<K,LE> newMap = new HashMap<K,LE>();

		Set<K> keys = new HashSet<K>(left.getKeySet());
		keys.addAll(right.getKeySet());
		
		// join the tuple lattice by joining each element
		for (K key : keys) {
			LE leftLE = left.get(key);
			LE rightLE = right.get(key);
			LE newLE = elementOps.join(leftLE, rightLE, node);
			newMap.put(key, newLE);
		}

		return new TupleLatticeElement<K, LE>(elementOps.bottom(), elementOps.copy(theDefault), newMap);
	}
	
	/**
	 * @return a default tuple lattice element which maps every key to the default value.
	 */
	public TupleLatticeElement<K, LE> getDefault() {
		return new TupleLatticeElement<K, LE>(elementOps.bottom(), elementOps.copy(theDefault), new HashMap<K, LE>());
	}
}
