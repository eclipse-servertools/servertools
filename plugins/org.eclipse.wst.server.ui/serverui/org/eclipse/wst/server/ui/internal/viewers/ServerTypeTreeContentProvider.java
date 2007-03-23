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

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Server type content provider.
 */
public class ServerTypeTreeContentProvider extends AbstractTreeContentProvider {
	protected boolean localhost;

	protected IModuleType moduleType;
	protected String serverTypeId;
	protected boolean includeIncompatibleVersions;

	/**
	 * ServerTypeTreeContentProvider constructor.
	 * 
	 * @param moduleType a module type
	 * @param serverTypeId a server type id, or null to match any id
	 */
	public ServerTypeTreeContentProvider(IModuleType moduleType, String serverTypeId) {
		super(false);
		localhost = true;
		
		this.moduleType = moduleType;
		this.serverTypeId = serverTypeId;
		
		fillTree();
	}
	
	public void fillTree() {
		clean();

		List list = new ArrayList();
		IServerType[] serverTypes = ServerCore.getServerTypes();
		if (serverTypes != null) {
			int size = serverTypes.length;
			for (int i = 0; i < size; i++) {
				IServerType serverType = serverTypes[i];
				if (include(serverType)) {
					try {
						IRuntimeType runtimeType = serverType.getRuntimeType();
						TreeElement ele = getOrCreate(list, runtimeType.getVendor());
						ele.contents.add(serverType);
						elementToParentMap.put(serverType, ele);
					} catch (Exception e) {
						Trace.trace(Trace.WARNING, "Error in server configuration content provider", e);
					}
				}
			}
		}
		elements = list.toArray();
	}

	protected boolean include(IServerType serverType) {
		if (serverTypeId != null && !serverType.getId().startsWith(serverTypeId))
			return false;
		
		IRuntimeType runtimeType = serverType.getRuntimeType();
		if (runtimeType == null)
			return false;
		
		if (moduleType == null)
			return true;
		
		String moduleTypeId = moduleType.getId();
		if (includeIncompatibleVersions) {
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), moduleTypeId, null))
				return false;
		} else {
			String moduleVersion = moduleType.getVersion();
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), moduleTypeId, moduleVersion))
				return false;
		}
		
		if (localhost || serverType.supportsRemoteHosts())
			return true;
		
		return false;
	}

	protected boolean checkForNonStubEnvironmentRuntime(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		IRuntime[] runtimes = ServerUIPlugin.getRuntimes(runtimeType);
		if (runtimes == null || runtimes.length == 0)
			return false;
		
		int size = runtimes.length;
		for (int i = 0; i < size; i++) {
			if (!runtimes[i].isStub())
				return true;
		}
		return false;
	}

	public void setLocalhost(boolean local) {
		localhost = local;
		fillTree();
	}

	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		fillTree();
	}
}