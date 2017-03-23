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
package edu.cmu.cs.crystal.flow.worklist;

import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.cfg.IControlFlowGraph;
import edu.cmu.cs.crystal.cfg.eclipse.EclipseNodeFirstCFG;
import edu.cmu.cs.crystal.flow.ILatticeOperations;

/**
 * @author Kevin Bierhoff
 *
 */
public abstract class AbstractWorklist<LE> extends WorklistTemplate<LE, ASTNode, ILatticeOperations<LE>> {
	
	private static final Logger log = Logger.getLogger(AbstractWorklist.class.getName());
	
	private final MethodDeclaration method;
	private final IProgressMonitor monitor;
	private int lastLine = -1;
	
	public AbstractWorklist(MethodDeclaration method) {
		this.method = method;
		this.monitor = null;
	}
	
	public AbstractWorklist(MethodDeclaration method, IProgressMonitor monitor) {
		this.method = method;
		this.monitor = monitor;
	}
	
	@Override
	protected IControlFlowGraph<ASTNode> getControlFlowGraph() {
		return new EclipseNodeFirstCFG(method);
	}

	/**
	 * Returns the analyzed method.
	 * @return the analyzed method.
	 */
	protected final MethodDeclaration getMethod() {
		return method;
	}

	/**
	 * Call this method to check if the given node hits a breakpoint.
	 * This method can be used by analysis writers during debugging.
	 * Setting a breakpoint at the point in the method that returns 
	 * <code>true</code> allows breaking into the debugger at a point
	 * in the analyzed code that was marked with a breakpoint in the child
	 * eclipse.  This method is probably not useful if Crystal is not 
	 * executed in debug mode.
	 * @param node
	 * @return <code>true</code> if the given node hits a breakpoint, <code>false</code> otherwise.
	 */
	protected final boolean checkBreakpoint(ASTNode node) {
		if(node == null)
			return false;
		final CompilationUnit compUnit = (CompilationUnit) node.getRoot();
		int nodeLine = compUnit.getLineNumber(node.getStartPosition());
		if(nodeLine < 0 || lastLine == nodeLine)
			// error getting the line number or last AST node seen was on the same line
			// no need to check/break again
			return false;
		lastLine = nodeLine;
		IResource r = compUnit.getJavaElement().getResource();
		try {
			// TODO Increase efficiency by filtering markers inside analyzed method up-front
			for(IMarker m : r.findMarkers(IBreakpoint.BREAKPOINT_MARKER, true, IResource.DEPTH_INFINITE)) {
				// see if one of the breakpoint markers for the analyzed resource is on the node's line
				if(((Integer) m.getAttribute(IMarker.LINE_NUMBER, 0)) == nodeLine) {
					if(log.isLoggable(Level.FINEST))
						log.finest("Hit breakpoint in " + r.getName() + " line " + nodeLine);
					// If you want to be notified of breakpoints in the analyzed program (child eclipse),
					// set a breakpoint for the following line in the parent eclipse
					return true;
				}
			}
		} 
		catch (CoreException e) {
			log.log(Level.WARNING, "Exception checking breakpoints for node " + node, e);
		}
		return false;
	}
	
	/**
	 * Call this method to check if the progress monitor was canceled.
	 * This method throws an exception if the monitor was canceled and
	 * returns normally otherwise.
	 * @throws CancellationException If progress monitor was canceled.
	 */
	protected final void checkCancel() {
		if(monitor != null && monitor.isCanceled())
			throw new CancellationException("Crystal flow analysis was canceled");
	}

}
