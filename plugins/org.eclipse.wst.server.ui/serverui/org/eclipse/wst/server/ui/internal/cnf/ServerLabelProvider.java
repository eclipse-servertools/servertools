/*******************************************************************************
 * Copyright (c) 2008,2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.cnf;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.*;
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
		//TODO: Angel says: Look at bug# 258184
	}

	public String getText(Object element) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			if (ms.module == null)
				return "";
			int size = ms.module.length;
			String name = ms.module[size - 1].getName();
			return name;
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
				
				CompositeServerImageDescriptor dsid = new CompositeServerImageDescriptor(serverTypeImg,serverStatusImg);
				
				image = dsid.createImage();
			}
		}
		return image;
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
}
