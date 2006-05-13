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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.wst.server.core.IModule;

/**
 * Utility for EAR module assembly.
 */
public class EarModuleAssembler extends AbstractModuleAssembler {

	protected EarModuleAssembler(IModule module, GenericServer server)
	{
		fModule=module;
		fServerdefinition=server.getServerDefinition();
		fServer=server;
	}

	protected IPath assemble(IProgressMonitor monitor) throws CoreException{
		IPath parent =copyModule(fModule,monitor);
		IEnterpriseApplication earModule = (IEnterpriseApplication)fModule.loadAdapter(IEnterpriseApplication.class, monitor);
		IModule[] childModules = earModule.getModules();
		for (int i = 0; i < childModules.length; i++) {
			IModule module = childModules[i];
			String uri = earModule.getURI(module);
			if(uri==null){
				IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0,	"unable to assemble module null uri",null ); //$NON-NLS-1$
				throw new CoreException(status);
			}
				
			packModule(module,uri, parent);
		}
		return parent;
	}
	
	protected void packModule(IModule module, String deploymentUnitName, IPath destination) throws CoreException {
		if(module.getModuleType().getId().equals("jst.web")) //$NON-NLS-1$
		{
			AbstractModuleAssembler assembler= AbstractModuleAssembler.Factory.getModuleAssembler(module, fServer);
			IPath webAppPath = assembler.assemble(new NullProgressMonitor());
			String realDestination = destination.append(deploymentUnitName).toString();
			ModulePackager packager=null;
			try {
				packager =new ModulePackager(realDestination,false);
				packager.pack(webAppPath.toFile(),webAppPath.toOSString());
			
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0,
						"unable to assemble module", e); //$NON-NLS-1$
				throw new CoreException(status);
			}
			finally{
				if(packager!=null)
				{
					
					try {
						packager.finished();
					} catch (IOException e) {
						// Unhandled
					}
				}
				
			}
			
			
		}
		else
		{
			super.packModule(module, deploymentUnitName, destination);
		}
	}
}
