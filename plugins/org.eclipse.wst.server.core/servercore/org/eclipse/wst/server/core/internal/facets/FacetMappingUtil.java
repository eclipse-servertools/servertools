/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
/**
 * The main server tooling plugin class.
 */
public class FacetMappingUtil extends Plugin {
	//	cached copy of all runtime component providers
	private static List<RuntimeComponentProviderWrapper> runtimeComponentProviders;

	//	cached copy of all runtime facet mappings
	private static List<RuntimeFacetMapping> runtimeFacetMappings;

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
	 * Uses the runtime component provider(s) that supports the given runtime to add
	 * additional runtime components to the list.
	 * The runtimeType may not be null.
	 *
	 * @param runtime a runtime
	 * @param components the existing list of components
	 */
	public static void addFacetRuntimeComponents(IRuntime runtime, List<IRuntimeComponent> components) {
		if (runtime == null || runtime.getRuntimeType() == null)
			throw new IllegalArgumentException();
		
		if (runtimeComponentProviders == null)
			loadRuntimeComponentProviders();
		
		Iterator<RuntimeComponentProviderWrapper> iterator = runtimeComponentProviders.iterator();
		while (iterator.hasNext()) {
			RuntimeComponentProviderWrapper runtimeComponentProvider = iterator.next();
			if (runtimeComponentProvider.supportsRuntimeType(runtime.getRuntimeType())) {
				List<IRuntimeComponent> list = runtimeComponentProvider.getComponents(runtime);
				if (list != null)
					components.addAll(list);
			}
		}
	}

	/**
	 * Load the runtime component providers.
	 */
	private static synchronized void loadRuntimeComponentProviders() {
		if (runtimeComponentProviders != null)
			return;
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .runtimeFacetComponentProviders extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "runtimeFacetComponentProviders");
		
		// load new wst extension point
		List<RuntimeComponentProviderWrapper> list = new ArrayList<RuntimeComponentProviderWrapper>(cf.length);
		for (IConfigurationElement ce : cf) {
			try {
				list.add(new RuntimeComponentProviderWrapper(ce));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded runtimeFacetComponentProvider: " + ce.getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load runtimeFacetComponentProvider: "
									+ ce.getAttribute("id"), t);
				}
			}
		}
		
		// load old jst extension point
		cf = registry.getConfigurationElementsFor("org.eclipse.jst.server.core.internalRuntimeComponentProviders");
		
		for (IConfigurationElement ce : cf) {
			try {
				list.add(new RuntimeComponentProviderWrapper(ce));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded runtimeFacetComponentProvider: " + ce.getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE,
							"  Could not load runtimeFacetComponentProvider: "
									+ ce.getAttribute("id"), t);
				}
			}
		}
		runtimeComponentProviders = list;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .runtimeFacetComponentProviders extension point -<-");
		}
	}

	/**
	 * Load the runtime facet mappings.
	 */
	private static synchronized void loadRuntimeFacetMapping() {
		if (runtimeFacetMappings != null)
			return;
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .runtimeFacetMapping extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor("org.eclipse.jst.server.core.runtimeFacetMappings");
		
		List<RuntimeFacetMapping> list = new ArrayList<RuntimeFacetMapping>(cf.length);
		for (IConfigurationElement ce : cf) {
			try {
				list.add(new RuntimeFacetMapping(ce));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG,
							"  Loaded runtimeFacetMapping: " + ce.getAttribute("runtimeTypeId"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(
							Trace.STRING_SEVERE,
							"  Could not load runtimeFacetMapping: "
									+ ce.getAttribute("id"), t);
				}
			}
		}
		runtimeFacetMappings = list;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .runtimeFacetMapping extension point -<-");
		}
	}	
}