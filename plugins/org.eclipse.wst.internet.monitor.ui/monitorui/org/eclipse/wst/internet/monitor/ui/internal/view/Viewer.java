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
package org.eclipse.wst.internet.monitor.ui.internal.view;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.internet.monitor.ui.internal.Trace;
import org.eclipse.wst.internet.monitor.ui.internal.provisional.ContentViewer;
/**
 * 
 */
public class Viewer {
	private IConfigurationElement element;

	/**
	 * Create a new content viewer.
	 * 
	 * @param element a configuration element
	 */
	public Viewer(IConfigurationElement element) {
		super();
		this.element = element;
	}

	public String getId() {
		return element.getAttribute("id");
	}

	public String getLabel() {
		String label = element.getAttribute("label");
		if (label == null)
			return "n/a";
		return label;
	}

	/**
	 * Create an instance of the viewer.
	 * 
	 * @return the viewer, or <code>null</code> if it couldn't be loaded
	 */
	public ContentViewer createViewer() {
		try {
			return (ContentViewer) element.createExecutableExtension("class");
		} catch (Exception e) {
			if(Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not create viewer" + toString(), e);
			}
			return null;
		}
	}

	public boolean isRequestViewer() {
		return element.getAttribute("type").toLowerCase().indexOf("request") >= 0;
	}

	public boolean isResponseViewer() {
		return element.getAttribute("type").toLowerCase().indexOf("response") >= 0;
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return String
	 */
	public String toString() {
		return "ContentViewer[" + getId() + "]";
	}
}
