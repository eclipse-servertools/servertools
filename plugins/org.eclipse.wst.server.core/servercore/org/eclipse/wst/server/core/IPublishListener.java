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

/**
 * A publish listener. Can listen to publishing events from a
 * server control. The usual steps are:
 * 
 * moduleStateChange() - Fired whenever resources change locally
 *     that cause the publishing state of anything to change
 * 
 * publishStarting() - The overall publish operation is starting
 *   publishStarted() - The initial connection to the remote machine
 *   moduleStarting() - This section is fired for each module
 *                           in the server
 *   moduleFinished() - This module is done
 * publishFinished() - The publish operation is over
 */
public interface IPublishListener {
	/**
	 * Fired when a module on this server needs to be published
	 * or no longer needs to be published.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.model.IModule
	 */
	public void moduleStateChange(IServer server, List parents, IModule module);
	
	/**
	 * Called when a project's restart state has changed. This state
	 * lets the user know whether a project should be restarted or
	 * does not need to be.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param project org.eclipse.core.resources.IProject
	 */
	//public void moduleRestartStateChange(IServer server, IModule module);

	/**
	 * Global event fired when publishing is about to begin and the modules
	 * that will be pushed to the remote machine.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param parents java.util.List[]
	 * @param module org.eclipse.wst.server.model.IModule[]
	 */
	public void publishStarting(IServer server, List[] parents, IModule[] module);
	
	/**
	 * Fired to notify the result of opening a connection to the remove machine.
	 * If the status is an error, the publishing will finish immediately. If not,
	 * events will be fired for each module in the array.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param status org.eclipse.wst.server.core.IPublishStatus
	 */
	public void publishStarted(IServer server);

	/**
	 * Fired with the module to notify that publishing of this module
	 * is starting.
	 * 
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.model.IModule
	 */
	public void moduleStarting(IServer server, List parents, IModule module);

	/**
	 * The event is fired when the module has finished publishing,
	 * and includes the status.
	 * 
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.model.IModule
	 * @param status org.eclipse.wst.server.core.IPublishStatus
	 */
	public void moduleFinished(IServer server, List parents, IModule module, IPublishStatus status);

	/**
	 * Publishing has finished. Returns the overall status.
	 *
	 * @param server org.eclipse.wst.server.model.IServer
	 * @param status org.eclipse.wst.server.core.IPublishStatus
	 */
	public void publishFinished(IServer server, IPublishStatus status);
}