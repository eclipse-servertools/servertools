/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal;

import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
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

    public void start(BundleContext context) throws Exception {

    	super.start(context);

    	// register the debug options listener
		final Hashtable<String, String> props = new Hashtable<String, String>(4);
		props.put(DebugOptions.LISTENER_SYMBOLICNAME, JavaServerUIPlugin.PLUGIN_ID);
		context.registerService(DebugOptionsListener.class.getName(), new Trace(), props);
    }
}