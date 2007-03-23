/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * Runtime type content provider.
 */
public class ServerTreeContentProvider extends AbstractTreeContentProvider {
	protected IModule module;
	protected String launchMode;
	protected boolean includeIncompatibleVersions;

	/**
	 * ServerTreeContentProvider constructor.
	 */
	public ServerTreeContentProvider() {
		super();
	}
	
	public ServerTreeContentProvider(IModule module, String launchMode) {
		super(false);
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
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (acceptServer(servers[i])) {
					TreeElement te = getOrCreate(list, servers[i].getHost());
					te.contents.add(servers[i]);
					elementToParentMap.put(servers[i], te);
				}
			}
		}
		elements = list.toArray();
	}

	protected boolean acceptServer(IServer server) {
		if (module == null || launchMode == null)
			return true;
		if (!ServerUIPlugin.isCompatibleWithLaunchMode(server, launchMode))
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