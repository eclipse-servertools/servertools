/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
/**
 * An event fired when a module changes.
 */
public class ModuleEvent {
	protected IModule module;
	protected boolean isChanged;
	protected IModule[] added;
	protected IModule[] changed;
	protected IModule[] removed;

	class IModuleArtifact {
		IPath path;
		long timestamp;
	}

	/**
	 * Create a new module event.
	 * 
	 * @param module
	 * @param isChanged
	 * @param added
	 * @param changed
	 * @param removed
	 */
	public ModuleEvent(IModule module, boolean isChanged, IModule[] added, IModule[] changed, IModule[] removed) {
		this.module = module;
		this.isChanged = isChanged;
		this.added = added;
		this.changed = changed;
		this.removed = removed;
	}

	/**
	 * Returns the module that has been changed.
	 *
	 * @return the module
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Returns true if this module's settings have changed.
	 * 
	 * @return boolean
	 */
	public boolean isChanged() {
		return isChanged;
	}

	/**
	 * Returns any child modules that have been added.
	 * 
	 * @return the added child modules, or null if no modules have been added
	 */
	public IModule[] getAddedChildModules() {
		return added;
	}

	/**
	 * Returns any child modules that have been changed.
	 * 
	 * @return the changed child modules, or null if no modules have been changed
	 */
	public IModule[] getChangedChildModules() {
		return changed;
	}

	/**
	 * Returns any child modules that have been removed.
	 * 
	 * @return the removed child modules, or null if no modules have been removed
	 */
	public IModule[] getRemovedChildModules() {
		return removed;
	}

	/**
	 * Returns the module artifacts that have changed.
	 * 
	 * @return the changed module artifacts
	 */
	public IModuleArtifact[] getChangedArtifacts() {
		return null;
	}
}