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
package edu.cmu.cs.crystal.internal;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;


/**
 * A handler for the "CrystalPlugin.enableanalysis" command. When this command is
 * issued, this handler responds by either enabling or disabling the analysis that
 * was passed as a parameter.
 * 
 * @author Nels E. Beckman
 */
public class EnableAnalysisHandler implements IHandler, IElementUpdater {

	public void addHandlerListener(IHandlerListener handlerListener) { }

	public void dispose() { }

	/**
	 * Will enable or disable an analysis based on which menu item was
	 * chosen.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final String analysis_name = event.getParameter("CrystalPlugin.analysisname");
		final Set<String> enabled = AbstractCrystalPlugin.getEnabledAnalyses();
		
		if( enabled.contains(analysis_name) )
			AbstractCrystalPlugin.disableAnalysis(analysis_name);
		else
			AbstractCrystalPlugin.enableAnalysis(analysis_name);
		
		return null;
	}

	public boolean isEnabled() { return true; }

	public boolean isHandled() { return true; }

	public void removeHandlerListener(IHandlerListener handlerListener) { }

	/**
	 * Method called to determine if the checkbox next to an analysis name
	 * should be checked or not.
	 */
	public void updateElement(UIElement element, Map parameters) {
		final String analysis_name = (String)parameters.get("CrystalPlugin.analysisname");
		final Set<String> enabled = AbstractCrystalPlugin.getEnabledAnalyses();
		
		if( enabled.contains(analysis_name) )
			element.setChecked(true);
		else
			element.setChecked(false);
	}

}
