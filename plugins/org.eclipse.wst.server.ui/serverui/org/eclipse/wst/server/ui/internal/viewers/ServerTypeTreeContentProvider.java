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
import java.util.HashMap;
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

	/**
	 * ServerTypeTreeContentProvider constructor comment.
	 */
	public ServerTypeTreeContentProvider(byte style) {
		super(style, false);
		localhost = true;
		
		fillTree();
	}
	
	public void fillTree() {
		elementToParentMap = new HashMap();
		textMap = new HashMap();
		initialSelection = null;
		initialSelectionOrder = -1000;

		List list = new ArrayList();
		Iterator iterator = ServerCore.getServerTypes().iterator();
		while (iterator.hasNext()) {
			IServerType type = (IServerType) iterator.next();
			if (include(type)) {
				if (type.getOrder() > initialSelectionOrder) {
					initialSelection = type;
					initialSelectionOrder = type.getOrder();
				}
				if (style == STYLE_FLAT) {
					list.add(type);
				} else if (style != STYLE_MODULE_TYPE) {
					try {
						IRuntimeType runtimeType = type.getRuntimeType();
						TreeElement ele = null;
						if (style == STYLE_VENDOR)
							ele = getOrCreate(list, runtimeType.getVendor());
						else if (style == STYLE_VERSION)
							ele = getOrCreate(list, runtimeType.getVersion());
						else if (style == STYLE_TYPE)
							ele = getOrCreate(list, runtimeType.getName());
						ele.contents.add(type);
						elementToParentMap.put(type, ele);
					} catch (Exception e) {
						Trace.trace(Trace.WARNING, "Error in server configuration content provider", e);
					}
				} else { // style = MODULE_TYPE
					IRuntimeType runtimeType = type.getRuntimeType();
					Iterator iterator2 = runtimeType.getModuleTypes().iterator();
					while (iterator2.hasNext()) {
						IModuleType mb = (IModuleType) iterator2.next();
						IModuleKind mt = ServerCore.getModuleKind(mb.getType());
						if (mt != null) {
							TreeElement ele = getOrCreate(list, mt.getName());
							TreeElement ele2 = getOrCreate(ele.contents, mt.getName() + "/" + mb.getVersion(), mb.getVersion());
							ele2.contents.add(type);
							elementToParentMap.put(type, ele2);
							elementToParentMap.put(ele2, ele);
						}
					}
				}
			}
		}
		elements = list.toArray();
	}

	protected boolean include(IServerType type) {
		if (!includeTestEnvironments && type.isTestEnvironment()) {
			if (!checkForTestEnvironmentRuntime(type))
				return false;
		}
		if (type.supportsRemoteHosts())
			return true;
		if (localhost && type.supportsLocalhost())
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
	
	public boolean getHost() {
		return localhost;
	}

	public void setHost(boolean local) {
		localhost = local;
	}
	
	public void setIncludeTestEnvironments(boolean te) {
		includeTestEnvironments = te;
	}
}