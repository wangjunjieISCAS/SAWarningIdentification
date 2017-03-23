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

import java.util.Collections;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

import edu.cmu.cs.crystal.ICrystalAnalysis;

/**
 * The dynamic menu populator for the "Crystal" menu that will add an item
 * for each registered analysis. When {@code getContributionItems} is called,
 * it returns menu contributions, one for each analysis that is registered.
 * These menu items are checkboxes, so they can be enabled or disabled.
 * 
 * @see edu.cmu.cs.crystal.internal.EnableAnalysisHandler
 * @author Nels E. Beckman
 */
public class AnalysisMenuPopulator extends CompoundContributionItem {

	public AnalysisMenuPopulator() {
	}

	public AnalysisMenuPopulator(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final Crystal crystal = AbstractCrystalPlugin.getCrystalInstance();
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		final int num_analyses = crystal.getAnalyses().size();
		
		IContributionItem[] result = new IContributionItem[num_analyses];
		// Create a CommandContributionItem for each crystal analysis.
		// This displays the name of the analysis and creates a checkbox
		// for it. 
		int arr_index = 0;
		for( ICrystalAnalysis analysis : crystal.getAnalyses() ) {
			final String analysis_name = analysis.getName();
			final CommandContributionItem item =
				new CommandContributionItem(window,
				                            null,
				                            "CrystalPlugin.enableanalysis",
				                            Collections.singletonMap("CrystalPlugin.analysisname", analysis_name),
				                            null,null,null,
				                            analysis_name,
				                            null,
				                            "Enable/Disable the Crystal analysis " + analysis_name,
				                            CommandContributionItem.STYLE_CHECK);
			
			result[arr_index] = item;
			arr_index++;
		}
		
		return result;
	}

}
