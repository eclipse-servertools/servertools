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
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.viewers.BaseLabelProvider;

public class ModuleLabelDecorator extends BaseLabelProvider implements ILabelDecorator {
	public ModuleLabelDecorator() {
		super(false);
	}

	public Image decorateImage(Image image, Object element) {
		try {
			IModule module = null;
			
			if (element instanceof IModule) {
				module = (IModule) element;
			} else if (element instanceof ModuleServer) {
				IModule[] modules = ((ModuleServer) element).module;
				module = modules[modules.length - 1];
			}
			if (module == null)
				return null;
			
			IProject project = module.getProject();
			
			if (project == null)
				return null;
			
			return PlatformUI.getWorkbench().getDecoratorManager().decorateImage(image, project);
		} catch (Exception e) {
			return null;
		}
	}

	public String decorateText(String text, Object element) {
		try {
			IModule module = null;
			
			if (element instanceof IModule) {
				module = (IModule) element;
			} else if (element instanceof ModuleServer) {
				IModule[] modules = ((ModuleServer) element).module;
				module = modules[modules.length - 1];
			}
			if (module == null)
				return null;
			
			IProject project = module.getProject();
			
			if (project == null)
				return null;
			
			if (!project.getName().equals(text))
				text = NLS.bind(Messages.moduleDecoratorProject, new String[] {text, project.getName()});
			return PlatformUI.getWorkbench().getDecoratorManager().decorateText(text, project);
		} catch (Exception e) {
			return null;
		}
	}
}