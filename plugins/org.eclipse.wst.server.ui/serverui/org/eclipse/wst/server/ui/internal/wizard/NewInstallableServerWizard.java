/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IInstallableServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.fragment.LicenseWizardFragment;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewInstallableServerWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard to create a new installable server.
 */
public class NewInstallableServerWizard extends TaskWizard {
	/**
	 * NewInstallableServerWizard constructor comment.
	 */
	public NewInstallableServerWizard() {
		super(Messages.wizNewServerWizardTitle, new WizardFragment() {
			protected void createChildFragments(List list) {
				list.add(new NewInstallableServerWizardFragment());
				list.add(new LicenseWizardFragment());
				list.add(new WizardFragment() {
					public void performFinish(IProgressMonitor monitor) throws CoreException {
						IInstallableServer is = (IInstallableServer) getTaskModel().getObject("installableServer");
						if (is != null)
							installServer(is);
					}
				});
			}
		});
	}

	public static void invalidateLicense(TaskModel taskModel) {
		IInstallableServer is = (IInstallableServer) taskModel.getObject("installableServer");
		IInstallableServer ls = (IInstallableServer) taskModel.getObject(LicenseWizardFragment.LICENSE_SERVER);
		if (is == ls)
			return;
		
		taskModel.putObject(LicenseWizardFragment.LICENSE, LicenseWizardFragment.LICENSE_UNKNOWN);
		taskModel.putObject(LicenseWizardFragment.LICENSE_ACCEPT, null);
		taskModel.putObject(LicenseWizardFragment.LICENSE_SERVER, null);
	}

	public static void updateLicense(IWizardHandle wizard, final TaskModel taskModel) {
		final IInstallableServer is = (IInstallableServer) taskModel.getObject("installableServer");
		IInstallableServer ls = (IInstallableServer) taskModel.getObject(LicenseWizardFragment.LICENSE_SERVER);
		if (is == ls)
			return;
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				String license = LicenseWizardFragment.LICENSE_UNKNOWN;
				try {
					license = is.getLicense(monitor);
					if (license == null)
						license = LicenseWizardFragment.LICENSE_NONE;
				} catch (CoreException ce) {
					// ignore
				}
				taskModel.putObject(LicenseWizardFragment.LICENSE, license);
				taskModel.putObject(LicenseWizardFragment.LICENSE_SERVER, is);
			}
		};
		
		try {
			wizard.run(true, false, runnable);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error with runnable", e); //$NON-NLS-1$
		}
	}

	/**
	 * Install a new server adapter.
	 * @param is
	 */
	protected static void installServer(final IInstallableServer is) {
		if (is == null)
			return;
		
		final boolean[] b = new boolean[1];
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				String msg = NLS.bind(Messages.wizNewInstallableServerConfirm, is.getName());
				b[0] = MessageDialog.openConfirm(display.getActiveShell(),
					Messages.defaultDialogTitle, msg);
			}
		});
		if (!b[0])
			return;
		
		String name = NLS.bind(Messages.wizNewInstallableServerJob, is.getName());
		Job job = new Job(name) {
			public IStatus run(IProgressMonitor monitor) {
				try {
					is.install(monitor);
					promptRestart();
					return Status.OK_STATUS;
				} catch (CoreException ce) {
					return ce.getStatus();
				}
			}
		};
		job.schedule();
	}

	/**
	 * Prompt the user to restart.
	 */
	public static void promptRestart() {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable() {
			public void run() {
				if (MessageDialog.openQuestion(display.getActiveShell(),
						Messages.defaultDialogTitle, Messages.wizNewInstallableServerRestart)) {
					Thread t = new Thread("Restart thread") {
						public void run() {
							try {
								sleep(1000);
							} catch (Exception e) {
								// ignore
							}
							display.asyncExec(new Runnable() {
								public void run() {
									PlatformUI.getWorkbench().restart();
								}
							});
						}
					};
					t.setDaemon(true);
					t.start();
				}
			}
		});
	}
}