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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.wst.server.core.*;
/**
 * Helper class that stores preference information for the server tools.
 */
public class ProjectProperties implements IProjectProperties {
	private static final String PROJECT_PREFERENCE_FILE = ".runtime";
	
	protected IProject project;
	
	protected String serverId;
	protected String runtimeId;
	protected boolean serverProject = false;
	
	// project properties listeners
	protected transient List listeners;

	/**
	 * ProjectProperties constructor comment.
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
			IMemento memento = null;
			if (!project.exists() || !project.isOpen())
				return;
			IFile file = project.getFile(PROJECT_PREFERENCE_FILE);
			if (file != null && file.exists()) {
				in = file.getContents();
				memento = XMLMemento.loadMemento(in);
			}
			
			if (memento == null)
				return;
			
			serverId = memento.getString("server-id");
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
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	private void savePreferences(IProgressMonitor monitor) throws CoreException {
		if (project.exists() && project.isOpen()) {
			IFile file = project.getFile(PROJECT_PREFERENCE_FILE);
			
			if (file.exists() && file.isReadOnly()) {
				IStatus status = ResourcesPlugin.getWorkspace().validateEdit(new IFile[] { file }, null);
				if (status.getSeverity() == IStatus.ERROR)
					// didn't work or not under source control
					throw new CoreException(status);
			}
			
			InputStream in = null;
			try {
				XMLMemento memento = XMLMemento.createWriteRoot("runtime");

				if (runtimeId != null)
					memento.putString("runtime-id", runtimeId);
				if (serverId != null)
					memento.putString("server-id", serverId);
				if (serverProject)
					memento.putString("servers", "true");
				else
					memento.putString("servers", "false");
				in = memento.getInputStream();
				
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
			return;
		}
	}

	/**
	 * Returns the preferred runtime server for the project. This method
	 * returns null if the server was never chosen or does not currently exist. (if the
	 * server is recreated or was in a closed project, etc. this method will return
	 * the original value if it becomes available again)
	 *
	 * @return server org.eclipse.wst.server.core.IServer
	 */
	public IServer getDefaultServer() {
		loadPreferences();

		if (serverId == null || serverId.length() == 0)
			return null;
		
		IServer server = ServerCore.findServer(serverId);
		/*if (server != null && ServerUtil.containsModule(server, module))
			return server;
		else
			return null;*/
		return server;
	}

	/**
	 * Sets the preferred runtime server for the project.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void setDefaultServer(IServer server, IProgressMonitor monitor) throws CoreException {
		loadPreferences();
		
		String newServerId = null;
		if (server != null)
			newServerId = server.getId();
		if (serverId == null && newServerId == null)
			return;
		if (serverId != null && serverId.equals(newServerId))
			return;
		
		serverId = newServerId;
		savePreferences(monitor);
		fireDefaultServerChanged(server);
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
	 */
	public void setRuntimeTarget(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
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
		
		fireRuntimeTargetChanged(newRuntime);
		Trace.trace(Trace.RUNTIME_TARGET, "setRuntimeTarget <");
	}

	/**
	 * Adds a new project properties listener.
	 * Has no effect if an identical listener is already registered.
	 * 
	 * @param listener the properties listener
	 * @see #removeProjectPropertiesListener(IProjectPropertiesListener)
	 */
	public void addProjectPropertiesListener(IProjectPropertiesListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding project properties listener " + listener + " to " + this);
		
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(listener);
	}

	/**
	 * Removes an existing project properties listener.
	 * Has no effect if the listener is not registered.
	 * 
	 * @param listener the properties listener
	 * @see #addProjectPropertiesListener(IProjectPropertiesListener)
	 */
	public void removeProjectPropertiesListener(IProjectPropertiesListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing project properties listener " + listener + " from " + this);
		
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 * Fire a event because the default server changed.
	 *
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	private void fireDefaultServerChanged(IServer server) {
		Trace.trace(Trace.LISTENERS, "->- Firing defaultServerChanged event: " + server + " ->-");
	
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IProjectPropertiesListener[] ppl = new IProjectPropertiesListener[size];
		listeners.toArray(ppl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing defaultServerChanged event to " + ppl[i]);
			try {
				ppl[i].defaultServerChanged(project, server);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing defaultServerChanged event to " + ppl[i], e);
			}
		}
	
		Trace.trace(Trace.LISTENERS, "-<- Done firing defaultServerChanged event -<-");
	}
	
	/**
	 * Fire a event because the runtime target changed.
	 *
	 * @param server org.eclipse.wst.server.core.IRuntime
	 */
	private void fireRuntimeTargetChanged(IRuntime runtime) {
		Trace.trace(Trace.LISTENERS, "->- Firing runtimeTargetChanged event: " + runtime + " ->-");
	
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IProjectPropertiesListener[] ppl = new IProjectPropertiesListener[size];
		listeners.toArray(ppl);
	
		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.LISTENERS, "  Firing runtimeTargetChanged event to " + ppl[i]);
			try {
				ppl[i].runtimeTargetChanged(project, runtime);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing runtimeTargetChanged event to " + ppl[i], e);
			}
		}
	
		Trace.trace(Trace.LISTENERS, "-<- Done firing runtimeTargetChanged event -<-");
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
	 * @param sp <code>true</code> to allow the project to contain server
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
		return "ProjectProperties[" + project + ", " + serverId + ", " + runtimeId + "]";
	}
}