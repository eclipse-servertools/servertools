package org.eclipse.wst.server.ui.internal.wizard;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.page.ImportConfigurationWizardPage;
import org.eclipse.wst.server.ui.internal.wizard.page.WizardUtil;
import org.eclipse.ui.IImportWizard;
/**
 * A wizard to import existing server configurations.
 */
public class ImportServerConfigurationWizard extends AbstractWizard implements IImportWizard {
	protected ImportConfigurationWizardPage page;

	/**
	 * ImportServerConfiguration constructor comment.
	 */
	public ImportServerConfigurationWizard() {
		super();
		setWindowTitle(ServerUIPlugin.getResource("%wizImportConfigurationWizardTitle"));
	}
	
	public void addPages() {
		page = new ImportConfigurationWizardPage();
		page.setDefaultContainer(WizardUtil.getSelectionContainer(selection));
		addPage(page);
	}
	
	public boolean performFinish() {
		return page.performFinish();
	}
}
