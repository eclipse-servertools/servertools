/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.wst.server.ui.internal.ServerLabelProvider;
import org.eclipse.wst.server.ui.internal.ServerUIPreferences;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.wizard.IWizardFragment;
/**
 * Server UI core.
 */
public class ServerUICore {
	// server UI plugin id
	public static final String PLUGIN_ID = "org.eclipse.wst.server.ui";

	protected static ServerLabelProvider labelProvider;

	// cached copy of all runtime wizards
	private static Map wizardFragments;
	
	static class WizardFragmentData {
		String id;
		IConfigurationElement ce;
		IWizardFragment fragment;
		
		public WizardFragmentData(String id, IConfigurationElement ce) {
			this.id = id;
			this.ce = ce;
		}
	}

	/**
	 * ServerUICore constructor comment.
	 */
	private ServerUICore() {
		super();
	}

	/**
	 * Return the UI preferences.
	 * 
	 * @return IServerUIPreferences
	 */
	public static IServerUIPreferences getPreferences() {
		return new ServerUIPreferences();
	}
	
	/**
	 * Returns the wizard fragment with the given id.
	 *
	 * @return
	 */
	public static IWizardFragment getWizardFragment(String typeId) {
		if (typeId == null)
			return null;

		if (wizardFragments == null)
			loadWizardFragments();
		
		Iterator iterator = wizardFragments.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (typeId.equals(key)) {
				WizardFragmentData data = (WizardFragmentData) wizardFragments.get(key);
				return getWizardFragment(data);
			}
		}
		return null;
	}

	/**
	 * Load the server startups.
	 */
	private static void loadWizardFragments() {
		Trace.trace(Trace.CONFIG, "->- Loading .wizardFragments extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUICore.PLUGIN_ID, "wizardFragments");

		int size = cf.length;
		wizardFragments = new HashMap(size);
		for (int i = 0; i < size; i++) {
			try {
				String id = cf[i].getAttribute("typeIds");
				wizardFragments.put(id, new WizardFragmentData(id, cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded wizardFragment: " + id);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load wizardFragment: " + cf[i].getAttribute("id"), t);
			}
		}
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .wizardFragments extension point -<-");
	}
	
	protected static IWizardFragment getWizardFragment(WizardFragmentData fragment) {
		if (fragment == null)
			return null;
	
		if (fragment.fragment == null) {
			try {
				fragment.fragment = (IWizardFragment) fragment.ce.createExecutableExtension("class");
			} catch (Exception cex) {
				Trace.trace(Trace.SEVERE, "Could not create wizardFragment: " + fragment.ce.getAttribute("id"), cex);
			}
		}
		return fragment.fragment;
	}

	public static ILabelProvider getLabelProvider() {
		if (labelProvider == null)
			labelProvider = new ServerLabelProvider();
		return labelProvider;
	}
	
	public static void runOnServer(Object object, String launchMode) {
		RunOnServerActionDelegate delegate = new RunOnServerActionDelegate();
		Action action = new Action() { };
		if (object != null) {
			StructuredSelection sel = new StructuredSelection(object);
			delegate.selectionChanged(action, sel);
		} else
			delegate.selectionChanged(action, null);

		delegate.run(action);
	}
}