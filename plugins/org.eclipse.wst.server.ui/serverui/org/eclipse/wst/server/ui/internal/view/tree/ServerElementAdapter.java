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
package org.eclipse.wst.server.ui.internal.view.tree;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ServerLabelProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
/**
 * 
 */
public class ServerElementAdapter implements IAdaptable, IWorkbenchAdapter, IServerElementTag {
	private static final Object[] NO_CHILDREN = new Object[0];

	protected Object resource;
	protected Object parent;
	protected byte flags;

	public ServerElementAdapter(Object parent, Object resource) {
		this.parent = parent;
		this.resource = resource;
	}
	
	public void setFlags(byte b) {
		flags = b;
	}
	
	public byte getFlags() {
		return flags;
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IAdaptable.class))
			return this;
		else if (adapter.equals(IWorkbenchAdapter.class))
			return this;
		/*else if (adapter.equals(IResource.class)) {
			IResourceManager rm = ServerCore.getResourceManager();
			return rm.getServerResourceLocation(resource);
		}*/
		else
			return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public Object[] getChildren(Object o) {
		return NO_CHILDREN;
	}
	
	/*
	 * @see IWorkbenchAdapter#getChildren(Object)
	 */
	public Object[] getChildren() {
		if (resource instanceof IServer) {
			IServer server = (IServer) resource;
			
			IModule[] modules = server.getModules(null);
			if (modules == null || modules.length == 0) {
				//return new Object[] { new TextResourceAdapter(this, TextResourceAdapter.STYLE_NO_MODULES)};
				return NO_CHILDREN;
			}
			int size = modules.length;
			Object[] obj = new Object[size];
			for (int i = 0; i < size; i++)
				obj[i] = new ModuleResourceAdapter(this, server, modules[i]);

			return obj;
		}/* else if (resource instanceof IServerConfiguration) {
			IServerConfiguration configuration = (IServerConfiguration) resource;
			
			List list = new ArrayList();
			
			// add modules
			IModule[] modules = server.getModules();
			if (modules == null || modules.length == 0) {
				//list.add(new TextResourceAdapter(this, TextResourceAdapter.STYLE_NO_MODULES));
			} else {
				int size = modules.length;
				for (int i = 0; i < size; i++)
					list.add(new ModuleResourceAdapter(this, configuration, modules[i]));
			}

			Object[] obj = new Object[list.size()];
			list.toArray(obj);
			return obj;
		}*/
		return NO_CHILDREN;
	}

	/*
	 * @see IWorkbenchAdapter#getImageDescriptor(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		/*try {
			IServerResourceFactory factory = ServerUtil.getServerResourceFactory(resource);
			String icon = factory.getConfigurationElement().getAttribute("icon");
			URL url = factory.getConfigurationElement().getDeclaringExtension().getDeclaringPluginDescriptor().getInstallURL();
			return ImageDescriptor.createFromURL(new URL(url, icon));
		} catch (Exception e) {
			Trace.trace("Error getting image descriptor", e);
		}*/
		return ((ServerLabelProvider)ServerUICore.getLabelProvider()).getImageDescriptor(resource);
	}

	/*
	 * @see IWorkbenchAdapter#getLabel(Object)
	 */
	public String getLabel(Object o) {
		return ServerUICore.getLabelProvider().getText(resource);
	}

	/*
	 * @see IWorkbenchAdapter#getParent(Object)
	 */
	public Object getParent(Object o) {
		return parent;
	}

	public Object getObject() {
		return resource;
	}

	protected IFile getFile() {
		if (resource instanceof IServer)
			return ((IServer) resource).getFile();
		return null;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ServerElementAdapter))
			return false;
		
		ServerElementAdapter adapter = (ServerElementAdapter) obj;
		IFile file1 = getFile();
		IFile file2 = adapter.getFile();
		return ((file1 == null && file2 == null)) ||
			(file1 != null && file1.equals(file2));
	}
	
	public int hashCode() {
		return getFile().hashCode();
	}
}