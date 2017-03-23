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
 * A n-tuple where n is three.
 * 
 * @author Nels Beckman

 * @see edu.cmu.cs.crystal.util.Pair
 *
 */
public final class Triple<F,S,T> {

	private final F fst;
	private final S snd;
	private final T thrd;
	
	public static <F,S,T> Triple<F,S,T> createTriple(F f, S s, T t) {
		return new Triple<F,S,T>(f,s,t);
	}
	
	public Triple(F f, S s, T t) {
		fst = f;
		snd = s;
		thrd = t;
	}
	
	public F fst() {
		return fst;
	}
	
	public S snd() {
		return snd;
	}
	
	public T thrd() {
		return thrd;
	}
}
