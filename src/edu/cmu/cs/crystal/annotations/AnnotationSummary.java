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
package edu.cmu.cs.crystal.annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the annotations for a method declaration.
 * 
 * @author ciera
 * @since Crystal 3.4.0
 */
public class AnnotationSummary {
	List<ICrystalAnnotation>[] annos;
	String[] paramNames;
	
	public AnnotationSummary(String[] paramNames) {
		this.paramNames = paramNames;
		annos = new List[paramNames.length + 1];
		
		for (int ndx = 0; ndx < annos.length; ndx++)
			annos[ndx] = new ArrayList<ICrystalAnnotation>();
	}
	
	public String getParameterName(int ndx) {
		return paramNames[ndx];
	}
	
	public String[] getParameterNames() {
		return paramNames;
	}
	
	/**
	 * @param ndx 0-based parameter index..
	 * @return A list of the annotations for a given parameter.
	 */
	public List<ICrystalAnnotation> getParameter(int ndx) {
		if (ndx < annos.length - 1)
			return annos[ndx];
		else
			return null;
	}

	/**
	 * Returns the (first) annotation of the given type for the given parameter, if any.
	 * Notice that when using {@link edu.cmu.cs.crystal.annotations.MultiAnnotation} there can be multiple annotations
	 * of one type on a given Java element, but this method returns only the first one.
	 * @param ndx 0-based parameter index.
	 * @param annoName The type name of the annotation.
	 * @return The (first) annotation of the given type or <code>null</code> if this annotation does not exist.
	 */
	public ICrystalAnnotation getParameter(int ndx, String annoName) {
		if (ndx < annos.length - 1)
			return AnnotationDatabase.findAnnotation(annoName, annos[ndx]);
		else
			return null;
	}
	
	/**
	 * Returns the (first) annotation of the given type for the given parameter, if any.
	 * Notice that when using {@link edu.cmu.cs.crystal.annotations.MultiAnnotation} there can be multiple annotations
	 * of one type on a given Java element, but this method returns only the first one.
	 * @param name The name of the parameter
	 * @param annoName The type name of the annotation.
	 * @return The (first) annotation of the given type or <code>null</code> if this annotation does not exist.
	 */
	public ICrystalAnnotation getParameter(String name, String annoName) {
		int ndx = 0;
		for (String param : paramNames) {
			if (name.equals(param))
				break;
			ndx++;
		}
		return getParameter(ndx, annoName);
	}

	
	/**
	 * @return A list of the annotations for the return value
	 */
	public List<ICrystalAnnotation> getReturn() {
		return annos[annos.length - 1];
	}

	/**
	 * Returns the (first) return annotation of the given type, if any.
	 * Notice that when using {@link edu.cmu.cs.crystal.annotations.MultiAnnotation} there can be multiple annotations
	 * of one type on a given Java element, but this method returns only the first one.
	 * @param annoName The type name of the annotation.
	 * @return The (first) annotation of the given type or <code>null</code>.
	 */
	public ICrystalAnnotation getReturn(String annoName) {
		return AnnotationDatabase.findAnnotation(annoName, annos[annos.length - 1]);
	}

	public void add(AnnotationSummary summary) {
		if (summary.annos.length == annos.length) {
			for (int ndx = 0; ndx < annos.length; ndx++) {
				annos[ndx].addAll(summary.annos[ndx]);
			}
		}
	}
	
	public void addReturn(ICrystalAnnotation anno) {
		annos[annos.length - 1].add(anno);
	}
	
	public void addAllReturn(List<ICrystalAnnotation> annosToAdd) {
		annos[annos.length - 1].addAll(annosToAdd);
	}
	
	public void addParameter(ICrystalAnnotation anno, int ndx) {
		if (ndx < annos.length - 1)
			annos[ndx].add(anno);
	}
	
	public void addAllParameter(List<ICrystalAnnotation> annosToAdd, int ndx) {
		if (ndx < annos.length - 1)
			annos[ndx].addAll(annosToAdd);
	}

}
