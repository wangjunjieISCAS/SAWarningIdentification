/**
 * Copyright (c) 2006-2009 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,    
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
package edu.cmu.cs.crystal.simple;

import edu.cmu.cs.crystal.IAnalysisInput;
import edu.cmu.cs.crystal.tac.ITACFlowAnalysis;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;

/**
 * Simple flow analysis driver for a transfer function based on three-address code.
 * {@link ITACFlowAnalysis} defines methods to be used to query results based
 * on Eclipse AST nodes or TAC instructions.
 * @author Kevin Bierhoff
 * @since Crystal 3.4.1
 */
public class SimpleTACFlowAnalysis<LE> extends TACFlowAnalysis<LE> implements
		ITACFlowAnalysis<LE> {

	/**
	 * Creates a simple flow analysis with the given transfer function.
	 * @param transferFunction Transfer function to be used to compute results.
	 * @param analysisInput Analysis input passed into 
	 * {@link edu.cmu.cs.crystal.ICrystalAnalysis#runAnalysis}
	 */
	public SimpleTACFlowAnalysis(ITACTransferFunction<LE> transferFunction,
			IAnalysisInput analysisInput) {
		super(transferFunction, analysisInput);
	}

}
