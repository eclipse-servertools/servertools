/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.IServerModule;
/**
 * A utility class for referencing a server and a module at the same time.
 */
public class ModuleServer implements IServerModule {
	/**
	 * The server
	 */
	public IServer server;

	/**
	 * The module
	 */
	public IModule[] module;

	/**
	 * Create a new module-server.
	 * 
	 * @param server the server
	 * @param module the module
	 */
	public ModuleServer(IServer server, IModule[] module) {
		this.server = server;
		this.module = module;
		if (module == null)
			throw new IllegalArgumentException();
	}

	/**
	 * Return the server that the module belongs to.
	 * 
	 * @return the server
	 */
	public IServer getServer() {
		return server;
	}

	/**
	 * Returns the module.
	 * 
	 * @return the module
	 */
	public IModule[] getModule() {
		return module;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ModuleServer))
			return false;
		
		ModuleServer ms = (ModuleServer) obj;
		
		if (ms.server == null && server != null)
			return false;
		
		if (ms.server != null && !ms.server.equals(server))
			return false;
		
		if (ms.module.length != module.length)
			return false;
		
		int size = module.length;
		for (int i = 0; i < size; i++) {
			if (!module[i].equals(ms.module[i]))
				return false;
		}
		return true;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (server != null)
			sb.append("Server-Module [" + server.getId() + "/" + server.getName() + ", (");
		else
			sb.append("Server-Module [null, (");
		
		int size = module.length;
		for (int i = 0; i < size; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(module[i].getName());
		}
		sb.append(")]");
		return sb.toString();
	}
}