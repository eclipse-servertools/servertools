/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
/**
 * Represents a server configuration instance. Every server configuration is an
 * instance of a particular, fixed server configuration type.
 * <p>
 * Servers have an optional server configuration. The server configuration is
 * information used to configure a running server. Simple types of servers
 * might not require any configuration, whereas full-featured web server have
 * an extensive set of parameters for adjusting the server's behavior. Even
 * though server configuration information usually takes the form of one or
 * more files, configuration information is treated separately from actual
 * content. Actual web content can be deployed on different servers without
 * change, whereas server configuration information is usually highly
 * dependent on the particulars of the server. Having the server configuration
 * identified as an entity separate from the server itself facilitates
 * switching an existing server between configurations, and sharing server
 * configurations between several servers of the same type (e.g., a local test
 * server and a remote server running on another host). * </p>
 * <p>
 * The resource manager maintains a global list of all known server 
 * configuration instances ({@link IResourceManager#getServerConfigurations()}).
 * </p>
 * <p>
 * [issue: The information actually stored in the (.config) file is:
 * server configuration id and name, and server configuration type id.
 * It's unclear what's gained by storing this
 * information in a workspace file. Is it so that this information
 * can be shared between users via a repository? Or is it just so that
 * there would be something to open in the resource navigator view?]
 * </p>
 * <p>
 * [issue: What is the role of the data files in the configuration
 * data folder? Where do they come from? Are they created by the
 * client (UI), or do they get created automatically?]
 * </p>
 * <p>
 * [issue: Are data files ever changed as a direct and immediate
 * result of a change to a working copy? Changing any of the data
 * files prior to the save() request would be problematic since
 * these files are shared by the original. The only way it could
 * work is to the data files to be updated only when a working copy
 * is saved.]
 * </p>
 * <p>
 * [issue: Equality/identify for server configurations?]
 * </p>
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IServerConfiguration extends IElement, IAdaptable {
	
	/**
	 * File extension (value "config") for serialized representation of
	 * server configuration instances.
	 * <p>
	 * [issue: What is relationship between this file extension and
	 * the file passed to IServerConfigurationType.createServerConfiguration(...)
	 * and importFromPath/Runtime or returned by IServerConfiguration.getFile()?
	 * That is, are server files configuration expected to end
	 * in ".config", or is this just a default? If the former
	 * (as I suspect), then IServerConfigurationType operations needs to say so,
	 * and the implementation should enforce the restriction.]
	 * </p>
	 */
	public static final String FILE_EXTENSION = "config";

	/**
	 * Returns the type of this server configuration.
	 * 
	 * @return the server configuration type
	 */
	public IServerConfigurationType getServerConfigurationType();
	
	/**
	 * Returns the file where this server configuration instance is serialized.
	 * 
	 * @return the file in the workspace where the server configuration instance
	 * is serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 */
	public IFile getFile();

	/**
	 * Returns the extension for this server configuration.
	 * The extension is a server-configuration-type-specific object.
	 * By casting the server configuration extension to the type prescribed in
	 * the API documentation for that particular server configuration type, 
	 * the client can access server-configuration-type-specific properties and
	 * methods.
	 * 
	 * @return the server configuration extension
	 */
	//public IServerExtension getExtension(IProgressMonitor monitor);

	/**
	 * Returns a working copy for modifying this server configuration instance.
	 * If this instance is already a working copy, it is returned.
	 * If this instance is not a working copy, a new server configuration
	 * working copy is created with the same id and attributes.
	 * Clients are responsible for saving or releasing the working copy when
	 * they are done with it.
	 * <p>
	 * The server configuration working copy is related to this server 
	 * configuration instance in the following ways:
	 * <pre>
	 * this.getWorkingCopy().getId() == this.getId()
	 * this.getWorkingCopy().getFile() == this.getFile()
	 * this.getWorkingCopy().getOriginal() == this
	 * </pre>
	 * </p>
	 * <p>
	 * [issue: IServerConfigurationWorkingCopy extends IServerConfiguration. 
	 * ServerConfiguration.getWorkingCopy() create a new working copy;
	 * ServerConfigurationWorkingCopy.getWorkingCopy() returns this.
	 * This may be convenient in code that is ignorant of
	 * whether they are dealing with a working copy or not.
	 * However, it is hard for clients to manage working copies
	 * with this design.
	 * This method should be renamed "createWorkingCopy"
	 * or "newWorkingCopy" to make it clear to clients that it
	 * creates a new object, even for working copies.]
	 * </p>
	 * 
	 * @return a new working copy
	 */
	public IServerConfigurationWorkingCopy createWorkingCopy();
	
	/**
	 * Returns the handle of a workspace folder where this server
	 * configuration's files are stored. Returns <code>null</code>
	 * if the folder is outside the workspace (in which case use
	 * {@link #getConfigurationDataPath()} to get the file system
	 * path).
	 * <p>
	 * When a configuration instance is serialized in a workspace
	 * file, the accompanying data files are stored in a
	 * sibling folder with a derivative name. If the configuration file
	 * is named "MyConfig.config", the sibling folder that holds the data
	 * files would be named "MyConfig-data".
	 * </p>
	 * 
	 * @return a workspace folder, or <code>null</code>
	 * if the data folder is not in the workspace
	 */
	public IFolder getConfigurationDataFolder();
	
	/**
	 * Returns the path to a non-workspace folder where this server
	 * configuration's files are stored. Returns <code>null</code>
	 * if the folder is inside the workspace (in which case use
	 * {@link #getConfigurationDataFolder()} to get the folder's handle).
	 * 
	 * @return a folder not in the workspace, or <code>null</code>
	 * if the data folder is in the workspace
	 */
	public IPath getConfigurationDataPath();
}