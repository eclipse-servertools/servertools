/**
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 */
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.ui.internal.DefaultServerLabelDecorator;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * Server table label provider.
 */
public class ServerTableLabelProvider implements ITableLabelProvider {
	private static final Image[] serverStateImage = new Image[] {
		null,
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_1),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_DEBUG),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTED_PROFILE),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_1),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPED),
		null};
	
	private static final Image[] startingImages = new Image[] {
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_1),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_2),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STARTING_3)
	};
	
	private static final Image[] stoppingImages = new Image[] {
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_1),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2),
		ImageResource.getImage(ImageResource.IMG_SERVER_STATE_STOPPING_2)
	};
	
	private static final String[] serverState = new String[] {
		"",
		ServerUIPlugin.getResource("%viewStatusStarting"),
		ServerUIPlugin.getResource("%viewStatusStarted"),
		ServerUIPlugin.getResource("%viewStatusStartedDebug"),
		ServerUIPlugin.getResource("%viewStatusStartedProfile"),
		ServerUIPlugin.getResource("%viewStatusStopping"),
		ServerUIPlugin.getResource("%viewStatusStopped"),
		ServerUIPlugin.getResource("%viewStatusUnsupported")};
		
	private static final String[] serverStateUnmanaged = new String[] {
		"",
		ServerUIPlugin.getResource("%viewStatusStarting4"),
		ServerUIPlugin.getResource("%viewStatusStarted2"),
		ServerUIPlugin.getResource("%viewStatusStartedDebug2"),
		ServerUIPlugin.getResource("%viewStatusStartedProfile2"),
		ServerUIPlugin.getResource("%viewStatusStopping4"),
		ServerUIPlugin.getResource("%viewStatusStopped2"),
		ServerUIPlugin.getResource("%viewStatusUnsupported2")};

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
		
	private static final String[] startingText = new String[] {
		ServerUIPlugin.getResource("%viewStatusStarting1"),
		ServerUIPlugin.getResource("%viewStatusStarting2"),
		ServerUIPlugin.getResource("%viewStatusStarting3")};
	
	private static final String[] stoppingText = new String[] {
		ServerUIPlugin.getResource("%viewStatusStopping1"),
		ServerUIPlugin.getResource("%viewStatusStopping2"),
		ServerUIPlugin.getResource("%viewStatusStopping3")};

	private int count = 0;
	
	protected DefaultServerLabelDecorator decorator = new DefaultServerLabelDecorator();
	
	protected IServer defaultServer;

	/**
	 * ServerTableLabelProvider constructor comment.
	 */
	public ServerTableLabelProvider() {
		super();
	}

	public void addListener(ILabelProviderListener listener) { }

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
			} else
				return null;
		} else if (columnIndex == 2) {
			IServerType serverType = server.getServerType();
			if (serverType == null)
				return null;
			if (serverType.getServerStateSet() == IServerType.SERVER_STATE_SET_PUBLISHED)
				return null;
			else return getServerStateImage(server);
		} else
			return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		IServer server = (IServer) element;
		if (columnIndex == 0)
			return notNull(server.getName());
		else if (columnIndex == 1) {
			return notNull(server.getHostname());
		} else if (columnIndex == 2) {
			IServerType serverType = server.getServerType();
			if (serverType != null)
				return getServerStateLabel(server, serverType.getServerStateSet());
			else
				return "";
		} else if (columnIndex == 3) {
			if (server.getServerType() == null)
				return "";
			if (server.getServerType().hasServerConfiguration() && server.getServerConfiguration() == null)
				return ServerUIPlugin.getResource("%viewNoConfiguration");
			
			String serverId = server.getId();
			if (ServerTableViewer.publishing.contains(serverId))
				return syncState[4];
			
			boolean republish = false;
			if (server.getConfigurationSyncState() != IServer.SYNC_STATE_IN_SYNC)
				republish = true;
			else {
				if (!server.getUnpublishedModules().isEmpty())
					republish = true;
			}
			
			int i = 0;
			if (server.isRestartNeeded())
				i = 1;
			if (republish)
				i += 2;
			
			IServerType serverType = server.getServerType();
			if (serverType.getServerStateSet() == IServerType.SERVER_STATE_SET_MANAGED)
				return syncState[i];
			else
				return syncStateUnmanaged[i];
		} else
			return "X";
	}
	
	protected String notNull(String s) {
		if (s != null)
			return s;
		else
			return "";
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) { }

	/**
	 * Returns an image representing the server's state.
	 * @return org.eclipse.jface.parts.IImage
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected Image getServerStateImage(IServer server) {
		byte state = server.getServerState();
		if (state == IServer.SERVER_STARTING)
			return startingImages[count];
		else if (state == IServer.SERVER_STOPPING)
			return stoppingImages[count];
		else
			return serverStateImage[server.getServerState()];
	}
	
	/**
	 * Returns a string representing the server's state.
	 *
	 * @return java.lang.String
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	protected String getServerStateLabel(IServer server, byte stateSet) {
		if (stateSet == IServerType.SERVER_STATE_SET_PUBLISHED) {
			return "";
		} else {
			if (stateSet == IServerType.SERVER_STATE_SET_MANAGED) {
				byte state = server.getServerState();
				if (state == IServer.SERVER_STARTING)
					return startingText[count];
				else if (state == IServer.SERVER_STOPPING)
					return stoppingText[count];
				else
					return serverState[server.getServerState()];
			} else
				return serverStateUnmanaged[server.getServerState()];
		}
	}
	
	protected void animate() {
		count ++;
		if (count > 2)
			count = 0;
	}
}