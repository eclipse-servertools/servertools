/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers.provisional;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.UpdateServerJob;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerToolTip;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.actions.NewServerWizardAction;
import org.eclipse.wst.server.ui.internal.view.servers.CopyAction;
import org.eclipse.wst.server.ui.internal.view.servers.DeleteAction;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleSloshAction;
import org.eclipse.wst.server.ui.internal.view.servers.MonitorServerPortAction;
import org.eclipse.wst.server.ui.internal.view.servers.OpenAction;
import org.eclipse.wst.server.ui.internal.view.servers.PasteAction;
import org.eclipse.wst.server.ui.internal.view.servers.PropertiesAction;
import org.eclipse.wst.server.ui.internal.view.servers.PublishAction;
import org.eclipse.wst.server.ui.internal.view.servers.PublishCleanAction;
import org.eclipse.wst.server.ui.internal.view.servers.RemoveModuleAction;
import org.eclipse.wst.server.ui.internal.view.servers.RenameAction;
import org.eclipse.wst.server.ui.internal.view.servers.RestartModuleAction;
import org.eclipse.wst.server.ui.internal.view.servers.ShowInConsoleAction;
import org.eclipse.wst.server.ui.internal.view.servers.ShowInDebugAction;
import org.eclipse.wst.server.ui.internal.view.servers.StartAction;
import org.eclipse.wst.server.ui.internal.view.servers.StartModuleAction;
import org.eclipse.wst.server.ui.internal.view.servers.StopAction;
import org.eclipse.wst.server.ui.internal.view.servers.StopModuleAction;
/**
 * A view of servers, their modules, and status.
 */
public class ServersView extends CommonNavigator {
	
	protected CommonViewer tableViewer;
	
	protected Clipboard clipboard;
	
	protected Action noneAction = new Action(Messages.dialogMonitorNone) {
		// dummy action
	};
	
	// actions on a server
	protected Action[] actions;
	protected Action actionModifyModules;
	protected Action openAction, showInConsoleAction, showInDebugAction, propertiesAction, monitorPropertiesAction;
	protected Action copyAction, pasteAction, deleteAction, renameAction;
	
	/**
	 * ServersView constructor comment.
	 */
	public ServersView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		clipboard = new Clipboard(getCommonViewer().getControl().getDisplay());
		
		deferInitialization();
	}
	
	private void deferInitialization() {
		TreeItem item = new TreeItem(getCommonViewer().getTree(), SWT.NONE);
		item.setText(Messages.viewInitializing);
			
		initializeActions(getCommonViewer());
		
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
		// TODO Angel says: What to do here? 
		//tableViewer.initialize();				
		
		// TODO Angel says: This probably shouldn't be here 
		tableViewer = getCommonViewer();
		
		tableViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				try {
					IStructuredSelection sel = (IStructuredSelection) event.getSelection();
					Object data = sel.getFirstElement();
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
		final Shell shell = tableViewer.getTree().getShell();
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(shell, mgr);
			}
		});
		Menu menu = menuManager.createContextMenu(tableViewer.getTree());
		tableViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);
		
		//TODO Angel: What to do here?
		//initDragAndDrop();
		
		// init the tooltip
		ServerToolTip toolTip = new ServerToolTip(tableViewer.getTree());
		toolTip.setShift(new Point(10, 3));
		toolTip.setPopupDelay(400); // in ms
		toolTip.setHideOnMouseDown(true);
		toolTip.activate();
		
		if (tableViewer.getTree().getItemCount() > 0) {
			Object obj = tableViewer.getTree().getItem(0).getData();
			tableViewer.setSelection(new StructuredSelection(obj));
		}
		
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					// ignore
				}
				IServer[] servers = ServerCore.getServers();
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					IServer server = servers[i];
					if (server.getServerType() != null && server.getServerState() == IServer.STATE_UNKNOWN) {
						UpdateServerJob job = new UpdateServerJob(server);
						job.schedule();
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY + 1);
		thread.start();
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
		fillNewContextMenu(null, selection, newMenu);
		menu.add(newMenu);
		
		// open action
		if (server != null && module == null) {
			menu.add(openAction);
			
			String text = Messages.actionShowIn;
			final IWorkbench workbench = PlatformUI.getWorkbench();
			final IBindingService bindingService = (IBindingService) workbench
					.getAdapter(IBindingService.class);
			final TriggerSequence[] activeBindings = bindingService
					.getActiveBindingsFor("org.eclipse.ui.navigate.showInQuickMenu");
			if (activeBindings.length > 0) {
				text += "\t" + activeBindings[0].format();
			}
			
			MenuManager showInMenu = new MenuManager(text);
			showInMenu.add(showInConsoleAction);
			showInMenu.add(showInDebugAction);
			//IActionBars actionBars = getViewSite().getActionBars();
			//actionBars.setGlobalActionHandler("group.show", showInMenu);
			menu.add(showInMenu);
			menu.add(new Separator());
		} else
			menu.add(new Separator());
		
		if (server != null) {
			if (module == null) {
				menu.add(copyAction);
				menu.add(pasteAction);
				menu.add(deleteAction);
				menu.add(renameAction);
			} else if (module.length == 1)
				menu.add(new RemoveModuleAction(shell, server, module[0]));
			menu.add(new Separator());
		}
		
		if (server != null && module == null) {
			// server actions
			for (int i = 0; i < actions.length; i++)
				menu.add(actions[i]);
			
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
						
						menuManager.add(new Separator());
						menuManager.add(monitorPropertiesAction);
					}
				});
				
				// add an initial menu item so that the menu appears correctly
				noneAction.setEnabled(false);
				menuManager.add(noneAction);
				menu.add(menuManager);
			}
		}
		
		if (server != null && module != null) {
			menu.add(new Separator());
			menu.add(new StartModuleAction(server, module));
			menu.add(new StopModuleAction(server, module));			
			menu.add(new RestartModuleAction(server, module));
		}
		
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));
		
		if (server != null) {
			menu.add(new Separator());
			menu.add(propertiesAction);
		}
	}

	
	private static void fillNewContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		IAction newServerAction = new NewServerWizardAction();
		newServerAction.setText(Messages.actionNewServer);
		menu.add(newServerAction);
	}
	
	
	/**
	 * Initialize actions
	 * 
	 * @param provider a selection provider
	 */
	public void initializeActions(ISelectionProvider provider) {
		Shell shell = getSite().getShell();
		IActionBars actionBars = getViewSite().getActionBars();
		
		actions = new Action[6];
		// create the start actions
		actions[0] = new StartAction(shell, provider, ILaunchManager.DEBUG_MODE);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.debug", actions[0]);
		actions[1] = new StartAction(shell, provider, ILaunchManager.RUN_MODE);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.run", actions[1]);
		actions[2] = new StartAction(shell, provider, ILaunchManager.PROFILE_MODE);
		
		// create the stop action
		actions[3] = new StopAction(shell, provider);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.stop", actions[3]);
		
		// create the publish actions
		actions[4] = new PublishAction(shell, provider);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.publish", actions[4]);
		actions[5] = new PublishCleanAction(shell, provider);
		
		// create the open action
		openAction = new OpenAction(provider);
		actionBars.setGlobalActionHandler("org.eclipse.ui.navigator.Open", openAction);
		
		// create copy, paste, and delete actions
		pasteAction = new PasteAction(shell, provider,  clipboard);
		copyAction = new CopyAction(provider, clipboard, pasteAction);
		deleteAction = new DeleteAction(shell, provider);
		renameAction = new RenameAction(shell, getCommonViewer(), provider);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), renameAction);
		
		// create the other actions
		actionModifyModules = new ModuleSloshAction(shell, provider);
		showInConsoleAction = new ShowInConsoleAction(provider);
		showInDebugAction = new ShowInDebugAction(provider);
		
		// create the properties action
		propertiesAction = new PropertiesAction(shell, provider);
		actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertiesAction);
		monitorPropertiesAction = new PropertiesAction(shell, "org.eclipse.wst.server.ui.properties.monitor", provider);
		
		// add toolbar buttons
		IContributionManager cm = actionBars.getToolBarManager();
		for (int i = 0; i < actions.length - 1; i++)
			cm.add(actions[i]);
		
		cm.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
}
