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
package org.eclipse.wst.monitor.core.internal;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.monitor.core.IContentFilter;
import org.eclipse.wst.monitor.core.IProtocolAdapter;
import org.eclipse.wst.monitor.core.IRequestListener;
/**
 * The monitor core plugin.
 */
public class MonitorPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.eclipse.wst.monitor.core";

	private static MonitorPlugin singleton;
	
	protected Map protocolAdapters;
	protected Map contentFilters;
	protected IRequestListener[] requestListeners;

	/**
	 * MonitorPlugin constructor comment.
	 */
	public MonitorPlugin() {
		super();
		singleton = this;
		loadProtocolAdapters();
		loadContentFilters();
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return org.eclipse.wst.monitor.core.MonitorPlugin
	 */
	public static MonitorPlugin getInstance() {
		return singleton;
	}

	/**
	 * Returns the translated String found with the given key.
	 *
	 * @return java.lang.String
	 * @param key java.lang.String
	 */
	public static String getString(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}
	
	public IProtocolAdapter getDefaultType() {
		return (ProtocolAdapter) protocolAdapters.get("HTTP");
	}
	
	public IProtocolAdapter getProtocolAdapter(String id) {
		return (ProtocolAdapter) protocolAdapters.get(id);
	}

	public IProtocolAdapter[] getProtocolAdapters() {
		List list = new ArrayList();
		Iterator iterator = protocolAdapters.values().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		IProtocolAdapter[] types = new IProtocolAdapter[list.size()];
		list.toArray(types);
		return types;
	}

	public IContentFilter[] getContentFilters() {
		List list = new ArrayList();
		Iterator iterator = contentFilters.values().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		IContentFilter[] cf = new IContentFilter[list.size()];
		list.toArray(cf);
		return cf;
	}
	
	public IContentFilter getContentFilter(String id) {
		return (IContentFilter) contentFilters.get(id);
	}
	
	public IRequestListener[] getRequestListeners() {
		if (requestListeners == null)
			loadRequestListeners();
		return requestListeners;
	}

	protected synchronized void loadProtocolAdapters() {
		if (protocolAdapters != null)
			return;
		Trace.trace(Trace.CONFIG, "Loading protocol adapters"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "protocolAdapters");

		int size = cf.length;
		protocolAdapters = new HashMap(size);
		for (int i = 0; i < size; i++) {
			String id = cf[i].getAttribute("id");
			Trace.trace(Trace.CONFIG, "Loading adapter: " + id);
			protocolAdapters.put(id, new ProtocolAdapter(cf[i]));
		}
	}
	
	protected synchronized void loadContentFilters() {
		if (contentFilters != null)
			return;
		Trace.trace(Trace.CONFIG, "Loading content filters"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "contentFilters");

		int size = cf.length;
		contentFilters = new HashMap(size);
		for (int i = 0; i < size; i++) {
			String id = cf[i].getAttribute("id");
			Trace.trace(Trace.CONFIG, "Loading filter: " + id);
			contentFilters.put(id, new ContentFilter(cf[i]));
		}
	}
	
	protected synchronized void loadRequestListeners() {
		if (requestListeners != null)
			return;
		Trace.trace(Trace.CONFIG, "Loading request listeners"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "requestListeners");

		int size = cf.length;
		List list = new ArrayList();
		for (int i = 0; i < size; i++) {
			String id = cf[i].getAttribute("id");
			Trace.trace(Trace.CONFIG, "Loading request listener: " + id);
			try {
				IRequestListener rl = (IRequestListener) cf[i].createExecutableExtension("class");
				list.add(rl);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create request listener: " + id, e);
				return;
			}
		}
		
		size = list.size();
		requestListeners = new IRequestListener[size];
		list.toArray(requestListeners);
	}
}