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

import java.util.*;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.viewers.BaseContentProvider;

public class ServerContentProvider extends BaseContentProvider implements ITreeContentProvider{
	// TODO Angel Says: Need to review if this is needed
	public static Object INITIALIZING = new Object();

	 // @deprecated @see org.eclipse.wst.server.ui.internal.cnf.ServersView2.publishing
	protected static Set<String> publishing = ServersView2.publishing;
		
	public Object[] getElements(Object element) {
		List<IServer> list = new ArrayList<IServer>();
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (!((Server)servers[i]).isPrivate())
					list.add(servers[i]);
			}
		}
		return list.toArray();
	}

	public Object[] getChildren(Object element) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			try {
				IModule[] children = ms.server.getChildModules(ms.module, null);
				int size = children.length;
				ModuleServer[] ms2 = new ModuleServer[size];
				for (int i = 0; i < size; i++) {
					int size2 = ms.module.length;
					IModule[] module = new IModule[size2 + 1];
					System.arraycopy(ms.module, 0, module, 0, size2);
					module[size2] = children[i];
					ms2[i] = new ModuleServer(ms.server, module);
				}
				return ms2;
			} catch (Exception e) {
				return null;
			}
		}
		
		IServer server = (IServer) element;
		IModule[] modules = server.getModules(); 
		int size = modules.length;
		ModuleServer[] ms = new ModuleServer[size];
		for (int i = 0; i < size; i++) {
			ms[i] = new ModuleServer(server, new IModule[] { modules[i] });
		}
		return ms;
	}

	public Object getParent(Object element) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			return ms.server;
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ModuleServer) {
			// Check if the module server has child modules.
			ModuleServer curModuleServer = (ModuleServer)element;
			IServer curServer = curModuleServer.server;
			IModule[] curModule = curModuleServer.module;
			if (curServer != null &&  curModule != null) {
				IModule[] curChildModule = curServer.getChildModules(curModule, null);
				if (curChildModule != null && curChildModule.length > 0)
					return true;
				return false;
			}
			return false;
		}
		if( element instanceof IServer ) {
			return ((IServer) element).getModules().length > 0;
		}
		return false;
	}
}
