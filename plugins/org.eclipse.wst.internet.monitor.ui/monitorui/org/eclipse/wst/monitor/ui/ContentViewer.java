/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.monitor.ui;

import org.eclipse.swt.widgets.Composite;
/**
 * Viewer interface for displaying request and response messages.
 */
public abstract class ContentViewer {
	/**
	 * Initializes the viewer so it can be used to display content. Requires the parent
	 * Composite that the viewer will display its information in.
	 * 
	 * @param parent the parent composite
	 */
	public void init(Composite parent) {
		// do nothing
	}

	/**
	 * Sets the content that the viewer should display. The parameter may be null if
	 * no content should be displayed.
	 * 
	 * @param b the content
	 */
	public void setContent(byte[] b) {
		// do nothing
	}

	/**
	 * Disposes this viewer and any underlying resources such as a composite.
	 */
	public void dispose() {
		// do nothing
	}
}