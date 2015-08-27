/*******************************************************************************
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.ui.ProvisioningOperationRunner;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.ui.AcceptLicensesWizardPage;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;
import org.eclipse.wst.server.discovery.internal.ImageResource;
import org.eclipse.wst.server.discovery.internal.Messages;
import org.eclipse.wst.server.discovery.internal.model.Extension;
import org.eclipse.wst.server.discovery.internal.wizard.ErrorWizardPage;
import org.eclipse.wst.server.discovery.internal.wizard.ExtensionWizardPage;

public class ExtensionWizard extends Wizard {
	protected ExtensionWizardPage extensionPage;
	protected AcceptLicensesWizardPage licensePage;
	protected ErrorWizardPage errorPage;
	protected IWizardPage nextPage;

	public ExtensionWizard() {
		super();
		setWindowTitle(Messages.wizExtensionTitle);
		setDefaultPageImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZARD));
		setNeedsProgressMonitor(true);
		setForcePreviousAndNextButtons(true);
	}

	public void addPages() {
		super.addPages();
		ProvisioningUI ui = ProvisioningUI.getDefaultUI();
		licensePage = new AcceptLicensesWizardPage(ui.getLicenseManager(), new IInstallableUnit[0], null);
		licensePage.setWizard(this);
		errorPage = new ErrorWizardPage();
		errorPage.setWizard(this);
		extensionPage = new ExtensionWizardPage(licensePage, errorPage);
		extensionPage.setWizard(this);
	}

	public int getPageCount() {
		if (nextPage != null)
			return 2;
		return 1;
	}

	public IWizardPage[] getPages() {
		if (nextPage != null)
			return new IWizardPage[] { extensionPage, nextPage };
		return new IWizardPage[] { extensionPage };
	}

	public boolean canFinish() {
		return licensePage.equals(nextPage) && licensePage.isPageComplete();
	}

	public IWizardPage getStartingPage() {
		return extensionPage;
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (extensionPage.equals(page))
			return nextPage;
		return null;
	}

	public IWizardPage getPreviousPage(IWizardPage page) {
		if (nextPage != null && nextPage.equals(page))
			return extensionPage;
		return null;
	}

	public void setSecondPage(IWizardPage page) {
		nextPage = page;
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				getContainer().updateButtons();
			}
		});
	}

	public boolean performFinish() {
		return install(extensionPage.getExtension());
	}

	/**
	 * Install a new feature.
	 * @param extension
	 */
	protected static boolean install(final Extension extension) {
		if (extension == null)
			return false;
		
		final boolean[] b = new boolean[1];
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				String msg = NLS.bind(Messages.installConfirm, extension.getName());
				b[0] = MessageDialog.openConfirm(display.getActiveShell(),
					Messages.dialogTitle, msg);
			}
		});
		if (!b[0])
			return true;
		
		return ExtensionUtility.installExtension(extension);
	}
}