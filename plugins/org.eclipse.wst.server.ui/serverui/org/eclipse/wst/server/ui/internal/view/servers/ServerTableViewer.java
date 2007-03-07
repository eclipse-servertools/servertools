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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
/**
 * Tree view showing servers and their associations.
 */
public class ServerTableViewer extends TreeViewer {
	protected static final String ROOT = "root";
	protected static Color color;
	protected static Font font;

	protected IServerLifecycleListener serverResourceListener;
	protected IPublishListener publishListener;
	protected IServerListener serverListener;

	protected static Object deletedElement = null;

	// servers that are currently publishing and starting
	protected static List publishing = new ArrayList();
	protected static List starting = new ArrayList();

	protected ServerTableLabelProvider labelProvider;
	//protected ISelectionListener dsListener;

	protected ServersView view;

	protected boolean animationActive = false;
	protected boolean stopAnimation = false;

	public class ServerContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		public Object[] getElements(Object element) {
			List list = new ArrayList();
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

		public void inputChanged(Viewer theViewer, Object oldInput, Object newInput) {
			// do nothing
		}
		
		public void dispose() {
			// do nothing
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

	/*protected void createHoverHelp(Shell parent, Point p) {
		if (fShell != null) {
			fShell.dispose();
			fShell = null;
		}
		fShell = new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP | SWT.RESIZE | SWT.NO_TRIM);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		fShell.setLayout(layout);
		
		Display display = parent.getDisplay();
		StyledText text = new StyledText(fShell, SWT.NONE);
		text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		text.append("Testing\nThis is multi-line");
		
		fShell.setLocation(p.x, p.y);
		fShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		fShell.pack();
		fShell.setVisible(true);
	}*/

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
						final Object[] rootElements = ((ITreeContentProvider)getContentProvider()).getElements(null); 
						if (getTree() != null && !getTree().isDisposed())
							update(rootElements, null);
					} catch (Exception e) {
						Trace.trace(Trace.FINEST, "Error in Servers view animation", e);
					}
					display.timerExec(SLEEP, animator[0]);
				}
			}
		};
		Display.getDefault().syncExec(new Runnable() {
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
	}
	
	protected void initialize() {
		/*tree.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (fShell != null) {
					fShell.dispose();
					fShell = null;
				}
			}
		});
		tree.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent event) {
				// ignore
			}

			public void mouseExit(MouseEvent event) {
				// ignore
			}

			public void mouseHover(MouseEvent event) {
				createHoverHelp(tree.getShell(), tree.toDisplay(event.x, event.y));
			}
		});*/
		
		setContentProvider(new ServerContentProvider());
		labelProvider = new ServerTableLabelProvider();
		labelProvider.addListener(new ILabelProviderListener() {
			public void labelProviderChanged(LabelProviderChangedEvent event) {
				Object[] obj = event.getElements();
				if (obj == null)
					refresh(true);
				else {
					obj = adaptLabelChangeObjects(obj);
					int size = obj.length;
					for (int i = 0; i < size; i++)
						refresh(obj[i], true);
				}
			}
		});
		setLabelProvider(labelProvider);
		setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IServer && e2 instanceof IServer) {
					IServer s1 = (IServer) e1;
					IServer s2 = (IServer) e2;
					return (s1.getName().compareToIgnoreCase(s2.getName()));
				} else if (e1 instanceof ModuleServer && e2 instanceof ModuleServer) {
					ModuleServer s1 = (ModuleServer) e1;
					ModuleServer s2 = (ModuleServer) e2;
					return (s1.module[s1.module.length - 1].getName().compareToIgnoreCase(s2.module[s2.module.length - 1].getName()));
				}
				
				return super.compare(viewer, e1, e2);
			}
		});
		
		setInput(ROOT);
		addListeners();
		IActionBars actionBars = view.getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new ServerAction(getControl().getShell(), this, "Delete it!", ServerActionHelper.ACTION_DELETE));
		
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
		
		//if (getTree().getItemCount() > 0)
		//	getTree().setSelection(getTree().getItem(0));
		
		if (getTree().getItemCount() > 0)
			this.setSelection(new StructuredSelection(getTree().getItem(0).getData()));
		
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

	protected Object[] adaptLabelChangeObjects(Object[] obj) {
		if (obj == null)
			return obj;
		
		List list = new ArrayList();
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

				List list2 = new ArrayList();
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

	private void getTreeChildren(List list, Widget widget) {
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
				if (event == null) {
					return;
				}
				int eventKind = event.getKind();
				IServer server = event.getServer();
				if ((eventKind & ServerEvent.SERVER_CHANGE) != 0) {
					// server change event
					if ((eventKind & ServerEvent.STATE_CHANGE) != 0) {
						refreshServer(server);
						int state = event.getState();
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
				servers[i].removeServerListener(serverListener);
				((Server) servers[i]).removePublishListener(publishListener);
			}
		}
	
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

	/*public void doUpdateItem(Widget widget, Object element, boolean fullMap) {
		if (widget instanceof TreeItem && color != null) {
			TreeItem item = (TreeItem) widget;
			if (element instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) element;
				IModule m = ms.module[ms.module.length-1];
				if ("external".equals(m.getId()))
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
	}*/
}