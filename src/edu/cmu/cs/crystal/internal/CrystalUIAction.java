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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IRunCrystalCommand;

/**
 * Begins the execution of the Crystal framework when the corresponding
 * GUI element is triggered.
 * 
 * See the "plugin.xml" file for the mapping between GUI element and this
 * class.
 * 
 * @author David Dickey
 */
public class CrystalUIAction implements IWorkbenchWindowActionDelegate {

	/**
	 * required by the IWorkbenchWindowActionDelegate interface
	 */
	public void run(IAction action) {
		final Crystal crystal = AbstractCrystalPlugin.getCrystalInstance();
		Job j = new Job("Crystal") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Set<String> enabled = AbstractCrystalPlugin.getEnabledAnalyses();
				final List<ITypeRoot> cus = WorkspaceUtilities.scanForCompilationUnits(
						CrystalPreferences.getIncludeArchives());
				
				IRunCrystalCommand run_command = new IRunCrystalCommand(){
					public Set<String> analyses() { return enabled;	}
					public List<ITypeRoot> compilationUnits() { 
						if(cus == null) 
							return emptyList();
						else return unmodifiableList(cus); 
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
	/**
	 * required by the IWorkbenchWindowActionDelegate interface
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
	}
	/**
	 * required by the IWorkbenchWindowActionDelegate interface
	 */
	public void dispose() {
		
	}
	/**
	 * required by the IWorkbenchWindowActionDelegate interface
	 */
    public void init(IWorkbenchWindow window) {

    }
}
