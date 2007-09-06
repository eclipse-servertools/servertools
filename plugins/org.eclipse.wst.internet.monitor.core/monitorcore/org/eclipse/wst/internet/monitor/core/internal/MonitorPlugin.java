/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.util.*;

import org.eclipse.core.runtime.*;
/**
 * The monitor core plugin.
 */
public class MonitorPlugin extends Plugin {
	/**
	 * The plugin/bundle id.
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.internet.monitor.core";

	private static MonitorPlugin singleton;
	
	protected Map<String, ProtocolAdapter> protocolAdapters;
	protected Map<String, IContentFilter> contentFilters;
	protected boolean startupsLoaded;

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
	 * @return org.eclipse.wst.internet.monitor.core.MonitorPlugin
	 */
	public static MonitorPlugin getInstance() {
		return singleton;
	}

	/**
	 * Returns the default protocol type.
	 * 
	 * @return the protocol
	 */
	public String getDefaultType() {
		return "HTTP";
	}

	/**
	 * Returns the protocol adapter with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * protocol adapters ({@link #getProtocolAdapters()}) for the one with a
	 * matching id.
	 *
	 * @param id the protocol adapter id; must not be <code>null</code>
	 * @return the protocol adapter instance, or <code>null</code> if there
	 *   is no protocol adapter with the given id
	 */
	public ProtocolAdapter getProtocolAdapter(String id) {
		return protocolAdapters.get(id);
	}

	/**
	 * Returns a list of all known protocol adapter instances.
	 * <p>
	 * Protocol adapters are registered via the <code>protocolAdapaters</code>
	 * extension point in the <code>org.eclipse.wst.internet.monitor.core</code>
	 * plug-in.
	 * </p>
	 * <p>
	 * A new array is returned on each call; clients may safely store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of protocol adapter instances
	 */
	public ProtocolAdapter[] getProtocolAdapters() {
		List<ProtocolAdapter> list = new ArrayList<ProtocolAdapter>();
		Iterator<ProtocolAdapter> iterator = protocolAdapters.values().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		ProtocolAdapter[] types = new ProtocolAdapter[list.size()];
		list.toArray(types);
		return types;
	}

	/**
	 * Return the content filters.
	 * 
	 * @return an array of content filters
	 */
	public IContentFilter[] getContentFilters() {
		List<IContentFilter> list = new ArrayList<IContentFilter>();
		Iterator<IContentFilter> iterator = contentFilters.values().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		IContentFilter[] cf = new IContentFilter[list.size()];
		list.toArray(cf);
		return cf;
	}

	/**
	 * Find a content filter by the id.
	 * 
	 * @param id an id
	 * @return the content filter, or <code>null</code> if it couldn't be found
	 */
	public IContentFilter findContentFilter(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		return contentFilters.get(id);
	}

	protected synchronized void loadProtocolAdapters() {
		if (protocolAdapters != null)
			return;
		Trace.trace(Trace.CONFIG, "Loading protocol adapters"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "internalProtocolAdapters");

		int size = cf.length;
		Map<String, ProtocolAdapter> map = new HashMap<String, ProtocolAdapter>(size);
		for (int i = 0; i < size; i++) {
			String id = cf[i].getAttribute("id");
			Trace.trace(Trace.CONFIG, "Loading adapter: " + id);
			map.put(id, new ProtocolAdapter(cf[i]));
		}
		protocolAdapters = map;
	}

	protected synchronized void loadContentFilters() {
		if (contentFilters != null)
			return;
		Trace.trace(Trace.CONFIG, "Loading content filters"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "internalContentFilters");

		int size = cf.length;
		Map<String, IContentFilter> map = new HashMap<String, IContentFilter>(size);
		for (int i = 0; i < size; i++) {
			String id = cf[i].getAttribute("id");
			Trace.trace(Trace.CONFIG, "Loading filter: " + id);
			map.put(id, new ContentFilter(cf[i]));
		}
		contentFilters = map;
	}

	protected synchronized void executeStartups() {
		if (startupsLoaded)
			return;
		
		Trace.trace(Trace.CONFIG, "Loading startups"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "internalStartup");
		
		int size = cf.length;
		for (int i = 0; i < size; i++) {
			String id = cf[i].getAttribute("id");
			Trace.trace(Trace.CONFIG, "Loading startup: " + id);
			try {
				IStartup startup = (IStartup) cf[i].createExecutableExtension("class");
				try {
					startup.startup();
				} catch (Exception ex) {
					Trace.trace(Trace.SEVERE, "Startup failed" + startup.toString(), ex);
				}
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create startup: " + id, e);
			}
		}
	}
}
