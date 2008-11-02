/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.ui.dialogs.AcceptLicensesWizardPage;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui.SimpleLicenseManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.discovery.internal.Messages;
import org.eclipse.wst.server.discovery.internal.model.IExtension;

public class ExtensionWizard extends Wizard {
	protected ExtensionWizardPage extensionPage;
	//protected LicenseWizardPage licensePage;
	protected AcceptLicensesWizardPage licensePage;

	public ExtensionWizard() {
		super();
		setWindowTitle(Messages.wizNewInstallableServerTitle);
		setDefaultPageImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER));
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		super.addPages();
		//licensePage = new LicenseWizardPage();
		licensePage = new AcceptLicensesWizardPage(new IInstallableUnit[0], new SimpleLicenseManager(), null);
		extensionPage = new ExtensionWizardPage(licensePage);
		addPage(extensionPage);
		addPage(licensePage);
	}

	public boolean performFinish() {
		install(extensionPage.getExtension());
		return true;
	}

	/**
	 * Install a new feature.
	 * @param extension
	 */
	protected static void install(final IExtension extension) {
		if (extension == null)
			return;
		
		final boolean[] b = new boolean[1];
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				String msg = NLS.bind(Messages.wizNewInstallableServerConfirm, extension.getName());
				b[0] = MessageDialog.openConfirm(display.getActiveShell(),
					Messages.defaultDialogTitle, msg);
			}
		});
		if (!b[0])
			return;
		
		String name = NLS.bind(Messages.wizNewInstallableServerJob, extension.getName());
		Job job = new Job(name) {
			public IStatus run(IProgressMonitor monitor) {
				IStatus status = extension.install(monitor);
				if (status.isOK() && !monitor.isCanceled())
					promptRestart();
				return status;
			}
		};
		job.setUser(true);
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