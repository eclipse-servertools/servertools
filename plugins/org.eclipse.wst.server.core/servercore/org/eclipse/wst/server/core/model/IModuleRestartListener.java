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
package org.eclipse.wst.server.core.model;

import org.eclipse.wst.server.core.IServer;
/**
 * This interface is used by a server implementing the IDeployTargetRestartable
 * interface to broadcast a change of restart state for a project.
 */
public interface IModuleRestartListener {
	/**
	 * Called when a project's restart state has changed. This state
	 * lets the user know whether a project should be restarted or
	 * does not need to be.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param project org.eclipse.core.resources.IProject
	 */
	public void moduleRestartStateChange(IServer server, IModule module);

	/**
	 * Called when a project's state has changed. This means that the
	 * project has switched it's available/unavailable state.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param project org.eclipse.core.resources.IProject
	 */
	public void moduleStateChange(IServer server, IModule module);
}
