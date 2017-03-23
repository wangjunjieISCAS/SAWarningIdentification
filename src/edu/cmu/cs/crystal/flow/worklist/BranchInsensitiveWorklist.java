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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.cfg.ICFGNode;
import edu.cmu.cs.crystal.flow.AnalysisDirection;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.flow.ITransferFunction;
import edu.cmu.cs.crystal.flow.SingleResult;

/**
 * This is the branch-<b>in</b>sensitive version of the worklist algorithm.
 * Call {@link #performAnalysis()} to run the worklist.
 * @author Kevin Bierhoff
 * @see #checkBreakpoint(ASTNode) for breakpoint support
 * @see BranchSensitiveWorklist
 */
public class BranchInsensitiveWorklist<LE> extends AbstractWorklist<LE> {
	
	/** The analysis-specific transfer function. */
	private final ITransferFunction<LE> transferFunction;

	/**
	 * Creates a worklist instance for the given method and transfer function.
	 * @param method
	 * @param def
	 */
	public BranchInsensitiveWorklist(MethodDeclaration method, ITransferFunction<LE> def) {
		super(method);
		this.transferFunction = def;
	}

	/**
	 * Creates a worklist instance for the given method and transfer function.
	 * @param method
	 * @param monitor Monitor that will be checked for cancellation
	 * @param def
	 */
	public BranchInsensitiveWorklist(MethodDeclaration method, 
			IProgressMonitor monitor,
			ITransferFunction<LE> def) {
		super(method);
		this.transferFunction = def;
	}

	@Override
	protected AnalysisDirection getAnalysisDirection() {
		return transferFunction.getAnalysisDirection();
	}

	@Override
	protected ILatticeOperations<LE> getLatticeOperations() {
		return transferFunction.getLatticeOperations();
	}

	@Override
	protected LE getEntryValue() {
		return transferFunction.createEntryValue(getMethod());
	}

	@Override
	protected IResult<LE> transferNode(ICFGNode<ASTNode> cfgNode, LE incoming,
			ILabel transferLabel) throws CancellationException {
		// are we canceled?
		checkCancel();

		final ASTNode astNode = cfgNode.getASTNode();
		if(astNode == null) {
			// dummy node
			// return immediately using the incoming result 
			return new SingleResult<LE>(incoming);
		}

		// this is a "real" node
		checkBreakpoint(astNode);
		// TODO take advantage of transferLabel for &&, ||, and ! nodes
		return new SingleResult<LE>(transferFunction.transfer(astNode, incoming));
	}

}
