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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.IBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.ITransferFunction;
import edu.cmu.cs.crystal.flow.MotherFlowAnalysis;

/**
 * Factory for worklist objects to be used by flow analysis implementations.
 * Worklist objects can perform conventional or branch-sensitive flow analyses.
 * 
 * @author Kevin Bierhoff
 * @see MotherFlowAnalysis
 */
public class WorklistFactory {
	
	private IProgressMonitor monitor;

	/**
	 * Default worklist factory.
	 * @see #setMonitor(IProgressMonitor) to use a cancellation monitor.
	 */
	public WorklistFactory() {
		this.monitor = null;
	}
	
	/**
	 * Use the given progress monitor to listen to cancellation in
	 * subsequently created worklist instances.
	 * @param monitor Monitor to listen to cancellation or <code>null</code>
	 * if worklists should not be cancelled.
	 */
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Creates a worklist object that performs a conventional flow analysis on the given method
	 * with the given transfer function.
	 * @param <LE>
	 * @param method
	 * @param transferFunction
	 * @return Worklist object that performs a conventional flow analysis.
	 * @see #createBranchSensitiveWorklist(MethodDeclaration, IBranchSensitiveTransferFunction)
	 */
	public <LE> WorklistTemplate<LE, ASTNode, ILatticeOperations<LE>> createBranchInsensitiveWorklist(
			MethodDeclaration method,
			ITransferFunction<LE> transferFunction) {
		return new BranchInsensitiveWorklist<LE>(method, monitor, transferFunction);
	}

	/**
	 * Creates a worklist object that performs a branch-sensitive flow analysis on the given method
	 * with the given transfer function.  Branch sensitivity means that the analysis will maintain
	 * separate information about different outcomes of a given AST node, such as boolean tests or
	 * exceptional control flow.
	 * @param <LE>
	 * @param method
	 * @param transferFunction
	 * @return Worklist object that performs a branch-sensitive flow analysis.
	 */
	public <LE> WorklistTemplate<LE, ASTNode, ILatticeOperations<LE>> createBranchSensitiveWorklist(
			MethodDeclaration method,
			IBranchSensitiveTransferFunction<LE> transferFunction) {
		return new BranchSensitiveWorklist<LE>(method, monitor, transferFunction);
	}

}
