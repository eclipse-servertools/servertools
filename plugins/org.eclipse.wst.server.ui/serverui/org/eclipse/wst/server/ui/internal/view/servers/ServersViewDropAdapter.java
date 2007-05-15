/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
/**
 *
 */
public class ServersViewDropAdapter extends ViewerDropAdapter {
	protected ServersViewDropAdapter(Viewer viewer) {
		super(viewer);
	}

	public void dragEnter(DropTargetEvent event) {
		if (event.detail == DND.DROP_DEFAULT)
			event.detail = DND.DROP_COPY;

		super.dragEnter(event);
	}

	public boolean performDrop(Object data) {
		Object target = getCurrentTarget();
		IServer server = null;
		if (target instanceof IServer)
			server = (IServer) target;
		
		if (server == null)
			return false;
		
		Iterator iterator = null;
		if (data instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) data;
			iterator = sel.iterator();
		}
		
		if (iterator == null)
			return false;
		
		boolean b = true;
		while (iterator.hasNext()) {
			Object data2 = iterator.next();
			if (!doSel(server, data2))
				b = false;
		}
		return b;
	}

	protected boolean doSel(IServer server, Object data) {
		// check if the selection is a project (module) that we can add to the server
		IProject project = (IProject) Platform.getAdapterManager().getAdapter(data, IProject.class);
		if (project != null) {
			IModule[] modules = ServerUtil.getModules(project);
			if (modules != null && modules.length == 1) {
				try {
					IServerWorkingCopy wc = server.createWorkingCopy();
					IModule[] parents = wc.getRootModules(modules[0], null);
					if (parents == null || parents.length == 0)
						return false;
					
					if (ServerUtil.containsModule(server, parents[0], null))
						return false;
					
					IModule[] add = new IModule[] { parents[0] };
					if (wc.canModifyModules(add, null, null).getSeverity() != IStatus.ERROR) {
						wc.modifyModules(modules, null, null);
						wc.save(false, null);
						return true;
					}
				} catch (final CoreException ce) {
					final Shell shell = getViewer().getControl().getShell();
					shell.getDisplay().asyncExec(new Runnable() {
						public void run() {
							EclipseUtil.openError(shell, ce.getLocalizedMessage());
						}
					});
					return true;
				}
			}
		}
		
		// otherwise, try Run on Server
		final IServer finalServer = server;
		RunOnServerActionDelegate ros = new RunOnServerActionDelegate() {
			public IServer getServer(IModule module, IModuleArtifact moduleArtifact, IProgressMonitor monitor) throws CoreException {
				if (!ServerUIPlugin.isCompatibleWithLaunchMode(finalServer, launchMode))
					return null;
				
				if (!ServerUtil.containsModule(finalServer, module, monitor)) {
					IServerWorkingCopy wc = finalServer.createWorkingCopy();
					try {
						ServerUtil.modifyModules(wc, new IModule[] { module }, new IModule[0], monitor);
						wc.save(false, monitor);
					} catch (CoreException ce) {
						throw ce;
					}
				}
				
				return finalServer;
			}
		};
		Action action = new Action() {
			//
		};
		ros.selectionChanged(action, new StructuredSelection(data));
		
		//if (!action.isEnabled())
		//	return false;
		
		ros.run(action);
		return true;
	}

	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		if (target == null)
			return false;
		/*IServer server = null;
		if (target instanceof IServer)
			server = (IServer) target;*/
		//if (!ServerUIPlugin.hasModuleArtifact(target))
		//	return false;
		
		Trace.trace(Trace.FINER, "Drop target: " + target + " " + operation + " " + transferType);
		
		if (FileTransfer.getInstance().isSupportedType(transferType))
			return true;
		if (ResourceTransfer.getInstance().isSupportedType(transferType))
			return true;
		if (LocalSelectionTransfer.getInstance().isSupportedType(transferType))
			return true;
		
		return false;
	}

	/**
    * Returns the resource selection from the LocalSelectionTransfer.
    * 
    * @return the resource selection from the LocalSelectionTransfer
    */
   /*private IResource[] getSelectedResources() {
       ArrayList selectedResources = new ArrayList();

       ISelection selection = LocalSelectionTransfer.getInstance()
               .getSelection();
       if (selection instanceof IStructuredSelection) {
           IStructuredSelection ssel = (IStructuredSelection) selection;
           for (Iterator i = ssel.iterator(); i.hasNext();) {
               Object o = i.next();
               if (o instanceof IResource) {
                   selectedResources.add(o);
               }
               else if (o instanceof IAdaptable) {
                   IAdaptable a = (IAdaptable) o;
                   IResource r = (IResource) a.getAdapter(IResource.class);
                   if (r != null) {
                       selectedResources.add(r);
                   }
               }
           }
       }
       return (IResource[]) selectedResources.toArray(new IResource[selectedResources.size()]);
   }*/
}