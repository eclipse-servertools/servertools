/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Naci M. Dai - initial API and implementation
 *     
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************/

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

	public void refresh() {
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

    public ServerRuntime readFile(java.net.URI file) {
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
        refresh();
        Iterator defs = getServerTypeDefinitions().iterator();
        while (defs.hasNext()) {
            ServerRuntime elem = (ServerRuntime) defs.next();
            if (id.equals(elem.getId()))
                return elem;
        }
        return null;
    }
   
	/**
	 * Sets the definitions.
	 * 
	 * @param definitions
	 *            The definitions to set
	 */
	public void setDefinitions(ArrayList definitions) {
		this.definitions = definitions;
	}

	
	
}
