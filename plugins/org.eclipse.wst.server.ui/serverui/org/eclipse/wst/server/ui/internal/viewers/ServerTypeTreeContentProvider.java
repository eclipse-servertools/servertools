/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Server type content provider.
 */
public class ServerTypeTreeContentProvider extends AbstractTreeContentProvider {
	public static final byte STYLE_VENDOR = 1;
	public static final byte STYLE_VERSION = 2;
	public static final byte STYLE_MODULE_TYPE = 3;
	public static final byte STYLE_TYPE = 4; // not used yet
	
	protected boolean localhost;
	protected boolean includeTestEnvironments = true;
	
	protected String type;
	protected String version;
	protected boolean includeIncompatibleVersions;

	/**
	 * ServerTypeTreeContentProvider constructor comment.
	 */
	public ServerTypeTreeContentProvider(byte style, String type, String version) {
		super(style, false);
		localhost = true;
		
		this.type = type;
		this.version = version;
		
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
					if (serverType.getOrder() > initialSelectionOrder) {
						initialSelection = serverType;
						initialSelectionOrder = serverType.getOrder();
					}
					if (style == STYLE_FLAT) {
						list.add(serverType);
					} else if (style != STYLE_MODULE_TYPE) {
						try {
							IRuntimeType runtimeType = serverType.getRuntimeType();
							TreeElement ele = null;
							if (style == STYLE_VENDOR)
								ele = getOrCreate(list, runtimeType.getVendor());
							else if (style == STYLE_VERSION)
								ele = getOrCreate(list, runtimeType.getVersion());
							else if (style == STYLE_TYPE)
								ele = getOrCreate(list, runtimeType.getName());
							ele.contents.add(serverType);
							elementToParentMap.put(serverType, ele);
						} catch (Exception e) {
							Trace.trace(Trace.WARNING, "Error in server configuration content provider", e);
						}
					} else { // style = MODULE_TYPE
						IRuntimeType runtimeType = serverType.getRuntimeType();
						IModuleType2[] moduleTypes = runtimeType.getModuleTypes();
						if (moduleTypes != null) {
							int size2 = moduleTypes.length;
							for (int j = 0; j < size2; j++) {
								IModuleType2 mb = moduleTypes[j];
								IModuleType mt = ServerCore.getModuleType(mb.getType());
								if (mt != null) {
									TreeElement ele = getOrCreate(list, mt.getName());
									TreeElement ele2 = getOrCreate(ele.contents, mt.getName() + "/" + mb.getVersion(), mb.getVersion());
									ele2.contents.add(serverType);
									elementToParentMap.put(serverType, ele2);
									elementToParentMap.put(ele2, ele);
								}
							}
						}
					}
				}
			}
		}
		elements = list.toArray();
	}

	protected boolean include(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		if (runtimeType == null)
			return false;
		if (includeIncompatibleVersions) {
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), type, null))
				return false;
		} else {
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), type, version))
				return false;
		}
		
		if (!includeTestEnvironments && serverType.isTestEnvironment()) {
			if (!checkForTestEnvironmentRuntime(serverType))
				return false;
		}
		
		if (serverType.supportsRemoteHosts())
			return true;
		
		return false;
	}

	protected boolean checkForTestEnvironmentRuntime(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		IRuntime[] runtimes = ServerCore.getResourceManager().getRuntimes(runtimeType);
		if (runtimes == null || runtimes.length == 0)
			return false;
		
		int size = runtimes.length;
		for (int i = 0; i < size; i++) {
			if (runtimes[i].isTestEnvironment())
				return true;
		}
		return false;
	}

	protected boolean checkForNonStubEnvironmentRuntime(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		IRuntime[] runtimes = ServerCore.getResourceManager().getRuntimes(runtimeType);
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
	
	public void setIncludeTestEnvironments(boolean te) {
		includeTestEnvironments = te;
		fillTree();
	}
	
	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		fillTree();
	}
}