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
/**
 * Publish controller.
 */
public class PublishControl {
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
	
	public String getMemento() {
		return memento;
	}
	
	public String getParentsRef() {
		return parents;
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
	
	public String toString() {
		return "PublishControl [" + memento + " " + isDirty + "]";
	}
}