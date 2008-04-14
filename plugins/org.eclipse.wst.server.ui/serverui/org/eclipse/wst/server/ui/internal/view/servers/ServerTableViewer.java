/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.viewers.BaseContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
/**
 * Tree view showing servers and their associations.
 */
public class ServerTableViewer extends TreeViewer {
	protected static final String ROOT = "root";
	protected static Color color;
	protected static Font font;
	protected Clipboard clipboard;

	protected IServerLifecycleListener serverResourceListener;
	protected IPublishListener publishListener;
	protected IServerListener serverListener;

	protected static Object deletedElement = null;

	// servers that are currently publishing and starting
	protected static Set<String> publishing = new HashSet<String>(4);
	protected static Set<String> starting = new HashSet<String>(4);

	protected ServerTableLabelProvider labelProvider;
	//protected ISelectionListener dsListener;

	protected ServersView view;

	protected boolean animationActive = false;
	protected boolean stopAnimation = false;

	public class ServerContentProvider extends BaseContentProvider implements ITreeContentProvider {
		public Object[] getElements(Object element) {
			List<IServer> list = new ArrayList<IServer>();
			IServer[] servers = ServerCore.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					if (!((Server)servers[i]).isPrivate())
						list.add(servers[i]);
				}
			}
			return list.toArray();
		}

		public Object[] getChildren(Object element) {
			if (element instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) element;
				try {
					IModule[] children = ms.server.getChildModules(ms.module, null);
					int size = children.length;
					ModuleServer[] ms2 = new ModuleServer[size];
					for (int i = 0; i < size; i++) {
						int size2 = ms.module.length;
						IModule[] module = new IModule[size2 + 1];
						System.arraycopy(ms.module, 0, module, 0, size2);
						module[size2] = children[i];
						ms2[i] = new ModuleServer(ms.server, module);
					}
					return ms2;
				} catch (Exception e) {
					return null;
				}
			}
			
			IServer server = (IServer) element;
			IModule[] modules = server.getModules(); 
			int size = modules.length;
			ModuleServer[] ms = new ModuleServer[size];
			for (int i = 0; i < size; i++) {
				ms[i] = new ModuleServer(server, new IModule[] { modules[i] });
			}
			return ms;
		}

		public Object getParent(Object element) {
			if (element instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) element;
				return ms.server;
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ModuleServer) {
				// Check if the module server has child modules.
				ModuleServer curModuleServer = (ModuleServer)element;
				IServer curServer = curModuleServer.server;
				IModule[] curModule = curModuleServer.module;
				if (curServer != null &&  curModule != null) {
					IModule[] curChildModule = curServer.getChildModules(curModule, null);
					if (curChildModule != null && curChildModule.length > 0)
						return true;
					
					return false;
				}
				
				return false;
			}
			
			IServer server = (IServer) element;
			return server.getModules().length > 0;
		}
	}

	protected void startThread() {
		if (animationActive)
			return;
		
		stopAnimation = false;
		
		final Display display = getTree().getDisplay();
		final int SLEEP = 200;
		final Runnable[] animator = new Runnable[1];
		animator[0] = new Runnable() {
			public void run() {
				if (!stopAnimation) {
					try {
						labelProvider.animate();
						
						int size = 0;
						String[] servers;
						synchronized (starting) {
							size = starting.size();
							servers = new String[size];
							starting.toArray(servers);
						}
						
						for (int i = 0; i < size; i++) {
							IServer server = ServerCore.findServer(servers[i]);
							if (server != null && getTree() != null && !getTree().isDisposed())
								updateAnimation(server);
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

	/**
	 * ServerTableViewer constructor comment.
	 * 
	 * @param view the view 
	 * @param tree the tree
	 */
	public ServerTableViewer(final ServersView view, final Tree tree) {
		super(tree);
		this.view = view;
		clipboard = new Clipboard(tree.getDisplay());
	}
	
	protected void initialize() {
		ColumnViewerToolTipSupport.enableFor(this);
		setContentProvider(new ServerContentProvider());
		labelProvider = new ServerTableLabelProvider();		
		setLabelProvider(labelProvider);		
		
		setComparator(new ServerViewerComparator(labelProvider));
		
		setInput(ROOT);
		addListeners();
		
		/*dsListener = new ISelectionListener() {
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
				if (proj == null && obj != null) {
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
					final IModule module = ServerUtil.getModule(project);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (getTree() == null || getTree().isDisposed())
								return;

							IServer defaultServer = null;
							if (module != null)
								defaultServer = ServerCore.getDefaultServer(module);
							
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
		view.getViewSite().getPage().addSelectionListener(dsListener);*/
		
		if (color == null) {
			Display display = getControl().getDisplay();
			color = display.getSystemColor(SWT.COLOR_DARK_GRAY);
			FontData[] fd = getControl().getFont().getFontData();
			int size = fd.length;
			for (int i = 0; i < size; i++)
				fd[i].setStyle(SWT.ITALIC);
			font = new Font(display, fd);
		}
	}

	protected void handleLabelProviderChanged(LabelProviderChangedEvent event) {
		Object[] obj = event.getElements();
		if (obj == null)
			refresh(true);
		else {
			obj = adaptLabelChangeObjects(obj);
			int size = obj.length;
			for (int i = 0; i < size; i++)
				update(obj[i], null);
		}
	}

	/**
	 * Resort the table based on field.
	 * 
	 * @param column the column being updated
	 * @param col
	 */
	protected void resortTable(final TreeColumn column, int col) {
		ServerViewerComparator sorter = (ServerViewerComparator) getComparator();
		
		if (col == sorter.getTopPriority())
			sorter.reverseTopPriority();
		else
			sorter.setTopPriority(col);
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(
			new Runnable() {
				public void run() {
					refresh();
					updateDirectionIndicator(column);
				}
			});
	}

	/**
	 * Update the direction indicator as column is now the primary column.
	 * 
	 * @param column
	 */
	protected void updateDirectionIndicator(TreeColumn column) {
		getTree().setSortColumn(column);
		if (((ServerViewerComparator) getComparator()).getTopPriorityDirection() == ServerViewerComparator.ASCENDING)
			getTree().setSortDirection(SWT.UP);
		else
			getTree().setSortDirection(SWT.DOWN);
	}

	protected Object[] adaptLabelChangeObjects(Object[] obj) {
		if (obj == null)
			return obj;
		
		List<Object> list = new ArrayList<Object>();
		int size = obj.length;
		for (int i = 0; i < size; i++) {
			if (obj[i] instanceof IModule) {
				list.add(obj[i]);
			} else if (obj[i] instanceof IServer) {
				list.add(obj[i]);
			} else if (obj[i] instanceof ModuleServer) {
				list.add(obj[i]);
			} else if (obj[i] instanceof IProject) {
				IProject proj = (IProject) obj[i];

				List<Object> list2 = new ArrayList<Object>();
				getTreeChildren(list2, view.treeTable);
				
				Iterator iterator = list2.iterator();
				while (iterator.hasNext()) {
					Object o = iterator.next();
					if (o instanceof ModuleServer) {
						ModuleServer ms = (ModuleServer) o;
						if (proj.equals(ms.module[ms.module.length - 1].getProject()))
							list.add(ms);
					}
				}
			}
		}
		
		Object[] o = new Object[list.size()];
		list.toArray(o);
		return o;
	}

	private void getTreeChildren(List<Object> list, Widget widget) {
		Item[] items = getChildren(widget);
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			Object data = item.getData();
			if (data != null)
				list.add(data);
			
			if (getExpanded(item)) {
				// only recurse if it is expanded - if
				// not then the children aren't visible
				getTreeChildren(list, item);
			}
		}
	}

	protected void addListeners() {
		serverResourceListener = new IServerLifecycleListener() {
			public void serverAdded(IServer server) {
				addServer(server);
				server.addServerListener(serverListener);
				((Server) server).addPublishListener(publishListener);
			}
			public void serverChanged(IServer server) {
				refreshServer(server);
			}
			public void serverRemoved(IServer server) {
				removeServer(server);
				server.removeServerListener(serverListener);
				((Server) server).removePublishListener(publishListener);
			}
		};
		ServerCore.addServerLifecycleListener(serverResourceListener);
		
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
						refreshServer(server);
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
					} else
						refreshServer(server);
				} else if ((eventKind & ServerEvent.MODULE_CHANGE) != 0) {
					// module change event
					if ((eventKind & ServerEvent.STATE_CHANGE) != 0 || (eventKind & ServerEvent.PUBLISH_STATE_CHANGE) != 0) {
						refreshServer(server);
					}
				}
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
		//if (dsListener != null)
		//	view.getViewSite().getPage().removeSelectionListener(dsListener);
		
		ServerCore.removeServerLifecycleListener(serverResourceListener);
		
		// remove listeners from server
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (serverListener != null)
					servers[i].removeServerListener(serverListener);
				if (publishListener != null)
					((Server) servers[i]).removePublishListener(publishListener);
			}
		}
		
		clipboard.dispose();
		
		super.handleDispose(event);
	}

	/**
	 * Called when the publish state changes.
	 * @param server org.eclipse.wst.server.core.IServer
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
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected void handleServerResourceAdded(IServer server) {
		add(null, server);
	}
	
	/*protected void handleServerResourceAdded(IServerConfiguration configuration) {
		configurationChange(configuration, true);
	}*/
	
	/**
	 * Called when an element is changed.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected void handleServerResourceChanged(IServer server) {
		refresh(server);
	}
	
	/*protected void handleServerResourceChanged(IServerConfiguration configuration) {
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				IServerConfiguration config = servers[i].getServerConfiguration();
				if (configuration.equals(config))
					refresh(servers[i]);
			}
		}
	}*/
	
	/**
	 * Called when an element is removed.
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	protected void handleServerResourceRemoved(IServer server) {
		remove(server);

		String serverId = server.getId();
		publishing.remove(serverId);

		view.getViewSite().getActionBars().getStatusLineManager().setMessage(null, null);
	}

	/*protected void handleServerResourceRemoved(IServerConfiguration configuration) {
		configurationChange(configuration, false);
	}*/

	protected void addServer(final IServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				add(ROOT, server);
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

	public void doUpdateItem(Widget widget, Object element, boolean fullMap) {
		if (widget instanceof TreeItem && color != null) {
			TreeItem item = (TreeItem) widget;
			if (element instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) element;
				IModule m = ms.module[ms.module.length-1];
				if (m.isExternal())
					item.setForeground(color);
				else
					item.setForeground(null);
				if (ms.server.getModulePublishState(ms.module) != IServer.PUBLISH_STATE_NONE)
					item.setFont(0, font);
				else
					item.setFont(0, null);
			}
		}
		super.doUpdateItem(widget, element, fullMap);
	}

	protected void updateAnimation(IServer server) {
		try {
			Widget widget = doFindItem(server);
			TreeItem item = (TreeItem) widget;
			item.setText(1, labelProvider.getColumnText(server, 1));
			item.setImage(1, labelProvider.getColumnImage(server, 1));
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error in optimized animation", e);
		}
	}
}