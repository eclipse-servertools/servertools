/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
/**
 * An event fired when a module changes.
 * 
 * @since 1.0
 */
public class ModuleEvent {
	private IModule module;
	private boolean isChanged;
	private IModule[] added;
	private IModule[] changed;
	private IModule[] removed;

	class IModuleArtifact {
		IPath path;
		long timestamp;
	}

	/**
	 * Create a new module event.
	 * 
	 * @param module the module that has been changed
	 * @param isChanged true if the module has changed
	 * @param added added child modules
	 * @param changed changed child modules
	 * @param removed removed child modules
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
	 * @return <code>true</code> if the contents have changed
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