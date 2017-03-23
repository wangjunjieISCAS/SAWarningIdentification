/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman, Kevin
 * Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 * 
 * This file is part of Crystal.
 * 
 * Crystal is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Crystal is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Crystal. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.annotations;

import java.util.HashMap;
import java.util.Map;

/**
 * The simplest kind of ICrystalAnnotation. It simply maps all parameters to an object. If
 * you would instead like to do more advanced parsing of an annotation, you must create
 * your own ICrystalAnnotation.
 * 
 * @author ciera
 * @since Crystal 3.4.0
 */
public class CrystalAnnotation implements ICrystalAnnotation {
	private String name;
	private Map<String, Object> pairs;

	public CrystalAnnotation(String name) {
		this.name = name;
		pairs = new HashMap<String, Object>();
	}

	public CrystalAnnotation() {
		pairs = new HashMap<String, Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cmu.cs.crystal.annotations.ICrystalAnnotation#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cmu.cs.crystal.annotations.ICrystalAnnotation#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cmu.cs.crystal.annotations.ICrystalAnnotation#getObject(java.lang.String)
	 */
	public Object getObject(String key) {
		Object obj = pairs.get(key);

		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cmu.cs.crystal.annotations.ICrystalAnnotation#setObject(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setObject(String key, Object value) {
		pairs.put(key, value);
	}
}
