/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A fragment used to select a client.
 */
public class OptionalClientWizardFragment extends WizardFragment {
	protected SelectClientWizardFragment fragment;
	protected IClient[] clients;
	protected String launchMode;
	protected IModuleArtifact moduleArtifact;
	protected IServer lastServer;

	public OptionalClientWizardFragment(IModuleArtifact moduleArtifact, String launchMode) {
		super();
		this.launchMode = launchMode;
		this.moduleArtifact = moduleArtifact;
	}

	protected void createChildFragments(List list) {
		if (clients != null && clients.length > 1) {
			fragment = new SelectClientWizardFragment(clients);
			list.add(fragment);
		} else
			fragment = null;
	}

	protected void updateClients() {
		try {
			IServer server = (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
			if (lastServer == null && server == null)
				return;
			if (lastServer != null && lastServer.equals(server))
				return;
			
			lastServer = server;
			
			// get the launchable adapter and module object
			ILaunchableAdapter launchableAdapter = null;
			Object launchable = null;
			ILaunchableAdapter[] adapters = ServerPlugin.getLaunchableAdapters();
			if (adapters != null) {
				int size2 = adapters.length;
				IStatus lastStatus = null;
				for (int j = 0; j < size2; j++) {
					ILaunchableAdapter adapter = adapters[j];
					try {
						Object launchable2 = adapter.getLaunchable(server, moduleArtifact);
						Trace.trace(Trace.FINEST, "adapter= " + adapter + ", launchable= " + launchable2);
						if (launchable2 != null) {
							launchableAdapter = adapter;
							launchable = launchable2;
						}
					} catch (CoreException ce) {
						lastStatus = ce.getStatus();
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Error in launchable adapter", e);
					}
				}
				if (launchable == null && lastStatus != null) {
					EclipseUtil.openError(null, lastStatus);
					return; // TODO
				}
			}
			if (launchable == null) {
				launchableAdapter = new ILaunchableAdapter() {
					public String getId() {
						return "org.eclipse.wst.server.ui.launchable.adapter.default";
					}

					public Object getLaunchable(IServer server3, IModuleArtifact moduleArtifact2) throws CoreException {
						return "launchable";
					}
				};
				try {
					launchable = launchableAdapter.getLaunchable(server, moduleArtifact);
				} catch (CoreException ce) {
					// ignore
				}
			}
			
			clients = getClients(server, launchable, launchMode);
			updateChildFragments();
		} catch (Exception e) {
			// ignore
		}
	}

	public void enter() {
		updateClients();
	}

	public List getChildFragments() {
		updateClients();
		return super.getChildFragments();
	}

	public void setTaskModel(TaskModel taskModel) {
		super.setTaskModel(taskModel);
		updateClients();
	}

	/**
	 * Returns the launchable clients for the given server and launchable
	 * object.
	 * 
	 * @param server org.eclipse.wst.server.core.IServer
	 * @param launchable
	 * @param launchMode String
	 * @return an array of clients
	 */
	public static IClient[] getClients(IServer server, Object launchable, String launchMode) {
		ArrayList list = new ArrayList(5);
		IClient[] clients = ServerPlugin.getClients();
		if (clients != null) {
			int size = clients.length;
			for (int i = 0; i < size; i++) {
				Trace.trace(Trace.FINEST, "client= " + clients[i]);
				if (clients[i].supports(server, launchable, launchMode))
					list.add(clients[i]);
			}
		}
		
		IClient[] clients2 = new IClient[list.size()];
		list.toArray(clients2);
		return clients2;
	}

	/**
	 * Return the selected client.
	 * 
	 * @return the client
	 */
	public IClient getSelectedClient() {
		if (fragment == null)
			return null;
		return fragment.getSelectedClient();
	}
}