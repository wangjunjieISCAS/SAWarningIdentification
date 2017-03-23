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

import org.eclipse.jdt.core.dom.ASTNode;


/**
 * This is a degenerate lattice element class with exactly one element in it. The only purpose
 * for this class is a flow analysis where we want to walk through the flow, but have no
 * information to store.
 * 
 * @author Kevin Bierhoff
 *
 */
public enum SingletonLatticeElement {
	
	INSTANCE;
	
	public static ILatticeOperations<SingletonLatticeElement> SINGLETON_OPS = 
			new ILatticeOperations<SingletonLatticeElement>() {

		public boolean atLeastAsPrecise(SingletonLatticeElement info,
				SingletonLatticeElement reference, ASTNode node) {
			assert info != null && reference != null;
			return info == reference;
		}

		public SingletonLatticeElement bottom() {
			return SingletonLatticeElement.INSTANCE;
		}

		public SingletonLatticeElement copy(SingletonLatticeElement original) {
			assert original != null;
			return SingletonLatticeElement.INSTANCE;
		}

		public SingletonLatticeElement join(SingletonLatticeElement someInfo,
				SingletonLatticeElement otherInfo, ASTNode node) {
			assert someInfo != null && otherInfo != null;
			return SingletonLatticeElement.INSTANCE;
		}
	};

}
