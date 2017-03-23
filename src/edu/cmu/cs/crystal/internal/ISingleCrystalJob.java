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
package edu.cmu.cs.crystal.internal;

import edu.cmu.cs.crystal.annotations.AnnotationDatabase;

/**
 * An interface for a single analysis job. A single analysis job is the running
 * of any number of analysis on one compilation unit. In the future, this may also 
 * manage dependencies in some more meaningful way.
 * 
 * @author Nels E. Beckman
 */
public interface ISingleCrystalJob {
	
	/**
	 * Run this single analysis. The given annotation database can be used to
	 * poll for annotations.
	 */
	public void run(AnnotationDatabase annoDB);
}
