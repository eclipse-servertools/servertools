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

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.jst.server.generic.internal.core.util.ExtensionPointUtil;
import org.eclipse.jst.server.generic.internal.core.util.ServerRuntimeMergeUtil;
import org.eclipse.jst.server.generic.internal.xml.XMLUtils;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
/**
 * Manages the retrieaval of ServerTypeDefinitions.
 * 
 * @author Gorkem Ercan
 */

public class ServerTypeDefinitionManager 
{
	private XMLUtils fXmlUtils;
	
	/**
	 * Watch for changes to serverdefinition and runtimedefinition extensions.
	 */
	private class RegistryChangeListener implements IRegistryChangeListener {
        public void registryChanged(IRegistryChangeEvent event) {
            IExtensionDelta[] deltas = event.getExtensionDeltas(CorePlugin.PLUGIN_ID, ExtensionPointUtil.SERVERDEFINITION_EXTENSION_ID);
            if (deltas != null && deltas.length > 0) {
                handleDefinitionsChanged();
            }
            else {
                deltas = event.getExtensionDeltas(CorePlugin.PLUGIN_ID, ExtensionPointUtil.RUNTIMEDEFINITION_EXTENSION_ID);
                if (deltas != null && deltas.length > 0) {
                    handleDefinitionsChanged();
                }
            }
        }
    }
	
	protected ServerTypeDefinitionManager(URL serverDefinitionURL){
		super();
		fXmlUtils = new XMLUtils();
		ExtensionPointUtil.addRegistryListener(new RegistryChangeListener());
	}

    /**
     * Returns either: 
     * 
     * 1. the ServerRuntime that represents the .serverdef file for a given 
     *    runtime type, based on the runtimeTypeId.
     *    
     * 2. the ServerRuntime that represents both the .serverdef file for a
     *    given server type, based on the serverTypeId, combined with the 
     *    .runtimedef file for the runtime type, based on the runtimeTypeId.   
     * 
     * The implementation looks for (1) first, if that combination is not
     * found, then (2) is returned.
     * 
     * @param serverTypeId server type id
     * @param runtimeTypeId runtime type id
     * @param properties user provided properties
     * @return server runtime that is initialized with user properties 
     */
    public ServerRuntime getServerRuntimeDefinition(String serverTypeId, String runtimeTypeId, Map properties) {
        
        ServerRuntime serverdef = fXmlUtils.getServerTypeDefinition(serverTypeId);
        
        if (serverdef != null) {
            ServerRuntime runtimedef = fXmlUtils.getRuntimeTypeDefinition(runtimeTypeId);
            if (runtimedef == null) {
            	// Fall back to the original usage
                serverdef = fXmlUtils.getServerTypeDefinition(runtimeTypeId);
            }
            else {
                serverdef = ServerRuntimeMergeUtil.combine(serverdef, runtimedef);
            }
        }
        else {
            // Fall back to the original usage
            serverdef = fXmlUtils.getServerTypeDefinition(runtimeTypeId);
        }
        
        if (serverdef != null) {
            serverdef.setPropertyValues(properties);
        }
        
        return serverdef;
    }
    
    /**
     * Returns the ServerRuntime that represents the .serverdef file
     * for a given runtime type.
     * 
     * @param runtimeTypeId runtime type id
     * @param properties user provided properties
     * @return server runtime that is initialized with user properties 
     */
    public ServerRuntime getServerRuntimeDefinition(String runtimeTypeId, Map properties) {
        ServerRuntime definition = fXmlUtils.getRuntimeTypeDefinition(runtimeTypeId);
        
        if (definition == null) {
            // Fall back to the original usage
            definition = fXmlUtils.getServerTypeDefinition(runtimeTypeId);
        }
        
        if (definition != null) {
            definition.setPropertyValues(properties);
        }
        
        return definition;
    }
	
	/**
	 * Returns all the ServerRuntimes registered a .serverdef.
	 * @return serverRuntimes
	 */
	public ServerRuntime[] getServerTypeDefinitions(){
		 List definitionList = fXmlUtils.getServerTypeDefinitions();
		 return (ServerRuntime[])definitionList.toArray(new ServerRuntime[definitionList.size()]);
	}
	
	private void handleDefinitionsChanged(){	
		XMLUtils utils = new XMLUtils();
		fXmlUtils = utils;
	}
	
}
