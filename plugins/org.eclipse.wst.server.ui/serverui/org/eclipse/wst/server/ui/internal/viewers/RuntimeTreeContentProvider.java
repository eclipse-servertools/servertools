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

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Runtime content provider.
 */
public class RuntimeTreeContentProvider extends AbstractTreeContentProvider {
	/**
	 * RuntimeTreeContentProvider constructor.
	 */
	public RuntimeTreeContentProvider() {
		super();
	}

	public void fillTree() {
		clean();
		List list = new ArrayList();
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				IRuntimeType runtimeType = runtimes[i].getRuntimeType();
				try {
					TreeElement ele = getOrCreate(list, runtimeType.getVendor());
					ele.contents.add(runtimes[i]);
					elementToParentMap.put(runtimes[i], ele);
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Error in runtime content provider", e);
				}
			}
		}
		elements = list.toArray();
	}
}