/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IModuleArtifactAdapter;
import org.eclipse.wst.server.core.model.ModuleArtifactAdapterDelegate;
/**
 * 
 */
public class ModuleArtifactAdapter implements IModuleArtifactAdapter {
	private IConfigurationElement element;
	private ModuleArtifactAdapterDelegate delegate;

	/**
	 * ModuleArtifactAdapter constructor comment.
	 */
	public ModuleArtifactAdapter(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * Returns the id of this ModuleArtifactAdapter.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the (super) class name that this adapter can work with.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return element.getAttribute("objectClass");
	}
	
	/**
	 * Returns true if the plugin that loaded this class has been loaded.
	 *
	 * @return boolean
	 */
	public boolean isPluginActivated() {
		String pluginId = element.getDeclaringExtension().getNamespace();
		return Platform.getBundle(pluginId).getState() == Bundle.ACTIVE;
	}

	/*
	 * @see IPublishManager#getDelegate()
	 */
	public ModuleArtifactAdapterDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (ModuleArtifactAdapterDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * Converts from a model object to an IModuleArtifact.
	 */
	public IModuleArtifact getModuleObject(Object obj) {
		try {
			return getDelegate().getModuleObject(obj);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate (" + obj + ") " + toString() + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ModuleArtifactAdapter[" + getId() + "]";
	}
}