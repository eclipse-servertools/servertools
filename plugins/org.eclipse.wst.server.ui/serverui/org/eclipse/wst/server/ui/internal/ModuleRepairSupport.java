/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IServerLifecycleEventHandler;
import org.eclipse.wst.server.ui.ServerUICore;
/**
 * 
 */
public class ModuleRepairSupport implements IServerLifecycleEventHandler {
	private static final String ROOT = "root";
	
	protected static final Object[] EMPTY = new Object[0];
	
	// content and label provider for server editor save dialog
	public class ServerEditorContentProvider implements IStructuredContentProvider {
		protected Object[] elements;
		
		public ServerEditorContentProvider(Object[] elements) {
			this.elements = elements;	
		}
		
		public void dispose() { }
		
		public Object[] getElements(Object element) {
			if (ROOT.equals(element))
				return elements;
			
			return EMPTY;
		}
		
		public void inputChanged(Viewer viewer2, Object oldInput, Object newInput) { }
	}

	public class ServerEditorLabelProvider implements ILabelProvider {
		public void addListener(ILabelProviderListener listener) { }
		
		public void removeListener(ILabelProviderListener listener) { }
		
		public Image getImage(Object element) {
			return ServerUICore.getLabelProvider().getImage(element);
		}
		
		public String getText(Object element) {
			return ServerUICore.getLabelProvider().getText(element);
		}
		
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		
		public void dispose() { }
	}
	
	public boolean[] handleModuleServerEvents(final IServerLifecycleEvent[] events) {
		if (ServerCore.getServerPreferences().getModuleRepairStatus() == IServerPreferences.REPAIR_NEVER)
			return null;
		
		// save open editors
		final List list = new ArrayList();
		int size = events.length;
		boolean[] bool = new boolean[size];
		for (int i = 0; i < size; i++) {
			bool[i] = true;
			IServer server = events[i].getServer();
			
			if (!list.contains(server) && server.isWorkingCopiesExist() && server.isAWorkingCopyDirty())
				list.add(server);
		}
		
		//final ILabelProvider labelProvider = new ModuleRepairSupport.ServerEditorLabelProvider();
		//final IStructuredContentProvider contentProvider = new ServerEditorContentProvider(list.toArray());
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell shell = EclipseUtil.getShell();
				
				/*if (!list.isEmpty()) {
					ListSelectionDialog dialog1 = new ListSelectionDialog(shell, ROOT,
						contentProvider, labelProvider, ServerUIPlugin.getResource("%dialogRepairConfigurationSaveMessage"));
					dialog1.setTitle(ServerUIPlugin.getResource("%dialogRepairConfigurationTitle"));
					dialog1.setBlockOnOpen(true);
					dialog1.setInitialSelections(list.toArray());
					dialog1.open();
					
					Object[] result = dialog1.getResult();
					// save selected editors
					if (result != null) {
						int size2 = result.length;
						for (int i = 0; i < size2; i++) {
							IServerConfiguration config = (IServerConfiguration) result[i];
							IResource resource = ServerCore.getResourceManager().getServerResourceLocation(config);
							IServerResource serverResource = ServerCore.getEditManager().getEditModel(resource, new NullProgressMonitor());
							try {
								serverResource.save(resource.getProject(), resource.getProjectRelativePath(), new NullProgressMonitor());
								
								GlobalCommandManager.getInstance().reload(resource, new NullProgressMonitor());
								GlobalCommandManager.getInstance().resourceSaved(resource);
								GlobalCommandManager.getInstance().updateTimestamps(resource);
								list.remove(config);
							} catch (CoreException e) {
								// ignore error for now
							}
						}
					}
					
					// TODO don't try to modify any unsaved configurations
					/*int size3 = list.size();
					for (int i = 0; i < size3; i++) {
						IServer server = (IServer) list.get(i);
						int size2 = diskFixes.size();
						for (int j = size2 - 1; j >= 0; j--) {
							FixInfo info = (FixInfo) diskFixes.get(j);
							if (info.server.equals(server)) {
								diskFixes.remove(j);
								size2--;
							}
						}
					}
				}*/
				
				ModuleRepairDialog dialog = new ModuleRepairDialog(shell, events);
				dialog.open();
			}
		});
		
		return bool;
	}
}