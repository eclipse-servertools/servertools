/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Base Code
 *     Red Hat - Refactor for CNF
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.cnf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.*;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.navigator.*;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.actions.NewServerWizardAction;
import org.eclipse.wst.server.ui.internal.view.servers.*;

public class ServerActionProvider extends CommonActionProvider {
	public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId";
	public static final String SHOW_IN_MENU_ID = "org.eclipse.ui.navigate.showInQuickMenu";
	public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart";
	public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd";
	public static final String EDIT_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnfeditSectionStart";
	public static final String EDIT_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.editSectionEnd";
	public static final String CONTROL_SERVER_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.controlServerSectionStart";
	public static final String CONTROL_SERVER_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.controlServerSectionEnd";
	public static final String SERVER_ETC_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.serverEtcSectionStart";
	public static final String SERVER_ETC_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.serverEtcSectionEnd";
	public static final String CONTROL_MODULE_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.controlModuleSectionStart";
	public static final String CONTROL_MODULE_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.controlModuleSectionEnd";
	
	private ICommonActionExtensionSite actionSite;
	private Clipboard clipboard;
	public ServerActionProvider() {
		super();
	}
	
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		this.actionSite = aSite;
		ICommonViewerSite site = aSite.getViewSite();
		if( site instanceof ICommonViewerWorkbenchSite ) {
			StructuredViewer v = aSite.getStructuredViewer();
			if( v instanceof CommonViewer ) {
				CommonViewer cv = (CommonViewer)v;
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)site;
				addListeners(cv);
				makeServerActions(cv, wsSite.getSelectionProvider());
			}
		}
	}


	// actions on a server
	protected Action[] actions;
	protected Action actionModifyModules;
	protected Action openAction, showInConsoleAction, showInDebugAction, propertiesAction, monitorPropertiesAction;
	protected Action copyAction, pasteAction, globalDeleteAction, renameAction;
	protected Action noneAction = new Action(Messages.dialogMonitorNone) {
		// dummy action
	};

	private void addListeners(CommonViewer tableViewer) {
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
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Could not open server", e);
					}
				}
			}
		});
	}
	
	private void makeServerActions(CommonViewer tableViewer, ISelectionProvider provider) {
		clipboard = new Clipboard(tableViewer.getTree().getDisplay());
		Shell shell = tableViewer.getTree().getShell();
		
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
		
		// create the open action
		openAction = new OpenAction(provider);

		// create copy, paste, and delete actions
		pasteAction = new PasteAction(shell, provider, clipboard);
		copyAction = new CopyAction(provider, clipboard, pasteAction);
		globalDeleteAction = new GlobalDeleteAction(shell, provider);
		renameAction = new RenameAction(shell, tableViewer, provider);
		
		// create the other actions
		actionModifyModules = new ModuleSloshAction(shell, provider);
		showInConsoleAction = new ShowInConsoleAction(provider);
		showInDebugAction = new ShowInDebugAction(provider);
		
		// create the properties action
		propertiesAction = new PropertiesAction(shell, provider);
		monitorPropertiesAction = new PropertiesAction(shell, "org.eclipse.wst.server.ui.properties.monitor", provider);
	}

	public void fillActionBars(IActionBars actionBars) {
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.debug", actions[0]);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.run", actions[1]);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.stop", actions[3]);
		actionBars.setGlobalActionHandler("org.eclipse.wst.server.publish", actions[4]);
		actionBars.setGlobalActionHandler("org.eclipse.ui.navigator.Open", openAction);
		actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertiesAction);
		actionBars.updateActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), globalDeleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), renameAction);
		
		IContributionManager cm = actionBars.getToolBarManager();
	    IContributionItem[] cis = cm.getItems();
	    List<IAction> existingActions = new ArrayList<IAction>();
	    for (IContributionItem ci : cis) {
	        if (ci instanceof ActionContributionItem) {
	            ActionContributionItem aci = (ActionContributionItem) ci;
	            existingActions.add(aci.getAction());
	        }
	    }

	    for (int i = 0; i < actions.length - 1; i++)
	        if (!existingActions.contains(actions[i]))
	            cm.add(actions[i]);

	}
	
	public void fillContextMenu(IMenuManager menu) {
		// This is a temp workaround to clean up the default group that are provided by CNF		
		menu.removeAll();
		
		ICommonViewerSite site = actionSite.getViewSite();
		IStructuredSelection selection = null;
		Shell shell = actionSite.getViewSite().getShell();
		if( site instanceof ICommonViewerWorkbenchSite ) {
			ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite)site;
			selection = (IStructuredSelection) wsSite.getSelectionProvider().getSelection();
		}

		IServer server = null;
		IModule[] module = null;
		ArrayList<IModule> multipleModulesOnOneServer = null;
		if (selection != null && !selection.isEmpty()) {
			multipleModulesOnOneServer = getMultipleModulesOnOneServer(selection);
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

		menu.add(invisibleSeparator(TOP_SECTION_START_SEPARATOR));
		addTopSection(menu, server, module);
		menu.add(invisibleSeparator(TOP_SECTION_END_SEPARATOR));
		menu.add(new Separator());

		if (server != null && module == null) {
			menu.add(invisibleSeparator(EDIT_SECTION_START_SEPARATOR));
			menu.add(copyAction);
			menu.add(pasteAction);
			menu.add(globalDeleteAction);
			menu.add(renameAction);
			menu.add(invisibleSeparator(EDIT_SECTION_END_SEPARATOR));

			menu.add(new Separator());
			
			menu.add(invisibleSeparator(CONTROL_SERVER_SECTION_START_SEPARATOR));
			for (int i = 0; i < actions.length; i++)
				menu.add(actions[i]);
			menu.add(invisibleSeparator(CONTROL_SERVER_SECTION_END_SEPARATOR));
			
			menu.add(new Separator());
			
			menu.add(invisibleSeparator(SERVER_ETC_SECTION_START_SEPARATOR));
			menu.add(actionModifyModules);
			addMonitor(server, menu, shell);
			menu.add(invisibleSeparator(SERVER_ETC_SECTION_END_SEPARATOR));
			menu.add(new Separator());
			
		} else if (server != null && module != null) {
			
			menu.add(invisibleSeparator(CONTROL_MODULE_SECTION_START_SEPARATOR));
			menu.add(new StartModuleAction(server, module));
			menu.add(new StopModuleAction(server, module));			
			menu.add(new RestartModuleAction(server, module));
			if(module.length == 1) {
				menu.add(new RemoveModuleAction(shell, server, module[0]));
			}
			menu.add(invisibleSeparator(CONTROL_MODULE_SECTION_END_SEPARATOR));
		} else if( server == null && module == null && multipleModulesOnOneServer != null) {
			server = selection.getFirstElement() == null ? null : ((ModuleServer)selection.getFirstElement()).getServer();
			menu.add(invisibleSeparator(CONTROL_MODULE_SECTION_START_SEPARATOR));
			menu.add(new RemoveModuleAction(shell, server, multipleModulesOnOneServer.toArray(new IModule[multipleModulesOnOneServer.size()])));
			menu.add(invisibleSeparator(CONTROL_MODULE_SECTION_END_SEPARATOR));
		}
	
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));
		menu.add(propertiesAction);
	}

	/*
	 * If the selection is several modules under one server, return
	 * the list of ModuleServer[] objects
	 */
	protected ArrayList<IModule> getMultipleModulesOnOneServer(IStructuredSelection selection) {
		return GlobalDeleteAction.getRemovableModuleList(selection);
	}

	
	protected void addTopSection(IMenuManager menu, IServer server, IModule[] module) {
		MenuManager newMenu = new MenuManager(Messages.actionNew, NEW_MENU_ID);
		IAction newServerAction = new NewServerWizardAction();
		newServerAction.setText(Messages.actionNewServer);
		newMenu.add(newServerAction);
		menu.add(newMenu);
		
		// open action
		if (server != null && module == null) {
			menu.add(openAction);
			
			String text = Messages.actionShowIn;
			final IWorkbench workbench = PlatformUI.getWorkbench();
			final IBindingService bindingService = (IBindingService) workbench
					.getAdapter(IBindingService.class);
			final TriggerSequence[] activeBindings = bindingService
					.getActiveBindingsFor(SHOW_IN_MENU_ID);
			if (activeBindings.length > 0) {
				text += "\t" + activeBindings[0].format();
			}
			
			MenuManager showInMenu = new MenuManager(text, SHOW_IN_MENU_ID);
			showInMenu.add(showInConsoleAction);
			showInMenu.add(showInDebugAction);
			menu.add(showInMenu);
		}
	}
	
	protected void addMonitor(IServer server, IMenuManager menu, Shell shell) {

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
	
	
	private Separator invisibleSeparator(String s) {
		Separator sep = new Separator(s);
		sep.setVisible(false);
		return sep;
	}
}
