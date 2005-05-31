/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.*;

import org.eclipse.wst.server.core.*;
/**
 * Helper class that stores preference information for the server tools.
 */
public class ModuleProperties {
	private static final String MODULE_DATA_FILE = "modules.xml";

	protected static ModuleProperties instance;
	protected Map modules;

	/**
	 * ModuleProperties constructor.
	 */
	protected ModuleProperties() {
		super();
		load();
		instance = this;
	}

	/**
	 * Return a static instance.
	 * 
	 * @return a static instance
	 */
	public static ModuleProperties getInstance() {
		if (instance == null)
			new ModuleProperties();
		return instance;
	}
	
	/**
	 * Load the data.
	 */
	private void load() {
		Trace.trace(Trace.FINEST, "Loading module info");
		String filename = ServerPlugin.getInstance().getStateLocation().append(MODULE_DATA_FILE).toOSString();
		modules = new HashMap();
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("module");
			int size = children.length;
			
			for (int i = 0; i < size; i++) {
				String moduleId = children[i].getString("moduleId");
				String serverId = children[i].getString("serverId");
				modules.put(moduleId, serverId);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load servers: " + e.getMessage());
		}
	}
	
	private void save(IProgressMonitor monitor) throws CoreException {
		String filename = ServerPlugin.getInstance().getStateLocation().append(MODULE_DATA_FILE).toOSString();
		
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("modules");

			Iterator iterator = modules.keySet().iterator();
			while (iterator.hasNext()) {
				String moduleId = (String) iterator.next();
				String serverId = (String) modules.get(moduleId);

				IMemento child = memento.createChild("module");
				child.putString("moduleId", moduleId);
				child.putString("serverId", serverId);
			}

			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save servers", e);
		}
	}

	/*
	 * @see ServerCore#getDefaultServer(IModule)
	 */
	public IServer getDefaultServer(IModule module) {
		if (module == null)
			throw new IllegalArgumentException();
		
		String serverId = (String) modules.get(module.getId());
		if (serverId == null || serverId.length() == 0)
			return null;
		
		return ServerCore.findServer(serverId);
	}

	/*
	 * @see ServerCore#setDefaultServer(IModule, IServer, IProgressMonitor)
	 */
	public void setDefaultServer(IModule module, IServer server, IProgressMonitor monitor) throws CoreException {
		if (module == null)
			throw new IllegalArgumentException();
		
		String newServerId = null;
		if (server != null)
			newServerId = server.getId();
		
		String serverId = (String) modules.get(module.getId());
		if (serverId == null && newServerId == null)
			return;
		if (serverId != null && serverId.equals(newServerId))
			return;
		
		modules.put(module.getId(), newServerId);
		save(monitor);
	}

	public String toString() {
		return "ModuleProperties[]";
	}
}