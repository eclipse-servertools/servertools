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

import java.util.*;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public abstract class ModuleFactoryDelegate2 extends ModuleFactoryDelegate {
	// modules - map from memento (String) to IModule
	protected Map modules;
	protected boolean cached = false;

	/**
	 * Construct a new ModuleFactoryDelegate2.
	 */
	public ModuleFactoryDelegate2() {
		modules = new HashMap();
	}

	/**
	 * Cache any existing modules into the modules Map.
	 */
	protected abstract void cacheModules();

	/**
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate2#getModule(String)
	 */
	public IModule getModule(String memento) {
		if (!cached) {
			cached = true;
			cacheModules();
		}

		try {
			return (IModule) modules.get(memento);
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate2#getModules()
	 */
	public IModule[] getModules() {
		if (!cached) {
			cached = true;
			cacheModules();
		}

		Collection list = modules.values();
		IModule[] m = new IModule[list.size()];
		list.toArray(m);
		return m;
	}
}