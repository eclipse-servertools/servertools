package org.eclipse.wst.server.ui.internal.view.tree;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerLabelProvider;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 *
 */
public class ConfigurationProxyResourceAdapter implements IAdaptable, IWorkbenchAdapter, IServerElementTag {
	private static final Object[] NO_CHILDREN = new Object[0];
	
	public static IServerConfiguration deleted;

	protected IServer server;
	protected Object parent;

	public ConfigurationProxyResourceAdapter(Object parent, IServer server) {
		super();
		this.parent = parent;
		this.server = server;
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IAdaptable.class))
			return this;
		else if (adapter.equals(IWorkbenchAdapter.class))
			return this;
		else
			return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	public Object[] getChildren(Object o) {
		return NO_CHILDREN;
	}

	/*
	 * @see IWorkbenchAdapter#getImageDescriptor(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		IServerConfiguration config = server.getServerConfiguration();
		if (config != null && config != deleted)
			return ((ServerLabelProvider)ServerUICore.getLabelProvider()).getImageDescriptor(config);
		else
			return ImageResource.getImageDescriptor(ImageResource.IMG_SERVER_CONFIGURATION_MISSING);
	}

	/*
	 * @see IWorkbenchAdapter#getLabel(Object)
	 */
	public String getLabel(Object o) {
		IServerConfiguration config = server.getServerConfiguration();
		if (config != null && config != deleted)
			return config.getName();
		else
			return ServerUIPlugin.getResource("%viewNoConfiguration");
	}

	/*
	 * @see IWorkbenchAdapter#getParent(Object)
	 */
	public Object getParent(Object o) {
		return parent;
	}

	/**
	 * Return true if the object is the same as this object.
	 * @return boolean
	 * @param obj java.lang.Object
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ConfigurationProxyResourceAdapter) {
			ConfigurationProxyResourceAdapter proxy = (ConfigurationProxyResourceAdapter) obj;

			IServer inst = proxy.getServer();
			if ((server == null && inst != null) ||
				server != null && !server.equals(inst))
				return false;
	
			return true;
		}
		return false;
	}
	
	/**
	 * Return the server.
	 *
	 * @return org.eclipse.wst.server.core.model.IServer
	 */
	public IServer getServer() {
		return server;
	}
	
	public int hashCode() {
		if (server != null) {
			IFile file = server.getFile();
			return file.hashCode();
		} else
			return 0;
	}
}
