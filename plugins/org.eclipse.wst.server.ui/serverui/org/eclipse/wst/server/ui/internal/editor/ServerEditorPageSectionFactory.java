/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class ServerEditorPageSectionFactory implements IServerEditorPageSectionFactory {
	private IConfigurationElement element;
	private ServerEditorPageSectionFactoryDelegate delegate;

	/**
	 * ServerEditorPageSectionFactory constructor comment.
	 */
	public ServerEditorPageSectionFactory(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 */
	public IConfigurationElement getConfigurationElement() {
		return element;
	}

	/**
	 * Returns the id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the order.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			String o = element.getAttribute("order");
			return Integer.parseInt(o);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * Returns the insertion id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getInsertionId() {
		return element.getAttribute("insertionId");
	}

	/**
	 * Return the ids of the server resource factories (specified
	 * using Java-import style) that this page may support.
	 * 
	 * @return java.lang.String[]
	 */
	protected String[] getTypeIds() {
		try {
			List list = new ArrayList();
			StringTokenizer st = new StringTokenizer(element.getAttribute("typeIds"), ",");
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				if (str != null && str.length() > 0)
					list.add(str.trim());
			}
			String[] s = new String[list.size()];
			list.toArray(s);
			return s;
		} catch (Exception e) {
			//Trace.trace("Could not get server resource from: " + element.getAttribute("serverResources"));
			return null;
		}
	}
	
	/**
	 * Returns true if the given server resource type (given by the
	 * id) can be opened with this editor. This result is based on
	 * the result of the getServerResources() method.
	 *
	 * @return boolean
	 */
	public boolean supportsType(String id) {
		if (id == null || id.length() == 0)
			return false;

		String[] s = getTypeIds();
		if (s == null)
			return false;
		
		int size = s.length;
		for (int i = 0; i < size; i++) {
			if (s[i].endsWith("*")) {
				if (id.length() >= s[i].length() && id.startsWith(s[i].substring(0, s[i].length() - 1)))
					return true;
			} else if (id.equals(s[i]))
				return true;
		}
		return false;
	}

	/*
	 * 
	 */
	public ServerEditorPageSectionFactoryDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ServerEditorPageSectionFactoryDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create server editorpage delegate", t);
			}
		}
		return delegate;
	}
	
	/**
	 * Returns true if this editor page should be visible with the given
	 * server instance and configuration combination. This allows (for
	 * instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 *
	 * <p>If the instance or configuration is being opened by itself, the
	 * other value (instance or configuration) will be null.
	 */
	public boolean shouldCreateSection(IServerWorkingCopy server) {
		try {
			return getDelegate().shouldCreateSection(server);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return false;
		}
	}

	/**
	 * Create the editor page.
	 */
	public IServerEditorSection createSection() {
		try {
			return getDelegate().createSection();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return null;
		}
	}
	
	public String toString() {
		return "ServerEditorSection [" + getId() + ", " + getInsertionId() + ", " + getOrder() + "]";
	}
}