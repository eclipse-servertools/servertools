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
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.*;
/**
 * 
 */
public abstract class ModuleFactoryDelegate2 extends ModuleFactoryDelegate {
	// modules - map from memento (String) to IModule
	protected Map modules;
	protected boolean cached = false;

	// change listeners
	private transient List listeners;

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
		} catch (Exception e) { }
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

	/**
	 * Add a listener to the module factory.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener) {
		Trace.trace(Trace.FINEST, "Adding module factory listener " + listener + " to " + this);
	
		if (listeners == null)
			listeners = new ArrayList();
		else if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the module factory.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleFactoryListener
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener) {
		Trace.trace(Trace.FINEST, "Removing module factory listener " + listener + " from " + this);
	
		if (listeners != null)
			listeners.remove(listener);
	}

	/**
	 * Fire a module factory event.
	 */
	protected void fireModuleFactoryEvent(IModule[] added, IModule[] removed) {
		Trace.trace(Trace.FINEST, "->- Firing module factory event: " + toString() + " ->-");

		if (listeners == null || listeners.isEmpty())
			return;

		int size = listeners.size();
		IModuleFactoryListener[] dfl = new IModuleFactoryListener[size];
		listeners.toArray(dfl);
		
		IModuleFactoryEvent event = new ModuleFactoryEvent(getFactoryId(), added, removed);
		
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.FINEST, "  Firing module factory event to: " + dfl[i]);
				dfl[i].moduleFactoryChanged(event);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module factory event", e);
			}
		}
		Trace.trace(Trace.FINEST, "-<- Done firing module factory event -<-");
	}

	/**
	 * Return the factory ID for this module factory.
	 *
	 * @return java.lang.String
	 */
	public abstract String getFactoryId();
}
