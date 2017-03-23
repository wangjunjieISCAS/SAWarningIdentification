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
package edu.cmu.cs.crystal.internal;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.cmu.cs.crystal.IAnalysisReporter;

/**
 * An analysis reporter to be used when running analyses through Eclipse. All methods will
 * print/output to the standard locations in Eclipse.
 * 
 * @author Nels E. Beckman
 */
public class StandardAnalysisReporter implements IAnalysisReporter {

	public static final String REGRESSION_LOGGER = "edu.cmu.cs.crystal.regression";
	private static final Logger logger = Logger.getLogger(Crystal.class.getName());
	private static final Logger regressionLogger = Logger.getLogger(REGRESSION_LOGGER);

	public void clearMarkersForCompUnit(ITypeRoot compUnit) {
		try {
			IResource resource = compUnit.getResource();
			if (resource == null)
				// this happens with external libraries like the Java runtime library
				logger.warning("Cannot clear markers in " + compUnit);
			else
				resource.deleteMarkers(
				    Crystal.MARKER_DEFAULT, true, IResource.DEPTH_INFINITE);
		}
		catch (CoreException ce) {
			logger.log(Level.SEVERE, "CoreException when removing markers", ce);
		}
	}

	public PrintWriter debugOut() {
		return new PrintWriter(System.out, true);
	}

	public PrintWriter userOut() {
		UserConsoleView consoleView = UserConsoleView.getInstance();
		if (consoleView == null) {
			// TODO: Automatically enable the user console or make this a pop-up on the child
			// eclipse
			return new PrintWriter(System.out, true);
		}
		return consoleView.getPrintWriter();
	}

	public void reportUserProblem(String problemDescription, ASTNode node, String analysisName) {
		reportUserProblem(problemDescription, node, analysisName, SEVERITY.INFO);
	}

	public void reportUserProblem(String problemDescription, ASTNode node, String analysisName,
	    SEVERITY severity) {
		if (node == null)
			throw new NullPointerException("null ASTNode argument in reportUserProblem");
		if (analysisName == null)
			throw new NullPointerException("null analysis argument in reportUserProblem");
		logger.fine("Reporting problem to user: " + problemDescription + "; node: " + node);
		regressionLogger.info(problemDescription);
		regressionLogger.info(node.toString());

		IResource resource = null;
		ASTNode root = node.getRoot();
		String prefix = null;

		// Identify the closest resource to the ASTNode,
		// otherwise fall back to using the high-level workspace root.
		if (root != null && root.getNodeType() == ASTNode.COMPILATION_UNIT) {
			CompilationUnit cu = (CompilationUnit) root;
			IJavaElement je = cu.getJavaElement();
			resource = je.getResource();
			// print type root name into message if no resource
			prefix = resource == null ? 
					"[" + analysisName + " in " + je.getElementName() + "]" : 
					"[" + analysisName + "]"; 
		}
		if (resource == null)
			// Fall back to the high-level Workspace
			resource = ResourcesPlugin.getWorkspace().getRoot();
		if (prefix == null)
			prefix = "[" + analysisName + " in ???]";

		int sevMarker;

		if (severity == SEVERITY.ERROR)
			sevMarker = IMarker.SEVERITY_ERROR;
		else if (severity == SEVERITY.WARNING)
			sevMarker = IMarker.SEVERITY_WARNING;
		else
			sevMarker = IMarker.SEVERITY_INFO;

		// Create the marker
		// TODO: create markers according to the type of the analysis
		try {
			IMarker marker = resource.createMarker(Crystal.MARKER_DEFAULT);
			marker.setAttribute(IMarker.CHAR_START, node.getStartPosition());
			marker.setAttribute(IMarker.CHAR_END, node.getStartPosition() + node.getLength());
			marker.setAttribute(IMarker.MESSAGE, prefix + ": " + problemDescription);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			marker.setAttribute(IMarker.SEVERITY, sevMarker);
			marker.setAttribute(Crystal.MARKER_ATTR_ANALYSIS, analysisName);
			CompilationUnit cu = (CompilationUnit) node.getRoot();
			int line = cu.getLineNumber(node.getStartPosition());
			if (line >= 0) // -1 and -2 indicate error conditions
				marker.setAttribute(IMarker.LINE_NUMBER, line);
		}
		catch (CoreException ce) {
			logger.log(Level.SEVERE, "CoreException when creating marker", ce);
		}
	}
}
