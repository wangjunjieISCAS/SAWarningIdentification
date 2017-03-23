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

import org.eclipse.core.runtime.IProgressMonitor;

import edu.cmu.cs.crystal.annotations.AnnotationDatabase;
import edu.cmu.cs.crystal.tac.eclipse.CompilationUnitTACs;
import edu.cmu.cs.crystal.util.Option;

/**
 * This interface holds input and data structures that the analysis may need during its run.
 * 
 * @author Nels E. Beckman
 */
public interface IAnalysisInput {

	/**
	 * @return the AnnotationDatabase that was populated on all the compilation
	 * units which will be analyzed.
	 */
	public AnnotationDatabase getAnnoDB();
	
	/**
	 * @return A cache of the TACs for every method declaration, if it is available.
	 */
	public Option<CompilationUnitTACs> getComUnitTACs();

	/**
	 * @return A progress monitor for canceling the ongoing
	 * analysis, or {@link Option#none()} if it cannot be canceled.
	 * An analysis might wish to cancel the analysis if it hits an error
	 * which will cause all further results to be invalid.
	 */
	public Option<IProgressMonitor> getProgressMonitor();
}
