/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
/**
 * A module delegate provides a mechanism for discovering information
 * about individual modules. Modules are returned from module factory
 * delegates; their delegates are created when
 * ModuleFactoryDelegate.createModule() is called.
 * <p>
 * When the module needs to be given a delegate, the delegate class
 * specified for the module is instantiated with a 0-argument
 * constructor.
 * </p>
 * <p>
 * Module delegates may keep state in instance fields, but that state is
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
 * @see org.eclipse.wst.server.core.IModule
 * @see ModuleFactoryDelegate
 * @since 1.0
 */
public abstract class ModuleDelegate {
	private IModule module;

	/**
	 * Delegates must have a public 0-arg constructor.
	 */
	public ModuleDelegate() {
		// do nothing
	}

	/**
	 * Initializes this module delegate with its life-long module instance.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 * 
	 * @param newModule the module instance
	 */
	public final void initialize(IModule newModule) {
		this.module = newModule;
		initialize();
	}

	/**
	 * Initializes this module delegate. This method gives delegates a chance
	 * to do their own initialization.
	 * <p>
	 * This method is called by the server core framework.
	 * Clients should never call this method.
	 * </p>
	 */
	public void initialize() {
		// do nothing
	}

	/**
	 * Returns the module that this module delegate corresponds to.
	 * 
	 * @return the module
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Validates this module instance. See the specification of
	 * {@link IModule#validate(IProgressMonitor)} for further details. Subclasses should
	 * override and call super.validate() for basic validation. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IModule.validate(IProgressMonitor)</code>.
	 * Clients should never call this method.
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 *   runtime is valid, otherwise a status object indicating what is
	 *   wrong with it
	 */
	public abstract IStatus validate();

	/**
	 * Returns the child modules of this module.
	 *
	 * @return a possibly empty array of child modules
	 */
	public abstract IModule[] getChildModules();
	
	/**
	 * Returns the current array of module artifacts.
	 * 
	 * @return a possibly empty array containing the module resources
	 * @throws CoreException thrown if there is a problem getting the members
	 */
	public abstract IModuleResource[] members() throws CoreException;
}