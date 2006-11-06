/**********************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
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

	private String getKey(IModule[] module) {
		StringBuffer sb = new StringBuffer();
		
		if (module != null) {
			int size = module.length;
			for (int i = 0; i < size; i++) {
				if (i != 0)
					sb.append("#");
				if (module[i] != null)
					sb.append(module[i].getId());
				else
					sb.append("null");
			}
		}
		
		return sb.toString();
	}

	private String getKey(String moduleId) {
		return moduleId;
	}
	
	private IModule[] getModule(String moduleId) {
		if (moduleId == null || moduleId.length() == 0)
			return new IModule[0];
		
		List list = new ArrayList();
		StringTokenizer st = new StringTokenizer(moduleId, "#");
		while (st.hasMoreTokens()) {
			String mid = st.nextToken();
			if (mid != null && mid.length() > 0) {
				IModule m = ServerUtil.getModule(mid);
				if (m == null)
					return null;
				list.add(m);
			}
		}
		
		IModule[] modules = new IModule[list.size()];
		list.toArray(modules);
		return modules;
	}

	public boolean hasModulePublishInfo(IModule[] module) {
		String key = getKey(module);
		return modulePublishInfo.containsKey(key);
	}

	/*public void removeModulePublishInfo(IModule[] module) {
		String key = getKey(module);
		modulePublishInfo.remove(key);
		
		save();
	}*/

	/**
	 * 
	 * Note: save() must be called manually after making this call.
	 * 
	 * @param moduleList
	 * @deprecated Use removeDeletedModulePublishInfo(Server, List) instead
	 */
	public void removeDeletedModulePublishInfo(List moduleList) {
		removeDeletedModulePublishInfo(null, moduleList);
	}

	/**
	 * Removes successfully deleted modules from the next publish.
	 * Note: save() must be called manually after making this call.
	 * 
	 * @param server a server
	 * @param moduleList the modules currently on the server
	 */
	public void removeDeletedModulePublishInfo(Server server, List moduleList) {
		int size = moduleList.size();
		List removed = new ArrayList();
		
		Iterator iterator = modulePublishInfo.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			
			boolean found = false;
			for (int i = 0; i < size; i++) {
				IModule[] module = (IModule[]) moduleList.get(i);
				String key2 = getKey(module);
				if (key != null && key.equals(key2))
					found = true;
			}
			
			if (server != null) {
				try {
					Integer in = (Integer) server.modulePublishState.get(key);
					if (in != null && in.intValue() != IServer.PUBLISH_STATE_NONE)
						found = true;
				} catch (Exception e) {
					// ignore
				}
			}
			
			if (!found)
				removed.add(key);
		}
		
		iterator = removed.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			modulePublishInfo.remove(key);
		}
	}

	/**
	 * Return the publish state.
	 */
	protected ModulePublishInfo getModulePublishInfo(IModule[] module) {
		String key = getKey(module);
		
		// check if it now exists
		if (modulePublishInfo.containsKey(key))
			return (ModulePublishInfo) modulePublishInfo.get(key);
		
		// have to create a new one
		IModule mod = module[module.length - 1];
		ModulePublishInfo mpi = new ModulePublishInfo(getKey(module), mod.getName(), mod.getModuleType());
		modulePublishInfo.put(key, mpi);
		return mpi;
	}

	public void addRemovedModules(List moduleList, List kindList) {
		int size = moduleList.size();
		List removed = new ArrayList();
		Iterator iterator = modulePublishInfo.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
		
			boolean found = false;
			for (int i = 0; i < size; i++) {
				IModule[] module = (IModule[]) moduleList.get(i);
				String key2 = getKey(module);
				if (key != null && key.equals(key2))
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
			IModule[] module2 = getModule(mpi.getModuleId());
			if (module2 == null || module2.length == 0) {
				String moduleId = mpi.getModuleId();
				if (moduleId != null) {
					int index = moduleId.lastIndexOf("#");
					module2 = new IModule[] { new DeletedModule(moduleId.substring(index + 1), mpi.getName(), mpi.getModuleType()) };
				}
			}
			if (module2 != null && module2.length > 0) {
				moduleList.add(module2);
				kindList.add(new Integer(ServerBehaviourDelegate.REMOVED));
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
				modulePublishInfo.put(getKey(mpi.getModuleId()), mpi);
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

	/**
	 * 
	 * Note: save() must be called manually after making this call.
	 * @param module
	 */
	public void fill(IModule[] module) {
		ModulePublishInfo mpi = getModulePublishInfo(module);
		mpi.fill(module);
	}

	protected IModuleResourceDelta[] getDelta(IModule[] module) {
		if (module == null)
			return new IModuleResourceDelta[0];
		
		return getModulePublishInfo(module).getDelta(module);
	}

	protected IModuleResource[] getResources(IModule[] module) {
		if (module == null)
			return new IModuleResource[0];
		
		return getModulePublishInfo(module).getModuleResources(module);
	}

	protected static IModuleResourceDelta[] getDelta(IModuleResource[] original, IModuleResource[] current) {
		if (original == null || current == null)
			return new IModuleResourceDelta[0];
		
		List list = new ArrayList();
		int size = original.length;
		int size2 = current.length;
		
		Map originalMap = new HashMap(size);
		for (int i = 0; i < size; i++)
			originalMap.put(original[i], original[i]);
		
		// added and changed resources
		for (int i = 0; i < size2; i++) {
			IModuleResource old = (IModuleResource) originalMap.remove(current[i]);
			if (old == null) {
				ModuleResourceDelta delta = new ModuleResourceDelta(current[i], IModuleResourceDelta.ADDED);
				if (current[i] instanceof IModuleFolder) {
					IModuleFolder currentFolder = (IModuleFolder) current[i]; 
					delta.setChildren(getDeltaTree(currentFolder.members(), IModuleResourceDelta.ADDED));
				}
				list.add(delta);
			} else {
				if (current[i] instanceof IModuleFile) {
					// include files only if the modification stamp has changed
					IModuleFile mf1 = (IModuleFile) old;
					IModuleFile mf2 = (IModuleFile) current[i];
					if (mf1.getModificationStamp() != mf2.getModificationStamp()) {
						list.add(new ModuleResourceDelta(current[i], IModuleResourceDelta.CHANGED));
					}
				} else {
					// include folders only if their contents have changed
					IModuleFolder mf1 = (IModuleFolder) old;
					IModuleFolder mf2 = (IModuleFolder) current[i];
					IModuleResourceDelta[] mrdc = getDelta(mf1.members(), mf2.members());
					if (mrdc.length > 0) {
						ModuleResourceDelta mrd = new ModuleResourceDelta(current[i], IModuleResourceDelta.NO_CHANGE);
						mrd.setChildren(mrdc);
						list.add(mrd);
					}
				}
			}
		}
		
		// removed resources
		for (int i = 0; i < size; i++) {
			if (originalMap.containsKey(original[i])) {
				ModuleResourceDelta delta = new ModuleResourceDelta(original[i], IModuleResourceDelta.REMOVED);
				if (original[i] instanceof IModuleFolder) {
					IModuleFolder removedFolder = (IModuleFolder) original[i]; 
					delta.setChildren(getDeltaTree(removedFolder.members(), IModuleResourceDelta.REMOVED));
				}
				list.add(delta);
			}
		}
		
		return (IModuleResourceDelta[]) list.toArray(new IModuleResourceDelta[list.size()]);
	}

	protected boolean hasDelta(IModule[] module) {
		if (module == null)
			return false;
		
		return hasModulePublishInfo(module) 
            && getModulePublishInfo(module).hasDelta(module);
	}

	protected static boolean hasDelta(IModuleResource[] original, IModuleResource[] current) {
		if (original == null || current == null)
			return false;
		
		int size = original.length;
		int size2 = current.length;
		
		Map originalMap = new HashMap(size);
		for (int i = 0; i < size; i++)
			originalMap.put(original[i], original[i]);
		
		// added and changed resources
		for (int i = 0; i < size2; i++) {
			IModuleResource old = (IModuleResource) originalMap.remove(current[i]);
			if (old == null)
				return true;
			
			if (current[i] instanceof IModuleFile) {
				// include files only if the modification stamp has changed
				IModuleFile mf1 = (IModuleFile) old;
				IModuleFile mf2 = (IModuleFile) current[i];
				if (mf1.getModificationStamp() != mf2.getModificationStamp())
					return true;
			} else {
				// include folders only if their contents have changed
				IModuleFolder mf1 = (IModuleFolder) old;
				IModuleFolder mf2 = (IModuleFolder) current[i];
				if (hasDelta(mf1.members(), mf2.members()))
					return true;
			}
		}
		
		// removed resources
		return !originalMap.isEmpty();
	}

	/**
	 * Create a resource delta for an entire tree.
	 */
	private static IModuleResourceDelta[] getDeltaTree(IModuleResource[] resources, int kind) {
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

	/**
	 * Returns true if the list of modules being published does not match the previous
	 * list of published modules.
	 * 
	 * TODO: This method should compare the modules. For now, comparing the size is fine.
	 * 
	 * @param modules a list of modules
	 * @return <code>true</code> if the structure of published modules has changed, or
	 *    <code>false</code> otherwise
	 */
	protected boolean hasStructureChanged(List modules) {
		return modules.size() != modulePublishInfo.keySet().size();
	}

	/**
	 * Fill the module cache.
	 */
	public void startCaching() {
		Iterator iterator = modulePublishInfo.values().iterator();
		while (iterator.hasNext()) {
			ModulePublishInfo mpi = (ModulePublishInfo) iterator.next();
			mpi.startCaching();
		}
	}

	/**
	 * Clears all caches of current module resources and deltas.
	 */
	public void clearCache() {
		Iterator iterator = modulePublishInfo.values().iterator();
		while (iterator.hasNext()) {
			ModulePublishInfo mpi = (ModulePublishInfo) iterator.next();
			mpi.clearCache();
		}
	}
}