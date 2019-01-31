/*******************************************************************************
 * Copyright (c) 2008,2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.cnf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.viewers.BaseCellLabelProvider;
/**
 * Server table label provider.
 */
public class ServerLabelProvider extends BaseCellLabelProvider{
	
	/**
	 * ServerTableLabelProvider constructor comment.
	 */
	public ServerLabelProvider() {
		super();
		this.providerImageCache = new HashMap<String, Image>();
		//TODO: Angel says: Look at bug# 258184
	}

	public String getText(Object element) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			if (ms.module == null)
				return "";
			return ms.getModuleDisplayName();
		}
		
		if( element instanceof IServer ) {
			IServer server = (IServer) element;
			return notNull(server.getName());
		} 
		
		if( element == ServerContentProvider.INITIALIZING)
			return Messages.viewInitializing;

		if (element instanceof IWorkspaceRoot){
			return Platform.getResourceString(ServerUIPlugin.getInstance().getBundle(), "%viewServers");
		}
		
		return "";
	}

	@Override
	public void dispose() {

		super.dispose();
		if (this.providerImageCache != null) {
			final Iterator<Image> providerImageCacheIterator = this.providerImageCache.values().iterator();
			while (providerImageCacheIterator.hasNext()) {
				providerImageCacheIterator.next().dispose();
			}
			this.providerImageCache.clear();
		}
	}

	public Image getImage(Object element) {
		
		Image image = null;
		
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			ILabelProvider labelProvider = ServerUICore.getLabelProvider();
			image = labelProvider.getImage(ms.module[ms.module.length - 1]);
			labelProvider.dispose();
		} else if( element instanceof IServer ) {
			IServer server = (IServer) element;
			if (server.getServerType() != null) {				
				// Ideally we won't be doing the overlay of the state here, but rather in a decorator so that 
				// users can turn it off and on. This works for now until we have more time to work and reorganize
				// the code
				Image serverTypeImg = ImageResource.getImage(server.getServerType().getId());
				Image serverStatusImg = ServerDecorator.getServerStateImageOverlay(server);

				// Images returned via the CompositeServerImageDescriptor#createImage() need to be cached since this API
				// will always create a new Image and it is impossible to know when to dispose them except when the
				// label provider is disposed.
				final String key = this.getCacheKey(server, serverTypeImg, serverStatusImg);
				image = this.providerImageCache.get(key);
				if(image == null) {
					CompositeServerImageDescriptor dsid = new CompositeServerImageDescriptor(serverTypeImg,serverStatusImg);
					image = dsid.createImage();
					this.providerImageCache.put(key, image);
				}
			}
		}
		return image;
	}

	private final String getCacheKey(final IServer server, final Image serverTypeImg, final Image serverStatusImg) {

		StringBuffer key = new StringBuffer();
		if(server != null) {
			key.append(server.getName());
		}
		if(serverTypeImg != null) {
			key.append(serverTypeImg.toString());
		}
		if(serverStatusImg != null) {
			key.append(serverStatusImg.toString());
		}
		return key.toString();
	}

	protected String notNull(String s) {
		if (s == null)
			return "";
		return s;
	}

	public boolean isLabelProperty(Object element, String property) {
		if (element instanceof IServer){
			if (property.equalsIgnoreCase("ICON")){
				return true;
			}
		}
		return true;
	}

	@Override
	public Image getColumnImage(Object element, int index) {
		// TODO Left blank since the CNF doesn't support this 
		return null;
	}

	@Override
	public String getColumnText(Object element, int index) {
		// TODO Left blank since the CNF doesn't support this
		return null;
	}	

	private final Map<String, Image> providerImageCache;
}