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
package org.eclipse.wst.server.ui.internal.wizard.fragment;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IModuleVisitor;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.page.ModifyModulesComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class ModifyModulesWizardFragment extends WizardFragment {
	protected ModifyModulesComposite comp;

	protected IModule module;
	protected boolean showPublishOption;

	public ModifyModulesWizardFragment() {
		// do nothing
	}

	public ModifyModulesWizardFragment(boolean showPublishOption) {
		this.showPublishOption = showPublishOption;
	}

	public ModifyModulesWizardFragment(IModule module) {
		this.module = module;
	}

	public boolean hasComposite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.internal.task.WizardTask#getWizardPage()
	 */
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		comp = new ModifyModulesComposite(parent, handle, module, showPublishOption);
		return comp;
	}

	public void setTaskModel(TaskModel taskModel) {
		super.setTaskModel(taskModel);
		if (comp != null)
			comp.setTaskModel(taskModel);
	}

	public List getChildFragments() {
		return super.getChildFragments();
	}

	public void enter() {
		updateModules();
		// ask the composite to refresh
		comp.refresh();
	}

	protected void updateModules() {
		if (comp != null) {
			IServerAttributes server = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
			comp.setTaskModel(getTaskModel());
			comp.setServer(server);
		} else if (module != null) {
			TaskModel taskModel = getTaskModel();
			if (taskModel == null)
				return;
			IServerWorkingCopy server = (IServerWorkingCopy) taskModel.getObject(TaskModel.TASK_SERVER);
			if (server == null) {
				taskModel.putObject(TaskModel.TASK_MODULES, null);
				return;
			}
			
			final List<IModule[]> moduleList = new ArrayList<IModule[]>();
			((Server) server).visit(new IModuleVisitor() {
				public boolean visit(IModule[] module2) {
					moduleList.add(module2);
					return true;
				}
			}, null);
			
			// add module
			IModule parent = null;
			try {
				IModule[] parents = server.getRootModules(module, null);
				List<IModule> list = new ArrayList<IModule>();
				
				if (parents != null && parents.length > 0) {
					parent = parents[0];
					list.add(parent);
				}
				// TODO - get parent modules correct
				if (!moduleList.contains(module)) {
					moduleList.add(new IModule[] { module });
				}
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not find parent module", e);
				}
			}
			
			taskModel.putObject(TaskModel.TASK_MODULES, moduleList);
		}
	}

	public boolean isComplete() {
		if (comp != null)
			return comp.isComplete();
		
		return true;
	}
	
	/**
	 * Expose this to wizard.  For internal use.  Extenders are not expected to override or call.
	 * @return
	 */
	public List<IModule> getModulesToRemove() {
		return comp.getModulesToRemove();
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException {
		if (comp != null) {
			WizardTaskUtil.modifyModules(comp.getModulesToAdd(), comp.getModulesToRemove(), getTaskModel(), monitor);
			if (showPublishOption)
				ServerUIPlugin.getPreferences().setPublishOnAddRemoveModule(comp.shouldPublishImmediately());
		}
	}
}