/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;

/**
 * The abstract publisher. This is intended to be subclassed by
 * clients implementing the genericpublisher extension point.
 *
 * @author Gorkem Ercan
 */
public abstract class GenericPublisher 
{
    
    private IModule[] fModule;
    private GenericServerRuntime fServerRuntime;
    private GenericServer fServer;
    private int fKind;
    private int fDeltaKind;
    
    
    /**
     * Intizilazes publisher.
     * 
     * @param module
     * @param server
     */
    protected void initialize( IModule[] module, IServer server, int kind, int deltaKind )
    {
        fModule = module;
        fServer = (GenericServer)server.loadAdapter(GenericServer.class,null);
        fServerRuntime = (GenericServerRuntime)server.getRuntime().loadAdapter(GenericServerRuntime.class,null);
        fKind = kind;
        fDeltaKind = deltaKind;
    }
    
    /**
     * Initializes publisher.
     * @deprecated
     * @param module
     * @param server
     */
    protected void initialize(IModule[] module, IServer server)
    {
        fModule = module;
        fServer = (GenericServer)server.loadAdapter(GenericServer.class,null);
        fServerRuntime = (GenericServerRuntime)server.getRuntime().loadAdapter(GenericServerRuntime.class,null);
    }
   /**
    * Called by the generic server implementation when a module is 
    * removed form the server instance. 
    * Subclasses may extend this method to perform their own module removal
    * 
    * @param monitor
    * @return status
    */ 
   public abstract IStatus[] unpublish(IProgressMonitor monitor);
    
    /**
     * Called by the generic server implementation when a publish module 
     * event occurs. 
     * Subclasses may extend this method to perform their own publishing
     * 
     * @param resource
     * @param monitor
     * @return status
     */
    public abstract IStatus[] publish(IModuleArtifact[] resource,
            IProgressMonitor monitor);
   
    /**
     * Returns the module associated with this publisher instance
     * @return module
     */
    public IModule[] getModule() {
        return fModule;
    }

    /**
     * Generic server instance
     * @return server
     */
    public GenericServer getServer(){
    	return fServer;
    }
    /**
     * a handle to server definition.
     * @return serverdef
     */
    public GenericServerRuntime getServerRuntime() {
        return fServerRuntime;
    }

    public int getDeltaKind() {
        return fDeltaKind;
    }

    public int getKind() {
        return fKind;
    }
}