/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class ServerEditorCore {
	// cached copy of all editor factories and actions
	private static List<ServerEditorPartFactory> editorPageFactories;
	private static List<ServerEditorPageSectionFactory> editorPageSectionFactories;
	private static List editorActionFactories;

	/**
	 * Returns a List of all editor page factories
	 *
	 * @return java.util.List
	 */
	public static List<ServerEditorPartFactory> getServerEditorPageFactories() {
		if (editorPageFactories == null)
			loadEditorPageFactories();
		return editorPageFactories;
	}

	/**
	 * Returns a List of all editor page section factories
	 *
	 * @return java.util.List
	 */
	public static List getServerEditorPageSectionFactories() {
		if (editorPageSectionFactories == null)
			loadEditorPageSectionFactories();
		return editorPageSectionFactories;
	}

	/**
	 * Load the editor page factory extension point.
	 */
	private static void loadEditorPageFactories() {
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .editorPages extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, ServerUIPlugin.EXTENSION_EDITOR_PAGES);
		List<ServerEditorPartFactory> list = new ArrayList<ServerEditorPartFactory>(cf.length);
		loadEditorPageFactories(cf, list);
		editorPageFactories = list;
		ServerUIPlugin.addRegistryListener();
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .editorPages extension point -<-");
		}
	}

	/**
	 * Load the editor page factory extension point.
	 */
	private static void loadEditorPageFactories(IConfigurationElement[] cf, List<ServerEditorPartFactory> list) {
		int size = cf.length;
		for (int i = 0; i < size; i++) {
			try {
				list.add(new ServerEditorPartFactory(cf[i]));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded editorPage: " + cf[i].getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "  Could not load editorPage: " + cf[i].getAttribute("id"), t);
				}
			}
		}
		
		// sort pages
		sortOrderedList(list);
	}

	public static void handleEditorPageFactoriesDelta(IExtensionDelta delta) {
		if (editorPageFactories == null) // not loaded yet
			return;
		
		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();
		
		List<ServerEditorPartFactory> list = new ArrayList<ServerEditorPartFactory>(editorPageFactories);
		if (delta.getKind() == IExtensionDelta.ADDED)
			loadEditorPageFactories(cf, list);
		else {
			int size = list.size();
			ServerEditorPartFactory[] sepf = new ServerEditorPartFactory[size];
			list.toArray(sepf);
			int size2 = cf.length;
			
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size2; j++) {
					if (sepf[i].getId().equals(cf[j].getAttribute("id"))) {
						list.remove(sepf[i]);
					}
				}
			}
		}
		editorPageFactories = list;
	}

	/**
	 * Load the editor page section factory extension point.
	 */
	private static void loadEditorPageSectionFactories() {
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .editorPageSections extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, ServerUIPlugin.EXTENSION_EDITOR_PAGE_SECTIONS);
		List<ServerEditorPageSectionFactory> list = new ArrayList<ServerEditorPageSectionFactory>(cf.length);
		loadEditorPageSectionFactories(cf, list);
		editorPageSectionFactories = list;
		ServerUIPlugin.addRegistryListener();
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .editorPageSections extension point -<-");
		}
	}

	/**
	 * Load the editor page section factory extension point.
	 */
	private static void loadEditorPageSectionFactories(IConfigurationElement[] cf, List<ServerEditorPageSectionFactory> list) {
		int size = cf.length;
		for (int i = 0; i < size; i++) {
			try {
				list.add(new ServerEditorPageSectionFactory(cf[i]));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded editorPageSection: " + cf[i].getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "  Could not load editorPageSection: " + cf[i].getAttribute("id"),
							t);
				}
			}
		}
		
		// sort sections
		sortOrderedList(list);
	}

	public static void handleEditorPageSectionFactoriesDelta(IExtensionDelta delta) {
		if (editorPageSectionFactories == null) // not loaded yet
			return;
		
		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();
		
		List<ServerEditorPageSectionFactory> list = new ArrayList<ServerEditorPageSectionFactory>(editorPageSectionFactories);
		if (delta.getKind() == IExtensionDelta.ADDED)
			loadEditorPageSectionFactories(cf, list);
		else {
			int size = list.size();
			ServerEditorPageSectionFactory[] seps = new ServerEditorPageSectionFactory[size];
			list.toArray(seps);
			int size2 = cf.length;
			
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size2; j++) {
					if (seps[i].getId().equals(cf[j].getAttribute("id"))) {
						list.remove(seps[i]);
					}
				}
			}
		}
		editorPageSectionFactories = list;
	}

	/**
	 * Returns a List of all editor action factories.
	 *
	 * @return java.util.List
	 */
	public static List getServerEditorActionFactories() {
		if (editorActionFactories == null)
			loadEditorActionFactories();
		return editorActionFactories;
	}
	
	/**
	 * Load the editor action factories extension point.
	 */
	private static void loadEditorActionFactories() {
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "->- Loading .editorActions extension point ->-");
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "editorActions");

		int size = cf.length;
		List<ServerEditorActionFactory> list = new ArrayList<ServerEditorActionFactory>(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new ServerEditorActionFactory(cf[i]));
				if (Trace.CONFIG) {
					Trace.trace(Trace.STRING_CONFIG, "  Loaded editorAction: " + cf[i].getAttribute("id"));
				}
			} catch (Throwable t) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "  Could not load editorAction: " + cf[i].getAttribute("id"), t);
				}
			}
		}
		
		// sort pages
		sortOrderedList(list);
		editorActionFactories = list;
		
		if (Trace.CONFIG) {
			Trace.trace(Trace.STRING_CONFIG, "-<- Done loading .editorActions extension point -<-");
		}
	}
	
	/**
	 * Sort the given list of IOrdered items into indexed order. This method
	 * modifies the original list, but returns the value for convenience.
	 *
	 * @param list java.util.List
	 * @return java.util.List
	 */
	public static List sortOrderedList(List list) {
		if (list == null)
			return null;

		int size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				IOrdered a = (IOrdered) list.get(i);
				IOrdered b = (IOrdered) list.get(j);
				if (a.getOrder() > b.getOrder()) {
					IOrdered temp = a;
					list.set(i, b);
					list.set(j, temp);
				}
			}
		}
		return list;
	}
}
