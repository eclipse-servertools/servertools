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
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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
package org.eclipse.jst.server.generic.internal.core;

import java.net.URL;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.HttpLaunchable;
import org.eclipse.wst.server.core.util.NullLaunchable;
import org.eclipse.wst.server.core.util.NullModuleObject;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.jst.server.j2ee.Servlet;
import org.eclipse.jst.server.j2ee.WebResource;
/**
 * Web Launchable adapter delegate
 * @author Gorkem Ercan 
 */
public class GenericServerLaunchableAdapterDelegate implements ILaunchableAdapterDelegate {
	/*
	 * @see ILaunchableAdapterDelegate#getLaunchable(IServer, IModuleObject)
	 */
	public ILaunchable getLaunchable(IServer server, IModuleObject moduleObject) {
		
		IServerDelegate delegate = server.getDelegate();
		if (!(delegate instanceof GenericServer))
			return null;
		if (!(moduleObject instanceof Servlet) &&
			!(moduleObject instanceof WebResource) &&
			!(moduleObject instanceof NullModuleObject))
			return null;
		if (!(moduleObject.getModule() instanceof IWebModule))
			return null;

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
			} else { // null
				return new NullLaunchable();
			}
			return new HttpLaunchable(url);
		} catch (Exception e) {
			Trace.trace("Error getting URL for " + moduleObject, e);
			return null;
		}
	}
}
