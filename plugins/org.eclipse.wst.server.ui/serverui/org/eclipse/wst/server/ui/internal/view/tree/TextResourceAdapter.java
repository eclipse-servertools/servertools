package org.eclipse.wst.server.ui.internal.view.tree;
/**
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerImageResource;
import org.eclipse.wst.server.ui.internal.ServerTreeContentProvider;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * 
 */
public class TextResourceAdapter implements IAdaptable, IWorkbenchAdapter, IServerElementTag {
	protected Object parent;
	protected byte thisStyle;
	
	public static IElement deleted;
	
	public final static byte STYLE_SERVERS = 0;
	public final static byte STYLE_CONFIGURATIONS = 1;
	//public final static byte STYLE_SERVERS_AND_CONFIGURATIONS = 2;
	//public final static byte STYLE_NO_CONFIGURATION = 3;
	public final static byte STYLE_NO_MODULES = 4;
	public final static byte STYLE_NO_SERVERS = 5;
	public final static byte STYLE_LOOSE_CONFIGURATIONS = 6;

	public TextResourceAdapter(Object parent, byte thisStyle) {
		this.parent = parent;
		this.thisStyle = thisStyle;
	}
	
	public byte getStyle() {
		return thisStyle;
	}
	
	public void setStyle(byte b) {
		thisStyle = b;
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

	/*
	 * @see IWorkbenchAdapter#getChildren(Object)
	 */
	public Object[] getChildren(Object o) {
		if (thisStyle == STYLE_NO_MODULES || thisStyle == STYLE_NO_SERVERS)
			return new Object[0];
		if (thisStyle == STYLE_LOOSE_CONFIGURATIONS) {
			IServerConfiguration[] looseCfg = ServerTreeContentProvider.getLooseConfigurations();
			int size = looseCfg.length;
			Object[] obj = new Object[size];
			for (int i = 0; i < size; i++) {
				obj[i] = new ServerElementAdapter(this, looseCfg[i]);
			}
			
			return obj;
		}
		/*else if (thisStyle == STYLE_SERVERS_AND_CONFIGURATIONS) 
			return new Object[] {
				new TextResourceAdapter(this, STYLE_SERVERS),
				new TextResourceAdapter(this, STYLE_CONFIGURATIONS)
			};*/

		Iterator iterator = null;
		if (thisStyle == STYLE_SERVERS)
			iterator = ServerCore.getResourceManager().getServers().iterator();
		else if (thisStyle == STYLE_CONFIGURATIONS)
			iterator = ServerCore.getResourceManager().getServerConfigurations().iterator();

		List list = new ArrayList();
		while (iterator.hasNext()) {
			IElement resource = (IElement) iterator.next();
			if (resource != deleted)
				list.add(new ServerElementAdapter(this, resource));
		}
		return list.toArray();
	}

	/*
	 * @see IWorkbenchAdapter#getImageDescriptor(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		if (thisStyle == STYLE_SERVERS)
			return ServerImageResource.getImageDescriptor(ServerImageResource.IMG_SERVER_FOLDER);
		else if (thisStyle == STYLE_CONFIGURATIONS || thisStyle == STYLE_LOOSE_CONFIGURATIONS)
			return ServerImageResource.getImageDescriptor(ServerImageResource.IMG_SERVER_CONFIGURATION_FOLDER);
		//else if (thisStyle == STYLE_SERVERS_AND_CONFIGURATIONS)
		//	return ServerImageResource.getImageDescriptor(ServerImageResource.IMG_SERVER_PROJECT);
		//else if (thisStyle == STYLE_NO_CONFIGURATION)
		//	return ServerImageResource.getImageDescriptor(ServerImageResource.IMG_SERVER_CONFIGURATION_NONE);
		else if (thisStyle == STYLE_NO_MODULES) {
			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			return sharedImages.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
		}
		else if (thisStyle == STYLE_NO_SERVERS)
			return ServerImageResource.getImageDescriptor(ServerImageResource.IMG_SERVER_CONFIGURATION_MISSING);
		else
			return null;
	}

	/*
	 * @see IWorkbenchAdapter#getLabel(Object)
	 */
	public String getLabel(Object o) {
		if (thisStyle == STYLE_SERVERS)
			return ServerUIPlugin.getResource("%viewServers");
		else if (thisStyle == STYLE_CONFIGURATIONS)
			return ServerUIPlugin.getResource("%viewConfigurations");
		//else if (thisStyle == STYLE_SERVERS_AND_CONFIGURATIONS)
		//	return "Server Info";
		//else if (thisStyle == STYLE_NO_CONFIGURATION)
		//	return "No configuration";
		else if (thisStyle == STYLE_NO_MODULES)
			return ServerUIPlugin.getResource("%viewNoModules");
		else if (thisStyle == STYLE_NO_SERVERS)
			return ServerUIPlugin.getResource("%viewConfigurationUnused");
		else if (thisStyle == STYLE_LOOSE_CONFIGURATIONS)
			return ServerUIPlugin.getResource("%viewLooseConfigurations");
		else
			return "n/a";
	}

	/*
	 * @see IWorkbenchAdapter#getParent(Object)
	 */
	public Object getParent(Object o) {
		return parent;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof TextResourceAdapter))	
			return false;
		
		TextResourceAdapter adapter = (TextResourceAdapter) obj;
		return (adapter.getStyle() == thisStyle);
	}
	
	public int hashCode() {
		return thisStyle;
	}
}
