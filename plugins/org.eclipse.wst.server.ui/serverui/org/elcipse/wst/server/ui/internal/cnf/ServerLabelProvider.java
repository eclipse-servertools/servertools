/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.elcipse.wst.server.ui.internal.cnf;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
/**
 * Server table label provider.
 */
public class ServerLabelProvider extends LabelProvider {

	/**
	 * ServerTableLabelProvider constructor comment.
	 */
	public ServerLabelProvider() {
		super();
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

		return "-";
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
				image = ImageResource.getImage(server.getServerType().getId());
				// TODO Angel says: Need to discuss about it
				// Because we are now grabbing the ServerState the type will not show. It might be best to create a new icon for the state
				ImageDescriptor imgDescriptor = ServerDecorator.getServerStateImage(server);
				if (image != null){
					image = imgDescriptor.createImage();
				}
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
}
