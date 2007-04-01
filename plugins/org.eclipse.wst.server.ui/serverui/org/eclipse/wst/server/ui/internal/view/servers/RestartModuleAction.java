/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
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
		
		int size = module.length;
		setText(NLS.bind(Messages.actionRestartModule, module[size - 1].getName()));
		
		setEnabled(server.getServerState() == IServer.STATE_STARTED
			&& server.getModuleState(module) != IServer.STATE_STOPPED
			&& server.canControlModule(module, null).isOK());
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		int size = module.length;
		Job restartJob = new Job(NLS.bind(Messages.actionRestartModule, module[size-1].getName())) {
			protected IStatus run(IProgressMonitor monitor) {
				server.restartModule(module, null);
				return Status.OK_STATUS;
			}
		};
		restartJob.schedule();
	}
}