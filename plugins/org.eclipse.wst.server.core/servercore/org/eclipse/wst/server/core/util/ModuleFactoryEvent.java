/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
/**
 * 
 */
public class ModuleFactoryEvent implements IModuleFactoryEvent {
	protected String factoryId;
	protected IModule[] added;
	protected IModule[] removed;

	public ModuleFactoryEvent(String factoryId, IModule[] added, IModule[] removed) {
		this.factoryId = factoryId;
		this.added = added;
		this.removed = removed;
	}

	/**
	 * Returns the id of this factory.
	 * 
	 * @return java.lang.String
	 */
	public String getFactoryId() {
		return factoryId;
	}

	/**
	 * Returns any modules that have been added.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getAddedModules() {
		return added;
	}

	/**
	 * Returns any modules that have been removed.
	 * 
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getRemovedModules() {
		return removed;
	}
}
