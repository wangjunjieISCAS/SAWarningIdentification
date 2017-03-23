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
package edu.cmu.cs.crystal.flow;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * This class implements a standard flow analysis.  
 * Implement {@link ITransferFunction} or {@link IBranchSensitiveTransferFunction}
 * and pass an instance to the respective constructor to create a specific
 * flow analysis.
 * 
 * @author Kevin Bierhoff
 *
 * @param <LE>	the LatticeElement subclass that represents the analysis knowledge
 * 
 * @see edu.cmu.cs.crystal.flow.ITransferFunction
 */
public class FlowAnalysis<LE> extends MotherFlowAnalysis<LE> {
	
	protected IFlowAnalysisDefinition<LE> def;

	public FlowAnalysis(ITransferFunction<LE> def) {
		this.def = def;
	}

	public FlowAnalysis(IBranchSensitiveTransferFunction<LE> def) {
		this.def = def;
	}

	@Override
	protected IFlowAnalysisDefinition<LE> createTransferFunction(MethodDeclaration method) {
		return def;
	}
}