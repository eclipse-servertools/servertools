/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Restart a module on a server.
 */
public class RestartModuleAction extends Action {
	protected IServer server;
	protected IModule[] module;

	public RestartModuleAction(IServer server, IModule[] module) {
		super();
		this.server = server;
		this.module = module;
		
		setText(Messages.actionRestartModule);
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_RESTART));
		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_RESTART));
		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_RESTART));
		
		setEnabled(server.getServerState() == IServer.STATE_STARTED
				&& server.getModuleState(module) != IServer.STATE_UNKNOWN
				&&	server.canRestartModule(module, null).isOK());
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		server.restartModule(module, null);
	}
}