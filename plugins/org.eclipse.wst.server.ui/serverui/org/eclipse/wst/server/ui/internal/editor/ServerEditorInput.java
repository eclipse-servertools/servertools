/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.editor.IServerEditorInput;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
/**
 * The editor input for server configurations and instances. The
 * input points to a resource that is either an instance or
 * configuration.
 *
 * <p>Editors supporting this editor input should use the ResourceManager
 * to load their own copy of the resource. Each editor is responsible
 * for managing this resource and making sure that only one copy is
 * loaded from disk when multiple editors are opened on the same
 * resource.</p>
 *
 * <p>When the editor saves back to the resource, the server tooling
 * will notice the change and reload the new configuration.</p>
 *
 * <p>Editors should call setEditing(resource, true) when the first
 * editor opens on a particular resource, and setEditing(resource,
 * false) when the last editor on a resource closes. This will
 * ensure that the server tooling does not try to edit the resource
 * and cause conflicting changes while an editor is open.</p>
 */
public class ServerEditorInput implements IServerEditorInput, IPersistableElement {
	private String serverId;

	/**
	 * ServerEditorInput constructor comment.
	 */
	public ServerEditorInput(String serverId) {
		super();
		this.serverId = serverId;
	}
	
	/**
	 * Returns the server id.
	 * @return org.eclipse.core.resources.IResource
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * In this case it means that the underlying IFolders are equal.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ServerEditorInput))
			return false;
		ServerEditorInput other = (ServerEditorInput) obj;
		if (serverId == null) {
			if (other.serverId != null)
				return false;	
		} else if (!serverId.equals(other.serverId))
			return false;
		return true;
	}

	/**
	 * Returns whether the editor input exists.  
	 * <p>
	 * This method is primarily used to determine if an editor input should 
	 * appear in the "File Most Recently Used" menu.  An editor input will appear 
	 * in the list until the return value of <code>exists</code> becomes 
	 * <code>false</code> or it drops off the bottom of the list.
	 *
	 * @return <code>true</code> if the editor input exists; <code>false</code>
	 *		otherwise
	 */
	public boolean exists() {
		if (serverId != null && ServerCore.findServer(serverId) == null)
			return false;
		
		return true;
	}

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found.
	 *
	 * @param adapter the adapter class to look up
	 * @return a object castable to the given class, 
	 *    or <code>null</code> if this object does not
	 *    have an adapter for the given class
	 */
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * Returns the ID of an element factory which can be used to recreate 
	 * this object.  An element factory extension with this ID must exist
	 * within the workbench registry.
	 * 
	 * @return the element factory ID
	 */
	public String getFactoryId() {
		return ServerEditorInputFactory.FACTORY_ID;
	}

	public ImageDescriptor getImageDescriptor() {
		return ImageResource.getImageDescriptor(ImageResource.IMG_SERVER);
	}
	
	/**
	 * Returns the name of this editor input for display purposes.
	 * <p>
	 * For instance, if the fully qualified input name is
	 * <code>"a\b\MyFile.gif"</code>, the return value would be just
	 * <code>"MyFile.gif"</code>.
	 *
	 * @return the file name string
	 */
	public String getName() {
		if (serverId != null) {
			IServer server = ServerCore.findServer(serverId);
			if (server != null)
				return server.getName();
			return serverId;
		}
		return "";
	}

	/*
	 * Returns an object that can be used to save the state of this editor input.
	 *
	 * @return the persistable element, or <code>null</code> if this editor input
	 *   cannot be persisted
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	public String getToolTipText() {
		String s = null;
		if (serverId != null) {
			IServer server = ServerCore.findServer(serverId);
			if (server != null) {
				if (server.getFile() != null) {
					s = server.getFile().getFullPath().makeRelative().toString();
					if (s.startsWith("/"))
						s = s.substring(1);
				} else
					s = server.getName();
			}
		}
		if (s == null)
			s = "";
		return s;
	}

	/**
	 * Saves the state of an element within a memento.
	 *
	 * @param memento the storage area for element state
	 */
	public void saveState(IMemento memento) {
		ServerEditorInputFactory.saveState(memento, this);
	}
}