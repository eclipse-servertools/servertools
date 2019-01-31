/***************************************************************************************************
 * Copyright (c) 2005, 2006 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;


import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Gorkem Ercan
 */
public class CorePlugin extends Plugin {


	/**
	 * Plug-in ID
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.generic.core"; //$NON-NLS-1$

	//The shared instance.
	private static CorePlugin plugin;
	private ServerTypeDefinitionManager fServerTypeDefinitionManager;
	/**
	 * The constructor.
	 */
	public CorePlugin() {
		super();
		plugin = this;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * @return genericServerCoreInstance
	 */
	public static CorePlugin getDefault() {
		return plugin;
	}


	/**
	 * Returns the server type definition manager instance
	 * 
	 * @return instance
	 */
	public ServerTypeDefinitionManager getServerTypeDefinitionManager()
	{
		if(fServerTypeDefinitionManager==null)
			fServerTypeDefinitionManager = new ServerTypeDefinitionManager(getInstallUrl());
		return fServerTypeDefinitionManager;
	}
	
	private URL getInstallUrl()
	{
		try {
			return FileLocator.resolve(this.getBundle().getEntry("/")); //$NON-NLS-1$
		} catch (IOException e) {
			return null;
		}	
	}
	
	
}
