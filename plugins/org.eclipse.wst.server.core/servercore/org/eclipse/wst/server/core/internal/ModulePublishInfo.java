/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
/**
 * Publish information for a specific module on a specific server.
 */
public class ModulePublishInfo {
	private static final String MODULE_ID = "module-id";
	private static final String PARENT_IDS = "parent-ids";
	private static final String NAME = "name";
	private static final String PATH = "path";
	private static final String STAMP = "stamp";
	private static final String FILE = "file";
	private static final String FOLDER = "folder";

	private String moduleId;
	private String parentsId;
	private IModuleResource[] resources = new IModuleResource[0];

	/**
	 * ModulePublishInfo constructor comment.
	 */
	public ModulePublishInfo(String parentsId, String moduleId) {
		super();

		this.parentsId = parentsId;
		this.moduleId = moduleId;
	}
	
	/**
	 * ModulePublishInfo constructor comment.
	 */
	public ModulePublishInfo(IMemento memento) {
		super();
		
		load(memento);
	}
	
	public String getModuleId() {
		return moduleId;
	}
	
	public String getParentsId() {
		return parentsId;
	}
	
	public IModuleResource[] getResources() {
		return resources;
	}
	
	public void setResources(IModuleResource[] res) {
		resources = res;
	}
	
	/**
	 * 
	 */
	protected void load(IMemento memento) {
		Trace.trace(Trace.FINEST, "Loading module publish info for: " + memento);
	
		try {
			moduleId = memento.getString(MODULE_ID);
			parentsId = memento.getString(PARENT_IDS);
	
			resources = loadResource(memento);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load module publish info information: " + e.getMessage());
		}
	}
	
	protected IModuleResource[] loadResource(IMemento memento) {
		if (memento == null)
			return new IModuleResource[0];
		
		List list = new ArrayList(5);
		IMemento[] children = memento.getChildren(FILE);
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String name = children[i].getString(NAME);
				IPath path = new Path(children[i].getString(PATH));
				long stamp = Long.parseLong(children[i].getString(STAMP));
				ModuleFile file = new ModuleFile(name, path, stamp);
				list.add(file);
			}
		}
		children = memento.getChildren(FOLDER);
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String name = children[i].getString(NAME);
				IPath path = new Path(children[i].getString(PATH));
				ModuleFolder folder = new ModuleFolder(name, path);
				folder.setMembers(loadResource(children[i]));
				list.add(folder);
			}
		}
		
		IModuleResource[] resources2 = new IModuleResource[list.size()];
		list.toArray(resources2);
		return resources;
	}
	
	/**
	 * 
	 */
	protected void save(IMemento memento) {
		try {
			memento.putString(MODULE_ID, moduleId);
			memento.putString(PARENT_IDS, parentsId);
			
			saveResource(memento, resources);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save module publish info", e);
		}
	}
	
	protected void saveResource(IMemento memento, IModuleResource[] resources2) {
		if (resources2 == null)
			return;
		int size = resources2.length;
		for (int i = 0; i < size; i++) {
			if (resources2[i] instanceof IModuleFile) {
				IModuleFile file = (IModuleFile) resources2[i];
				IMemento child = memento.createChild(FILE);
				child.putString(NAME, file.getName());
				child.putString(PATH, file.getModuleRelativePath().toPortableString());
				child.putString(STAMP, "" + file.getModificationStamp());
			} else {
				IModuleFolder folder = (IModuleFolder) resources2[i];
				IMemento child = memento.createChild(FOLDER);
				child.putString(NAME, folder.getName());
				child.putString(PATH, folder.getModuleRelativePath().toPortableString());
				IModuleResource[] resources3 = folder.members();
				saveResource(child, resources3);
			}
		}
	}
	
	public String toString() {
		return "ModulePublishInfo [" + moduleId + "]";
	}
}