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

import java.util.Iterator;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.actions.ServerAction;
import org.eclipse.wst.server.ui.internal.view.tree.DisabledMenuManager;
import org.eclipse.wst.server.ui.internal.view.tree.SwitchConfigurationAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.*;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.part.ViewPart;
/**
 * View of server, their configurations and status.
 */
public class ServersView extends ViewPart {
	//private static final String LAUNCH_CONFIGURATION_TYPE_ID = "org.eclipse.wst.server.core.launchConfigurationTypes";

	protected Table table;
	protected ServerTableViewer tableViewer;

	// actions on a server
	protected Action[] actions;
	protected MenuManager restartMenu;

	/**
	 * ServersView constructor comment.
	 */
	public ServersView() {
		super();
	}
	
	/**
	 * createPartControl method comment.
	 */
	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setFont(parent.getFont());
		WorkbenchHelp.setHelp(table, ContextIds.VIEW_CONTROL);
		
		TableLayout tableLayout = new TableLayout();
	
		// add columns
		TableColumn column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%viewServer"));
		ColumnWeightData colData = new ColumnWeightData(200, 200, true);
		tableLayout.addColumnData(colData);
		
		column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%viewHost"));
		colData = new ColumnWeightData(100, 150, true);
		tableLayout.addColumnData(colData);
	
		column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%viewStatus"));
		colData = new ColumnWeightData(100, 150, true);
		tableLayout.addColumnData(colData);
	
		column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%viewSync"));
		colData = new ColumnWeightData(100, 150, true);
		tableLayout.addColumnData(colData);
		
		table.setLayout(tableLayout);
	
		tableViewer = new ServerTableViewer(this, table);
		initializeActions(tableViewer);
	
		table.addSelectionListener(new SelectionAdapter() {
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
					TableItem item = table.getSelection()[0];
					IServer server = (IServer) item.getData();
					ServerUIUtil.editServer(server, server.getServerConfiguration());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not open server", e);
				}
			}
		});
		
		MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		final Shell shell = table.getShell();
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(shell, mgr);
			}
		});
		Menu menu = menuManager.createContextMenu(parent);
		table.setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
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
	 */
	public void initializeActions(ISelectionProvider provider) {
		Shell shell = getSite().getShell();

		// create the debug action
		Action debugAction = new StartAction(shell, provider, "debug", ILaunchManager.DEBUG_MODE);
		debugAction.setToolTipText(ServerUIPlugin.getResource("%actionDebugToolTip"));
		debugAction.setText(ServerUIPlugin.getResource("%actionDebug"));
		debugAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
		debugAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
		debugAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
	
		// create the start action
		Action runAction = new StartAction(shell, provider, "start", ILaunchManager.RUN_MODE);
		runAction.setToolTipText(ServerUIPlugin.getResource("%actionStartToolTip"));
		runAction.setText(ServerUIPlugin.getResource("%actionStart"));
		runAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		runAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		runAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		
		// create the profile action
		Action profileAction = new StartAction(shell, provider, "profile", ILaunchManager.PROFILE_MODE);
		profileAction.setToolTipText(ServerUIPlugin.getResource("%actionProfileToolTip"));
		profileAction.setText(ServerUIPlugin.getResource("%actionProfile"));
		profileAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
		profileAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
		profileAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
	
		// create the restart menu
		restartMenu = new MenuManager(ServerUIPlugin.getResource("%actionRestart"));
		
		Action restartAction = new RestartAction(shell, provider, "restartDebug", ILaunchManager.DEBUG_MODE);
		restartAction.setToolTipText(ServerUIPlugin.getResource("%actionDebugToolTip"));
		restartAction.setText(ServerUIPlugin.getResource("%actionDebug"));
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
		restartMenu.add(restartAction);
		
		restartAction = new RestartAction(shell, provider, "restartRun", ILaunchManager.RUN_MODE);
		restartAction.setToolTipText(ServerUIPlugin.getResource("%actionRestartToolTip"));
		restartAction.setText(ServerUIPlugin.getResource("%actionStart"));
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		restartMenu.add(restartAction);
		
		restartAction = new RestartAction(shell, provider, "restartProfile", ILaunchManager.PROFILE_MODE);
		restartAction.setToolTipText(ServerUIPlugin.getResource("%actionRestartToolTip"));
		restartAction.setText(ServerUIPlugin.getResource("%actionProfile"));
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
		restartMenu.add(restartAction);
		
		// create the restart action
		restartAction = new RestartAction(shell, provider, "restart");
		restartAction.setToolTipText(ServerUIPlugin.getResource("%actionRestartToolTip"));
		restartAction.setText(ServerUIPlugin.getResource("%actionRestart"));
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_RESTART));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_RESTART));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_RESTART));

		// create the stop action
		Action stopAction = new StopAction(shell, provider, "stop", IServerType.SERVER_STATE_SET_MANAGED);
		stopAction.setToolTipText(ServerUIPlugin.getResource("%actionStopToolTip"));
		stopAction.setText(ServerUIPlugin.getResource("%actionStop"));
		stopAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
		stopAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
		stopAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));

		// create the disconnect action
		Action disconnectAction = new StopAction(shell, provider, "disconnect", IServerType.SERVER_STATE_SET_ATTACHED);
		disconnectAction.setToolTipText(ServerUIPlugin.getResource("%actionStopToolTip2"));
		disconnectAction.setText(ServerUIPlugin.getResource("%actionStop2"));
		disconnectAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_DISCONNECT));
		disconnectAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_DISCONNECT));
		disconnectAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_DISCONNECT));

		// create the publish action
		Action publishAction = new PublishAction(shell, provider, "publish");
		publishAction.setToolTipText(ServerUIPlugin.getResource("%actionPublishToolTip"));
		publishAction.setText(ServerUIPlugin.getResource("%actionPublish"));
		publishAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_PUBLISH));
		publishAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_PUBLISH));
		publishAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_PUBLISH));
		
		// create the module slosh dialog action
		Action addModuleAction = new ModuleSloshAction(shell, provider, "modules");
		addModuleAction.setToolTipText(ServerUIPlugin.getResource("%actionModifyModulesToolTip"));
		addModuleAction.setText(ServerUIPlugin.getResource("%actionModifyModules"));
		addModuleAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_MODIFY_MODULES));
		addModuleAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_MODIFY_MODULES));
		addModuleAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_MODIFY_MODULES));
		
		actions = new Action[8];
		actions[0] = debugAction;
		actions[1] = runAction;
		actions[2] = profileAction;
		actions[3] = restartAction;
		actions[4] = stopAction;
		actions[5] = disconnectAction;
		actions[6] = publishAction;
		actions[7] = addModuleAction;
		
		// add toolbar buttons
		IContributionManager cm = getViewSite().getActionBars().getToolBarManager();
		for (int i = 0; i < actions.length - 1; i++) {
			cm.add(actions[i]);
		}
	}
	
	protected void fillContextMenu(Shell shell, IMenuManager menu) {
		// get selection but avoid no selection or multiple selection
		IServer server = null;
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		if (!selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			server = (IServer) iterator.next();
			if (iterator.hasNext())
				server = null;
		}
		
		// new action
		MenuManager newMenu = new MenuManager(ServerUIPlugin.getResource("%actionNew"));
		ServerTree.fillNewContextMenu(null, selection, newMenu);
		menu.add(newMenu);
		
		// open action
		if (server != null) {
			menu.add(new OpenAction(server));
			menu.add(new Separator());
			
			menu.add(new DeleteAction(shell, server));
			menu.add(new Separator());
		
			// server actions
			for (int i = 0; i < actions.length - 1; i++) {
				if (i == 3)
					menu.add(restartMenu);
				else
					menu.add(actions[i]);
			}
		
			// switch config
			if (server.getServerType() != null && server.getServerType().hasServerConfiguration()) {
				MenuManager menuManager = new MenuManager(ServerUIPlugin.getResource("%actionSwitchConfiguration"));
				menuManager.add(new SwitchConfigurationAction(shell, ServerUIPlugin.getResource("%viewNoConfiguration"), server, null));
	
				IServerConfiguration[] configs = ServerUtil.getSupportedServerConfigurations(server);
				if (configs != null) {
					int size = configs.length;
					for (int i = 0; i < size; i++) {
						menuManager.add(new SwitchConfigurationAction(shell, configs[i].getName(), server, configs[i]));
					}
				}
	
				menu.add(menuManager);
			}
			
			// monitor
			if (server.getServerType() != null) {
				final MenuManager menuManager = new MenuManager(ServerUIPlugin.getResource("%actionMonitor"));
				
				final IServer server2 = server;
				final Shell shell2 = shell;
				menuManager.addMenuListener(new IMenuListener() {
					public void menuAboutToShow(IMenuManager manager) {
						menuManager.removeAll();
						if (server2.isDelegatePluginActivated()) {
							IServerPort[] ports = server2.getServerPorts();
							if (ports != null) {
								int size = ports.length;
								for (int i = 0; i < size; i++) {
									if (!ports[i].isAdvanced())
										menuManager.add(new MonitorServerPortAction(shell2, server2, ports[i]));
								}
							}
						}
						
						menuManager.add(new MonitorServerAction(shell2, server2));
					}
				});
				
				// add an initial menu item so that the menu appears correctly
				menuManager.add(new MonitorServerAction(shell, server));
				menu.add(menuManager);
			}
		}
	
		if (server != null && server.isDelegateLoaded()) {
			menu.add(new Separator());
	
			MenuManager restartProjectMenu = new MenuManager(ServerUIPlugin.getResource("%actionRestartProject"));
	
			if (server != null) {
				IModule[] modules = ServerUtil.getAllContainedModules(server, null);
				if (modules != null) {
					int size = modules.length;
					for (int i = 0; i < size; i++) {
						Action action = new RestartModuleAction(server, modules[i]);
						restartProjectMenu.add(action);
					}
				}
			}
			if (restartProjectMenu.isEmpty())
				menu.add(new DisabledMenuManager(ServerUIPlugin.getResource("%actionRestartProject")));
			else
				menu.add(restartProjectMenu);
		}
		
		if (server != null) {
			ServerAction.addServerMenuItems(shell, menu, server);
		}
	
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));
	}
	
	/**
	 * 
	 */
	public void setFocus() {
		if (table != null)
			table.setFocus();
	}
}