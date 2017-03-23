/* Copyright (c) 2006-2009 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,    
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

/**
 * This package contains classes and interfaces for writing flow analyses
 * based on three-address code (TAC).
 * If you are writing a flow analysis, you may find
 * TAC easier to use that the Eclipse AST. 
 * TAC has no sub-expressions and uses temporary variables to break up
 * complex expressions in the AST into simpler expressions.
 * 
 * To create a flow analysis using TAC, 
 * implement either {@link edu.cmu.cs.crystal.tac.ITACTransferFunction} or 
 * {@link edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction}.
 */
package edu.cmu.cs.crystal.tac;