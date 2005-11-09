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

import java.io.InputStream;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.wst.server.core.*;
/**
 * Helper class that stores preference information for the server tools.
 * 
 * TODO: Currently this class always reads from disk. It should cache the file
 * and have a resource listener.
 */
public class ProjectProperties implements IProjectProperties {
	private static final String PROJECT_PREFERENCE_FILE = ".runtime";

	protected IProject project;

	protected String runtimeId;
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
		
		InputStream in = null;
		try {
			IPath path = project.getWorkingLocation(ServerPlugin.PLUGIN_ID).append(PROJECT_PREFERENCE_FILE);
			
			IMemento memento = XMLMemento.loadMemento(path.toOSString());
			
			if (memento == null)
				return;
			
			runtimeId = memento.getString("runtime-id");
			String s = memento.getString("servers");
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
		if (project.exists() && project.isOpen()) {
			
			IPath path = project.getWorkingLocation(ServerPlugin.PLUGIN_ID).append(PROJECT_PREFERENCE_FILE);
			
			InputStream in = null;
			try {
				XMLMemento memento = XMLMemento.createWriteRoot("runtime");

				if (runtimeId != null)
					memento.putString("runtime-id", runtimeId);
				if (serverProject)
					memento.putString("servers", "true");
				else
					memento.putString("servers", "false");
				in = memento.getInputStream();
				
				memento.saveToFile(path.toOSString());
			} catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "", e));
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
			return;
		}
	}

	protected String getRuntimeTargetId() {
		loadPreferences();
		return runtimeId;
	}

	protected void setRuntimeTargetId(String newRuntimeId, IProgressMonitor monitor) throws CoreException {
		loadPreferences();
		runtimeId = newRuntimeId;
		savePreferences(monitor);
	}

	/**
	 * Returns the current runtime target type for the given project.
	 * 
	 * @return the runtime target
	 */
	public IRuntime getRuntimeTarget() {
		loadPreferences();
		if (runtimeId == null)
			return null;
		return ServerCore.findRuntime(runtimeId);
	}

	/**
	 * Sets the runtime target for the project.
	 * 
	 * @param runtime the target runtime
	 * @param monitor a progress monitor
	 * @throws CoreException if anything goes wrong
	 */
	protected void setRuntimeTarget(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		loadPreferences();
		IRuntime oldRuntime = null;
		if (runtimeId != null)
			oldRuntime = ServerCore.findRuntime(runtimeId);
		setRuntimeTarget(oldRuntime, runtime, true, monitor);
	}

	protected void setRuntimeTarget(IRuntime oldRuntime, IRuntime newRuntime, boolean save, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.RUNTIME_TARGET, "setRuntimeTarget : " + oldRuntime + " -> " + newRuntime);
		
		if (oldRuntime == null && newRuntime == null)
			return;
		if (oldRuntime != null && oldRuntime.equals(newRuntime))
			return;
		
		IRuntimeTargetHandler[] handlers = ServerCore.getRuntimeTargetHandlers();
		if (handlers == null)
			return;
	
		int size = handlers.length;
		// remove old target
		if (oldRuntime != null) {
			IRuntimeType runtimeType = oldRuntime.getRuntimeType();
			for (int i = 0; i < size; i++) {
				IRuntimeTargetHandler handler = handlers[i];
				long time = System.currentTimeMillis();
				boolean supports = handler.supportsRuntimeType(runtimeType);
				Trace.trace(Trace.RUNTIME_TARGET, "  < " + handler + " " + supports);
				if (supports)
					((RuntimeTargetHandler)handler).removeRuntimeTarget(project, oldRuntime, monitor);
				Trace.trace(Trace.PERFORMANCE, "Runtime target: <" + (System.currentTimeMillis() - time) + "> " + handler.getId());
			}
		}
		
		// add new target
		if (newRuntime != null) {
			runtimeId = newRuntime.getId();
			if (save)
				savePreferences(monitor);
			IRuntimeType runtimeType = newRuntime.getRuntimeType();
			for (int i = 0; i < size; i++) {
				IRuntimeTargetHandler handler = handlers[i];
				long time = System.currentTimeMillis();
				boolean supports = handler.supportsRuntimeType(runtimeType);
				Trace.trace(Trace.RUNTIME_TARGET, "  > " + handler + " " + supports);
				if (supports)
					((RuntimeTargetHandler)handler).setRuntimeTarget(project, newRuntime, monitor);
				Trace.trace(Trace.PERFORMANCE, "Runtime target: <" + (System.currentTimeMillis() - time) + "> " + handler.getId());
			}
		} else {
			runtimeId = null;
			if (save)
				savePreferences(monitor);
		}
		
		Trace.trace(Trace.RUNTIME_TARGET, "setRuntimeTarget <");
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
		return "ProjectProperties[" + project + ", " + runtimeId + "]";
	}
}