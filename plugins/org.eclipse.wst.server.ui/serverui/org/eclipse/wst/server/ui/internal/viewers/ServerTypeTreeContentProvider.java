/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.Iterator;
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
		Iterator iterator = ServerCore.getServerTypes().iterator();
		while (iterator.hasNext()) {
			IServerType serverType = (IServerType) iterator.next();
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
					Iterator iterator2 = runtimeType.getModuleTypes().iterator();
					while (iterator2.hasNext()) {
						IModuleType mb = (IModuleType) iterator2.next();
						IModuleKind mt = ServerCore.getModuleKind(mb.getType());
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
		if (localhost && serverType.supportsLocalhost())
			return true;
		
		return false;
	}

	protected boolean checkForTestEnvironmentRuntime(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		List list = ServerCore.getResourceManager().getRuntimes(runtimeType);
		if (list.isEmpty())
			return false;
		
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IRuntime runtime = (IRuntime) iterator.next();
			if (runtime.isTestEnvironment())
				return true;
		}
		return false;
	}

	protected boolean checkForNonStubEnvironmentRuntime(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		List list = ServerCore.getResourceManager().getRuntimes(runtimeType);
		if (list.isEmpty())
			return false;
		
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IRuntime runtime = (IRuntime) iterator.next();
			if (!runtime.getAttribute("stub", false))
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
