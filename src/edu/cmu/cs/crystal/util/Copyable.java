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
package edu.cmu.cs.crystal.util;

/**
 * @author ciera
 * @since Crystal 3.4.0
 */
public interface Copyable<C extends Copyable<C>> {
	/**
	 * Creates a new deep copy of this C.  "Deep copy" means that all mutable (changeable)
	 * objects referenced by the original C, must not be referenced by the copied C. Notice that
	 * immutable references may be shallow copied, so a completely immutable type may just return this.
	 * 
	 * @return	a copy of this that contains no references to mutable objects found in the original  
	 */
	public C copy();
}
