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
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Runtime type content provider.
 */
public class ServerTreeContentProvider extends AbstractTreeContentProvider {
	public static final byte STYLE_HOST = 1;
	public static final byte STYLE_VENDOR = 2;
	public static final byte STYLE_VERSION = 3;
	
	protected IModule module;
	protected String launchMode;

	/**
	 * ServerTreeContentProvider constructor comment.
	 */
	public ServerTreeContentProvider(byte style) {
		super(style);
	}
	
	public ServerTreeContentProvider(byte style, IModule module, String launchMode) {
		super(style, false);
		this.module = module;
		this.launchMode = launchMode;
		
		fillTree();
	}
	
	protected void fillTree() {
		List list = new ArrayList();
		if (style != STYLE_FLAT) {
			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				if (acceptServer(server)) {
					IServerType serverType = server.getServerType();
					IRuntimeType runtimeType = serverType.getRuntimeType();
					if (serverType.getOrder() > initialSelectionOrder) {
						initialSelection = server;
						initialSelectionOrder = serverType.getOrder();
					}
					TreeElement te = null;
					if (style == STYLE_HOST) {
						te = getOrCreate(list, server.getHostname());
					} else if (style == STYLE_VERSION) {
						String version = ServerUIPlugin.getResource("%elementUnknownName");
						if (runtimeType != null)
							version = runtimeType.getVersion();
						te = getOrCreate(list, version);
					} else if (style == STYLE_VENDOR) {
						String vendor = ServerUIPlugin.getResource("%elementUnknownName");
						if (runtimeType != null)
							vendor = runtimeType.getVendor();
						te = getOrCreate(list, vendor);
					}
					te.contents.add(server);
					elementToParentMap.put(server, te);
				}
			}
		} else {
			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				if (acceptServer(server)) {
					IServerType serverType = server.getServerType();
					list.add(server);
					if (serverType.getOrder() > initialSelectionOrder) {
						initialSelection = server;
						initialSelectionOrder = serverType.getOrder();
					}
				}
			}
		}
		elements = list.toArray();
	}

	protected boolean acceptServer(IServer server) {
		if (module == null || launchMode == null)
			return true;
		if (!ServerUtil.isCompatibleWithLaunchMode(server, launchMode))
			return false;
		if (!ServerUtil.isSupportedModule(server.getServerType().getRuntimeType().getModuleTypes(), module.getType(), module.getVersion()))
			return false;
		return true;
	}
}