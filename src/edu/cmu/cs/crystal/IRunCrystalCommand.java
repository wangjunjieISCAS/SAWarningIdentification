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
package edu.cmu.cs.crystal;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jdt.core.ITypeRoot;


/**
 * A command to run certain crystal analyses on certain files. Should not require
 * any internal knowledge of Crystal so that it can be created by Eclipse buttons
 * and menus. Internally, Crystal will translate a command to run analyses into
 * actual jobs.
 * 
 * @author Nels E. Beckman
 */
public interface IRunCrystalCommand {

	/**
	 * A set of analyses to run. Could be run in any order.
	 */
	public Set<String> analyses();
	
	/**
	 * A list of compilation units that the analyses will be run on. Will
	 * be run in order.
	 */
	public Collection<? extends ITypeRoot> compilationUnits();
	
	/**
	 * The reporter to be used.
	 */
	public IAnalysisReporter reporter();
}
