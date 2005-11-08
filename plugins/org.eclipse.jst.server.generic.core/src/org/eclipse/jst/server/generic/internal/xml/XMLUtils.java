/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/

package org.eclipse.jst.server.generic.internal.xml;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.internal.core.util.ExtensionPointUtil;
import org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage;
import org.eclipse.jst.server.generic.internal.servertype.definition.util.ServerTypeResourceFactoryImpl;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.osgi.framework.Bundle;

public class XMLUtils {

	
    private ArrayList definitions;


	public XMLUtils() {
		refresh();
	}

	private void refresh() {
		definitions= new ArrayList();
         IExtension[] extensions = ExtensionPointUtil.getGenericServerDefinitionExtensions();
        for (int i = 0; extensions!=null && i < extensions.length; i++) {
            java.net.URI definitionFile=null;
            IExtension extension = extensions[i];
            IConfigurationElement[] elements = ExtensionPointUtil.getConfigurationElements(extension);
            for (int j = 0; j < elements.length; j++) {
                IConfigurationElement element = elements[j];
                definitionFile = getDefinitionFile(element);
                ServerRuntime runtime =readFile(definitionFile);
                    if(runtime!=null){
                        runtime.setId(element.getAttribute("id"));
                        runtime.setConfigurationElementNamespace(element.getNamespace());
                        definitions.add(runtime);
                    }
                }
            }

       }


    /**
     * @param extension
     */
    private java.net.URI getDefinitionFile(IConfigurationElement element) {
        Bundle bundle = Platform.getBundle(element.getNamespace());
        String definitionFile = element.getAttribute("definitionfile");
		URL url = bundle.getEntry(definitionFile);
		try {
			java.net.URI uri = new java.net.URI(url.getProtocol(), url.getHost(),url.getPath(), url.getQuery());
		    return uri;
		} catch (URISyntaxException e) {
			//ignore
		}
        return null;
    }

    private ServerRuntime readFile(java.net.URI file) {
        // Create a resource set.
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register the default resource factory -- only needed for
        // stand-alone!
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                        new ServerTypeResourceFactoryImpl());

         ServerTypePackage gstPack = ServerTypePackage.eINSTANCE;

        // Get the URI of the model file.
        URI fileURI = URI.createURI(file.toString());

        // Demand load the resource for this file.
        Resource resource = null;
        try {
            resource = resourceSet.getResource(fileURI, true);
        } catch (WrappedException e) {
            // sth wrong with this .server file.
            CorePlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 1,
                            "Error loading the server type definition", e));
        }

        if (resource != null) {
            ServerRuntime def = (ServerRuntime) resource.getContents().get(0);
            if (def != null) {
                def.setFilename(file.toString());
                return def;
            }
        }
        return null;

    }


	/**
	 * @return ArrayList
	 */
	public ArrayList getServerTypeDefinitions() {
		return definitions;
	}

    public ServerRuntime getServerTypeDefinition(String id) {
    	Iterator defs = getServerTypeDefinitions().iterator();
        while (defs.hasNext()) {
            ServerRuntime elem = (ServerRuntime) defs.next();
            if (id.equals(elem.getId()))
                return elem;
        }
        return null;
    }
}
