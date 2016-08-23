/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard;

import java.util.HashMap;
import java.util.List;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.core.internal.ILaunchableAdapter;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.PreferenceUtil;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.wst.server.ui.internal.wizard.fragment.RunOnServerWizardFragment;
/**
 * A wizard used for Run on Server.
 */
public class RunOnServerWizard extends TaskWizard {
	
	private static RunOnServerWizardFragment fragment;
	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param module a module
	 * @param launchMode a launch mode
	 * @param moduleArtifact a module artifact
	 * 
	 * @deprecated
	 */
	public RunOnServerWizard(IModule module, String launchMode, IModuleArtifact moduleArtifact) {
		this(module, launchMode,moduleArtifact, null);
	}
	
	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param module a module
	 * @param launchMode a launch mode
	 * @param moduleArtifact a module artifact
	 * @param properties a HashMap with the key/value pair that defines the behaviour of the wizard 
	 */
	public RunOnServerWizard(IModule module, String launchMode, IModuleArtifact moduleArtifact, HashMap properties) {
		super(Messages.wizRunOnServerTitle, createRootWizard(module, launchMode, moduleArtifact, properties));
		
		setNeedsProgressMonitor(true);
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			setWindowTitle(Messages.wizDebugOnServerTitle);
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			setWindowTitle(Messages.wizProfileOnServerTitle);
		getTaskModel().putObject(TaskModel.TASK_LAUNCH_MODE, launchMode);
	}

	private static RunOnServerWizardFragment createRootWizard(IModule module, String launchMode, IModuleArtifact moduleArtifact, HashMap properties){
		fragment = new RunOnServerWizardFragment(module, launchMode, moduleArtifact);
		setFragmentProperties(fragment, properties);
		return fragment;
	}
	
	private static RunOnServerWizardFragment createRootWizard	(IServer server, String launchMode, IModuleArtifact moduleArtifact,HashMap properties) {
		fragment = new RunOnServerWizardFragment(server, launchMode, moduleArtifact);
		setFragmentProperties(fragment, properties);		
		return fragment;
	}
	
	private static void setFragmentProperties(RunOnServerWizardFragment fragment, HashMap properties){
		if (properties != null){
			fragment.setClient((IClient)properties.get(RunOnServerActionDelegate.ROS_CLIENT));
			fragment.setLaunchable((ILaunchableAdapter)properties.get(RunOnServerActionDelegate.ROS_LAUNCHABLE));
		}
	}
	
	/**
	 * RunOnServerWizard constructor comment.
	 * 
	 * @param server a server
	 * @param launchMode a launch mode
	 * @param moduleArtifact a module artifact
	 */
	public RunOnServerWizard(IServer server, String launchMode, IModuleArtifact moduleArtifact,HashMap properties) {
		super(Messages.wizRunOnServerTitle, createRootWizard(server,launchMode,moduleArtifact, properties));
		
		setNeedsProgressMonitor(true);
		if (ILaunchManager.DEBUG_MODE.equals(launchMode))
			setWindowTitle(Messages.wizDebugOnServerTitle);
		else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
			setWindowTitle(Messages.wizProfileOnServerTitle);
		
		getTaskModel().putObject(TaskModel.TASK_SERVER, server);
		getTaskModel().putObject(TaskModel.TASK_LAUNCH_MODE, launchMode);
		addPages();
	}

	/**
	 * Return the server.
	 * 
	 * @return the server
	 */
	public IServer getServer() {
		try {
			return (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return if the user wants to use the server as a default.
	 * 
	 * @return true if the server should be the default
	 */
	public boolean isPreferredServer() {
		try {
			Boolean b = (Boolean) getTaskModel().getObject(WizardTaskUtil.TASK_DEFAULT_SERVER);
			return b.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return the selected client.
	 * 
	 * @return the client
	 */
	public IClient getSelectedClient() {
		try {
			return (IClient) getTaskModel().getObject(WizardTaskUtil.TASK_CLIENT);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Set the launchable adapter
	 * @param launch
	 */
	public void setLaunchableAdapter(ILaunchableAdapter launch){
		getTaskModel().putObject(WizardTaskUtil.TASK_LAUNCHABLE_ADAPTER, launch);
	}
	
	/**
	 * Return the launchable adapter.
	 * 
	 * @return the adapter
	 */
	public ILaunchableAdapter getLaunchableAdapter() {
		try {
			return (ILaunchableAdapter) getTaskModel().getObject(WizardTaskUtil.TASK_LAUNCHABLE_ADAPTER);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns true if this wizard should be shown to the user.
	 * 
	 * @return <code>true</code> if this wizard should be shown, and <code>false</code>
	 *    otherwise
	 */
	public boolean shouldAppear() {
		return getServer() == null || hasTasks() || hasClients();
	}

	/**
	 * Return <code>true</code> if this wizard has tasks.
	 * 
	 * @return <code>true</code> if this wizard has tasks, and <code>false</code>
	 *    otherwise
	 */
	protected boolean hasTasks() {
		try {
			Boolean b = (Boolean) getTaskModel().getObject(WizardTaskUtil.TASK_HAS_TASKS);
			return b.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return <code>true</code> if this wizard has multiple clients to show.
	 * 
	 * @return <code>true</code> if this wizard has multiple clients, and <code>false</code>
	 *    otherwise
	 */
	protected boolean hasClients() {
		try {
			Boolean b = (Boolean) getTaskModel().getObject(WizardTaskUtil.TASK_HAS_CLIENTS);
			return b.booleanValue();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * This is not intended to be used by extenders.  The prompt to remove modules dialog appears there are modules to be removed.
	 * 
	 */
	@Override
	public boolean performFinish() {
		// This code should instead be done in the RunOnServerWizardFragment.  However, we need to do this prompt up-front
		// prior to any fragments being executed in another thread.  If this prompt is moved to the fragment, then we need a 
		// a way to get access to the UI shell even though the fragments are run on a non-UI thread.  Secondly, the TaskWizard
		// needs to support cancel to undo previously run fragments and stop later fragments from running.  This is because the
		// prompt dialog allows the user to cancel.  See also ModifyModulesWizard.
		List<IModule> modulesToRemove = fragment.getModulesToRemove();
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