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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.ui.editor.IOrdered;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class ServerEditorCore {
	// cached copy of all editor factories and actions
	private static List editorPageFactories;
	private static List editorPageSectionFactories;
	private static List editorActionFactories;
	
	/**
	 * Returns a List of all editor page factories
	 *
	 * @return java.util.List
	 */
	public static List getServerEditorPageFactories() {
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
		Trace.trace(Trace.CONFIG, "->- Loading .editorPages extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "editorPages");

		int size = cf.length;
		editorPageFactories = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				editorPageFactories.add(new ServerEditorPartFactory(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded editorPage: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load editorPage: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort pages
		sortOrderedList(editorPageFactories);
		Trace.trace(Trace.CONFIG, "-<- Done loading .editorPages extension point -<-");
	}
	
	/**
	 * Load the editor page section factory extension point.
	 */
	private static void loadEditorPageSectionFactories() {
		Trace.trace(Trace.CONFIG, "->- Loading .editorPageSections extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "editorPageSections");

		int size = cf.length;
		editorPageSectionFactories = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				editorPageSectionFactories.add(new ServerEditorPageSectionFactory(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded editorPageSection: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load editorPageSection: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort sections
		sortOrderedList(editorPageSectionFactories);
		Trace.trace(Trace.CONFIG, "-<- Done loading .editorPageSections extension point -<-");
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
		Trace.trace(Trace.CONFIG, "->- Loading .editorActions extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "editorActions");

		int size = cf.length;
		editorActionFactories = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				editorActionFactories.add(new ServerEditorActionFactory(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded editorAction: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load editorAction: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort pages
		sortOrderedList(editorActionFactories);
		Trace.trace(Trace.CONFIG, "-<- Done loading .editorActions extension point -<-");
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
					Object temp = a;
					list.set(i, b);
					list.set(j, temp);
				}
			}
		}
		return list;
	}
}