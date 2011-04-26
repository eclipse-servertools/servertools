/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Connector;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Service;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.ServerPort;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
/**
 * Tomcat v4.0 server configuration.
 */
public class Tomcat40Configuration extends TomcatConfiguration {
	protected static final String DEFAULT_SERVICE = "Tomcat-Standalone";
	protected static final String HTTP_CONNECTOR = "org.apache.catalina.connector.http.HttpConnector";
	protected static final String SSL_SOCKET_FACTORY = "org.apache.catalina.net.SSLServerSocketFactory";
	protected static final String TEST_CONNECTOR = "org.apache.catalina.connector.test.HttpConnector";
	protected static final String AJP_CONNECTOR = "org.apache.ajp.tomcat4.Ajp13Connector";
	protected static final String WARP_CONNECTOR = "org.apache.catalina.connector.warp.WarpConnector";

	protected Server server;
	protected ServerInstance serverInstance;
	protected Factory serverFactory;
	protected boolean isServerDirty;

	protected WebAppDocument webAppDocument;

	protected Document tomcatUsersDocument;

	protected String policyFile;

	/**
	 * Tomcat40Configuration constructor.
	 * 
	 * @param path a path
	 */
	public Tomcat40Configuration(IFolder path) {
		super(path);
	}

	/**
	 * Return the port number.
	 * @return int
	 */
	public ServerPort getMainPort() {
		Iterator iterator = getServerPorts().iterator();
		while (iterator.hasNext()) {
			ServerPort port = (ServerPort) iterator.next();
			// Return only an HTTP port from the selected Service
			if (port.getName().equals("HTTP Connector") && port.getId().indexOf('/') < 0)
				return port;
		}
		return null;
	}
	
	/**
	 * Returns the mime mappings.
	 * @return java.util.List
	 */
	public List getMimeMappings() {
		return webAppDocument.getMimeMappings();
	}

	/**
	 * Returns a list of ServerPorts that this configuration uses.
	 *
	 * @return java.util.List
	 */
	public List getServerPorts() {
		List<ServerPort> ports = new ArrayList<ServerPort>();
	
		// first add server port
		try {
			int port = Integer.parseInt(server.getPort());
			ports.add(new ServerPort("server", Messages.portServer, port, "TCPIP"));
		} catch (Exception e) {
			// ignore
		}
	
		// add connectors
		try {
			String instanceServiceName = serverInstance.getService().getName();
			int size = server.getServiceCount();
			for (int i = 0; i < size; i++) {
				Service service = server.getService(i);
				int size2 = service.getConnectorCount();
				for (int j = 0; j < size2; j++) {
					Connector connector = service.getConnector(j);
					String className = connector.getClassName();
					String name = Messages.portUnknown;
					String protocol = "TCPIP";
					boolean advanced = true;
					String[] contentTypes = null;
					int port = -1;
					try {
						port = Integer.parseInt(connector.getPort());
					} catch (Exception e) {
						// ignore
					}
					if (HTTP_CONNECTOR.equals(className)) {
						name = "HTTP Connector";
						protocol = "HTTP";
						contentTypes = new String[] { "web", "webservices" };
						// check for SSL connector
						try {
							Element element = connector.getSubElement("Factory");
							if (SSL_SOCKET_FACTORY.equals(element.getAttribute("className"))) {
								name = "SSL Connector";
								protocol = "SSL";
							}
						} catch (Exception e) {
							// ignore
						}
						if ("HTTP".equals(protocol))
							advanced = false;
					} else if (AJP_CONNECTOR.equals(className))
						name = "AJP Connector";
					else if (WARP_CONNECTOR.equals(className))
						name = "Warp Connector";
					String portId;
					if (instanceServiceName != null && instanceServiceName.equals(service.getName()))
						portId = Integer.toString(j);
					else
						portId = i +"/" + j;
					if (className != null && className.length() > 0)
						ports.add(new ServerPort(portId, name, port, protocol, contentTypes, advanced));
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting server ports", e);
		}
		return ports;
	}
	
	/**
	 * Return a list of the web modules in this server.
	 * @return java.util.List
	 */
	public List getWebModules() {
		List<WebModule> list = new ArrayList<WebModule>();
	
		try {
			Context [] contexts = serverInstance.getContexts();
			if (contexts != null) {
				for (int i = 0; i < contexts.length; i++) {
					Context context = contexts[i];
					String reload = context.getReloadable();
					if (reload == null)
						reload = "false";
					WebModule module = new WebModule(context.getPath(), 
						context.getDocBase(), context.getSource(),
						reload.equalsIgnoreCase("true") ? true : false);
					list.add(module);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting modules", e);
		}
		return list;
	}
	
	/**
	 * @see TomcatConfiguration#getServerWorkDirectory(IPath)
	 */
	public IPath getServerWorkDirectory(IPath basePath) {
		return serverInstance.getHostWorkDirectory(basePath);
	}

	/**
	 * @see TomcatConfiguration#getContextWorkDirectory(IPath, ITomcatWebModule)
	 */
	public IPath getContextWorkDirectory(IPath basePath, ITomcatWebModule module) {
		Context context = serverInstance.getContext(module.getPath());
		if (context != null)
			return serverInstance.getContextWorkDirectory(basePath, context);
		
		return null;
	}

	/**
	 * @see TomcatConfiguration#importFromPath(IPath, boolean, IProgressMonitor)
	 */
	public void importFromPath(IPath path, boolean isTestEnv, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
		
		// for test environment, remove existing contexts since a separate
		// catalina.base will be used
		if (isTestEnv) {
			while (serverInstance.removeContext(0)) {
				// no-op
			}
		}
	}

	/**
	 * @see TomcatConfiguration#load(IPath, IProgressMonitor)
	 */
	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 5);
			
			// check for catalina.policy to verify that this is a v4.0 config
			InputStream in = new FileInputStream(path.append("catalina.policy").toFile());
			in.read();
			in.close();
			monitor.worked(1);
	
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			server = (Server) serverFactory.loadDocument(new FileInputStream(path.append("server.xml").toFile()));
			serverInstance = new ServerInstance(server, null, null);
			monitor.worked(1);

			webAppDocument = new WebAppDocument(path.append("web.xml"));
			monitor.worked(1);
	
			tomcatUsersDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(new FileInputStream(path.append("tomcat-users.xml").toFile())));
			monitor.worked(1);
		
			// load policy file
			policyFile = TomcatVersionHelper.getFileContents(new FileInputStream(path.append("catalina.policy").toFile()));
			monitor.worked(1);
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load Tomcat v4.0 configuration from " + path.toOSString() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, path.toOSString()), e));
		}
	}

	/**
	 * @see TomcatConfiguration#load(IFolder, IProgressMonitor)
	 */
	public void load(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 800);
	
			// check for catalina.policy to verify that this is a v4.0 config
			IFile file = folder.getFile("catalina.policy");
			if (!file.exists())
				throw new CoreException(new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), null));
	
			// load server.xml
			file = folder.getFile("server.xml");
			InputStream in = file.getContents();
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			server = (Server) serverFactory.loadDocument(in);
			serverInstance = new ServerInstance(server, null, null);
			monitor.worked(200);
	
			// load web.xml
			file = folder.getFile("web.xml");
			webAppDocument = new WebAppDocument(file);
			monitor.worked(200);
	
			// load tomcat-users.xml
			file = folder.getFile("tomcat-users.xml");
			in = file.getContents();
			
			tomcatUsersDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(in));
			monitor.worked(200);
		
			// load catalina.policy
			file = folder.getFile("catalina.policy");
			in = file.getContents();
			policyFile = TomcatVersionHelper.getFileContents(in);
			monitor.worked(200);
	
			if (monitor.isCanceled())
				throw new Exception("Cancelled");
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not reload Tomcat v4.0 configuration from: " + folder.getFullPath() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), e));
		}
	}
	
	/**
	 * Save to the given directory.
	 * 
	 * @param path a path
	 * @param forceDirty boolean
	 * @param monitor a progress monitor
	 * @exception CoreException
	 */
	protected void save(IPath path, boolean forceDirty, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 3);
			
			// make sure directory exists
			if (!path.toFile().exists()) {
				forceDirty = true;
				path.toFile().mkdir();
			}
			monitor.worked(1);
			
			// save files
			if (forceDirty || isServerDirty) {
				serverFactory.save(path.append("server.xml").toOSString());
				isServerDirty = false;
			}
			monitor.worked(1);
			
			webAppDocument.save(path.append("web.xml").toOSString(), forceDirty);
			monitor.worked(1);
			
			if (forceDirty)
				XMLUtil.save(path.append("tomcat-users.xml").toOSString(), tomcatUsersDocument);
			monitor.worked(1);
			
			if (forceDirty) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(path.append("catalina.policy").toFile()));
				bw.write(policyFile);
				bw.close();
			}
			monitor.worked(1);
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v4.0 configuration to " + path, e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotSaveConfiguration, new String[] {e.getLocalizedMessage()}), e));
		}
	}

	/**
	 * Save to the given directory.  All files are forced to be saved.
	 * 
	 * @param path desination path for the files
	 * @param monitor a progress monitor
	 * @exception CoreException
	 */
	public void save(IPath path, IProgressMonitor monitor) throws CoreException {
		save(path, true, monitor);
	}

	/**
	 * Save the information held by this object to the given directory.
	 *
	 * @param folder a folder
	 * @param monitor a progress monitor
	 * @throws CoreException
	 */
	public void save(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 900);
			
			// save server.xml
			byte[] data = serverFactory.getContents();
			InputStream in = new ByteArrayInputStream(data);
			IFile file = folder.getFile("server.xml");
			if (file.exists()) {
				if (isServerDirty)
					file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
				else
					monitor.worked(200);
			} else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			isServerDirty = false;
			
			// save web.xml
			webAppDocument.save(folder.getFile("web.xml"), ProgressUtil.getSubMonitorFor(monitor, 200));
			
			// save tomcat-users.xml
			data = XMLUtil.getContents(tomcatUsersDocument);
			in = new ByteArrayInputStream(data);
			file = folder.getFile("tomcat-users.xml");
			if (file.exists())
				monitor.worked(200);
				//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			
			// save catalina.policy
			in = new ByteArrayInputStream(policyFile.getBytes());
			file = folder.getFile("catalina.policy");
			if (file.exists())
				monitor.worked(200);
				//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v4.0 configuration to " + folder.toString(), e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotSaveConfiguration, new String[] {e.getLocalizedMessage()}), e));
		}
	}

	/**
	 * @see ITomcatConfigurationWorkingCopy#addMimeMapping(int, IMimeMapping)
	 */
	public void addMimeMapping(int index, IMimeMapping map) {
		webAppDocument.addMimeMapping(index, map);
		firePropertyChangeEvent(ADD_MAPPING_PROPERTY, new Integer(index), map);
	}

	/**
	 * @see ITomcatConfigurationWorkingCopy#addWebModule(int, ITomcatWebModule)
	 */
	public void addWebModule(int index, ITomcatWebModule module) {
		try {
			Context context = serverInstance.createContext(index);
			if (context != null) {
				context.setDocBase(module.getDocumentBase());
				context.setPath(module.getPath());
				context.setReloadable(module.isReloadable() ? "true" : "false");
				if (module.getMemento() != null && module.getMemento().length() > 0)
					context.setSource(module.getMemento());
				isServerDirty = true;
				firePropertyChangeEvent(ADD_WEB_MODULE_PROPERTY, null, module);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error adding web module " + module.getPath(), e);
		}
	}

	/**
	 * Change the extension of a mime mapping.
	 * 
	 * @param index
	 * @param map
	 */
	public void modifyMimeMapping(int index, IMimeMapping map) {
		webAppDocument.modifyMimeMapping(index, map);
		firePropertyChangeEvent(MODIFY_MAPPING_PROPERTY, new Integer(index), map);
	}
	
	/**
	 * Modify the port with the given id.
	 *
	 * @param id java.lang.String
	 * @param port int
	 */
	public void modifyServerPort(String id, int port) {
		try {
			if ("server".equals(id)) {
				server.setPort(port + "");
				isServerDirty = true;
				firePropertyChangeEvent(MODIFY_PORT_PROPERTY, id, new Integer(port));
				return;
			}
	
			int i = id.indexOf("/");
			// If a connector in the instance Service
			if (i < 0) {
				int connNum = Integer.parseInt(id);
				Connector connector = serverInstance.getConnector(connNum);
				if (connector != null) {
					connector.setPort(port + "");
					isServerDirty = true;
					firePropertyChangeEvent(MODIFY_PORT_PROPERTY, id, new Integer(port));
				}
			}
			// Else a connector in another Service
			else {
				int servNum = Integer.parseInt(id.substring(0, i));
				int connNum = Integer.parseInt(id.substring(i + 1));
				
				Service service = server.getService(servNum);
				Connector connector = service.getConnector(connNum);
				connector.setPort(port + "");
				isServerDirty = true;
				firePropertyChangeEvent(MODIFY_PORT_PROPERTY, id, new Integer(port));
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error modifying server port " + id, e);
		}
	}
	/**
	 * Change a web module.
	 * @param index int
	 * @param docBase java.lang.String
	 * @param path java.lang.String
	 * @param reloadable boolean
	 */
	public void modifyWebModule(int index, String docBase, String path, boolean reloadable) {
		try {
			Context context = serverInstance.getContext(index);
			if (context != null) {
				context.setPath(path);
				context.setDocBase(docBase);
				context.setReloadable(reloadable ? "true" : "false");
				isServerDirty = true;
				WebModule module = new WebModule(path, docBase, null, reloadable);
				firePropertyChangeEvent(MODIFY_WEB_MODULE_PROPERTY, new Integer(index), module);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error modifying web module " + index, e);
		}
	}
	
	/**
	 * Removes a mime mapping.
	 * @param index int
	 */
	public void removeMimeMapping(int index) {
		webAppDocument.removeMimeMapping(index);
		firePropertyChangeEvent(REMOVE_MAPPING_PROPERTY, null, new Integer(index));
	}
	
	/**
	 * Removes a web module.
	 * @param index int
	 */
	public void removeWebModule(int index) {
		try {
			serverInstance.removeContext(index);
			isServerDirty = true;
			firePropertyChangeEvent(REMOVE_WEB_MODULE_PROPERTY, null, new Integer(index));
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error removing module ref " + index, e);
		}
	}

	/**
	 * Cleanup the server instance.  This consists of deleting the work
	 * directory associated with Contexts that are going away in the
	 * up coming publish.
	 * 
	 * @param baseDir path to server instance directory, i.e. catalina.base
	 * @param installDir path to server installation directory (not currently used)
	 * @param monitor a progress monitor or null
	 * @return MultiStatus containing results of the cleanup operation
	 */
	protected IStatus cleanupServer(IPath baseDir, IPath installDir, boolean removeKeptContextFiles, IProgressMonitor monitor) {
		List modules = getWebModules();
		return TomcatVersionHelper.cleanupCatalinaServer(baseDir, installDir, removeKeptContextFiles, modules, monitor);
	}

	/**
	 * @see TomcatConfiguration#localizeConfiguration(IPath, IPath, TomcatServer, IProgressMonitor)
	 */
	public IStatus localizeConfiguration(IPath baseDir, IPath deployDir, TomcatServer tomcatServer, IProgressMonitor monitor) {
		return TomcatVersionHelper.localizeConfiguration(baseDir, deployDir, tomcatServer, monitor);
	}
}
