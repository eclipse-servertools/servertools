/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * "Open" menu action.
 */
public class OpenAction extends AbstractServerAction {
	/**
	 * OpenAction constructor.
	 * 
	 * @param sp a selection provider
	 */
	public OpenAction(ISelectionProvider sp) {
		super(sp, Messages.actionOpen);
		
		//setActionDefinitionId("org.eclipse.ui.navigator.Open");
		setActionDefinitionId("org.eclipse.jdt.ui.edit.text.java.open.editor");
	}

	public void perform(IServer server) {
		try {
			ServerUIPlugin.editServer(server);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error editing element", e);
			}
		}
	}
}