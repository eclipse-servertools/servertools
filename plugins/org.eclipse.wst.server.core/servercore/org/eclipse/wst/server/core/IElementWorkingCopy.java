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
package org.eclipse.wst.server.core;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
/**
 * A server element.
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IElementWorkingCopy extends IElement {
	public void setAttribute(String attributeName, int value);

	public void setAttribute(String attributeName, boolean value);

	public void setAttribute(String attributeName, String value);

	public void setAttribute(String attributeName, List value);

	public void setAttribute(String attributeName, Map value);

	public void setName(String name);
	
	public void setLocked(boolean b);
	
	public void setPrivate(boolean b);
	
	public boolean isDirty();
	
	public void release();

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Fire a property change event.
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue);
}