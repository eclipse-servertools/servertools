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

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * 
 */
public class ServerWorkingCopy extends Server implements IServerWorkingCopy {
	protected Server server;
	protected WorkingCopyHelper wch;
	
	protected IServerWorkingCopyDelegate workingCopyDelegate;
	
	// working copy
	public ServerWorkingCopy(Server server) {
		super(server.getFile());
		this.server = server;
		
		map = new HashMap(server.map);
		wch = new WorkingCopyHelper(this);
		resolve();
	}
	
	// creation
	public ServerWorkingCopy(String id, IFile file, IRuntime runtime, IServerType serverType) {
		super(id, file, runtime, serverType);
		//server = this;
		wch = new WorkingCopyHelper(this);
		wch.setDirty(true);
		serverState = ((ServerType)serverType).getInitialState();
	}

	public boolean isWorkingCopy() {
		return true;
	}
	
	public IServer getOriginal() {
		return server;
	}
	
	public IServerWorkingCopy getWorkingCopy() {
		return this;
	}
	
	public byte getServerState() {
		if (server != null)
			return server.getServerState();
		else
			return serverState;
	}
	
	public void setServerState(byte state) {
		if (server != null)
			server.setServerState(state);
		else
			super.setServerState(state);
	}

	public void setAttribute(String attributeName, int value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, boolean value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, String value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, List value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, Map value) {
		wch.setAttribute(attributeName, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerWorkingCopy#setName(java.lang.String)
	 */
	public void setName(String name) {
		setAttribute(PROP_NAME, name);
	}
	
	public void setLocked(boolean b) {
		setAttribute(PROP_LOCKED, b);
	}

	public void setPrivate(boolean b) {
		setAttribute(PROP_PRIVATE, b);
	}

	public void setHostname(String host) {
		setAttribute(PROP_HOSTNAME, host);
	}
	
	public void setServerConfiguration(IServerConfiguration config) {
		this.configuration = config;
		if (configuration == null)
			setAttribute(CONFIGURATION_ID, (String)null);
		else
			setAttribute(CONFIGURATION_ID, configuration.getId());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerWorkingCopy#isDirty()
	 */
	public boolean isDirty() {
		return wch.isDirty();
	}

	public void release() {
		wch.release();
		dispose();
		if (server != null)
			server.release(this);
	}
	
	public IServerDelegate getDelegate() {
		return getWorkingCopyDelegate();
	}
	
	public IServerWorkingCopyDelegate getWorkingCopyDelegate() {
		// make sure that the regular delegate is loaded 
		//getDelegate();
		
		if (workingCopyDelegate != null)
			return workingCopyDelegate;
		
		if (serverType != null) {
			synchronized (this) {
				if (workingCopyDelegate == null) {
					try {
						long time = System.currentTimeMillis();
						IConfigurationElement element = ((ServerType) serverType).getElement();
						workingCopyDelegate = (IServerWorkingCopyDelegate) element.createExecutableExtension("workingCopyClass");
						workingCopyDelegate.initialize((IServerState) this);
						workingCopyDelegate.initialize((IServerWorkingCopy) this);
						Trace.trace(Trace.PERFORMANCE, "ServerWorkingCopy.getWorkingCopyDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getServerType().getId());
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
					}
				}
			}
		}
		return workingCopyDelegate;
	}
	
	public void dispose() {
		super.dispose();
		if (workingCopyDelegate != null)
			workingCopyDelegate.dispose();
	}
	
	public IServer save(IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.subTask(ServerPlugin.getResource("%savingTask", getName()));
		if (wch.isReleased())
			return null;
		if (server == null) {
			server = new Server(file);
			server.setServerState(serverState);
			server.publishListeners = publishListeners;
			server.serverListeners = serverListeners;
		}
		
		getWorkingCopyDelegate().handleSave(IServerWorkingCopyDelegate.PRE_SAVE, monitor);
		server.setInternal(this);
		server.doSave(monitor);
		wch.setDirty(false);
		release();
		getWorkingCopyDelegate().handleSave(IServerWorkingCopyDelegate.POST_SAVE, monitor);
		
		return server;
	}

	public IServer save(IProgressMonitor monitor, boolean release) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.subTask(ServerPlugin.getResource("%savingTask", getName()));
		if (wch.isReleased())
			return null;
		if (server == null) {
			server = new Server(file);
			server.setServerState(serverState);
			server.publishListeners = publishListeners;
			server.serverListeners = serverListeners;
		}
		getWorkingCopyDelegate().handleSave(IServerWorkingCopyDelegate.PRE_SAVE, monitor);
		server.setInternal(this);
		server.doSave(monitor);
		wch.setDirty(false);
		if (release)
			release();
		getWorkingCopyDelegate().handleSave(IServerWorkingCopyDelegate.POST_SAVE, monitor);
		return server;
	}

	public IServer saveAll(IProgressMonitor monitor) throws CoreException {
		if (runtime != null && runtime.isWorkingCopy()) {
			IRuntimeWorkingCopy wc = (IRuntimeWorkingCopy) runtime;
			wc.save(monitor);
		}
		
		if (configuration != null && configuration.isWorkingCopy()) {
			IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) configuration;
			wc.save(monitor);
		}
		
		return save(monitor);
	}

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		wch.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		wch.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fire a property change event.
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		wch.firePropertyChangeEvent(propertyName, oldValue, newValue);
	}
	
	public void addServerListener(IServerListener listener) {
		if (server != null)
			server.addServerListener(listener);
		else
			super.addServerListener(listener);
	}
	
	public void removeServerListener(IServerListener listener) {
		if (server != null)
			server.removeServerListener(listener);
		else
			super.removeServerListener(listener);
	}
	
	public void addPublishListener(IPublishListener listener) {
		if (server != null)
			server.addPublishListener(listener);
		else
			super.addPublishListener(listener);
	}
	
	public void removePublishListener(IPublishListener listener) {
		if (server != null)
			server.removePublishListener(listener);
		else
			super.removePublishListener(listener);
	}

	public void setRuntime(IRuntime runtime) {
		this.runtime = runtime;
		if (runtime != null)
			setAttribute(RUNTIME_ID, runtime.getId());
		else
			setAttribute(RUNTIME_ID, (String)null);
	}
	
	public void setRuntimeId(String runtimeId) {
		setAttribute(RUNTIME_ID, runtimeId);
		resolve();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServer#modifyModule(org.eclipse.wst.server.core.model.IModule)
	 */
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		int i = 0;
		while (getServerState() == IServer.SERVER_UNKNOWN && i < 10) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) { }
			i++;
		}
		
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.subTask(ServerPlugin.getResource("%taskModifyModules"));
			getWorkingCopyDelegate().modifyModules(add, remove, monitor);
			wch.setDirty(true);
		} catch (CoreException ce) {
			throw ce;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate modifyModule() " + toString(), e);
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, e.getLocalizedMessage(), e));
		}
	}

	public void setDefaults() {
		try {
			getWorkingCopyDelegate().setDefaults();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setDefaults() " + toString(), e);
		}
	}
	
	public String toString() {
		return "ServerWorkingCopy " + getId();
	}
}
