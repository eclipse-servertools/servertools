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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.HttpLaunchable;
import org.eclipse.wst.server.core.util.WebResource;
import org.eclipse.jst.server.core.EJBBean;
import org.eclipse.jst.server.core.JndiLaunchable;
import org.eclipse.jst.server.core.JndiObject;
import org.eclipse.jst.server.core.Servlet;
import org.eclipse.jst.server.generic.servertype.definition.ArgumentPair;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
/**
 * Launchable adapter delegate for generic servers.
 * @author Gorkem Ercan 
 */
public class GenericServerLaunchableAdapterDelegate extends LaunchableAdapterDelegate {
	private static final String JAVA_NAMING_PROVIDER_URL_PROPKEY = "java.naming.provider.url"; //$NON-NLS-1$
	private static final String JAVA_NAMING_FACTORY_INITIAL_PROPKEY = "java.naming.factory.initial"; //$NON-NLS-1$

	/*
	 * @see ILaunchableAdapterDelegate#getLaunchable(IServer, IModuleObject)
	 */
	public Object getLaunchable(IServer server, IModuleArtifact moduleObject) {
		ServerDelegate delegate = (ServerDelegate)server.loadAdapter(ServerDelegate.class,null);
		if (!(delegate instanceof GenericServer))
			return null;
		if ((moduleObject instanceof Servlet) ||(moduleObject instanceof WebResource))
            return prepareHttpLaunchable(moduleObject, delegate);
		
        if((moduleObject instanceof EJBBean) || (moduleObject instanceof JndiObject))
            return prepareJndiLaunchable(moduleObject,delegate);
		return null;
	}

    private Object prepareJndiLaunchable(IModuleArtifact moduleObject, ServerDelegate delegate) {
        JndiLaunchable launchable = null;
        GenericServer genericServer = (GenericServer)delegate;
        ServerRuntime definition = genericServer.getServerDefinition();
        Properties props = new Properties();
        props.put(JAVA_NAMING_FACTORY_INITIAL_PROPKEY,definition.getJndiConnection().getInitialContextFactory());
        props.put(JAVA_NAMING_PROVIDER_URL_PROPKEY,definition.getJndiConnection().getProviderUrl());
        List jps = definition.getJndiConnection().getJndiProperty();
        Iterator propsIt =jps.iterator();
        while(propsIt.hasNext()){
            ArgumentPair prop = (ArgumentPair)propsIt.next();
            props.put(prop.getName(),prop.getValue());
        }
        
        if(moduleObject instanceof EJBBean)
        {
            EJBBean bean = (EJBBean)moduleObject;
            launchable = new JndiLaunchable(props,bean.getJndiName());
        }
        if(moduleObject instanceof JndiObject)
        {
            JndiObject jndi = (JndiObject)moduleObject;
            launchable = new JndiLaunchable(props,jndi.getJndiName());
        }
        return launchable;
    }

    /**
     * @param moduleObject
     * @param delegate
     * @return object
     */
    private Object prepareHttpLaunchable(IModuleArtifact moduleObject, ServerDelegate delegate) {
        try {
			URL url = ((IURLProvider) delegate).getModuleRootURL(moduleObject.getModule());
			
			Trace.trace("root: " + url); //$NON-NLS-1$

			if (moduleObject instanceof Servlet) {
				Servlet servlet = (Servlet) moduleObject;
				if (servlet.getAlias() != null) {
					String path = servlet.getAlias();
					if (path.startsWith("/")) //$NON-NLS-1$
						path = path.substring(1);
					url = new URL(url, path);
				} else
					url = new URL(url, "servlet/" + servlet.getServletClassName()); //$NON-NLS-1$
			} else if (moduleObject instanceof WebResource) {
				WebResource resource = (WebResource) moduleObject;
				String path = resource.getPath().toString();
				Trace.trace("path: " + path); //$NON-NLS-1$
				if (path != null && path.startsWith("/") && path.length() > 0) //$NON-NLS-1$
					path = path.substring(1);
				if (path != null && path.length() > 0)
					url = new URL(url, path);
			} 
			return new HttpLaunchable(url);
		} catch (Exception e) {
			Trace.trace("Error getting URL for " + moduleObject, e); //$NON-NLS-1$
			return null;
		}
    }
}
