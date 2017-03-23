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


/**
 * This instruction indicates reading a variable that appears in the source program,
 * i.e. the receiver, locals, or parameters.  This instruction is even generated when
 * the source variable appears on the left-hand side of an assignment, to indicate that
 * the variable is being touched.
 * 
 * TODO Figure out if assignment targets should be a "SourceVariableRead" or not.
 *  
 * @author Kevin Bierhoff
 * @see AssignmentInstruction#getTarget()
 */
public interface SourceVariableReadInstruction extends TACInstruction {

	/**
	 * Returns the variable being read, of type {@link SourceVariable} or {@link KeywordVariable}.
	 * @return The variable being read, of type {@link SourceVariable} or {@link KeywordVariable}.
	 */
	public Variable getVariable();

}
