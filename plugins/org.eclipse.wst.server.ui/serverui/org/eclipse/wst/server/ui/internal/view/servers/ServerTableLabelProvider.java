/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.DefaultServerLabelDecorator;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.provisional.UIDecoratorManager;
import org.eclipse.swt.graphics.Image;
/**
 * Server table label provider.
 */
public class ServerTableLabelProvider implements ITableLabelProvider {
	public static final String[] syncState = new String[] {
		ServerUIPlugin.getResource("%viewSyncOkay"),
		ServerUIPlugin.getResource("%viewSyncRestart"),
		ServerUIPlugin.getResource("%viewSyncPublish"),
		ServerUIPlugin.getResource("%viewSyncRestartPublish"),
		ServerUIPlugin.getResource("%viewSyncPublishing")};
	
	public static final String[] syncStateUnmanaged = new String[] {
		ServerUIPlugin.getResource("%viewSyncOkay2"),
		ServerUIPlugin.getResource("%viewSyncRestart2"),
		ServerUIPlugin.getResource("%viewSyncPublish2"),
		ServerUIPlugin.getResource("%viewSyncRestartPublish2"),
		ServerUIPlugin.getResource("%viewSyncPublishing2")};

	private int count = 0;
	
	protected DefaultServerLabelDecorator decorator = new DefaultServerLabelDecorator();
	
	protected IServer defaultServer;

	/**
	 * ServerTableLabelProvider constructor comment.
	 */
	public ServerTableLabelProvider() {
		super();
	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	public void dispose() {
		decorator.dispose();
	}

	public void setDefaultServer(IServer ds) {
		defaultServer = ds;
	}
	
	public IServer getDefaultServer() {
		return defaultServer;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			if (columnIndex == 0)
				ServerUICore.getLabelProvider().getImage(ms.module);
			if (columnIndex == 1)
				return getStateImage(ms.server.getServerType(), ms.server.getModuleState(ms.module), null);
			return null;
		}
		IServer server = (IServer) element;
		if (columnIndex == 0) {
			if (server.getServerType() != null) {
				Image image = ImageResource.getImage(server.getServerType().getId());
				if (defaultServer != null && defaultServer.equals(server)) {
					Image decorated = decorator.decorateImage(image, element);
					if (decorated != null)
						return decorated;
				}
				return image;
			}
			return null;
		} else if (columnIndex == 1) {
			IServerType serverType = server.getServerType();
			if (serverType == null)
				return null;
			//if (serverType.getServerStateSet() == IServerType.SERVER_STATE_SET_PUBLISHED)
			//	return null;
			return getServerStateImage(server);
		} else
			return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			if (columnIndex == 0) {
				int size = ms.module.length;
				return ms.module[size - 1].getName();
			}
			else if (columnIndex == 1)
				return getStateLabel(ms.server.getServerType(), ms.server.getModuleState(ms.module), null);
			else if (columnIndex == 2)
				return "-";
		}
		IServer server = (IServer) element;
		if (columnIndex == 0)
			return notNull(server.getName());
		//else if (columnIndex == 1)
		//	return notNull(server.getHost());
		else if (columnIndex == 1) {
			IServerType serverType = server.getServerType();
			if (serverType != null)
				return getServerStateLabel(server);
			
			return "";
		} else if (columnIndex == 2) {
			if (server.getServerType() == null)
				return "";
			
			//if (server.getServerType().hasServerConfiguration() && server.getServerConfiguration() == null)
			//	return ServerUIPlugin.getResource("%viewNoConfiguration");
			
			if (server.getServerState() == IServer.STATE_UNKNOWN)
				return "";
			
			String serverId = server.getId();
			if (ServerTableViewer.publishing.contains(serverId))
				return syncState[4];
			
			int i = 0;
			if (server.getServerRestartState())
				i = 1;
			
			// republish
			if (server.getServerPublishState() != IServer.PUBLISH_STATE_NONE)
				i += 2;
			
			//IServerType serverType = server.getServerType();
			// TODO: state set
			//if (serverType.getServerStateSet() == IServerType.SERVER_STATE_SET_MANAGED)
				return syncState[i];
			//return syncStateUnmanaged[i];
		} else
			return "-";
	}
	
	protected String notNull(String s) {
		if (s == null)
			return "";
		return s;
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}

	/**
	 * Returns an image representing the server's state.
	 * 
	 * @return org.eclipse.jface.parts.IImage
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected Image getServerStateImage(IServer server) {
		return getStateImage(server.getServerType(), server.getServerState(), server.getMode());
	}

	/**
	 * Returns an image representing the given state.
	 * 
	 * @return org.eclipse.jface.parts.IImage
	 */
	protected Image getStateImage(IServerType serverType, int state, String mode) {
		return UIDecoratorManager.getUIDecorator(serverType).getStateImage(state, mode, count);
		/*if (state == IServer.STATE_UNKNOWN)
			return null;
		else if (state == IServer.STATE_STARTING)
			return startingImages[count];
		else if (state == IServer.STATE_STOPPING)
			return stoppingImages[count];
		else if (state == IServer.STATE_STOPPED)
			return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPED);
		else { //if (state == IServer.STATE_STARTED) {
			//String mode = server.getMode();
			if (ILaunchManager.DEBUG_MODE.equals(mode))
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_DEBUG);
			else if (ILaunchManager.PROFILE_MODE.equals(mode))
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_PROFILE);
			else
				return ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED);
		}*/
	}
	
	/**
	 * Returns a string representing the server's state.
	 *
	 * @return java.lang.String
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected String getServerStateLabel(IServer server) {
		return getStateLabel(server.getServerType(), server.getServerState(), server.getMode());
	}

	/**
	 * Returns a string representing the given state.
	 *
	 * @return java.lang.String
	 */
	protected String getStateLabel(IServerType serverType, int state, String mode) {
		return UIDecoratorManager.getUIDecorator(serverType).getStateLabel(state, mode, count);
		/*if (stateSet == IServerType.SERVER_STATE_SET_PUBLISHED)
			return "";
		
		if (stateSet == IServerType.SERVER_STATE_SET_MANAGED) {
			if (state == IServer.STATE_UNKNOWN)
				return "";
			else if (state == IServer.STATE_STARTING)
				return startingText[count];
			else if (state == IServer.STATE_STOPPING)
				return stoppingText[count];
			else if (state == IServer.STATE_STARTED) {
				if (ILaunchManager.DEBUG_MODE.equals(mode))
					return ServerUIPlugin.getResource("%viewStatusStartedDebug");
				else if (ILaunchManager.PROFILE_MODE.equals(mode))
					return ServerUIPlugin.getResource("%viewStatusStartedProfile");
				else
					return ServerUIPlugin.getResource("%viewStatusStarted");
			} else if (state == IServer.STATE_STOPPED)
				return ServerUIPlugin.getResource("%viewStatusStopped");
		}
		
		return serverStateUnmanaged[state];*/
	}
	
	protected void animate() {
		count ++;
		if (count > 2)
			count = 0;
	}
}