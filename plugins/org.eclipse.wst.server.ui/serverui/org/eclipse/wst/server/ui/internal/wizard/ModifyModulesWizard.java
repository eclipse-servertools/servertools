/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.fragment.ModifyModulesWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * A wizard used to add and remove modules.
 */
public class ModifyModulesWizard extends TaskWizard {
	static class ModifyModulesWizard2 extends WizardFragment {
		protected void createChildFragments(List<WizardFragment> list) {
			list.add(new ModifyModulesWizardFragment(true));
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
}