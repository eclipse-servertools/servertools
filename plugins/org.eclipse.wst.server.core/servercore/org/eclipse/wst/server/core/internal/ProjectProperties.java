/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IProjectPropertiesListener;
/**
 * Helper class that stores preference information for
 * the server tools.
 */
public class ProjectProperties implements IProjectProperties {
	private static final String PROJECT_PREFERENCE_FILE = ".runtime";
	
	protected IProject project;
	
	protected String serverId;
	protected String runtimeId;
	
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
		
		try {
			IMemento memento = null;
			if (!project.exists() || !project.isOpen())
				return;
			IFile file = project.getFile(PROJECT_PREFERENCE_FILE);
			if (file != null && file.exists())
				memento = XMLMemento.loadMemento(file.getContents());
			
			if (memento == null)
				return;
			
			serverId = memento.getString("server-id");
			runtimeId = memento.getString("runtime-id");
		} catch (Exception e) {
			Trace.trace("Could not load preferences: " + e.getMessage());
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
				in = memento.getInputStream();
				
				if (file.exists())
					file.setContents(in, true, true, monitor);
				else
					file.create(in, true, monitor);
			} catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, "", e));
			} finally {
				try {
					in.close();
				} catch (Exception e) { }
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
	 * @return server org.eclipse.wst.server.core.model.IServer
	 */
	public IServer getDefaultServer() {
		loadPreferences();

		IServer server = ServerCore.getResourceManager().getServer(serverId);
		/*if (server != null && ServerUtil.containsModule(server, module))
			return server;
		else
			return null;*/
		return server;
	}

	/**
	 * Sets the preferred runtime server for the project.
	 *
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public void setDefaultServer(IServer server, IProgressMonitor monitor) throws CoreException {
		loadPreferences();
		if (server == null)
			serverId = null;
		else
			serverId = server.getId();
		savePreferences(monitor);
		fireDefaultServerChanged(server);
	}

	/**
	 * Returns the current runtime target type for the given project.
	 * 
	 * @return
	 */
	public IRuntime getRuntimeTarget() {
		loadPreferences();
		return ServerCore.getResourceManager().getRuntime(runtimeId);
	}

	/**
	 * Sets the runtime target for the project.
	 * 
	 * @param target
	 * @param monitor
	 */
	public void setRuntimeTarget(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		if (runtime != null)
			Trace.trace(Trace.RUNTIME_TARGET, "setRuntimeTarget : " + runtime + " " + runtime.getRuntimeType());
		else
			Trace.trace(Trace.RUNTIME_TARGET, "setRuntimeTarget : null");
		// remove old target
		loadPreferences();
		IRuntime oldRuntime = ServerCore.getResourceManager().getRuntime(runtimeId);
		if (oldRuntime != null) {
			IRuntimeType runtimeType = oldRuntime.getRuntimeType();
			Iterator iterator = ServerCore.getRuntimeTargetHandlers().iterator();
			while (iterator.hasNext()) {
				IRuntimeTargetHandler handler = (IRuntimeTargetHandler) iterator.next();
				Trace.trace(Trace.RUNTIME_TARGET, "  < " + handler + " " + handler.supportsRuntimeType(runtimeType));
				if (handler.supportsRuntimeType(runtimeType))
					handler.removeRuntimeTarget(project, oldRuntime, monitor);
			}
		}
		
		// add new target
		if (runtime != null) {
			runtimeId = runtime.getId();
			IRuntimeType runtimeType = runtime.getRuntimeType();
			Iterator iterator = ServerCore.getRuntimeTargetHandlers().iterator();
			while (iterator.hasNext()) {
				IRuntimeTargetHandler handler = (IRuntimeTargetHandler) iterator.next();
				Trace.trace(Trace.RUNTIME_TARGET, "  > " + handler + " " + handler.supportsRuntimeType(runtimeType));
				if (handler.supportsRuntimeType(runtimeType))
					handler.setRuntimeTarget(project, runtime, monitor);
			}
		} else
			runtimeId = null;
		savePreferences(monitor);
		fireRuntimeTargetChanged(runtime);
		Trace.trace(Trace.RUNTIME_TARGET, "setRuntimeTarget <");
	}

	public void addProjectPropertiesListener(IProjectPropertiesListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding project properties listener " + listener + " to " + this);
		
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(listener);
	}

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
	
	public String toString() {
		return "ProjectProperties[" + project + ", " + serverId + ", " + runtimeId + "]";
	}
}
