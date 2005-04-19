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
package org.eclipse.wst.server.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.provisional.ServerEditorActionFactoryDelegate;
import org.eclipse.ui.IEditorSite;
/**
 * A default server that can be created for a set of given
 * natures.
 */
public class ServerEditorActionFactory implements IServerEditorActionFactory {
	private IConfigurationElement element;
	private ServerEditorActionFactoryDelegate delegate;

	/**
	 * ServerEditorActionFactory constructor.
	 * 
	 * @param element a configuration element
	 */
	public ServerEditorActionFactory(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 */
	protected IConfigurationElement getConfigurationElement() {
		return element;
	}

	/**
	 * Returns the id of this default server.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the id of this default server.
	 *
	 * @return java.lang.String
	 */
	public String getName() {
		return element.getAttribute("name");
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
	 * Return the ids of the server resource factories (specified
	 * using Java-import style) that this page may support.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getTypeIds() {
		try {
			List list = new ArrayList();
			StringTokenizer st = new StringTokenizer(element.getAttribute("typeIds"), ",");
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				if (str != null && str.length() > 0)
					list.add(str);
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
	 * @see IServerEditorActionFactory#supportsServerElementType(String)
	 */
	public boolean supportsServerElementType(String id) {
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
	public ServerEditorActionFactoryDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ServerEditorActionFactoryDelegate) element.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create server action factory delegate", t);
			}
		}
		return delegate;
	}
	
	/**
	 * @see IServerEditorActionFactory#shouldDisplay(IServerWorkingCopy)
	 */
	public boolean shouldDisplay(IServerWorkingCopy server) {
		try {
			return getDelegate().shouldDisplay(server);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return false;
		}
	}

	/**
	 * @see IServerEditorActionFactory#createAction(IEditorSite, IServerEditorPartInput)
	 */
	public IAction createAction(IEditorSite site, IServerEditorPartInput input) {
		try {
			return getDelegate().createAction(site, input);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return null;
		}
	}
}