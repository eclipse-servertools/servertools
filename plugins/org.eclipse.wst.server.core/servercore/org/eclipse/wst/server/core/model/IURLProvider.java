/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import java.net.URL;

import org.eclipse.wst.server.core.IModule;
/**
 * 
 */
public interface IURLProvider {
	/**
	 * Return the base URL of this module on the server. This method may return
	 * null if this server does not have a valid configuration or does not contain
	 * this module. Otherwise, it should return the base URL (e.g. 
	 * "http://localhost:8080/myProject") regardless of whether the server is
	 * running or not. The returned URL should not end in a trailing slash.
	 * 
	 * <p>If the module is null, the returned URL will just be to the root of
	 * the server (e.g. "http://localhost:8080")</p>
	 * 
	 * <p>If the module is not already added to the server, the method will return
	 * as close an approximation as possible. (for instance, for a web project it
	 * may use the project's context root, which may not be the same when deployed
	 * to a server)</p>
	 *
	 * @param module com.ibm.etools.server.core.model.IModule
	 * @return java.net.URL
	 */
	public URL getModuleRootURL(IModule module);
}