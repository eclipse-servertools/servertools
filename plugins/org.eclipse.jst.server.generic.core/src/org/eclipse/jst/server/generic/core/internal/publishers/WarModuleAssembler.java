/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
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
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.wst.server.core.IModule;

/**
 * Assembly utility for war modules.
 * 
 */
public class WarModuleAssembler extends AbstractModuleAssembler {
	
	protected WarModuleAssembler(IModule module, GenericServer server)
	{
		fModule=module;
		fServerdefinition=server.getServerDefinition();
		fServer=server;
	}

	protected IPath assemble(IProgressMonitor monitor) throws CoreException{
		IPath parent =copyModule(fModule,monitor);
		IWebModule webModule = (IWebModule)fModule.loadAdapter(IWebModule.class, monitor);
		IModule[] childModules = webModule.getModules();
		for (int i = 0; i < childModules.length; i++) {
			IModule module = childModules[i];
			packModule(module, webModule.getURI(module), parent);
		}
		return parent;
	}
}
