package org.eclipse.jst.server.generic.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jst.server.generic.internal.core.util.ExtensionPointUtil;

/**
 * 
 *
 * @author Gorkem Ercan
 */
public class PublishManager 
{
     public static GenericPublisher getPublisher(String id) 
    {
        IExtension[] extensions = ExtensionPointUtil.getGenericPublisherExtension();
        try {
            for (int i = 0; i < extensions.length; i++) {
                IConfigurationElement[] configs = ExtensionPointUtil.getConfigurationElements(extensions[i]);
                for (int j = 0; j < configs.length; j++) {
                    if(configs[j].getAttribute("id").equals(id)) {
                        return (GenericPublisher)configs[j].createExecutableExtension("class");
                      }
                }
            }
        }catch(CoreException e){
            //ingored
        }
        return null;
    }

     

}
