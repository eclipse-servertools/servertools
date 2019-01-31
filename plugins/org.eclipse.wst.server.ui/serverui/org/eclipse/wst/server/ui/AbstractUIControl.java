/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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
package org.eclipse.wst.server.ui;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract UI control that allows adopter to control the UI
 */
public abstract class AbstractUIControl {
	// Control map describing the UI that needs to be controlled. 
	public Map<String, UIControlEntry> controlMap = new HashMap<String, UIControlEntry>();
	// UI Control listener that listens to changes in event on GUI provided by this UI control
	public IUIControlListener controlListener;
	
	// The list of properties supported by the existing UI control.
	public static final String PROP_HOSTNAME = "PROP_HOSTNAME";
	public static final String PROP_SERVER_TYPE = "PROP_SERVERTYPE";

	/**
	 * UI Control listener that listens to changes in event on GUI provided by this UI control
	 * IUIControlListener
	 */
	public interface IUIControlListener {
		/**
		 * Handle the UI control map change by the listener based on the given control map.
		 * @param controlMap the control map that describes the UI control.
		 */
		public void handleUIControlMapChanged(Map<String, UIControlEntry> controlMap);
		/**
		 * Get the value of a given control UI on the listener's page.
		 * @param controlId control ID of the UI on the listener's page.
		 * @return the String value of the given control ID field.
		 */
		public String getControlStringValue(String controlId);
	}
	
	/**
	 * UI control entry class that describes the behaviour of the UI control of specific fields or widget.
	 */
	public class UIControlEntry {
		private boolean isEnabled;
		private String newTextValue;
		
		public UIControlEntry(boolean curIsEnabled, String curNewTextValue) {
			isEnabled = curIsEnabled;
			newTextValue = curNewTextValue;
		}

		/**
		 * Gives the new text value of the given field.
		 * @return the new text value of the given field. If the value returns <code>null</code>, then it means
		 * the field do not need to be modified.
		 */
		public String getNewTextValue() {
			return newTextValue;
		}

		/**
		 * Return if the UI control needs to be enabled.
		 * @return true if value needs to be enabled; otherwise, return false.
		 */
		public boolean isEnabled() {
			return isEnabled;
		}
	}

	/**
	 * Fire the control change event for the changed control map to take effect.
	 */
	protected void fireUIControlChangedEvent() {
		if (controlListener != null) {
			controlListener.handleUIControlMapChanged(controlMap);
		}
	}
	
	/**
	 * Allow UI Control to react based on a property change and change the UI control.
	 * @param event property change event that describes the change.
	 */
	public abstract void handlePropertyChanged(PropertyChangeEvent event);
	
	/**
	 * Set the UI control listener that listens to the changes initialed by this UI control. 
	 * @param curControlListener the UI control listener
	 */
	public void setUIControlListener(IUIControlListener curControlListener) {
		controlListener = curControlListener;
	}
}
