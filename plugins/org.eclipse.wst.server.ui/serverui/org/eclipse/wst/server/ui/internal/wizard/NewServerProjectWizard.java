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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.page.CreateServerProjectWizardPage;
/**
 * Wizard to create a new server project.
 */
public class NewServerProjectWizard extends AbstractWizard implements INewWizard, IExecutableExtension {
	protected CreateServerProjectWizardPage page;
	private IConfigurationElement configElement;

	/**
	 * NewServerProjectWizard constructor comment.
	 */
	public NewServerProjectWizard() {
		super();
		setHelpAvailable(false);
		setNeedsProgressMonitor(true);
		
		setWindowTitle(ServerUIPlugin.getResource("%wizNewServerProjectWizardTitle"));
	}
	
	/**
	 * Create a new default wizard page.
	 */
	public void addPages() {
		page = new CreateServerProjectWizardPage();
		addPage(page);
	}
	
	public boolean performFinish() {
		boolean finish = page.performFinish();
		if (finish)
			BasicNewProjectResourceWizard.updatePerspective(configElement);
		return finish;
	}
	
	/**
	 * This method is called by the implementation of the method
	 * <code>IConfigurationElement.createExecutableExtension</code>
	 * on a newly constructed extension, passing it its relevant configuration 
	 * information. Most executable extensions only make use of the first 
	 * two call arguments.
	 *
	 * @param config the configuration element used to trigger this execution. 
	 *		It can be queried by the executable extension for specific
	 *		configuration properties
	 * @param propertyName the name of an attribute of the configuration element
	 *		used on the <code>createExecutableExtension(String)</code> call. This
	 *		argument can be used in the cases where a single configuration element
	 *		is used to define multiple executable extensions.
	 * @param data adapter data in the form of a <code>String</code>, 
	 *		a <code>Hashtable</code>, or <code>null</code>.
	 * @exception CoreException if error(s) detected during initialization processing
	 * @see IConfigurationElement#createExecutableExtension
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) {
		configElement = config;
	}
}
