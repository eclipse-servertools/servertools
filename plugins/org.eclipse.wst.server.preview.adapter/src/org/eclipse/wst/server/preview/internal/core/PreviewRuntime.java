/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal.core;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.osgi.framework.Bundle;
/**
 * HTTP preview runtime.
 */
public class PreviewRuntime extends RuntimeDelegate {
	public static final String ID = "org.eclipse.wst.server.preview.runtime";

	/**
	 * Create a new preview runtime.
	 */
	public PreviewRuntime() {
		// do nothing
	}

	/**
	 * Returns the path that corresponds to the specified bundle.
	 * 
	 * @return a path
	 */
	protected static Path getPluginPath(Bundle bundle) {
		try {
			URL installURL = bundle.getEntry("/");
			URL localURL = FileLocator.toFileURL(installURL);
			return new Path(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}

	protected static IPath getJarredPluginPath(Bundle bundle) {
		Path runtimeLibFullPath = null;
		String jarPluginLocation = bundle.getLocation().substring(7);
		
		// handle case where jars are installed outside of eclipse installation
		Path jarPluginPath = new Path(jarPluginLocation);
		if (jarPluginPath.isAbsolute())
			runtimeLibFullPath = jarPluginPath;
		// handle normal case where all plugins under eclipse install
		else {
			int ind = jarPluginLocation.lastIndexOf(":");
			if (ind > 0)
				jarPluginLocation = jarPluginLocation.substring(ind+1);
			
			String installPath = Platform.getInstallLocation().getURL().getPath();
			runtimeLibFullPath = new Path(installPath+"/"+jarPluginLocation);
		}
		return runtimeLibFullPath;
	}

	/**
	 * @see RuntimeDelegate#setDefaults(IProgressMonitor)
	 */
	public void setDefaults(IProgressMonitor monitor) {
		getRuntimeWorkingCopy().setLocation(new Path(""));
	}
}