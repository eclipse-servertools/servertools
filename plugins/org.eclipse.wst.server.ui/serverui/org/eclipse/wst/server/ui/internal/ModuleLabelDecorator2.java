/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.viewers.BaseLabelProvider;

public class ModuleLabelDecorator2 extends BaseLabelProvider implements ILightweightLabelDecorator {
	public ModuleLabelDecorator2() {
		super(false);
	}

	public void decorate(Object element, IDecoration decoration) {
		try {
			IModule module = null;
			System.out.println("decorate2");
			
			if (element instanceof IServer) {
				decoration.addSuffix(" *");
				return;
			}
			
			if (element instanceof IModule) {
				module = (IModule) element;
			} else if (element instanceof ModuleServer) {
				IModule[] modules = ((ModuleServer) element).module;
				module = modules[modules.length - 1];
			}
			if (module == null)
				return;
			
			IProject project = module.getProject();
			if (project == null)
				return;
			
			String text = module.getName();
			
			if (!project.getName().equals(text))
				decoration.addSuffix(" (" + project.getName() + ")");
				//text = NLS.bind(Messages.moduleDecoratorProject, new String[] {text, project.getName()});
			//return PlatformUI.getWorkbench().getDecoratorManager().decorateText(text, project);
		} catch (Exception e) {
			return;
		}
	}
}