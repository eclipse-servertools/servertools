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

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.Trace;

/**
 * Runtime content provider.
 */
public class RuntimeTreeContentProvider extends AbstractTreeContentProvider {
	public static final byte STYLE_VENDOR = 1;
	public static final byte STYLE_VERSION = 2;
	public static final byte STYLE_TYPE = 3;

	/**
	 * RuntimeTreeContentProvider constructor comment.
	 */
	public RuntimeTreeContentProvider(byte style) {
		super(style);
	}
	
	public void fillTree() {
		List list = new ArrayList();
		if (style != STYLE_FLAT) {
			Iterator iterator = ServerCore.getResourceManager().getRuntimes().iterator();
			while (iterator.hasNext()) {
				IRuntime runtime = (IRuntime) iterator.next();
				IRuntimeType runtimeType = runtime.getRuntimeType();
				try {
					TreeElement ele = null;
					if (style == STYLE_VENDOR)
						ele = getOrCreate(list, runtimeType.getVendor());
					else if (style == STYLE_VERSION)
						ele = getOrCreate(list, runtimeType.getVersion());
					else if (style == STYLE_TYPE)
						ele = getOrCreate(list, runtimeType.getName());
					ele.contents.add(runtime);
					elementToParentMap.put(runtime, ele);
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Error in runtime content provider", e);
				}
			}
		} else {
			Iterator iterator = ServerCore.getResourceManager().getRuntimes().iterator();
			while (iterator.hasNext()) {
				IRuntime runtime = (IRuntime) iterator.next();
				list.add(runtime);
			}
		}
		elements = list.toArray();
	}
}
