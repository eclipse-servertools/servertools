/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
	 * RuntimeTreeContentProvider constructor.
	 * 
	 * @param style a style
	 */
	public RuntimeTreeContentProvider(byte style) {
		super(style);
	}
	
	public void fillTree() {
		clean();
		List list = new ArrayList();
		if (style != STYLE_FLAT) {
			IRuntime[] runtimes = ServerCore.getRuntimes();
			if (runtimes != null) {
				int size = runtimes.length;
				for (int i = 0; i < size; i++) {
					IRuntimeType runtimeType = runtimes[i].getRuntimeType();
					try {
						TreeElement ele = null;
						if (style == STYLE_VENDOR)
							ele = getOrCreate(list, runtimeType.getVendor());
						else if (style == STYLE_VERSION)
							ele = getOrCreate(list, runtimeType.getVersion());
						else if (style == STYLE_TYPE)
							ele = getOrCreate(list, runtimeType.getName());
						ele.contents.add(runtimes[i]);
						elementToParentMap.put(runtimes[i], ele);
					} catch (Exception e) {
						Trace.trace(Trace.WARNING, "Error in runtime content provider", e);
					}
				}
			}
		} else {
			IRuntime[] runtimes = ServerCore.getRuntimes();
			if (runtimes != null) {
				int size = runtimes.length;
				for (int i = 0; i < size; i++)
					list.add(runtimes[i]);
			}
		}
		elements = list.toArray();
	}
}