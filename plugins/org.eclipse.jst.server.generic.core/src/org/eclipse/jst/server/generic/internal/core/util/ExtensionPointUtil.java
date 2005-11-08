/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.internal.core.util;

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
    public static final String SERVERDEFINITION_EXTENSION_ID = "serverdefinition";
    private static final String GENERICPUBLISHER_EXTENSION_ID = "genericpublisher";

    public static IExtension[] getGenericServerDefinitionExtensions(){
        return getExtensions(CorePlugin.PLUGIN_ID+"."+SERVERDEFINITION_EXTENSION_ID);
    }
    
    public static IExtension[] getGenericPublisherExtension(){
        return getExtensions(CorePlugin.PLUGIN_ID+"."+GENERICPUBLISHER_EXTENSION_ID);
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
    
    
    public static IConfigurationElement[] getConfigurationElements(IExtension extension){
        return extension!=null?extension.getConfigurationElements():null;
    }
    
    public static void addRegistryListener(IRegistryChangeListener listener){
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addRegistryChangeListener(listener, CorePlugin.PLUGIN_ID);
    }
}
