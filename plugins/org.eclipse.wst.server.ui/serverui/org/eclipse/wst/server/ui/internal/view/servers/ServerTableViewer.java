/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.*;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IServerListener;
import org.eclipse.wst.server.core.model.IServerResourceListener;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.core.util.ServerResourceAdapter;
import org.eclipse.wst.server.ui.internal.ServerStartupListener;
import org.eclipse.wst.server.ui.internal.ServerTree;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.view.tree.ServerTreeAction;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Tree view showing servers and their associations.
 */
public class ServerTableViewer extends TableViewer {
	protected static final String ROOT = "root";

	protected IServerResourceListener serverResourceListener;
	protected IPublishListener publishListener;
	protected IServerListener serverListener;

	protected static IElement deletedElement = null;

	// servers that are currently publishing and starting
	protected static List publishing = new ArrayList();
	protected static List starting = new ArrayList();
	
	protected ServerTableLabelProvider labelProvider;
	protected ISelectionListener dsListener;

	protected static Map startupWatch = new HashMap();

	protected ServersView view;
	
	public class ServerContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			List list = new ArrayList();
			IServer[] servers = ServerCore.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					if (!servers[i].isPrivate())
						list.add(servers[i]);
				}
			}
			return list.toArray();
		}

		public void inputChanged(Viewer theViewer, Object oldInput, Object newInput) {
			// do nothing
		}
		
		public void dispose() {
			// do nothing
		}
	}

	/*protected void createHover(Shell parent, Point p) {
		final Shell fShell = new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP | SWT.RESIZE | SWT.NO_TRIM);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		fShell.setLayout(layout);
		
		Display display = parent.getDisplay();
		StyledText text = new StyledText(fShell, SWT.NONE);
		text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		text.append("Testing <b>me</b>");
		
		fShell.setLocation(p.x, p.y);
		fShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		fShell.pack();
		fShell.setVisible(true);
		
		Thread t = new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (Exception e) { }
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						fShell.dispose();
					}
				});
			}
		};
		t.start();
	}*/
	
	protected Thread thread = null;
	protected boolean stopThread = false;
	
	protected void startThread() {
		stopThread = false;
		if (thread != null)
			return;
		
		thread = new Thread("Servers view animator") {
			public void run() {
				while (!stopThread) {
					try {
						labelProvider.animate();
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if (getTable() != null && !getTable().isDisposed())
									refresh();
							}
						});
						Thread.sleep(200);
					} catch (Exception e) {
						Trace.trace(Trace.FINEST, "Error in animated server view", e);
					}
					thread = null;
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	protected void stopThread() {
		stopThread = true;
	}
	
	/**
	 * ServerTableViewer constructor comment.
	 */
	public ServerTableViewer(final ServersView view, final Table table) {
		super(table);
		this.view = view;
		/*table.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent event) {
			}

			public void mouseExit(MouseEvent event) {
			}

			public void mouseHover(MouseEvent event) {
				createHover(table.getShell(), table.toDisplay(event.x, event.y));
			}
		});*/
	
		setContentProvider(new ServerContentProvider());
		labelProvider = new ServerTableLabelProvider();
		setLabelProvider(labelProvider);
		setSorter(new ViewerSorter() {
			// empty
		});
	
		setInput(ROOT);
	
		addListeners();
		
		IActionBars actionBars = view.getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new ServerTreeAction(getControl().getShell(), this, "Delete it!", ServerTree.ACTION_DELETE));
		
		dsListener = new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if (!(selection instanceof IStructuredSelection))
					return;
				IStructuredSelection sel = (IStructuredSelection) selection;
				final Object obj = sel.getFirstElement();
				IProject proj = null;
				if (obj instanceof IResource) {
					IResource res = (IResource) obj;
					proj = res.getProject();
				}
				if (proj == null) {
					try {
						IResource res = (IResource) Platform.getAdapterManager().getAdapter(obj, IResource.class);
						if (res != null)
							proj = res.getProject();
					} catch (Exception e) {
						// ignore
					}
				}
				if (proj != null) {
					final IProject project = proj;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (getTable() == null || getTable().isDisposed())
								return;

							IServer defaultServer = null;
							if (project != null) {
								IProjectProperties props = ServerCore.getProjectProperties(project);
								defaultServer = props.getDefaultServer();
							}
							IServer oldDefaultServer = labelProvider.getDefaultServer();
							if ((oldDefaultServer == null && defaultServer == null)
									|| (oldDefaultServer != null && oldDefaultServer.equals(defaultServer)))
								return;
							labelProvider.setDefaultServer(defaultServer);
							
							if (oldDefaultServer != null)
								refresh(oldDefaultServer);
							if (defaultServer != null)
								refresh(defaultServer);
						}
					});
				}
			}
		};
		view.getViewSite().getPage().addSelectionListener(dsListener);
	}

	protected void addListeners() {
		serverResourceListener = new ServerResourceAdapter() {
			public void serverAdded(IServer server) {
				addServer(server);
				server.addServerListener(serverListener);
				server.addPublishListener(publishListener);
			}
			public void serverChanged(IServer server) {
				refreshServer(server);
			}
			public void serverRemoved(IServer server) {
				removeServer(server);
				server.removeServerListener(serverListener);
				server.removePublishListener(publishListener);
			}
		};
		ServerCore.addResourceListener(serverResourceListener);
		
		publishListener = new PublishAdapter() {
			public void moduleStateChange(IServer server, List parents, IModule module) {
				refreshServer(server);
			}
			
			public void publishStarting(IServer server, List[] parents, IModule[] module) {
				handlePublishChange(server, true);
			}
			
			public void publishFinished(IServer server, IPublishStatus globalStatus) {
				handlePublishChange(server, false);
			}
		};
		
		serverListener = new IServerListener() {
			public void serverStateChange(IServer server) {
				refreshServer(server);
				int state = server.getServerState();
				String id = server.getId();
				if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING) {
					if (!starting.contains(id)) {
						if (starting.isEmpty())
							startThread();
						starting.add(id);
					}
				} else {
					if (starting.contains(id)) {
						starting.remove(id);
						if (starting.isEmpty())
							stopThread();
					}
				}
			}
			public void configurationSyncStateChange(IServer server) {
				refreshServer(server);
			}
			public void restartStateChange(IServer server) {
				refreshServer(server);
			}
			public void modulesChanged(IServer server) {
				handleServerModulesChanged(server);
			}
			public void moduleStateChange(IServer server, IModule module) {
				// do nothing
			}
		};
		
		// add listeners to servers
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].addServerListener(serverListener);
				servers[i].addPublishListener(publishListener);
			}
		}
	}

	/**
	 * Respond to a configuration being added or deleted.
	 * @param configuration org.eclipse.wst.server.core.model.IServerConfiguration
	 * @param add boolean
	 */
	protected void configurationChange(IServerConfiguration configuration, boolean add) {
		if (configuration == null)
			return;

		if (!add)
			deletedElement = configuration;
	
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (configuration.equals(servers[i].getServerConfiguration()))
					refresh(servers[i]);
			}
		}
		deletedElement = null;
	}
	
	protected void refreshServer(final IServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					refresh(server);
					ISelection sel = ServerTableViewer.this.getSelection();
					ServerTableViewer.this.setSelection(sel);
				} catch (Exception e) {
					// ignore
				}
			}
		});
	}

	protected void handleDispose(DisposeEvent event) {
		stopThread();
		view.getViewSite().getPage().removeSelectionListener(dsListener);

		ServerCore.removeResourceListener(serverResourceListener);
		
		// remove listeners from server
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].removeServerListener(serverListener);
				servers[i].removePublishListener(publishListener);
			}
		}
	
		super.handleDispose(event);
	}

	/**
	 * Called when the publish state changes.
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handlePublishChange(IServer server, boolean isPublishing) {
		String serverId = server.getId();
		if (isPublishing)
			publishing.add(serverId);
		else
			publishing.remove(serverId);
	
		refreshServer(server);
	}
	
	/**
	 * 
	 */
	protected void handleServerModulesChanged(IServer server2) {
		if (server2 == null)
			return;

		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (server2.equals(servers[i]))
					refresh(servers[i]);
			}
		}
	}
	
	/**
	 * Called when an element is added.
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerResourceAdded(IElement element) {
		if (element instanceof IServer) {
			IServer server = (IServer) element;
			add(server);

		} else if (element instanceof IServerConfiguration) {
			IServerConfiguration configuration = (IServerConfiguration) element;
			configurationChange(configuration, true);
		}
	}
	
	/**
	 * Called when an element is changed.
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerResourceChanged(IElement element) {
		if (element instanceof IServer) {
			refresh(element);
		} else if (element instanceof IServerConfiguration) {
			IServer[] servers = ServerCore.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					IServerConfiguration config = servers[i].getServerConfiguration();
					if (element.equals(config))
						refresh(servers[i]);
				}
			}
		}
	}
	
	/**
	 * Called when an element is removed.
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerResourceRemoved(IElement element) {
		if (element instanceof IServer) {
			IServer server = (IServer) element;
			remove(server);
	
			String serverId = server.getId();
			publishing.remove(serverId);
	
			view.getViewSite().getActionBars().getStatusLineManager().setMessage(null, null);
		} else if (element instanceof IServerConfiguration) {
			IServerConfiguration configuration = (IServerConfiguration) element;
			configurationChange(configuration, false);
		}
	}
	
	/**
	 * Register a startup listener.
	 *
	 * @param server org.eclipse.wst.server.core.model.IServer
	 * @param listener org.eclipse.wst.server.core.internal.ServerStartupListener
	 */
	protected static void registerStartupListener(IServer server, ServerStartupListener listener) {
		String id = server.getId();
		startupWatch.put(id, listener);
	}
	
	/**
	 * Remove a startup listener.
	 *
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	protected static void removeStartupListener(IServer server) {
		/*String ref = ServerCore.getServerRef(server);
		try {
			ServerStartupListener listener = (ServerStartupListener) startupWatch.get(ref);
			if (listener != null)
				listener.setEnabled(false);
		} catch (Exception e) {
		}
	
		startupWatch.remove(ref);*/
	}
	
	protected void addServer(final IServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				add(server);
			}
		});
	}
	
	protected void removeServer(final IServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				remove(server);
			}
		});
	}
}