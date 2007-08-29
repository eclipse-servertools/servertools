/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * 
 */
public class WorkingCopyHelper {
	protected Base base;
	protected boolean isDirty;

	// property change listeners
	private transient List<PropertyChangeListener> propertyListeners;

	public WorkingCopyHelper(Base base) {
		this.base = base;
	}

	public void setAttribute(String attributeName, int value) {
		int current = base.getAttribute(attributeName, 0);
		if (base.isAttributeSet(attributeName) && current == value)
			return;
		
		isDirty = true;
		base.map.put(attributeName, Integer.toString(value));
		firePropertyChangeEvent(attributeName, new Integer(current), new Integer(value));
	}

	public void setAttribute(String attributeName, boolean value) {
		boolean current = base.getAttribute(attributeName, false);
		if (base.isAttributeSet(attributeName) && current == value)
			return;
		
		isDirty = true;
		base.map.put(attributeName, Boolean.toString(value));
		firePropertyChangeEvent(attributeName, new Boolean(current), new Boolean(value));
	}

	public void setAttribute(String attributeName, String value) {
		String current = base.getAttribute(attributeName, (String)null);
		if (base.isAttributeSet(attributeName) && current != null && current.equals(value))
			return;
		
		isDirty = true;
		if (value == null)
			base.map.remove(attributeName);
		else
			base.map.put(attributeName, value);
		firePropertyChangeEvent(attributeName, current, value);
	}

	public void setAttribute(String attributeName, List<String> value) {
		List current = base.getAttribute(attributeName, (List<String>)null);
		if (base.isAttributeSet(attributeName) && current != null && current.equals(value))
			return;
		
		isDirty = true;
		if (value == null)
			base.map.remove(attributeName);
		else
			base.map.put(attributeName, value);
		firePropertyChangeEvent(attributeName, current, value);
	}

	public void setAttribute(String attributeName, Map value) {
		Map current = base.getAttribute(attributeName, (Map)null);
		if (base.isAttributeSet(attributeName) && current != null && current.equals(value))
			return;
		
		isDirty = true;
		if (value == null)
			base.map.remove(attributeName);
		else
			base.map.put(attributeName, value);
		firePropertyChangeEvent(attributeName, current, value);
	}

	public void setName(String name) {
		setAttribute(Base.PROP_NAME, name);
	}

	public void setLocked(boolean b) {
		setAttribute(Base.PROP_LOCKED, b);
	}

	public void setPrivate(boolean b) {
		setAttribute(Base.PROP_PRIVATE, b);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerWorkingCopy#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
	}

	protected void validateTimestamp(int timestamp) throws CoreException {
		if (base.getTimestamp() != timestamp)
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, IServerWorkingCopy.SAVE_CONFLICT, Messages.errorWorkingCopyTimestamp, null));
	}

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners == null)
			propertyListeners = new ArrayList<PropertyChangeListener>(2);
		propertyListeners.add(listener);
	}

	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners != null)
			propertyListeners.remove(listener);
	}

	/**
	 * Fire a property change event.
	 * 
	 * @param propertyName a property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		if (propertyListeners == null)
			return;
	
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		try {
			Iterator iterator = propertyListeners.iterator();
			while (iterator.hasNext()) {
				try {
					PropertyChangeListener listener = (PropertyChangeListener) iterator.next();
					listener.propertyChange(event);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error firing property change event", e);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error in property event", e);
		}
	}

	protected void setDirty(boolean dirty) {
		isDirty = dirty;
	}
}