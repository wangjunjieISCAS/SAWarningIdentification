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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IRunCrystalCommand;

/**
 * An action that will be called when a popup menu is used to run Crystal.
 */
public class CrystalFileAction implements IObjectActionDelegate {

	private ISelection selection;
	
	/**
	 * Constructor for Action1.
	 */
	public CrystalFileAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		// use set in case of overlapping selections (e.g., methods from 1 comp unit)
		Set<ITypeRoot> reanalyzeList = null;
		
		if (!selection.isEmpty()) {
			if (selection instanceof IStructuredSelection) {
				for (Object element : ((IStructuredSelection)selection).toList()) {
					List<ITypeRoot> temp =
						WorkspaceUtilities.collectCompilationUnits((IJavaElement) element,
								CrystalPreferences.getIncludeArchives());
					if(temp == null)
						continue;
					if(reanalyzeList == null)
						reanalyzeList = new LinkedHashSet<ITypeRoot>(temp);
					else
						reanalyzeList.addAll(temp);
				}
			}
		}
		
		if(reanalyzeList != null) {
			final Crystal crystal = AbstractCrystalPlugin.getCrystalInstance();
			final Collection<ITypeRoot> compUnits = reanalyzeList;
			Job j = new Job("Crystal") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					final Set<String> enabled = AbstractCrystalPlugin.getEnabledAnalyses(); 
					IRunCrystalCommand run_command = new IRunCrystalCommand(){
						public Set<String> analyses() {	return enabled;	}
						public Collection<ITypeRoot> compilationUnits() {
							return compUnits;
						}
						public IAnalysisReporter reporter() { 
							return new StandardAnalysisReporter(); 
						}
					};
					
					crystal.runAnalyses(run_command, monitor);
					if(monitor.isCanceled())
						return Status.CANCEL_STATUS;
					return Status.OK_STATUS;
				}
				
			};
			j.setUser(true);
			j.schedule();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
