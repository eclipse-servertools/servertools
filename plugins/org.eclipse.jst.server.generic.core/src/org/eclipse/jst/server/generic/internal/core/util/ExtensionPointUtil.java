/***************************************************************************************************
 * Copyright (c) 2005, 2010 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.internal.core.util;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
/**
 * Utilities for handling the extension points.
 *
 * @author Gorkem Ercan
 */
public class ExtensionPointUtil {
    /**
     * serverdefinition extension id
     */
    public static final String SERVERDEFINITION_EXTENSION_ID = "serverdefinition"; //$NON-NLS-1$
    public static final String RUNTIMEDEFINITION_EXTENSION_ID = "runtimedefinition"; //$NON-NLS-1$
    private static final String GENERICPUBLISHER_EXTENSION_ID = "genericpublisher"; //$NON-NLS-1$
    private static final String EXTENSION_SERVER_TYPE = "serverTypes";//$NON-NLS-1$

    /**
     * Returns serverdefinition extensions
     * @return serverdefinition Extensions
     */
    public static IExtension[] getGenericServerDefinitionExtensions(){
        return getExtensions(CorePlugin.PLUGIN_ID+"."+SERVERDEFINITION_EXTENSION_ID); //$NON-NLS-1$
    }

    /**
     * Returns runtimedefinition extensions
     * @return runtimedefinition Extensions
     */
    public static IExtension[] getGenericServerRuntimeDefinitionExtensions(){
        return getExtensions(CorePlugin.PLUGIN_ID+"."+RUNTIMEDEFINITION_EXTENSION_ID); //$NON-NLS-1$
    }
    
    /**
     * Returns publisher extensions
     * @return genericpublisher extensions
     */
    public static IExtension[] getGenericPublisherExtension(){
        return getExtensions(CorePlugin.PLUGIN_ID+"."+GENERICPUBLISHER_EXTENSION_ID); //$NON-NLS-1$
    }

    private static IExtension[] getExtensions(String extensionId){
        IExtensionPoint extensionPoint=getExtensionPoint(extensionId);
        IExtension[] extensions = extensionPoint.getExtensions();
        return extensions;
    }
 
    private static IExtensionPoint getExtensionPoint(String id)
    {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint= registry.getExtensionPoint(id);
        return extensionPoint;
    }
    
    
    /**
     * Returns the configuration elements
     *
     * @param extension
     * @return configurationElements
     */
    public static IConfigurationElement[] getConfigurationElements(IExtension extension){
        return extension!=null?extension.getConfigurationElements():null;
    }
    
    /**
     * Add listener to extension registry.
     * @param listener
     */
    public static void addRegistryListener(IRegistryChangeListener listener){
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addRegistryChangeListener(listener, CorePlugin.PLUGIN_ID);
    }
    /**
     * Retrieves the list of servers with the given launch configuration ID.
     * Returns an empty array if there is none.
     * @param launchConfigId
     * @return
     */
    public static String[] getServerTypesFromLaunchConfig(String launchConfigId){
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] cf = registry.getConfigurationElementsFor("org.eclipse.wst.server.core", EXTENSION_SERVER_TYPE); //$NON-NLS-1$
        ArrayList<String> list = new ArrayList<String>();
        for( int i = 0; i < cf.length; i++ )
        {      
            String id =  cf[i].getAttribute( "launchConfigId" ); //$NON-NLS-1$
            if (id != null && id.equals( launchConfigId ))
            {
                list.add( cf[i].getAttribute( "id" ) ); //$NON-NLS-1$
            }
               
        }
        return list.toArray(new String[list.size()]);
       
    }
}
