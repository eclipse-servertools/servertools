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
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Represents a server type from which server instances can be created.
 * <p>
 * The server core framework supports
 * an open-ended set of server types, which are contributed via
 * the <code>serverTypes</code> extension point in the server core
 * plug-in. Server type objects carry no state (all information is
 * read-only and is supplied by the server type declaration).
 * The global list of known server types is available via
 * {@link ServerCore#getServerTypes()}. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * [issue: Equality/identify for server types? Are IServerType
 * instances guaranteed to be canonical (client can use ==),
 * or is it possible for there to be non-identical IServerType
 * objects in play that both represent the same server type?
 * The latter is the more common; type should spec equals.]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IServerType {
	/**
	 * Constant (value 0) indicating that a type of server that can be
	 * directly started and stopped.
	 * 
	 * @see #getServerStateSet()
	 */
	public static final int SERVER_STATE_SET_MANAGED = 0;
	
	/**
	 * Constant (value 1) indicating that a type of server that can be
	 * attached to, typically for debugging.
	 * 
	 * @see #getServerStateSet()
	 */
	public static final int SERVER_STATE_SET_ATTACHED = 1;
	
	/**
	 * Constant (value 2) indicating that a type of server that
	 * can only be used for publishing.
	 * 
	 * @see #getServerStateSet()
	 */
	public static final int SERVER_STATE_SET_PUBLISHED = 2;

	/**
	 * Returns the id of this server type.
	 * Each known server type has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the server type id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this server type.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this server type
	 */
	public String getName();

	/**
	 * Returns the displayable description for this server type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this server type
	 */
	public String getDescription();

	/**
	 * Returns the type of server runtime that this type
	 * of server requires.
	 * <p>
	 * [issue: "runtimeTypeId" is mandatory according the
	 * serverTypes schema. This suggests that all types
	 * of servers have a server runtime. But there is also
	 * a boolean "runtime" attribute indicating whether the
	 * server requires a runtime. I supect that server type
	 * has an optional server runtime, in which case you
	 * can make "runtimeTypeId" optional and dispense with
	 * "runtime".]
	 * </p>
	 * <p>
	 * [issue: Does it really make sense for
	 * runtimeTypes and serverTypes be separate extension
	 * points? Would it not be sufficient to have the party declaring
	 * the server type also declare the server runtime type?
	 * Having runtimeType as a separate extension point
	 * only makes sense if it would be possible in principle to 
	 * declare a server runtime type that could actually be
	 * used on serveral server types. If server runtimes
	 * always end up being server-type specific, it would be better
	 * to combine them.]
	 * </p>
	 * <p>
	 * [issue: What should happen when a server type mentions
	 * the id of a server runtime type that is not known
	 * to the system?]
	 * </p>
	 * 
	 * @return a server runtime type
	 */
	public IRuntimeType getRuntimeType();
	
	/**
	 * Returns whether this type of server requires a server
	 * runtime.
	 * <p>
	 * [issue: See issues on getRuntimeType(). I suspect this
	 * method is unnecessary, and that 
	 * this.getRuntimeType() != null will do.]
	 * </p>
	 * 
	 * @return <code>true</code> if this type of server requires
	 * a server runtime, and <code>false</code> if it does not
	 * @see #getRuntimeType()
	 */
	public boolean hasRuntime();
	
	/**
	 * Returns whether this type of server supports the given launch mode.
	 * <p>
	 * [issue: It also seems odd that this is part of the server type
	 * declaration. This means that any server type has to commit
	 * so early on which modes it supports.]
	 * </p>
	 * <p>
	 * [issue: Because the spec for this method piggy-backs off the
	 * debug API, this method is vulnerable to any expansion that may
	 * happen there. For instance, a 3rd mode (PROFILE_MODE) was added in 3.0.
	 * As spec'd, clients can reasonably expect to launch servers
	 * in this mode as well. It may also make sense for the server
	 * core to define its own notion of legal modes.]
	 * </p>
	 * 
	 * @param launchMode a mode in which a server can be launched,
	 * one of the mode constants defined by
	 * {@link org.eclipse.debug.core.ILaunchManager}
	 * @return whether this type of server supports the given mode
	 */
	public boolean supportsLaunchMode(String launchMode);

	/**
	 * Returns the server state set that should for instances of this server type.
	 * If the state set is {@link #SERVER_STATE_SET_MANAGED}, this is
	 * a runnable server that may be directly started and stopped 
	 * (i.e., it should be represented as starting, started in debug mode,
	 * etc.) If the state set is {@link #SERVER_STATE_SET_ATTACHED}, this is a
	 * server that can be attached to, typically for debugging purposes
	 * (i.e., it should be represented as attaching, attached for
	 * debugging, etc.).
	 * If the state set is {@link #SERVER_STATE_SET_PUBLISHED}, this is a
	 * server that only be published to (i.e., it should be represented as
	 * not having states).
	 * <p>
	 * [issue: The notion of a "server state set" is a little abstruce.
	 * It feels like the main thing to capture in the server type
	 * is that there are 3 different styles of servers, and this
	 * has a bearing on how you control them. That would suggest
	 * there will be different rules for operating on them, and
	 * that they might cycle through different sets of states.
	 *  ]
	 * </p>
	 * <p>
	 * [issue: It also seems odd that this is part of the server type
	 * declaration. This means that any server type has to commit
	 * so early on which one it is.]
	 * </p>
	 *
	 * @return one of {@link #SERVER_STATE_SET_MANAGED},
	 * {@link #SERVER_STATE_SET_ATTACHED}, or
	 * {@link #SERVER_STATE_SET_PUBLISHED}
	 */
	public int getServerStateSet();

	/**
	 * Returns whether this type of server requires a server
	 * configuration.
	 * <p>
	 * [issue: It's not clear how this method differs from 
	 * this.getServerConfigurationType() != null]
	 * </p>
	 * 
	 * @return <code>true</code> if this type of server requires
	 * a server configuration, and <code>false</code> if it does not
	 * @see #getServerConfigurationType()
	 */
	public boolean hasServerConfiguration();

	/**
	 * Returns <code>true</code> if this type of server can run on a remote host.
	 * Returns <code>false</code> if the server type can only be run on "localhost"
	 * (the local machine). 
	 * <p>
	 * [issue: Should be renamed "supportsRemoteHost" (no "s").]
	 * </p>
	 * <p>
	 * [issue: Again, it seems odd to me that this is something
	 * hard-wired to a server type.]
	 * </p>
	 * 
	 * @return <code>true</code> if this type of server can run on
	 * a remote host, and <code>false</code> if it cannot
	 */
	public boolean supportsRemoteHosts();

	/**
	 * Creates an working copy instance of this server type.
	 * After setting various properties of the working copy,
	 * the client should call {@link IServerWorkingCopy#save(IProgressMonitor)}
	 * to bring the server instance into existence.
	 * <p>
	 * [issue: Why is a runtime passed in? 
	 * IServerWorkingCopy.setRuntime(runtime) could be called on
	 * the result to accomplish the same thing.]
	 * </p>
	 * <p>
	 * [issue: The implementation of this method never creates a server
	 * config working copy, whereas the other one does!?]
	 * Consider combining the method with the other.]
	 * </p>
	 * <p>
	 * The server returned from this method will have it's
	 * host set to "localhost".
	 * </p>
	 * <p>
	 * [issue: This method is declared as throwing CoreException.
	 * From a clients's point of view, what are the circumstances that
	 * cause this operation to fail?]
	 * </p>
	 * 
	 * @param id the id to assign to the server instance; a generated
	 * id is used if id is <code>null</code> or an empty string
	 * @param file the file in the workspace where the server instance
	 * is to be serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 * @param runtime the runtime to associate with the server instance,
	 * or <code>null</code> if none
	 * @return a new server working copy with the given id
	 * @throws CoreException [missing]
	 */
	public IServerWorkingCopy createServer(String id, IFile file, IRuntime runtime, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Creates a working copy instance of this server type.
	 * After setting various properties of the working copy,
	 * the client should call {@link IServerWorkingCopy#save(IProgressMonitor)}
	 * to bring the server instance into existence.
	 * <p>
	 * [issue: Since this method just creates a working copy,
	 * it's not clear the operation is long-running and in need
	 * of a progress monitor.]
	 * </p>
	 * <p>
	 * The server returned from this method will have it's
	 * host set to "localhost".
	 * </p>
	 * <p>
	 * [issue: The implementation of this method creates a server
	 * config working copy, whereas the other one does not!?
	 * Consider combining the method with the other.]
	 * </p>
	 * <p>
	 * [issue: This method is declared as throwing CoreException.
	 * From a clients's point of view, what are the circumstances that
	 * cause this operation to fail?]
	 * </p>
	 * 
	 * @param id the id to assign to the server instance; a generated
	 *    id is used if id is <code>null</code> or an empty string
	 * @param file the file in the workspace where the server instance
	 * is to be serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server working copy with the given id
	 * @throws CoreException [missing]
	 */
	public IServerWorkingCopy createServer(String id, IFile file, IProgressMonitor monitor) throws CoreException;

	/**
	 * Return the timeout (in ms) that should be used to wait for the server to start.
	 * Returns -1 if there is no timeout.
	 * 
	 * @return the server startup timeout
	 */
	public int getStartTimeout();

	/**
	 * Return the timeout (in ms) to wait before assuming that the server
	 * has failed to stop. Returns -1 if there is no timeout.
	 * 
	 * @return the server shutdown timeout
	 */
	public int getStopTimeout();
}