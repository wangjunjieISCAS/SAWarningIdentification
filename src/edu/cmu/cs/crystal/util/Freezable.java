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
 * An interface for mutable objects that can be made immutable by freezing them.
 * 
 * @author Nels Beckman
 *
 */
public interface Freezable<T> {

	/**
	 * Create a copy of this object that can be modified.
	 * 
	 * @return A mutable copy of this object.
	 */
	public T mutableCopy();
	
	/**
	 * Freeze the state of this object so that future modifying calls are
	 * disallowed.
	 * 
	 * @return This newly frozen object.
	 */
	public T freeze();
	
}
