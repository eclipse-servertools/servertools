/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * Server UI core.
 */
public class ServerUICore {
	private static ServerLabelProvider labelProvider;

	// cached copy of all runtime wizards
	private static Map wizardFragments;
	
	static class WizardFragmentData {
		String id;
		IConfigurationElement ce;
		WizardFragment fragment;
		
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
	 * Returns the wizard fragment with the given id.
	 *
	 * @return
	 */
	public static WizardFragment getWizardFragment(String typeId) {
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
	 * Load the wizard fragments.
	 */
	private static synchronized void loadWizardFragments() {
		if (wizardFragments != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .wizardFragments extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "wizardFragments");

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

	protected static WizardFragment getWizardFragment(WizardFragmentData fragment) {
		if (fragment == null)
			return null;
	
		if (fragment.fragment == null) {
			try {
				fragment.fragment = (WizardFragment) fragment.ce.createExecutableExtension("class");
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create wizardFragment: " + fragment.ce.getAttribute("id"), t);
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
		Action action = new Action() {
			// dummy action
		};
		if (object != null) {
			StructuredSelection sel = new StructuredSelection(object);
			delegate.selectionChanged(action, sel);
		} else
			delegate.selectionChanged(action, null);

		delegate.run(action);
	}
}