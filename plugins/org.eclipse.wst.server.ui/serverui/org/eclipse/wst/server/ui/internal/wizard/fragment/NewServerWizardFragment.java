/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.page.NewServerComposite;
import org.eclipse.wst.server.ui.internal.wizard.page.WizardUtil;
import org.eclipse.wst.server.ui.wizard.IWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class NewServerWizardFragment extends WizardFragment {
	public static final String MODE = "mode";
	public static final byte MODE_EXISTING = 0;
	public static final byte MODE_DETECT = 1;
	public static final byte MODE_MANUAL= 2;

	protected NewServerComposite comp;
	protected IModule module;
	protected String launchMode;
	
	protected Map fragmentMap = new HashMap();
	protected Map configMap = new HashMap();
	
	public NewServerWizardFragment() { }
	
	public NewServerWizardFragment(IModule module, String launchMode) {
		this.module = module;
		this.launchMode = launchMode;
	}

	public boolean hasComposite() {
		return true;
	}

	public void enter() {
		super.enter();
		getTaskModel().putObject(ITaskModel.TASK_LAUNCH_MODE, launchMode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		comp = new NewServerComposite(parent, wizard, module, launchMode);
		if (getTaskModel() != null)
			comp.setTaskModel(getTaskModel());
		return comp;
	}

	protected void createConfiguration(IServerWorkingCopy server) {
		ITaskModel model = getTaskModel();
		IRuntime runtime = (IRuntime) model.getObject(ITaskModel.TASK_RUNTIME);
		
		IServerType type = server.getServerType();
		if (type.hasServerConfiguration()) {
			server.setServerConfiguration(null);
			IStatus status = null;
			if (runtime != null)
				status = runtime.validate();
			if (status == null || status.isOK()) {
				try {
					IFile file = null;
					if (ServerCore.getServerPreferences().isCreateResourcesInWorkspace())
						file = ServerUtil.getUnusedServerConfigurationFile(WizardUtil.getServerProject(), type.getServerConfigurationType());
					
					/*IServerConfigurationWorkingCopy serverConfiguration = type.getServerConfigurationType().importFromRuntime(null, file, runtime, new NullProgressMonitor());
					ServerUtil.setServerConfigurationDefaultName(serverConfiguration);*/
					IServerConfigurationWorkingCopy serverConfiguration = getServerConfiguration(type.getServerConfigurationType(), file, runtime);
					model.putObject(ITaskModel.TASK_SERVER_CONFIGURATION, serverConfiguration);
					server.setServerConfiguration(serverConfiguration);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not create configuration", e);
				}
			}
		}
	}
	
	protected IServerConfigurationWorkingCopy getServerConfiguration(IServerConfigurationType type, IFile file, IRuntime runtime) throws CoreException {
		Object key = type.getId() + "|" + file + "|" + runtime;
		try {
			IServerConfigurationWorkingCopy serverConfiguration = (IServerConfigurationWorkingCopy) configMap.get(key);
			if (serverConfiguration != null)
				return serverConfiguration;
		} catch (Exception e) { }

		IServerConfigurationWorkingCopy serverConfiguration = type.importFromRuntime(null, file, runtime, new NullProgressMonitor());
		ServerUtil.setServerConfigurationDefaultName(serverConfiguration);
		configMap.put(key, serverConfiguration);
		return serverConfiguration;
	}

	protected IWizardFragment getWizardFragment(String typeId) {
		try {
			IWizardFragment fragment = (IWizardFragment) fragmentMap.get(typeId);
			if (fragment != null)
				return fragment;
		} catch (Exception e) { }
		
		IWizardFragment fragment = ServerUICore.getWizardFragment(typeId);
		if (fragment != null)
			fragmentMap.put(typeId, fragment);
		return fragment;
	}
	
	public List getChildFragments() {
		listImpl = new ArrayList();
		createSubFragments(listImpl);
		return listImpl;
	}

	public void createSubFragments(List list) {
		if (getTaskModel() == null)
			return;

		Byte b = (Byte) getTaskModel().getObject(MODE);
		if (b != null && b.byteValue() == MODE_MANUAL) {
			IRuntime runtime = (IRuntime) getTaskModel().getObject(ITaskModel.TASK_RUNTIME);
			if (runtime != null && runtime instanceof IRuntimeWorkingCopy) {
				IWizardFragment sub = getWizardFragment(runtime.getRuntimeType().getId());
				if (sub != null)
					list.add(sub);
			}
			
			IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
			if (server != null) {
				createConfiguration(server);
				IWizardFragment sub = getWizardFragment(server.getServerType().getId());
				if (sub != null)
					list.add(sub);
			
				IServerConfiguration serverConfiguration = server.getServerConfiguration();
				if (serverConfiguration != null) {
					sub = getWizardFragment(serverConfiguration.getServerConfigurationType().getId());
					if (sub != null)
						list.add(sub);
				}
			}
			//list.add(new TasksWizardFragment());
		} else if (b != null && b.byteValue() == MODE_EXISTING) {
			/*if (comp != null) {
				IServer server = comp.getServer();
				if (server != null)
					list.add(new TasksWizardFragment());
			}*/
		}
	}
	
	public IServer getServer() {
		if (comp == null)
			return null;
		return comp.getServer();
	}

	public boolean isPreferredServer() {
		if (comp == null)
			return false;
		return comp.isPreferredServer();
	}
}