/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.debug.core.*;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.ServerAdapter;
import org.eclipse.wst.server.core.util.SocketUtil;
/**
 * 
 */
public class Server extends Base implements IServer {
	protected static final List EMPTY_LIST = new ArrayList(0);
	
	protected static final String PROP_HOSTNAME = "hostname";
	protected static final String SERVER_ID = "server-id";
	protected static final String RUNTIME_ID = "runtime-id";
	protected static final String CONFIGURATION_ID = "configuration-id";
	protected static final String MODULE_LIST = "modules";
	protected static final String PROP_AUTO_PUBLISH_TIME = "auto-publish-time";
	protected static final String PROP_AUTO_PUBLISH_DEFAULT = "auto-publish-default";

	protected static final char[] INVALID_CHARS = new char[] {'\\', '/', ':', '*', '?', '"', '<', '>', '|', '\0', '@', '&'};

	protected IServerType serverType;
	protected ServerDelegate delegate;
	protected ServerBehaviourDelegate behaviourDelegate;

	protected IRuntime runtime;
	protected IFolder configuration;
	
	// the list of modules that are to be published to the server
	protected List modules;
	
	// transient fields
	protected transient String mode = ILaunchManager.RUN_MODE;
	protected transient IModule[] serverModules;
	protected transient int serverState = STATE_UNKNOWN;
	protected transient int serverSyncState;
	protected transient boolean serverRestartNeeded;

	protected transient Map moduleState = new HashMap();
	protected transient Map modulePublishState = new HashMap();
	protected transient Map moduleRestartState = new HashMap();

	protected transient ServerPublishInfo publishInfo;
	protected transient AutoPublishThread autoPublishThread;

/*	private static final String[] stateStrings = new String[] {
		"unknown", "starting", "started", "started_debug",
		"stopping", "stopped", "started_unsupported", "started_profile"
	};*/
	
	// publish listeners
	protected transient List publishListeners;
	
	// server listeners
	protected transient List serverListeners;
	
	public class AutoPublishThread extends Thread {
		public boolean stop;
		public int time = 0; 
		
		public void run() {
			Trace.trace(Trace.FINEST, "Auto-publish thread starting for " + Server.this + " - " + time + "s");
			if (stop)
				return;
			
			try {
				sleep(time * 1000);
			} catch (Exception e) {
				// ignore
			}
			
			if (stop)
				return;
			
			Trace.trace(Trace.FINEST, "Auto-publish thread publishing " + Server.this);

			PublishServerJob publishJob = new PublishServerJob(Server.this, IServer.PUBLISH_AUTO, false);
			publishJob.schedule();
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
	
	public IServerWorkingCopy createWorkingCopy() {
		return new ServerWorkingCopy(this); 
	}

	public boolean isWorkingCopy() {
		return false;
	}
	
	protected void deleteFromMetadata() {
		ResourceManager.getInstance().removeServer(this);
	}
	
	protected void saveToMetadata(IProgressMonitor monitor) {
		super.saveToMetadata(monitor);
		ResourceManager.getInstance().addServer(this);
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
	public IFolder getServerConfiguration() {
		return configuration;
	}

	protected ServerDelegate getDelegate() {
		if (delegate != null)
			return delegate;
		
		if (serverType != null) {
			synchronized (this) {
				if (delegate == null) {
					try {
						long time = System.currentTimeMillis();
						IConfigurationElement element = ((ServerType) serverType).getElement();
						delegate = (ServerDelegate) element.createExecutableExtension("class");
						delegate.initialize(this);
						Trace.trace(Trace.PERFORMANCE, "Server.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getServerType().getId());
					} catch (Throwable t) {
						Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), t);
					}
				}
			}
		}
		return delegate;
	}
	
	protected ServerBehaviourDelegate getBehaviourDelegate() {
		if (behaviourDelegate != null)
			return behaviourDelegate;
		
		if (serverType != null) {
			synchronized (this) {
				if (behaviourDelegate == null) {
					try {
						long time = System.currentTimeMillis();
						IConfigurationElement element = ((ServerType) serverType).getElement();
						behaviourDelegate = (ServerBehaviourDelegate) element.createExecutableExtension("behaviourClass");
						behaviourDelegate.initialize(this);
						Trace.trace(Trace.PERFORMANCE, "Server.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getServerType().getId());
					} catch (Throwable t) {
						Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), t);
					}
				}
			}
		}
		return behaviourDelegate;
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

	public String getHost() {
		return getAttribute(PROP_HOSTNAME, "localhost");
	}
	
	public int getAutoPublishTime() {
		return getAttribute(PROP_AUTO_PUBLISH_TIME, -1);
	}
	
	public boolean getAutoPublishDefault() {
		return getAttribute(PROP_AUTO_PUBLISH_DEFAULT, true);
	}

	/**
	 * Returns the current state of the server. (see SERVER_XXX constants)
	 *
	 * @return int
	 */
	public int getServerState() {
		return serverState;
	}
	
	public String getMode() {
		return mode;
	}

	public void setServerState(int state) {
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
		IModule[] parents = new IModule[0];
		try {
			parents = getRootModules(module, null);
		} catch (Exception e) {
			// ignore
		}
		serverListeners.toArray(sil);
		
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.LISTENERS, "  Firing server module state change event to: " + sil[i]);
				sil[i].moduleStateChange(this, parents, module);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing server module state change event", e);
			}
		}
		Trace.trace(Trace.LISTENERS, "-<- Done firing server module state change event -<-");
	}

	public void setMode(String m) {
		this.mode = m;
	}

	public void setModules(IModule[] modules) {
		this.serverModules = modules;
	}

	public void setModuleState(IModule module, int state) {
		Integer in = new Integer(state);
		moduleState.put(module.getId(), in);
		fireServerModuleStateChangeEvent(module);
	}
	
	public void setModulePublishState(IModule module, int state) {
		Integer in = new Integer(state);
		modulePublishState.put(module.getId(), in);
		//fireServerModuleStateChangeEvent(module);
	}

	public void setModuleRestartState(IModule module, boolean r) {
		Boolean b = new Boolean(r);
		moduleState.put(module.getId(), b);
		//fireServerModuleStateChangeEvent(module);
	}

	protected void handleModuleProjectChange(final IModule module) {
		Trace.trace(Trace.FINEST, "> handleDeployableProjectChange() " + this + " " + module);
		
		class Helper {
			boolean changed;
		}
		final Helper helper = new Helper();
		
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(IModule[] parents2, IModule module2) {
				if (module2.getProject() == null)
					return true;
				
				if (module.equals(module2)) {
					IModuleResourceDelta[] delta2 = getPublishedResourceDelta(parents2, module2);
					if (delta2.length > 0)
						helper.changed = true;
					
					// TODO
					/*if (deployableDelta[i] == null)
						deployableDelta[i] = moduleProjects[i].getModuleResourceDelta(delta);
					
					if (deployableDelta[i] != null) {
						// updateDeployable(module, deployableDelta[i]);

						ModulePublishInfo control = PublishInfo.getPublishInfo().getPublishControl(Server.this, parents, module);
						if (control.isDirty())
							return true;
	
						control.setDirty(true);
						firePublishStateChange(parents, module);
					}*/
					return true;
				}
				return true;
			}
		};

		ServerUtil.visit(this, visitor, null);
		
		if (!helper.changed)
			return;
		
		// check for auto-publish
		if (autoPublishThread != null) {
			autoPublishThread.stop = true;
			autoPublishThread.interrupt();
			autoPublishThread = null;
		}
		
		int time = 0;
		if (getAutoPublishDefault()) {
			boolean local = SocketUtil.isLocalhost(getHost());
			if (local && ServerPreferences.getInstance().getAutoPublishLocal())
				time = ServerPreferences.getInstance().getAutoPublishLocalTime();
			else if (!local && ServerPreferences.getInstance().getAutoPublishRemote())
				time = ServerPreferences.getInstance().getAutoPublishRemoteTime();
		} else {
			time = getAutoPublishTime();
		}
		
		if (time > 5) {
			autoPublishThread = new AutoPublishThread();
			autoPublishThread.time = time;
			autoPublishThread.setPriority(Thread.MIN_PRIORITY + 1);
			autoPublishThread.start();
		}
		
		//Trace.trace(Trace.FINEST, "< handleDeployableProjectChange()");
	}

	/**
	 * Returns the configuration's sync state.
	 *
	 * @return int
	 */
	public int getServerPublishState() {
		return serverSyncState;
	}

	/**
	 * Sets the configuration sync state.
	 *
	 * @param state int
	 */
	public void setServerPublishState(int state) {
		if (state == serverSyncState)
			return;
		serverSyncState = state;
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
	private void firePublishStarted() {
		Trace.trace(Trace.FINEST, "->- Firing publish started event ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing publish started event to " + srl[i]);
			try {
				srl[i].publishStarted(this);
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
	private void fireModulePublishStarted(IModule[] parents, IModule module) {
		Trace.trace(Trace.FINEST, "->- Firing module publish started event: " + module + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing module publish started event to " + srl[i]);
			try {
				srl[i].publishModuleStarted(this, parents, module);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module publish started event to " + srl[i], e);
			}
		}

		Trace.trace(Trace.FINEST, "-<- Done firing module publish started event -<-");
	}
	
	/**
	 * Fire a publish target event.
	 *
	 * @param 
	 */
	private void fireModulePublishFinished(IModule[] parents, IModule module, IStatus status) {
		Trace.trace(Trace.FINEST, "->- Firing module finished event: " + module + " " + status + " ->-");
	
		if (publishListeners == null || publishListeners.isEmpty())
			return;

		int size = publishListeners.size();
		IPublishListener[] srl = new IPublishListener[size];
		publishListeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing module finished event to " + srl[i]);
			try {
				srl[i].publishModuleFinished(this, parents, module, status);
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
	private void firePublishFinished(IStatus status) {
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
	protected void firePublishStateChange(IModule[] parents, IModule module) {
		Trace.trace(Trace.FINEST, "->- Firing publish state change event: " + module + " ->-");
	
		if (serverListeners == null || serverListeners.isEmpty())
			return;

		int size = serverListeners.size();
		IServerListener[] sl = new IServerListener[size];
		serverListeners.toArray(sl);

		for (int i = 0; i < size; i++) {
			Trace.trace(Trace.FINEST, "  Firing publish state change event to " + sl[i]);
			try {
				sl[i].moduleStateChange(this, parents, module);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing publish state change event to " + sl[i], e);
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
		int state = getServerState();
		if (state == STATE_STARTING || state == STATE_STOPPING)
			return false;
	
		// can't publish if there is no configuration
		if (getServerType() == null || getServerType().hasServerConfiguration() && configuration == null)
			return false;
	
		// return true if the configuration can be published
		if (getServerPublishState() != PUBLISH_STATE_NONE)
			return true;

		// return true if any modules can be published
		class Temp {
			boolean found = false;
		}
		//final Temp temp = new Temp();
		
		return true;
	
		/*IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(IModule[] parents, IModule module) {
				if (getModulePublishState(module) != PUBLISH_STATE_NONE) {
					temp.found = true;
					return false;
				}
				return true;
			}
		};
		ServerUtil.visit(this, visitor, null);
		
		return temp.found;*/
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
	
		if (getServerPublishState() != PUBLISH_STATE_NONE)
			return true;
	
		//if (getUnpublishedModules().length > 0)
		return true;
	
		//return false;
	}
	

	/**
	 * Returns a list of the projects that have not been published
	 * since the last modification. (i.e. the projects that are
	 * out of sync with the server.
	 *
	 * @return java.util.List
	 */
	/*public IModule[] getUnpublishedModules() {
		final List modules = new ArrayList();
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(IModule[] parents, IModule module) {
				if (getModulePublishState(module) != PUBLISH_STATE_NONE && !modules.contains(module)) {
					ModulePublishInfo control = PublishInfo.getPublishInfo().getPublishControl(Server.this, parents, module);
					if (control.isDirty)
						modules.add(module);
				}
				return true;
			}
		};
		ServerUtil.visit(this, visitor, null);
		
		Trace.trace(Trace.FINEST, "Unpublished modules: " + modules);
		
		IModule[] m = new IModule[modules.size()];
		modules.toArray(m);
		return m;
	}*/

	protected ServerPublishInfo getServerPublishInfo() {
		if (publishInfo == null) {
			publishInfo = PublishInfo.getInstance().getServerPublishInfo(this);
		}
		return publishInfo;
	}

	/*
	 * Publish to the server using the progress monitor. The result of the
	 * publish operation is returned as an IStatus.
	 */
	public IStatus publish(IProgressMonitor monitor) {
		return publish(PUBLISH_INCREMENTAL, monitor);
	}

	/*
	 * Publish to the server using the progress monitor. The result of the
	 * publish operation is returned as an IStatus.
	 */
	public IStatus publish(final int kind, IProgressMonitor monitor) {
		if (getServerType() == null)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorPublishing"), null);

		// check what is out of sync and publish
		if (getServerType().hasServerConfiguration() && configuration == null)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorNoConfiguration"), null);
		
		return doPublish(kind, monitor);
	}

	protected IStatus doPublish(int kind, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "-->-- Publishing to server: " + toString() + " -->--");

		final List parentList = new ArrayList();
		final List moduleList = new ArrayList();
		final List taskParentList = new ArrayList();
		final List kindList = new ArrayList();
		
		final ServerPublishInfo spi = getServerPublishInfo();
		
		IModuleVisitor visitor = new IModuleVisitor() {
			public boolean visit(IModule[] parents, IModule module) {
				int size = parents.length;
				List list = new ArrayList(size);
				for (int i = 0; i < size; i++)
					list.add(parents[i]);
				
				taskParentList.add(list);
				if (parents != null)
					parentList.add(parents);
				else
					parentList.add(EMPTY_LIST);
				moduleList.add(module);
				
				if (spi.hasModulePublishInfo(parents, module)) {
					if (getPublishedResourceDelta(parents, module).length == 0)
						kindList.add(new Integer(NO_CHANGE));
					else
						kindList.add(new Integer(CHANGED));
				} else
					kindList.add(new Integer(ADDED));
				return true;
			}
		};

		ServerUtil.visit(this, visitor, monitor);
		
		// build arrays & lists
		List[] taskParents = new List[taskParentList.size()];
		taskParentList.toArray(taskParents);
		List parents = parentList;
		IModule[] modules2 = new IModule[moduleList.size()];
		moduleList.toArray(modules2);
		
		List tasks = getTasks(taskParents, modules2);
		
		spi.addRemovedModules(parentList, moduleList, kindList);
		
		parents = parentList;
		modules2 = new IModule[moduleList.size()];
		moduleList.toArray(modules2);
		
		int size = parents.size();
		int[] deltaKind = new int[size];
		for (int i = 0; i < size; i++) {
			Integer in = (Integer) kindList.get(i);
			deltaKind[i] = in.intValue();
		}

		size = 2000 + 3500 * parentList.size();
		
		// find tasks
		size += tasks.size() * 500;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(ServerPlugin.getResource("%publishing", toString()), size);

		MultiStatus multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%publishingStatus"), null);
		
		// perform tasks
		IStatus taskStatus = performTasks(tasks, monitor);
		if (taskStatus != null)
			multi.add(taskStatus);

		if (monitor.isCanceled())
			return new Status(IStatus.INFO, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%publishingCancelled"), null);
		
		// start publishing
		Trace.trace(Trace.FINEST, "Calling publishStart()");
		firePublishStarted();
		try {
			getBehaviourDelegate().publishStart(ProgressUtil.getSubMonitorFor(monitor, 1000));
		} catch (CoreException ce) {
			Trace.trace(Trace.INFO, "CoreException publishing to " + toString(), ce);
			firePublishFinished(ce.getStatus());
			return ce.getStatus();
		}
		
		// publish the server
		try {
			if (!monitor.isCanceled() && serverType.hasServerConfiguration()) {
				getBehaviourDelegate().publishServer(kind, ProgressUtil.getSubMonitorFor(monitor, 1000));
			}
		} catch (CoreException ce) {
			Trace.trace(Trace.INFO, "CoreException publishing to " + toString(), ce);
			multi.add(ce.getStatus());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error publishing configuration to " + toString(), e);
			multi.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorPublishing"), e));
		}
		
		// publish modules
		if (!monitor.isCanceled()) {
			publishModules(kind, parents, modules2, deltaKind, multi, monitor);
		}
		
		// end the publishing
		Trace.trace(Trace.FINEST, "Calling publishFinish()");
		try {
			getBehaviourDelegate().publishFinish(ProgressUtil.getSubMonitorFor(monitor, 500));
		} catch (CoreException ce) {
			Trace.trace(Trace.INFO, "CoreException publishing to " + toString(), ce);
			multi.add(ce.getStatus());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error stopping publish to " + toString(), e);
			multi.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorPublishing"), e));
		}
		
		if (monitor.isCanceled()) {
			IStatus status = new Status(IStatus.INFO, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%publishingCancelled"), null);
			multi.add(status);
		}

		MultiStatus ps = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%publishingStop"), null);
		ps.add(multi);
		firePublishFinished(multi);
		
		spi.save();

		monitor.done();

		Trace.trace(Trace.FINEST, "--<-- Done publishing --<--");
		return multi;
	}

	/**
	 * Publish a single module.
	 */
	protected IStatus publishModule(int kind, IModule[] parents, IModule module, int deltaKind, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "Publishing module: " + module);
		
		monitor.beginTask(ServerPlugin.getResource("%publishingModule", module.getName()), 1000);
		
		fireModulePublishStarted(parents, module);
		
		IStatus status = new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%publishedModule", module.getName()), null);
		try {
			getBehaviourDelegate().publishModule(kind, deltaKind, parents, module, monitor);
		} catch (CoreException ce) {
			status = ce.getStatus();
		}
		fireModulePublishFinished(parents, module, status);
		
		/*Trace.trace(Trace.FINEST, "Delta:");
		IModuleResourceDelta[] delta = getServerPublishInfo().getDelta(parents, module);
		int size = delta.length;
		for (int i = 0; i < size; i++) {
			((ModuleResourceDelta)delta[i]).trace(">  ");
		}*/
		if (deltaKind == REMOVED)
			getServerPublishInfo().removeModulePublishInfo(parents, module);
		else
			getServerPublishInfo().fill(parents, module);
		
		monitor.done();
		
		Trace.trace(Trace.FINEST, "Done publishing: " + module);
		return status;
	}

	/**
	 * Publishes the given modules. Returns true if the publishing
	 * should continue, or false if publishing has failed or is cancelled.
	 * 
	 * Uses 500 ticks plus 3500 ticks per module
	 */
	protected void publishModules(int kind, List parents, IModule[] modules2, int[] deltaKind, MultiStatus multi, IProgressMonitor monitor) {
		if (parents == null)
			return;

		int size = parents.size();
		if (size == 0)
			return;
		
		if (monitor.isCanceled())
			return;

		// publish modules
		for (int i = 0; i < size; i++) {
			IStatus status = publishModule(kind, (IModule[]) parents.get(i), modules2[i], deltaKind[i], ProgressUtil.getSubMonitorFor(monitor, 3000));
			multi.add(status);
		}
	}

	/*
	 * Returns the module resources that have been published.
	 * 
	 * @see ServerBehaviourDelegate.getPublishedResources(IModule[], IModule)
	 */
	public IModuleResource[] getPublishedResources(IModule[] parents, IModule module) {
		return getServerPublishInfo().getModulePublishInfo(parents, module).getResources();
	}

	/*
	 * Returns the delta of the current module resources that have been
	 * published compared to the current state of the module.
	 * 
	 * @see ServerBehaviourDelegate.getPublishedResourceDelta(IModule[], IModule)
	 */
	public IModuleResourceDelta[] getPublishedResourceDelta(IModule[] parents, IModule module) {
		return getServerPublishInfo().getDelta(parents, module);
	}

	protected List getTasks(List[] parents, IModule[] modules2) {
		List tasks = new ArrayList();
		
		String serverTypeId = getServerType().getId();
		
		IServerTask[] serverTasks = ServerCore.getServerTasks();
		if (serverTasks != null) {
			int size = serverTasks.length;
			for (int i = 0; i < size; i++) {
				IServerTask task = serverTasks[i];
				if (task.supportsType(serverTypeId)) {
					IOptionalTask[] tasks2 = task.getTasks(this, parents, modules2);
					if (tasks2 != null) {
						int size2 = tasks2.length;
						for (int j = 0; j < size2; j++) {
							if (tasks2[j].getStatus() == IOptionalTask.TASK_MANDATORY)
								tasks.add(tasks2[j]);
						}
					}
				}
			}
		}

		int size = tasks.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				IOrdered a = (IOrdered) tasks.get(i);
				IOrdered b = (IOrdered) tasks.get(j);
				if (a.getOrder() > b.getOrder()) {
					Object temp = a;
					tasks.set(i, b);
					tasks.set(j, temp);
				}
			}
		}
		
		return tasks;
	}

	protected IStatus performTasks(List tasks, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "Performing tasks: " + tasks.size());
		
		if (tasks.isEmpty())
			return null;
		
		Status multi = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%taskPerforming"), null);

		Iterator iterator = tasks.iterator();
		while (iterator.hasNext()) {
			IOptionalTask task = (IOptionalTask) iterator.next();
			monitor.subTask(ServerPlugin.getResource("%taskPerforming", task.toString()));
			try {
				task.execute(ProgressUtil.getSubMonitorFor(monitor, 500));
			} catch (CoreException ce) {
				Trace.trace(Trace.SEVERE, "Task failed", ce);
			}
			if (monitor.isCanceled())
				return multi;
		}
		
		// save server and configuration
		/*try {
			ServerUtil.save(server, ProgressUtil.getSubMonitorFor(monitor, 1000));
			ServerUtil.save(configuration, ProgressUtil.getSubMonitorFor(monitor, 1000));
		} catch (CoreException se) {
			Trace.trace(Trace.SEVERE, "Error saving server and/or configuration", se);
			multi.addChild(se.getStatus());
		}*/

		return multi;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		ServerDelegate delegate2 = getDelegate();
		if (adapter.isInstance(delegate2))
			return delegate2;
		ServerBehaviourDelegate delegate3 = getBehaviourDelegate();
		if (adapter.isInstance(delegate3))
			return delegate3;
		return Platform.getAdapterManager().getAdapter(this, adapter);
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
	public boolean canStart(String mode2) {
		int state = getServerState();
		if (state != STATE_STOPPED && state != STATE_UNKNOWN)
			return false;
		
		if (getServerType() == null || !getServerType().supportsLaunchMode(mode2))
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
			} catch (CoreException e) {
				// ignore
			}
		}
		
		return null;
	}

	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor) throws CoreException {
		try {
			getBehaviourDelegate().setupLaunchConfiguration(workingCopy, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setLaunchDefaults() " + toString(), e);
		}
	}

	public void importConfiguration(IRuntime runtime2, IProgressMonitor monitor) {
		try {
			getDelegate().importConfiguration(runtime2, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setLaunchDefaults() " + toString(), e);
		}
	}

	public ILaunchConfiguration getLaunchConfiguration(boolean create, IProgressMonitor monitor) throws CoreException {
		ILaunchConfigurationType launchConfigType = ((ServerType) getServerType()).getLaunchConfigurationType();
		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] launchConfigs = null;
		try {
			launchConfigs = launchManager.getLaunchConfigurations(launchConfigType);
		} catch (CoreException e) {
			// ignore
		}
		
		if (launchConfigs != null) {
			int size = launchConfigs.length;
			for (int i = 0; i < size; i++) {
				try {
					String serverId = launchConfigs[i].getAttribute(SERVER_ID, (String) null);
					if (getId().equals(serverId)) {
						ILaunchConfigurationWorkingCopy wc = launchConfigs[i].getWorkingCopy();
						setupLaunchConfiguration(wc, monitor);
						if (wc.isDirty())
							return wc.doSave();
						return launchConfigs[i];
					}
				} catch (CoreException e) {
					// ignore
				}
			}
		}
		
		if (!create)
			return null;
		
		// create a new launch configuration
		String launchName = getValidLaunchConfigurationName(getName());
		launchName = launchManager.generateUniqueLaunchConfigurationNameFrom(launchName); 
		ILaunchConfigurationWorkingCopy wc = launchConfigType.newInstance(null, launchName);
		wc.setAttribute(SERVER_ID, getId());
		setupLaunchConfiguration(wc, monitor);
		return wc.doSave();
	}

	protected String getValidLaunchConfigurationName(String s) {
		if (s == null || s.length() == 0)
			return "1";
		int size = INVALID_CHARS.length;
		for (int i = 0; i < size; i++) {
			s = s.replace(INVALID_CHARS[i], '_');
		}
		return s;
	}

	/**
	 * Start the server in the given mode.
	 *
	 * @param launchMode String
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclispe.core.runtime.IStatus
	 */
	public ILaunch start(String mode2, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.FINEST, "Starting server: " + toString() + ", launchMode: " + mode2);
	
		try {
			ILaunchConfiguration launchConfig = getLaunchConfiguration(true, monitor);
			ILaunch launch = launchConfig.launch(mode2, monitor);
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
				} catch (Exception e) {
					// ignore
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Returns true if the server is in a state that it can
	 * be restarted.
	 *
	 * @return boolean
	 */
	public boolean canRestart(String mode2) {
		/*ServerDelegate delegate2 = getDelegate();
		if (!(delegate2 instanceof IStartableServer))
			return false;*/
		if (!getServerType().supportsLaunchMode(mode2))
			return false;

		int state = getServerState();
		return (state == STATE_STARTED);
	}

	/**
	 * Returns the current restart state of the server. This
	 * implementation will always return false when the server
	 * is stopped.
	 *
	 * @return boolean
	 */
	public boolean getServerRestartState() {
		if (getServerState() == STATE_STOPPED)
			return false;
		return serverRestartNeeded;
	}

	/**
	 * Sets the server restart state.
	 *
	 * @param state boolean
	 */
	public synchronized void setServerRestartState(boolean state) {
		if (state == serverRestartNeeded)
			return;
		serverRestartNeeded = state;
		fireRestartStateChangeEvent();
	}

	/**
	 * Restart the server with the given debug mode.
	 * A server may only be restarted when it is currently running.
	 * This method is asynchronous.
	 */
	public void restart(final String mode2) {
		if (getServerState() == STATE_STOPPED)
			return;
	
		Trace.trace(Trace.FINEST, "Restarting server: " + getName());
	
		try {
			try {
				getBehaviourDelegate().restart(mode2);
				return;
			} catch (CoreException ce) {
				Trace.trace(Trace.SEVERE, "Error calling delegate restart() " + toString());
			}
		
			// add listener to start it as soon as it is stopped
			addServerListener(new ServerAdapter() {
				public void serverStateChange(IServer server) {
					if (server.getServerState() == STATE_STOPPED) {
						server.removeServerListener(this);

						// restart in a quarter second (give other listeners a chance
						// to hear the stopped message)
						Thread t = new Thread() {
							public void run() {
								try {
									Thread.sleep(250);
								} catch (Exception e) {
									// ignore
								}
								try {
									Server.this.start(mode2, new NullProgressMonitor());
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
			stop(false);
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
		if (getServerState() == STATE_STOPPED)
			return false;

		return true;
	}

	/**
	 * Stop the server if it is running.
	 */
	public void stop(boolean force) {
		if (getServerState() == STATE_STOPPED)
			return;

		Trace.trace(Trace.FINEST, "Stopping server: " + toString());

		try {
			getBehaviourDelegate().stop(force);
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error calling delegate stop() " + toString(), t);
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
	public ILaunch synchronousStart(String mode2, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.FINEST, "synchronousStart 1");
		final Object mutex = new Object();
	
		// add listener to the server
		IServerListener listener = new ServerAdapter() {
			public void serverStateChange(IServer server) {
				int state = server.getServerState();
				if (state == IServer.STATE_STARTED || state == IServer.STATE_STOPPED) {
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
		ILaunch launch;
		try {
			launch = start(mode2, monitor);
		} catch (CoreException e) {
			removeServerListener(listener);
			throw e;
		}
	
		Trace.trace(Trace.FINEST, "synchronousStart 3");
	
		// wait for it! wait for it! ...
		synchronized (mutex) {
			try {
				while (!timer.timeout && !(getServerState() == IServer.STATE_STARTED || getServerState() == IServer.STATE_STOPPED))
					mutex.wait();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error waiting for server start", e);
			}
		}
		removeServerListener(listener);
		
		if (timer.timeout)
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorStartFailed", getName()), null));
		timer.alreadyDone = true;
		
		if (getServerState() == IServer.STATE_STOPPED)
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorStartFailed", getName()), null));
	
		Trace.trace(Trace.FINEST, "synchronousStart 4");
		
		return launch;
	}

	/*
	 * @see IServer#synchronousRestart(String, IProgressMonitor)
	 */
	public void synchronousRestart(String mode2, IProgressMonitor monitor) throws CoreException {
		synchronousStop(true);
		synchronousStart(mode2, monitor);
	}

	/*
	 * @see IServer#synchronousStop()
	 */
	public void synchronousStop(boolean force) {
		if (getServerState() == IServer.STATE_STOPPED)
			return;
		
		final Object mutex = new Object();
	
		// add listener to the server
		IServerListener listener = new ServerAdapter() {
			public void serverStateChange(IServer server) {
				int state = server.getServerState();
				if (Server.this == server && state == IServer.STATE_STOPPED) {
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
		stop(force);
	
		// wait for it! wait for it!
		synchronized (mutex) {
			try {
				while (!timer.timeout && getServerState() != IServer.STATE_STOPPED)
					mutex.wait();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error waiting for server stop", e);
			}
		}
		removeServerListener(listener);
		
		/*
		//can't throw exceptions
		if (timer.timeout)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorStartFailed", getName()), null));
		else
			timer.alreadyDone = true;
		
		if (getServerState() == IServer.STATE_STOPPED)
			throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, 0, ServerPlugin.getResource("%errorStartFailed", getName()), null));*/
	}
	
	/**
	 * Trigger a restart of the given module and wait until it has finished restarting.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @exception org.eclipse.core.runtime.CoreException - thrown if an error occurs while trying to restart the module
	 */
	public void synchronousRestartModule(final IModule module, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.FINEST, "synchronousModuleRestart 1");

		final Object mutex = new Object();
	
		// add listener to the module
		IServerListener listener = new ServerAdapter() {
			public void moduleStateChange(IServer server) {
				int state = server.getModuleState(module);
				if (state == IServer.STATE_STARTED || state == IServer.STATE_STOPPED) {
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
			getBehaviourDelegate().restartModule(module, monitor);
		} catch (CoreException e) {
			removeServerListener(listener);
			throw e;
		}
	
		Trace.trace(Trace.FINEST, "synchronousModuleRestart 3");
	
		// wait for it! wait for it! ...
		synchronized (mutex) {
			try {
				while (!timer.timeout && !(getModuleState(module) == IServer.STATE_STARTED || getModuleState(module) == IServer.STATE_STOPPED))
					mutex.wait();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error waiting for server start", e);
			}
		}
		removeServerListener(listener);
		if (timer.timeout)
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorModuleRestartFailed", getName()), null));
		timer.alreadyDone = true;
		
		if (getModuleState(module) == IServer.STATE_STOPPED)
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, ServerPlugin.getResource("%errorModuleRestartFailed", getName()), null));
	
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
		if (serverTypeId != null)
			serverType = ServerCore.findServerType(serverTypeId);
		else
			serverType = null;
		if (serverType != null && !serverType.equals(oldServerType))
			serverState = ((ServerType)serverType).getInitialState();
		
		String runtimeId = getAttribute(RUNTIME_ID, (String)null);
		if (runtimeId != null)
			runtime = ServerCore.findRuntime(runtimeId);
		
		String configPath = getAttribute(CONFIGURATION_ID, (String)null);
		configuration = null;
		if (configPath != null)
			configuration = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(configPath));
	}

	protected void setInternal(ServerWorkingCopy wc) {
		map = wc.map;
		configuration = wc.configuration;
		runtime = wc.runtime;
		serverSyncState = wc.serverSyncState;
		//restartNeeded = wc.restartNeeded;
		serverType = wc.serverType;
		modules = wc.modules;

		// can never modify the following properties via the working copy
		//serverState = wc.serverState;
		delegate = wc.delegate;
	}

	protected void saveState(IMemento memento) {
		if (serverType != null)
			memento.putString("server-type", serverType.getId());

		if (configuration != null)
			memento.putString(CONFIGURATION_ID, configuration.getFullPath().toString());
		else
			memento.putString(CONFIGURATION_ID, null);
		
		if (runtime != null)
			memento.putString(RUNTIME_ID, runtime.getId());
		else
			memento.putString(RUNTIME_ID, null);
	}

	/*public void updateConfiguration() {
		try {
			getDelegate(null).updateConfiguration();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate updateConfiguration() " + toString(), e);
		}
	}*/
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServerConfiguration#canModifyModule(org.eclipse.wst.server.core.model.IModule)
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) {
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
		if (modules == null) {
			// convert from attribute
			List list = getAttribute(MODULE_LIST, (List) null);
			if (list == null)
				list = new ArrayList(1);
			
			modules = new ArrayList(list.size() + 1);
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				String moduleStr = (String) iterator.next();
				IModule module = ServerUtil.getModule(moduleStr);
				modules.add(module);
			}
		}
		
		IModule[] modules2 = new IModule[modules.size()];
		modules.toArray(modules2);
		return modules2;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServer#getServerModules()
	 */
	public IModule[] getServerModules(IProgressMonitor monitor) {
		try {
			return serverModules;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getModules() " + toString(), e);
			return new IModule[0];
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServer#getModuleState()
	 */
	public int getModuleState(IModule module) {
		try {
			Integer in = (Integer) moduleState.get(module.getId());
			if (in != null)
				return in.intValue();
		} catch (Exception e) {
			// ignore
		}
		return STATE_UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IServer#getModuleState()
	 */
	public int getModulePublishState(IModule module) {
		try {
			Integer in = (Integer) modulePublishState.get(module.getId());
			if (in != null)
				return in.intValue();
		} catch (Exception e) {
			// ignore
		}
		return PUBLISH_STATE_UNKNOWN;
	}

	/*
	 * @see IServer#getChildModule(IModule)
	 */
	public IModule[] getChildModules(IModule module, IProgressMonitor monitor) {
		try {
			return getDelegate().getChildModules(module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getChildModules() " + toString(), e);
			return null;
		}
	}

	/*
	 * @see IServer#getRootModules(IModule)
	 */
	public IModule[] getRootModules(IModule module, IProgressMonitor monitor) throws CoreException {
		try {
			return getDelegate().getRootModules(module);
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
	
	/**
	 * Returns whether the given module can be restarted.
	 *
	 * @param module the module
	 * @return <code>true</code> if the given module can be
	 * restarted, and <code>false</code> otherwise 
	 */
	public boolean canRestartModule(IModule module) {
		try {
			return getBehaviourDelegate().canRestartModule(module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate canRestartRuntime() " + toString(), e);
			return false;
		}
	}

	/**
	 * Check if the given module is in sync on the server. It should
	 * return true if the module should be restarted (is out of
	 * sync) or false if the module does not need to be restarted.
	 *
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	public boolean getModuleRestartState(IModule module) {
		try {
			Boolean b = (Boolean) moduleRestartState.get(module.getId());
			if (b != null)
				return b.booleanValue();
		} catch (Exception e) {
			// ignore
		}
		return false;
	}

	/*
	 * @see IServer#restartModule(IModule, IProgressMonitor)
	 */
	public void restartModule(IModule module, IProgressMonitor monitor) throws CoreException {
		try {
			getBehaviourDelegate().restartModule(module, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate restartModule() " + toString(), e);
		}
	}
	
	/**
	 * Returns an array of IServerPorts that this server has.
	 *
	 * @return 
	 */
	public IServerPort[] getServerPorts() {
		try {
			return getDelegate().getServerPorts();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getServerPorts() " + toString(), e);
			return null;
		}
	}
}