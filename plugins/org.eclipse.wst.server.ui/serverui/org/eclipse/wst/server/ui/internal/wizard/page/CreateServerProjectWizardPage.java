package org.eclipse.wst.server.ui.internal.wizard.page;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
/**
 * Wizard page for creating a new server project.
 */
public class CreateServerProjectWizardPage extends WizardNewProjectCreationPage {
	/**
	 * WizardNewServerProjectPage constructor comment.
	 */
	public CreateServerProjectWizardPage() {
		super("create server project");
	
		setTitle(ServerUIPlugin.getResource("%wizNewServerProjectTitle"));
		setDescription(ServerUIPlugin.getResource("%wizNewServerProjectDescription"));
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER_PROJECT));
	}
	
	/** (non-Javadoc)
	 * Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		Control control = getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertHorizontalDLUsToPixels(475);
		control.setLayoutData(data);
	}

	/**
	 * Creates a project resource handle for the current project name field value.
	 * <p>
	 * This method does not create the project resource; this is the responsibility
	 * of <code>IProject::create</code> invoked by the new project resource wizard.
	 * </p>
	 *
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
	}
	
	/**
	 * Creates the server project and adds the nature.
	 * @return boolean
	 */
	public boolean performFinish() {
		final String name = getProjectName();
		IPath path2 = null;
		if (!useDefaults())
			path2 = getLocationPath();
		final IPath path = path2;
	
		if (name == null || name.length() == 0)
			return false;
	
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					IStatus status = EclipseUtil.createNewServerProject(getShell(), name, path, monitor);
					if (!status.isOK())
						monitor.setCanceled(true);
					if (monitor.isCanceled())
						throw new Exception("Creation canceled");
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			getWizard().getContainer().run(true, true, runnable);
			return true;
		} catch (Exception e) {
			Trace.trace("Could not create server project", e);
			return false;
		}
	}
}