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

import java.util.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;
import org.osgi.framework.Bundle;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.resources.*;
import org.eclipse.wst.server.core.util.ProgressUtil;
import org.eclipse.wst.server.core.util.ServerAdapter;
/**
 * 
 */
public class Server extends Base implements IServer, IServerState {
	protected static final List EMPTY_LIST = new ArrayList(0);
	
	protected static final String PROP_HOSTNAME = "hostname";
	protected static final String SERVER_ID = "server-id";
	protected static final String RUNTIME_ID = "runtime-id";
	protected static final String CONFIGURATION_ID = "configuration-id";

	protected IServerType serverType;
	protected IServerDelegate delegate;

	protected IRuntime runtime;
	protected IServerConfiguration configuration;
	protected byte serverState = SERVER_UNKNOWN;

	// the configuration sync state
	protected byte configurationSyncState;

/*	private static final String[] stateStrings = new String[] {
		"unknown", "starting", "started", "started_debug",
		"stopping", "stopped", "started_unsupported", "started_profile"
	};*/
	
	// the current restart value
	protected boolean restartNeeded;
	
	//protected String lastMode = ILaunchManager.DEBUG_MODE;

	// publish listeners
	protected transient List publishListeners;
	
	// server listeners
	protected transient List serverListeners;
	
	class ServerTaskInfo implements IOrdered {
		IServerTask task;
		List[] parents;
		IModule[] modules;
		
		public int getOrder() {
			return task.getOrder();
		}
		
		public String toString() {
			return task.getName();
		}
	}

	class ModuleTaskInfo implements IOrdered {
		IModuleTask task;
		List parents;
		IModule module;
	
		public int getOrder() {
			return task.getOrder();
		}
		
		public String toString() {
			return task.getName();
		}
	}

	// working copy, loaded resource
	public Server(IFile file) {
		super(file);
		map.put(PROP_HOSTNAME, "localhost");
	}

	// creation (working copy)
	public Server(String id, IFile file, IRuntime runtime, IServerType serverType) {
		super(file, id);
		this.runtime = runtime;
		this.serverType = serverType;
		map.put("server-type-id", serverType.getId());
		map.put(PROP_HOSTNAME, "localhost");
		if (runtime != null && runtime.getRuntimeType() != null) {
			String name = runtime.getRuntimeType().getName();
			map.put(PROP_NAME, name);
		}
		serverState = ((ServerType)serverType).getInitialState();
	}
	
	public IServerType getServerType() {
		return serverType;
	}
	
	public IServerWorkingCopy getWorkingCopy() {
		IServerWorkingCopy wc = new ServerWorkingCopy(this); 
		addWorkingCopy(wc);
		return wc;
	}

	public boolean isWorkingCopy() {
		return false;
	}
	
	protected void deleteFromMetadata() {
		ResourceManager rm = (ResourceManager) ServerCore.getResourceManager();
		rm.removeServer(this);
	}
	
	protected void saveToMetadata(IProgressMonitor monitor) {
		super.saveToMetadata(monitor);
		ResourceManager rm = (ResourceManager) ServerCore.getResourceManager();
		rm.addServer(this);
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.IServer2#getRuntime()
	 */
	public IRuntime getRuntime() {
		return runtime;
	}

	protected String getRuntimeId() {
		return getAttribute(RUNTIME_ID, (String) null);
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.IServer2#getServerConfiguration()
	 */
	public IServerConfiguration getServerConfiguration() {
		return configuration;
	}

	public IServerDelegate getDelegate() {
		if (delegate != null)
			return delegate;
		
		if (serverType != null) {
			synchronized (this) {
				if (delegate == null) {
					try {
						long time = System.currentTimeMillis();
						IConfigurationElement element = ((ServerType) serverType).getElement();
						delegate = (IServerDelegate) element.createExecutableExtension("class");
						delegate.initialize(this);
						Trace.trace(Trace.PERFORMANCE, "Server.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getServerType().getId());
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
					}
				}
			}
		}
		return delegate;
	}
	
	/**
	 * Returns true if the delegate has been loaded.
	 * 
	 * @return
	 */
	public boolean isDelegateLoaded() {
		return delegate != null;
	}
	
	public void dispose() {
		if (delegate != null)
			delegate.dispose();
	}
	
	public boolean isDelegatePluginActivated() {
		IConfigurationElement element = ((ServerType) serverType).getElement();
		String pluginId = element.getDeclaringExtension().getNamespace();
		return Platform.getBundle(pluginId).getState() == Bundle.ACTIVE;
	}
	
	/**
	 * Returns true if this is a configuration that is
	 * applicable to (can be used with) this server.
	 *
	 * @param configuration org.eclipse.wst.server.core.model.IServerConfiguration
	 * @return boolean
	 */
	public boolean isSupportedConfiguration(IServerConfiguration configuration2) {
		if (!getServerType().hasServerConfiguration() || configuration2 == null)
			return false;
		return getServerType().getServerConfigurationType().equals(configuration2.getServerConfigurationType());
	}

	public String getHostname() {
		return getAttribute(PROP_HOSTNAME, "localhost");
	}

	/**
	 * Returns the current state of the server. (see SERVER_XXX constants)
	 *
	 * @return byte
	 */
	public byte getServerState() {
		return serverState;
	}

	public void setServerState(byte state) {
		if (state == serverState)
			return;

		this.serverState = state;
		fireServerStateChangeEvent();
	}
	
	/**
	 * Add a listener to this server.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerListener
	 */
	public void addServerListener(IServerListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding server listener " + listener + " to " + this);
	
		if (serverListeners == null)
			serverListeners = new ArrayList();
		serverListeners.add(listener);
	}
	
	/**
	 * Remove a listener from this server.
	 *
	 * @param listener org.eclipse.wst.server.model.IServerListener
	 */
	public void removeServerListener(IServerListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing server listener " + listener + " from " + this);
	
		if (serverListeners != null)
			serverListeners.remove(listener);
	}
	
	/**
	 * Fire a server listener configuration sync state change event.
	 */
	protected void fireConfigurationSyncStateChangeEvent() {
		Trace.trace(Trace.LISTENERS, "->- Firing server configuration change event: " + getName() + " ->-");
	
		if (serverListeners == null || serverListeners.isEmpty())
			return;
	
		int size = serverListeners.size();
		IServerListener[] sil = new IServerListener[size];
		serverListeners.toArray(sil);
	
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.LISTENERS, "  Firing server configuration change event to: " + sil[i]);
				sil[i].configurationSyncStateChange(this);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server configuration change event", e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server configuration change event -<-");
	}
	
	/**
	 * Fire a server listener restart state change event.
	 */
	protected void fireRestartStateChangeEvent() {
		Trace.trace(Trace.LISTENERS, "->- Firing server restart change event: " + getName() + " ->-");
	
		if (serverListeners == null || serverListeners.isEmpty())
			return;
	
		int size = serverListeners.size();
		IServerListener[] sil = new IServerListener[size];
		serverListeners.toArray(sil);
	
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.LISTENERS, "  Firing server restart change event to: " + sil[i]);
				sil[i].restartStateChange(this);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server restart change event", e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server restart change event -<-");
	}
	
	/**
	 * Fire a server listener state change event.
	 */
	protected void fireServerStateChangeEvent() {
		Trace.trace(Trace.LISTENERS, "->- Firing server state change event: " + getName() + ", " + getServerState() + " ->-");
	
		if (serverListeners == null || serverListeners.isEmpty())
			return;
	
		int size = serverListeners.size();
		IServerListener[] sil = new IServerListener[size];
		serverListeners.toArray(sil);
	
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.LISTENERS, "  Firing server state change event to: " + sil[i]);
				sil[i].serverStateChange(this);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server state change event", e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server state change event -<-");
	}
	
	/**
	 * Fire a server listener module change event.
	 */
	protected void fireServerModuleChangeEvent() {
		Trace.trace(Trace.LISTENERS, "->- Firing server module change event: " + getName() + ", " + getServerState() + " ->-");
		
		if (serverListeners == null || serverListeners.isEmpty())
			return;
		
		int size = serverListeners.size();
		IServerListener[] sil = new IServerListener[size];
		serverListeners.toArray(sil);
		
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.LISTENERS, "  Firing server module change event to: " + sil[i]);
				sil[i].modulesChanged(this);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server module change event", e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server module change event -<-");
	}
	
	/**
	 * Fire a server listener module state change event.
	 */
	protected void fireServerModuleStateChangeEvent(IModule module) {
		Trace.trace(Trace.LISTENERS, "->- Firing server module state change event: " + getName() + ", " + getServerState() + " ->-");
		
		if (serverListeners == null || serverListeners.isEmpty())
			return;
		
		int size = serverListeners.size();
		IServerListener[] sil = new IServerListener[size];
		serverListeners.toArray(sil);
		
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.LISTENERS, "  Firing server module state change event to: " + sil[i]);
				sil[i].moduleStateChange(this, module);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server module state change event", e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server module state change event -<-");
	}

	public void updateModuleState(IModule module) {
		fireServerModuleStateChangeEvent(module);
	}
	
	protected void handleModuleProjectChange(final IResourceDelta delta, final IProjectModule[] moduleProjects) {
		//Trace.trace(Trace.FINEST, "> handleDeployableProjectChange() " + server + " " + delta + " " + moduleProjects);
		final int size = moduleProjects.length;
		final IModuleResourceDelta[] deployableDelta = new IModuleResourceDelta[size];
		
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(List parents, IModule module) {
				if (!(module instanceof IProjectModule))
					return true;
				
				IPublisher publisher = getPublisher(parents, module);
				if (publisher == null)
					return true;

				for (int i = 0; i < size; i++) {
					if (moduleProjects[i].equals(module)) {
						if (deployableDelta[i] == null)
							deployableDelta[i] = moduleProjects[i].getModuleResourceDelta(delta);
						
						if (deployableDelta[i] != null) {
							// TODO updateDeployable(module, deployableDelta[i]);

							PublishControl control = PublishInfo.getPublishInfo().getPublishControl(Server.this, parents, module);
							if (control.isDirty())
								return true;
		
							control.setDirty(true);
							firePublishStateChange(parents, module);
						}
						return true;
					}
				}
				return true;
			}
		};

		ServerUtil.visit(this, visitor);
		//Trace.trace(Trace.FINEST, "< handleDeployableProjectChange()");
	}
	
	/**
	 * Returns the configuration's sync state.
	 *
	 * @return byte
	 */
	public byte getConfigurationSyncState() {
		return configurationSyncState;
	}
	
	/**
	 * Sets the configuration sync state.
	 *
	 * @param state byte
	 */
	public void setConfigurationSyncState(byte state) {
		if (state == configurationSyncState)
			return;
		configurationSyncState = state;
		fireConfigurationSyncStateChangeEvent();
	}
	
	/**
	 * Adds a publish listener to this server.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void addPublishListener(IPublishListener listener) {
		Trace.trace(Trace.LISTENERS, "Adding publish listener " + listener + " to " + this);

		if (publishListeners == null)
			publishListeners = new ArrayList();
		publishListeners.add(listener);
	}
	
	/**
	 * Removes a publish listener from this server.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IPublishListener
	 */
	public void removePublishListener(IPublishListener listener) {
		Trace.trace(Trace.LISTENERS, "Removing publish listener " + listener + " from " + this);

		if (publishListeners != null)
			publishListeners.remove(listener);
	}

	/**
	 * Fire a publish start event.
	 *
	 * @param 
	 */
	private void firePublishStarting(List[] parents, IModule[] targets) {
		Trace.trace(Trace.FINEST, "->- Firing publish starting event: " + targets + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing publish starting event to " + srl[i]);
			try {
				srl[i].publishStarting(this, parents, targets);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing publish starting event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing publish starting event -<-");
	}
	
	/**
	 * Fire a publish start event.
	 *
	 * @param 
	 */
	private void firePublishStarted(IPublishStatus status) {
		Trace.trace(Trace.FINEST, "->- Firing publish started event: " + status + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing publish started event to " + srl[i]);
			try {
				srl[i].publishStarted(this, status);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing publish started event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing publish started event -<-");
	}
	
	/**
	 * Fire a publish target event.
	 *
	 * @param 
	 */
	private void fireModulePublishStarting(List parents, IModule module) {
		Trace.trace(Trace.FINEST, "->- Firing module starting event: " + module + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing module starting event to " + srl[i]);
			try {
				srl[i].moduleStarting(this, parents, module);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module starting event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing module starting event -<-");
	}
	
	/**
	 * Fire a publish target event.
	 *
	 * @param 
	 */
	private void fireModulePublishFinished(List parents, IModule module, IPublishStatus status) {
		Trace.trace(Trace.FINEST, "->- Firing module finished event: " + module + " " + status + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing module finished event to " + srl[i]);
			try {
				srl[i].moduleFinished(this, parents, module, status);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module finished event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing module finished event -<-");
	}
	
	/**
	 * Fire a publish stop event.
	 *
	 * @param 
	 */
	private void firePublishFinished(IPublishStatus status) {
		Trace.trace(Trace.FINEST, "->- Firing publishing finished event: " + status + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing publishing finished event to " + srl[i]);
			try {
				srl[i].publishFinished(this, status);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing publishing finished event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing publishing finished event -<-");
	}

	/**
	 * Fire a publish state change event.
	 *
	 * @param 
	 */
	protected void firePublishStateChange(List parents, IModule module) {
		Trace.trace(Trace.FINEST, "->- Firing publish state change event: " + module + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing publish state change event to " + srl[i]);
			try {
				srl[i].moduleStateChange(this, parents, module);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing publish state change event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing publish state change event -<-");
	}

	/**
	 * Returns true if the server is in a state that it can
	 * be published to.
	 *
	 * @return boolean
	 */
	public boolean canPublish() {
		// can't publish if the server is starting or stopping
		byte state = getServerState();
		if (state == SERVER_STARTING ||
			state == SERVER_STOPPING)
			return false;
	
		// can't publish if there is no configuration
		if (getServerType() == null || getServerType().hasServerConfiguration() && configuration == null)
			return false;
	
		// return true if the configuration can be published
		if (getConfigurationSyncState() != SYNC_STATE_IN_SYNC)
			return true;

		// return true if any modules can be published
		class Temp {
			boolean found = false;
		}
		final Temp temp = new Temp();
	
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(List parents, IModule module) {
				if (getPublisher(parents, module) != null) {
					temp.found = true;
					return false;
				}
				return true;
			}
		};
		ServerUtil.visit(this, visitor);
		
		return temp.found;
	}
	
	/**
	 * Returns true if the server is in a state that it can
	 * be published to.
	 *
	 * @return boolean
	 */
	public boolean shouldPublish() {
		if (!canPublish())
			return false;
	
		if (getConfigurationSyncState() != SYNC_STATE_IN_SYNC)
			return true;
	
		if (!getUnpublishedModules().isEmpty())
			return true;
	
		return false;
	}
	

	/**
	 * Returns a list of the projects that have not been published
	 * since the last modification. (i.e. the projects that are
	 * out of sync with the server.
	 *
	 * @return java.util.List
	 */
	public List getUnpublishedModules() {
		final List modules = new ArrayList();
		
		if (configuration == null)
			return modules;
		
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(List parents, IModule module) {
				IPublisher publisher = getPublisher(parents, module);
				if (publisher != null && !modules.contains(module)) {
					PublishControl control = PublishInfo.getPublishInfo().getPublishControl(Server.this, parents, module);
					if (control.isDirty)
						modules.add(module);
				}
				return true;
			}
		};
		ServerUtil.visit(this, visitor);
		
		Trace.trace(Trace.FINEST, "Unpublished modules: " + modules);
		
		return modules;
	}
	
	public IPublisher getPublisher(List parents, IModule module) {
		try {
			return getDelegate().getPublisher(parents, module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getPublisher() " + toString(), e);
		}
		return null;
	}
	
	/**
	 * Publish to the server using the given progress monitor.
	 * This method will use the smart publisher which has no UI.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclispe.core.runtime.IStatus
	 */
	public IStatus publish(IProgressMonitor monitor) {
		return publish(ServerCore.getPublishManager(ServerPreferences.DEFAULT_PUBLISH_MANAGER), monitor);
	}

	/**
	 * Publish to the server using the given publisher and progress
	 * monitor. The result of the publish operation is returned as
	 * an IStatus.
	 *
	 * <p>This method will not present any UI unless 1) The publisher
	 * requires UI, or 2) There is already a publish listener on this
	 * server control which will respond to publish events by updating
	 * a UI.</p>
	 *
	 * @param publisher org.eclipse.wst.server.core.model.IPublishManager
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclispe.core.runtime.IStatus
	 */
	public IStatus publish(IPublishManager publishManager, IProgressMonitor monitor) {
		if (getServerType() == null)
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorPublishing"), null);

		// check what is out of sync and publish
		if (getServerType().hasServerConfiguration() && configuration == null)
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorNoConfiguration"), null);
	
		Trace.trace(Trace.FINEST, "-->-- Publishing to server: " + toString() + " -->--");

		final List parentList = new ArrayList();
		final List moduleList = new ArrayList();
		final List taskParentList = new ArrayList();
		final List taskModuleList = new ArrayList();
		
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(List parents, IModule module) {
				taskParentList.add(parents);
				taskModuleList.add(module);
				IPublisher publisher = getPublisher(parents, module);
				if (publisher != null) {
					if (parents != null)
						parentList.add(parents);
					else
						parentList.add(EMPTY_LIST);
					moduleList.add(module);
				}
				return true;
			}
		};

		ServerUtil.visit(this, visitor);
		
		// get arrays without the server configuration
		List[] taskParents = new List[taskParentList.size()];
		taskParentList.toArray(taskParents);
		IModule[] taskModules = new IModule[taskModuleList.size()];
		taskModuleList.toArray(taskModules);

		// get arrays with the server configuration
		List[] parents = new List[parentList.size()];
		parentList.toArray(parents);
		IModule[] modules = new IModule[moduleList.size()];
		moduleList.toArray(modules);

		int size = 2000 + 3500 * parentList.size();
		
		// find tasks
		List tasks = getTasks(taskParents, taskModules);
		size += tasks.size() * 500;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(ServerPlugin.getResource("%publishingTask", toString()), size);

		MultiStatus multi = new MultiStatus(ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%publishingStatus"), null);
		
		// perform tasks
		IStatus taskStatus = performTasks(tasks, monitor);
		if (taskStatus != null)
			multi.add(taskStatus);

		// start publishing
		Trace.trace(Trace.FINEST, "Opening connection to the remote server");
		boolean connectionOpen = false;
		try {
			if (!monitor.isCanceled()) {
				firePublishStarting(parents, modules);
				long time = System.currentTimeMillis();
				PublishStatus ps = new PublishStatus(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingStart"), null);
				IStatus status = getDelegate().publishStart(ProgressUtil.getSubMonitorFor(monitor, 1000));
				ps.setTime(System.currentTimeMillis() - time);
				ps.addChild(status);
				firePublishStarted(ps);
				multi.add(ps);
				if (status.getSeverity() != IStatus.ERROR)
					connectionOpen = true;
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error starting publish to " + toString(), e);
			connectionOpen = true; // possibly open
		}
		
		// publish the configuration
		try {
			if (connectionOpen && !monitor.isCanceled() && serverType.hasServerConfiguration()) {
				delegate.publishConfiguration(ProgressUtil.getSubMonitorFor(monitor, 1000));
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error publishing configuration to " + toString(), e);
		}
		
		// remove old modules
	
		// publish modules
		if (connectionOpen && !monitor.isCanceled()) {
			publishModules(publishManager, parents, modules, multi, monitor);
		}
		
		// end the publishing
		if (connectionOpen) {
			Trace.trace(Trace.FINEST, "Closing connection with the remote server");
			try {
				PublishStatus ps = new PublishStatus(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingStop"), null);
				IStatus status = delegate.publishStop(ProgressUtil.getSubMonitorFor(monitor, 500));
				ps.addChild(status);
				multi.add(ps);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error stopping publish to " + toString(), e);
			}
		}
	
		if (monitor.isCanceled()) {
			IStatus status = new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%publishingCancelled"), null);
			multi.add(status);
		}

		PublishStatus ps = new PublishStatus(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingStop"), null);
		ps.addChild(multi);
		firePublishFinished(ps);
		
		PublishInfo.getPublishInfo().save(this);

		monitor.done();

		Trace.trace(Trace.FINEST, "--<-- Done publishing --<--");
		return multi;
	}

	/**
	 * Publish a single module.
	 */
	protected IStatus publishModule(List parents, IModule module, IPublisher publisher, IPublishManager publishManager, PublishControl control, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "Publishing module: " + module + " " + publisher);
		
		monitor.beginTask(ServerPlugin.getResource("%publishingProject", module.getName()), 1000);
		
		fireModulePublishStarting(parents, module);
		long time = System.currentTimeMillis();
	
		PublishStatus multi = new PublishStatus(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingProject", module.getName()), module);
	
		// delete
		List verifyDeleteList = new ArrayList();
		try {
			List deleteList = publishManager.getResourcesToDelete(module);
			Trace.trace(Trace.FINEST, "Deleting: " + module + " " + deleteList);
			if (deleteList != null) {
				Trace.trace(Trace.FINEST, "Deleting remote resources:");
				IRemoteResource[] remote = new IRemoteResource[deleteList.size()];
				deleteList.toArray(remote);
				IStatus[] status = publisher.delete(remote, ProgressUtil.getSubMonitorFor(monitor, 300));
				int size = remote.length;
				if (status.length < size) {
					Trace.trace(Trace.WARNING, "Publish results missing: " + status.length + "/" + size);
					size = status.length;
				} 
				for (int i = 0; i < size; i++) {
					Trace.trace(Trace.FINEST, "  " + remote[i]);
					PublishStatusItem publishStatusItem = null;
					if (remote[i] instanceof IRemoteFolder)
						publishStatusItem = new PublishStatusItem(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingDeleteFolder", remote[i].getPath().toString()), status[i]);
					else
						publishStatusItem = new PublishStatusItem(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingDeleteFile", remote[i].getPath().toString()), status[i]);
					multi.addChild(publishStatusItem);
					if (status[i] == null || status[i].getSeverity() != IStatus.ERROR)
						verifyDeleteList.add(remote[i]);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not delete from server", e);
		}
		
		// publish
		List verifyList = new ArrayList();
		try {
			List publishList = publishManager.getResourcesToPublish(module);
			Trace.trace(Trace.FINEST, "Publishing: " + module + " " + publishList);
			if (publishList != null) {
				Trace.trace(Trace.FINEST, "Publishing resources:");
				IModuleResource[] resource = new IModuleResource[publishList.size()];
				publishList.toArray(resource);
				IStatus[] status = publisher.publish(resource, ProgressUtil.getSubMonitorFor(monitor, 600));
				int size = resource.length;
				if (status == null)
					size = 0;
				else if (status.length < size)
					size = status.length;
				for (int i = 0; i < size; i++) {
					Trace.trace(Trace.FINEST, "  " + resource[i]);
					PublishStatusItem publishStatusItem = null;
					if (resource[i] instanceof IModuleFolder)
						publishStatusItem = new PublishStatusItem(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingPublishFolder", resource[i].getPath().toString()), status[i]);
					else
						publishStatusItem = new PublishStatusItem(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%publishingPublishFile", resource[i].getPath().toString()), status[i]);
					multi.addChild(publishStatusItem);
					if (status[i] == null || status[i].getSeverity() != IStatus.ERROR)
						verifyList.add(resource[i]);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not publish to server", e);
		}
		
		// update state info
		control.verify(verifyList, verifyDeleteList, ProgressUtil.getSubMonitorFor(monitor, 100));
		control.setDirty(false);

		multi.setTime(System.currentTimeMillis() - time);
		fireModulePublishFinished(parents, module, multi);
		
		monitor.done();
		
		Trace.trace(Trace.FINEST, "Done publishing: " + module);
		return multi;
	}
	
	/**
	 * Publishes the given modules. Returns true if the publishing
	 * should continue, or false if publishing has failed or is cancelled.
	 * 
	 * Uses 500 ticks plus 3500 ticks per module
	 */
	protected void publishModules(final IPublishManager publishManager, List[] parents, IModule[] modules, MultiStatus multi, IProgressMonitor monitor) {
		if (parents == null)
			return;

		int size = parents.length;
		if (size == 0)
			return;

		PublishControl[] controls = new PublishControl[size];
		IPublisher[] publishers = new IPublisher[size];

		// fill publish control cache
		Trace.trace(Trace.FINEST, "Filling remote resource cache");
		for (int i = 0; i < size; i++) {
			publishers[i] = delegate.getPublisher(parents[i], modules[i]);
			controls[i] = PublishInfo.getPublishInfo().getPublishControl(this, parents[i], modules[i]);
			controls[i].setPublisher(publishers[i]);
			try {
				controls[i].fillRemoteResourceCache(ProgressUtil.getSubMonitorFor(monitor, 500));
			} catch (Exception e) { }
		}
	
		if (modules != null && modules.length > 0) {
			Trace.trace(Trace.FINEST, "Using publish manager: " + publishManager.getName());
	
			publishManager.resolve(controls, modules, ProgressUtil.getSubMonitorFor(monitor, 500));
			Trace.trace(Trace.FINEST, "Done resolving");
		}
		
		if (monitor.isCanceled())
			return;

		// publish modules
		for (int i = 0; i < size; i++) {
			IStatus status = publishModule(parents[i], modules[i], publishers[i], publishManager, controls[i], ProgressUtil.getSubMonitorFor(monitor, 3000));
			multi.add(status);
		}
	}

	protected List getTasks(List[] parents, IModule[] modules) {
		List tasks = new ArrayList();
		
		Iterator iterator = ServerCore.getServerTasks().iterator();
		while (iterator.hasNext()) {
			IServerTask task = (IServerTask) iterator.next();
			task.init(this, configuration, parents, modules);
			byte status = task.getTaskStatus();
			if (status == IServerTaskDelegate.TASK_MANDATORY) {
				ServerTaskInfo info = new ServerTaskInfo();
				info.task = task;
				info.parents = parents;
				info.modules = modules;
				tasks.add(info);
			}
		}
		
		int size = parents.length;
		for (int i = 0; i < size; i++) {
			iterator = ServerCore.getModuleTasks().iterator();
			while (iterator.hasNext()) {
				IModuleTask task = (IModuleTask) iterator.next();
				task.init(this, configuration, parents[i], modules[i]);
				byte status = task.getTaskStatus();
				if (status == IModuleTaskDelegate.TASK_MANDATORY) {
					ModuleTaskInfo info = new ModuleTaskInfo();
					info.task = task;
					info.parents = parents[i];
					info.module = modules[i];
					tasks.add(info);
				}
			}
		}

		ServerUtil.sortOrderedList(tasks);
		
		return tasks;
	}

	protected IStatus performTasks(List tasks, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "Performing tasks: " + tasks.size());
		
		if (tasks.isEmpty())
			return null;
		
		long time = System.currentTimeMillis();
		PublishStatus multi = new PublishStatus(ServerCore.PLUGIN_ID, ServerPlugin.getResource("%taskPerforming"), null);

		/*Iterator iterator = tasks.iterator();
		while (iterator.hasNext()) {
			IOrdered task = (IOrdered) iterator.next();
			monitor.subTask(ServerPlugin.getResource("%taskPerforming", task.toString()));
			IStatus status = null;
			if (task instanceof ServerTaskInfo) {
				ServerTaskInfo info = (ServerTaskInfo) task;
				status = info.task.performTask(server, configuration, info.parents, info.modules, ProgressUtil.getSubMonitorFor(monitor, 500));
			} else {
				ModuleTaskInfo info = (ModuleTaskInfo) task;
				status = info.task.performTask(server, configuration, info.parents, info.module, ProgressUtil.getSubMonitorFor(monitor, 500));
			}
			multi.addChild(status);
			if (monitor.isCanceled())
				return multi;
		}
		
		// save server and configuration
		try {
			ServerUtil.save(server, ProgressUtil.getSubMonitorFor(monitor, 1000));
			ServerUtil.save(configuration, ProgressUtil.getSubMonitorFor(monitor, 1000));
		} catch (CoreException se) {
			Trace.trace(Trace.SEVERE, "Error saving server and/or configuration", se);
			multi.addChild(se.getStatus());
		}*/

		multi.setTime(System.currentTimeMillis() - time);
		return multi;
	}

	public String toString() {
		return getName();
	}

	/**
	 * Returns true if the server is in a state that it can
	 * be started, and supports the given mode.
	 *
	 * @param mode
	 * @return boolean
	 */
	public boolean canStart(String mode) {
		byte state = getServerState();
		if (state != SERVER_STOPPED && state != SERVER_UNKNOWN)
			return false;
		
		if (getServerType() == null || !getServerType().supportsLaunchMode(mode))
			return false;

		return true;
	}
	
	public ILaunch getExistingLaunch() {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		
		ILaunch[] launches = launchManager.getLaunches();
		int size = launches.length;
		for (int i = 0; i < size; i++) {
			ILaunchConfiguration launchConfig = launches[i].getLaunchConfiguration();
			try {
				if (launchConfig != null) {
					String serverId = launchConfig.getAttribute(SERVER_ID, (String) null);
					if (getId().equals(serverId)) {
						if (!launches[i].isTerminated())
							return launches[i];
					}
				}
			} catch (CoreException e) { }
		}
		
		return null;
	}

	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		try {
			getDelegate().setLaunchDefaults(workingCopy);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setLaunchDefaults() " + toString(), e);
		}
	}
	
	public ILaunchConfiguration getLaunchConfiguration(boolean create) throws CoreException {
		ILaunchConfigurationType launchConfigType = ((ServerType) getServerType()).getLaunchConfigurationType();
		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] launchConfigs = null;
		try {
			launchConfigs = launchManager.getLaunchConfigurations(launchConfigType);
		} catch (CoreException e) { }
		
		if (launchConfigs != null) {
			int size = launchConfigs.length;
			for (int i = 0; i < size; i++) {
				try {
					String serverId = launchConfigs[i].getAttribute(SERVER_ID, (String) null);
					if (getId().equals(serverId))
						return launchConfigs[i];
				} catch (CoreException e) { }
			}
		}
		
		if (!create)
			return null;
		
		// create a new launch configuration
		String name = launchManager.generateUniqueLaunchConfigurationNameFrom(getName()); 
		ILaunchConfigurationWorkingCopy wc = launchConfigType.newInstance(null, name);
		wc.setAttribute(SERVER_ID, getId());
		setLaunchDefaults(wc);
		return wc.doSave();
	}

	/**
	 * Start the server in the given mode.
	 *
	 * @param launchMode String
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclispe.core.runtime.IStatus
	 */
	public ILaunch start(String mode, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.FINEST, "Starting server: " + toString() + ", launchMode: " + mode);
	
		try {
			ILaunchConfiguration launchConfig = getLaunchConfiguration(true);
			ILaunch launch = launchConfig.launch(mode, monitor);
			Trace.trace(Trace.FINEST, "Launch: " + launch);
			return launch;
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Error starting server " + toString(), e);
			throw e;
		}
	}

	/**
	 * Clean up any launch configurations with the given server ref.
	 * 
	 * @param serverRef java.lang.String
	 */
	protected void deleteLaunchConfigurations() {
		if (getServerType() == null)
			return;
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigType = ((ServerType) getServerType()).getLaunchConfigurationType();
		
		ILaunchConfiguration[] configs = null;
		try {
			configs = launchManager.getLaunchConfigurations(launchConfigType);
			int size = configs.length;
			for (int i = 0; i < size; i++) {
				try {
					if (getId().equals(configs[i].getAttribute(SERVER_ID, (String) null)))
						configs[i].delete();
				} catch (Exception e) { }
			}
		} catch (Exception e) { }
	}

	/**
	 * Returns true if the server is in a state that it can
	 * be restarted.
	 *
	 * @return boolean
	 */
	public boolean canRestart(String mode) {
		/*IServerDelegate delegate2 = getDelegate();
		if (!(delegate2 instanceof IStartableServer))
			return false;*/
		if (!getServerType().supportsLaunchMode(mode))
			return false;

		byte state = getServerState();
		return (state == SERVER_STARTED || state == SERVER_STARTED_DEBUG || state == SERVER_STARTED_PROFILE);
	}

	/**
	 * Returns the current restart state of the server. This
	 * implementation will always return false when the server
	 * is stopped.
	 *
	 * @return boolean
	 */
	public boolean isRestartNeeded() {
		if (getServerState() == SERVER_STOPPED)
			return false;
		return restartNeeded;
	}
	
	/**
	 * Sets the server restart state.
	 *
	 * @param state boolean
	 */
	public synchronized void setRestartNeeded(boolean state) {
		if (state == restartNeeded)
			return;
		restartNeeded = state;
		fireRestartStateChangeEvent();
	}

	/**
	 * Restart the server with the given debug mode.
	 * A server may only be restarted when it is currently running.
	 * This method is asynchronous.
	 */
	public void restart(final String mode) {
		if (getServerState() == SERVER_STOPPED)
			return;
	
		Trace.trace(Trace.FINEST, "Restarting server: " + getName());
	
		try {
			IServerDelegate delegate2 = getDelegate();
			if (delegate2 instanceof IRestartableServer) {
				((IRestartableServer) delegate2).restart(mode);
			} else {
				// add listener to start it as soon as it is stopped
				addServerListener(new ServerAdapter() {
					public void serverStateChange(IServer server) {
						if (server.getServerState() == SERVER_STOPPED) {
							server.removeServerListener(this);
	
							// restart in a quarter second (give other listeners a chance
							// to hear the stopped message)
							Thread t = new Thread() {
								public void run() {
									try {
										Thread.sleep(250);
									} catch (Exception e) { }
									try {
										Server.this.start(mode, new NullProgressMonitor());
									} catch (Exception e) {
										Trace.trace(Trace.SEVERE, "Error while restarting server", e);
									}
								}
							};
							t.setDaemon(true);
							t.setPriority(Thread.NORM_PRIORITY - 2);
							t.start();
						}
					}
				});
	
				// stop the server
				stop();
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error restarting server", e);
		}
	}


	/**
	 * Returns true if the server is in a state that it can
	 * be stopped.
	 *
	 * @return boolean
	 */
	public boolean canStop() {
		if (getServerState() == SERVER_STOPPED)
			return false;
		
		if (!(getDelegate() instanceof IStartableServer))
			return false;

		return true;
	}

	/**
	 * Stop the server if it is running.
	 */
	public void stop() {
		if (getServerState() == SERVER_STOPPED)
			return;

		// check if this is still a valid server
		if (!(getDelegate() instanceof IStartableServer))
			return;

		Trace.trace(Trace.FINEST, "Stopping server: " + toString());

		try {
			((IStartableServer) getDelegate()).stop();
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error stopping server " + toString(), t);
		}
	}
	
	/**
	 * Terminate the server process(es). This method should only be
	 * used as a last resort after the stop() method fails to work.
	 * The server should return from this method quickly and
	 * use the server listener to notify shutdown progress.
	 * It MUST terminate the server completely and return it to
	 * the stopped state.
	 */
	public void terminate() {
		try {
			IStartableServer startableServer = (IStartableServer) getDelegate();
			startableServer.terminate();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate terminate() " + toString(), e);
		}
	}
	
	/**
	 * Start the server in the given start mode and waits until the server
	 * has finished started.
	 *
	 * @param mode java.lang.String
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @exception org.eclipse.core.runtime.CoreException - thrown if an error occurs while trying to start the server
	 */
	public void synchronousStart(String mode, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.FINEST, "synchronousStart 1");
		final Object mutex = new Object();
	
		// add listener to the server
		IServerListener listener = new ServerAdapter() {
			public void serverStateChange(IServer server) {
				byte state = server.getServerState();
				if (state == IServer.SERVER_STARTED || state == IServer.SERVER_STARTED_DEBUG
					|| state == IServer.SERVER_STARTED_PROFILE || state == IServer.SERVER_STOPPED) {
					// notify waiter
					synchronized (mutex) {
						try {
							Trace.trace(Trace.FINEST, "synchronousStart notify");
							mutex.notifyAll();
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error notifying server start", e);
						}
					}
				}
			}
		};
		addServerListener(listener);
		
		class Timer {
			boolean timeout;
			boolean alreadyDone;
		}
		final Timer timer = new Timer();
		
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(120000);
					if (!timer.alreadyDone) {
						timer.timeout = true;
						// notify waiter
						synchronized (mutex) {
							Trace.trace(Trace.FINEST, "synchronousStart notify timeout");
							mutex.notifyAll();
						}
					}
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error notifying server start timeout", e);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	
		Trace.trace(Trace.FINEST, "synchronousStart 2");
	
		// start the server
		try {
			start(mode, monitor);
		} catch (CoreException e) {
			removeServerListener(listener);
			throw e;
		}
	
		Trace.trace(Trace.FINEST, "synchronousStart 3");
	
		// wait for it! wait for it! ...
		synchronized (mutex) {
			try {
				while (!timer.timeout && !(getServerState() == IServer.SERVER_STARTED ||
					getServerState() == IServer.SERVER_STARTED_DEBUG ||
					getServerState() == IServer.SERVER_STARTED_PROFILE ||
					getServerState() == IServer.SERVER_STOPPED))
					mutex.wait();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error waiting for server start", e);
			}
		}
		removeServerListener(listener);
		
		if (timer.timeout)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorInstanceStartFailed", getName()), null));
		timer.alreadyDone = true;
		
		if (getServerState() == IServer.SERVER_STOPPED)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorInstanceStartFailed", getName()), null));
	
		Trace.trace(Trace.FINEST, "synchronousStart 4");
	}

	/**
	 * Stop the server and wait until the
	 * server has completely stopped.
	 */
	public void synchronousStop() {
		if (getServerState() == IServer.SERVER_STOPPED)
			return;
		
		final Object mutex = new Object();
	
		// add listener to the server
		IServerListener listener = new ServerAdapter() {
			public void serverStateChange(IServer server) {
				byte state = server.getServerState();
				if (Server.this == server && state == IServer.SERVER_STOPPED) {
					// notify waiter
					synchronized (mutex) {
						try {
							mutex.notifyAll();
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error notifying server stop", e);
						}
					}
				}
			}
		};
		addServerListener(listener);
		
		class Timer {
			boolean timeout;
			boolean alreadyDone;
		}
		final Timer timer = new Timer();
		
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(120000);
					if (!timer.alreadyDone) {
						timer.timeout = true;
						// notify waiter
						synchronized (mutex) {
							Trace.trace(Trace.FINEST, "stop notify timeout");
							mutex.notifyAll();
						}
					}
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error notifying server stop timeout", e);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	
		// stop the server
		stop();
	
		// wait for it! wait for it!
		synchronized (mutex) {
			try {
				while (!timer.timeout && getServerState() != IServer.SERVER_STOPPED)
					mutex.wait();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error waiting for server stop", e);
			}
		}
		removeServerListener(listener);
		
		/*
		//can't throw exceptions
		if (timer.timeout)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorInstanceStartFailed", getName()), null));
		else
			timer.alreadyDone = true;
		
		if (getServerState() == IServer.SERVER_STOPPED)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorInstanceStartFailed", getName()), null));*/
	}
	
	/**
	 * Trigger a restart of the given module and wait until it has finished restarting.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @exception org.eclipse.core.runtime.CoreException - thrown if an error occurs while trying to restart the module
	 */
	public void synchronousModuleRestart(final IModule module, IProgressMonitor monitor) throws CoreException {
		IRestartableModule rm = null;
		try {
			rm = (IRestartableModule) getDelegate();
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, "Server does not support restarting modules", e));
		}
		Trace.trace(Trace.FINEST, "synchronousModuleRestart 1");

		final Object mutex = new Object();
	
		// add listener to the module
		IServerListener listener = new ServerAdapter() {
			public void moduleStateChange(IServer server) {
				byte state = server.getModuleState(module);
				if (state == IServer.MODULE_STATE_STARTED || state == IServer.MODULE_STATE_STOPPED) {
					// notify waiter
					synchronized (mutex) {
						try {
							Trace.trace(Trace.FINEST, "synchronousModuleRestart notify");
							mutex.notifyAll();
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error notifying module restart", e);
						}
					}
				}
			}
		};
		addServerListener(listener);
		
		// make sure it times out after 30s
		class Timer {
			boolean timeout;
			boolean alreadyDone;
		}
		final Timer timer = new Timer();
		
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(30000);
					if (!timer.alreadyDone) {
						timer.timeout = true;
						// notify waiter
						synchronized (mutex) {
							Trace.trace(Trace.FINEST, "synchronousModuleRestart notify timeout");
							mutex.notifyAll();
						}
					}
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error notifying module restart timeout", e);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	
		Trace.trace(Trace.FINEST, "synchronousModuleRestart 2");
	
		// restart the module
		try {
			rm.restartModule(module, monitor);
		} catch (CoreException e) {
			removeServerListener(listener);
			throw e;
		}
	
		Trace.trace(Trace.FINEST, "synchronousModuleRestart 3");
	
		// wait for it! wait for it! ...
		synchronized (mutex) {
			try {
				while (!timer.timeout && !(getModuleState(module) == IServer.MODULE_STATE_STARTED || getModuleState(module) == IServer.MODULE_STATE_STOPPED))
					mutex.wait();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error waiting for server start", e);
			}
		}
		removeServerListener(listener);
		if (timer.timeout)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorModuleRestartFailed", getName()), null));
		timer.alreadyDone = true;
		
		if (getModuleState(module) == IServer.SERVER_STOPPED)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorModuleRestartFailed", getName()), null));
	
		Trace.trace(Trace.FINEST, "synchronousModuleRestart 4");
	}

	public IPath getTempDirectory() {
		return ServerPlugin.getInstance().getTempDirectory(getId());
	}

	protected String getXMLRoot() {
		return "server";
	}
	
	protected void loadState(IMemento memento) {
		/*String serverTypeId = memento.getString("server-type-id");
		serverType = ServerCore.getServerType(serverTypeId);
		
		String runtimeId = memento.getString("runtime-id");
		runtime = ServerCore.getResourceManager().getRuntime(runtimeId);
		
		String configurationId = memento.getString("configuration-id");
		configuration = ServerCore.getResourceManager().getServerConfiguration(configurationId);*/
		resolve();
	}
	
	protected void resolve() {
		IServerType oldServerType = serverType;
		String serverTypeId = getAttribute("server-type-id", (String)null);
		serverType = ServerCore.getServerType(serverTypeId);
		if (serverType != null && !serverType.equals(oldServerType))
			serverState = ((ServerType)serverType).getInitialState();
		
		String runtimeId = getAttribute(RUNTIME_ID, (String)null);
		runtime = ServerCore.getResourceManager().getRuntime(runtimeId);
		
		String configurationId = getAttribute(CONFIGURATION_ID, (String)null);
		configuration = ServerCore.getResourceManager().getServerConfiguration(configurationId);
	}
	
	protected void setInternal(ServerWorkingCopy wc) {
		map = wc.map;
		configuration = wc.configuration;
		runtime = wc.runtime;
		configurationSyncState = wc.configurationSyncState;
		restartNeeded = wc.restartNeeded;
		serverType = wc.serverType;

		// can never modify the following properties via the working copy
		//serverState = wc.serverState;
		delegate = wc.delegate;
	}
	
	protected void saveState(IMemento memento) {
		if (serverType != null)
			memento.putString("server-type", serverType.getId());

		if (configuration != null)
			memento.putString(CONFIGURATION_ID, configuration.getId());
		else
			memento.putString(CONFIGURATION_ID, null);
		
		if (runtime != null)
			memento.putString(RUNTIME_ID, runtime.getId());
		else
			memento.putString(RUNTIME_ID, null);
	}
	
	public void updateConfiguration() {
		try {
			getDelegate().updateConfiguration();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate updateConfiguration() " + toString(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerConfiguration#canModifyModule(org.eclipse.wst.server.core.model.IModule)
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		try {
			return getDelegate().canModifyModules(add, remove);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate canModifyModules() " + toString(), e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServer#getModules()
	 */
	public IModule[] getModules() {
		try {
			return getDelegate().getModules();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getModules() " + toString(), e);
			return new IModule[0];
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServer#getModuleState()
	 */
	public byte getModuleState(IModule module) {
		try {
			return getDelegate().getModuleState(module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getModuleState() " + toString(), e);
			return MODULE_STATE_UNKNOWN;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerConfiguration#getRepairCommands(org.eclipse.wst.server.core.model.IModuleFactoryEvent[], org.eclipse.wst.server.core.model.IModuleEvent[])
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] moduleEvent) {
		try {
			return getDelegate().getRepairCommands(factoryEvent, moduleEvent);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getRepairCommands() " + toString(), e);
			return new ITask[0];
		}
	}

	/*
	 * @see IServerConfigurationFactory#getChildModule(IModule)
	 */
	public List getChildModules(IModule module) {
		try {
			return getDelegate().getChildModules(module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getChildModules() " + toString(), e);
			return null;
		}
	}

	/*
	 * @see IServerConfigurationFactory#getParentModules(IModule)
	 */
	public List getParentModules(IModule module) throws CoreException {
		try {
			return getDelegate().getParentModules(module);
		} catch (CoreException se) {
			//Trace.trace(Trace.FINER, "CoreException calling delegate getParentModules() " + toString() + ": " + se.getMessage());
			throw se;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getParentModules() " + toString(), e);
			return null;
		}
	}
	
	/*
	 * 
	 */
	/*public boolean hasRuntime() {
		try {
			return getDelegate().requiresRuntime();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate requiresRuntime() " + toString(), e);
			return false;
		}
	}*/
}