/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.core.internal.ServerTypeProxy;
import org.eclipse.wst.server.discovery.internal.Activator;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Server type content provider.
 */
public class ServerTypeTreeContentProvider extends AbstractTreeContentProvider {
	protected boolean localhost;

	protected IModuleType moduleType;
	protected String serverTypeId;
	protected boolean includeIncompatibleVersions;
	List<IServerType> serverInstalledList;

	/**
	 * ServerTypeTreeContentProvider constructor.
	 * 
	 * @param moduleType a module type
	 * @param serverTypeId a server type id, or null to match any id
	 */
	public ServerTypeTreeContentProvider(IModuleType moduleType, String serverTypeId) {
		super(false);
		localhost = true;
		
		this.moduleType = moduleType;
		this.serverTypeId = serverTypeId;
		
		fillTree();
	}
	
	public void fillTree() {
		clean();
		serverInstalledList = new ArrayList<IServerType>();
		List<TreeElement> list = new ArrayList<TreeElement>();
		IServerType[] serverTypes = ServerCore.getServerTypes();
		if (serverTypes != null) {
			int size = serverTypes.length;
			for (int i = 0; i < size; i++) {
				IServerType serverType = serverTypes[i];
				if (include(serverType)) {
					try {
						IRuntimeType runtimeType = serverType.getRuntimeType();
						TreeElement ele = getOrCreate(list, runtimeType.getVendor());
						ele.contents.add(serverType);
						elementToParentMap.put(serverType, ele);
						serverInstalledList.add(serverType);
					} catch (Exception e) {
						if (Trace.WARNING) {
							Trace.trace(Trace.STRING_WARNING, "Error in server configuration content provider", e);
						}
					}
				}
			}
		}
		elements = list.toArray();
	}
	
	public void cleanAdapterTree(final TreeViewer treeViewer){
		fillTree();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					treeViewer.refresh("root");
				} catch (Exception e) {
					// ignore - wizard has already been closed
				}
			}
		});
	}
	


	protected boolean include(IServerType serverType) {
		if (serverType != null && serverType instanceof ServerTypeProxy)
			return true;
		if (serverTypeId != null && !serverType.getId().startsWith(serverTypeId))
			return false;
		
		try {
			if (!((ServerType)serverType).supportsManualCreation()) {
				return false;
			}
		} catch (Exception e) {
			// Do nothing since all IServerType should be instance of ServerType.
		}
		
		IRuntimeType runtimeType = serverType.getRuntimeType();
		if (runtimeType == null)
			return false;
		
		String moduleTypeId = null;
		if (moduleType != null)
			moduleTypeId = moduleType.getId();
		if (includeIncompatibleVersions) {
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), moduleTypeId, null))
				return false;
		} else {
			String moduleVersion = null;
			if (moduleType != null)
				moduleVersion = moduleType.getVersion();
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), moduleTypeId, moduleVersion))
				return false;
		}
		
		if (localhost || serverType.supportsRemoteHosts())
			return true;
		
		return false;
	}

	@SuppressWarnings("restriction")
	private boolean compareServers(List serverList, ServerTypeProxy server){
		for (Iterator iterator = serverList.iterator(); iterator.hasNext();) {
			IServerType existingServer = (IServerType) iterator.next();
			if (existingServer.getId().equals(server.getProxyServerId())){
				Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID,"already installed: " + server.getProxyServerId(), null));
				return true;
			}
	
		}
		return false;
	}
	protected void deferredAdapterInitialize(final TreeViewer treeViewer, IProgressMonitor monitor) {
		List<TreeElement> list = new ArrayList<TreeElement>();
		IServerType[] serverTypes = ServerCore.getDownloadableServers(monitor);
		if (serverTypes != null) {
			int size = serverTypes.length;
			for (int i = 0; i < size; i++) {
				IServerType serverType = serverTypes[i];
				if (include(serverType)){
					try {
						IRuntimeType runtimeType = serverType.getRuntimeType();
						TreeElement ele = getOrCreate(list, runtimeType.getVendor());
						if (compareServers(ele.contents, (ServerTypeProxy)serverType))
							continue;
						if ( !compareServers(serverInstalledList, (ServerTypeProxy)serverType)){ 
							// Sometime vendor name is different so need to search the entire list
								ele.contents.add(serverType);
								elementToParentMap.put(serverType, ele);
						}
						else {
							if (ele.contents.isEmpty()) {
								list.remove(ele);
								elementToParentMap.remove(ele);
							}
						}
					} catch (Exception e) {
						if (Trace.WARNING) {
							Trace.trace(Trace.STRING_WARNING, "Error in server configuration content provider", e);
						}
					}
				}
			}
		}
		if (list.size() >0) {
			List<Object> newList = new ArrayList<Object>();
			newList.addAll(Arrays.asList(elements));
			newList.addAll(list);
			elements = newList.toArray();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!treeViewer.getTree().isDisposed())
						treeViewer.refresh("root");
				}
			});
		}
	}

	
	protected boolean checkForNonStubEnvironmentRuntime(IServerType serverType) {
		IRuntimeType runtimeType = serverType.getRuntimeType();
		IRuntime[] runtimes = ServerUIPlugin.getRuntimes(runtimeType);
		if (runtimes == null || runtimes.length == 0)
			return false;
		
		int size = runtimes.length;
		for (int i = 0; i < size; i++) {
			if (!runtimes[i].isStub())
				return true;
		}
		return false;
	}

	public void setLocalhost(boolean local) {
		localhost = local;
		fillTree();
	}

	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		fillTree();
	}
}