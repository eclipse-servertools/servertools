package org.eclipse.wst.server.ui.internal.wizard;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.util.List;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.task.FinishWizardFragment;
import org.eclipse.wst.server.ui.internal.task.SaveServerTask;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * A wizard used to select server and module tasks.
 */
public class SelectTasksWizard extends TaskWizard {
	protected TasksWizardFragment fragment;
	
	/**
	 * SelectTasksWizard constructor comment.
	 */
	public SelectTasksWizard(final IServer server, final IServerConfiguration configuration,
			final List[] parents, final IModule[] modules) {
		super(ServerUIPlugin.getResource("%wizTaskWizardTitle"));
		
		setRootFragment(new WizardFragment() {
			public void createSubFragments(List list) {
				fragment = new TasksWizardFragment(server, configuration, parents, modules);
				list.add(fragment);
				list.add(new FinishWizardFragment(new SaveServerTask()));
			}
		});
		addPages();
	}

	/**
	 * 
	 */
	public boolean hasTasks() {
		return fragment.hasTasks();
	}

	public boolean hasOptionalTasks() {
		return fragment.hasOptionalTasks();
	}
}
