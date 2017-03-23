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
 * @author Kevin Bierhoff
 *
 */
public class Pair<A, B> {
	
	private A component1;
	private B component2;

	public Pair() {
		super();
	}
	
	public Pair(A component1, B component2) {
		this.component1 = component1;
		this.component2 = component2;
	}

	public A fst() {
		return component1;
	}

	public void setComponent1(A component1) {
		this.component1 = component1;
	}

	public B snd() {
		return component2;
	}

	public void setComponent2(B component2) {
		this.component2 = component2;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Pair<A, B>(component1, component2);
	}

	@Override
	public String toString() {
		return "<" + component1 + "," + component2 + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((component1 == null) ? 0 : component1.hashCode());
		result = prime * result
				+ ((component2 == null) ? 0 : component2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (component1 == null) {
			if (other.component1 != null)
				return false;
		} else if (!component1.equals(other.component1))
			return false;
		if (component2 == null) {
			if (other.component2 != null)
				return false;
		} else if (!component2.equals(other.component2))
			return false;
		return true;
	}

	public static <A, B> Pair<A, B> create(A component1, B component2) {
		return new Pair<A, B>(component1, component2);
	}

}
