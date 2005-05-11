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
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.wst.internet.monitor.core.internal.provisional.MonitorCore;
/**
 * Monitor content provider.
 */
public class MonitorContentProvider implements IStructuredContentProvider {
	/**
	 * MonitorContentProvider constructor comment.
	 */
	public MonitorContentProvider() {
		super();
	}

	/*
	 * Disposes of this content provider.  
	 */
	public void dispose() {
		// do nothing
	}

	/*
	 * Returns the elements to display in the viewer 
	 * when its input is set to the given element. 
	 */
	public Object[] getElements(Object inputElement) {
		return MonitorCore.getMonitors();
	}

	/*
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}