/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.server.core.internal.ServerPlugin;
/**
 * A server project.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerProject {
	// server project nature id
	public static final String NATURE_ID = ServerPlugin.PLUGIN_ID + ".nature";

	/**
	 * Returns a list of available folders within the server project. These are
	 * all the folders within the project, except those that are already part of
	 * a server configuration.
	 */ 
	public List getAvailableFolders();

	/**
	 * Returns the project that this nature is associated with.
	 *
	 * @return org.eclipse.core.resources.IProject
	 */
	public IProject getProject();

	/**
	 * Returns the server configurations that are located in
	 * this project.
	 *
	 * @return java.util.List
	 */
	public List getServerConfigurations();

	/**
	 * Returns the servers that are located in this project.
	 *
	 * @return java.util.List
	 */
	public List getServers();
}