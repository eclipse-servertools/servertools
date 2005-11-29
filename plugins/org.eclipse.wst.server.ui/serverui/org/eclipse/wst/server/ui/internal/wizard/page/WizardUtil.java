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
package org.eclipse.wst.server.ui.internal.wizard.page;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
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
	 * Return the full pathname of a container.
	 *
	 * @param container org.eclipse.core.resources.Container
	 * @return java.lang.String
	 */
	public static String getContainerText(IContainer container) {
		String name = container.getName();
		while (container != null && !(container instanceof IProject)) {
			container = container.getParent();
			name = container.getName() + "/" + name;
		}
		return name;
	}

	/**
	 * Returns the selected container from this selection.
	 *
	 * @param selection the selection
	 * @return the container
	 */
	public static IContainer getSelectionContainer(IStructuredSelection selection) {
		if (selection == null || selection.isEmpty())
			return null;
	
		Object obj = selection.getFirstElement();
		if (obj instanceof IResource)
			return findServerProjectContainer((IResource) obj);
	
		return null;
	}

	/**
	 * Return true if the container is a valid server project
	 * folder and is not "within" a server instance or configuration.
	 *
	 * @param name a container name
	 * @return <code>null</code> if the container is fine, and an error message
	 *    otherwise 
	 */
	public static String validateContainer(String name) {
		IContainer container = WizardUtil.findContainer(name);
		if (container == null || !container.exists()) {
			IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.PROJECT);
			if (status.isOK())
				return null; // we can create one later
			return status.getMessage();
		}
		
		String error = Messages.wizErrorInvalidFolder;
		try {
			// find project of this container
			IProject project = null;
			if (container instanceof IProject) {
				project = (IProject) container;
			} else {
				// look up hierarchy for project
				IContainer temp = container.getParent();
				while (project == null && temp != null && !(temp instanceof IProject)) {
					temp = temp.getParent();
				}
				if (temp != null && temp instanceof IProject)
					project = (IProject) temp;
			}
	
			// validate the project
			if (project != null && !project.isOpen())
				return Messages.wizErrorClosedProject;

			if (project == null || !project.exists() || !project.isOpen())
				return error;
	
			// make sure we're not embedding in another server element
			IResource temp = container;
			while (temp != null && !(temp instanceof IProject)) {
				if (temp instanceof IFile) {
					IFile file = (IFile) temp;
					if (ServerUIPlugin.findServer(file) != null)
						return error;
				}
				temp = temp.getParent();
			}
		} catch (Exception e) {
			return error;
		}
		return null;
	}

	/**
	 * Returns true if the user said okay to creating a new server
	 * project.
	 * 
	 * @param shell a shell
	 * @param projectName a project name
	 * @return <code>true</code> if the user wants to create a server project
	 */
	public static boolean promptForServerProjectCreation(Shell shell, String projectName) {
		String msg = NLS.bind(Messages.createServerProjectDialogMessage, projectName);
		return MessageDialog.openQuestion(shell, Messages.createServerProjectDialogTitle, msg);
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