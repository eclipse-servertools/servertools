/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import java.net.URL;

import org.eclipse.wst.server.core.IModule;
/**
 * 
 * @plannedfor 1.0
 */
public interface IURLProvider {
	/**
	 * Return the base URL of this module on the server. (e.g. 
	 * "http://localhost:8080/myProject") 
	 * <p>
	 * This method may return null if this server does not have a valid configuration
	 * or if the server is not running. The returned URL must not end in a trailing
	 * slash.
	 * </p>
	 * <p>
	 * If the module is null, the returned URL will just be to the root of
	 * the server (e.g. "http://localhost:8080")
	 * </p>
	 * <p>
	 * If the module is not already added to the server, the method will return
	 * as close an approximation as possible. (for instance, for a J2EE web project
	 * it may use the project's context root, which may not be the same when deployed
	 * to a server)
	 * </p>
	 *
	 * @param module com.ibm.etools.server.core.IModule
	 * @return java.net.URL
	 */
	public URL getModuleRootURL(IModule module);
}