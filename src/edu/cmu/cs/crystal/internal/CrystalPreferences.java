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
package edu.cmu.cs.crystal.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * @author Kevin Bierhoff
 * @since Crystal 3.4.2
 */
public class CrystalPreferences extends AbstractPreferenceInitializer {

	private static final String P_DISABLED_ANALYSES = "enabledAnalyses";
	private static final String P_INCLUDE_ARCHIVES = "includeArchives";

	@Override
	public void initializeDefaultPreferences() {
		AbstractCrystalPlugin.getDefault().getPreferenceStore().
			setDefault(P_DISABLED_ANALYSES, "");
		AbstractCrystalPlugin.getDefault().getPreferenceStore().
			setDefault(P_INCLUDE_ARCHIVES, false);
	}
	
	static Set<String> getDisabledAnalyses() {
		String disabledString = AbstractCrystalPlugin.getDefault().getPreferenceStore().
				getString(P_DISABLED_ANALYSES);
		Set<String> result = new HashSet<String>();
		StringTokenizer t = new StringTokenizer(disabledString, "\0");
		while(t.hasMoreTokens()) {
			result.add(t.nextToken());
		}
		return result;
	}
	
	static void setDisabledAnalyses(Set<String> disabled) {
		StringBuilder disabledString = new StringBuilder();
		boolean first = true;
		for(String s : disabled) {
			if(first) 
				first = false;
			else 
				// let's hope no analysis name contains \0
				disabledString.append('\0');
			disabledString.append(s);
		}
		AbstractCrystalPlugin.getDefault().getPreferenceStore().
				setValue(P_DISABLED_ANALYSES, disabledString.toString());
	}

	static boolean getIncludeArchives() {
		return AbstractCrystalPlugin.getDefault().getPreferenceStore().
				getBoolean(P_INCLUDE_ARCHIVES);
	}

	static void setIncludeArchives(boolean include) {
		AbstractCrystalPlugin.getDefault().getPreferenceStore().
				setValue(P_INCLUDE_ARCHIVES, include);
	}
}
