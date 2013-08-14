/*
 * Copyright (c) 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Base Code
 *     Red Hat - Refactor for CNF
 */

package org.eclipse.wst.server.ui.internal.cnf;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.internal.view.servers.PublishAction;

public class ServersViewDropAdapterAssistant extends CommonDropAdapterAssistant {

	private List dndElementList;

	public IStatus validatePluginTransferDrop(
			IStructuredSelection aDragSelection, Object aDropTarget) {
		initializeSelection(aDragSelection);
		return internalValidate(aDropTarget, dndElementList);
	}

	public IStatus validateDrop(Object target, int operation,
			TransferData transferType) {
		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			ISelection s = LocalSelectionTransfer.getTransfer().getSelection();
			initializeSelection(s);
			return internalValidate(target, dndElementList);
		}
		return Status.CANCEL_STATUS;
	}

	protected void initializeSelection(ISelection s) {
		if (dndElementList != null)
			return;
		if (!(s instanceof IStructuredSelection)) {
			dndElementList = Collections.EMPTY_LIST;
			return;
		}
		dndElementList = ((IStructuredSelection) s).toList();
	}

	protected IStatus internalValidate(Object target, List elements) {
		if (target instanceof IServer) {
			IServer server = (IServer) target;
			Object next;
			if (elements != null) {
				Iterator i = elements.iterator();
				while (i.hasNext()) {
					next = i.next();
					IModuleArtifact[] moduleArtifacts = ServerPlugin
							.getModuleArtifacts(next);
					if (moduleArtifacts != null && moduleArtifacts.length > 0) {
						for (int j = 0; j < moduleArtifacts.length; j++) {
							if (moduleArtifacts[j] != null
									&& moduleArtifacts[j].getModule() != null) {
								IModuleType[] moduleTypes = server
										.getServerType().getRuntimeType()
										.getModuleTypes();
								if (ServerUtil.isSupportedModule(moduleTypes,
										moduleArtifacts[j].getModule()
												.getModuleType())) {
									return Status.OK_STATUS;
								}
							}
						}
					}
				}
			}
		}
		clear();
		return Status.CANCEL_STATUS;
	}

	public IStatus handleDrop(CommonDropAdapter dropAdapter, DropTargetEvent dropTargetEvent, Object target) {
		IStatus status = internalHandleDrop(target, dndElementList);
		if (status.isOK())
			dropTargetEvent.detail = DND.DROP_NONE;
		return status;
	}

	public IStatus handlePluginTransferDrop(IStructuredSelection aDragSelection, Object aDropTarget) {
		return internalHandleDrop(aDropTarget, dndElementList);
	}

	protected IStatus internalHandleDrop(Object target, List elements) {
		boolean success = false;
		if (target instanceof IServer) {
			success = true;
			if (dndElementList != null) {
				Iterator iterator = elements.iterator();
				while (iterator.hasNext()) {
					Object data2 = iterator.next();
					if (!doRunOnServerAction((IServer) target, data2))
						success = false;
				}
			}
		}
		clear();
		return success ? Status.OK_STATUS : Status.CANCEL_STATUS;
	}

	private void clear() {
		dndElementList = null;
	}

	protected boolean doRunOnServerAction(IServer server, Object data) {
		IModule module = null;
		// If the selection can be directly converted to an IModule, use that.
		module = (IModule) Platform.getAdapterManager().getAdapter(data,
				IModule.class);
		if (module == null) {
			// check if the selection is a project (module) that we can add to
			// the server
			IProject project = (IProject) Platform.getAdapterManager()
					.getAdapter(data, IProject.class);
			if (project != null) {
				IModule[] modules = ServerUtil.getModules(project);
				if (modules != null && modules.length == 1) {
					module = modules[0];
				}
			}
		}

		if (module != null) {
			try {
				IServerWorkingCopy wc = server.createWorkingCopy();
				IModule[] parents = wc.getRootModules(module, null);
				if (parents == null || parents.length == 0)
					return false;

				if (ServerUtil.containsModule(server, parents[0], null)) {
					PublishAction.publish(server, getShell());
					return false;
				}

				IModule[] add = new IModule[] { parents[0] };
				if (wc.canModifyModules(add, null, null).getSeverity() != IStatus.ERROR) {
					wc.modifyModules(new IModule[] { module }, null, null);
					wc.save(false, null);
					PublishAction.publish(server, getShell());
					return true;
				}
			} catch (final CoreException ce) {
				final Shell shell = getShell();
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						EclipseUtil.openError(shell, ce.getLocalizedMessage());
					}
				});
				return true;
			}
		}

		// otherwise, try Run on Server
		final IServer finalServer = server;
		RunOnServerActionDelegate ros = new RunOnServerActionDelegate() {
			public IServer getServer(IModule module2,
					IModuleArtifact moduleArtifact, IProgressMonitor monitor)
					throws CoreException {
				if (!ServerUIPlugin.isCompatibleWithLaunchMode(finalServer,
						launchMode))
					return null;

				if (!ServerUtil.containsModule(finalServer, module2, monitor)) {
					IServerWorkingCopy wc = finalServer.createWorkingCopy();
					try {
						ServerUtil.modifyModules(wc, new IModule[] { module2 },
								new IModule[0], monitor);
						wc.save(false, monitor);
					} catch (CoreException ce) {
						throw ce;
					}
				}

				return finalServer;
			}
		};
		Action action = new Action() {
			// dummy action
		};
		ros.selectionChanged(action, new StructuredSelection(data));

		ros.run(action);
		return true;
	}
}
