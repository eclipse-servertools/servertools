/***************************************************************************************************
 * Copyright (c) 2005, 2010 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal.publishers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

/**
 * Assembly utility for war modules.
 * 
 */
public class WarModuleAssembler extends AbstractModuleAssembler {
	
	protected WarModuleAssembler(IModule module, GenericServer server, IPath assembleRoot)
	{
		super(module, server, assembleRoot);
	}

	public IPath assemble(IProgressMonitor monitor) throws CoreException{
		IPath parent =copyModule(fModule,monitor);
		IWebModule webModule = (IWebModule)fModule.loadAdapter(IWebModule.class, monitor);
		IModule[] childModules = webModule.getModules();
		for (int i = 0; i < childModules.length; i++) {
			IModule module = childModules[i];
			String uri = webModule.getURI(module);
			if (uri == null) { // The bad memories of WTP 1.0
				IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "unable to assemble module null uri", null); //$NON-NLS-1$
				throw new CoreException(status);
			}
			IJ2EEModule jeeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, monitor);
			if (jeeModule != null && jeeModule.isBinary()) { // Binary module
				ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
				IModuleResource[] resources = pm.members();
				publishHelper.publishToPath(resources, parent.append(uri), monitor);
			}
			else { // Project module
				packModule(module, uri, parent);
			}
		}
		return parent;
	}
}
