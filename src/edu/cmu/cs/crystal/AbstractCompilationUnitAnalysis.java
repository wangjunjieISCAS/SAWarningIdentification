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
 * An ICrystal analysis which analyses each compilation unit as a whole.
 * 
 * @author David Dickey
 * 
 */
public abstract class AbstractCompilationUnitAnalysis implements ICrystalAnalysis {
	
	protected IAnalysisReporter reporter = null;
	protected IAnalysisInput analysisInput = null;
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * This implementation of runAnalysis will set the reporter and input and then
	 * call the abstract method $analyzeCompilationUnit.
	 * 
	 * @param compUnit The ITypeRoot that represents the .java or .class file we are analyzing
	 * @param reporter The IAnalysisReport that allows an analysis to report issues.
	 * @param rootNode The ASTNode which represents this compilation unit.
	 */
	public void runAnalysis(IAnalysisReporter reporter,
			IAnalysisInput input, ITypeRoot compUnit, 
			CompilationUnit rootNode) {
		this.reporter = reporter;
		this.analysisInput = input;
		analyzeCompilationUnit(rootNode);
		this.reporter = null;
		this.analysisInput = null;
	}

	public void afterAllCompilationUnits() {
		// default does nothing
	}

	public void beforeAllCompilationUnits() {
		// default does nothing
	}
	
	public IAnalysisReporter getReporter() {
		return reporter;
	}
	
	public IAnalysisInput getInput() {
		return analysisInput;
	}

	/**
	 * Invoked once for each compilation unit.
	 */
	public abstract void analyzeCompilationUnit(CompilationUnit d);
}
