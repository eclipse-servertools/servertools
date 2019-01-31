/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
import org.eclipse.wst.internet.monitor.ui.internal.MonitorUIPlugin;
/**
 * A label provider for the monitor server view.
 */
public class TreeLabelProvider implements ILabelProvider {
	/**
	 * TreeLabelProvider constructor comment.
	 */
	public TreeLabelProvider() {
		super();
	}

	/*
	 * Adds a listener to this label provider.
	 */
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	/*
	 * Disposes of this label provider.
	 */
	public void dispose() {
		// do nothing
	}

	/*
	 * Returns the image for the label of the given element for use
	 * in the given viewer.
	 */
	public Image getImage(Object element) {
		if (element instanceof Request) {
			if (element instanceof ResendHTTPRequest) {
				if (!((ResendHTTPRequest) element).hasBeenSent())
					return MonitorUIPlugin.getImage(MonitorUIPlugin.IMG_RESEND_REQUEST_RESPONSE);
			}
			return MonitorUIPlugin.getImage(MonitorUIPlugin.IMG_REQUEST_RESPONSE);
		}
		return MonitorUIPlugin.getImage(MonitorUIPlugin.IMG_HOST);
	}

	/*
	 * Returns the text for the label of the given element for use
	 * in the given viewer.
	 */
	public String getText(Object element) {
		if (element instanceof Request) {
			Request call = (Request) element;
			return call.getName();
		} else if (element instanceof Integer) {
			Integer in = (Integer) element;
			return "localhost:" + in.intValue();
		} else
			return element.toString();
	}

	/*
	 * Returns whether the label would be affected 
	 * by a change to the given property of the given element.
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
	/*
	 * Removes a listener to this label provider.
	 */
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
}