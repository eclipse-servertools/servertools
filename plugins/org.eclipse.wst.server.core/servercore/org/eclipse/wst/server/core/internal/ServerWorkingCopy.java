/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.InternalInitializer;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.model.ServerDelegate;

/**
 * 
 */
public class ServerWorkingCopy extends Server implements IServerWorkingCopy {
	protected Server server;
	protected WorkingCopyHelper wch;

	protected ServerDelegate workingCopyDelegate;

	// working copy
	public ServerWorkingCopy(Server server) {
		super(server.getFile());
		this.server = server;
		
		map = new HashMap<String, Object>(server.map);
		wch = new WorkingCopyHelper(this);
		
		resolve();
	}

	// creation
	public ServerWorkingCopy(String id, IFile file, IRuntime runtime, IServerType serverType) {
		super(id, file, runtime, serverType);
		wch = new WorkingCopyHelper(this);
		wch.setDirty(true);
		if (serverType instanceof ServerType)
			serverState = ((ServerType)serverType).getInitialState();
	}

	public boolean isWorkingCopy() {
		return true;
	}

	public IServer getOriginal() {
		return server;
	}

	public IServerWorkingCopy createWorkingCopy() {
		return this;
	}

	public int getServerState() {
		if (server != null)
			return server.getServerState();
		return super.getServerState();
	}

	public void setServerState(int state) {
		if (server != null)
			server.setServerState(state);
		else
			super.setServerState(state);
	}

	public int getServerPublishState() {
		if (server != null)
			return server.getServerPublishState();
		return super.getServerPublishState();
	}

	public void setServerPublishState(int state) {
		if (server != null)
			server.setServerPublishState(state);
		else
			super.setServerPublishState(state);
	}

	public IStatus getServerStatus() {
		if (server != null)
			return server.getServerStatus();
		return super.getServerStatus();
	}

	public void setServerStatus(IStatus status) {
		if (server != null)
			server.setServerStatus(status);
		else
			super.setServerStatus(status);
	}

	public int getModuleState(IModule[] module) {
		if (server != null)
			return server.getModuleState(module);
		return super.getModuleState(module);
	}

	public void setModuleState(IModule[] module, int state) {
		if (server != null)
			server.setModuleState(module, state);
		else
			super.setModuleState(module, state);
	}

	public int getModulePublishState(IModule[] module) {
		if (server != null)
			return server.getModulePublishState(module);
		return super.getModulePublishState(module);
	}

	public void setModulePublishState(IModule[] module, int state) {
		if (server != null)
			server.setModulePublishState(module, state);
		else
			super.setModulePublishState(module, state);
	}

	public boolean getModuleRestartState(IModule[] module) {
		if (server != null)
			return server.getModuleRestartState(module);
		return super.getModuleRestartState(module);
	}

	public void setModuleRestartState(IModule[] module, boolean r) {
		if (server != null)
			server.setModuleRestartState(module, r);
		else
			super.setModuleRestartState(module, r);
	}

	public IStatus getModuleStatus(IModule[] module) {
		if (server != null)
			return server.getModuleStatus(module);
		return super.getModuleStatus(module);
	}

	public void setModuleStatus(IModule[] module, IStatus status) {
		if (server != null)
			server.setModuleStatus(module, status);
		else
			super.setModuleStatus(module, status);
	}

	public String getMode() {
		if (server != null)
			return server.getMode();
		return mode;
	}

	public void setMode(String mode) {
		if (server != null)
			server.setMode(mode);
		else
			super.setMode(mode);
	}

	public void setAttribute(String attributeName, int value) {
		canModifyAttribute(attributeName);		
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, boolean value) {
		canModifyAttribute(attributeName);
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, String value) {
		canModifyAttribute(attributeName);
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, List<String> value) {
		canModifyAttribute(attributeName);
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, Map value) {
		canModifyAttribute(attributeName);
		wch.setAttribute(attributeName, value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerWorkingCopy#setName(java.lang.String)
	 */
	public void setName(String name) {
		setAttribute(PROP_NAME, name);
		boolean set = getAttribute(PROP_ID_SET, false);
		if (server == null && !set)
			setAttribute(PROP_ID, name);
	}

	public void setReadOnly(boolean b) {
		setAttribute(PROP_LOCKED, b);
	}

	/**
	 * Sets whether this element is private.
	 * Generally speaking, elements marked private are internal ones
	 * that should not be shown to users (because they won't know
	 * anything about them).
	 * 
	 * @param b <code>true</code> if this element is private,
	 * and <code>false</code> otherwise
	 * @see #isPrivate()
	 */
	public void setPrivate(boolean b) {
		setAttribute(PROP_PRIVATE, b);
	}

	public void setHost(String host) {
		setAttribute(PROP_HOSTNAME, host);
	}

	public void setAutoPublishTime(int p) {
		setAttribute(PROP_AUTO_PUBLISH_TIME, p);
	}

	public void setStartTimeout(int p) {
		setAttribute(PROP_START_TIMEOUT, p);
	}

	public void setStopTimeout(int p) {
		setAttribute(PROP_STOP_TIMEOUT, p);
	}

	public void setAutoPublishSetting(int s) {
		setAttribute(PROP_AUTO_PUBLISH_SETTING, s);
	}

	public void setServerConfiguration(IFolder config) {
		this.configuration = config;
		if (configuration == null)
			setAttribute(CONFIGURATION_ID, (String)null);
		else
			setAttribute(CONFIGURATION_ID, configuration.getFullPath().toString());
	}

	/**
	 * Disable the preferred publish operation.
	 * 
	 * @param op a publish operation
	 * @return true if change is made. 
	 */
	public boolean disablePreferredPublishOperations(PublishOperation op) {
		List<String> list = getAttribute(PROP_DISABLED_PERFERRED_TASKS, (List<String>)null);
		if (list == null)
			list = new ArrayList<String>();
		
		String opId = getPublishOperationId(op);
		if (list.contains(opId))
			return false;
		list.add(opId);
		setAttribute(PROP_DISABLED_PERFERRED_TASKS, list);
		return true;
	}

	/**
	 * Enable the optional publish operation. Optional publish operation is not ran by default.
	 * 
	 * @param op a publish operation
	 * @return true if change is made. 
	 */
	public boolean enableOptionalPublishOperations(PublishOperation op) {
		List<String> list = getAttribute(PROP_ENABLED_OPTIONAL_TASKS, (List<String>)null);
		if (list == null)
			list = new ArrayList<String>();
		
		String opId = getPublishOperationId(op);
		if (list.contains(opId))
			return false;
		list.add(opId);
		setAttribute(PROP_ENABLED_OPTIONAL_TASKS, list);
		return true;
	}

	/**
	 * Reset all preferred operations to default
	 */
	public void resetPreferredPublishOperations() {
		setAttribute(PROP_DISABLED_PERFERRED_TASKS, (List<String>)null);
	}

	/**
	 * Reset all optional operations to default
	 */
	public void resetOptionalPublishOperations() {
		setAttribute(PROP_ENABLED_OPTIONAL_TASKS, (List<String>)null);
	}

	public void setPublisherEnabled(Publisher pub, boolean enabled) {
		if (pub == null)
			return;
		
		// copy over all elements except the updated publisher
		List<String> list = getAttribute(PROP_PUBLISHERS, EMPTY_LIST);
		List<String> newList = new ArrayList<String>();
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			int ind = id.indexOf(":");
			if (!pub.getId().equals(id.substring(0, ind)))
				newList.add(id);
		}
		
		String s = pub.getId() + ":";
		if (enabled)
			s += "true";
		else
			s += "false";
		newList.add(s);
		setAttribute(PROP_PUBLISHERS, newList);
	}

	/**
	 * Sets the file where this server instance is serialized.
	 * 
	 * @param file the file in the workspace where the server instance
	 *    is serialized, or <code>null</code> if the information is
	 *    instead to be persisted with the workspace but not with any
	 *    particular workspace resource
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerWorkingCopy#isDirty()
	 */
	public boolean isDirty() {
		return wch.isDirty();
	}

	public ServerDelegate getWorkingCopyDelegate(IProgressMonitor monitor) {
		// make sure that the regular delegate is loaded 
		//getDelegate();
		
		if (workingCopyDelegate != null || serverType == null)
			return workingCopyDelegate;
		
		synchronized (this) {
			if (workingCopyDelegate == null) {
				try {
					long time = System.currentTimeMillis();
					workingCopyDelegate = ((ServerType) serverType).createServerDelegate();
					InternalInitializer.initializeServerDelegate(workingCopyDelegate, this, monitor);
					if (Trace.PERFORMANCE) {
						Trace.trace(Trace.STRING_PERFORMANCE,
								"ServerWorkingCopy.getWorkingCopyDelegate(): <" + (System.currentTimeMillis() - time)
										+ "> " + getServerType().getId());
					}
				} catch (Exception e) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Could not create delegate " + toString(), e);
					}
				}
			}
		}
		return workingCopyDelegate;
	}

	protected ServerBehaviourDelegate getBehaviourDelegate(IProgressMonitor monitor) {
		if (server == null)
			return null;
		
		if (behaviourDelegate != null)
			return behaviourDelegate;
		
		synchronized (this) {
			if (behaviourDelegate == null)
				behaviourDelegate = server.getBehaviourDelegate(monitor);
		}
		return behaviourDelegate;
	}

	public void dispose() {
		// behaviour delegate is cached from the original server
		behaviourDelegate = null;
		
		super.dispose();
		if (workingCopyDelegate != null)
			workingCopyDelegate.dispose();
	}

	public IServer save(boolean force, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.subTask(NLS.bind(Messages.savingTask, getName()));
		
		if (!force && getOriginal() != null)
			wch.validateTimestamp(((Server)getOriginal()).getTimestamp());
		
		int timestamp = getTimestamp();
		map.put(PROP_TIMESTAMP, Integer.toString(timestamp+1));
		
		if (server == null) {
			server = new Server(file);
			server.setServerState(serverState);
			server.publishListeners = publishListeners;
			server.notificationManager = notificationManager;
		}
		
		if (getServerType() != null && getServerType().hasServerConfiguration()) {
			IFolder folder = getServerConfiguration();
			if (folder == null) {
				folder = ServerType.getServerProject().getFolder(getName() + "-config");
				if (!folder.exists())
					folder.create(true, true, null);
				setServerConfiguration(folder);
			}
		}
		
		server.setInternal(this);
		server.doSave(monitor);
		if (getServerType() != null && getServerType().hasServerConfiguration()) {
			IFolder folder = getServerConfiguration();
			if (folder != null) {
				IProject project = folder.getProject();
				if (project != null && !project.exists()) {
					project.create(null);
					project.open(null);
					ServerPlugin.getProjectProperties(project).setServerProject(true, monitor);
				}
				if (!folder.exists())
					folder.create(IResource.FORCE, true, null);
			}
		}
		if (getWorkingCopyDelegate(monitor) != null)
			getWorkingCopyDelegate(monitor).saveConfiguration(monitor);
		wch.setDirty(false);
		
		if (getServerState() == IServer.STATE_STARTED)
			server.autoPublish();
		
		return server;
	}

	public IServer saveAll(boolean force, IProgressMonitor monitor) throws CoreException {
		if (runtime != null && runtime.isWorkingCopy()) {
			IRuntimeWorkingCopy wc = (IRuntimeWorkingCopy) runtime;
			wc.save(force, monitor);
		}
		
		return save(force, monitor);
	}

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		wch.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		wch.removePropertyChangeListener(listener);
	}

	/**
	 * Fire a property change event.
	 * 
	 * @param propertyName a property name
	 * @param oldValue the old value
	 * @param newValue the new value
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
		// null checks are in canModifyModules
		IStatus status = canModifyModules(add, remove, monitor);
		if (status != null && status.getSeverity() == IStatus.ERROR)
			throw new CoreException(status);
		
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.subTask(Messages.taskModifyModules);
			getWorkingCopyDelegate(monitor).modifyModules(add, remove, monitor);
			wch.setDirty(true);
			
			// trigger load of modules list
			synchronized (modulesLock){
				getModulesWithoutLock();
				if (add != null) {
					int size = add.length;
					for (int i = 0; i < size; i++) {
						if (!modules.contains(add[i])) {
							modules.add(add[i]);
							resetState(new IModule[] { add[i] }, monitor);
						}
					}
				}
				
				if (remove != null) {
					int size = remove.length;
					externalModules = getExternalModules();
					for (int i = 0; i < size; i++) {
						if (modules.contains(remove[i])) {
							modules.remove(remove[i]);
							resetState(new IModule[] { remove[i] }, monitor);
						}
						if (externalModules != null && externalModules.contains(remove[i])) {
							externalModules.remove(remove[i]);
							resetState(new IModule[] { remove[i] }, monitor);
						}
					}
				}
				
				// convert to attribute
				List<String> list = new ArrayList<String>();
				Iterator iterator = modules.iterator();
				while (iterator.hasNext()) {
					IModule module = (IModule) iterator.next();
					StringBuffer sb = new StringBuffer(module.getName());
					sb.append("::");
					sb.append(module.getId());
					IModuleType mt = module.getModuleType();
					if (mt != null) {
						sb.append("::");
						sb.append(mt.getId());
						sb.append("::");
						sb.append(mt.getVersion());
					}
					list.add(sb.toString());
				}
				setAttribute(MODULE_LIST, list);
			}

			resetOptionalPublishOperations();
			resetPreferredPublishOperations();
		} catch (CoreException ce) {
			throw ce;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate modifyModule() " + toString(), e);
			}
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "" + e.getLocalizedMessage(), e));
		}
	}

	protected void resetState(IModule[] module, IProgressMonitor monitor) {
		setModulePublishState(module, PUBLISH_STATE_UNKNOWN);
		setModuleState(module, IServer.STATE_UNKNOWN);
		setModuleRestartState(module, false);
		setModuleStatus(module, null);
		try {
			IModule[] children = getChildModules(module, monitor);
			int size = children.length;
			int size2 = module.length;
			for (int i = 0; i < size; i++) {
				IModule[] child = new Module[size2 + 1];
				System.arraycopy(module, 0, child, 0, size2);
				child[size2] = children[i];
				resetPublishState(child, monitor);
				setModuleState(module, IServer.STATE_UNKNOWN);
				setModuleRestartState(module, false);
				setModuleStatus(module, null);
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * 
	 * @param module
	 * @param monitor
	 * @deprecated use resetState() instead
	 */
	protected void resetPublishState(IModule[] module, IProgressMonitor monitor) {
		setModulePublishState(module, PUBLISH_STATE_UNKNOWN);
		try {
			IModule[] children = getChildModules(module, monitor);
			int size = children.length;
			int size2 = module.length;
			for (int i = 0; i < size; i++) {
				IModule[] child = new Module[size2 + 1];
				System.arraycopy(module, 0, child, 0, size2);
				child[size2] = children[i];
				resetPublishState(child, monitor);
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Sets the defaults for this server, including the name. 
	 * 
	 * This method will only be called when creating a new server.
	 * 
	 * @param monitor a progress monitor, or null
	 */
	public void setDefaults(IProgressMonitor monitor) {
		try {
			ServerUtil.setServerDefaultName(this);
			getWorkingCopyDelegate(monitor).setDefaults(monitor);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate setDefaults() " + toString(), e);
			}
		}
	}
	
	/**
	 * The new server's host or runtime has changed. 
	 * 
	 * This method allows delegates to reset the default values 
	 * for the server in the context of the new runtime and host combination. 
	 * 
	 * This method should only be called when creating a new server.
	 *  
	 * @param monitor a progress monitor, or null
	 */
	public void newServerDetailsChanged(IProgressMonitor monitor) {
		try {
			getWorkingCopyDelegate(monitor).newServerDetailsChanged(monitor);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate newServerDetailsChanged() " + toString(), e);
			}
		}
	}
	

	public void renameFiles(IProgressMonitor monitor) throws CoreException {
		if (getServerConfiguration() != null) {
			IFolder folder = getServerConfiguration();
			IFolder folder2 = ServerType.getServerProject().getFolder(getName() + "-config");
			folder.move(folder2.getFullPath(), true, true, monitor);
			setServerConfiguration(folder2);
			save(true, monitor);
		}
		
		if (file != null) {
			IFile file2 = ServerUtil.getUnusedServerFile(file.getProject(), this);
			file.move(file2.getFullPath(), true, true, monitor);
		}
	}

	/*
	 * Publish to the server using the progress monitor. The result of the
	 * publish operation is returned as an IStatus.
	 */
	public IStatus publish(int kind, IProgressMonitor monitor) {
		if (server != null)
			return server.publish(kind, monitor);
		return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, null);
	}

	public void publish(int kind, List<IModule[]> modules2, IAdaptable info, IOperationListener listener) {
		if (server != null) {
			server.publish(kind, modules2, info, listener);
			return;
		}
		listener.done(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, Messages.errorPublishing, null));
	}

	public ILaunch getLaunch() {
		if (server != null)
			return server.getLaunch();
		return null;
	}

	public ILaunchConfiguration getLaunchConfiguration(boolean create, IProgressMonitor monitor) throws CoreException {
		if (server != null)
			return server.getLaunchConfiguration(create, monitor);
		return null;
	}

	/**
	 * Sets the server restart state.
	 *
	 * @param state boolean
	 */
	public void setServerRestartState(boolean state) {
		if (server != null)
			server.setServerRestartState(state);
		else
			super.setServerRestartState(state);
	}

	/**
	 * @see IServer#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (workingCopyDelegate != null) {
			if (adapter.isInstance(workingCopyDelegate))
				return workingCopyDelegate;
		}
		if (delegate != null) {
			if (adapter.isInstance(delegate))
				return delegate;
		}
		if (behaviourDelegate != null) {
			if (adapter.isInstance(behaviourDelegate))
				return behaviourDelegate;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * @see IServer#loadAdapter(Class, IProgressMonitor)
	 */
	public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
		getWorkingCopyDelegate(monitor);
		if (adapter.isInstance(workingCopyDelegate))
			return workingCopyDelegate;
		
		getDelegate(monitor);
		if (adapter.isInstance(delegate))
			return delegate;
		
		getBehaviourDelegate(monitor);
		if (adapter.isInstance(behaviourDelegate))
			return behaviourDelegate;
		
		return Platform.getAdapterManager().loadAdapter(this, adapter.getName());
	}

	/**
	 * Import the server configuration from the given runtime.
	 * 
	 * @param runtime2
	 * @param monitor
	 * @deprecated should use importRuntimeConfiguration() instead
	 */
	public void importConfiguration(IRuntime runtime2, IProgressMonitor monitor) {
		try {
			getWorkingCopyDelegate(monitor).importConfiguration(runtime2, monitor);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate importConfiguration() " + toString(), e);
			}
		}
	}

	/**
	 * Import the server configuration from the given runtime.
	 * 
	 * @param runtime2 a server runtime
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException if there is any problem importing the configuration
	 *    from the runtime
	 */
	public void importRuntimeConfiguration(IRuntime runtime2, IProgressMonitor monitor) throws CoreException {
		try {
			getWorkingCopyDelegate(monitor).importRuntimeConfiguration(runtime2, monitor);
		} catch (CoreException ce) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "CoreException calling delegate importConfiguration() " + toString(),
						ce);
			}
			throw ce;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate importConfiguration() " + toString(), e);
			}
		}
	}

	public void setExternalModules(IModule[] modules) {
		if (server != null)
			server.setExternalModules(modules);
		super.setExternalModules(modules);
	}

	public List<IModule> getExternalModules() {
		if (server != null)
			return server.getExternalModules();
		return super.getExternalModules();
	}

	/*
	 * Break connection with the original server so that this working copy can
	 * be duplicated. The name *must* be set afterwards to change the id. 
	 */
	public void disassociate() {
		server = null;
		if (getAttribute(PROP_LOCKED, false))
			setAttribute(PROP_LOCKED, false);
		if (getAttribute(PROP_ID_SET, false))
			setAttribute(PROP_ID_SET, false);
	}
	
	/**
	 * Checks if a given attribute can be modified, throws an IllegalArgumentException if otherwise 
	 * @param attributeName
	 */
	protected void canModifyAttribute(String attributeName){
		if (attributeName != null && 
				PROP_TIMESTAMP.equalsIgnoreCase(attributeName))			
			throw new IllegalArgumentException("Unmodifiable attribute: "+ attributeName);
	}

	public String toString() {
		return "ServerWorkingCopy " + getId();
	}
}