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
/**
 * An event fired when a module changes.
 */
public interface IModuleEvent {
	/**
	 * Returns the module that has been changed.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule
	 */
	public IModule getModule();

	/**
	 * Returns true if this module's settings have changed.
	 * 
	 * @return boolean
	 */
	public boolean isChanged();

	/**
	 * Returns any child modules that have been added.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getAddedChildModules();

	/**
	 * Returns any child modules that have been changed.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getChangedChildModules();

	/**
	 * Returns any child modules that have been removed.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getRemovedChildModules();
}
