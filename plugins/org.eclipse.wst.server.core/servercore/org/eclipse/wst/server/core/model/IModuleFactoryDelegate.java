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

import java.util.List;
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
 * [issue: Since service providers must implement this class, it is
 * more flexible to provide an abstract class than an interface. It is
 * not a breaking change to add non-abstract methods to an abstract class.]
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
public interface IModuleFactoryDelegate {

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
	public IModule getModule(String memento);

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
	 * [issue: Consistency: IServer.getModules returns IModule[] rather than List<IModule>.]
	 * </p>
	 * <p>
	 * [issue: The list returned is precious. You would not want a client
	 * to accidentally or malicously whack it. Normal practice is to
	 * return an array instead of a List, and to return a new copy each call.
	 * This allows the spec to say that the client can do what they want
	 * with the result, and that it won't change under foot.
	 * Another alternative is to return a UnmodifiableList implementation
	 * so that clients cannot modify.
	 * The trick here is that this is a method that a service
	 * provider is supposed to implement. It is unwise to trust a service
	 * provider to return an UnmodifiableList, it would be
	 * wasteful to add an unmodifiable wrapper on every call and incorrect
	 * to cache one. Returning an IModule[] seems the best
	 * option.]
	 * </p>
	 * 
	 * @return a possibly-empty list of modules (element type:
	 * {@link IModule}
	 */
	public List getModules();
	
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
	public void addModuleFactoryListener(IModuleFactoryListener listener);

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
	public void removeModuleFactoryListener(IModuleFactoryListener listener);
}
