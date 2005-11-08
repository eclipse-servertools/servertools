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
	
	private class RegistryChangeListener implements IRegistryChangeListener{
		public void registryChanged(IRegistryChangeEvent event) {
			IExtensionDelta[] deltas = event.getExtensionDeltas(CorePlugin.PLUGIN_ID, ExtensionPointUtil.SERVERDEFINITION_EXTENSION_ID);
			if(deltas!=null && deltas.length>0){
				handleServerDefinitionsChanged();
			}
		}		
	}
	
	protected ServerTypeDefinitionManager(URL serverDefinitionURL){
		super();
		fXmlUtils = new XMLUtils();
		ExtensionPointUtil.addRegistryListener(new RegistryChangeListener());
	}

	/**
	 * Returns the ServerRuntime that represents the .serverdef file
	 * for a given runtime type.
	 * @param id runtime type id
	 * @param properties user provided properties
	 * @return server runtime that is initialized with user properties 
	 */
	public ServerRuntime getServerRuntimeDefinition(String id, Map properties){
		ServerRuntime definition =  fXmlUtils.getServerTypeDefinition(id);
		if(definition !=null)
            definition.setPropertyValues(properties);
		return definition;
	}
	
	/**
	 * Returns all the ServerRuntimes registered a .serverdef.
	 * @return
	 */
	public ServerRuntime[] getServerTypeDefinitions(){
		 List definitionList = fXmlUtils.getServerTypeDefinitions();
		 return (ServerRuntime[])definitionList.toArray(new ServerRuntime[definitionList.size()]);
	}
	
	private void handleServerDefinitionsChanged(){	
		XMLUtils utils = new XMLUtils();
		fXmlUtils = utils;
	}
	
}
