/**
 * Copyright (c) 2006-2010 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,
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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;

import edu.cmu.cs.crystal.IAnalysisReporter;
import edu.cmu.cs.crystal.IRunCrystalCommand;

/**
 * Handler for the {@code CrystalPlugin.runcrystaloncurrent} command
 * that runs Crystal on the <b>file</b> in the currently active editor.
 * This means that unsaved changes in the editor will not be considered
 * in the analysis.
 * @author kevin
 * @since Crystal 3.4.2
 * @see RunCrystalHandler
 */
public class RunCrystalOnCurrentFileHandler implements IHandler {
	
	private static final Logger log = Logger.getLogger(RunCrystalOnCurrentFileHandler.class.getName());

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ICompilationUnit cu = null;
		try {
			IEditorPart editor = AbstractCrystalPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IPathEditorInput) {
				IPath path = ((IPathEditorInput) input).getPath();
				IFile f = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
				cu = JavaCore.createCompilationUnitFrom(f);
			}
		} catch (NullPointerException e) {
			// editor or other components may come back null
			log.log(Level.WARNING, "Could not get editor or file", e);
			return null;
		}
		if (cu == null) {
			// current editor may be for a class file or non-Java file
			log.info("Current editor does not seem to be for a .java file");
			return null;
		}

		// fire off crystal job
		final Crystal crystal = AbstractCrystalPlugin.getCrystalInstance();
		final List<ICompilationUnit> compUnits = Collections.singletonList(cu);
		Job j = new Job("Crystal") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final Set<String> enabled = AbstractCrystalPlugin.getEnabledAnalyses();
				IRunCrystalCommand run_command = new IRunCrystalCommand(){
					public Set<String> analyses() {	return enabled;	}
					public List<ICompilationUnit> compilationUnits() {
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
		return null;
	}

	public boolean isEnabled() { return true; }
	public boolean isHandled() { return true; }

	public void addHandlerListener(IHandlerListener handlerListener) { }
	public void removeHandlerListener(IHandlerListener handlerListener) { }
	public void dispose() {	}
}
