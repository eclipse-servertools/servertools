/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.File;
import java.util.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.*;
/**
 * Helper to obtain and store the publishing information (what files
 * were published and when) for a single server.
 */
public class ServerPublishInfo {
	protected IPath path;

	// map of module ids to ModulePublishInfo
	protected Map modulePublishInfo;

	/**
	 * ServerPublishInfo constructor comment.
	 */
	protected ServerPublishInfo(IPath path) {
		super();
		
		this.path = path;
		modulePublishInfo = new HashMap();
		load();
	}

	private String getKey(IModule[] parents, IModule module) {
		StringBuffer sb = new StringBuffer();
		
		if (parents != null) {
			int size = parents.length;
			for (int i = 0; i < size; i++) {
				sb.append(parents[i].getId());
				sb.append("#");
			}
		}
		
		sb.append(module.getId());
		return sb.toString();
	}

	private String getKey(String parentIds, String moduleId) {
		StringBuffer sb = new StringBuffer();
		
		if (parentIds != null && parentIds.length() > 0) {
			sb.append(parentIds);
			sb.append("#");
		}
		
		sb.append(moduleId);
		return sb.toString();
	}

	private String getParentsId(IModule[] parents) {
		StringBuffer sb = new StringBuffer();
		
		if (parents != null) {
			int size = parents.length;
			for (int i = 0; i < size; i++) {
				sb.append(parents[i].getId());
				sb.append("#");
			}
		}
		
		return sb.toString();
	}
	
	private IModule[] getParents(String parentIds) {
		if (parentIds == null || parentIds.length() == 0)
			return new IModule[0];
		
		List list = new ArrayList();
		StringTokenizer st = new StringTokenizer(parentIds, "#");
		while (st.hasMoreTokens()) {
			String parentId = st.nextToken();
			if (parentId != null && parentId.length() > 0) {
				list.add(ServerUtil.getModule(parentId));
			}
		}
		
		IModule[] parents = new IModule[list.size()];
		list.toArray(parents);
		return parents;
	}

	public boolean hasModulePublishInfo(IModule[] parents, IModule module) {
		String key = getKey(parents, module);
		return modulePublishInfo.containsKey(key);
	}

	public void removeModulePublishInfo(IModule[] parents, IModule module) {
		String key = getKey(parents, module);
		modulePublishInfo.remove(key);
	}

	/**
	 * Return the publish state.
	 * 
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public ModulePublishInfo getModulePublishInfo(IModule[] parents, IModule module) {
		String key = getKey(parents, module);

		// check if it now exists
		if (modulePublishInfo.containsKey(key))
			return (ModulePublishInfo) modulePublishInfo.get(key);
	
		// have to create a new one
		ModulePublishInfo mpi = new ModulePublishInfo(getParentsId(parents), module.getId());
		modulePublishInfo.put(key, mpi);
		return mpi;
	}

	public void addRemovedModules(List parentList, List moduleList, List kindList) {
		int size = parentList.size();
		List removed = new ArrayList();
		Iterator iterator = modulePublishInfo.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
		
			boolean found = false;
			for (int i = 0; i < size; i++) {
				IModule[] parents = (IModule[]) parentList.get(i);
				IModule module = (IModule) moduleList.get(i);
				String key2 = getKey(parents, module);
				if (key.equals(key2))
					found = true;
			}
			if (!found) {
				ModulePublishInfo mpi = (ModulePublishInfo) modulePublishInfo.get(key);
				removed.add(mpi);
			}
		}
		
		iterator = removed.iterator();
		while (iterator.hasNext()) {
			ModulePublishInfo mpi = (ModulePublishInfo) iterator.next();
			IModule[] parents2 = getParents(mpi.getParentsId());
			String moduleId = mpi.getModuleId();
			IModule module2 = ServerUtil.getModule(moduleId);
			if (module2 == null) {
				module2 = new DeletedModule(moduleId);
				/*int index = moduleId.indexOf(":");
				if (index > 0) {
					String moduleSubId = moduleId.substring(index+1);
					module2 = new DeletedModule(moduleSubId);
				}*/
			}
			if (module2 != null) {
				parentList.add(parents2);
				moduleList.add(module2);
				kindList.add(new Integer(IServer.REMOVED));
			}
		}
	}

	/**
	 * 
	 */
	public void load() {
		String filename = path.toOSString();
		if (!(new File(filename).exists()))
			return;
	
		Trace.trace(Trace.FINEST, "Loading publish info from " + filename);

		try {
			IMemento memento2 = XMLMemento.loadMemento(filename);
			IMemento[] children = memento2.getChildren("module");
	
			int size = children.length;
			for (int i = 0; i < size; i++) {
				ModulePublishInfo mpi = new ModulePublishInfo(children[i]);
				modulePublishInfo.put(getKey(mpi.getParentsId(), mpi.getModuleId()), mpi);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load publish information: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void save() {
		String filename = path.toOSString();
		Trace.trace(Trace.FINEST, "Saving publish info to " + filename);
	
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("server");

			Iterator iterator = modulePublishInfo.keySet().iterator();
			while (iterator.hasNext()) {
				String controlRef = (String) iterator.next();
				ModulePublishInfo mpi = (ModulePublishInfo) modulePublishInfo.get(controlRef);
				IMemento child = memento.createChild("module");
				mpi.save(child);
			}
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save publish information", e);
		}
	}
	
	public void fill(IModule[] parents, IModule module) {
		ModulePublishInfo mpi = getModulePublishInfo(parents, module);
		ModuleDelegate pm = (ModuleDelegate) module.getAdapter(ModuleDelegate.class);
		try {
			mpi.setResources(pm.members());
		} catch (CoreException ce) {
			// ignore
		}
	}

	public IModuleResourceDelta[] getDelta(IModule[] parents, IModule module) {
		ModulePublishInfo mpi = getModulePublishInfo(parents, module);
		ModuleDelegate pm = (ModuleDelegate) module.getAdapter(ModuleDelegate.class);
		IModuleResource[] resources = null;
		try {
			resources = pm.members();
		} catch (CoreException ce) {
			// ignore
		}
		if (resources == null)
			resources = new IModuleResource[0];
		return getDelta(mpi.getResources(), resources);
	}

	protected IModuleResourceDelta[] getDelta(IModuleResource[] original, IModuleResource[] current) {
		if (original == null || current == null)
			return new IModuleResourceDelta[0];
	
		List list = new ArrayList();
		
		// look for duplicates
		List found = new ArrayList();
		int size = original.length;
		int size2 = current.length;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size2; j++) {
				if (!found.contains(original[i]) && !found.contains(current[i]) && original[i].equals(current[j])) {
					// found a match
					found.add(original[i]);
					if (original[i] instanceof IModuleFile) {
						// include files only if the modification stamp has changed
						IModuleFile mf1 = (IModuleFile) original[i];
						IModuleFile mf2 = (IModuleFile) current[j];
						if (mf1.getModificationStamp() != mf2.getModificationStamp()) {
							list.add(new ModuleResourceDelta(original[i], IModuleResourceDelta.CHANGED));
						}
					} else {
						// include folders only if their contents have changed
						IModuleFolder mf1 = (IModuleFolder) original[i];
						IModuleFolder mf2 = (IModuleFolder) current[j];
						IModuleResourceDelta[] mrdc = getDelta(mf1.members(), mf2.members());
						if (mrdc.length > 0) {
							ModuleResourceDelta mrd = new ModuleResourceDelta(original[i], IModuleResourceDelta.NO_CHANGE);
							mrd.setChildren(mrdc);
							list.add(mrd);
						}
					}
				}
			}
		}
		
		// add deletions (unfound items in the original list)
		for (int i = 0; i < size; i++) {
			if (!found.contains(original[i])) {
				if (original[i] instanceof IModuleFile) {
					list.add(new ModuleResourceDelta(original[i], IModuleResourceDelta.REMOVED));
				} else {
					IModuleFolder mf = (IModuleFolder) original[i];
					ModuleResourceDelta mrd = new ModuleResourceDelta(original[i], IModuleResourceDelta.REMOVED);
					IModuleResourceDelta[] mrdc = getDeltaTree(mf.members(), IModuleResourceDelta.REMOVED);
					mrd.setChildren(mrdc);
					list.add(mrd);
				}
			}
		}
		
		//	add additions (unfound items in the current list)
		for (int j = 0; j < size2; j++) {
			if (!found.contains(current[j])) {
				if (current[j] instanceof IModuleFile) {
					list.add(new ModuleResourceDelta(current[j], IModuleResourceDelta.ADDED));
				} else {
					IModuleFolder mf = (IModuleFolder) current[j];
					ModuleResourceDelta mrd = new ModuleResourceDelta(current[j], IModuleResourceDelta.ADDED);
					IModuleResourceDelta[] mrdc = getDeltaTree(mf.members(), IModuleResourceDelta.ADDED);
					mrd.setChildren(mrdc);
					list.add(mrd);
				}
			}
		}
		
		IModuleResourceDelta[] delta = new IModuleResourceDelta[list.size()];
		list.toArray(delta);
		return delta;
	}

	/**
	 * Create a resource delta for an entire tree.
	 */
	protected IModuleResourceDelta[] getDeltaTree(IModuleResource[] resources, int kind) {
		if (resources == null)
			return new IModuleResourceDelta[0];
	
		List list = new ArrayList();
		
		// look for duplicates
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			ModuleResourceDelta mrd = new ModuleResourceDelta(resources[i], kind);
			if (resources[i] instanceof IModuleFolder) {
				IModuleFolder mf = (IModuleFolder) resources[i];
				mrd.setChildren(getDeltaTree(mf.members(), kind));
			}
			list.add(mrd);
		}
		
		IModuleResourceDelta[] delta = new IModuleResourceDelta[list.size()];
		list.toArray(delta);
		return delta;
	}
}