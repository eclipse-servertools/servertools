package org.eclipse.wst.server.ui.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.actions.NewServerAction;
import org.eclipse.wst.server.ui.internal.view.servers.DeleteAction;
import org.eclipse.wst.server.ui.internal.view.servers.OpenAction;
import org.eclipse.wst.server.ui.internal.view.servers.PublishAction;
import org.eclipse.wst.server.ui.internal.view.servers.RestartAction;
import org.eclipse.wst.server.ui.internal.view.servers.StartAction;
import org.eclipse.wst.server.ui.internal.view.tree.ServerElementAdapter;
import org.eclipse.wst.server.ui.internal.view.tree.ServerTreeAction;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 */
public class ServerTree {
	/**
	 * Constants for actions
	 */
	public static final byte ACTION_OPEN = 0;
	public static final byte ACTION_DELETE = 1;
	public static final byte ACTION_BOOKMARK = 2;

	private ServerTree() { }
	
	public static void fillContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		MenuManager newMenu = new MenuManager(ServerUIPlugin.getResource("%actionNew"));
		fillNewContextMenu(shell, selection, newMenu);
		menu.add(newMenu);
		fillOtherContextMenu(shell, selection, menu);
	}
	
	public static void fillNewContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		IAction newServerAction = new NewServerAction();
		newServerAction.setText(ServerUIPlugin.getResource("%actionNewServer"));
		menu.add(newServerAction);
	}

	public static void fillOtherContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		if (selection == null)
			return;

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return;

		//IStructuredSelection sel = (IStructuredSelection) selection;
		
		//boolean singleSelect = (sel.size() == 1);
		//Object first = sel.getFirstElement();

		// open menu
		/*if (singleSelect && first instanceof ServerResourceAdapter) {
			ServerResourceAdapter adapter = (ServerResourceAdapter) first;
			IServerResource resource = adapter.getServerResource();
			menu.add(new OpenAction(resource));
			menu.add(new Separator());
		}
		
		// delete menu
		List list = new ArrayList();
		boolean canDelete = true;
		Iterator iterator2 = sel.iterator();
		while (iterator2.hasNext()) {
			Object obj = iterator2.next();
			
			if (obj instanceof ServerResourceAdapter)
				list.add(((ServerResourceAdapter) obj).getServerResource());
			else
				canDelete = false;
		}
		if (canDelete) {
			IServerResource[] res = new IServerResource[list.size()];
			list.toArray(res);
			menu.add(new DeleteAction(shell, res));
		}

		if (singleSelect && first instanceof IServerElementTag)
			menu.add(new Separator());

		if (singleSelect && first instanceof ServerResourceAdapter) {
			ServerResourceAdapter adapter = (ServerResourceAdapter) first;
			IServerResource resource = adapter.getServerResource();

			IServer server = null;
			if (resource instanceof IServer)
				server = (IServer) resource;

			// switch configuration menu
			if (server != null) {
				addServerActions(shell, menu, server);

				MenuManager menuManager = new MenuManager(ServerUIPlugin.getResource("%actionSwitchConfiguration"));
				menuManager.add(new SwitchConfigurationAction(shell, ServerUIPlugin.getResource("%viewNoConfiguration"), server, null));

				List configs = ServerUtil.getSupportedServerConfigurations(server);
				Iterator iterator = configs.iterator();
				while (iterator.hasNext()) {
					IServerConfiguration config = (IServerConfiguration) iterator.next();
					menuManager.add(new SwitchConfigurationAction(shell, ServerUtil.getName(config), server, config));
				}

				menu.add(menuManager);
			}
			
			if (server != null && server instanceof IModuleRestartable) {
				menu.add(new Separator());

				MenuManager restartProjectMenu = new MenuManager(ServerUIPlugin.getResource("%actionRestartProject"));

				IModuleRestartable restartable = (IModuleRestartable) server;

				IServerConfiguration configuration = ServerUtil.getServerConfiguration(server);
				if (configuration != null) {
					Iterator iterator = ServerUtil.getAllContainedModules(configuration).iterator();
					while (iterator.hasNext()) {
						IModule module = (IModule) iterator.next();
						Action action = new RestartModuleAction(restartable, module);
						restartProjectMenu.add(action);
					}
				}
				if (restartProjectMenu.isEmpty())
					menu.add(new DisabledMenuManager(ServerUIPlugin.getResource("%actionRestartProject")));
				else
					menu.add(restartProjectMenu);
			}
		}
		if (singleSelect && first instanceof ModuleResourceAdapter) {
			ModuleResourceAdapter adapter = (ModuleResourceAdapter) first;
			IServerConfiguration configuration = adapter.getServerConfiguration();
			IModule module = adapter.getModule();
			
			IModule[] modules = configuration.getModules();
			boolean found = false;
			if (modules != null && module != null) {
				int size = modules.length;
				for (int i = 0; i < size; i++) {
					if (module.equals(modules[i]) && configuration.canRemoveModule(module))
						found = true;
				}
			}
	
			if (found)
				menu.add(new ModifyConfigurationModulesAction(shell, configuration, module, false));
		}
		
		if (singleSelect && first instanceof ServerResourceAdapter) {
			ServerResourceAdapter adapter = (ServerResourceAdapter) first;
			IServerResource resource = adapter.getServerResource();

			IServer server = null;
			IServerConfiguration configuration = null;
			if (resource instanceof IServerConfiguration)
				configuration = (IServerConfiguration) resource;
			else if (resource instanceof IServer) {
				server = (IServer) resource;
				configuration = ServerUtil.getServerConfiguration(server);
			}
			
			ServerAction.addServerMenuItems(shell, menu, server, configuration);
		}*/
	}

	protected static void addServerActions(Shell shell, IMenuManager menu, IServer server) {
		final ISelection selection = new StructuredSelection(server);
		ISelectionProvider provider = new ISelectionProvider() {
			public void addSelectionChangedListener(ISelectionChangedListener listener) { }
			public ISelection getSelection() {
				return selection;
			}
			public void removeSelectionChangedListener(ISelectionChangedListener listener) { }
			public void setSelection(ISelection sel) { }
		};
	
		// create the debug action
		Action debugAction = new StartAction(shell, provider, "debug", ILaunchManager.DEBUG_MODE);
		debugAction.setToolTipText(ServerUIPlugin.getResource("%actionDebugToolTip"));
		debugAction.setText(ServerUIPlugin.getResource("%actionDebug"));
		debugAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
		debugAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
		debugAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
		menu.add(debugAction);
	
		// create the start action
		Action runAction = new StartAction(shell, provider, "start", ILaunchManager.RUN_MODE);
		runAction.setToolTipText(ServerUIPlugin.getResource("%actionStartToolTip"));
		runAction.setText(ServerUIPlugin.getResource("%actionStart"));
		runAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		runAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		runAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		menu.add(runAction);
		
		// create the profile action
		Action profileAction = new StartAction(shell, provider, "profile", ILaunchManager.PROFILE_MODE);
		profileAction.setToolTipText(ServerUIPlugin.getResource("%actionProfileToolTip"));
		profileAction.setText(ServerUIPlugin.getResource("%actionProfile"));
		profileAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
		profileAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
		profileAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
		menu.add(profileAction);
	
		// create the restart action
		MenuManager menuManager = new MenuManager(ServerUIPlugin.getResource("%actionRestart"));
		
		Action restartAction = new RestartAction(shell, provider, "restart", ILaunchManager.RUN_MODE);
		restartAction.setToolTipText(ServerUIPlugin.getResource("%actionRestartToolTip"));
		restartAction.setText(ServerUIPlugin.getResource("%actionRestart"));
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_RESTART));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_RESTART));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_RESTART));
		menuManager.add(restartAction);
		//menu.add(restartAction);
		menu.add(menuManager);

		// create the stop action
		/*Action stopAction = new StopAction(shell, provider, "stop", IServerFactory.SERVER_STATE_SET_MANAGED);
		stopAction.setToolTipText(ServerUIPlugin.getResource("%actionStopToolTip"));
		stopAction.setText(ServerUIPlugin.getResource("%actionStop"));
		stopAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
		stopAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
		stopAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));
		menu.add(stopAction);

		// create the disconnect action
		Action disconnectAction = new StopAction(shell, provider, "disconnect", IServerFactory.SERVER_STATE_SET_ATTACHED);
		disconnectAction.setToolTipText(ServerUIPlugin.getResource("%actionStopToolTip2"));
		disconnectAction.setText(ServerUIPlugin.getResource("%actionStop2"));
		disconnectAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_DISCONNECT));
		disconnectAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_DISCONNECT));
		disconnectAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_DISCONNECT));
		menu.add(disconnectAction);*/

		// create the publish action
		Action publishAction = new PublishAction(shell, provider, "publish");
		publishAction.setToolTipText(ServerUIPlugin.getResource("%actionPublishToolTip"));
		publishAction.setText(ServerUIPlugin.getResource("%actionPublish"));
		publishAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_PUBLISH));
		publishAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_PUBLISH));
		publishAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_PUBLISH));
		menu.add(publishAction);
	}

	public static boolean isActionEnabled(ISelection selection, byte action) {
		if (selection == null || action < 0)
			return false;

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return false;

		IStructuredSelection sel = (IStructuredSelection) selection;
		
		if (action == ACTION_OPEN) {
			if (sel.size() != 1)
				return false;

			Object obj = sel.getFirstElement();

			return (obj instanceof ServerElementAdapter);
		} else if (action == ACTION_DELETE) {
			if (sel.size() == 0)
				return false;
			
			Iterator iterator = sel.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				
				if (!(obj instanceof ServerElementAdapter) && !(obj instanceof IServer))
					return false;
			}
			return true;
		}

		return false;
	}

	public static boolean performAction(Shell shell, ISelection selection, byte action) {
		//if (!isActionEnabled(selection, action))
		//	return false;

		if (selection == null || action < 0)
			return false;

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return false;

		IStructuredSelection sel = (IStructuredSelection) selection;

		if (action == ACTION_OPEN) {
			if (sel.size() != 1)
				return false;

			Object obj = sel.getFirstElement();
			if (obj instanceof IServer) {
				Action open = new OpenAction((IServer) obj);
				open.run();
				return true;
			} else if (obj instanceof ServerElementAdapter) {
				ServerElementAdapter adapter = (ServerElementAdapter) obj;
				IElement element = adapter.getServerResource();
				if (element instanceof IServer) {
					Action open = new OpenAction((IServer) element);
					open.run();
					return true;
				}
				return false;
			}
			return false;
		} else if (action == ACTION_DELETE) {
			if (sel.size() == 0)
				return false;
			
			List list = new ArrayList();
			Iterator iterator = sel.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				
				if (obj instanceof IElement)
					list.add(obj);
				else if (obj instanceof ServerElementAdapter)
					list.add(((ServerElementAdapter) obj).getServerResource());
			}
			
			IElement[] res = new IElement[list.size()];
			list.toArray(res);
			
			Action delete = new DeleteAction(shell, res);
			delete.run();
			return true;
		}

		return false;
	}
	
	/**
	 * Returns an action of the specified type, which can be used for global
	 */
	public static IAction getAction(Shell shell, ISelectionProvider provider, byte action) {
		if (action == ACTION_DELETE) {
			return new ServerTreeAction(shell, provider, ServerUIPlugin.getResource("%actionDelete"), ServerTree.ACTION_DELETE);
		}
		return null;
	}
}
