/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleVisitor;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.task.ModifyModulesTask;
import org.eclipse.wst.server.ui.internal.wizard.page.ModifyModulesComposite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * 
 */
public class ModifyModulesWizardFragment extends WizardFragment {
	protected ModifyModulesComposite comp;
	protected ModifyModulesTask task;
	
	protected IModule module;

	public ModifyModulesWizardFragment() {
		// do nothing
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
		comp = new ModifyModulesComposite(parent, handle, module);
		return comp;
	}
	
	public void setTaskModel(ITaskModel taskModel) {
		super.setTaskModel(taskModel);
		if (comp != null)
			comp.setTaskModel(taskModel);
	}
	
	public List getChildFragments() {
		updateModules();
		return super.getChildFragments();
	}

	public void enter() {
		updateModules();
	}
	
	protected void updateModules() {
		if (comp != null) {
			IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_SERVER);
			comp.setServer(server);
			comp.setTaskModel(getTaskModel());
		} else if (module != null) {
			ITaskModel taskModel = getTaskModel();
			if (taskModel == null)
				return;
			IServerWorkingCopy server = (IServerWorkingCopy) taskModel.getObject(ITaskModel.TASK_SERVER);
			if (server == null) {
				taskModel.putObject(ITaskModel.TASK_MODULE_PARENTS, null);
				taskModel.putObject(ITaskModel.TASK_MODULES, null);
				return;
			}
			
			class Helper {
				List parentList = new ArrayList();
				List moduleList = new ArrayList();
			}
			final Helper help = new Helper();
			if (server != null) {
				ServerUtil.visit(server, new IModuleVisitor() {
					public boolean visit(IModule[] parents2, IModule module2) {
						help.parentList.add(parents2);
						help.moduleList.add(module2);
						return true;
					}
				}, null);
			}
			
			// add module
			IModule parent = null;
			try {
				IModule[] parents = server.getParentModules(module, null);
				List list = new ArrayList();
				
				if (parents != null && parents.length > 0) {
					parent = parents[0];
					list.add(parent);
				}
				if (!help.moduleList.contains(module)) {
					help.moduleList.add(module);
					help.parentList.add(list);
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not find parent module", e);
			}
			
			int size = help.parentList.size();
			List[] parents = new List[size];
			help.parentList.toArray(parents);
			IModule[] modules = new IModule[size];
			help.moduleList.toArray(modules);
			
			taskModel.putObject(ITaskModel.TASK_MODULE_PARENTS, parents);
			taskModel.putObject(ITaskModel.TASK_MODULES, modules);
		}
	}

	public void exit() {
		if (comp != null) {
			createFinishTask();
			task.setAddModules(comp.getModulesToAdd());
			task.setRemoveModules(comp.getModulesToRemove());
		}
	}

	public ITask createFinishTask() {
		if (task == null)
			task = new ModifyModulesTask(); 
		return task;
	}
}