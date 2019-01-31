/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.cnf.ServerDecorator;
import org.eclipse.wst.server.ui.internal.provisional.UIDecoratorManager;
import org.eclipse.wst.server.ui.internal.viewers.BaseCellLabelProvider;
import org.eclipse.wst.server.ui.internal.viewers.ServerTreeContentProvider;
/**
 * Server table label provider.
 */
public class ServerTableLabelProvider extends BaseCellLabelProvider {
	public static final String[] syncState = new String[] {
		Messages.viewSyncOkay,
		Messages.viewSyncRestart,
		Messages.viewSyncPublish,
		Messages.viewSyncRestartPublish,
		Messages.viewSyncPublishing};

	public static final String[] syncStateUnmanaged = new String[] {
		Messages.viewSyncOkay2,
		Messages.viewSyncRestart2,
		Messages.viewSyncPublish2,
		Messages.viewSyncRestartPublish2,
		Messages.viewSyncPublishing2};

	private static final String[] modulePublishState = new String[] {
		"",
		Messages.viewSyncOkay,
		Messages.viewSyncPublish,
		Messages.viewSyncPublish};

	private int count = 0;

	protected IServer defaultServer;

	/**
	 * ServerTableLabelProvider constructor comment.
	 */
	public ServerTableLabelProvider() {
		// Ensure decorator is initialized.
		super(null);
	}

	public void setDefaultServer(IServer ds) {
		defaultServer = ds;
	}

	public IServer getDefaultServer() {
		return defaultServer;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			if (columnIndex == 0) {
				ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
				return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
			return null;
		}
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			if (columnIndex == 0) {
				//return ServerUICore.getLabelProvider().getImage(ms.module[ms.module.length - 1]);
				ILabelProvider labelProvider = ServerUICore.getLabelProvider();
				Image image = labelProvider.getImage(ms.module[ms.module.length - 1]);
				labelProvider.dispose();
				if (decorator != null) {
					Image dec = decorator.decorateImage(image, ms);
					if (dec != null)
						return dec;
				}
				return image;
			} else if (columnIndex == 1) {
				if (ms.server == null)
					return null;
				
				/*int state = ms.server.getModuleState(ms.module);
				if (state == IServer.STATE_STARTED)
					return ImageResource.getImage(ImageResource.IMG_STATE_STARTED);
				else if (state == IServer.STATE_STOPPED)
					return ImageResource.getImage(ImageResource.IMG_STATE_STOPPED);
				*/
				return getStateImage(ms.server.getServerType(), ms.server.getModuleState(ms.module), null);
			} else if (columnIndex == 2) {
				IStatus status = ((Server) ms.server).getModuleStatus(ms.module);
				if (status != null) {
					ISharedImages sharedImages = ServerUIPlugin.getInstance().getWorkbench().getSharedImages();
					if (status.getSeverity() == IStatus.ERROR)
						return sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
					else if (status.getSeverity() == IStatus.WARNING)
						return sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
					else if (status.getSeverity() == IStatus.INFO)
						return sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
				}
			}
			return null;
		}
		IServer server = (IServer) element;
		if (columnIndex == 0) {
			if (server.getServerType() != null) {
				Image image = ImageResource.getImage(server.getServerType().getId());
				/*IStatus status = ((Server) server).getServerStatus();
				if (defaultServer != null && defaultServer.equals(server) || status != null) {
					Image decorated = decorator.decorateImage(image, element);
					if (decorated != null)
						return decorated;
				}*/
				//return image;
				if (decorator != null) {
					Image dec = decorator.decorateImage(image, server);
					if (dec != null)
						return dec;
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

	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ServerTreeContentProvider.TreeElement) {
			if (columnIndex == 0)
				return ((ServerTreeContentProvider.TreeElement) element).text;
			return "";
		}
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			if (columnIndex == 0) {
				if (ms.module == null)
					return "";
				int size = ms.module.length;
				String name = ms.module[size - 1].getName();
				if (decorator != null) {
					String dec = decorator.decorateText(name, ms);
					if (dec != null)
						return dec;
				}
				return name;
			} else if (columnIndex == 1) {
				if (ms.server == null)
					return "";
				return getStateLabel(ms.server.getServerType(), ms.server.getModuleState(ms.module), null);
			} else if (columnIndex == 2) {
				IStatus status = ((Server) ms.server).getModuleStatus(ms.module);
				if (status != null)
					return status.getMessage();
				
				return modulePublishState[ms.server.getModulePublishState(ms.module)];
			}
		}
		IServer server = (IServer) element;
		if (columnIndex == 0)
			return notNull(server.getName());
		else if (columnIndex == 1) {
			IServerType serverType = server.getServerType();
			if (serverType != null)
				return getServerStateLabel(server);
			
			return "";
		} else if (columnIndex == 2) {
			IStatus status = ((Server) server).getServerStatus();
			if (status != null)
				return status.getMessage();
			
			if (server.getServerType() == null)
				return "";
			
			//if (server.getServerType().hasServerConfiguration() && server.getServerConfiguration() == null)
			//	return Messages.viewNoConfiguration");
			
			if (server.getServerState() == IServer.STATE_UNKNOWN)
				return "";
			
			String serverId = server.getId();
			if (ServerTableViewer.publishing.contains(serverId))
				return syncState[4];
			
			// republish
			int i = 0;
			if (server.shouldPublish()) {
				if (((Server)server).isPublishUnknown())
					return "";
				i += 2;
			}
			
			if (server.shouldRestart())
				i = 1;
			
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
	}
	
	/**
	 * Returns a string representing the server's state.
	 *
	 * @return java.lang.String
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected String getServerStateLabel(IServer server) {
		return ServerDecorator.getServerStateLabel(server);
	}

	/**
	 * Returns a string representing the given state.
	 *
	 * @return java.lang.String
	 * @deprecated
	 */
	protected String getStateLabel(IServerType serverType, int state, String mode) {
		return UIDecoratorManager.getUIDecorator(serverType).getStateLabel(state, mode, count);
	}
	
	protected void animate() {
		count ++;
		if (count > 2)
			count = 0;
	}
}