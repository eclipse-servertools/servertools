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

import org.eclipse.wst.server.core.IModule;
/**
 * A module factory delegate provides a mechanism for discovering
 * modules. A module factory delegate is specified by the
 * <code>class</code> attribute of a <code>moduleFactories</code> extension.
 * <p>
 * When the module factory needs to be given a delegate, the delegate class
 * specified for the module factory is instantiated with a 0-argument
 * constructor.
 * </p>
 * <p>
 * [issue: 2 differences from server delegate.
 * (1) module factory delegate is associated with the module factory
 * itself (server delegates are associated with each server instance
 * (2) the module factory delegate has no backpoint to its IModuleFactory.
 * The first is ok; the second is problematic because there is
 * protocol on IModuleFactory that the delegate might need, such
 * as the module factory id. Should add an initialize(IModuleFactory)
 * method and spec that initialize is called at creation time.]
 * </p>
 * <p>
 * Module factory delegates may keep state in instance fields, but that state is
 * transient and will not be persisted across workbench sessions.
 * </p>
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @see org.eclipse.wst.server.core.IModuleFactory#getDelegate()
 * @since 1.0
 */
public abstract class ModuleFactoryDelegate {
	/**
	 * Finds a module create by this factory with the given id.
	 * See the specification of
	 * {@link org.eclipse.wst.server.core.IModuleFactory#getModule(String)}
	 * for further details. 
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to {@link IModuleFactory#getModule(String)}.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * 
	 * @param id the module id
	 * @return the module with the given id, or <code>null</code>
	 * if none
	 */
	public abstract IModule getModule(String memento);

	/**
	 * Return all modules created by this factory.
	 * See the specification of
	 * {@link org.eclipse.wst.server.core.IModuleFactory#getModules()}
	 * for further details. 
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to {@link IModuleFactory#getModules()}.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of modules {@link IModule}
	 */
	public abstract IModule[] getModules();
	
	/**
	 * Adds the given listener to this module factory.
	 * Once registered, a listener starts receiving notification of 
	 * modules are added/removed. The listener continues to receive
	 * notifications until it is removed.
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to
	 * {@link IModuleFactory#addModuleFactoryListener(IModuleFactoryListener)}.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 * <p>
	 * [issue: Duplicate server listeners should be ignored.]
	 * </p>
	 *
	 * @param listener the module factory listener to add
	 * @see #removeModuleFactoryListener(IModuleFactoryListener)
	 */
	public abstract void addModuleFactoryListener(IModuleFactoryListener listener);

	/**
	 * Removes the given listener from this module factory.
	 * Has no effect if the listener is not registered.
	 * <p>
	 * This method is normally called by the web server core framework,
	 * in response to a call to
	 * {@link IModuleFactory#removeModuleFactoryListener(IModuleFactoryListener)}.
	 * Clients (other than the delegate) should never call this method.
	 * </p>
	 *
	 * @param listener the module factory listener to remove
	 * @see #addModuleFactoryListener(IModuleFactoryListener)
	 */
	public abstract void removeModuleFactoryListener(IModuleFactoryListener listener);
}