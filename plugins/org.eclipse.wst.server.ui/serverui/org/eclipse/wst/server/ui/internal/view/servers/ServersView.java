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

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
/**
 * View of server, their configurations and status.
 */
public class ServersView extends ViewPart {
	private static final String TAG_COLUMN_WIDTH = "columnWidth";

	protected Action noneAction = new Action(Messages.dialogMonitorNone) {
		// dummy action
	};

	protected int[] cols;

	protected Tree treeTable;
	protected ServerTableViewer tableViewer;

	// actions on a server
	protected Action[] actions;
	protected Action actionModifyModules;
	protected Action actionProperties;
	protected MenuManager restartMenu;

	/**
	 * ServersView constructor comment.
	 */
	public ServersView() {
		super();
	}

	/**
	 * createPartControl method comment.
	 * 
	 * @param parent a parent composite
	 */
	public void createPartControl(Composite parent) {
		treeTable = new Tree(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
		treeTable.setHeaderVisible(true);
		treeTable.setLinesVisible(false);
		treeTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeTable.setFont(parent.getFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(treeTable, ContextIds.VIEW_SERVERS);
		
		// add columns
		TreeColumn column = new TreeColumn(treeTable, SWT.SINGLE);
		column.setText(Messages.viewServer);
		column.setWidth(cols[0]);
		
		TreeColumn column2 = new TreeColumn(treeTable, SWT.SINGLE);
		column2.setText(Messages.viewState);
		column2.setWidth(cols[1]);
		
		TreeColumn column3 = new TreeColumn(treeTable, SWT.SINGLE);
		column3.setText(Messages.viewStatus);
		column3.setWidth(cols[2]);
		
		deferInitialization();
	}

	private void deferInitialization() {
		TreeItem item = new TreeItem(treeTable, SWT.NONE);
		item.setText(Messages.viewInitializing);
		
		tableViewer = new ServerTableViewer(this, treeTable);
		initializeActions(tableViewer);
		
		Job job = new Job(Messages.jobInitializingServersView) {
			public IStatus run(IProgressMonitor monitor) {
				IServer[] servers = ServerCore.getServers();
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					((Server)servers[i]).getAllModules().iterator();
					/*while (iterator.hasNext()) {
						Module module = (Module) iterator.next();
						module.g
					}*/
				}
				
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							deferredInitialize();
						} catch (Exception e) {
							// ignore - view has already been closed
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		
		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	protected void deferredInitialize() {
		tableViewer.initialize();
		
		treeTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					/*TableItem item = table.getSelection()[0];
					IServerResource resource = (IServerResource) item.getData();
					IServerResourceFactory factory = ServerUtil.getServerResourceFactory(resource);
					String label = ServerLabelProvider.getInstance().getText(factory);
					label += " (";
					label += ServerCore.getResourceManager().getServerResourceLocation(resource).getFullPath().toString().substring(1);
					label += ")";
					getViewSite().getActionBars().getStatusLineManager().setMessage(ServerLabelProvider.getInstance().getImage(factory), label);
					
					if (resource instanceof IServer) {
						IServer server = (IServer) resource;
						ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
						ILaunch[] launches = launchManager.getLaunches();
						int size = launches.length;
						for (int i = size-1; i >= 0; i--) {
							ILaunchConfiguration config = launches[i].getLaunchConfiguration();
							if (LAUNCH_CONFIGURATION_TYPE_ID.equals(config.getType().getIdentifier()) &&
									ServerCore.getServerRef(server).equals(config.getAttribute(SERVER_REF, (String)null))) {
								selectServerProcess(launches[i]);
								return;
							}
						}
					}*/
				} catch (Exception e) {
					getViewSite().getActionBars().getStatusLineManager().setMessage(null, "");
				}
			}
			public void widgetDefaultSelected(SelectionEvent event) {
				try {
					TreeItem item = treeTable.getSelection()[0];
					Object data = item.getData();
					if (!(data instanceof IServer))
						return;
					IServer server = (IServer) data;
					ServerUIPlugin.editServer(server);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not open server", e);
				}
			}
		});
		
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		final Shell shell = treeTable.getShell();
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(shell, mgr);
			}
		});
		Menu menu = menuManager.createContextMenu(treeTable);
		treeTable.setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);
		
		initDragAndDrop();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		cols = new int[3];
		for (int i = 0; i < 3; i++) {
			cols[i] = 200;
			if (memento != null) {
				Integer in = memento.getInteger(TAG_COLUMN_WIDTH + i);
				if (in != null && in.intValue() > 5)
					cols[i] = in.intValue();
			}
		}
	}

	public void saveState(IMemento memento) {
		TreeColumn[] tc = treeTable.getColumns();
		for (int i = 0; i < 3; i++) {
			int width = tc[i].getWidth();
			if (width != 0)
				memento.putInteger(TAG_COLUMN_WIDTH + i, width);
		}
	}

	protected void selectServerProcess(Object process) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IWorkbenchPart part = page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
				if (part != null) {
					IDebugView view = (IDebugView)part.getAdapter(IDebugView.class);
					if (view != null) {
						Viewer viewer = view.getViewer();
						if (viewer != null) {
							viewer.setSelection(new StructuredSelection(process));
						}
					}
				}
			}
		}
	}

	/**
	 * Initialize actions
	 * 
	 * @param provider a selection provider
	 */
	public void initializeActions(ISelectionProvider provider) {
		Shell shell = getSite().getShell();
		
		// create the restart menu
		restartMenu = new MenuManager(Messages.actionRestart);
		restartMenu.add(new RestartAction(shell, provider, ILaunchManager.DEBUG_MODE));
		restartMenu.add(new RestartAction(shell, provider, ILaunchManager.RUN_MODE));
		restartMenu.add(new RestartAction(shell, provider, ILaunchManager.PROFILE_MODE));
		
		actions = new Action[6];
		// create the start actions
		actions[0] = new StartAction(shell, provider, ILaunchManager.DEBUG_MODE);
		actions[1] = new StartAction(shell, provider, ILaunchManager.RUN_MODE);
		actions[2] = new StartAction(shell, provider, ILaunchManager.PROFILE_MODE);
		
		// create the stop action
		actions[3] = new StopAction(shell, provider);
		
		// create the publish actions
		actions[4] = new PublishAction(shell, provider);
		actions[5] = new PublishCleanAction(shell, provider);
		
		// create the module slosh dialog action
		actionModifyModules = new ModuleSloshAction(shell, provider);
		
		// create the properties action
		actionProperties = new PropertiesAction(shell, provider);
		
		// add toolbar buttons
		IContributionManager cm = getViewSite().getActionBars().getToolBarManager();
		for (int i = 0; i < actions.length - 1; i++)
			cm.add(actions[i]);
	}

	protected void fillContextMenu(Shell shell, IMenuManager menu) {
		// get selection but avoid no selection or multiple selection
		IServer server = null;
		IModule[] module = null;
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		if (!selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			Object obj = iterator.next();
			if (obj instanceof IServer)
				server = (IServer) obj;
			if (obj instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) obj;
				server = ms.server;
				module = ms.module;
			}
			if (iterator.hasNext()) {
				server = null;
				module = null;
			}
		}
		
		// new action
		MenuManager newMenu = new MenuManager(Messages.actionNew);
		ServerActionHelper.fillNewContextMenu(null, selection, newMenu);
		menu.add(newMenu);
		
		// open action
		if (server != null && module == null) {
			menu.add(new OpenAction(server));
			menu.add(new UpdateStatusAction(server));
			menu.add(new Separator());
		} else
			menu.add(new Separator());
		
		if (server != null) {
			if (module == null) {
				menu.add(new DeleteAction(shell, server));
				menu.add(new RenameAction(shell, tableViewer, tableViewer));
			} else if (module.length == 1)
				menu.add(new RemoveModuleAction(shell, server, module[0]));
			menu.add(new Separator());
		}
		
		if (server != null && module == null) {
			// server actions
			for (int i = 0; i < actions.length; i++) {
				if (i == 3) // insert restart menu
					menu.add(restartMenu);
				menu.add(actions[i]);
			}
			
			menu.add(new Separator());
			menu.add(actionModifyModules);
			
			// monitor
			if (server.getServerType() != null) {
				final MenuManager menuManager = new MenuManager(Messages.actionMonitor);
				
				final IServer server2 = server;
				final Shell shell2 = shell;
				menuManager.addMenuListener(new IMenuListener() {
					public void menuAboutToShow(IMenuManager manager) {
						menuManager.removeAll();
						if (server2.getAdapter(ServerDelegate.class) != null) {
							ServerPort[] ports = server2.getServerPorts(null);
							if (ports != null) {
								int size = ports.length;
								for (int i = 0; i < size; i++) {
									if (!ports[i].isAdvanced())
										menuManager.add(new MonitorServerPortAction(shell2, server2, ports[i]));
								}
							}
						}
						
						if (menuManager.isEmpty())
							menuManager.add(noneAction);
					}
				});
				
				// add an initial menu item so that the menu appears correctly
				noneAction.setEnabled(false);
				menuManager.add(noneAction);
				menu.add(menuManager);
			}
			menu.add(new SwitchServerLocationAction(server));
		}
		
		if (server != null && module != null) {
			menu.add(new Separator());
			menu.add(new RestartModuleAction(server, module));
		}
		
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));
		
		if (server != null) {
			menu.add(new Separator());
			menu.add(actionProperties);
		}
	}

	/**
	 * 
	 */
	public void setFocus() {
		if (treeTable != null)
			treeTable.setFocus();
	}

	/**
    * Adds drag and drop support to the Servers view.
    */
   protected void initDragAndDrop() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getInstance(),
			ResourceTransfer.getInstance(), FileTransfer.getInstance() };
		//tableViewer.addDragSupport(ops, transfers, new ServersViewDragAdapter(viewer));
		tableViewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, new ServersViewDropAdapter(tableViewer));
   }
}