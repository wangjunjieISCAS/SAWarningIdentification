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
 * @author Nels E. Beckman
 */
public abstract class Option<T> {

	
	@SuppressWarnings("unchecked")
	private static final Option<?> NONE = new Option() {
		@Override public boolean isNone() { return true; }
		@Override public boolean isSome() {	return false; }
		@Override
		public Object unwrap() { throw new IllegalStateException("Unwrapped None."); }
		@Override public String toString() { return "NONE"; }
	};
	
	
	@SuppressWarnings("unchecked")
	public static <T> Option<T> none() {
		return (Option<T>)NONE;
	}
	
	public static <T> Option<T> some(final T t) {
		return new Option<T>(){
			@Override public boolean isNone() { return false;	}
			@Override public boolean isSome() { return true; }
			@Override public T unwrap() { return t; }
			@Override public String toString() { return "SOME(" + t.toString() + ")"; }
	    };
	}
	
	public static <T> Option<T> wrap(final T t) {
		if(t == null)
			return none();
		else
			return some(t);
	}
	
	public abstract T unwrap();
	
	public abstract boolean isSome();
	
	public abstract boolean isNone();
	
	
}
