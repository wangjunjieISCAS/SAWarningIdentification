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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.internal.WorkspaceUtilities;

/**
 * An ICrystalAnalysis which runs on each method (and constructor) of the class.
 * 
 * @author David Dickey
 * @author Jonathan Aldrich
 * 
 */
public abstract class AbstractCrystalMethodAnalysis implements ICrystalAnalysis {

	private static final Logger logger = Logger.getLogger(AbstractCrystalMethodAnalysis.class.getName());

	/** Object used to report errors */
	protected IAnalysisReporter reporter = null;
	/** Auxiliary analysis input */
	protected IAnalysisInput analysisInput = null;
	
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * {@link #beforeAllMethods} is run before any method is analyzed.<br/>
	 * Then each method is analysed by {@link #analyzeMethod(MethodDeclaration)}.<br/>
	 * Finally {@link #afterAllMethods} is run after all methods have
	 * been analyzed.
	 * @param reporter {@inheritDoc}
	 * @param input {@inheritDoc}
	 * @param compUnit {@inheritDoc}
	 * @param rootNode {@inheritDoc}
	 */
	public final void runAnalysis(IAnalysisReporter reporter,
			IAnalysisInput input, ITypeRoot compUnit, 
			CompilationUnit rootNode) {
		this.reporter = reporter;
		this.analysisInput = input;
		
		try {
			beforeAllMethods(compUnit, rootNode);
			
			RuntimeException err = null;
			List<MethodDeclaration> methods = WorkspaceUtilities.scanForMethodDeclarationsFromAST(rootNode);
			for (MethodDeclaration md : methods) {
				// TODO automatically poll for cancel here?  call afterAllMethods or not?
				try {
					analyzeMethod(md);
				}
				catch (RuntimeException e) {
					// analyze the remaining methods anyway
					// don't catch errors so we terminate asap
					err = e;
					logger.log(Level.SEVERE, "Analysis " + getName() + " had an error in " + md.resolveBinding().getDeclaringClass().getQualifiedName() + " when analyzing " + md.resolveBinding().toString(), e);
				}
			}
			
			afterAllMethods(compUnit, rootNode);
			
			if (err != null)
				// re-throw latest exception, if any, so user gets notified
				throw err;
		}
		finally {
			this.reporter = null;
			this.analysisInput = null;
		}
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
	 * This method is invoked once before any methods are analyzed. 
	 * It can be used to perform pre-analysis functionality, if needed.
	 */
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
	}

	/**
	 * Invoked for each method or constructor in the class.
	 * @param d method or constructor AST node to be analyzed
	 */
	public abstract void analyzeMethod(MethodDeclaration d);

	/**
	 * This method is invoked once after all methods are analyzed. 
	 * It can be used to perform post-analysis functionality, if needed.
	 */
	public void afterAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
	}
}
