/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A fragment used for the Run on Server wizard.
 */
public class RunOnServerWizardFragment extends WizardFragment {
	protected IServer server;
	protected IModule module;
	protected IModuleArtifact moduleArtifact;
	
	protected IClient client;
	protected ILaunchableAdapter launchable;
	
	private ModifyModulesWizardFragment modifyModulesWizardFragment;

	/**
	 * Create the Run on Server wizard with all pages.
	 * 
	 * @param module
	 * @param launchMode
	 * @param moduleArtifact
	 */
	public RunOnServerWizardFragment(IModule module, String launchMode, IModuleArtifact moduleArtifact) {
		super();
		this.module = module;
		this.moduleArtifact = moduleArtifact;
	}

	/**
	 * Create the Run on Server wizard with a default server.
	 * 
	 * @param server a server
	 * @param launchMode
	 * @param moduleArtifact
	 */
	public RunOnServerWizardFragment(IServer server, String launchMode, IModuleArtifact moduleArtifact) {
		super();
		this.server = server;
		this.moduleArtifact = moduleArtifact;
	}

	protected void createChildFragments(List<WizardFragment> list) {
		if (server == null) {
			list.add(new NewServerWizardFragment(module));
			
			list.add(WizardTaskUtil.TempSaveRuntimeFragment);
			list.add(WizardTaskUtil.TempSaveServerFragment);
			modifyModulesWizardFragment = new ModifyModulesWizardFragment(module);
			list.add(modifyModulesWizardFragment);
		}
		
		list.add(new TasksWizardFragment());
		
		list.add(WizardTaskUtil.SaveRuntimeFragment);
		list.add(WizardTaskUtil.SaveServerFragment);
		if (server == null)
			list.add(WizardTaskUtil.SaveHostnameFragment);
		if (client == null || launchable == null){
			list.add(new OptionalClientWizardFragment(moduleArtifact));
		}
	}

	public void setClient(IClient client) {
		this.client = client;			
	}

	public void setLaunchable(ILaunchableAdapter launchable) {
		this.launchable = launchable;	
	}
	
	/**
	 * Expose this to wizard.  For internal use.  Extenders are not expected to override or call.
	 * @return
	 */
	public List<IModule> getModulesToRemove() {
		if (modifyModulesWizardFragment != null) {
			return modifyModulesWizardFragment.getModulesToRemove();
		}
		return new ArrayList<IModule>();
	}
}