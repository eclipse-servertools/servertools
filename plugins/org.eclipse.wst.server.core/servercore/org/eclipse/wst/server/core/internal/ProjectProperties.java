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
package org.eclipse.wst.server.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IProjectProperties;
import org.eclipse.wst.server.core.IRuntime;
/**
 * Helper class that stores preference information for the server tools.
 * 
 * TODO: Currently this class always reads from disk. It should cache the file
 * and have a resource listener.
 */
public class ProjectProperties implements IProjectProperties {
	private static final String PREFERENCE_FOLDER = ".settings";
	private static final String PREFERENCE_FILE = "org.eclipse.wst.server.core.prefs";
	private static final String SERVER_PROJECT_PREF = "org.eclipse.wst.server.core.isServerProject";

	protected IProject project;

	protected boolean serverProject = false;

	/**
	 * ProjectProperties constructor.
	 * 
	 * @param project a project
	 */
	public ProjectProperties(IProject project) {
		super();
		this.project = project;
	}

	/**
	 * Load the preferences.
	 */
	private void loadPreferences() {
		Trace.trace(Trace.FINEST, "Loading project preferences: " + project);
		
		if (!project.isAccessible())
			return;
		
		InputStream in = null;
		try {
			IFile file = project.getFolder(PREFERENCE_FOLDER).getFile(PREFERENCE_FILE);
			if (file == null || !file.isAccessible())
				return;
			
			in = file.getContents();
			Properties p = new Properties();
			p.load(in);
			
			String s = p.getProperty(SERVER_PROJECT_PREF);
			if (s != null && "true".equals(s))
				serverProject = true;
			else
				serverProject = false;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load preferences: " + e.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	private void savePreferences(IProgressMonitor monitor) throws CoreException {
		if (project == null || !project.isAccessible())
			return;
		
		IFolder folder = project.getFolder(PREFERENCE_FOLDER);
		if (!folder.exists())
			folder.create(true, true, monitor);
		
		IFile file = project.getFolder(PREFERENCE_FOLDER).getFile(PREFERENCE_FILE);
		
		if (file.exists() && file.isReadOnly()) {
			IStatus status = ResourcesPlugin.getWorkspace().validateEdit(new IFile[] { file }, null);
			if (status.getSeverity() == IStatus.ERROR)
				// didn't work or not under source control
				throw new CoreException(status);
		}
		
		Properties p = new Properties();
		p.put("eclipse.preferences.version", "1");
		if (serverProject)
			p.put(SERVER_PROJECT_PREF, "true");
		else
			p.put(SERVER_PROJECT_PREF, "false");
		
		InputStream in = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			p.store(out, null);
		
			in = new ByteArrayInputStream(out.toByteArray());
			
			if (file.exists())
				file.setContents(in, true, true, monitor);
			else
				file.create(in, true, monitor);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "", e));
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/**
	 * Returns the current runtime target type for the given project.
	 * 
	 * @return the runtime target
	 */
	public IRuntime getRuntimeTarget() {
		return null;
	}

	/**
	 * Returns <code>true</code> if this project can contain server artifacts, and
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this project can contain server artifacts, and
	 *    <code>false</code> otherwise
	 */
	public boolean isServerProject() {
		loadPreferences();
		return serverProject;
	}

	/**
	 * Sets whether the project can contain server resources.
	 * 
	 * @param b <code>true</code> to allow the project to contain server
	 *    resources, or <code>false</code> to not allow the project to contain
	 *    servers
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is a problem setting the server project
	 */
	public void setServerProject(boolean b, IProgressMonitor monitor) throws CoreException {
		loadPreferences();
		serverProject = b;
		savePreferences(monitor);
	}

	public String toString() {
		return "ProjectProperties[" + project + "]";
	}
}