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
package org.eclipse.wst.server.core.internal;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.resources.*;
/**
 * Abstract class for publishing support.
 */
public abstract class AbstractPublisher implements IPublishManagerDelegate {
	protected Map deleteMap;
	protected Map publishMap;

	protected List delete;
	protected List publish;
	protected IModule module;

	protected Map resourceMap;

	protected IPublishControl control;
	
	private static final List EMPTY_LIST = new ArrayList(0);

	/**
	 * AbstractPublisher constructor comment.
	 */
	public AbstractPublisher() {
		super();
	}
	
	/**
	 * 
	 */
	protected void deleteResources(IRemoteResource remote, List delete2, List visited, IProgressMonitor monitor) {
		if (remote == null)
			return;
	
		/*if (!visited.contains(resource)) {
			delete.add(resource);
		}*/
		IModuleResource resource = null;
		try {
			resource = (IModuleResource) resourceMap.get(remote.getPath());
		} catch (Exception e) {
			//Trace.trace("Error in abstract publisher", e);
		}
	
		if (shouldDelete(resource, null, remote, IRemoteResource.TIMESTAMP_UNKNOWN, control.getPublishedTimestamp(remote))) {
			delete2.add(remote);
		}
	
		if (remote instanceof IRemoteFolder) {
			IRemoteFolder folder = (IRemoteFolder) remote;
			Iterator iterator = folder.getContents().iterator();
			while (iterator.hasNext()) {
				IRemoteResource sub = (IRemoteResource) iterator.next();
				deleteResources(sub, delete2, visited, monitor);
			}
		}
	}
	
	protected IRemoteResource findRemoteResource(IRemoteFolder folder, IPath path) {
		Iterator iterator = folder.getContents().iterator();
		while (iterator.hasNext()) {
			IRemoteResource remote = (IRemoteResource) iterator.next();
			if (path.equals(remote.getPath()))
				return remote;
			if (remote instanceof IRemoteFolder) {
				IRemoteFolder folder2 = (IRemoteFolder) remote;
				IPath folderPath = folder2.getPath();
				if (folderPath.isPrefixOf(path)) {
					IRemoteResource rem = findRemoteResource(folder2, path);
					if (rem != null)
						return rem;
				}
			}
		}
		return null;
	}
	
	protected IRemoteResource findRemoteResource(IPath path) {
		if (path == null)
			return null;
	
		IRemoteResource[] resources = control.getRemoteResources();
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			IRemoteResource remote = resources[i];
			
			if (path.equals(remote.getPath()))
				return remote;
			if (remote instanceof IRemoteFolder) {
				IRemoteFolder folder2 = (IRemoteFolder) remote;
				IPath folderPath = folder2.getPath();
				if (folderPath.isPrefixOf(path)) {
					IRemoteResource rem = findRemoteResource(folder2, path);
					if (rem != null)
						return rem;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the list of remote resources to delete from the
	 * remote machine.
	 *
	 * @return java.util.List
	 */
	public List getResourcesToDelete(IModule module2) {
		try {
			return (List) deleteMap.get(module2);
		} catch (Exception e) {
			return EMPTY_LIST;
		}
	}
	
	/**
	 * Returns the list of resources to publish to the
	 * remote machine.
	 *
	 * @return java.util.List
	 */
	public List getResourcesToPublish(IModule module2) {
		try {
			return (List) publishMap.get(module2);
		} catch (Exception e) {
			return EMPTY_LIST;
		}
	}
	
	/**
	 * 
	 */
	protected void publishResources(IModuleResource resource, List publish2, List visited, IProgressMonitor monitor) {
		if (resource == null)
			return;
	
		IPath path = control.getMappedLocation(resource);
		resourceMap.put(path, resource);
	
		if (path != null) {
			IRemoteResource remote = findRemoteResource(path);
			if (!(resource instanceof IModuleFolder && remote != null)) { // don't republish directories that already exist
				if (shouldPublish(resource, path, remote, control.getPublishedTimestamp(resource), control.getPublishedTimestamp(remote))) {
					publish2.add(resource);
				}
			}
		}
	
		if (resource instanceof IModuleFolder) {
			IModuleFolder cont = (IModuleFolder) resource;
			if (control.shouldMapMembers(cont)) {
				try {
					IModuleResource[] sub = cont.members();
					if (sub != null) {
						int size = sub.length;
						for (int i = 0; i < size; i++)
							publishResources(sub[i], publish2, visited, monitor);
					}
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error in abstract publisher", e);
				}
			}
		}
	}
	
	/**
	 * Sets the publish control, used to obtain information about
	 * the publishing.
	 *
	 * Sets the publish state, used to determine the timestamps
	 * of the last publishing action.
	 * 
	 * Resolve which resources to publish or delete.
	 *
	 * @param controls org.eclipse.wst.server.model.IPublishControl[]
	 * @param modules org.eclipse.wst.server.core.model.IModule[]
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void resolve(IPublishControl[] controls, IModule[] modules, IProgressMonitor monitor) {
		int size = controls.length;
		Trace.trace(Trace.FINEST, "Abstract publisher starting " + size);
		
		deleteMap = new HashMap(size);
		publishMap = new HashMap(size);
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "Resolving: " + modules[i]);
			this.control = controls[i];
			this.module = modules[i];
			
			delete = new ArrayList();
			publish = new ArrayList();

			resourceMap = new HashMap();

			resolveModule(monitor);
			
			Trace.trace(Trace.FINEST, "Deleting " + delete.size() + " resources");
			Trace.trace(Trace.FINEST, "Publishing " + publish.size() + " resources");
			deleteMap.put(modules[i], delete);
			publishMap.put(modules[i], publish);
		}
	}
	
	/**
	 * Resolve which resources to publish or delete. Both setXxx()
	 * methods will have been called before this method.
	 */
	protected void resolveModule(IProgressMonitor monitor) {
		List visited = new ArrayList();
	
		// first, decide which files to publish
		Trace.trace(Trace.FINEST, "Resolving publish for " + module);
		try {
			publish = new ArrayList();
			IModuleResource[] resources = module.members();
			if (resources != null) {
				int size = resources.length;
				for (int i = 0; i < size; i++)
					publishResources(resources[i], publish, visited, monitor);
			}
			sortPublishList(publish);
		} catch (Exception e) {
		}
	
		// next, choose which files to delete
		Trace.trace(Trace.FINEST, "Resolving delete for " + module);
		delete = new ArrayList();
		IRemoteResource[] resources = control.getRemoteResources();
		if (resources != null) {
			int size = resources.length;
			for (int i = 0; i < size; i++) {
				IRemoteResource remote = resources[i];
				deleteResources(remote, delete, visited, monitor);
			}
		}
		sortDeletionList(delete);
	}

	/**
	 * Returns true if the remote resource should be deleted, and false
	 * if it should be left on the server.
	 *
	 * @return boolean
	 * @param option int
	 */
	public abstract boolean shouldDelete(IModuleResource resource, IPath path, IRemoteResource remote, long resourceTimestamp, long remoteTimestamp);

	/**
	 * Returns true if the resource should be published, and false
	 * if it should be left on the server.
	 *
	 * @return boolean
	 * @param option int
	 */
	public abstract boolean shouldPublish(IModuleResource resource, IPath path, IRemoteResource remote, long resourceTimestamp, long remoteTimestamp);

	/**
	 * Sorts the deletions so that directories are last and
	 * appear in "reverse depth order".
	 */
	public static void sortDeletionList(List list) {
		if (list == null)
			return;
	
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				Object a = list.get(i);
				Object b = list.get(j);
	
				boolean swap = false;
				if (a instanceof IRemoteFolder && !(b instanceof IRemoteFolder))
					swap = true;
				else if (a instanceof IRemoteFolder && b instanceof IRemoteFolder) {
					IRemoteFolder rfa = (IRemoteFolder) a;
					IRemoteFolder rfb = (IRemoteFolder) b;
					if (rfa.getPath().isPrefixOf(rfb.getPath()))
						swap = true;
				}
	
				if (swap) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
	}
	
	/**
	 * Sorts the publishes so that directories are first and are
	 * in "depth order". (subdirectories appear after their parents)
	 */
	public static void sortPublishList(List list) {
		if (list == null)
			return;
	
		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				Object a = list.get(i);
				Object b = list.get(j);
	
				boolean swap = false;
				if (!(a instanceof IModuleFolder) && b instanceof IModuleFolder)
					swap = true;
				else if (a instanceof IModuleFolder && b instanceof IModuleFolder) {
					IModuleFolder ca = (IModuleFolder) a;
					IModuleFolder cb = (IModuleFolder) b;
					if (cb.getPath().isPrefixOf(ca.getPath()))
						swap = true;
				}
	
				if (swap) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
	}
}
