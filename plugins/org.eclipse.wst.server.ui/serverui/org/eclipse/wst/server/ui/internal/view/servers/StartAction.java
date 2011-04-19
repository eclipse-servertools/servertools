/**********************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import java.util.Iterator;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.provisional.UIDecoratorManager;
import org.eclipse.swt.widgets.Shell;
/**
 * Start a server.
 */
public class StartAction extends AbstractServerAction {
	protected String launchMode = ILaunchManager.RUN_MODE;
	
	public StartAction(Shell shell, ISelectionProvider selectionProvider, String launchMode) {
		super(shell, selectionProvider, "start");
		this.launchMode = launchMode;
		if (launchMode == ILaunchManager.RUN_MODE) {
			setToolTipText(Messages.actionStartToolTip);
			setText(Messages.actionStart);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START));
			setActionDefinitionId("org.eclipse.wst.server.run");
		} else if (launchMode == ILaunchManager.DEBUG_MODE) {
			setToolTipText(Messages.actionDebugToolTip);
			setText(Messages.actionDebug);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_DEBUG));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_DEBUG));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_DEBUG));
			setActionDefinitionId("org.eclipse.wst.server.debug");
		} else if (launchMode == ILaunchManager.PROFILE_MODE) {
			setToolTipText(Messages.actionProfileToolTip);
			setText(Messages.actionProfile);
			setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ELCL_START_PROFILE));
			setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CLCL_START_PROFILE));
			setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DLCL_START_PROFILE));
		}
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Update the name of the Action label, depending on the status of the server.
	 * @param sel the IStructuredSelection from the view
	 */
	private void updateText(IStructuredSelection sel){
		if (this.launchMode == ILaunchManager.RUN_MODE) {
			if (sel.isEmpty()) {
				setText(Messages.actionStart);
				return;
			}
			Iterator iterator = sel.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (obj instanceof IServer) {
					IServer server = (IServer) obj;
					if (server.getServerState() == IServer.STATE_STARTED ||
						 server.getServerState() == IServer.STATE_STARTING) {
						setText(Messages.actionRestart);
						setToolTipText(Messages.actionRestartToolTip);
					} else {
						setText(Messages.actionStart);
						setToolTipText(Messages.actionStartToolTip);
					}
				}
			}	
		} else if (this.launchMode == ILaunchManager.DEBUG_MODE) {
			if (sel.isEmpty()) {
				setText(Messages.actionDebug);
				return;
			}
			Iterator iterator = sel.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (obj instanceof IServer) {
					IServer server = (IServer) obj;
					if (server.getServerState() == IServer.STATE_STARTED ||
						 server.getServerState() == IServer.STATE_STARTING) {
						setText(Messages.actionDebugRestart);
						setToolTipText(Messages.actionDebugRestartToolTip);
					} else {
						setText(Messages.actionDebug);
						setToolTipText(Messages.actionDebugToolTip);
					}
				}
			}
		} else if (this.launchMode == ILaunchManager.PROFILE_MODE) {
			if (sel.isEmpty()) {
				setText(Messages.actionProfile);
				return;
			}
			Iterator iterator = sel.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (obj instanceof IServer) {
					IServer server = (IServer) obj;
					if (server.getServerState() == IServer.STATE_STARTED ||
						 server.getServerState() == IServer.STATE_STARTING) {
						setText(Messages.actionProfileRestart);
						setToolTipText(Messages.actionProfileRestartToolTip);
					} else {
						setText(Messages.actionProfile);
						setToolTipText(Messages.actionProfileToolTip);
					}
				}
			}
		}		
	}
	
	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server a server
	 */
	public boolean accept(IServer server) {
		if (server.getServerState() != IServer.STATE_STARTED) { // start
			return server.canStart(launchMode).isOK();
		}
		// restart
		String mode2 = launchMode;
		if (mode2 == null)
			mode2 = server.getMode();
		return server.getServerType() != null && UIDecoratorManager.getUIDecorator(server.getServerType()).canRestart() && server.canRestart(mode2).isOK();
	}

	/**
	 * Perform action on this server.
	 * @param server a server
	 */
	public void perform(IServer server) {
		start(server, launchMode, shell);
	}

	public static void start(IServer server, String launchMode, final Shell shell) {
		if (server.getServerState() != IServer.STATE_STARTED) {
			if (!ServerUIPlugin.saveEditors())
				return;
			
			/*final IAdaptable info = new IAdaptable() {
				public Object getAdapter(Class adapter) {
					if (Shell.class.equals(adapter))
						return shell;
					return null;
				}
			};*/
			server.start(launchMode, (IOperationListener)null);
		} else {
			if (shell != null && !ServerUIPlugin.promptIfDirty(shell, server))
				return;
			
			try {
				String launchMode2 = launchMode;
				if (launchMode2 == null)
					launchMode2 = server.getMode();
				server.restart(launchMode2, (IOperationListener) null);
			} catch (Exception e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Error restarting server", e);
				}
			}
		}
	}

	public void selectionChanged(IStructuredSelection sel) {
		super.selectionChanged(sel);
		updateText(sel);		
	}
}