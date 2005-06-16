/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rfrost@bea.com - initial API and implementation
 *    
 * Based on GenericServerRuntime by Gorkem Ercan
 *******************************************************************************/

package org.eclipse.jst.server.generic.core.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;

/**
 * Subclass of <code>GenericServerRuntime</code> that provides runtime server support for 
 * servers that are started and stopped via external executables.
 */
public class ExternalServerRuntime extends GenericServerRuntime {
	
	/**
	 * Override definition of validate to relax classpath and VM constraints.
	 */
	public IStatus validate() {
		ServerRuntime serverTypeDefinition = getServerTypeDefinition();
		
		// we need to have a type definition
        if(serverTypeDefinition == null) {
		    return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorNoServerType, null);
        }
        
        // if there is a classpath definition, then want to make certain that there is an associated VM install
        if(serverTypeDefinition.getClasspath()!= null && serverTypeDefinition.getClasspath().size() > 0) {
    		if (getVMInstall() == null) {
    			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorJRE, null);
    		}
        }
        // check all defined classpaths
        return validateClasspaths(serverTypeDefinition);
	}
	
}
