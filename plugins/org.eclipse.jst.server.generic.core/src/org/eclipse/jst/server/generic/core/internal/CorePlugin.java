/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static CorePlugin getDefault() {
		return plugin;
	}

	/**
	 * 
	 * @return
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
			return FileLocator.resolve(this.getBundle().getEntry("/"));
		} catch (IOException e) {
			return null;
		}	
	}
	
	
}
