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
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleEvent;
/**
 * 
 */
public class ModuleEvent implements IModuleEvent {
	protected IModule module;
	protected boolean isChanged;
	protected IModule[] added;
	protected IModule[] changed;
	protected IModule[] removed;

	public ModuleEvent(IModule module, boolean isChanged, IModule[] added, IModule[] changed, IModule[] removed) {
		this.module = module;
		this.isChanged = isChanged;
		this.added = added;
		this.changed = changed;
		this.removed = removed;
	}
	
	/**
	 * @see org.eclipse.wst.server.core.model.IModuleEvent#getModule()
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * @see org.eclipse.wst.server.core.model.IModuleEvent#isChanged()
	 */
	public boolean isChanged() {
		return isChanged;
	}

	/**
	 * @see org.eclipse.wst.server.core.model.IModuleEvent#getAddedChildModules()
	 */
	public IModule[] getAddedChildModules() {
		return added;
	}

	/**
	 * @see org.eclipse.wst.server.core.model.IModuleEvent#getModifiedChildModules()
	 */
	public IModule[] getChangedChildModules() {
		return changed;
	}

	/**
	 * @see org.eclipse.wst.server.core.model.IModuleEvent#getRemovedChildModules()
	 */
	public IModule[] getRemovedChildModules() {
		return removed;
	}
}