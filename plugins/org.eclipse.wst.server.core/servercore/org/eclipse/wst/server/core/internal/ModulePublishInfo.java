/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ModuleDelegate;
/**
 * Publish information for a specific module on a specific server.
 */
public class ModulePublishInfo {
	private static final IModuleResource[] EMPTY_MODULE_RESOURCE = new IModuleResource[0];
	private static final IModuleResourceDelta[] EMPTY_MODULE_RESOURCE_DELTA = new IModuleResourceDelta[0];
	private static final String MODULE_ID = "module-ids";
	private static final String NAME = "name";
	private static final String MODULE_TYPE_ID = "module-type-id";
	private static final String MODULE_TYPE_VERSION = "module-type-version";
	private static final String STAMP = "stamp";
	private static final String FILE = "file";
	private static final String FOLDER = "folder";

	private String moduleId;
	private String name;
	private IModuleResource[] resources = EMPTY_MODULE_RESOURCE;
	private IModuleType moduleType;

	private boolean useCache;
	private IModuleResource[] currentResources = null;
	private IModuleResourceDelta[] delta = null;
	private boolean hasDelta;

	/**
	 * ModulePublishInfo constructor.
	 * 
	 * @param moduleId a module id
	 * @param name the module's name
	 * @param moduleType the module type
	 */
	public ModulePublishInfo(String moduleId, String name, IModuleType moduleType) {
		super();

		this.moduleId = moduleId;
		this.name = name;
		this.moduleType = moduleType;
	}

	/**
	 * ModulePublishInfo constructor.
	 * 
	 * @param memento a memento
	 */
	public ModulePublishInfo(IMemento memento) {
		super();
		
		load(memento);
	}

	/**
	 * ModulePublishInfo constructor.
	 * 
	 * @param in an input stream
	 * @throws IOException if the load fails
	 */
	public ModulePublishInfo(DataInput in) throws IOException {
		super();
		
		load(in);
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getName() {
		return name;
	}

	public IModuleType getModuleType() {
		return moduleType;
	}

	public IModuleResource[] getResources() {
		return resources;
	}

	public void setResources(IModuleResource[] res) {
		resources = res;
	}

	/**
	 * Used only for reading from WTP 1.x workspaces.
	 */
	protected void load(IMemento memento) {
		Trace.trace(Trace.FINEST, "Loading module publish info for: " + memento);
		
		try {
			moduleId = memento.getString(MODULE_ID);
			name = memento.getString(NAME);
			String mt = memento.getString(MODULE_TYPE_ID);
			String mv = memento.getString(MODULE_TYPE_VERSION);
			if (mt != null && mt.length() > 0)
				moduleType = ModuleType.getModuleType(mt, mv);
			
			resources = loadResource(memento, new Path(""));
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load module publish info information", e);
		}
	}

	/**
	 * Used only for reading from WTP 1.x workspaces.
	 */
	protected IModuleResource[] loadResource(IMemento memento, IPath path) {
		if (memento == null)
			return EMPTY_MODULE_RESOURCE;
		
		List<IModuleResource> list = new ArrayList<IModuleResource>(10);
		
		// load files
		IMemento[] children = memento.getChildren(FILE);
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String name2 = children[i].getString(NAME);
				long stamp = Long.parseLong(children[i].getString(STAMP));
				ModuleFile file = new ModuleFile(name2, path, stamp);
				list.add(file);
			}
		}
		
		// load folders
		children = memento.getChildren(FOLDER);
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String name2 = children[i].getString(NAME);
				ModuleFolder folder = new ModuleFolder(null, name2, path);
				folder.setMembers(loadResource(children[i], path.append(name2)));
				list.add(folder);
			}
		}
		
		IModuleResource[] resources2 = new IModuleResource[list.size()];
		list.toArray(resources2);
		return resources2;
	}

	protected void load(DataInput in) throws IOException {
		Trace.trace(Trace.FINEST, "Loading module publish info");
		
		moduleId = in.readUTF();
		byte b = in.readByte();
		
		if ((b & 1) != 0)
			name = in.readUTF();
		else
			name = null;
		
		if ((b & 2) != 0) {
			String mt = in.readUTF();
			String mv = in.readUTF();
			if (mt != null && mt.length() > 0)
				moduleType = ModuleType.getModuleType(mt, mv);
		} else
			moduleType = null;
		
		resources = loadResource(in, new Path(""));
	}

	private IModuleResource[] loadResource(DataInput in, IPath path) throws IOException {
		int size = in.readInt();
		IModuleResource[] resources2 = new IModuleResource[size];
		
		for (int i = 0; i < size; i++) {
			byte b = in.readByte();
			if (b == 0) {
				String name2 = in.readUTF();
				long stamp = in.readLong();
				resources2[i] = new ModuleFile(name2, path, stamp);
			} else if (b == 1) {
				String name2 = in.readUTF();
				ModuleFolder folder = new ModuleFolder(null, name2, path);
				folder.setMembers(loadResource(in, path.append(name2)));
				resources2[i] = folder;
			}
		}
		
		return resources2;
	}

	protected void save(DataOutput out) {
		try {
			out.writeUTF(moduleId);
			byte b = 0;
			if (name != null)
				b &= 1;
			if (moduleType != null)
				b &= 2;
			out.writeByte(b);
			
			if (name != null)
				out.writeUTF(name);
			
			if (moduleType != null) {
				out.writeUTF(moduleType.getId());
				out.writeUTF(moduleType.getVersion());
			}
			saveResource(out, resources);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save module publish info", e);
		}
	}

	protected void saveResource(DataOutput out, IModuleResource[] resources2) throws IOException {
		if (resources2 == null)
			return;
		int size = resources2.length;
		out.writeInt(0);
		for (int i = 0; i < size; i++) {
			if (resources2[i] instanceof IModuleFile) {
				IModuleFile file = (IModuleFile) resources2[i];
				out.writeByte(0);
				out.writeUTF(file.getName());
				out.writeLong(file.getModificationStamp());
			} else {
				IModuleFolder folder = (IModuleFolder) resources2[i];
				out.writeByte(1);
				out.writeUTF(folder.getName());
				IModuleResource[] resources3 = folder.members();
				saveResource(out, resources3);
			}
		}
	}

	/**
	 * Start using the module cache.
	 */
	protected void startCaching() {
		useCache = true;
		currentResources = null;
		delta = null;
		hasDelta = false;
	}

	/**
	 * Fill the module cache.
	 * 
	 * @param module
	 */
	private void fillCache(IModule[] module) {
		if (!useCache)
			return;
		
		if (currentResources != null)
			return;
		
		try {
			long time = System.currentTimeMillis();
			IModule m = module[module.length - 1];
			ModuleDelegate pm = (ModuleDelegate) m.loadAdapter(ModuleDelegate.class, null);
			if (pm == null || (m.getProject() != null && !m.getProject().isAccessible()))
				currentResources = EMPTY_MODULE_RESOURCE;
			else
				currentResources = pm.members();
			
			delta = ServerPublishInfo.getDelta(resources, currentResources);
			hasDelta = (delta != null && delta.length > 0);
			Trace.trace(Trace.PERFORMANCE, "Filling publish cache for " + m.getName() + ": " + (System.currentTimeMillis() - time));
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Couldn't fill publish cache for " + module);
		}
		if (delta == null)
			delta = EMPTY_MODULE_RESOURCE_DELTA;
	}

	protected void clearCache() {
		useCache = false;
		currentResources = null;
		delta = null;
		hasDelta = false;
	}

	protected IModuleResource[] getModuleResources(IModule[] module) {
		if (module == null)
			return EMPTY_MODULE_RESOURCE;
		
		if (useCache) {
			fillCache(module);
			return currentResources;
		}
		
		int size = module.length;
		IModule m = module[size - 1];
		ModuleDelegate pm = (ModuleDelegate) m.loadAdapter(ModuleDelegate.class, null);
		if (pm == null || (m.getProject() != null && !m.getProject().isAccessible()))
			return EMPTY_MODULE_RESOURCE;
		
		try {
			long time = System.currentTimeMillis();
			IModuleResource[] x = pm.members();
			Trace.trace(Trace.PERFORMANCE, "Time to get members() for " + module[size - 1].getName() + ": " + (System.currentTimeMillis() - time));
			return x;
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Possible failure in getModuleResources", ce);
		}
		return EMPTY_MODULE_RESOURCE;
	}

	protected IModuleResourceDelta[] getDelta(IModule[] module) {
		if (module == null)
			return EMPTY_MODULE_RESOURCE_DELTA;
		
		if (useCache) {
			fillCache(module);
			return delta;
		}
		
		IModule m = module[module.length - 1];
		ModuleDelegate pm = (ModuleDelegate) m.loadAdapter(ModuleDelegate.class, null);
		if (pm == null || (m.getProject() != null && !m.getProject().isAccessible()))
			return EMPTY_MODULE_RESOURCE_DELTA;
		
		IModuleResource[] resources2 = null;
		try {
			resources2 = pm.members();
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Possible failure in getDelta", ce);
		}
		if (resources2 == null)
			resources2 = EMPTY_MODULE_RESOURCE;
		return ServerPublishInfo.getDelta(getResources(), resources2);
	}

	protected boolean hasDelta(IModule[] module) {
		if (module == null)
			return false;
		
		if (useCache) {
			fillCache(module);
			return hasDelta;
		}
		
		IModule m = module[module.length - 1];
		ModuleDelegate pm = (ModuleDelegate) m.loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] resources2 = null;
		if (pm == null || (m.getProject() != null && !m.getProject().isAccessible()))
			return false;
		
		try {
			resources2 = pm.members();
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Possible failure in hasDelta", ce);
		}
		if (resources2 == null)
			resources2 = EMPTY_MODULE_RESOURCE;
		return ServerPublishInfo.hasDelta(getResources(), resources2);
	}

	public void fill(IModule[] module) {
		if (module == null)
			return;
		
		if (useCache) {
			fillCache(module);
			setResources(currentResources);
			return;
		}
		
		IModule m = module[module.length - 1];
		ModuleDelegate pm = (ModuleDelegate) m.loadAdapter(ModuleDelegate.class, null);
		if (pm == null || (m.getProject() != null && !m.getProject().isAccessible())) {
			setResources(EMPTY_MODULE_RESOURCE);
			return;
		}
		
		try {
			setResources(pm.members());
		} catch (CoreException ce) {
			Trace.trace(Trace.WARNING, "Possible failure in fill", ce);
		}
	}

	/**
	 * Return a deleted module that represents this module.
	 * 
	 * @return a module
	 */
	protected IModule getDeletedModule() {
		String id = moduleId;
		int index = id.lastIndexOf("#");
		if (index > 0)
			id = id.substring(index+1);
		return new DeletedModule(id, name, moduleType);
	}

	public String toString() {
		return "ModulePublishInfo [" + moduleId + "]";
	}
}