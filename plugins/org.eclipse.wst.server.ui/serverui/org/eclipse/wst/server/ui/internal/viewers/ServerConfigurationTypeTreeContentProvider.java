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

import org.eclipse.wst.server.core.IServerConfigurationType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.Trace;

/**
 * Server configuration type content provider.
 */
public class ServerConfigurationTypeTreeContentProvider extends AbstractTreeContentProvider {
	public static final byte STYLE_VENDOR = 1;
	public static final byte STYLE_VERSION = 2;
	public static final byte STYLE_HOST = 3;
	public static final byte STYLE_TYPE = 4;

	/**
	 * ServerConfigurationTypeTreeContentProvider constructor comment.
	 */
	public ServerConfigurationTypeTreeContentProvider(byte style) {
		super(style);
	}
	
	public void fillTree() {
		List list = new ArrayList();
		Iterator iterator = ServerCore.getServerConfigurationTypes().iterator();
		while (iterator.hasNext()) {
			IServerConfigurationType type = (IServerConfigurationType) iterator.next();
			if (style == STYLE_FLAT) {
				list.add(type);
			} else {
				try {
					/*TreeElement ele = null;
					if (style == STYLE_VENDOR)
						ele = getOrCreate(list, runtimeType.getVendor());
					else if (style == STYLE_VERSION)
						ele = getOrCreate(list, runtimeType.getVersion());
					else if (style == STYLE_HOST)
						ele = getOrCreate(list, runtime.getHost());
					else if (style == STYLE_TYPE)
						ele = getOrCreate(list, runtimeType.getLabel());
					ele.contents.add(type);
					elementToParentMap.put(type, ele);*/
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Error in server configuration content provider", e);
				}
			}
		}
		elements = list.toArray();
	}
}
