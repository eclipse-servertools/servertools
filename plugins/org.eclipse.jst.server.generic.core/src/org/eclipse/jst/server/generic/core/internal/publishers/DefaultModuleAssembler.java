package org.eclipse.jst.server.generic.core.internal.publishers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.wst.server.core.IModule;

/**
 * Default module assembler that basically copies the contents.
 * @author Gorkem Ercan
 */
public class DefaultModuleAssembler extends AbstractModuleAssembler {
	
	protected DefaultModuleAssembler(IModule module, GenericServer server, IPath assembleRoot)
	{
		super(module, server, assembleRoot);
	}
	
	public IPath assemble(IProgressMonitor monitor) throws CoreException {
		return copyModule(fModule,monitor);		
	}



}
