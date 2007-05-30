/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import org.eclipse.wst.server.core.internal.IInstallableServer;
import org.eclipse.wst.server.core.internal.ServerPlugin;
/**
 * Installable server content provider.
 */
public class InstallableServerContentProvider extends AbstractTreeContentProvider {
	public static final byte STYLE_VENDOR = 1;
	public static final byte STYLE_VERSION = 2;

	public InstallableServerContentProvider(byte style) {
		super(style, false);
		
		fillTree();
	}

	public void fillTree() {
		clean();
		List list = new ArrayList();
		if (style != STYLE_FLAT) {
			IInstallableServer[] iis = ServerPlugin.getInstallableServers();
			if (iis != null) {
				int size = iis.length;
				for (int i = 0; i < size; i++) {
					IInstallableServer is = iis[i];
					TreeElement ele = null;
					if (style == STYLE_VENDOR) {
						ele = getOrCreate(list, is.getVendor());
						ele.contents.add(is);
						elementToParentMap.put(is, ele);
					} else if (style == STYLE_VERSION) {
						ele = getOrCreate(list, is.getVersion());
						ele.contents.add(is);
						elementToParentMap.put(is, ele);
					}
				}
			}
		} else {
			IInstallableServer[] iis = ServerPlugin.getInstallableServers();
			if (iis != null) {
				int size = iis.length;
				for (int i = 0; i < size; i++)
					list.add(iis[i]);
			}
		}
		elements = list.toArray();
	}
}