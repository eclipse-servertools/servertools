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
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.*;
/**
 * The main server tooling plugin class.
 */
public class JavaServerPlugin extends Plugin {
	/**
	 * Java server plugin id
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.core";

	// singleton instance of this class
	private static JavaServerPlugin singleton;

	//	cached copy of all runtime classpath providers
	private static List runtimeClasspathProviders;

	//	cached copy of all runtime facet mappings
	private static List runtimeFacetMappings;

	/**
	 * Create the JavaServerPlugin.
	 */
	public JavaServerPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return a singleton instance
	 */
	public static JavaServerPlugin getInstance() {
		return singleton;
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status a status
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}
	
	/**
	 * Convenience method for logging.
	 *
	 * @param t a throwable
	 */
	public static void log(Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Internal error", t)); //$NON-NLS-1$
	}

	/**
	 * Returns an array of all known runtime classpath provider instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime classpath provider instances
	 *    {@link RuntimeClasspathProviderWrapper}
	 */
	public static RuntimeClasspathProviderWrapper[] getRuntimeClasspathProviders() {
		if (runtimeClasspathProviders == null)
			loadRuntimeClasspathProviders();
		
		RuntimeClasspathProviderWrapper[] rth = new RuntimeClasspathProviderWrapper[runtimeClasspathProviders.size()];
		runtimeClasspathProviders.toArray(rth);
		return rth;
	}

	/**
	 * Returns the runtime classpath provider with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known runtime
	 * classpath providers ({@link #getRuntimeClasspathProviders()}) for the one with
	 * a matching runtime classpath provider id ({@link RuntimeClasspathProviderWrapper#getId()}).
	 * The id may not be null.
	 *
	 * @param id the runtime classpath provider id
	 * @return the runtime classpath provider instance, or <code>null</code> if
	 *   there is no runtime classpath provider with the given id
	 */
	public static RuntimeClasspathProviderWrapper findRuntimeClasspathProvider(String id) {
		if (id == null)
			throw new IllegalArgumentException();

		if (runtimeClasspathProviders == null)
			loadRuntimeClasspathProviders();
		
		Iterator iterator = runtimeClasspathProviders.iterator();
		while (iterator.hasNext()) {
			RuntimeClasspathProviderWrapper runtimeClasspathProvider = (RuntimeClasspathProviderWrapper) iterator.next();
			if (id.equals(runtimeClasspathProvider.getId()))
				return runtimeClasspathProvider;
		}
		return null;
	}

	/**
	 * Load the runtime classpath providers.
	 */
	private static synchronized void loadRuntimeClasspathProviders() {
		if (runtimeClasspathProviders != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .runtimeClasspathProviders extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(JavaServerPlugin.PLUGIN_ID, "runtimeClasspathProviders");

		int size = cf.length;
		runtimeClasspathProviders = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				RuntimeClasspathProviderWrapper runtimeClasspathProvider = new RuntimeClasspathProviderWrapper(cf[i]);
				runtimeClasspathProviders.add(runtimeClasspathProvider);
				Trace.trace(Trace.CONFIG, "  Loaded runtimeClasspathProviders: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeClasspathProviders: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .runtimeClasspathProviders extension point -<-");
	}
	
	/**
	 * Returns an array of all known runtime classpath provider instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime classpath provider instances
	 *    {@link RuntimeClasspathProviderWrapper}
	 */
	public static RuntimeFacetMapping[] getRuntimeFacetMapping() {
		if (runtimeFacetMappings == null)
			loadRuntimeFacetMapping();
		
		RuntimeFacetMapping[] rfm = new RuntimeFacetMapping[runtimeFacetMappings.size()];
		runtimeFacetMappings.toArray(rfm);
		return rfm;
	}

	/**
	 * Load the runtime facet mappings.
	 */
	private static synchronized void loadRuntimeFacetMapping() {
		if (runtimeFacetMappings != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .runtimeFacetMapping extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(JavaServerPlugin.PLUGIN_ID, "runtimeFacetMappings");

		int size = cf.length;
		runtimeFacetMappings = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				RuntimeFacetMapping rfm = new RuntimeFacetMapping(cf[i]);
				runtimeFacetMappings.add(rfm);
				Trace.trace(Trace.CONFIG, "  Loaded runtimeFacetMapping: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeFacetMapping: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .runtimeFacetMapping extension point -<-");
	}
}