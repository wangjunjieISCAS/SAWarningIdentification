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

import java.io.PrintWriter;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.part.ViewPart;

/**
 * This is a text console for users to submit messages to.
 * 
 * @author David Dickey
 *
 */
public class UserConsoleView extends ViewPart {
	
	private static final Logger log = Logger.getLogger(UserConsoleView.class.getName());
	
	private static UserConsoleView instance = null;
	private TextConsoleViewer viewer;
	private IOConsole ioConsole;
	private IOConsoleOutputStream ioConsoleOutputStream;
	
	/**
	 * The constructor.
	 */
	public UserConsoleView() {
		log.fine("UserConsoleView Instantiated");
		instance = this;
	}
	
	/**
	 * The instance of this singleton can be retrieved through this method.
	 * 
	 * @return	the one and only UserConsoleView
	 */
	public static UserConsoleView getInstance() {
		return instance;
	}

	/**
	 * Called by the framework to open the view.
	 */
	public void createPartControl(Composite parent) {
		ioConsole = new IOConsole("Crystal User Console", ImageDescriptor.getMissingImageDescriptor());
		viewer = new TextConsoleViewer(parent, ioConsole);
		viewer.setInput(getViewSite());
		
		ioConsoleOutputStream = ioConsole.newOutputStream();
		
		PrintWriter pw = getPrintWriter();
		pw.println("[userOut]");
	}
	
	/**
	 * Creates a PrintWriter object to allow for text to be
	 * printed to the console
	 * 
	 * @return	the PrintWriter corresponding to this console.  Or null if
	 * 			the console has not been properly setup.
	 */
	public PrintWriter getPrintWriter() {
		if(ioConsoleOutputStream == null) {
			log.warning("The User Console has not been properly initiated.");
			return null;
		}
		return new PrintWriter(ioConsoleOutputStream, true);
	}
	
	/**
	 * Clears all text from the console
	 *
	 */
	public void clearConsole() {
		ioConsole.getDocument().set("");
	}

	/**
	 * Provides the focus to the user console.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}