package org.eclipse.jst.server.generic.core.internal.publishers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.wst.server.core.IModule;

public class DefaultModuleAssembler extends AbstractModuleAssembler {
	
	protected DefaultModuleAssembler(IModule module, ServerRuntime serverdefinition)
	{
		fModule=module;
		fServerdefinition=serverdefinition;
	}
	
	
	protected void assemble(IProgressMonitor monitor) throws CoreException {
		copyModule(fModule,monitor);		
	}



}
