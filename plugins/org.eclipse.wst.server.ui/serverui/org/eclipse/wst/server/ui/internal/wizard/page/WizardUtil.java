/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.osgi.util.NLS;
/**
 * A helper class for wizards.
 */
public class WizardUtil {
	/**
	 * Use static methods.
	 */
	private WizardUtil() {
		// do nothing
	}

	/**
	 * Return a new or existing server project.
	 * 
	 * @return the server project
	 */
	public static IProject getServerProject() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				if (ServerPlugin.getProjectProperties(projects[i]).isServerProject())
					return projects[i];
			}
		}
		
		String s = findUnusedServerProjectName();
		return ResourcesPlugin.getWorkspace().getRoot().getProject(s);
	}
	
	/**
	 * Finds an unused project name to use as a server project.
	 * 
	 * @return java.lang.String
	 */
	protected static String findUnusedServerProjectName() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String name = NLS.bind(Messages.defaultServerProjectName, "");
		int count = 1;
		while (root.getProject(name).exists()) {
			name = NLS.bind(Messages.defaultServerProjectName, ++count + "");
		}
		return name;
	}

	/**
	 * Return the container with the given name, if one exists.
	 *
	 * @param containerName java.lang.String
	 * @return org.eclipse.core.resources.IContainer
	 */
	public static IContainer findContainer(String containerName) {
		if (containerName == null || containerName.equals(""))
			return null;
	
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			IProject project = root.getProject(containerName);
			if (project != null && project.exists())
				return project;
		} catch (Exception e) {
			// ignore
		}
	
		try {
			IFolder folder = root.getFolder(new Path(containerName));
			if (folder != null && folder.exists())
				return folder;
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * Tries to find a server project folder in the heirarchy
	 * of the given resource. If it finds one, it returns the
	 * folder that the resource is or is in.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return org.eclipse.core.resources.IContainer
	 */
	protected static IContainer findServerProjectContainer(IResource resource) {
		IContainer container = null;
		while (resource != null) {
			if (container == null && resource instanceof IContainer)
				container = (IContainer) resource;
	
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (ServerUIPlugin.findServer(file) != null)
				return null;
			}
	
			if (resource instanceof IProject) {
				if (resource.getProject().isOpen())
					return container;
			}
			resource = resource.getParent();
		}
		return null;
	}

	/**
	 * Handles default selection within a wizard by going to the next
	 * page, or finishing the wizard if possible.
	 * 
	 * @param wizard a wizard
	 * @param page a wizard page
	 */
	public static void defaultSelect(IWizard wizard, IWizardPage page) {
		if (page.canFlipToNextPage() && page.getNextPage() != null)
			wizard.getContainer().showPage(page.getNextPage());
		else if (wizard.canFinish() && wizard.getContainer() instanceof ClosableWizardDialog) {
			ClosableWizardDialog dialog = (ClosableWizardDialog) wizard.getContainer();
			dialog.finishPressed();
		}
	}
}