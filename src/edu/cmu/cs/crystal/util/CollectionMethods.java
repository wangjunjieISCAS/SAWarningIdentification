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
package edu.cmu.cs.crystal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionMethods {

	/**
	 * Interface used for the method in a map call.
	 */
	public interface Mapping<I,O> {
		public O eval(I elem);
	}

	/**
	 * Functional map, returns a new list.
	 */
	public static <I,O> List<O> map(List<? extends I> list, Mapping<I,O> fun) {
		List<O> result = new ArrayList<O>(list.size());
		for( I elem : list ) {
			result.add(fun.eval(elem));
		}
		return result;
	}

	/**
	 * Concatenates two lists. Returns a brand new list and does not modify the original
	 * lists.
	 */
	public static <T> List<T> concat(List<? extends T> l1, List<? extends T> l2) {
		List<T> result = new ArrayList<T>(l1.size() + l2.size());
		result.addAll(l1);
		result.addAll(l2);
		return result;
	}
	
	/**
	 * Return the union of two maps without modifying either one.
	 */
	public static <K, V> Map<K,V> union(Map<? extends K, ? extends V> m1,
			                            Map<? extends K, ? extends V> m2) {
		Map<K,V> result = new HashMap<K,V>();
		result.putAll(m1);
		result.putAll(m2);
		return result;
	}
	
	/**
	 * Add an element to a 'multi-map.' Modifies the map in place.
	 */
	public static <K, V> void addToMultiMap(K key, V val, 
			Map<K, List<V>> map) {
		if( map.containsKey(key) ) {
			map.get(key).add(val);
		}
		else {
			List<V> l = new LinkedList<V>();
			l.add(val);
			map.put(key, l);
		}
	}
	
	public static <T> Set<T> createSetWithoutElement(Set<T> s, T element) {
		if(! s.contains(element))
			return s;
		LinkedHashSet<T> result = new LinkedHashSet<T>(s);
		result.remove(element);
		return result;
	}

	/**
	 * Creates a set from an array of elements (i.e., duplicate elements will be dropped).
	 */
	public static <T> Set<T> mutableSet(T... elements) {
		LinkedHashSet<T> elemSet = new LinkedHashSet<T>(elements.length);
		for(T e : elements) {
			elemSet.add(e);
		}
		return elemSet;
	}

}
