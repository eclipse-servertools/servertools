package org.eclipse.jst.server.generic.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;

/**
 * The abstract publisher interface.
 *
 * @author Gorkem Ercan
 */
public abstract class GenericPublisher 
{
    IModule[] fParents;
    IModule fModule;
    ServerRuntime fServerRuntime;
    
    public void initialize(IModule[] parents, IModule module, ServerRuntime serverDefinition)
    {
        fModule = module;
        fParents = parents;
        fServerRuntime = serverDefinition;
    }
    
    /**
     * 
     * @param resource
     * @param monitor
     * @return
     * @throws CoreException
     */
    public abstract IStatus[] publish(IModuleArtifact[] resource,
            IProgressMonitor monitor);
}