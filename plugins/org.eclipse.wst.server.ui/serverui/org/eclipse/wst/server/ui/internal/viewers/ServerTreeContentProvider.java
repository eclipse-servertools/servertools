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
import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
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
	protected boolean includeIncompatibleVersions;

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
	
	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		fillTree();
	}
	
	protected void fillTree() {
		clean();
		List list = new ArrayList();
		if (style != STYLE_FLAT) {
			IServer[] servers = ServerCore.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					if (acceptServer(servers[i])) {
						IServerType serverType = servers[i].getServerType();
						IRuntimeType runtimeType = serverType.getRuntimeType();
						if (serverType.getOrder() > initialSelectionOrder) {
							initialSelection = servers[i];
							initialSelectionOrder = serverType.getOrder();
						}
						TreeElement te = null;
						if (style == STYLE_HOST) {
							te = getOrCreate(list, servers[i].getHost());
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
						te.contents.add(servers[i]);
						elementToParentMap.put(servers[i], te);
					}
				}
			}
		} else {
			IServer[] servers = ServerCore.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					if (acceptServer(servers[i])) {
						IServerType serverType = servers[i].getServerType();
						list.add(servers[i]);
						if (serverType.getOrder() > initialSelectionOrder) {
							initialSelection = servers[i];
							initialSelectionOrder = serverType.getOrder();
						}
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
		
		IModuleType mt = module.getModuleType();
		if (includeIncompatibleVersions) {
			if (!ServerUtil.isSupportedModule(server.getServerType().getRuntimeType().getModuleTypes(), mt.getId(), null))
				return false;
		} else {
			if (!ServerUtil.isSupportedModule(server.getServerType().getRuntimeType().getModuleTypes(), mt.getId(), mt.getVersion()))
				return false;
		}
		return true;
	}
}