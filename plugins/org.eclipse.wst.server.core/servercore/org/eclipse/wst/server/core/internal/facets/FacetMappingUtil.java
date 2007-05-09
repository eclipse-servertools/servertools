/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.facets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
/**
 * The main server tooling plugin class.
 */
public class FacetMappingUtil extends Plugin {
	//	cached copy of all runtime component providers
	private static List runtimeComponentProviders;

	//	cached copy of all runtime facet mappings
	private static List runtimeFacetMappings;

	/**
	 * Returns an array of all known runtime facet mapping instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime facet mapping instances
	 *    {@link RuntimeFacetMapping}
	 */
	public static RuntimeFacetMapping[] getRuntimeFacetMapping() {
		if (runtimeFacetMappings == null)
			loadRuntimeFacetMapping();
		
		RuntimeFacetMapping[] rfm = new RuntimeFacetMapping[runtimeFacetMappings.size()];
		runtimeFacetMappings.toArray(rfm);
		return rfm;
	}

	/**
	 * Returns the runtime component provider that supports the given runtime type, or <code>null</code>
	 * if none. This convenience method searches the list of known runtime
	 * component providers for the one with a matching runtime type.
	 * The runtimeType may not be null.
	 *
	 * @param runtimeType a runtime type
	 * @return the runtime component provider instance, or <code>null</code> if
	 *   there is no runtime component provider with the given id
	 */
	public static RuntimeComponentProviderWrapper findRuntimeFacetComponentProvider(IRuntimeType runtimeType) {
		if (runtimeType == null)
			throw new IllegalArgumentException();

		if (runtimeComponentProviders == null)
			loadRuntimeComponentProviders();
		
		Iterator iterator = runtimeComponentProviders.iterator();
		while (iterator.hasNext()) {
			RuntimeComponentProviderWrapper runtimeComponentProvider = (RuntimeComponentProviderWrapper) iterator.next();
			if (runtimeComponentProvider.supportsRuntimeType(runtimeType))
				return runtimeComponentProvider;
		}
		return null;
	}

	/**
	 * Load the runtime component providers.
	 */
	private static synchronized void loadRuntimeComponentProviders() {
		if (runtimeComponentProviders != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .runtimeFacetComponentProviders extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "runtimeFacetComponentProviders");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new RuntimeComponentProviderWrapper(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded runtimeFacetComponentProvider: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeFacetComponentProvider: " + cf[i].getAttribute("id"), t);
			}
		}
		runtimeComponentProviders = list;
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .runtimeFacetComponentProviders extension point -<-");
	}

	/**
	 * Load the runtime facet mappings.
	 */
	private static synchronized void loadRuntimeFacetMapping() {
		if (runtimeFacetMappings != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .runtimeFacetMapping extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor("org.eclipse.jst.server.core.runtimeFacetMappings");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new RuntimeFacetMapping(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded runtimeFacetMapping: " + cf[i].getAttribute("runtimeTypeId"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeFacetMapping: " + cf[i].getAttribute("id"), t);
			}
		}
		runtimeFacetMappings = list;
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .runtimeFacetMapping extension point -<-");
	}	
}