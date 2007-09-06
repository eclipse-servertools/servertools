/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
/**
 * The main server tooling plugin class.
 */
public class JavaServerUIPlugin extends AbstractUIPlugin {
	/**
	 * Java server UI plugin id
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.ui";

	// singleton instance of this class
	private static JavaServerUIPlugin singleton;

	/**
	 * Create the JavaServerUIPlugin.
	 */
	public JavaServerUIPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return org.eclipse.jst.server.ui.JavaServerUIPlugin
	 */
	public static JavaServerUIPlugin getInstance() {
		return singleton;
	}
	
	/**
	 * Convenience method for logging.
	 *
	 * @param t a throwable
	 */
	public static void log(Throwable t) {
		getInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Internal error", t)); //$NON-NLS-1$
	}

	/**
	 * Returns the active workbench shell
	 * 
	 * @return the active workbench shell
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
		if (workBenchWindow == null)
			return null;
		return workBenchWindow.getShell();
	}
	
	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	protected static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbench workBench= getInstance().getWorkbench();
		if (workBench == null)
			return null;
		return workBench.getActiveWorkbenchWindow();
	}
}
