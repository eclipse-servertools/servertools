package org.eclipse.wst.server.ui.internal.publish;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.wst.server.core.IPublishControl;
import org.eclipse.wst.server.core.internal.AbstractPublisher;
import org.eclipse.wst.server.core.internal.SmartPublisher;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IPublishManagerDelegate;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteFolder;
import org.eclipse.wst.server.core.resources.IRemoteResource;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A visual publisher that allows the user to select
 * which files to publish from a tree table.
 */
public class VisualPublisher implements IPublishManagerDelegate {
	//protected IPublishControl control;
	protected Map publishControls;
	protected List modules;

	// use a smart publisher to initially fill in publish selection
	protected SmartPublisher smartPublisher;

	protected Map deleteMap;
	protected Map publishMap;

	protected Map deletedResources;

	public static final byte STATUS_NEW = 0;
	public static final byte STATUS_UNCHANGED = 1;
	public static final byte STATUS_NEWER_LOCALLY = 2;
	public static final byte STATUS_NEWER_REMOTELY = 3;
	public static final byte STATUS_NEWER_UNKNOWN = 4;

	/**
	 * VisualPublisher constructor comment.
	 */
	public VisualPublisher() {
		super();
	}

	protected IRemoteResource findRemoteResource(IRemoteFolder folder, IPath path) {
		Iterator iterator = folder.getContents().iterator();
		while (iterator.hasNext()) {
			IRemoteResource remote = (IRemoteResource) iterator.next();
			if (path.equals(remote.getPath()))
				return remote;
			if (remote instanceof IRemoteFolder) {
				IRemoteFolder folder2 = (IRemoteFolder) remote;
				IPath folderPath = folder2.getPath();
				if (folderPath.isPrefixOf(path)) {
					IRemoteResource rem = findRemoteResource(folder2, path);
					if (rem != null)
						return rem;
				}
			}
		}
		return null;
	}

	protected IRemoteResource findRemoteResource(IModule module, IPath path) {
		if (path == null)
			return null;
	
		IRemoteResource[] remoteResources = getPublishControl(module).getRemoteResources();
		if (remoteResources != null) {
			int size = remoteResources.length;
			for (int i = 0; i < size; i++) {
				IRemoteResource remote = remoteResources[i];
				if (path.equals(remote.getPath()))
					return remote;
				if (remote instanceof IRemoteFolder) {
					IRemoteFolder folder2 = (IRemoteFolder) remote;
					IPath folderPath = folder2.getPath();
					if (folderPath.isPrefixOf(path)) {
						IRemoteResource rem = findRemoteResource(folder2, path);
						if (rem != null)
							return rem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the resources that should be deleted from
	 * the server because they no longer exist in the
	 * workbench.
	 *
	 * @return java.util.List
	 */
	public List getDeletedResources(IModule module) {
		try {
			List list = (List) deletedResources.get(module);
			if (list != null)
				return list;
		} catch (Exception e) {
		}
	
		List visited = new ArrayList();
		List unvisited = new ArrayList();
	
		try {
			IModuleResource resources[] = module.members();
			int size = resources.length;
			for (int i = 0; i < size; i++) {
				visit(module, visited, resources[i]);
			}
		} catch (Exception e) {
			Trace.trace("Error visiting resources", e);
		}
	
		IRemoteResource[] remoteResources = getPublishControl(module).getRemoteResources();
		if (remoteResources != null) {
			int size = remoteResources.length;
			for (int i = 0; i < size; i++) {
				IRemoteResource remote = remoteResources[i];
				IPath path = remote.getPath();
				if (!visited.contains(path) && !unvisited.contains(remote))
					unvisited.add(remote);
	
				if (remote instanceof IRemoteFolder)
					unvisit(visited, unvisited, (IRemoteFolder) remote);
			}
		}
	
		deletedResources.put(module, unvisited);
		return unvisited;
	}

	/**
	 * Returns the publish control, used to obtain information about
	 * the publishing.
	 *
	 * @return org.eclipse.wst.server.core.model.IPublishControl
	 */
	public IPublishControl getPublishControl(IModule module) {
		try {
			return (IPublishControl) publishControls.get(module);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns the modules.
	 *
	 * @return java.util.List
	 */
	public List getModules() {
		return modules;
	}

	/**
	 * Returns the resource state.
	 *
	 * @return byte
	 */
	public byte getResourceStatus(IModuleResource resource, IPath path) {
		IModule module = resource.getModule();
		IRemoteResource remote = findRemoteResource(module, path);
		if (remote == null)
			return STATUS_NEW;
	
		if (resource instanceof IModuleFolder) {
			return STATUS_UNCHANGED;
		}
	
		boolean changedRemote = false;
		long timestamp = getPublishControl(module).getPublishedTimestamp(remote);
		if (timestamp == IRemoteResource.TIMESTAMP_UNKNOWN || remote.getTimestamp() != timestamp)
			changedRemote = true;
	
		boolean changedLocally = false;
		timestamp = getPublishControl(module).getPublishedTimestamp(resource);
		if (timestamp == IRemoteResource.TIMESTAMP_UNKNOWN || resource.getTimestamp() != timestamp)
			changedLocally = true;
	
		if (changedLocally && changedRemote)
			return STATUS_NEWER_UNKNOWN;
		else if (changedLocally)
			return STATUS_NEWER_LOCALLY;
		else if (changedRemote)
			return STATUS_NEWER_REMOTELY;
		else
			return STATUS_UNCHANGED;
	}

	/**
	 * Returns the list of remote resources to delete from the
	 * remote system.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public List getResourcesToDelete(IModule module) {
		try {
			List list = (List) deleteMap.get(module);
			if (list != null)
				return list;
		} catch (Exception e) {	}
	
		List list = new ArrayList();
		deleteMap.put(module, list);
		return list;
	}

	/**
	 * Returns the list of resources to publish to the remote
	 * system.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 * @return java.util.List
	 */
	public List getResourcesToPublish(IModule module) {
		try {
			List list = (List) publishMap.get(module);
			if (list != null)
				return list;
		} catch (Exception e) {	}
	
		List list = new ArrayList();
		publishMap.put(module, list);
		return list;
	}

	/**
	 * Sets the publish control, used to obtain information about
	 * the publishing.
	 *
	 * Sets the publish state, used to determine the timestamps
	 * of the last publishing action.
	 * 
	 * Resolve which resources to publish or delete.
	 *
	 * @param control org.eclipse.wst.server.core.IPublishControl[]
	 * @param module org.eclipse.wst.server.core.model.IModule[]
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void resolve(IPublishControl[] control, IModule[] module, IProgressMonitor monitor) {
		int size = control.length;
		
		deleteMap = new HashMap();
		publishMap = new HashMap();
		deletedResources = new HashMap();
		
		publishControls = new HashMap(size);
		modules = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			publishControls.put(module[i], control[i]);
			modules.add(module[i]);
		}
		
		smartPublisher = new SmartPublisher();	
		smartPublisher.resolve(control, module, new NullProgressMonitor());
	
		Iterator iterator = getModules().iterator();
		while (iterator.hasNext()) {
			IModule module2 = (IModule) iterator.next();
			
			List publish = smartPublisher.getResourcesToPublish(module2);
			publishMap.put(module2, publish);
			
			List delete = smartPublisher.getResourcesToDelete(module2);
			deleteMap.put(module2, delete);
		}
		
		class Temp {
			boolean cancel = false;
		}
		final Temp temp = new Temp();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell shell = EclipseUtil.getShell();
				PublisherWizard wizard = new PublisherWizard(VisualPublisher.this);
				WizardDialog dialog = new WizardDialog(shell, wizard);
				temp.cancel = (dialog.open() == Window.CANCEL);
			}
		});
		if (temp.cancel) {
			deleteMap = new HashMap();
			publishMap = new HashMap();
			monitor.setCanceled(true);
			return;
		}
		
		// sort delete and publish lists
		iterator = getModules().iterator();
		while (iterator.hasNext()) {
			IModule module2 = (IModule) iterator.next();
			
			List publish = getResourcesToPublish(module2);
			AbstractPublisher.sortPublishList(publish);
	
			List delete = getResourcesToDelete(module2);
			AbstractPublisher.sortDeletionList(delete);
		}
	}

	/**
	 * 
	 */
	private void unvisit(List visited, List unvisited, IRemoteFolder folder) {
		try {
			Iterator iterator = folder.getContents().iterator();
			while (iterator.hasNext()) {
				IRemoteResource remote = (IRemoteResource) iterator.next();
				IPath path = remote.getPath();
				if (!visited.contains(path) && !unvisited.contains(remote))
					unvisited.add(remote);
	
				if (remote instanceof IRemoteFolder)
					unvisit(visited, unvisited, (IRemoteFolder) remote);
			}
		} catch (Exception e) {
			Trace.trace("Error unvisiting in visual publish", e);
		}
	}
	
	/**
	 * 
	 */
	private void visit(IModule module, List visited, IModuleResource resource) {
		try {
			IPath path = getPublishControl(module).getMappedLocation(resource);
			if (path != null && !visited.contains(path))
				visited.add(path);
		
			if (resource instanceof IModuleFolder) {
				IModuleResource[] resources = ((IModuleFolder) resource).members();
				int size = resources.length;
				for (int i = 0; i < size; i++) {
					if (resources[i] instanceof IModuleFolder) {
						visit(module, visited, resources[i]);
					} else {
						path = getPublishControl(module).getMappedLocation(resources[i]);
						if (path != null && !visited.contains(path))
							visited.add(path);
					}
				}
			}
		} catch (Exception e) {
			Trace.trace("Error in visual publish visit", e);
		}
	}
}
