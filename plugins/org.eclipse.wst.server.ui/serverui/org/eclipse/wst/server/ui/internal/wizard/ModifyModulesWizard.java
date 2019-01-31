/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.PreferenceUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to add and remove modules.
 */
public class ModifyModulesWizard extends TaskWizard {
	static ModifyModulesWizardFragment modifyModulesWizardFragment = new ModifyModulesWizardFragment(true);
	
	static class ModifyModulesWizard2 extends WizardFragment {
		protected void createChildFragments(List<WizardFragment> list) {
			list.add(modifyModulesWizardFragment);
			list.add(WizardTaskUtil.SaveServerFragment);
			
			list.add(new WizardFragment() {
				public void performFinish(IProgressMonitor monitor) throws CoreException {
					IServerAttributes svr = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
					if (svr instanceof IServer) {
						IServer server = (IServer) svr;
						if (server.getServerState() != IServer.STATE_STOPPED &&
								ServerUIPlugin.getPreferences().getPublishOnAddRemoveModule()) {
							IAdaptable info = null;
							/*IAdaptable info = new IAdaptable() {
								public Object getAdapter(Class adapter) {
									if (Shell.class.equals(adapter))
										return shell;
									return null;
								}
							};*/
							server.publish(IServer.PUBLISH_INCREMENTAL, null, info, null);
						}
					}
				}
			});
		}
	}

	/**
	 * ModifyModulesWizard constructor.
	 * 
	 * @param server a server
	 */
	public ModifyModulesWizard(IServer server) {
		super(Messages.wizModuleWizardTitle, new ModifyModulesWizard2());
		
		if (server != null)
			getTaskModel().putObject(TaskModel.TASK_SERVER, server.createWorkingCopy());
	}
	
	/**
	 * This is not intended to be used by extenders.  The prompt to remove modules dialog appears there are modules to be removed.
	 * 
	 */
	@Override
	public boolean performFinish() {
		// This code should instead be done in the ModifyModulesWizardFragment.  However, we need to do this prompt up-front
		// prior to any fragments being executed in another thread.  If this prompt is moved to the fragment, then we need a 
		// a way to get access to the UI shell even though the fragments are run on a non-UI thread.  Secondly, the TaskWizard
		// needs to support cancel to undo previously run fragments and stop later fragments from running.  This is because the\
		// prompt dialog allows the user to cancel.   See also RunOnServerWizard.
		List<IModule> modulesToRemove = modifyModulesWizardFragment.getModulesToRemove();
		if (modulesToRemove.size() > 0) {
			IServerAttributes server = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
			boolean doRemove = PreferenceUtil.confirmModuleRemoval(server, getContainer().getShell(), modulesToRemove);
			if (!doRemove) {
				return false;
			}
		}
		return super.performFinish();
	}
}