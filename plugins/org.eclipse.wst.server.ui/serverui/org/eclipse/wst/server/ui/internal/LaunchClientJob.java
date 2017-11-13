/*******************************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.*;
/**
 * 
 */
public class LaunchClientJob extends ChainedJob {
	protected IModule[] module;
	protected IClient client;
	protected ILaunchableAdapter launchableAdapter;
	protected String launchMode;
	protected IModuleArtifact moduleArtifact;

	public LaunchClientJob(IServer server, IModule[] module, String launchMode, IModuleArtifact moduleArtifact, ILaunchableAdapter launchableAdapter, IClient client) {
		super(Messages.launchingClientTask, server);
		this.module = module;
		this.launchMode = launchMode;
		this.moduleArtifact = moduleArtifact;
		this.launchableAdapter = launchableAdapter;
		this.client = client;
		setRule(server);
	}

	/** (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus run(IProgressMonitor monitor) {
		if (Trace.FINER) {
			Trace.trace(Trace.STRING_FINER, "LaunchClient job");
		}

		// wait for up to 5 minutes
		final Server server = (Server) getServer();
		int state = server.getModuleState(module);
		int count = ServerPreferences.getInstance().getModuleStartTimeout();
		while (state == IServer.STATE_STARTING && count > 0) {
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				// ignore
			}
			count -= 2000;
			state = server.getModuleState(module);
		}
		
		if (Trace.FINER) {
			Trace.trace(Trace.STRING_FINER, "LaunchClient job 2 " + state);
		}
		
		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;
		
		if (state == IServer.STATE_STARTING)
			return Status.OK_STATUS;
		
		if (Trace.FINER) {
			Trace.trace(Trace.STRING_FINER, "LaunchClient job 3");
		}

		// job return status
		final IStatus[] resultingStatus = new IStatus[] { Status.OK_STATUS };

		// acquire the launchable object.
		final Object[] launchable = new Object[1];
		try {
			launchable[0] = launchableAdapter.getLaunchable(server, moduleArtifact);
		}
		catch (CoreException ce) {
			resultingStatus[0] = ce.getStatus();
			EclipseUtil.openError(null, resultingStatus[0]);
		}
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		// display client on UI thread if launchable exists.
		if (launchable[0] != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (Trace.FINEST) {
						Trace.trace(Trace.STRING_FINEST, "Attempting to load client: " + client.getId());
					}
					try {
						resultingStatus[0] = client.launch(server, launchable[0], launchMode, server.getLaunch());
						if (resultingStatus[0] != null && resultingStatus[0].getSeverity() == IStatus.ERROR) {
							EclipseUtil.openError(null, resultingStatus[0]);
						} else if (resultingStatus[0] == null) {
							resultingStatus[0] = Status.OK_STATUS;
						}
					}
					catch (Exception e) {
						if (Trace.SEVERE) {
							Trace.trace(Trace.STRING_SEVERE, "Server client failed", e);
						}
						if (resultingStatus[0] == null) {
							resultingStatus[0] = new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, e.getMessage(), e);
						}
					}
				}
			});
		}
		if (Trace.FINER) {
			Trace.trace(Trace.STRING_FINER, "LaunchClient job 4");
		}
		return resultingStatus[0];
	}
}