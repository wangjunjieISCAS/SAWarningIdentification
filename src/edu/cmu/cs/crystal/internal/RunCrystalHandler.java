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

import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ITypeRoot;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IRunCrystalCommand;

/**
 * A class that will handle the "CrystalPlugin.runcrystal" command. It handles
 * this command by running Crystal.
 * @author Nels E. Beckman
 */
public class RunCrystalHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Crystal crystal = AbstractCrystalPlugin.getCrystalInstance();
		Job j = new Job("Crystal") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Set<String> enabled = AbstractCrystalPlugin.getEnabledAnalyses();
				final List<ITypeRoot> cus = WorkspaceUtilities.scanForCompilationUnits(
						CrystalPreferences.getIncludeArchives());
				
				IRunCrystalCommand run_command = new IRunCrystalCommand(){
					public Set<String> analyses() { return enabled;	}
					public List<ITypeRoot> compilationUnits() { return cus; }
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
		
		return null;
	}

	public boolean isEnabled() { return true; }
	public boolean isHandled() { return true; }
	
	public void addHandlerListener(IHandlerListener handlerListener) { }
	public void removeHandlerListener(IHandlerListener handlerListener) { }
	public void dispose() {	}
}
