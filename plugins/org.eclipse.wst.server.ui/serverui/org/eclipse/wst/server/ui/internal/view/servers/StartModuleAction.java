/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * Start a module on a server.
 */
public class StartModuleAction extends Action {
	protected IServer server;
	protected IModule[] module;

	public StartModuleAction(IServer server, IModule[] module) {
		super();
		this.server = server;
		this.module = module;
		
		setText(Messages.actionStartModule);
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		setEnabled(server.getServerState() == IServer.STATE_STARTED
				&& (server.getModuleState(module) == IServer.STATE_STOPPED
					|| server.getModuleState(module) == IServer.STATE_UNKNOWN)
				&& server.canControlModule(module, null).isOK());
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		server.startModule(module, null);
	}
}