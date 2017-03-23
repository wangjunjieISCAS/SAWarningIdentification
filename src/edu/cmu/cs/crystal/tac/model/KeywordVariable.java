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

import org.eclipse.jdt.core.dom.Name;

import edu.cmu.cs.crystal.tac.eclipse.EclipseTAC;

/**
 * Java and our three address code contain two 'special' variables that
 * correspond with Java keywords and must be treated in a different manner
 * than traditional source code variables.
 * 
 * These two variables are <code>super</code> and <code>this</code>. At the
 * very minimum, these variables are interesting in the sense that their
 * type changes depending upon which class the method we are examining is
 * contained within. 
 * 
 * <code>super</code> and <code>this</code> can be qualified, so methods
 * for returning this information are included as well.  <code>this</code>
 * could be an implicit; in this case, the qualifier information is not
 * guaranteed to be available.
 * 
 * @author Kevin Bierhoff
 *
 */
public abstract class KeywordVariable extends Variable {

	protected EclipseTAC tac;
	private Name qualifier;

	/**
	 * 
	 */
	protected KeywordVariable(EclipseTAC tac, Name qualifier) {
		super();
		this.tac = tac;
		this.qualifier = qualifier;
	}
	
	/**
	 * 
	 */
	protected KeywordVariable(EclipseTAC tac) {
		super();
		this.tac = tac;
	}
	
	/**
	 * Is this a standard keyword variable, or is it qualified?
	 * @return true if the keyword is qualified, false if it is not and represents the default keyword
	 */
	public boolean isQualified() {
		return getQualifier() != null;
	}
	
	/**
	 * Returns the qualifier.
	 * @return The qualifier or <code>null</code> if not qualified or qualifier not available (implicit qualification).
	 */
	public Name getQualifier() {
		return qualifier;
	}
	
	/**
	 * Sets the qualifier.
	 * @param qualifier The new qualifier.
	 * @see ThisVariable#explicitQualifier(Name)
	 */
	protected void setQualifier(Name qualifier) {
		this.qualifier = qualifier;
	}
	
	/**
	 * Which keyword does this variable represent?
	 * @return The keyword, in string form.
	 */
	public abstract String getKeyword();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(! isQualified())
			return getKeyword();
		if(getQualifier() != null)
			return getQualifier().getFullyQualifiedName() + "." + getKeyword();
		else if(resolveType() != null && "".equals(resolveType().getName()) == false)
			return resolveType().getName() + "." + getKeyword();
		else
			return "<Qualifier>." + getKeyword();
	}

}
