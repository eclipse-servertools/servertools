/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import java.beans.PropertyChangeListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * A working copy server object used for formulating changes
 * to a server instance ({@link IServer}).
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IServerWorkingCopy extends IServerAttributes {
	public static final int TIMESTAMP_ERROR = 5;

	/**
	 * Sets the displayable name for this server.
	 * <p>
	 * The name should be appropriate for the current locale.
	 * </p>
	 *
	 * @param name a displayable name
	 * @see IServerAttributes#getName()
	 */
	public void setName(String name);
	
	/**
	 * Sets or unsets whether this server is marked as read only.
	 * When a server is read only, working copies can be created but
	 * they cannot be saved.
	 *
	 * @param readOnly <code>true</code> to set this server to be marked
	 *    read only, and <code>false</code> to unset
	 */
	public void setReadOnly(boolean readOnly);
	
	/**
	 * Sets whether this element is private.
	 * Generally speaking, elements marked private are internal ones
	 * that should not be shown to users (because they won't know
	 * anything about them).
	 * 
	 * @param b <code>true</code> if this element is private,
	 * and <code>false</code> otherwise
	 * @see IServerAttributes#isPrivate()
	 */
	public void setPrivate(boolean b);
	
	/**
	 * Returns whether this working copy has unsaved changes.
	 * 
	 * @return <code>true</code> if this working copy has unsaved
	 *    changes, and <code>false</code> otherwise
	 */
	public boolean isDirty();

	/**
	 * Adds a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Fires a property change event.
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue);

	/**
	 * Sets the server configuration associated with this server working copy.
	 * <p>
	 * Note: The server configuration of a server working copy may
	 * or may not be a working copy.
	 * </p>
	 * <p>
	 * [issue: According to serverType extension point, 
	 * configurationTypeId is an optional attribute. What happens if the
	 * server configuration passed is null but the server must have a
	 * server configuration? What happens of the server configuration
	 * has the wrong type? Do the errors get detected and reported now, or
	 * upon save()?]
	 * </p>
	 * 
	 * @param configuration the server configuration, or <code>null</code> if none
	 */
	public void setServerConfiguration(IFolder configuration);
	
	/**
	 * Returns the server instance that this working copy is
	 * associated with.
	 * <p>
	 * For a server working copy created by a call to
	 * {@link IServer#createWorkingCopy()},
	 * <code>this.getOriginal()</code> returns the original
	 * server object. For a server working copy just created by
	 * a call to {@link IServerType#createServer(String, IFile, IProgressMonitor)},
	 * <code>this.getOriginal()</code> returns <code>null</code>.
	 * </p>
	 * 
	 * @return the associated server instance, or <code>null</code> if none
	 */
	public IServer getOriginal();

	/**
	 * Commits the changes made in this working copy. If there is
	 * no extant server instance with a matching id and server
	 * type, this will create a server instance with attributes
	 * taken from this working copy. If there an existing server
	 * instance with a matching id and server type, this will
	 * change the server instance accordingly.
	 * <p>
	 * If there an existing server instance with a matching id and
	 * server type, this will change the server instance accordingly.
	 * The returned server will be the same server this is returned
	 * from getOriginal(), after the changes have been applied.
	 * Otherwise, this method will return a newly created server.
	 * </p>
	 * <p>
	 * Servers can be saved even when they have invalid properties. It
	 * is the clients responsibility to validate or check the
	 * properties before saving.
	 * </p>
	 * <p>
	 * This method does not apply changes to the server. A publish()
	 * must be completed to push out after the save to push out any
	 * changes to the server.
	 * </p>
	 * <p>
	 * [issue: What is lifecycle for ServerWorkingCopyDelegate
	 * associated with this working copy?]
	 * </p>
	 * <p>
	 * [issue: Since it does not make sense to commit a server
	 * working copy without first committing any associated
	 * runtime and server config working copies, the semantics
	 * of saveAll should be part and parcel of the
	 * normal save, and the saveAll method eliminated.]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server instance
	 * @throws CoreException [missing]
	 */
	public IServer save(boolean force, IProgressMonitor monitor) throws CoreException;

	/**
	 * Commits the changes made in this server working copy after
	 * first committing any associated server configuration or
	 * server runtime working copies.
	 * <p>
	 * This convenience method is equivalent to:
	 * <pre>
	 * IRuntime rt = this.getRuntime();
	 * if (rt != null && rt.isWorkingCopy()) {
	 *    ((IRuntimeWorkingCopy) rt).save(monitor);
	 * }
	 * IServerConfiguration cf = this.getServerConfiguration();
	 * if (cf != null && cf.isWorkingCopy()) {
	 *    ((IServerConfigurationWorkingCopy) cf).save(monitor);
	 * }
	 * return save(monitor);
	 * </pre>
	 * </p>
	 * <p>
	 * [issue: Since it does not make sense to commit a server
	 * working copy without first committing any associated
	 * runtime and server config working copies, the semantics
	 * of this operation should be part and parcel of the
	 * normal save, and the saveAll method eliminated.]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new server instance
	 * @throws CoreException [missing]
	 */
	public IServer saveAll(boolean force, IProgressMonitor monitor) throws CoreException;

	/**
	 * Sets the file where this server instance is serialized.
	 * 
	 * @param the file in the workspace where the server instance
	 * is serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 */
	public void setFile(IFile file);

	/**
	 * Sets the runtime associated with this server working copy.
	 * <p>
	 * Note: The runtime of a server working copy may
	 * or may not be a working copy.
	 * </p>
	 * <p>
	 * [issue: According to serverType extension point, 
	 * runtimeTypeId is a mandatory attribute. But IServer.getRuntime()
	 * is allowed to return null, suggesting that it is optional for instances.
	 * What happens if the runtime passed is null but the server must
	 * have a runtime? What happens if the runtime has the wrong
	 * type? Do the errors get detected and reported now, or upon save()?]
	 * </p>
	 * 
	 * @param runtime the runtime, or <code>null</code> if none
	 */
	public void setRuntime(IRuntime runtime);

	/**
	 * Changes the host for the server.
	 * The format of the host can be either a qualified or unqualified hostname,
	 * or an IP address and must conform to RFC 2732.
	 * 
	 * <p>
	 * [issue: This is a questionable operation if there is a running
	 * server associated with the original. When a host name
	 * change is committed, the server instance loses contact with
	 * the running server because of the host name change.]
	 * </p>
	 * 
	 * @param host a host string conforming to RFC 2732
	 * @see IServer#getHost()
	 * @see java.net.URL.getHost()
	 */
	public void setHost(String host);

	/**
	 * Modifies the list of modules associated with the server.
	 * The modules included in the <code>add</code> list
	 * must exist in the workspace and must not already be associated
	 * with the server.
	 * The modules included in the <code>remove</code> list
	 * must be associated with the server, but may or may not exist
	 * in the workspace.
	 * <p>
	 * This method will not communicate with the server. After saving,
	 * publish() can be used to sync up with the server.
	 * </p>
	 * <p>
	 * [issue: How to formulate what it means
	 * to say "the module must exist in the workspace"?]
	 * </p>
	 * <p>
	 * [issue: The spec should be more lax. Attempting to add
	 * a module that's already include should be quietly ignore;
	 * ditto removing a module that's not on this list. This
	 * simplifies the handling of various other wacko cases 
	 * such as duplication within and between the add and remove
	 * lists.]
	 * </p>
	 * <p>
	 * [issue: The spec had also said: "...canModifyModules()
	 * should have returned true. The configuration must assume
	 * any default settings and add the module without any UI."]
	 * </p>
	 * <p>
	 * [issue: What error checking should be performed by this
	 * operation, and what needs to be performed by save() if
	 * the client tries to commit these hypothetisized changes?]
	 * </p>
	 *
	 * @param add a possibly-empty list of modules to add
	 * @param remove a possibly-empty list of modules to remove
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException [missing]
	 */
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException;
}