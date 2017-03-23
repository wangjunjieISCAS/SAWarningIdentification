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
package edu.cmu.cs.crystal.tac.eclipse;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * @author nbeckman
 * @since 3.3.1
 */
public class CompilationUnitTACs {

	private final Map<IMethodBinding, EclipseTAC> tacs;
	
	public CompilationUnitTACs() {
		this.tacs = new HashMap<IMethodBinding, EclipseTAC>();
	}
	
	public synchronized EclipseTAC getMethodTAC(MethodDeclaration methodDecl) {
		EclipseTAC tac;
		IMethodBinding methodBinding = methodDecl.resolveBinding();
		// try to reuse existing TAC instructions for this method
		tac = tacs.get(methodBinding);
		if(tac == null) {
			tac = new EclipseTAC(methodBinding);
			tacs.put(methodBinding, tac);
		}
		return tac;
	}
	
}
