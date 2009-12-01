/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.wizard;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.AbstractUIControl;

/**
 * A modifier class that allow adopter to modify the behaviour of the server creation wizard page.
 * It also has places where adopter can inject custom GUI into the server creation wizard page.
 */
public abstract class ServerCreationWizardPageExtension extends AbstractUIControl {
	protected IServerWorkingCopy serverWc = null;
	
	/**
	 * The list of position on the server creation wizard page that allows inserting custom GUI.
	 * UI_POSITION
	 */
	public enum UI_POSITION { TOP, MIDDLE, BOTTOM }

	/**
	 * Create the customer UI on the specified position.
	 * @param position the position on the server creation wizard page that allows inserting custom GUI.
	 * @param parent parent composite.
	 */
	public abstract void createControl(UI_POSITION position, Composite parent);
	
	/**
	 * Set the server working copy to the control to allow extension to store the extension values.
	 * @param curServerWc
	 */
	public void setServerWorkingCopy(IServerWorkingCopy curServerWc) {
		serverWc = curServerWc;
	}
}
