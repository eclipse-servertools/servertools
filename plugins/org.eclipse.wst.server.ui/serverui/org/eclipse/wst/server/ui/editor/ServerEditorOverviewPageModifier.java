/*******************************************************************************
 * Copyright (c) 2010, 2014 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.editor;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.AbstractUIControl;
import org.eclipse.wst.server.ui.internal.Trace;

/**
 * A modifier class that allows adopter to modify the behaviour of a UI control on the
 * server editor page(s).
 * 
 * ServerEditorOverviewPageModifier
 */
public abstract class ServerEditorOverviewPageModifier extends AbstractUIControl {
	protected IServerWorkingCopy serverWc = null;
	private FormToolkit serverEditorFormToolkit = null;
	private ServerEditorPart serverEditorPart = null;
	
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
	
	public void setServerEditorPart(ServerEditorPart part) {
		serverEditorPart = part;
	}

	public void setFormToolkit(FormToolkit toolkit) {
		this.serverEditorFormToolkit = toolkit;
	}

	protected FormToolkit getFormToolkit() {
		return serverEditorFormToolkit;
	}

	protected void executeCommand(IUndoableOperation operation) {
		if (serverEditorPart != null) {
			serverEditorPart.execute(operation);
		} else {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE,
						"Error executing command: No reference to editor part");
			}
		}
	}
}