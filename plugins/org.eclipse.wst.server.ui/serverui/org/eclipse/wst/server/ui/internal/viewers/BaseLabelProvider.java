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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
/**
 * A basic label provider.
 */
public abstract class BaseLabelProvider implements ILabelProvider {
	/**
	 * BaseLabelProvider constructor comment.
	 */
	public BaseLabelProvider() {
		super();
	}

	/**
	 * Adds a listener to this label provider. 
	 * Has no effect if an identical listener is already registered.
	 * <p>
	 * Label provider listeners are informed about state changes 
	 * that affect the rendering of the viewer that uses this label provider.
	 * </p>
	 *
	 * @param listener a label provider listener
	 */
	public void addListener(ILabelProviderListener listener) { }

	/**
	 * Disposes of this label provider.  When a label provider is
	 * attached to a viewer, the viewer will automatically call
	 * this method when the viewer is being closed.  When label providers
	 * are used outside of the context of a viewer, it is the client's
	 * responsibility to ensure that this method is called when the
	 * provider is no longer needed.
	 */
	public void dispose() { }

	/**
	 * Returns the label image for the given column of the given element.
	 *
	 * @param element the object representing the entire row, or 
	 *    <code>null</code> indicating that no input object is set
	 *    in the viewer
	 * @param columnIndex the zero-based index of the column in which
	 *   the label appears
	 */
	public Image getImage(Object element) {
		return null;
	}

	/**
	 * Returns the label text for the given column of the given element.
	 *
	 * @param element the object representing the entire row, or
	 *   <code>null</code> indicating that no input object is set
	 *   in the viewer
	 * @param columnIndex the zero-based index of the column in which the label appears
	 */
	public String getText(Object element) {
		return "";
	}

	/**
	 * Returns whether the label would be affected 
	 * by a change to the given property of the given element.
	 * This can be used to optimize a non-structural viewer update.
	 * If the property mentioned in the update does not affect the label,
	 * then the viewer need not update the label.
	 *
	 * @param element the element
	 * @param property the property
	 * @return <code>true</code> if the label would be affected,
	 *    and <code>false</code> if it would be unaffected
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * Removes a listener to this label provider.
	 * Has no affect if an identical listener is not registered.
	 *
	 * @param listener a label provider listener
	 */
	public void removeListener(ILabelProviderListener listener) { }
	
	protected String notNull(String s) {
		if (s == null)
			return "";
		return s;
	}
}