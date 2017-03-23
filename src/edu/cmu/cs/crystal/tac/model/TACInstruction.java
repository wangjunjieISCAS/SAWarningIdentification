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
package edu.cmu.cs.crystal.tac.model;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;

/**
 * Abstract base class for 3-Address-Code instructions built from Eclipse AST nodes.
 * {@link ITACTransferFunction} lists subclasses that define the different
 * types of instructions.  Additional (abstract and/or package-private) classes 
 * simplify 3-Address-Code generation from AST nodes.  
 * 
 * @author Kevin Bierhoff
 *
 * @see ITACTransferFunction
 */
public interface TACInstruction {
	
	/**
	 * Returns the node this instruction is for.  Usually,
	 * one instruction exists per AST node, but can be more
	 * when AST nodes are desugared, such as for post-increment.
	 * 
	 * Subtypes may give more specific information on the type of
	 * AST node returned, but more specific typing is not guaranteed
	 * due to possible evolution of the Eclipse AST or these interfaces.
	 * 
	 * @return The AST node this instruction is for.
	 */
	public abstract ASTNode getNode();

	/**
	 * Use this method to transfer over an instruction.  This method performs double-dispatch
	 * to call the appropriate <code>transfer</code> method on the transfer function being
	 * passed.
	 * @param <LE> Lattice element used in the transfer function.
	 * @param tf Transfer function.
	 * @param value Incoming lattice value.
	 * @return Outgoing lattice value after transferring over this instruction.
	 */
	public abstract <LE> LE transfer(ITACTransferFunction<LE> tf, LE value);
	
	/**
	 * Use this method to transfer over an instruction.  This method performs double-dispatch
	 * to call the appropriate <code>transfer</code> method on the transfer function being
	 * passed.
	 * @param <LE> Lattice element used in the transfer function.
	 * @param tf Transfer function.
	 * @param labels Branch labels to consider.
	 * @param value Incoming lattice value.
	 * @return Outgoing lattice values for given labels after transferring over this instruction.
	 */
	public abstract <LE> IResult<LE> transfer(
			ITACBranchSensitiveTransferFunction<LE> tf, 
			List<ILabel> labels, 
			LE value);
	
}
