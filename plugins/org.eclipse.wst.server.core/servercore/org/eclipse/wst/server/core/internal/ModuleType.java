/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IModuleType;
/**
 * 
 */
public class ModuleType implements IModuleType {
	protected String id;
	protected String version;

	//	cached copy of all module kinds
	private static List<ModuleKind> moduleKinds;

	private static List<ModuleType> moduleTypes;

	public ModuleType(String id, String version) {
		super();
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		ModuleKind mt = findModuleType(id);
		if (mt != null)
			return mt.getName();
		return Messages.moduleTypeUnknown;
	}

	public String getVersion() {
		return version;
	}

	/**
	 * Returns the module type with the given id and version, or create a new one if
	 * none already exists.
	 * 
	 * @param id the module type id
	 * @param version the module type version
	 * @return the module type
	 */
	public synchronized static ModuleType getModuleType(String id, String version) {
		if (moduleTypes == null)
			moduleTypes = new ArrayList<ModuleType>();
		
		// look for an existing one first
		Iterator iterator = moduleTypes.iterator();
		while (iterator.hasNext()) {
			ModuleType mt = (ModuleType) iterator.next();
			if ((id == null && mt.id == null) || (id != null && id.equals(mt.id))) {
				if ((version == null && mt.version == null) || (version != null && version.equals(mt.version)))
					return mt;
			}
		}
		
		// otherwise create one
		ModuleType mt = new ModuleType(id, version);
		moduleTypes.add(mt);
		return mt;
	}

	/**
	 * Returns the module type with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * module types for the one a matching
	 * module type id ({@link ModuleType#getId()}). The id may not be null.
	 *
	 * @param id the module type id
	 * @return the module type, or <code>null</code> if there is no module type
	 * with the given id
	 */
	private static ModuleKind findModuleType(String id) {
		if (id == null)
			throw new IllegalArgumentException();

		if (moduleKinds == null)
			loadModuleTypes();
		
		Iterator iterator = moduleKinds.iterator();
		while (iterator.hasNext()) {
			ModuleKind moduleType = (ModuleKind) iterator.next();
			if (id.equals(moduleType.getId()))
				return moduleType;
		}
		return null;
	}

	/**
	 * Load the module types.
	 */
	private static synchronized void loadModuleTypes() {
		if (moduleKinds != null)
			return;
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "->- Loading .moduleTypes extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "moduleTypes");

		int size = cf.length;
		moduleKinds = new ArrayList<ModuleKind>(size);
		for (int i = 0; i < size; i++) {
			try {
				ModuleKind moduleType = new ModuleKind(cf[i]);
				moduleKinds.add(moduleType);
				if (Trace.EXTENSION_POINT) {
					Trace.trace(Trace.STRING_EXTENSION_POINT, "  Loaded moduleType: " + cf[i].getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "  Could not load moduleType: " + cf[i].getAttribute("id"), t);
				}
			}
		}
		
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "-<- Done loading .moduleTypes extension point -<-");
		}
	}

	public int hashCode() {
		int hash = 17;
		if (id != null)
			hash += id.hashCode();
		if (version != null)
			hash += version.hashCode();
		return hash;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof ModuleType))
			return false;
		
		ModuleType mt = (ModuleType) obj;
		if (!ServerPlugin.matches(id, mt.id))
			return false;
		
		if (!ServerPlugin.matches(version, mt.version))
			return false;
		
		return true;
	}

	public String toString() {
		return "ModuleType[" + id + ", " + version + "]";
	}
}