/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.IWorkbenchAdapter;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Eclipse utility methods.
 */
public class EclipseUtil {
	/**
	 * EclipseUtil constructor comment.
	 */
	private EclipseUtil() {
		super();
	}
	
	/**
	 * Creates a new server project with the given name. If path is
	 * null, it will be created in the default location.
	 *
	 * @param name java.lang.String
	 * @param path org.eclipse.core.resource.IPath
	 * @param monitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	private static IStatus createServerProject(String name, IPath path, IProgressMonitor monitor) {
		//monitor = ProgressUtil.getMonitorFor(monitor);
		//monitor.beginTask(ServerPlugin.getResource("%createServerProjectTask"), 3000);

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(name);
	
			// get a project descriptor
			IProjectDescription description = workspace.newProjectDescription(name);
			description.setLocation(path);
	
			project.create(description, ProgressUtil.getSubMonitorFor(monitor, 1000));
			if (monitor.isCanceled())
				return null;
			project.open(ProgressUtil.getSubMonitorFor(monitor, 1000));
			if (monitor.isCanceled())
				return null;

			// add the server project nature
			ServerCore.getProjectProperties(project).setServerProject(true, monitor);
	
			if (monitor.isCanceled())
				return null;
	
			return new Status(IStatus.OK, ServerUIPlugin.PLUGIN_ID, 0, ServerUIPlugin.getResource("%serverProjectCreated"), null);
		} catch (CoreException ce) {
			Trace.trace(Trace.SEVERE, "Could not create server project named " + name, ce);
			return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, 0, ServerUIPlugin.getResource("%errorCouldNotCreateServerProjectStatus", ce.getMessage()), ce);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not create server project (2) named " + name, e);
			return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, 0, ServerUIPlugin.getResource("%errorCouldNotCreateServerProject"), e);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Creates a new server project with the given name. If path is
	 * null, it will be created in the default location.
	 *
	 * @param name java.lang.String
	 * @param path org.eclipse.core.resource.IPath
	 * @return org.eclipse.core.resources.IProject
	 */
	public static IStatus createNewServerProject(final Shell shell, String name, IPath path, IProgressMonitor monitor) {
		final IStatus status = createServerProject(name, path, monitor);
		if (!status.isOK()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Shell shell2 = shell;
					if (shell == null)
						shell2 = getShell();
					openError(shell2, ServerUIPlugin.getResource("%errorCouldNotCreateServerProject"), status);
				}
			});
		}
		return status;
	}

	/**
	 * Returns the image for a project.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return org.eclipse.jface.resource.ImageDescriptor
	 */
	public static ImageDescriptor getProjectImageDescriptor(IProject project) {
		if (project == null)
			return null;
	
		IWorkbenchAdapter adapter = (IWorkbenchAdapter) project.getAdapter(IWorkbenchAdapter.class);
	
		if (adapter != null)
			return adapter.getImageDescriptor(project);
		
		return null;
	}

	/**
	 * Return a shell for the workbench.
	 *
	 * @return org.eclipse.swt.widgets.Shell
	 */
	public static Shell getShell() {
		return getStandardDisplay().getActiveShell();
	}
	
	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();

		return display;		
	}	
	
	/**
	 * Open a dialog window.
	 *
	 * @param title java.lang.String
	 * @param message java.lang.String
	 */
	public static void openError(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell shell = getShell();
				MessageDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message);
			}
		});
	}

	/**
	 * Open a dialog window.
	 *
	 * @param message java.lang.String
	 * @param status IStatus
	 */
	public static void openError(final String message, final IStatus status) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell shell = getShell();
				ErrorDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message, status);
			}
		});
	}

	/**
	 * Open a dialog window.
	 *
	 * @param title java.lang.String
	 * @param message java.lang.String
	 */
	public static void openError(Shell shell, String message) {
		MessageDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message);
	}

	/**
	 * Open a dialog window.
	 *
	 * @param message java.lang.String
	 * @param status IStatus
	 */
	public static void openError(Shell shell, String message, IStatus status) {
		ErrorDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message, status);
	}
	
	/**
	 * Start a server and display a dialog if it fails.
	 */
	/*public static ILaunch startServer(final Shell shell, final IServer server, final String mode) throws CoreException {
			//, final ServerStartupListener listener) throws CoreException {
		// Eclipse v2 workaround to make sure that the debug UI listeners are setup
		DebugUIPlugin.getDefault();

		try {
			return server.start(mode, new NullProgressMonitor());
		} catch (final CoreException e) {
			Trace.trace(Trace.SEVERE, "Error starting server", e);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					EclipseUtil.openError(shell, ServerUIPlugin.getResource("%serverStartError"), e.getStatus());
					listener.setEnabled(false);
				}
			});
			throw e;
		}
	}*/

	/**
	 * Do a validateEdit() on the given server.
	 */
	public static boolean validateEdit(Shell shell, IServer server) {
		IStatus status = server.validateEdit(shell);
		return validateEdit(shell, status);
	}

	protected static boolean validateEdit(Shell shell, IStatus status) {
		if (status != null && status.getSeverity() == IStatus.ERROR) {
			// inform user
			String message = ServerUIPlugin.getResource("%editorValidateEditFailureMessage");
			ErrorDialog.openError(shell, ServerUIPlugin.getResource("%errorDialogTitle"), message, status);

			// do not execute command
			return false;
		}
		return true;
	}
}