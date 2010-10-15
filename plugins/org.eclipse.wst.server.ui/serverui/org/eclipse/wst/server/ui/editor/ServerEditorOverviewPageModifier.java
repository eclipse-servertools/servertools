/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.AbstractUIControl;

/**
 * A modifier class that allows adopter to modify the behaviour of a UI control on the
 * server editor page(s).
 * 
 * ServerEditorOverviewPageModifier
 */
public abstract class ServerEditorOverviewPageModifier extends AbstractUIControl {
	protected IServerWorkingCopy serverWc = null;

	
	/**
	 * The list of editor sections on the server editors that allow inserting custom GUI.
	 * UI_POSITION
	 */
	public enum UI_LOCATION { OVERVIEW, OTHER }


	/**
	 * Create the customer UI on the specified server editor part, either general or other.
	 * @param position the position on the server creation wizard page that allows inserting custom GUI.
	 * @param parent parent composite.
	 * @param location
	 */
	public abstract void createControl(UI_LOCATION location, Composite parent);
	
	
	/**
	* Set the server working copy to the control to allow extension to store the extension values.
	* @param curServerWc
	*/
	public void setServerWorkingCopy(IServerWorkingCopy curServerWc) {
		serverWc = curServerWc;
	}
}