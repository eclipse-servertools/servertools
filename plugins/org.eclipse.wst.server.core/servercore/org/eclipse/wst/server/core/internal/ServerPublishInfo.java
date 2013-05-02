/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.*;
import java.util.*;

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
	private static final String VERSION = "version";

	protected IPath path;

	// map of module ids to ModulePublishInfo
	protected Map<String, ModulePublishInfo> modulePublishInfo;

	/**
	 * ServerPublishInfo constructor comment.
	 */
	protected ServerPublishInfo(IPath path) {
		super();
		
		this.path = path;
		modulePublishInfo = new HashMap<String, ModulePublishInfo>();
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
		
		List<IModule> list = new ArrayList<IModule>();
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
		synchronized (modulePublishInfo) {
			return modulePublishInfo.containsKey(key);
		}
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
		List<String> removed = new ArrayList<String>();
		
		synchronized (modulePublishInfo) {
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
						Integer in = server.modulePublishState.get(key);
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
	}

	/**
	 * Return the publish state.
	 */
	protected ModulePublishInfo getModulePublishInfo(IModule[] module) {
		String key = getKey(module);
		
		// check if it now exists
		synchronized (modulePublishInfo) {
			if (modulePublishInfo.containsKey(key))
				return modulePublishInfo.get(key);
			
			// have to create a new one
			IModule mod = module[module.length - 1];
			ModulePublishInfo mpi = new ModulePublishInfo(getKey(module), mod.getName(), mod.getModuleType(), mod.isExternal());
			modulePublishInfo.put(key, mpi);
			return mpi;
		}
	}

	public void addRemovedModules(List<IModule[]> moduleList) {
		int size = moduleList.size();
		List<ModulePublishInfo> removed = new ArrayList<ModulePublishInfo>();
		synchronized (modulePublishInfo) {
			Iterator iterator = modulePublishInfo.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
			
				boolean found = false;
				for (int i = 0; i < size; i++) {
					IModule[] module = moduleList.get(i);
					String key2 = getKey(module);
					if (key != null && key.equals(key2))
						found = true;
				}
				if (!found) {
					ModulePublishInfo mpi = modulePublishInfo.get(key);
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
						String[] ids = getModuleIds(moduleId);
						int depth = ids.length;
						
						module2 = new IModule[depth];
						String s = "";
						for (int i = 0; i < depth; i++) {
							s += ids[i];
							if (i == depth - 1)
								module2[i] = mpi.getDeletedModule();
							else {
								ModulePublishInfo mpi2 = modulePublishInfo.get(s);
								if (mpi2 != null)
									module2[i] = mpi2.getDeletedModule();
							}
							s += "#";
						}
					}
				}
				if (module2 != null && module2.length > 0)
					moduleList.add(module2);
			}
		}
	}

	/**
	 * Parse a combined module id string into the individual module ids
	 * @param moduleId
	 * @return an array of module ids
	 */
	private String[] getModuleIds(String moduleId) {
		StringTokenizer st = new StringTokenizer(moduleId, "#");
		List<String> list = new ArrayList<String>(2);
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}

	/**
	 * 
	 */
	public void load() {
		String filename = path.toOSString();
		
		if (new File(filename).exists()) {
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Loading publish info from " + filename);
			}
			
			DataInputStream in = null;
			try {
				in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
				in.readByte();
				in.readByte();
				// version
				int ver = in.readByte();
				if (ver <= 1) {
					int size = in.readInt();	
					for (int i = 0; i < size; i++) {
						ModulePublishInfo mpi = new ModulePublishInfo(in);
						modulePublishInfo.put(getKey(mpi.getModuleId()), mpi);
					}
					return;
				}
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not load publish information", e);
				}
			}
		}
		
		filename = filename.substring(0, filename.length() - 3) + "xml";
		if (new File(filename).exists()) {
			if (Trace.FINEST) {
				Trace.trace(Trace.STRING_FINEST, "Loading publish info from old format " + filename);
			}
			
			try {
				IMemento memento2 = XMLMemento.loadMemento(filename);
				Float f = memento2.getFloat(VERSION);
				if (f != null && f.floatValue() >= 3)
					return;
				
				IMemento[] children = memento2.getChildren("module");
				
				int size = children.length;
				for (int i = 0; i < size; i++) {
					ModulePublishInfo mpi = new ModulePublishInfo(children[i]);
					modulePublishInfo.put(getKey(mpi.getModuleId()), mpi);
				}
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not load publish information", e);
				}
			}
		}
	}

	/**
	 * 
	 */
	public void save() {
		String filename = path.toOSString();
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Saving publish info to " + filename);
		}
		
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			out.writeByte(14);
			out.writeByte(14);
			// version
			out.writeByte(1);
			
			synchronized (modulePublishInfo) {
				out.writeInt(modulePublishInfo.keySet().size());
				Iterator iterator = modulePublishInfo.keySet().iterator();
				while (iterator.hasNext()) {
					String controlRef = (String) iterator.next();
					ModulePublishInfo mpi = modulePublishInfo.get(controlRef);
					mpi.save(out);
				}
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not save publish information", e);
			}
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				// ignore
			}
		}
		
		// remove old file
		filename = filename.substring(0, filename.length() - 3) + "xml";
		File f = new File(filename);
		if (f.exists())
			f.delete();
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
		
		List<ModuleResourceDelta> list = new ArrayList<ModuleResourceDelta>();
		int size = original.length;
		int size2 = current.length;
		
		Map<IModuleResource, IModuleResource> originalMap = new HashMap<IModuleResource, IModuleResource>(size);
		for (int i = 0; i < size; i++){
			originalMap.put(original[i], original[i]);
		}
		
		// added and changed resources
		for (int i = 0; i < size2; i++) {
			IModuleResource old = originalMap.remove(current[i]);
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
		return list.toArray(new IModuleResourceDelta[list.size()]);
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
		
		Map<IModuleResource, IModuleResource> originalMap = new HashMap<IModuleResource, IModuleResource>(size);
		for (int i = 0; i < size; i++)
			originalMap.put(original[i], original[i]);
		
		// added and changed resources
		for (int i = 0; i < size2; i++) {
			IModuleResource old = originalMap.remove(current[i]);
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
		
		List<ModuleResourceDelta> list = new ArrayList<ModuleResourceDelta>();
		
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
		
		return list.toArray(new IModuleResourceDelta[list.size()]);
	}

	/**
	 * Returns true if the list of modules being published does not match the previous
	 * list of published modules.
	 * 
	 * @param modules a list of modules
	 * @return <code>true</code> if the structure of published modules has changed, or
	 *    <code>false</code> otherwise
	 */
	protected boolean hasStructureChanged(List<IModule[]> modules) {
		synchronized (modulePublishInfo) {
			// if the lists are different size, the structured changed
			if (modules.size() != modulePublishInfo.keySet().size())
				return true;
			
			// if the list are the same size, compare modules id
			final boolean[] changed = new boolean[1];
			
			for (IModule[] module:modules){
				String key = getKey(module);
				if (!modulePublishInfo.containsKey(key)){
					changed[0] = true;
				}
			}
			
			return changed[0];
		}
	}

	/**
	 * Fill the module cache.
	 */
	public void startCaching() {
		synchronized (modulePublishInfo) {
			Iterator iterator = modulePublishInfo.values().iterator();
			while (iterator.hasNext()) {
				ModulePublishInfo mpi = (ModulePublishInfo) iterator.next();
				mpi.startCaching();
			}
		}
	}

	/**
	 * Clears all caches of current module resources and deltas.
	 */
	public void clearCache() {
		synchronized (modulePublishInfo) {
			Iterator iterator = modulePublishInfo.values().iterator();
			while (iterator.hasNext()) {
				ModulePublishInfo mpi = (ModulePublishInfo) iterator.next();
				mpi.clearCache();
			}
		}
	}

	/**
	 * Recreates the cache for the specified {@link IModule}.
	 * 
	 * @param module The {@link IModule}
	 */
	public void rebuildCache(IModule[] module) {

		synchronized (modulePublishInfo) {
			final String publishInfoKey = this.getKey(module);
			ModulePublishInfo mpi = modulePublishInfo.get(publishInfoKey);
			if(mpi != null) {
				mpi.startCaching(); // clear out the resource list
				mpi.fill(module); // rebuild the resource list
			}
		}
	}
}