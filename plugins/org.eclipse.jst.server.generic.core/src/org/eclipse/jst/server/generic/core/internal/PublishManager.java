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
            //ignored
        }
        return null;
    }

     

}
