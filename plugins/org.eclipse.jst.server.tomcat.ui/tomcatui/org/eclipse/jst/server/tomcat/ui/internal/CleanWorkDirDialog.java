/*******************************************************************************
 * Copyright (c) 2007, 2008 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatWebModule;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.IServer.IOperationListener;

/**
 * Dialog to confirm deletion of the work directory for a module on a
 * server, or the work directory for the entire server.  Handling
 * includes stopping and restarting the server if it is running at
 * the time of the deletion. 
 *
 */
public class CleanWorkDirDialog extends Dialog {
	protected IServer server;
	protected IModule module;
	protected int state;
	protected String mode;
	protected IStatus completionStatus;
	
	/**
	 * Creates a dialog instance confirm deletion of the work directory for a
	 * module on a server, or the work directory for the entire server.
	 * 
	 * @param parentShell the parent shell, or <code>null</code> to create a
	 *  top-level shell
	 * @param server server on which to delete the work directory
	 * @param module module whose work directory is to be deleted, or <code>null</code> if
	 *  if these server's entire work directory is to be deleted.
	 */
	public CleanWorkDirDialog(Shell parentShell, IServer server, IModule module) {
		super(parentShell);
		
		if (server == null)
			throw new IllegalArgumentException();

		this.server = server;
		this.module = module;
		
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.confirmCleanWorkDirTitle);
	}

	protected Control createDialogArea(Composite parent) {
		if (state < 0 || state == IServer.STATE_UNKNOWN) 
			captureServerState();

		// create a composite with standard margins and spacing
		Composite composite = (Composite)super.createDialogArea(parent);
		// Since there are only label widgets on this page, set the help on the parent
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, ContextIds.SERVER_CLEAN_WORK_DIR);

		Label label = new Label(composite, SWT.WRAP);
		if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING || state == IServer.STATE_UNKNOWN) {
			label.setText(NLS.bind(Messages.cleanServerStateChanging, server.getName()));
		}
		else {
			if (module != null)
				label.setText(NLS.bind(Messages.cleanModuleWorkDir, module.getName(), server.getName()));
			else
				label.setText(NLS.bind(Messages.cleanServerWorkDir, server.getName()));
			GridData data = new GridData();
			data.widthHint = 300;
			label.setLayoutData(data);
			
			if (state == IServer.STATE_STARTED) {
				label = new Label(composite, SWT.WRAP);
				label.setText(Messages.cleanServerRunning);
				data = new GridData();
				data.widthHint = 300;
				label.setLayoutData(data);
			}
		}
		
		applyDialogFont(composite);
		return composite;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		if (state < 0 || state == IServer.STATE_UNKNOWN) 
			captureServerState();

		// If server is transitioning, only allow Cancel
		if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING) {
			Button button = getButton(IDialogConstants.OK_ID);
			if (button != null)
				button.setEnabled(false);
		}
	}

	protected void okPressed() {
		String jobName = NLS.bind(Messages.cleanServerTask,
				module != null ? module.getName() : server.getName());
		// Create job to perform the cleaning, including stopping and starting the server if necessary
		CleanWorkDirJob job = new CleanWorkDirJob(jobName);
		// Note: Since stop and start, if needed, will set scheduling rules in their jobs,
		// don't set one here. Instead do the actual deletion in a child job too with the
		// scheduling rule on that job, like stop and start.
		job.schedule();
		
		super.okPressed();
	}
	
	/*
	 * Job to clean the appropriate Tomcat work directory.  It includes
	 * stopping and starting the server if the server is currently running.
	 * The stopping, deletion, and starting are all done with child jobs,
	 * each using the server scheduling rule.  Thus, this job should
	 * not use this rule or it will block these child jobs. 
	 */
	class CleanWorkDirJob extends Job {
		/**
		 * @param name name for job
		 */
		public CleanWorkDirJob(String jobName) {
			super(jobName);
		}

		/**
		 * @see Job#belongsTo(Object)
		 */
		public boolean belongsTo(Object family) {
			return ServerUtil.SERVER_JOB_FAMILY.equals(family);
		}

		protected IStatus run(IProgressMonitor monitor) {
			final Object mutex = new Object();

			IWebModule webModule = null;
			if (module != null) {
				webModule = (IWebModule)module.loadAdapter(IWebModule.class, null);
				if (webModule == null) {
					return newErrorStatus(NLS.bind(Messages.errorCantIdentifyWebApp, module.getName()), null);
				}
			}
			
			// If state has changed since dialog was open, abort
			if (server.getServerState() != state) {
				return newErrorStatus(
						NLS.bind(Messages.errorCouldNotCleanStateChange, server.getName()), null);
			}

			IOperationListener listener = new IOperationListener() {
				public void done(IStatus result) {
					synchronized (mutex) {
						completionStatus = result;
						mutex.notifyAll();
					}
				}
			};
			
			boolean restart = false;
			IStatus status = Status.OK_STATUS;
			// If server isn't stopped, try to stop, clean, and restart
			if (state != IServer.STATE_STOPPED) {
				status = server.canStop();
				if (!status.isOK()) {
					return wrapErrorStatus(status, 
							NLS.bind(Messages.errorCouldNotCleanCantStop, server.getName()));
				}

				boolean done = false;
				boolean force = false;
				while (!done) {
					// Stop the server and wait for completion
					synchronized (mutex) {
						server.stop(force, listener);

						while (completionStatus == null) {
							try {
								mutex.wait();
							} catch (InterruptedException e) {
								// Ignore
							}
						}
					}
					// If forced, or there was an error (doesn't include timeout), or we are stopped, time to exit
					if (force || !completionStatus.isOK() || server.getServerState() == IServer.STATE_STOPPED) {
						done = true;
					}
					else {
						force = TomcatUIPlugin.queryCleanTermination(server);
						completionStatus = null;
					}
				}
			
				if (!completionStatus.isOK()) {
					// If stop job failed, assume error was displayed for that job
					return Status.OK_STATUS;
				}
				if (server.getServerState() != IServer.STATE_STOPPED) {
					return newErrorStatus(
							NLS.bind(Messages.errorCouldNotCleanStopFailed, server.getName()), null);
				}
				restart = true;
				completionStatus = null;
			}
			
			DeleteWorkDirJob deleteJob = new DeleteWorkDirJob(getName(), webModule, restart);
			deleteJob.setRule(ServerUtil.getServerSchedulingRule(server));

			deleteJob.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					synchronized (mutex) {
						completionStatus = event.getResult();
						mutex.notifyAll();
					}

				}
			});

			// Perform the work directory deletion job
			synchronized (mutex) {
				deleteJob.schedule();

				while (completionStatus == null) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			}
			if (!completionStatus.isOK()) {
				// If delete job failed, assume error was displayed for that job
				return Status.OK_STATUS;
			}
			completionStatus = null;

			if (restart) {
				status = server.canStart(mode);
				if (!status.isOK()) {
					return wrapErrorStatus(status, 
							NLS.bind(Messages.errorCleanCantRestart, server.getName()));
				}

				// Restart the server and wait for completion
				synchronized (mutex) {
					server.start(mode, listener);

					while (completionStatus == null) {
						try {
							mutex.wait();
						} catch (InterruptedException e) {
							// Ignore
						}
					}
				}
				
				if (!completionStatus.isOK()) {
					// If start job failed, assume error was displayed for that job
					return Status.OK_STATUS;
				}
			}
			return status;
		}
	}
	
	/*
	 * Job to actually delete the work directory.  This is done
	 * in a separate job so it can be a "sibling" of potential
	 * stop and start jobs. This allows it to have a server
	 * scheduling rule.
	 */
	class DeleteWorkDirJob extends Job {
		private IWebModule webModule;
		private boolean restart;
		
		/**
		 * @param name name for job
		 */
		public DeleteWorkDirJob(String jobName, IWebModule webModule, boolean restart) {
			super(jobName);
			this.webModule = webModule;
			this.restart = restart;
		}

		/**
		 * @see Job#belongsTo(Object)
		 */
		public boolean belongsTo(Object family) {
			return ServerUtil.SERVER_JOB_FAMILY.equals(family);
		}

		protected IStatus run(IProgressMonitor monitor) {
			
			IStatus status = Status.OK_STATUS;
			// If server isn't stopped, abort the attempt to delete the work directory
			if (server.getServerState() != IServer.STATE_STOPPED) {
				return newErrorStatus(
						NLS.bind(Messages.errorCantDeleteServerNotStopped, 
								webModule != null ? module.getName() : server.getName()), null);
			}				

			// Delete the work directory
			TomcatServerBehaviour tsb = (TomcatServerBehaviour)server.loadAdapter(
					TomcatServerBehaviour.class, monitor);
			try {
				if (webModule != null) {
					ITomcatWebModule tcWebModule = new WebModule(webModule.getContextRoot(), "", "", true);
					status = tsb.cleanContextWorkDir(tcWebModule, null);
				}
				else {
					status = tsb.cleanServerWorkDir(null);
				}
			} catch (CoreException ce) {
				status = ce.getStatus();
			}
			if (!status.isOK()) {
				String cleanName = module != null ? module.getName() : server.getName();
				return wrapErrorStatus(status,
						restart ?
								NLS.bind(Messages.errorErrorDuringCleanWasRunning, 
										cleanName , server.getName()) :
								NLS.bind(Messages.errorErrorDuringClean, cleanName));
			}
			return status;
		}
	}
	
	private void captureServerState() {
		state = server.getServerState();
		if (state != IServer.STATE_STOPPED) {
			mode = server.getMode();
		}
	}

	protected IStatus newErrorStatus(String message, Throwable throwable) {
		return new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID, 0,
				message, throwable);
	}
	
	protected IStatus wrapErrorStatus(IStatus status, String message) {
		MultiStatus ms = new MultiStatus(TomcatUIPlugin.PLUGIN_ID, 0, message, null);
		ms.add(status);
		return ms;
	}
}