/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Gorkem Ercan - initial API and implementation
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
        props.put("java.naming.factory.initial",definition.getJndiConnection().getInitialContextFactory());
        props.put("java.naming.provider.url",definition.getJndiConnection().getProviderUrl());
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
     * @return
     */
    private Object prepareHttpLaunchable(IModuleArtifact moduleObject, ServerDelegate delegate) {
        try {
			URL url = ((IURLProvider) delegate).getModuleRootURL(moduleObject.getModule());
			
			Trace.trace("root: " + url);

			if (moduleObject instanceof Servlet) {
				Servlet servlet = (Servlet) moduleObject;
				if (servlet.getAlias() != null) {
					String path = servlet.getAlias();
					if (path.startsWith("/"))
						path = path.substring(1);
					url = new URL(url, path);
				} else
					url = new URL(url, "servlet/" + servlet.getServletClassName());
			} else if (moduleObject instanceof WebResource) {
				WebResource resource = (WebResource) moduleObject;
				String path = resource.getPath().toString();
				Trace.trace("path: " + path);
				if (path != null && path.startsWith("/") && path.length() > 0)
					path = path.substring(1);
				if (path != null && path.length() > 0)
					url = new URL(url, path);
			} 
			return new HttpLaunchable(url);
		} catch (Exception e) {
			Trace.trace("Error getting URL for " + moduleObject, e);
			return null;
		}
    }
}
