package org.eclipse.jst.server.generic.internal.core.util;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Some utilities for handling the extension points.
 *
 * @author Gorkem Ercan
 */
public class ExtensionPointUtil {
    private static final String SERVERDEFINITION_EXTENSION_ID = "org.eclipse.jst.server.generic.core.serverdefinition";
    private static final String GENERICPUBLISHER_EXTENSION_ID = "org.eclipse.jst.server.generic.core.genericpublisher";

    public static IExtension[] getGenericServerDefinitionExtensions(){
        return getExtensions(SERVERDEFINITION_EXTENSION_ID);
    }
    
    public static IExtension[] getGenericPublisherExtension(){
        return getExtensions(GENERICPUBLISHER_EXTENSION_ID);
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
    
    
    public static IConfigurationElement[] getConfigurationElements(IExtension extension)
    {
        return extension!=null?extension.getConfigurationElements():null;
    }
    
}
