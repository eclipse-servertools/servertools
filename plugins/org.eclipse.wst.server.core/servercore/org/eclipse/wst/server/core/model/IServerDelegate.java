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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerState;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.resources.IModuleResourceDelta;
/**
 * 
 */
public interface IServerDelegate {
	/**
	 * Called when the server is loaded as a model object.
	 */
	public void initialize(IServerState liveServer);

	/**
	 * Called when this server resource has become invalid or no longer
	 * required and is being deregistered or dicarded. This method can
	 * be used to remove listeners, etc.
	 */
	public void dispose();
	
	/**
	 * Returns the publisher that can be used to publish the
	 * given module. If the module should never
	 * be published to the server, it may return null.
	 *
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return org.eclipse.wst.server.core.model.IPublisher
	 */
	public IPublisher getPublisher(List parents, IModule module);

	/**
	 * The server configuration has changed. This method should return
	 * quickly. If any republishing must occur, the relevant in-sync
	 * methods should return a new value. If the server must be restarted,
	 * the isRestartNeeded() method should return true.
	 */
	public void updateConfiguration();

	/**
	 * A module resource has changed. This method should return
	 * quickly. If the server must be restarted to handle the
	 * change of this file, the isRestartNeeded() method should
	 * return true and the event should be fired.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param delta org.eclipse.wst.server.core.IModuleResourceDelta
	 */
	public void updateModule(IModule module, IModuleResourceDelta delta);

	/**
	 * Methods called to notify that publishing is about to begin.
	 * This allows the server to open a connection to the server
	 * or get any global information ready.
	 *
	 * <p>This method should not be called directly! Use the
	 * IServerControl to correctly publish to the server.</p>
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus publishStart(IProgressMonitor monitor);

	/**
	 * Publish the configuration.
	 * 
	 * @param monitor
	 * @return
	 */
	public IStatus publishConfiguration(IProgressMonitor monitor);

	/**
	 * Methods called to notify that publishing has finished.
	 * The server can close any open connections to the server
	 * and do any cleanup operations.
	 *
	 * <p>This method should not be called directly! Use the
	 * IServerControl to correctly publish to the
	 * server.</p>
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus publishStop(IProgressMonitor monitor);
	
	/**
	 * Returns true if this module can be added to this
	 * configuration at the current time, and false otherwise.
	 *
	 * <p>This method may decide based on the type of module
	 * or refuse simply due to reaching a maximum number of
	 * modules or other criteria.</p>
	 *
	 * @param add org.eclipse.wst.server.core.model.IModule[]
	 * @param remove org.eclipse.wst.server.core.model.Module[]
	 * @return boolean
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove);

	/**
	 * Returns the modules that are in this configuration.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getModules();
	
	/**
	 * Returns the current state of the given module. See
	 * class header for MODULE_XXX constants.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return byte
	 */
	public byte getModuleState(IModule module);

	/**
	 * Method called when changes to the modules or module factories
	 * within this configuration occur. Return any necessary commands to repair
	 * or modify the server configuration in response to these changes.
	 * 
	 * @param org.eclipse.wst.server.core.model.IModuleFactoryEvent[]
	 * @param org.eclipse.wst.server.core.model.IModuleEvent[]
	 * @return org.eclipse.wst.server.core.model.ITask[]
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] moduleEvent);
	
	/**
	 * Returns the child module(s) of this module. If this
	 * module contains other modules, it should list those
	 * modules. If not, it should return an empty list.
	 *
	 * <p>This method should only return the direct children.
	 * To obtain the full module tree, this method may be
	 * recursively called on the children.</p>
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.util.List
	 */
	public List getChildModules(IModule module);

	/**
	 * Returns the parent module(s) of this module. When
	 * determining if a given project can run on a server
	 * configuration, this method will be used to find the
	 * actual module(s) that may be run on the server. For
	 * instance, a Web module may return a list of Ear
	 * modules that it is contained in if the server only
	 * supports configuring Ear modules.
	 *
	 * <p>If the module type is not supported, this method
	 * may return null. If the type is normally supported but there
	 * is a configuration problem or missing parent, etc., this
	 * method may fire a CoreException that may then be presented
	 * to the user.</p>
	 *
	 * <p>If it does return valid parent(s), this method should
	 * always return the topmost parent module(s), even if
	 * there are a few levels (a heirarchy) of modules.</p>
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return java.util.List
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public List getParentModules(IModule module) throws CoreException;
	
	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy);
}
