/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman, Kevin
 * Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 * 
 * This file is part of Crystal.
 * 
 * Crystal is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Crystal is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Crystal. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.crystal.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.cmu.cs.crystal.ICrystalAnalysis;
import edu.cmu.cs.crystal.annotations.ICrystalAnnotation;

/**
 * Provided Crystal plugin functionality
 * 
 * @author David Dickey
 * @author Jonathan Aldrich
 * 
 */
public abstract class AbstractCrystalPlugin extends AbstractUIPlugin {

	private static final Logger log = Logger.getLogger(AbstractCrystalPlugin.class.getName());

	/**
	 * Contains the names of all registered analyses, as well as a boolean indicating whether or not
	 * that analysis is enabled.
	 */
	private static final Map<String, Boolean> registeredAnalyses =
	    Collections.synchronizedMap(new LinkedHashMap<String, Boolean>());

	/**
	 * Returns the set of analyses that are enabled at the moment this method is called.
	 */
	public static Set<String> getEnabledAnalyses() {
		Set<String> result = new LinkedHashSet<String>();

		synchronized (registeredAnalyses) {
			for (Map.Entry<String, Boolean> entry : registeredAnalyses.entrySet()) {
				if (entry.getValue())
					result.add(entry.getKey());
			}
		}
		return result;
	}

	/**
	 * Add the given name to the set of analyses that are enabled. Note that if there is no analysis
	 * with this name, no error will be reported!
	 */
	public static void enableAnalysis(String analysis_name) {
		registeredAnalyses.put(analysis_name, Boolean.TRUE);
		Set<String> disabledPref = CrystalPreferences.getDisabledAnalyses();
		disabledPref.remove(analysis_name);
		CrystalPreferences.setDisabledAnalyses(disabledPref);
	}

	/**
	 * Remove the given name from the set of analyses that are enabled. Note that if there is no
	 * analysis with this name, no error will be reported!
	 */
	public static void disableAnalysis(String analysis_name) {
		registeredAnalyses.put(analysis_name, Boolean.FALSE);
		Set<String> disabledPref = CrystalPreferences.getDisabledAnalyses();
		disabledPref.add(analysis_name);
		CrystalPreferences.setDisabledAnalyses(disabledPref);
	}

	/**
	 * This method is called upon plug-in activation. Used to initialize the plugin for first time
	 * use. Invokes setupCrystalAnalyses, which is overridden by CrystalPlugin.java to register any
	 * necessary analyses with the framework.
	 */
	static private Crystal crystal;
	
	private static AbstractCrystalPlugin plugin;
	
	/**
	 * Package-private method to access the singleton activator class.
	 * @return the singleton activator class.
	 */
	static AbstractCrystalPlugin getDefault() {
		return plugin;
	}

	static public Crystal getCrystalInstance() {
		synchronized (AbstractCrystalPlugin.class) {
			return crystal;
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		synchronized (AbstractCrystalPlugin.class) {
			if (crystal == null)
				crystal = new Crystal();
		}
		setupCrystalAnalyses(crystal);

		// analysis extensions
		Set<String> disabled = CrystalPreferences.getDisabledAnalyses();
		for (IConfigurationElement config : Platform
		    .getExtensionRegistry().getConfigurationElementsFor(
		        "edu.cmu.cs.crystal.CrystalAnalysis")) {
			if ("analysis".equals(config.getName()) == false) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Unknown CrystalAnalysis configuration element: "
					    + config.getName());
				continue;
			}
			try {
				ICrystalAnalysis analysis =
				    (ICrystalAnalysis) config.createExecutableExtension("class");
				String analysisName = analysis.getName();
				if (log.isLoggable(Level.CONFIG))
					log.config("Registering analysis extension: " + analysisName);
				crystal.registerAnalysis(analysis);

				// Enable analysis by default, disable if disabled before
				registeredAnalyses.put(analysisName, ! disabled.contains(analysisName));
			}
			catch (CoreException e) {
				log.log(Level.SEVERE, "Problem with configured analysis: " + config.getValue(), e);
			}
		}
		if(disabled.retainAll(registeredAnalyses.keySet()))
			// update disabled preference if it contains unknown analyses
			CrystalPreferences.setDisabledAnalyses(disabled);
		
		// disable analyses according to preferences
		for(String s : CrystalPreferences.getDisabledAnalyses()) {
			if(registeredAnalyses.containsKey(s))
				registeredAnalyses.put(s, Boolean.FALSE);
		}

		// annotation extensions
		for (IConfigurationElement config : Platform
		    .getExtensionRegistry().getConfigurationElementsFor(
		        "edu.cmu.cs.crystal.CrystalAnnotation")) {
			if ("customAnnotation".equals(config.getName()) == false) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Unknown CrystalAnnotation configuration element: "
					    + config.getName());
				continue;
			}
			try {
				Class<? extends ICrystalAnnotation> annoClass;
				try {
					annoClass =
					    (Class<? extends ICrystalAnnotation>) Class.forName(config
					        .getAttribute("parserClass"));
				}
				catch (ClassNotFoundException x) {
					if (log.isLoggable(Level.WARNING))
						log
						    .warning("Having classloader problems.  Try to add to your MANIFEST.MF: "
						        + "\"Eclipse-RegisterBuddy: edu.cmu.cs.crystal\"");
					// can only directly load annotation class if defining plugin considers Crystal
					// a "buddy"
					// See:
					// http://www.ibm.com/developerworks/library/os-ecl-osgi/index.html#buddyoptions
					// but somehow on my eclipse the configuration is always able to create an
					// instance
					// so we will try this and get the Class object from the returned instance
					annoClass =
					    ((ICrystalAnnotation) config.createExecutableExtension("parserClass"))
					        .getClass();
					if (log.isLoggable(Level.WARNING))
						log.warning("Recovered from problem loading class: "
						    + config.getAttribute("parserClass"));
				}
				if (config.getChildren("sourceAnnotation").length == 0) {
					if (log.isLoggable(Level.WARNING))
						log.warning("No @Annotation classes associated with parser: " + annoClass);
					continue;
				}
				for (IConfigurationElement anno : config.getChildren("sourceAnnotation")) {
					if (log.isLoggable(Level.CONFIG))
						log.config("Registering annotation: "
						    + anno.getAttribute("annotationClass"));
					boolean parseAsMeta = Boolean.parseBoolean(anno.getAttribute("parseFromMeta"));

					crystal.registerAnnotation(
					    anno.getAttribute("annotationClass"), annoClass, parseAsMeta);
				}
			}
			catch (Throwable e) {
				log.log(Level.SEVERE, "Problem with configured annotation parser: "
				    + config.getValue(), e);
			}
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public abstract void setupCrystalAnalyses(Crystal crystal);
}
