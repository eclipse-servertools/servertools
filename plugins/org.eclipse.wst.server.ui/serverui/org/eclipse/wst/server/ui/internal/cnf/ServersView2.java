/*******************************************************************************
 * Copyright (c) 2008,2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.cnf;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.UpdateServerJob;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerToolTip;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * A view of servers, their modules, and status.
 */
public class ServersView2 extends CommonNavigator {
	private static final String SERVERS_VIEW_CONTEXT = "org.eclipse.ui.serverViewScope";
	
	protected CommonViewer tableViewer;
	
	protected IServerLifecycleListener serverResourceListener;
	protected IPublishListener publishListener;
	protected IServerListener serverListener;
		
	// servers that are currently publishing and starting
	protected static Set<String> publishing = new HashSet<String>(4);
	protected static Set<String> starting = new HashSet<String>(4);
	protected boolean animationActive = false;
	protected boolean stopAnimation = false;

	/**
	 * ServersView constructor comment.
	 */
	public ServersView2() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IContextService contextSupport = (IContextService)getSite().getService(IContextService.class);
		contextSupport.activateContext(SERVERS_VIEW_CONTEXT);

		deferInitialization();
	}

	private void deferInitialization() {
		// TODO Angel Says: Need to do a final check on this line below. I don't think there is anything else
		// that we need from to port from the old Servers View
		//initializeActions(getCommonViewer());
		
		Job job = new Job(Messages.jobInitializingServersView) {
			public IStatus run(IProgressMonitor monitor) {
				IServer[] servers = ServerCore.getServers();
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					((Server)servers[i]).getAllModules().iterator();
				}
				deferredInitialize();
				return Status.OK_STATUS;
			}
		};
		
		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}
	
	protected void deferredInitialize() {
		addListener();
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					tableViewer = getCommonViewer();
					getSite().setSelectionProvider(tableViewer);
					
					// init the tooltip
					ServerToolTip toolTip = new ServerToolTip(tableViewer.getTree());
					toolTip.setShift(new Point(10, 3));
					toolTip.setPopupDelay(400); // in ms
					toolTip.setHideOnMouseDown(true);
					toolTip.activate();
					
				} catch (Exception e) {
					// ignore - view has already been closed
				}
			}
		});
		
		UpdateServerJob job = new UpdateServerJob(ServerCore.getServers());
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (tableViewer.getTree().getItemCount() > 0) {
							Object obj = tableViewer.getTree().getItem(0).getData();
							tableViewer.setSelection(new StructuredSelection(obj));
						}
					}
				});
			}
		});
		job.schedule();
	}
	
	protected void handlePublishChange(IServer server, boolean isPublishing) {
		String serverId = server.getId();
		if (isPublishing)
			publishing.add(serverId);
		else
			publishing.remove(serverId);
	
		refreshServerState(server);
	}
	
	/**
	 * @deprecated
	 * @param server
	 */
	protected void refreshServer(final IServer server){
		Trace.trace(Trace.FINEST, "Refreshing UI for server="+server);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {		
				IDecoratorManager dm = PlatformUI.getWorkbench().getDecoratorManager();
				dm.update("org.eclipse.wst.server.ui.navigatorDecorator");
				tableViewer.setSelection(tableViewer.getSelection());
			}
		});
	}
	
	protected void refreshServerContent(final IServer server){
		Trace.trace(Trace.FINEST, "Refreshing Content for server="+server);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(!tableViewer.getTree().isDisposed())
					tableViewer.refresh(server, true);
			}
		});
	}
	
	protected void refreshServerState(final IServer server){
		Trace.trace(Trace.FINEST, "Refreshing UI for server="+server);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {		
				IDecoratorManager dm = PlatformUI.getWorkbench().getDecoratorManager();
				dm.update("org.eclipse.wst.server.ui.navigatorDecorator");
				tableViewer.setSelection(tableViewer.getSelection());
			}
		});
	}
	
	protected void addListener(){
		// To enable the UI updating of servers and its childrens  
		serverResourceListener = new IServerLifecycleListener() {
			public void serverAdded(IServer server) {
				addServer(server);
				server.addServerListener(serverListener);
				((Server) server).addPublishListener(publishListener);
			}
			public void serverChanged(IServer server) {
				refreshServerContent(server);
			}
			public void serverRemoved(IServer server) {
				removeServer(server);
				server.removeServerListener(serverListener);
				((Server) server).removePublishListener(publishListener);
			}
		};
		ServerCore.addServerLifecycleListener(serverResourceListener);
		
		// To enable the refresh of the State decorator
		publishListener = new PublishAdapter() {
			public void publishStarted(IServer server) {
				handlePublishChange(server, true);
			}
			
			public void publishFinished(IServer server, IStatus status) {
				handlePublishChange(server, false);
			}
		};
		
		serverListener = new IServerListener() {
			public void serverChanged(ServerEvent event) {
				if (event == null)
					return;
								
				int eventKind = event.getKind();
				IServer server = event.getServer();
				if ((eventKind & ServerEvent.SERVER_CHANGE) != 0) {
					// server change event
					if ((eventKind & ServerEvent.STATE_CHANGE) != 0) {
						int state = event.getState();
						String id = server.getId();
						if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING) {
							boolean startThread = false;
							synchronized (starting) {
								if (!starting.contains(id)) {
									if (starting.isEmpty())
										startThread = true;
									starting.add(id);
								}
							}
							if (startThread)
								startThread();
						} else {
							boolean stopThread = false;
							synchronized (starting) {
								if (starting.contains(id)) {
									starting.remove(id);
									if (starting.isEmpty())
										stopThread = true;
								}
							}
							if (stopThread)
								stopThread();
						}
						refreshServerState(server);
						refreshServerContent(server);
					} 
				} else if ((eventKind & ServerEvent.MODULE_CHANGE) != 0) {
					// module change event
					if ((eventKind & ServerEvent.STATE_CHANGE) != 0 || (eventKind & ServerEvent.PUBLISH_STATE_CHANGE) != 0) {
						refreshServerContent(server);
					}
				}
				// TODO Angel Says: I don't think we need this
				//refreshServer(server);
			}
		};
		
		// add listeners to servers
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].addServerListener(serverListener);
				((Server) servers[i]).addPublishListener(publishListener);
			}
		}
	}
	
	protected void addServer(final IServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.add(tableViewer.getInput(), server);
			}
		});
	}

	protected void removeServer(final IServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.remove(server);
			}
		});
	}
	
	@Override
	public void dispose() {
		ServerCore.removeServerLifecycleListener(serverResourceListener);

		// remove listeners from servers
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].removeServerListener(serverListener);
				((Server) servers[i]).removePublishListener(publishListener);
			}
		}
		super.dispose();
	}
	
	/**
	 * Start the animation thread
	 */
	protected void startThread() {
		if (animationActive)
			return;
		
		stopAnimation = false;
		
		final Display display = tableViewer == null ? Display.getDefault() : tableViewer.getControl().getDisplay();
		final int SLEEP = 200;
		final Runnable[] animator = new Runnable[1];
		animator[0] = new Runnable() {
			public void run() {
				if (!stopAnimation) {
					try {
						int size = 0;
						String[] servers;
						synchronized (starting) {
							size = starting.size();
							servers = new String[size];
							starting.toArray(servers);
							
						}
						
						for (int i = 0; i < size; i++) {
							IServer server = ServerCore.findServer(servers[i]);
							if (server != null ) {
								ServerDecorator.animate();
								tableViewer.update(server, new String[]{"ICON"});
							}
						}
					} catch (Exception e) {
						Trace.trace(Trace.FINEST, "Error in Servers view animation", e);
					}
					display.timerExec(SLEEP, animator[0]);
				}
			}
		};
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				display.timerExec(SLEEP, animator[0]);
			}
		});
	}

	protected void stopThread() {
		stopAnimation = true;
	}
}
