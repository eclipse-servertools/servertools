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

import org.eclipse.wst.server.core.IModule;

/**
 * An event fired when a module factory changes.
 */
public interface IModuleFactoryEvent {
	/**
	 * Returns the id of this factory.
	 * 
	 * @return java.lang.String
	 */
	public String getFactoryId();

	/**
	 * Returns any modules that have been added.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getAddedModules();

	/**
	 * Returns any modules that have been removed.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getRemovedModules();
}