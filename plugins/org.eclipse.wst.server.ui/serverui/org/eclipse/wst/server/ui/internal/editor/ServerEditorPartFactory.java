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
import org.eclipse.wst.server.ui.editor.ServerEditorPartFactoryDelegate;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.ui.IEditorPart;
/**
 * 
 */
public class ServerEditorPartFactory implements IServerEditorPartFactory {
	private IConfigurationElement element;
	private ServerEditorPartFactoryDelegate delegate;

	/**
	 * ServerEditorPartFactory constructor comment.
	 */
	public ServerEditorPartFactory(IConfigurationElement element) {
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
	 * Returns the id of this part factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the name of this part factory. 
	 *
	 * @return java.lang.String
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	protected String[] getInsertionIds() {
		try {
			List list = new ArrayList();
			StringTokenizer st = new StringTokenizer(element.getAttribute("insertionIds"), ",");
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
	
	public boolean supportsInsertionId(String id) {
		if (id == null || id.length() == 0)
			return false;

		String[] s = getInsertionIds();
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
	 * Return the ids of the server and server configuration type ids (specified
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
	 * Returns true if the given server or server configuration type id
	 * can be opened with this editor. This result is based on
	 * the result of the getTypeIds() method.
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

	public ServerEditorPartFactoryDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ServerEditorPartFactoryDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create server editorpage delegate", t);
			}
		}
		return delegate;
	}
	
	/**
	 * Returns true if this editor page should be visible with the given server.
	 * This allows (for instance) complex configuration pages to only be shown when used
	 * with non-unittest servers.
	 */
	public boolean shouldCreatePage(IServerWorkingCopy server) {
		try {
			return getDelegate().shouldCreatePage(server);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return false;
		}
	}

	/**
	 * Create the editor page.
	 */
	public IEditorPart createPage() {
		try {
			return getDelegate().createPage();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return null;
		}
	}
}