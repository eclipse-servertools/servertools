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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.page.NewServerComposite;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
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
	
	public NewServerWizardFragment() {
		// do nothing
	}
	
	public NewServerWizardFragment(IModule module, String launchMode) {
		this.module = module;
		this.launchMode = launchMode;
	}

	public boolean hasComposite() {
		return true;
	}

	public void enter() {
		super.enter();
		getTaskModel().putObject(TaskModel.TASK_LAUNCH_MODE, launchMode);
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

	protected WizardFragment getWizardFragment(String typeId) {
		try {
			WizardFragment fragment = (WizardFragment) fragmentMap.get(typeId);
			if (fragment != null)
				return fragment;
		} catch (Exception e) {
			// ignore
		}
		
		WizardFragment fragment = ServerUIPlugin.getWizardFragment(typeId);
		if (fragment != null)
			fragmentMap.put(typeId, fragment);
		return fragment;
	}

	public List getChildFragments() {
		List listImpl = new ArrayList();
		createChildFragments(listImpl);
		return listImpl;
	}

	protected void createChildFragments(List list) {
		if (getTaskModel() == null)
			return;
		
		Byte b = (Byte) getTaskModel().getObject(MODE);
		if (b != null && b.byteValue() == MODE_MANUAL) {
			IRuntime runtime = (IRuntime) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
			if (runtime != null && runtime instanceof IRuntimeWorkingCopy) {
				WizardFragment sub = getWizardFragment(runtime.getRuntimeType().getId());
				if (sub != null)
					list.add(sub);
			}
			
			IServerAttributes server = (IServerAttributes) getTaskModel().getObject(TaskModel.TASK_SERVER);
			if (server != null) {
				if (server.getServerType().hasServerConfiguration() && server instanceof ServerWorkingCopy) {
					ServerWorkingCopy swc = (ServerWorkingCopy) server;
					//try {
						if (!runtime.getLocation().isEmpty())
							swc.importConfiguration(runtime, null);
					//} catch (CoreException ce) {
					//	// ignore
					//}
				}
				WizardFragment sub = getWizardFragment(server.getServerType().getId());
				if (sub != null)
					list.add(sub);
			}
		} else if (b != null && b.byteValue() == MODE_EXISTING) {
			/*if (comp != null) {
				IServer server = comp.getServer();
				if (server != null)
					list.add(new TasksWizardFragment());
			}*/
		}
	}
	
	public boolean isComplete() {
		if (comp == null)
			return false;
		return comp.getServer() != null; 
	}
	
	public IServerWorkingCopy getServer() {
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