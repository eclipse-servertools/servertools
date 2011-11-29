/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Action for removing a module from a server.
 */
public class RemoveModuleAction extends Action {
	protected IServer server;
	protected IModule[] module;
	protected Shell shell;
	CoreException saveServerException = null;

	/**
	 * RemoveModuleAction constructor.
	 * 
	 * @param shell a shell
	 * @param server a server
	 * @param module a module
	 */
	public RemoveModuleAction(Shell shell, IServer server, IModule module) {
		this(shell, server, new IModule[] {module});
	}
	
	/**
	 * RemoveModuleAction constructor.
	 * 
	 * @param shell a shell
	 * @param server a server
	 * @param module a list of module items, used for removing multiple different modules
	 */
	public RemoveModuleAction(Shell shell, IServer server, IModule[] module) {
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
		String message = module.length == 1 ? Messages.dialogRemoveModuleConfirm : Messages.dialogRemoveModulesConfirm;
		if (MessageDialog.openConfirm(shell, Messages.defaultDialogTitle, message)) {
			for( int i = 0; i < module.length; i++ ) 
				handleRemoveOneModule(module[i]);
		}
	}
	
	protected void handleRemoveOneModule(final IModule mod) {
			try {
				final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				dialog.setBlockOnOpen(false);
				dialog.setCancelable(true);
				IRunnableWithProgress runnable = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) {
						try {
							IServerWorkingCopy wc = server.createWorkingCopy();
							if (monitor.isCanceled()) {
								return;
							}
							wc.modifyModules(null, new IModule[] { mod }, monitor);
							if (monitor.isCanceled()) {
								return;
							}
							server = wc.save(true, monitor);
							if (Trace.INFO) {
								Trace.trace(Trace.STRING_INFO, "Done save server configuration in RemoveModuleAction.");
							}
						} catch (CoreException e) {
							if (Trace.WARNING) {
								Trace.trace(Trace.STRING_WARNING,
										"Failed to save server configuration. Could not remove module", e);
							}
							saveServerException = e;
						}
					}
				};
				dialog.run(true, true, runnable);
				
				// Error when saving server so do not proceed on the remove action.
				if (saveServerException != null) {
					return;
				}
				
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
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not remove module", e);
				}
			}
	}
}