/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.*;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.actions.NewServerWizardAction;
import org.eclipse.swt.widgets.Shell;
/**
 * 
 */
public class ServerActionHelper {
	/**
	 * Constants for actions
	 */
	public static final byte ACTION_OPEN = 0;
	public static final byte ACTION_DELETE = 1;
	public static final byte ACTION_BOOKMARK = 2;

	private ServerActionHelper() {
		// do nothing
	}
	
	public static void fillContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		MenuManager newMenu = new MenuManager(Messages.actionNew);
		fillNewContextMenu(shell, selection, newMenu);
		menu.add(newMenu);
		fillOtherContextMenu(shell, selection, menu);
	}
	
	public static void fillNewContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		IAction newServerAction = new NewServerWizardAction();
		newServerAction.setText(Messages.actionNewServer);
		menu.add(newServerAction);
	}

	public static void fillOtherContextMenu(Shell shell, ISelection selection, IMenuManager menu) {
		if (selection == null)
			return;

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return;
	}

	protected static void addServerActions(Shell shell, IMenuManager menu, IServer server) {
		final ISelection selection = new StructuredSelection(server);
		ISelectionProvider provider = new ISelectionProvider() {
			public void addSelectionChangedListener(ISelectionChangedListener listener) {
				// do nothing
			}
			public ISelection getSelection() {
				return selection;
			}
			public void removeSelectionChangedListener(ISelectionChangedListener listener) {
				// do nothing
			}
			public void setSelection(ISelection sel) {
				// do nothing
			}
		};
	
		// create the debug action
		Action debugAction = new StartAction(shell, provider, "debug", ILaunchManager.DEBUG_MODE);
		debugAction.setToolTipText(Messages.actionDebugToolTip);
		debugAction.setText(Messages.actionDebug);
		debugAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
		debugAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
		debugAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
		menu.add(debugAction);
	
		// create the start action
		Action runAction = new StartAction(shell, provider, "start", ILaunchManager.RUN_MODE);
		runAction.setToolTipText(Messages.actionStartToolTip);
		runAction.setText(Messages.actionStart);
		runAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
		runAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
		runAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
		menu.add(runAction);
		
		// create the profile action
		Action profileAction = new StartAction(shell, provider, "profile", ILaunchManager.PROFILE_MODE);
		profileAction.setToolTipText(Messages.actionProfileToolTip);
		profileAction.setText(Messages.actionProfile);
		profileAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
		profileAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
		profileAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
		menu.add(profileAction);
	
		// create the restart action
		MenuManager menuManager = new MenuManager(Messages.actionRestart);
		
		Action restartAction = new RestartAction(shell, provider, "restart", ILaunchManager.RUN_MODE);
		restartAction.setToolTipText(Messages.actionRestartToolTip);
		restartAction.setText(Messages.actionRestart);
		restartAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_RESTART));
		restartAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_RESTART));
		restartAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_RESTART));
		menuManager.add(restartAction);
		//menu.add(restartAction);
		menu.add(menuManager);

		// create the stop action
		/*Action stopAction = new StopAction(shell, provider, "stop", IServerFactory.SERVER_STATE_SET_MANAGED);
		stopAction.setToolTipText(Messages.actionStopToolTip"));
		stopAction.setText(Messages.actionStop"));
		stopAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_STOP));
		stopAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_STOP));
		stopAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_STOP));
		menu.add(stopAction);

		// create the disconnect action
		Action disconnectAction = new StopAction(shell, provider, "disconnect", IServerFactory.SERVER_STATE_SET_ATTACHED);
		disconnectAction.setToolTipText(Messages.actionStopToolTip2"));
		disconnectAction.setText(Messages.actionStop2"));
		disconnectAction.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_DISCONNECT));
		disconnectAction.setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_DISCONNECT));
		disconnectAction.setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_DISCONNECT));
		menu.add(disconnectAction);*/

		// create the publish action
		Action publishAction = new PublishAction(shell, provider, "publish");
		publishAction.setToolTipText(Messages.actionPublishToolTip);
		publishAction.setText(Messages.actionPublish);
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
			return false;
		} else if (action == ACTION_DELETE) {
			// get selection but avoid no selection or multiple selection
			IModule[] module = null;
			if (!sel.isEmpty()) {
				Iterator iterator = sel.iterator();
				Object obj = iterator.next();
				if (obj instanceof ModuleServer) {
					ModuleServer ms = (ModuleServer) obj;
					module = ms.module;
				}
				if (iterator.hasNext())
					module = null;
			}
			
			return (module == null || module.length == 1);
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
			}
			return false;
		} else if (action == ACTION_DELETE) {
			// get selection but avoid no selection or multiple selection
			IServer server = null;
			IModule[] module = null;
			if (!sel.isEmpty()) {
				Iterator iterator = sel.iterator();
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
			
			if (module == null)
				new DeleteAction(shell, server).run();
			else if (module.length == 1)
				new RemoveModuleAction(shell, server, module[0]).run();
			
			return true;
		}

		return false;
	}
	
	/**
	 * Returns an action of the specified type, which can be used for global.
	 * 
	 * @param shell a shell
	 * @param provider a selection provider
	 * @param action an action
	 * @return an action
	 */
	public static IAction getAction(Shell shell, ISelectionProvider provider, byte action) {
		if (action == ACTION_DELETE) {
			return new ServerAction(shell, provider, Messages.actionDelete, ServerActionHelper.ACTION_DELETE);
		}
		return null;
	}
}