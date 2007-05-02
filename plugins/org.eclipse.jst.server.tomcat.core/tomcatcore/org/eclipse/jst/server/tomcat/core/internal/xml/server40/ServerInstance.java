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
package org.eclipse.jst.server.tomcat.core.internal.xml.server40;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * This class represents an instance of a Tomcat 4.0, or later, server as
 * defined by a specific Service, Engine, and Host defined in a server.xml
 * configuration file.
 */
public class ServerInstance {
	protected static final String DEFAULT_SERVICE = "Catalina";
	protected static final String DEFAULT_SERVICE2 = "Tomcat-Standalone";
	protected static final String DEFAULT_HOST = "localhost";

	protected Server server;
	protected Service service;
	protected Engine engine;
	protected Host host;

	protected String serviceName;
	protected String engineName;
	protected String hostName;
	
	protected String hostWorkDir;

	protected IStatus status = Status.OK_STATUS;

	/**
	 * Constructs a ServerInstance using the specified Server configuration.
	 * The ServerInstance provides access to a selected Service, Engine, and Host
	 * as determined by the supplied service and host name or their defaults.
	 * 
	 * @param server Server configuration on which to base this instance.
	 * @param serviceName Name of the service the instance should use.  Defaults
	 *  to &quot;Catalina&quot; if <b>null</b> or any empty string is specified.
	 * @param hostName Name of the host the instance should use. Defaults to
	 *  the defaultHost setting on the Engine element found under the service.
	 *  If the defaultHost is not set, defaults to &quot;localhost&quot;.
	 */
	public ServerInstance(Server server, String serviceName, String hostName) {
		if (server == null)
			throw new IllegalArgumentException("Server argument may not be null.");
		this.server = server;
		this.serviceName = serviceName;
		this.hostName = hostName;
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
	 * Gets the array of Listeners found in the Server configuration
	 * of this ServerInstance.
	 * 
	 * @return Array of Listeners found in the Server configuration. 
	 */
	public Listener [] getListeners() {
		status = Status.OK_STATUS;
		int size = server.getListenerCount();
		Listener [] listeners = new Listener [size];
		for (int i = 0; i < size; i++) {
			listeners[i] = server.getListener(i);
		}
		return listeners;
	}

	/**
	 * Gets the selected Service in the server configuration of
	 * this ServerInstance. The method will return <b>null</b> if the
	 * server configuration does not contain a Service with the name
	 * selected in this ServerInstance. If no name is selected, then
	 * a service is chosen as follows.  If there is only one Service,
	 * it is returned.  If there are multiple Services, the first one
	 * with the name &quot;Catalina&quot; or &quot;Tomcat-Standalone&quot;
	 * is returned.  If none is found with either name, the first
	 * Service is returned.
	 * 
	 * @return Returns the selected Service for this ServerInstance. Returns <b>null</b>
	 * if the server configuration does not contain a Service with the selected name
	 * or does not contain a Service.
	 */
	public Service getService() {
		status = Status.OK_STATUS;
		if (service != null)
			return service;
		
		int serviceCount = server.getServiceCount();
		// If service name is specified, require that name
		if (serviceName != null) {
			for (int i = 0; i < serviceCount; i++) {
				Service svc = server.getService(i);
				if (serviceName.equalsIgnoreCase(svc.getName())) {
					service = svc;
					return service;
				}
			}
			status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
					NLS.bind(Messages.errorXMLServiceNotFound, serviceName));
			return null;
		}
		// If there is only one service, return that one
		if (serviceCount == 1) {
			service = server.getService(0);
			serviceName = service.getName();
			return service;
		}
		// If there are multiple services, try to find a default name
		if (serviceCount > 1) {
			for (int i = 0; i < serviceCount; i++) {
				Service svc = server.getService(i);
				String svcName = svc.getName();
				if (DEFAULT_SERVICE.equalsIgnoreCase(svc.getName())) {
					service = svc;
					serviceName = svcName;
					return service;
				}
				if (DEFAULT_SERVICE2.equalsIgnoreCase(svc.getName())) {
					service = svc;
					serviceName = svcName;
					return service;
				}
			}
			// If not found, use the first service
			service = server.getService(0);
			serviceName = service.getName();
			return service;
		}
		status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
				Messages.errorXMLNoService);
		return null;
	}

	/**
	 * Gets the connector at the specified index. If a Connector
	 * does not exist at that index a new Connector is appended
	 * and returned.  This method call will return <b>null</b>
	 * if the selected Service does not exist in the server
	 * configuration.
	 * 
	 * @param index Index of the Connector to return.
	 * @return Returns the Connecter at the specified index or
	 * a new Connector if one at that index doesn't exist.
	 * Returns <b>null</b> if the selected Service is not found.
	 */
	public Connector getConnector(int index) {
		status = Status.OK_STATUS;
		if (service == null && getService() == null)
			return null;
		
		return service.getConnector(index);
	}

	/**
	 * Gets the Connectors found in the selected Service in the
	 * server configuration.
	 *    
	 * @return Array of Connectors found in the selected Service in the
	 * server configuration.  Returns <b>null</b> if the selected
	 * Service can not be found.
	 */
	public Connector [] getConnectors() {
		status = Status.OK_STATUS;
		if (service == null && getService() == null)
			return null;
		
		int size = service.getConnectorCount();
		Connector [] connectors = new Connector [size];
		for (int i = 0; i < size; i++) {
			connectors[i] = service.getConnector(i);
		}
		return connectors;
	}
	
	/**
	 * Gets the selected Engine in the server configuration of
	 * this ServerInstance. The Engine is selected by being the
	 * one child Engine of the selected Service. The method will
	 * return <b>null</b> if the selected Service is not found
	 * in the server configuration or the selected Service
	 * does not contain a child Engine.
	 * 
	 * @return Returns the one Engine that is a child of the
	 * selected Service. Returns <b>null</b> if the selected
	 * Service is not found or it contains no child Engine.
	 */
	public Engine getEngine() {
		status = Status.OK_STATUS;
		if (engine != null)
			return engine;

		if (service == null && getService() == null)
			return null;
		
		engine = service.getEngine();
		if (engine == null || engine.getName() == null) {
			status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
					NLS.bind(Messages.errorXMLEngineNotFound, serviceName));
			return null;
		}
		engineName = engine.getName();
		if (hostName == null || hostName.length() == 0) {
			hostName = engine.getDefaultHost();
			if (hostName == null)
				hostName = DEFAULT_HOST;
		}
		return engine;
	}
	
	/**
	 * Gets the selected Host in the server configuration of
	 * this ServerInstance. This method will return <b>null</b>
	 * if selected Engine is not found or the selected Engine
	 * does not contain a Host with the name specified in this
	 * ServerInstance.
	 * 
	 * @return Returns the selected Host for this ServerInstance.
	 * Returns <b>null</b> if the selected Engine is not found
	 * or it does not contain a Host with the expected name.
	 */
	public Host getHost() {
		status = Status.OK_STATUS;
		if (host != null)
			return host;
		
		if (engine == null && getEngine() == null)
			return null;
		
		int size = engine.getHostCount();
		for (int i = 0; i < size; i++) {
			Host h = engine.getHost(i);
			if (hostName.equals(h.getName())) {
				host = h;
				return host;
			}
		}
		status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
				NLS.bind(Messages.errorXMLHostNotFound, new String [] { hostName, engineName, serviceName }));
		return null;
	}
	
	/**
	 * Gets the Context that has the specified path within the selected
	 * Host. This method will return <b>null</b> if the selected Host or
	 * Context with the required path is not found in the server configuration.
	 * 
	 * @param contextPath Path of the Context to be returned.  A leading '/' is optional.
	 * @return Returns the Context whose <b>path</b> attribute matches
	 * the specified contextPath. Returns <b>null</b> if the selected
	 * Host or a Context with the required path is not found.
	 */
	public Context getContext(String contextPath) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;
		
		if (contextPath != null && contextPath.length() > 0 && !contextPath.startsWith("/"))
			contextPath = "/" + contextPath;
		
		int size = host.getContextCount();
		for (int i = 0; i < size; i++) {
			Context ctx = host.getContext(i);
			if (ctx.getPath().equals(contextPath)) {
				return ctx;
			}
		}
		status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
				NLS.bind(Messages.errorXMLContextNotFoundPath,
						new String [] { contextPath, serviceName, engineName, hostName }));
		return null;
	}
	
	/**
	 * Gets the Context at the specified index within the selected
	 * Host. If a Connector does not exist at the specified index,
	 * a new Context will be appended and returned. This method will
	 * return <b>null</b> if the selected Host can not be found in the
	 * server configuration.
	 * 
	 * @param index Index of the Context to return.
	 * @return Returns the Context at the specified index, or
	 * a new appended Context if the index is beyond any existing
	 * Contexts. Returns <b>null</b> if the selected Host can not
	 * be found.
	 */
	public Context getContext(int index) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;
		
		return host.getContext(index);
	}
	
	/**
	 * Gets the Contexts contained in the selected Host.  This
	 * method will return <b>null</b> if the selected Host can
	 * not be found in the server configuration.
	 * 
	 * @return Array of Contexts contained in the selected Host.
	 * Returns <b>null</b> if the selected Host is not found.
	 */
	public Context [] getContexts() {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;
		
		int size = host.getContextCount();
		Context [] contexts = new Context [size];
		for (int i = 0; i < size; i++) {
			contexts[i] = host.getContext(i);
		}
		return contexts;
	}
	
	/**
	 * Creates a new Context and inserts it before the specifed index
	 * or appends it if the index is beyond any existing Contexts in the
	 * selected Host. This method will return <b>null</b> if the selected
	 * Host is not found in the server configuration.
	 * 
	 * @param index Index prior to which to insert the new Contexts.
	 * @return Returns the created Context. Returns <b>null</b> if the
	 * selected Host is not found.
	 */
	public Context createContext(int index) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;

		return (Context)host.createElement(index, "Context");
	}
	
	/**
	 * Removes the Context with the specified path, if it can be found.
	 * 
	 * @param contextPath Path of the Context to be removed.  A leading '/' is optional.
	 * @return Returns <b>true</b> if the Context was removed.  Returns <b>false</b>
	 * if the Context or the selected Host is not found.
	 */
	public boolean removeContext(String contextPath) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return false;
		
		if (contextPath != null && contextPath.length() > 0 && !contextPath.startsWith("/"))
			contextPath = "/" + contextPath;
		
		int size = host.getContextCount();
		for (int i = 0; i < size; i++) {
			Context ctx = host.getContext(i);
			if (ctx.getPath().equals(contextPath)) {
				host.removeElement("Context", i);
				return true;
			}
		}
		status = new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
				NLS.bind(Messages.errorXMLContextNotFoundPath,
						new String [] { contextPath, serviceName, engineName, hostName }));
		return false;
	}
	
	/**
	 * @param index Index of the Context to remove.
	 * @return Returns <b>true</b> if a Context is removed at the specified
	 * index.  Returns <b>false</b> no Context exists at that index or
	 * the selected Host is not found.
	 */
	public boolean removeContext(int index) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return false;

		return host.removeElement("Context", index);
	}
	
	/**
	 * Gets the directory where the context XML files are stored
	 * for the selected Service, Engine, and Host found in the
	 * server configuration. This method will return <b>null</b>
	 * if the selected Host is not found in the server configuration.
	 * 
	 * @param confDir Path to the &quot;conf&quot; directory for
	 * the server.
	 * @return Returns the path to the context XML directory.
	 * Returns <b>null</b> if the selected Host can not be found.
	 */
	public IPath getContextXmlDirectory(IPath confDir) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;
		
		return confDir.append(engineName).append(hostName);
	}
	
	
	/**
	 * Gets the path for the context XML file that would be used
	 * if this context were written to a separate files. This
	 * method will return <b>null</b> if the selected Host is
	 * not found in the server configuration. This method does
	 * not verify if the specified Context currently exists
	 * within the selected Host.
	 * 
	 * @param baseDir Path to the base directory for the server.
	 * @param context Context whose context XML file path to return.
	 * @return Returns the path to the context XML file for the specifed
	 * Context. Returns <b>null</b> if the selected Host can not be
	 * found or the context has no path attribute.
	 */
	public IPath getContextFilePath(IPath baseDir, Context context) {
		if (context == null)
			throw new IllegalArgumentException(Messages.errorXMLNullContextArg);
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;

		IPath contextFilePath = null;
		IPath contextDir = getContextXmlDirectory(baseDir.append("conf"));
		String name = context.getPath();
		if (name != null) {
			if (name.startsWith("/"))
				name = name.substring(1);
			if (name.length() == 0)
				name = "ROOT";
			contextFilePath = contextDir.append(name + ".xml");
		}
		else {
			// TODO Set error status
		}
		return contextFilePath;
	}
	
	/**
	 * Gets the work directory associated with the specified 
	 * Context. If the work directory obtained is relative,
	 * it is appended to the specified base path. This method
	 * will return <b>null</b> if the selected Host is not
	 * found in the server configuration.  This method does
	 * not verify if the specified Context currently exists
	 * within the selected Host. 
	 * 
	 * @param basePath Path to the base directory for the server.
	 * @param context Context whose work directory to return.
	 * @return Returns the path to the work directory for the specifed
	 * Context. Returns <b>null</b> if the selected Host can not be
	 * found.
	 */
	public IPath getContextWorkDirectory(IPath basePath, Context context) {
		if (context == null)
			throw new IllegalArgumentException(Messages.errorXMLNullContextArg);
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;

		// If the work directory is specified on the context, use that one
		String workDir = context.getAttributeValue("workDir");
		if (workDir == null) {
			// If context doesn't specify the work directory, check the host
			if (hostWorkDir == null) {
				hostWorkDir = host.getAttributeValue("workDir");
				if (hostWorkDir == null || hostWorkDir.length() == 0)
					// If host doesn't specify the work directory, build the default
					hostWorkDir = "work/" + engineName + "/" + hostName;
			}
			String ctxName = context.getPath();
			if (ctxName.startsWith("/"))
				ctxName = ctxName.substring(1);
			ctxName = ctxName.replace('/', '_');
			ctxName = ctxName.replace('\\', '_');
			if (ctxName.length() == 0)
				ctxName = "_";
			workDir = hostWorkDir + "/" + ctxName;
		}
		IPath workPath = new Path(workDir);
		if (!workPath.isAbsolute()) {
			if (basePath == null)
				basePath = new Path("");
			workPath = basePath.append(workPath);
		}
		return workPath;
	}

	/**
	 * Gets the work directory associated with the selected 
	 * Host. If the work directory obtained is relative,
	 * it is appended to the specified base path. This method
	 * will return <b>null</b> if the selected Host is not
	 * found in the server configuration.
	 * 
	 * @param basePath Path to the base directory for the server.
	 * @return Returns the path to the work directory for the selected
	 * Host. Returns <b>null</b> if the selected Host can not be
	 * found.
	 */
	public IPath getHostWorkDirectory(IPath basePath) {
		status = Status.OK_STATUS;
		if (host == null && getHost() == null)
			return null;
		// If context doesn't specify the work directory, check the host
		if (hostWorkDir == null) {
			hostWorkDir = host.getAttributeValue("workDir");
			if (hostWorkDir == null || hostWorkDir.length() == 0)
				// If host doesn't specify the work directory, build the default
				hostWorkDir = "work/" + engineName + "/" + hostName;
		}
		IPath workPath = new Path(hostWorkDir);
		if (!workPath.isAbsolute()) {
			if (basePath == null)
				basePath = new Path("");
			workPath = basePath.append(workPath);
		}
		return workPath;
	}
}
