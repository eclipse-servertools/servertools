/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
/**
 * Adapter factory to adapt Requests to IActionFilter.
 */
public class RequestAdapterFactory implements IAdapterFactory {
	private RequestActionFilter reqActionFilter = null;

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IActionFilter.class) {
			if (reqActionFilter == null) {
				reqActionFilter = new RequestActionFilter();
			}
			return reqActionFilter;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { IActionFilter.class };
	}
}