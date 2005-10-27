/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
/**
 * Tomcat 55 handler.
 */
public class Tomcat55Handler extends Tomcat50Handler {
	/**
	 * @see ITomcatVersionHandler#verifyInstallPath(IPath)
	 */
	public boolean verifyInstallPath(IPath installPath) {
		if (installPath == null)
			return false;

		if (!TomcatPlugin.verifyTomcatVersionFromPath(installPath, TomcatPlugin.TOMCAT_55))
			return false;
		return TomcatPlugin.verifyInstallPath(installPath, TomcatPlugin.TOMCAT_55);
	}
	
	/**
	 * @see ITomcatVersionHandler#canAddModule(IModule)
	 */
	public IStatus canAddModule(IModule module) {
		String version = module.getModuleType().getVersion();
		if ("2.2".equals(version) || "2.3".equals(version) || "2.4".equals(version))
			return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, Messages.canAddModule, null);
		
		return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorSpec55, null);
	}
}