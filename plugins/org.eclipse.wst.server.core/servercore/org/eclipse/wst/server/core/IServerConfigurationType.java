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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Represents a server configuration type from which server configuration 
 * instances ({@link IServerConfiguration}) can be created.
 * <p>
 * The server core framework supports
 * an open-ended set of server configuration types, which are contributed via
 * the <code>serverConfigurationTypes</code> extension point in the server core
 * plug-in. Server configuration type objects carry no state (all information is
 * read-only and is supplied by the server configuration type declaration).
 * The global list of known server configuration types is available via
 * {@link ServerCore#getServerConfigurationTypes()}. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * [issue: It is notoriously difficult to place any kind of
 * useful order on objects that are contributed independently by
 * non-collaborating parties. The IOrdered mechanism is weak, and
 * can't really solve the problem. Issues of presentation are usually
 * best left to the UI, which can sort objects based on arbitrary
 * properties.]
 * </p>
 * <p>
 * [issue: Equality/identify for server types? Are IServerConfigurationType
 * instances guaranteed to be canonical (client can use ==),
 * or is it possible for there to be non-identical IServerConfigurationType
 * objects in play that both represent the same server configuration type?
 * The latter is the more common; type should spec equals.]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IServerConfigurationType extends IOrdered {

	/**
	 * Returns the id of this server configuration type.
	 * Each known server configuration type has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the server configuration type id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this server configuration type.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this server configuration type
	 */
	public String getName();

	/**
	 * Returns the displayable description for this server configuration type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this server configuration type
	 */
	public String getDescription();

	/**
	 * Returns the extensions to filter when importing the server
	 * resource. If these extensions are given, the resource is
	 * assumed to be a file. If null is returned, the import will
	 * look for folders instead.
	 * <p>
	 * [issue: Explain how these are used.]
	 * </p>
	 * <p>
	 * [issue: Seems like a very UI-centric API, useful only
	 * in conjuction with importFromPath(...).]
	 * </p>
	 *
	 * @return java.lang.String[]
	 */
	public String[] getImportFilterExtensions();

	/**
	 * Returns whether this type of server configuration requires
	 * requires it's own data in a folder in the workspace. If false,
	 * this server configuration does not store any files and all data
	 * is contained within the actual server configuration.
	 * 
	 * @return <code>true</code> if this type of server configuration
	 * needs a folder, and <code>false</code> if it does not
	 */
	public boolean isFolder();

	/**
	 * Creates a working copy instance of this server configuration type.
	 * After setting various properties of the working copy,
	 * the client should call
	 * {@link IServerConfigurationWorkingCopy#save(IProgressMonitor)}
	 * to bring the server configuration instance into existence.
	 * <p>
	 * [issue: Since this method just creates a working copy,
	 * it's not clear the operation is long-running and in need
	 * of a progress monitor.]
	 * </p>
	 * <p>
	 * [issue: This method is declared as throwing CoreException.
	 * From a clients's point of view, what are the circumstances that
	 * cause this operation to fail?]
	 * </p>
	 * 
	 * @param id the id to assign to the server configuration instance;
	 * a generated id is used if id is <code>null</code> or an empty string
	 * @param file the file in the workspace where the server configuration
	 * instance is to be serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server configuration working copy with the given id
	 * @throws CoreException [missing]
	 */
	public IServerConfigurationWorkingCopy createServerConfiguration(String id, IFile file, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Creates a working copy instance of this server configuration type,
	 * by importing from the given local file system path (outside the workspace).
	 * After setting additional various properties of the working copy,
	 * the client should call
	 * {@link IServerConfigurationWorkingCopy#save(IProgressMonitor)}
	 * to bring the server configuration instance into existence.
	 * <p>
	 * [issue: What this does is type-dependent. Perhaps these
	 * methods should instead be type-dependent API on 
	 * IServerConfigurationWorkingCopy(Delegate).]
	 * </p>
	 * <p>
	 * [issue: This method is declared as throwing CoreException.
	 * From a clients's point of view, what are the circumstances that
	 * cause this operation to fail?]
	 * </p>
	 * 
	 * @param id the id to assign to the server configuration instance;
	 * a generated id is used if id is <code>null</code> or an empty string
	 * @param file the file in the workspace where the server configuration
	 * instance is to be serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 * @param path a local file system path
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server configuration working copy with the given id
	 * @throws CoreException [missing]
	 */
	public IServerConfigurationWorkingCopy importFromPath(String id, IFile file, IPath path, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Creates a working copy instance of this server configuration type,
	 * by importing from the given runtime instance.
	 * After setting additional various properties of the working copy,
	 * the client should call
	 * {@link IServerConfigurationWorkingCopy#save(IProgressMonitor)}
	 * to bring the server configuration instance into existence.
	 * <p>
	 * [issue: A server runtime is primarily for building against.
	 * How is it that it can cough up a server configuration?]
	 * </p>
	 * <p>
	 * [issue: What this does is type-dependent. Perhaps these
	 * methods should instead be type-dependent API on 
	 * IServerConfigurationWorkingCopy(Delegate).]
	 * </p>
	 * <p>
	 * [issue: This method is declared as throwing CoreException.
	 * From a clients's point of view, what are the circumstances that
	 * cause this operation to fail?]
	 * </p>
	 * 
	 * @param id the id to assign to the server configuration instance;
	 * a generated id is used if id is <code>null</code> or an empty string
	 * @param file the file in the workspace where the server configuration
	 * instance is to be serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 * @param runtime a runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server configuration working copy with the given id
	 * @throws CoreException [missing]
	 */
	public IServerConfigurationWorkingCopy importFromRuntime(String id, IFile file, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
}