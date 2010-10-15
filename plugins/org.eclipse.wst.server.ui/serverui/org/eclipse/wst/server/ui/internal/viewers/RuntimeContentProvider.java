/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
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
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Runtime;
/**
 * Runtime content provider.
 */
public class RuntimeContentProvider extends BaseContentProvider {
	/**
	 * RuntimeContentProvider constructor comment.
	 */
	public RuntimeContentProvider() {
		super();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		List<IRuntime> list = new ArrayList<IRuntime>();
		IRuntime[] runtimes = ServerCore.getRuntimes();
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (!((Runtime)runtimes[i]).isPrivate())
					list.add(runtimes[i]);
			}
		}
		return list.toArray();
	}
}