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
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
/**
 * Represents a specialized server delegate for servers capable of
 * restarting individual modules while the server is running.
 * <p>
 * [issue: This should be folded in to IServerDelegate. If
 * the server delegate is represented as an abstract class as
 * recommended elsewhere, all these methods could have default
 * implementations appropriate for a server that does not support 
 * restartable modules. IServerDelegate.supportsModuleRestart()
 * could answer whether module restart was possible in pinciple
 * (false by default).]
 * </p>
 * <p>
 * Concrete module types are represented by concrete classes
 * implementing this interface. The only legitimate reason
 * to declare a subclass is to implement a module factory.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IRestartableModule extends IServerDelegate {
	
	/**
	 * Returns whether the given module can be restarted.
	 * <p>
	 * [issue: It's unclear whether this operations is guaranteed to be fast
	 * or whether it could involve communication with any actual
	 * server. If it is not fast, the method should take a progress
	 * monitor.]
	 * </p>
	 *
	 * @param module the module
	 * @return <code>true</code> if the given module can be
	 * restarted, and <code>false</code> otherwise 
	 */
	public boolean canRestartModule(IModule module);

	/**
	 * Check if the given module is in sync on the server. It should
	 * return true if the module should be restarted (is out of
	 * sync) or false if the module does not need to be restarted.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	public boolean isModuleRestartNeeded(IModule module);

	/**
	 * Asynchronously restarts the given module on the server.
	 * See the specification of 
	 * {@link IServer#synchronousModuleRestart(IModule, IProgressMonitor)}
	 * for further details. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IServer.synchronousModuleRestart</code>.
	 * Clients should never call this method.
	 * </p>
	 * <p>
	 * The implementation should update the module sync state and fire
	 * an event for the module.
	 * </p>
	 * <p>
	 * [issue: It should probably be spec'd to throw an exception error if the
	 * given module is not associated with the server.]
	 * </p>
	 * <p>
	 * [issue: Since this method is ascynchronous, is there
	 * any need for the progress monitor?]
	 * </p>
	 * <p>
	 * [issue: Since this method is ascynchronous, how can
	 * it return a meaningful IStatus? 
	 * And IServer.synchronousModuleRestart throws CoreException
	 * if anything goes wrong.]
	 * </p>
	 * <p>
	 * [issue: If the module was just published to the server
	 * and had never been started, would is be ok to "start"
	 * the module using this method?]
	 * </p>
	 * 
	 * @param module the module to be started
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return status object
	 * @exception CoreException if an error occurs while trying to restart the module
	 */
	public IStatus restartModule(IModule module, IProgressMonitor monitor) throws CoreException;
}