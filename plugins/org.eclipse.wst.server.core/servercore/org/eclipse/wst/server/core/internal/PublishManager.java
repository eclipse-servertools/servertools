/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IPublishControl;
import org.eclipse.wst.server.core.IPublishManager;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IPublishManagerDelegate;
/**
 *
 */
public class PublishManager implements IPublishManager {
	private IConfigurationElement element;
	private IPublishManagerDelegate delegate;

	/**
	 * PublishManager constructor comment.
	 */
	public PublishManager(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * Returns the id of this publish manager.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}

	/*
	 * @see IPublishManager#getDescription()
	 */
	public String getDescription() {
		String s = element.getAttribute("description");
		if (s == null || s.length() == 0)
			return "?";
		else
			return s;
	}

	/*
	 * @see IPublishManager#getLabel()
	 */
	public String getName() {
		String s = element.getAttribute("name");
		if (s == null || s.length() == 0)
			return "?";
		else
			return s;
	}

	/*
	 * @see IPublishManager#getResourcesToDelete(IModule)
	 */
	public List getResourcesToDelete(IModule module) {
		return getDelegate().getResourcesToDelete(module);
	}

	/*
	 * @see IPublishManager#getResourcesToPublish(IModule)
	 */
	public List getResourcesToPublish(IModule module) {
		return getDelegate().getResourcesToPublish(module);
	}

	/*
	 * @see IPublishManager#resolve()
	 */
	public void resolve(IPublishControl[] control, IModule[] module, IProgressMonitor monitor) {
		getDelegate().resolve(control, module, monitor);
	}

	/*
	 * @see IPublishManager#getDelegate()
	 */
	public IPublishManagerDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = (IPublishManagerDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace("Could not create publisher", e);
			}
		}
		return delegate;
	}
}
