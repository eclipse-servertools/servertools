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
import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.resources.*;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * Publish controller.
 */
public class PublishControl implements IPublishControl {
	public String memento;
	public String parents;
	public boolean isDirty = true;
	public List resourceInfo = new ArrayList();

	public class ResourcePublishInfo {
		IPath localPath;
		long localTimestamp;
		IPath remotePath;
		long remoteTimestamp;

		public boolean equals(Object obj) {
			if (!(obj instanceof ResourcePublishInfo))
				return false;

			// return true if local or remote paths are equal
			ResourcePublishInfo rpi = (ResourcePublishInfo) obj;
			if (localPath == null && rpi.localPath == null)
				return true;
			else if (localPath != null && localPath.equals(rpi.localPath))
				return true;
			else if (remotePath != null && rpi.remotePath == null)
				return true;
			else if (remotePath != null && remotePath.equals(rpi.remotePath))
				return true;
			else
				return false;
		}
	}
	
	protected transient IPublisher publisher;
	protected transient IRemoteResource[] remoteResources;

	/**
	 * PublishControl constructor comment.
	 */
	public PublishControl(String parents, String memento) {
		super();

		this.parents = parents;
		this.memento = memento;
	}
	
	/**
	 * PublishControl constructor comment.
	 */
	public PublishControl(IMemento memento) {
		super();
		
		load(memento);
	}
	
	public void setPublisher(IPublisher publisher) {
		this.publisher = publisher;
	}
	
	public String getMemento() {
		return memento;
	}
	
	public String getParentsRef() {
		return parents;
	}

	/**
	 * 
	 */
	protected void fillRemoteResourceCache(IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(ServerPlugin.getResource("%caching"), 1000);
		
		if (publisher != null)
			remoteResources = publisher.getRemoteResources(ProgressUtil.getSubMonitorFor(monitor, 1000));
	
		monitor.done();
	}

	/**
	 * Returns the mapping of this file on the remote system. Returns
	 * null if this file should not be copied to the remote server.
	 *
	 * @param resource org.eclipse.wst.server.core.publish.IModuleResource
	 * @return org.eclipse.core.resources.IPath
	 */
	public IPath getMappedLocation(IModuleResource resource) {
		if (publisher == null)
			return null;
		return publisher.getMappedLocation(resource);
	}

	/**
	 * Returns the root level remote resources on the remote system.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public IRemoteResource[] getRemoteResources() {
		return remoteResources;
	}

	/**
	 * Returns true if there may be any files or folders within
	 * this container that should be mapped to the remote system.
	 * Returns false if files within this folder are never copied
	 * to the remote system.
	 *
	 * @param folder org.eclipse.wst.server.core.publish.IModuleFolder
	 * @return boolean
	 */
	public boolean shouldMapMembers(IModuleFolder folder) {
		if (publisher == null)
			return false;
		return publisher.shouldMapMembers(folder);
	}
	
	/**
	 * Returns the timestamp of the remote resource on the remote
	 * machine after it was last published.
	 *
	 * @param resource org.eclipse.wst.server.publish.IRemoteResource
	 * @return long
	 */
	public long getPublishedTimestamp(IRemoteResource resource) {
		if (resource == null)
			return IRemoteResource.TIMESTAMP_UNKNOWN;
	
		IPath remotePath = resource.getPath();
		if (remotePath == null)
			return IRemoteResource.TIMESTAMP_UNKNOWN;
	
		Iterator iterator = resourceInfo.iterator();
		while (iterator.hasNext()) {
			ResourcePublishInfo rpi = (ResourcePublishInfo) iterator.next();
			if (remotePath.equals(rpi.remotePath))
				return rpi.remoteTimestamp;
		}
	
		return IRemoteResource.TIMESTAMP_UNKNOWN;
	}

	/**
	 * Returns the timestamp that the resource was last published.
	 *
	 * @param resource org.eclipse.wst.server.publish.IModuleResource
	 * @return long
	 */
	public long getPublishedTimestamp(IModuleResource resource) {
		if (resource == null)
			return IRemoteResource.TIMESTAMP_UNKNOWN;
	
		IPath resourcePath = resource.getPath();
		if (resourcePath == null)
			return IRemoteResource.TIMESTAMP_UNKNOWN;
	
		Iterator iterator = resourceInfo.iterator();
		while (iterator.hasNext()) {
			ResourcePublishInfo rpi = (ResourcePublishInfo) iterator.next();
			if (resourcePath.equals(rpi.localPath))
				return rpi.localTimestamp;
		}
	
		return IRemoteResource.TIMESTAMP_UNKNOWN;
	}
	
	/**
	 * Returns true if the project is dirty.
	 *
	 * @return boolean
	 */
	public boolean isDirty() {
		return isDirty;
	}
	
	/**
	 * Sets the dirty flag.
	 *
	 * @param boolean
	 */
	public void setDirty(boolean b) {
		isDirty = b;
	}
	
	/**
	 * 
	 */
	protected void load(IMemento memento2) {
		Trace.trace(Trace.FINEST, "Loading publish control for: " + memento2);
	
		try {
			memento = memento2.getString("memento");
			parents = memento2.getString("parents");
			/*String temp = projectChild[i].getString("dirty");
			if ("true".equals(temp))
				ppi.isDirty = true;
			else
				ppi.isDirty = false;*/
	
			IMemento[] resourceChild = memento2.getChildren("resource");
			int size2 = resourceChild.length;
			resourceInfo = new ArrayList(size2 + 5);
			for (int j = 0; j < size2; j++) {
				ResourcePublishInfo rpi = new ResourcePublishInfo();
				String temp = resourceChild[j].getString("localPath");
				if (temp != null && temp.length() > 1)
					rpi.localPath = new Path(temp);
				temp = resourceChild[j].getString("remotePath");
				if (temp != null && temp.length() > 1)
					rpi.remotePath = new Path(temp);
				temp = resourceChild[j].getString("localTimestamp");
				if (temp != null && temp.length() > 1)
					rpi.localTimestamp = Long.parseLong(temp);
				temp = resourceChild[j].getString("remoteTimestamp");
				if (temp != null && temp.length() > 1)
					rpi.remoteTimestamp = Long.parseLong(temp);
				resourceInfo.add(rpi);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load publish control information: " + e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	protected void save(IMemento memento2) {
		try {
			memento2.putString("memento", memento);
			memento2.putString("parents", parents);
			/*if (ppi.isDirty)
				project.putString("dirty", "true");
			else
				project.putString("dirty", "false");*/
	
			Iterator ite = resourceInfo.iterator();
			while (ite.hasNext()) {
				ResourcePublishInfo rpi = (ResourcePublishInfo) ite.next();
				IMemento resource = memento2.createChild("resource");
	
				if (rpi.localPath != null) {
					resource.putString("localPath", rpi.localPath.toString());
					resource.putString("localTimestamp", new Long(rpi.localTimestamp).toString());
				}
				if (rpi.remotePath != null) {
					resource.putString("remotePath", rpi.remotePath.toString());
					resource.putString("remoteTimestamp", new Long(rpi.remoteTimestamp).toString());
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save publish control info", e);
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
	
	protected IRemoteResource findRemoteResource(IRemoteResource[] resources, IPath path) {
		if (resources == null || path == null)
			return null;
	
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
	 * Verify the files were published and update info.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @param publisher org.eclipse.wst.server.core.model.IDeployTargetPublisher
	 * @param verifyList java.util.List
	 */
	protected void verify(List verifyList, List deleteList, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "Verifying resource state info");
		try {
			IRemoteResource[] resources = publisher.getRemoteResources(monitor);
	
			Iterator iterator = verifyList.iterator();
			while (iterator.hasNext()) {
				IModuleResource resource = (IModuleResource) iterator.next();
	
				ResourcePublishInfo rpi = new ResourcePublishInfo();
				rpi.localPath = resource.getPath();
				rpi.localTimestamp = resource.getTimestamp();
	
				IPath path = publisher.getMappedLocation(resource);
				IRemoteResource remote = findRemoteResource(resources, path);
				Trace.trace(Trace.FINEST, "Verifying resource: " + resource + " " + remote);
				if (remote != null) {
					rpi.remotePath = remote.getPath();
					rpi.remoteTimestamp = remote.getTimestamp();
				}
	
				// remove any previous entry for this resource
				if (resourceInfo.contains(rpi))
					resourceInfo.remove(rpi);

				// add new one
				resourceInfo.add(rpi);
			}
	
			iterator = deleteList.iterator();
			while (iterator.hasNext()) {
				IRemoteResource remote = (IRemoteResource) iterator.next();
				ResourcePublishInfo rpi = new ResourcePublishInfo();
				rpi.remotePath = remote.getPath();
				resourceInfo.remove(rpi);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error verifying resource state info", e);
		}
	}
	
	public String toString() {
		return "PublishControl [" + memento + " " + isDirty + "]";
	}
}
