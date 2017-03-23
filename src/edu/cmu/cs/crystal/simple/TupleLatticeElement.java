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
package edu.cmu.cs.crystal.simple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Represents a lattice element that is a tuple of lattice elements for an underlying lattice.
 * Conceptually, this is a map from keys to individual lattice elements for each key.
 * Keys can be parameterized, but common keys are {@link org.eclipse.jdt.core.dom.ASTNode}s and 3-address code {@link edu.cmu.cs.crystal.tac.model.Variable}s. 
 * The representation is optimized: a default element is the assumed value for every key
 * that has not been explicitly set.
 * 
 * To use the TupleLatticeElement, create a TupleLatticeOperations.
 * 
 * <b>This lattice is mutable</b>; the {@link #put(Object, Object)} operation can be used to 
 * change its value.
 * 
 * @author aldrich
 * @author Kevin Bierhoff
 *
 * @param <K> The type of things that individual lattice elements are tracked for.
 * @param <LE>  The element type of the underlying lattice.
 */
public class TupleLatticeElement<K, LE> {

	protected final LE bot;
	protected final LE theDefault;
	// if elements==null, then this element is the bottom tuple lattice 
	protected final HashMap<K,LE> elements;
	
	/**
	 * Construct a tuple lattice.
	 * 
	 * @param b The bottom value for the underlying lattice
	 * @param d The default lattice value for the underlying lattice
	 * @see {@link TupleLatticeOperations#getDefault()}
	 */
	protected TupleLatticeElement(LE b, LE d, HashMap<K,LE> e) {
		bot = b;
		theDefault = d;
		elements = e;
	}

	/**
	 * Currently being used by Plural. Should not be used by anyone else
	 * @param b The bottom value for the underlying lattice
	 * @param d The default lattice value for the underlying lattice
	 * @see {@link TupleLatticeOperations#getDefault()}
	 */
	@Deprecated
	public TupleLatticeElement(LE b, LE d) {
		this(b, d, new HashMap<K,LE>());
	}
	
	/**
	 * Retrieves the lattice information for a given key.
	 * @param n The key for which lattice information is requested.
	 * @return bottom if this lattice is bottom,
	 * theDefault if n not found in map,
	 * or else the element at n in the map */
	public LE get(K n) {
		if (elements == null)
			return bot;
		LE elem = elements.get(n);
		if (elem == null)
			return theDefault;
		else
			return elem;
	}
	
	/** 
	 * Sets an element in the tuple.
	 * 
	 * @param n  The Variable for which we are setting the value
	 * @param l	 The value to set
	 * @return   The old value of the map
	 */
	public LE put(K n, LE l) {
		return elements.put(n,l);
	}
	
	/**
	 * Removes an element from the tuple.
	 * 
	 * @param n Element to be removed
	 * @return Lattice information previously stored in the tuple or <code>null</code>
	 * if element wasn't in the tuple.
	 */
	public LE remove(K n) {
		return elements.remove(n);
	}
	
	/** 
	 * Prints the lattice element as a map.  Only non-default values are included.
	 * @return String representing the tuple as a map.
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("[");
		Set<K> keys = new HashSet<K>(getKeySet());
		boolean isFirst = true;
		
		for (K key : keys) {
			if (!isFirst)
				buf.append(", ");
			isFirst = false;
			
			LE myLE = get(key);
			
			buf.append(key.getClass().getSimpleName())
				.append('{').append(key.toString()).append("}->")
				.append(myLE.toString());
		}
		
		buf.append(']');
		return buf.toString();
	}

	/**
	 * Returns the set of keys for which lattice information was previously stored.
	 * This set is backed by the tuple, thus removing elements from the set removes
	 * their entry in the tuple.
	 * @return The set of keys for which lattice information was previously stored.
	 * The result is never <code>null</code> but may be empty.
	 * @see java.util.Map#keySet()
	 */
	public Set<K> getKeySet() {
		if (elements == null)
			return new HashSet<K>();
		else
			return elements.keySet();
	}
}
