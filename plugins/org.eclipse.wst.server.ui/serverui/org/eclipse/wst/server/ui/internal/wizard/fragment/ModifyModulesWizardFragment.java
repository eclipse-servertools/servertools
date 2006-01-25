/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IModuleVisitor;
import org.eclipse.wst.server.core.internal.Server;
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
	
	public void setTaskModel(TaskModel taskModel) {
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
			IServerAttributes server = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
			comp.setServer(server);
			comp.setTaskModel(getTaskModel());
		} else if (module != null) {
			TaskModel taskModel = getTaskModel();
			if (taskModel == null)
				return;
			IServerWorkingCopy server = (IServerWorkingCopy) taskModel.getObject(TaskModel.TASK_SERVER);
			if (server == null) {
				taskModel.putObject(TaskModel.TASK_MODULES, null);
				return;
			}
			
			final List moduleList = new ArrayList();
			if (server != null) {
				((Server) server).visit(new IModuleVisitor() {
					public boolean visit(IModule[] module2) {
						moduleList.add(module2);
						return true;
					}
				}, null);
			}
			
			// add module
			IModule parent = null;
			try {
				IModule[] parents = server.getRootModules(module, null);
				List list = new ArrayList();
				
				if (parents != null && parents.length > 0) {
					parent = parents[0];
					list.add(parent);
				}
				// TODO - get parent modules correct
				if (!moduleList.contains(module)) {
					moduleList.add(new IModule[] { module });
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not find parent module", e);
			}
			
			taskModel.putObject(TaskModel.TASK_MODULES, moduleList);
		}
	}

	public void performFinish(IProgressMonitor monitor) throws CoreException {
		if (comp != null)
			WizardTaskUtil.modifyModules(comp.getModulesToAdd(), comp.getModulesToRemove(), getTaskModel(), monitor);
	}
}