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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServer extends IElement {
	public static final String FILE_EXTENSION = "server";
	
	// attribute for launch configurations
	public static final String ATTR_SERVER_ID = "server-id";

	// --- Server State Constants ---
	// (returned from getServerState() method)

	// the server state is unknown
	public static final byte SERVER_UNKNOWN = 0;

	// the server is starting, but not yet ready to serve content
	public static final byte SERVER_STARTING = 1;

	// the server is ready to serve content
	public static final byte SERVER_STARTED = 2;

	// the server is started in debug mode and ready
	// to serve content
	public static final byte SERVER_STARTED_DEBUG = 3;
	
	// the server is started in profiling mode and ready
	// to serve content
	public static final byte SERVER_STARTED_PROFILE = 4;

	// the server is shutting down
	public static final byte SERVER_STOPPING = 5;

	// the server is stopped
	public static final byte SERVER_STOPPED = 6;

	// getting the server state is unsupported
	public static final byte SERVER_UNSUPPORTED = 7;


	// --- Module State Constants ---
	// (returned from getModuleState() method)

	// the module state is unknown
	public static final byte MODULE_STATE_UNKNOWN = 0;

	// the module is starting up
	public static final byte MODULE_STATE_STARTING = 1;

	// the module is ready to serve content
	public static final byte MODULE_STATE_STARTED = 2;

	// the module is shutting down
	public static final byte MODULE_STATE_STOPPING = 3;

	// the module is stopped
	public static final byte MODULE_STATE_STOPPED = 4;


	// --- Sync State Constants ---
	// (returned from the isXxxInSnyc() methods)

	// the state of the server's contents are unknown
	public static final byte SYNC_STATE_UNKNOWN = 0;

	// the local contents exactly match the server's contents
	public static final byte SYNC_STATE_IN_SYNC = 1;

	// the local contents do not match the server's contents
	public static final byte SYNC_STATE_DIRTY = 2;
	
	/**
	 * Returns the current state of the server. (see SERVER_XXX
	 * constants)
	 *
	 * @return byte
	 */
	public byte getServerState();
	
	public String getHostname();
	
	public IFile getFile();
	
	//public boolean hasRuntime();
	
	public IRuntime getRuntime();
	
	public IServerType getServerType();
	
	public IServerConfiguration getServerConfiguration();
	
	public IServerDelegate getDelegate();
	
	public IServerWorkingCopy getWorkingCopy();
	
	/**
	 * Returns true if this is a configuration that is
	 * applicable to (can be used with) this server.
	 *
	 * @param configuration org.eclipse.wst.server.core.model.IServerConfiguration
	 * @return boolean
	 */
	public boolean isSupportedConfiguration(IServerConfiguration configuration);

	/**
	 * Returns the configuration's sync state.
	 *
	 * @return byte
	 */
	public byte getConfigurationSyncState();

	/**
	 * Returns a list of the projects that have not been published
	 * since the last modification. (i.e. the projects that are
	 * out of sync with the server.
	 *
	 * @return java.util.List
	 */
	public List getUnpublishedModules();

	/**
	 * Add a listener to this server.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerListener
	 */
	public void addServerListener(IServerListener listener);
	
	/**
	 * Remove a listener from this server.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerListener
	 */
	public void removeServerListener(IServerListener listener);

	/**
	 * Adds a publish listener to this server.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void addPublishListener(IPublishListener listener);
	
	/**
	 * Removes a publish listener from this server.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void removePublishListener(IPublishListener listener);
	
	/**
	 * Returns true if the server is in a state that it can
	 * be published to.
	 *
	 * @return boolean
	 */
	public boolean canPublish();
	
	/**
	 * Returns true if the server may have any projects or it's
	 * configuration out of sync.
	 *
	 * @return boolean
	 */
	public boolean shouldPublish();
	
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

	public IStatus publish(IProgressMonitor monitor);
	
	public IStatus publish(IPublishManager publishManager, IProgressMonitor monitor);
	
	/**
	 * Returns true if the server is in a state that it can
	 * be started, and supports the given mode.
	 *
	 * @param mode
	 * @return boolean
	 */
	public boolean canStart(String mode);
	
	public ILaunch getExistingLaunch();
	
	/**
	 * Return the launch configuration for this server. If one does not exist, it
	 * will be created if "create" is true, and otherwise will return null.
	 * 
	 * @param create
	 * @return
	 * @throws CoreException
	 */
	public ILaunchConfiguration getLaunchConfiguration(boolean create) throws CoreException;

	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy);

	public ILaunch start(String mode, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Start the server in the given start mode and waits until the server
	 * has finished started.
	 *
	 * @param mode java.lang.String
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @exception CoreException - thrown if an error occurs while trying to start the server
	 */
	public void synchronousStart(String mode, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns true if the server is in a state that it can
	 * be restarted.
	 *
	 * @return boolean
	 */
	public boolean canRestart(String mode);
	
	/**
	 * Returns true if the server is not in sync and needs to be
	 * restarted. Returns false if the server should not be restarted.
	 * (e.g. if the contents have not been modified and the server
	 * process is still in sync)
	 * Result is undefined if the server is not running.
	 *
	 * @return boolean
	 */
	public boolean isRestartNeeded();

	/**
	 * Restart the server with the given debug mode.
	 * A server may only be restarted when it is currently running.
	 * This method is asynchronous.
	 */
	public void restart(String mode);

	/**
	 * Returns true if the server is in a state that it can
	 * be stopped.
	 *
	 * @return boolean
	 */
	public boolean canStop();

	/**
	 * Stop the server if it is running.
	 */
	public void stop();

	/**
	 * Stop the server and wait until the
	 * server has completely stopped.
	 */
	public void synchronousStop();

	/**
	 * Terminate the server process(es). This method should only be
	 * used as a last resort after the stop() method fails to work.
	 * The server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 * It MUST terminate the server completely and return it to
	 * the stopped state.
	 */
	public void terminate();

	/**
	 * Trigger a restart of the given module and wait until it has finished restarting.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @exception org.eclipse.core.runtime.CoreException - thrown if an error occurs while trying to restart the module
	 */
	public void synchronousModuleRestart(final IModule module, IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns a temporary directory that the requestor can use
	 * throughout it's lifecycle. This is primary to be used by
	 * servers for working directories, server specific
	 * files, etc.
	 *
	 * <p>As long as the same key is used to call this method on
	 * each use of the workbench, this method directory will return
	 * the same directory. If the directory is not requested over a
	 * period of time, the directory may be deleted and a new one
	 * will be assigned on the next request. For this reason, a
	 * server should request the temp directory on startup
	 * if it wants to store files there. In all cases, the server
	 * should have a backup plan to refill the directory
	 * in case it has been deleted since last use.</p>
	 *
	 * @param serverResource org.eclipse.wst.server.core.model.IServerResource
	 * @return org.eclipse.core.runtime.IPath
	 */
	public IPath getTempDirectory();
	
	public void updateConfiguration();

	/**
	 * Returns true if this module can be added to this
	 * configuration at the current time, and false otherwise.
	 *
	 * <p>This method may decide based on the type of module
	 * or refuse simply due to reaching a maximum number of
	 * modules or other criteria.</p>
	 *
	 * @param add org.eclipse.wst.server.core.model.IModule
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

	/**
	 * Method called when changes to the module or module factories
	 * within this configuration occur. Return any necessary commands to repair
	 * or modify the server configuration in response to these changes.
	 * 
	 * @param org.eclipse.wst.server.core.model.IModuleFactoryEvent[]
	 * @param org.eclipse.wst.server.core.model.IModuleEvent[]
	 * @return org.eclipse.wst.server.core.model.ITask[]
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] moduleEvent);
}
