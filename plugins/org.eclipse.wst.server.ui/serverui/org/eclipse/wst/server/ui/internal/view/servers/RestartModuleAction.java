/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IProjectModule;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
/**
 * Restart a module on a server.
 */
public class RestartModuleAction extends Action {
	protected IServer server;
	protected IModule module;

	public RestartModuleAction(IServer server, IModule module) {
		super();
		this.server = server;
		this.module = module;
	
		setText(module.getName());
		
		if (module instanceof IProjectModule) {
			IProjectModule project = (IProjectModule) module;
			ImageDescriptor descriptor = EclipseUtil.getProjectImageDescriptor(project.getProject());
			if (descriptor != null)
				setImageDescriptor(descriptor);
		}
	
		// enable or disable
		if (server == null || module == null) {
			setEnabled(false);
			return;
		}

		/*if (!(dr.getServerState() == IServer2.STATE_STARTED ||
			dr.getServerState() == IServer2.STATE_STARTED_DEBUG) ||
			!dr.canRestartModule(module)) {
			setEnabled(false);
			return;
		}*/
	
		setEnabled(server.canRestartModule(module));
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		try {
			server.restartModule(module, new NullProgressMonitor());
		} catch (CoreException e) {
		}
	}
}