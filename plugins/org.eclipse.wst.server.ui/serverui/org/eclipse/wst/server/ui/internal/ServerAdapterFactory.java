/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
import org.eclipse.wst.server.core.IServer;
/**
 * Adapter factory to adapt servers to IActionFilter.
 */
public class ServerAdapterFactory implements IAdapterFactory {
	IActionFilter actionFilter = new IActionFilter() {
		public boolean testAttribute(Object target, String name, String value) {
			IServer server = (IServer) target;
			String[] typeIds = tokenize(value, ",");
			return supportsServerType(server.getServerType().getId(), typeIds);
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType != IActionFilter.class)
			return null;
		
		return actionFilter; 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { IActionFilter.class };
	}

	protected static String[] tokenize(String param, String delim) {
		if (param == null)
			return new String[0];
		
		List list = new ArrayList();
		
		StringTokenizer st = new StringTokenizer(param, delim);
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (str != null && str.length() > 0)
				list.add(str.trim());
		}

		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}

	/**
	 * Returns true if the given server type (given by the id) can use this action.
	 *
	 * @return boolean
	 */
	protected boolean supportsServerType(String id, String[] typeIds) {
		if (id == null || id.length() == 0)
			return false;

		if (typeIds == null)
			return false;
		
		int size = typeIds.length;
		for (int i = 0; i < size; i++) {
			if (typeIds[i].endsWith("*")) {
				if (id.length() >= typeIds[i].length() && id.startsWith(typeIds[i].substring(0, typeIds[i].length() - 1)))
					return true;
			} else if (id.equals(typeIds[i]))
				return true;
		}
		return false;
	}
}