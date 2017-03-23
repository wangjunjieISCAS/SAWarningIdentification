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

/**
 * That's right, a really simple map interface. The only thing you can do with
 * it is get things. Check out getInvariantPermissions in PluralTupleLatticeElement
 * to see how I use this to create a lazy map. Note that there is no real good reason
 * to have this interface now that we have Lambda, except for the possibly more
 * precise implication.
 * 
 * @author Nels Beckman
 *
 * @param <K>
 * @param <V>
 */
public interface SimpleMap<K, V> {
	public V get(K key);
}
