/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
/**
 * The monitor core plugin.
 */
public class MonitorPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.eclipse.wst.internet.monitor.core";

	private static MonitorPlugin singleton;
	
	protected Map protocolAdapters;
	protected Map contentFilters;
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
	 * Returns the translated String found with the given key.
	 *
	 * @return java.lang.String
	 * @param key java.lang.String
	 */
	public static String getResource(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given arguments using java.text.MessageFormat.
	 *
	 * @param key java.lang.String
	 * @param arguments java.lang.Object[]
	 * @return java.lang.String
	 */
	public static String getResource(String key, Object[] arguments) {
		try {
			String text = getResource(key);
			return MessageFormat.format(text, arguments);
		} catch (Exception e) {
			return key;
		}
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given arguments using java.text.MessageFormat.
	 *
	 * @param key the key
	 * @param arg an argument
	 * @return the translated string
	 */
	public static String getResource(String key, String arg) {
		return getResource(key, new String[] { arg });
	}
	
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
		return (ProtocolAdapter) protocolAdapters.get(id);
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
		List list = new ArrayList();
		Iterator iterator = protocolAdapters.values().iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		ProtocolAdapter[] types = new ProtocolAdapter[list.size()];
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
	
	public IContentFilter findContentFilter(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		return (IContentFilter) contentFilters.get(id);
	}

	protected synchronized void loadProtocolAdapters() {
		if (protocolAdapters != null)
			return;
		Trace.trace(Trace.CONFIG, "Loading protocol adapters"); 
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(MonitorPlugin.PLUGIN_ID, "internalProtocolAdapters");

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