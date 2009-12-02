/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Shell;
/**
 * Action for removing a module from a server.
 */
public class RemoveModuleAction extends Action {
	protected IServer server;
	protected IModule module;
	protected Shell shell;

	/**
	 * RemoveModuleAction constructor.
	 * 
	 * @param shell a shell
	 * @param server a server
	 * @param module a module
	 */
	public RemoveModuleAction(Shell shell, IServer server, IModule module) {
		super(Messages.actionRemove);
		this.shell = shell;
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));		
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		this.server = server;
		this.module = module;
	}

	/**
	 * Invoked when an action occurs. 
	 */
	public void run() {
		if (MessageDialog.openConfirm(shell, Messages.defaultDialogTitle, Messages.dialogRemoveModuleConfirm)) {
			try {
				IServerWorkingCopy wc = server.createWorkingCopy();
				wc.modifyModules(null, new IModule[] { module }, null);
				server = wc.save(true, null);
				
				if (server.getServerState() != IServer.STATE_STOPPED &&
						ServerUIPlugin.getPreferences().getPublishOnAddRemoveModule()) {
					final IAdaptable info = new IAdaptable() {
						public Object getAdapter(Class adapter) {
							if (Shell.class.equals(adapter))
								return shell;
							return null;
						}
					};
					server.publish(IServer.PUBLISH_INCREMENTAL, null, info, null);
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not remove module", e);
			}
		}
	}
}