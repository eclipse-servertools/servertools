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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
/**
 * A module.
 * 
 * <p>This is the implementation of a module delegate, returned from the
 * module factories extension point.</p>
 */
public abstract class ModuleDelegate {
	private IModule module;
	
	public void initialize(IModule module2) {
		this.module = module2;
	}

	/**
	 * 
	 */
	protected IModule getModule() {
		return module;
	}

	/**
	 * Validates this module instance. See the specification of
	 * {@link IModule#validate()} for further details. Subclasses should
	 * override and call super.validate() for basic validation. 
	 * <p>
	 * This method is called by the web server core framework,
	 * in response to a call to <code>IModule.validate()</code>.
	 * Clients should never call this method.
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if this
	 * runtime is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public abstract IStatus validate();

	/**
	 * Returns the child modules of this module.
	 *
	 * @return org.eclipse.wst.server.core.IModule[]
	 */
	public abstract IModule[] getChildModules();
}