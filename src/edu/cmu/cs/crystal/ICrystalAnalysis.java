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

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * The primary interface to create a Crystal analysis. To create a Crystal plugin, implement
 * this interface (or extend a subtype). Then register the analysis in the plugin.xml file by using
 * the extension-point edu.cmu.cs.crystal.CrystalAnalysis.
 * 
 * @author David Dickey
 * @author Jonathan Aldrich
 */
public interface ICrystalAnalysis {
	/**
	 * Run the analysis!
	 * @param reporter The object that is used to report errors. Output.
	 * @param input The input to this analysis.
	 * @param compUnit The compilation unit
	 * @param rootNode The root ASTNode of the compilation unit
	 */
	public void runAnalysis(IAnalysisReporter reporter,	
			IAnalysisInput input, ITypeRoot compUnit, 
			CompilationUnit rootNode);
	
	/**
	 * @return a unique name for this analysis. This name will be used by Crystal for menu
	 * items, error reporting, and otherwise identifying this analysis to the user.
	 */
	public String getName();
	
	/**
	 * Inform the analysis that all compilation units have been analyzed.
	 */
	public void afterAllCompilationUnits();

	/**
	 * Inform the analysis that the analysis process is about to begin.
	 */
	public void beforeAllCompilationUnits();
	
	/**
	 * @return the IAnalysisReporter that is being used for the current run of the analysis,
	 * or null if there is not currently an analysis being run.
	 */
	public IAnalysisReporter getReporter();
	
	/**
	 * @return the IAnalysisInput that is being used for the current run of the analysis,
	 * or null if there is not currently an analysis being run.
	 */
	public IAnalysisInput getInput();
}
