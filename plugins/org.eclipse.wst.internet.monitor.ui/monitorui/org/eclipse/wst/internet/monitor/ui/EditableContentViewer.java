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
package org.eclipse.wst.internet.monitor.ui;
/**
 * Editable viewer interface for viewing and editing requests and responses.
 */
public abstract class EditableContentViewer extends ContentViewer {
	/**
	 * Set whether the current viewer is editable, that is, the user is able to
	 * edit the content.
	 * 
	 * @param editable <code>true</code> if true the content can be edited,
	 *     and <code>false</code> otherwise
	 */
	public void setEditable(boolean editable) {
		// do nothing
	}

	/**
	 * Get the content from the viewer. This is really only interesting if the
	 * content has changed.
	 * 
	 * @return the content from the viewer.
	 */
	public byte[] getContent() {
		return null;
	}
}