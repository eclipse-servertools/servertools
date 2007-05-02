/*******************************************************************************
 * Copyright (c) 2007 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal.xml.server32;

import java.net.URLEncoder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This class represents an instance of a Tomcat 3.2, or later, server as
 * defined a server.xml configuration file.
 */
public class ServerInstance {

	protected Server server;
	protected ContextManager contextManager;
	
	protected IStatus status = Status.OK_STATUS;
	
	/**
	 * Constructs a ServerInstance using the specified Server configuration.
	 * The ServerInstance provides access to Connectors and Contexts. It does
	 * not support interacting with Host elements under the ContextManager
	 * element.
	 * 
	 * @param server Server configuration on which to base this instance.
	 */
	public ServerInstance(Server server) {
		if (server == null)
			throw new IllegalArgumentException("Server argument may not be null.");
		this.server = server;
	}

	/**
	 * This method is used to get the problem status following
	 * a method call that returned <b>null</b> due to an error.
	 * @return Status of last method call.
	 */
	public IStatus getStatus() {
		return status;
	}

	/**
	 * Gets the ContextManager element if one exists or will
	 * create a new one if it does not currently exist.
	 * @return Returns an existing or new ContextManger.
	 */
	public ContextManager getContextManager() {
		status = Status.OK_STATUS;
		if (contextManager == null)
			contextManager = server.getContextManager();
		return contextManager;
	}
	
	/**
	 * Gets the connector at the specified index. If a Connector
	 * does not exist at that index a new Connector is appended
	 * and returned.
	 * 
	 * @param index Index of the Connector to return.
	 * @return Returns the Connecter at the specified index or
	 * a new Connector if one at that index doesn't exist.
	 */
	public Connector getConnector(int index) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur
		
		return contextManager.getConnector(index);
	}

	/**
	 * Gets the Connectors found in the ContextManager in the
	 * server configuration.
	 *    
	 * @return Array of Connectors found in the ContextManger in the
	 * server configuration.
	 */
	public Connector [] getConnectors() {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur
		
		int size = contextManager.getConnectorCount();
		Connector [] connectors = new Connector [size];
		for (int i = 0; i < size; i++) {
			connectors[i] = contextManager.getConnector(i);
		}
		return connectors;
	}
	
	/**
	 * Gets the Context that has the specified path within the selected
	 * Host. This method will return <b>null</b> if the Context with the
	 * required path is not found in the server configuration.
	 * 
	 * @param contextPath Path of the Context to be returned.  A leading '/' is optional.
	 * @return Returns the Context whose <b>path</b> attribute matches
	 * the specified contextPath. Returns <b>null</b> if the Context with
	 * the required path is not found.
	 */
	public Context getContext(String contextPath) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur
		
		if (contextPath != null && contextPath.length() > 0 && !contextPath.startsWith("/"))
			contextPath = "/" + contextPath;
		
		int size = contextManager.getContextCount();
		for (int i = 0; i < size; i++) {
			Context ctx = contextManager.getContext(i);
			if (ctx.getPath().equals(contextPath)) {
				return ctx;
			}
		}
		status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
				NLS.bind(Messages.errorXMLContextNotFoundPath32, contextPath));
		return null;
	}
	
	/**
	 * Gets the Context at the specified index. If a Connector does not
	 * exist at the specified index, a new Context will be appended and
	 * returned.
	 * 
	 * @param index Index of the Context to return.
	 * @return Returns the Context at the specified index, or
	 * a new appended Context if the index is beyond any existing
	 * Contexts.
	 */
	public Context getContext(int index) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur
		
		return contextManager.getContext(index);
	}
	
	/**
	 * Gets the Contexts contained in the ContextManager.
	 * 
	 * @return Array of Contexts contained in the ContextManager.
	 */
	public Context [] getContexts() {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur
		
		int size = contextManager.getContextCount();
		Context [] contexts = new Context [size];
		for (int i = 0; i < size; i++) {
			contexts[i] = contextManager.getContext(i);
		}
		return contexts;
	}
	
	/**
	 * Creates a new Context and inserts it before the specifed index
	 * or appends it if the index is beyond any existing Contexts in the
	 * ContextManager.
	 * 
	 * @param index Index prior to which to insert the new Contexts.
	 * @return Returns the created Context.
	 */
	public Context createContext(int index) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur

		return (Context)contextManager.createElement(index, "Context");
	}
	
	/**
	 * Removes the Context with the specified path, if it can be found.
	 * 
	 * @param contextPath Path of the Context to be removed.  A leading '/' is optional.
	 * @return Returns <b>true</b> if the Context was removed.  Returns <b>false</b>
	 * if the Context is not found.
	 */
	public boolean removeContext(String contextPath) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return false;	// Note: Can't currently occur
		
		if (contextPath != null && contextPath.length() > 0 && !contextPath.startsWith("/"))
			contextPath = "/" + contextPath;
		
		int size = contextManager.getContextCount();
		for (int i = 0; i < size; i++) {
			Context ctx = contextManager.getContext(i);
			if (ctx.getPath().equals(contextPath)) {
				contextManager.removeElement("Context", i);
				return true;
			}
		}
		status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
				NLS.bind(Messages.errorXMLContextNotFoundPath32, contextPath));
		return false;
	}
	
	/**
	 * @param index Index of the Context to remove.
	 * @return Returns <b>true</b> if a Context is removed at the specified
	 * index.  Returns <b>false</b> no Context exists at that index.
	 */
	public boolean removeContext(int index) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return false;	// Note: Can't currently occur

		return contextManager.removeElement("Context", index);
	}

	/**
	 * Gets the work directory associated with the specified 
	 * Context. If the work directory obtained is relative,
	 * it is appended to the specified base path. This method does
	 * not verify if the specified Context currently exists. 
	 * 
	 * @param basePath Path to the base directory for the server.
	 * @param context Context whose work directory to return.
	 * @return Returns the path to the work directory for the specifed
	 * Context.
	 */
	public IPath getContextWorkDirectory(IPath basePath, Context context) {
		if (context == null)
			throw new IllegalArgumentException("Context argument may not be null.");
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur

		StringBuffer sb=new StringBuffer();
		String workDir = contextManager.getWorkDir();
		if (workDir == null)
			workDir = "work";
		sb.append(workDir);
		sb.append("/");
		String hostName = contextManager.getAttributeValue("hostName");
		if (hostName == null)
			hostName = "localhost";
		sb.append(hostName);
		sb.append("_");
		String legacyPort = contextManager.getAttributeValue("port");
		if (legacyPort == null)
			legacyPort = "8080";
		sb.append(legacyPort);
		// Duplicate URLEncoder.encode() used in Tomcat 3.2
		sb.append(URLEncoder.encode(context.getPath()));

		IPath workPath = new Path(sb.toString());
		if (!workPath.isAbsolute()) {
			if (basePath == null)
				basePath = new Path("");
			workPath = basePath.append(workPath);
		}
		return workPath;
	}
	
	/**
	 * Gets the work directory associated with the server. 
	 * If the work directory obtained is relative,
	 * it is appended to the specified base path.
	 * 
	 * @param basePath Path to the base directory for the server.
	 * @return Returns the path to the work directory for the server.
	 */
	public IPath getServerWorkDirectory(IPath basePath) {
		status = Status.OK_STATUS;
		if (contextManager == null && getContextManager() == null)
			return null;	// Note: Can't currently occur

		String workDir = contextManager.getWorkDir();
		if (workDir == null)
			workDir = "work";
		IPath workPath = new Path(workDir);
		if (!workPath.isAbsolute()) {
			if (basePath == null)
				basePath = new Path("");
			workPath = basePath.append(workPath);
		}
		return workPath;
	}
}
