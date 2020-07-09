/*******************************************************************************
 * Copyright (c) 2008, 2020 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.ui.internal;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.model.PublisherDelegate;

public class ContextPublisherDelegate extends PublisherDelegate {
	private static final QualifiedName QUALIFIED_NAME = new QualifiedName(TomcatUIPlugin.PLUGIN_ID, "contextRoot");

	public IStatus execute(int kind, IProgressMonitor monitor, IAdaptable info) throws CoreException {
		// this publisher only runs when there is a UI
		if (info == null)
			return Status.OK_STATUS;
		
		final Shell shell = info.getAdapter(Shell.class);
		if (shell == null)
			return Status.OK_STATUS;
		
		IServer server = (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
		TomcatServer tomcatServer = (TomcatServer) server.loadAdapter(TomcatServer.class, monitor);
		final TomcatConfiguration configuration = tomcatServer.getTomcatConfiguration();
		
		final boolean[] save = new boolean[1];
		List modules = (List) getTaskModel().getObject(TaskModel.TASK_MODULES);
		int size = modules.size();
		for (int i = 0; i < size; i++) {
			IModule[] module = (IModule[]) modules.get(i);
			final IModule m = module[module.length - 1];
			IWebModule webModule = (IWebModule) m.loadAdapter(IWebModule.class, monitor);
			final WebModule webModule2 = configuration.getWebModule(m);
			if (webModule != null && webModule2 != null) {
				String contextRoot = webModule.getContextRoot();
				if (contextRoot != null && !contextRoot.startsWith("/") && contextRoot.length() > 0)
					contextRoot = "/" + contextRoot;
				
				if (!contextRoot.equals(webModule2.getPath()) && shouldPrompt(m, contextRoot)) {
					final String context = contextRoot;
					shell.getDisplay().syncExec(new Runnable() {
						public void run() {
							if (MessageDialog.openQuestion(shell, Messages.wizardTitle, NLS.bind(Messages.contextCleanup, m.getName()))) {
								int index = configuration.getWebModules().indexOf(webModule2);
								configuration.modifyWebModule(index, webModule2.getDocumentBase(), context, webModule2.isReloadable());
								save[0] = true;
							}
						}
					});
					markProject(m, contextRoot);
				}
			}
		}
		if (save[0])
			tomcatServer.saveConfiguration(monitor);
		
		return Status.OK_STATUS;
	}

	protected boolean shouldPrompt(IModule m, String contextRoot) {
		IProject project = m.getProject();
		if (project == null)
			return true;
		
		try {
			String s = project.getPersistentProperty(QUALIFIED_NAME);
			if (s == null)
				return true;
			return !contextRoot.equals(s);
		} catch (CoreException ce) {
			return true;
		}
	}

	protected void markProject(IModule m, String contextRoot) {
		IProject project = m.getProject();
		if (project == null)
			return;
		
		try {
			project.setPersistentProperty(QUALIFIED_NAME, contextRoot);
		} catch (CoreException ce) {
			// ignore, it's ok to prompt again later
		}
	}
}