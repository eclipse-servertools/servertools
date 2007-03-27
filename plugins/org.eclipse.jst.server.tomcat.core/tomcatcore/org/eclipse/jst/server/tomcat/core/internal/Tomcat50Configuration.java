/**********************************************************************
 * Copyright (c) 2003, 2005, 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.PublishUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Connector;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Listener;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Service;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.ServerPort;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
/**
 * Tomcat v5.0 server configuration.
 */
public class Tomcat50Configuration extends TomcatConfiguration {
	protected static final String DEFAULT_SERVICE = "Catalina";
	protected Server server;
	protected ServerInstance serverInstance;
	protected Factory serverFactory;
	protected boolean isServerDirty;

	protected WebAppDocument webAppDocument;

	protected Document tomcatUsersDocument;

	protected String policyFile;

	protected String propertiesFile;
	
	/**
	 * Tomcat50Configuration constructor.
	 * 
	 * @param path a path
	 */
	public Tomcat50Configuration(IFolder path) {
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
			if (port.getName().equals("HTTP") && port.getId().indexOf('/') < 0)
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
		List ports = new ArrayList();
	
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
					String name = "HTTP";
					String protocol2 = "HTTP";
					boolean advanced = true;
					String[] contentTypes = null;
					int port = -1;
					try {
						port = Integer.parseInt(connector.getPort());
					} catch (Exception e) {
						// ignore
					}
					String protocol = connector.getProtocol();
					if (protocol != null && protocol.length() > 0) {
						name = protocol;
						protocol2 = protocol; 
					}
					if ("HTTP".equals(protocol))
						contentTypes = new String[] { "web", "webservices" };
					String secure = connector.getSecure();
					if (secure != null && secure.length() > 0) {
						name = "SSL";
						protocol2 = "SSL";
					} else
						advanced = false;
					String portId;
					if (instanceServiceName != null && instanceServiceName.equals(service.getName()))
						portId = Integer.toString(i);
					else
						portId = i +"/" + j;
					ports.add(new ServerPort(portId, name, port, protocol2, contentTypes, advanced));
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
		List list = new ArrayList();
	
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
			Trace.trace(Trace.SEVERE, "Error getting project refs", e);
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
	 * @see TomcatConfiguration#load(IPath, IProgressMonitor)
	 */
	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 6);
			
			// check for catalina.policy to verify that this is a v5.0 config
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

			// load properties file
			File file = path.append("catalina.properties").toFile();
			if (file.exists())
				propertiesFile = TomcatVersionHelper.getFileContents(new FileInputStream(file));
			else
				propertiesFile = null;
			monitor.worked(1);
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load Tomcat v5.0 configuration from " + path.toOSString() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, path.toOSString()), e));
		}
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
	 * @see TomcatConfiguration#load(IFolder, IProgressMonitor)
	 */
	public void load(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.loadingTask, 1000);
	
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

			// load catalina.properties
			file = folder.getFile("catalina.properties");
			if (file.exists()) {
				in = file.getContents();
				propertiesFile = TomcatVersionHelper.getFileContents(in);
			}
			else
				propertiesFile = null;
			monitor.worked(200);
			
			if (monitor.isCanceled())
				throw new Exception("Cancelled");
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not reload Tomcat v5.0 configuration from: " + folder.getFullPath() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), e));
		}
	}

	/**
	 * Save to the given directory.
	 * @param path a path
	 * @param forceDirty boolean
	 * @param monitor a progress monitor
	 * @exception CoreException
	 */
	protected void save(IPath path, boolean forceDirty, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 4);
	
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
			if (propertiesFile != null && forceDirty) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(path.append("catalina.properties").toFile()));
				bw.write(propertiesFile);
				bw.close();
			}
			monitor.worked(1);
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v5.0 configuration to " + path, e);
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
			monitor.beginTask(Messages.savingTask, 1100);
			
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
			
			// save catalina.properties
			if (propertiesFile != null) {
				in = new ByteArrayInputStream(propertiesFile.getBytes());
				file = folder.getFile("catalina.properties");
				if (file.exists())
					monitor.worked(200);
					//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
				else
					file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			} else
				monitor.worked(200);
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v5.0 configuration to " + folder.toString(), e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotSaveConfiguration, new String[] {e.getLocalizedMessage()}), e));
		}
	}

	protected static boolean hasMDBListener(Server server) {
		if (server == null)
			return false;
		
		int count = server.getListenerCount();
		if (count == 0)
			return false;
			
		for (int i = 0; i < count; i++) {
			Listener listener = server.getListener(i);
			if (listener != null && listener.getClassName() != null && listener.getClassName().indexOf("mbean") >= 0)
				return true;
		}
		return false;
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
	 * Add context configuration found in META-INF/context.xml files
	 * present in projects to published server.xml.
	 * 
	 * @param baseDir path to catalina instance directory
	 * @param deployDir path to deployment directory
	 * @param monitor a progress monitor or null
	 * @return result of operation
	 */
	protected IStatus publishContextConfig(IPath baseDir, IPath deployDir, IProgressMonitor monitor) {
		return TomcatVersionHelper.publishCatalinaContextConfig(baseDir, deployDir, monitor);
	}

	/**
	 * Update contexts in server.xml to serve projects directly without
	 * publishing.
	 * 
	 * @param baseDir path to catalina instance directory
	 * @param monitor a progress monitor or null
	 * @return result of operation
	 */
	protected IStatus updateContextsToServeDirectly(IPath baseDir, String loader, IProgressMonitor monitor) {
		return TomcatVersionHelper.updateContextsToServeDirectly(baseDir, loader, monitor);
	}

	/**
	 * Cleanup the server instance.  This consists of deleting the work
	 * directory associated with Contexts that are going away in the
	 * up coming publish.  Also, Context XML files which may have been
	 * created for these Contexts are also deleted.
	 * 
	 * @param baseDir path to server instance directory, i.e. catalina.base
	 * @param installDir path to server installation directory (not currently used)
	 * @param monitor a progress monitor or null
	 * @return MultiStatus containing results of the cleanup operation
	 */
	protected IStatus cleanupServer(IPath baseDir, IPath installDir, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, Messages.cleanupServerTask, null);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(Messages.cleanupServerTask, 200);

		try {
			monitor.subTask(Messages.detectingRemovedProjects);

			IPath serverXml = baseDir.append("conf").append("server.xml");
			ServerInstance oldInstance = TomcatVersionHelper.getCatalinaServerInstance(serverXml, null, null);
			if (oldInstance != null) {
				List modules = getWebModules();
				Collection oldPaths = TomcatVersionHelper.getRemovedCatalinaContexts(oldInstance, modules);
				monitor.worked(100);
				if (oldPaths != null && oldPaths.size() > 0) {
					// Begin building path to context directory
					IPath contextXmlDir = oldInstance.getContextXmlDirectory(baseDir.append("conf"));

					// Delete context files and work directories for managed web modules that have gone away
					if (oldPaths.size() > 0 ) {
						IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 100);
						subMonitor.beginTask(Messages.deletingContextFilesTask, oldPaths.size() * 200);
						
						Iterator iter = oldPaths.iterator();
						while (iter.hasNext()) {
							String oldPath = (String)iter.next();
							// Derive the context file name from the path + ".xml", minus the leading '/'
							String fileName;
							if (oldPath.length() > 0)
								fileName = oldPath.substring(1) + ".xml";
							else
								fileName = "ROOT.xml";
							IPath contextPath = contextXmlDir.append(fileName);
							File contextFile = contextPath.toFile();
							if (contextFile.exists()) {
								subMonitor.subTask(NLS.bind(Messages.deletingContextFile, fileName));
								if (contextFile.delete()) {
									if (Trace.isTraceEnabled())
										Trace.trace(Trace.FINER, "Leftover context file " + fileName + " deleted.");
									ms.add(new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0,
											NLS.bind(Messages.deletedContextFile, fileName), null));
								} else {
									Trace.trace(Trace.SEVERE, "Could not delete obsolete context file " + contextPath.toOSString());
									ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
											NLS.bind(Messages.errorCouldNotDeleteContextFile, contextPath.toOSString()), null));
								}
								subMonitor.worked(100);
							}
							
							// Delete work directory associated with the removed context if it is within confDir.
							// If it is outside of confDir, assume user is going to manage it.
							Context ctx = oldInstance.getContext(oldPath);
							IPath ctxWorkPath = oldInstance.getContextWorkDirectory(baseDir, ctx);
							if (baseDir.isPrefixOf(ctxWorkPath)) {
								File ctxWorkDir = ctxWorkPath.toFile();
								if (ctxWorkDir.exists() && ctxWorkDir.isDirectory()) {
									IStatus [] results = PublishUtil.deleteDirectory(ctxWorkDir, ProgressUtil.getSubMonitorFor(monitor, 100));
									if (results.length > 0) {
										Trace.trace(Trace.SEVERE, "Could not delete work directory " + ctxWorkDir.getPath() + " for removed context " + oldPath);
										for (int i = 0; i < results.length; i++) {
											ms.add(results[i]);
										}
									}
								}
								else
									monitor.worked(100);
							}
							else
								monitor.worked(100);
						}
						subMonitor.done();
					} else {
						monitor.worked(100);
					}
				}
			}
			// Else no server.xml.  Assume first publish to new temp directory
			else {
				monitor.worked(200);
			}
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Server cleaned");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not cleanup server at " + baseDir.toOSString() + ": " + e.getMessage());
			ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
					NLS.bind(Messages.errorCleanupServer, new String[] {e.getLocalizedMessage()}), e));
		}
		
		monitor.done();
		return ms;
	}

	/**
	 * @see TomcatConfiguration#localizeConfiguration(IPath, IPath, TomcatServer, IProgressMonitor)
	 */
	public IStatus localizeConfiguration(IPath baseDir, IPath deployDir, TomcatServer tomcatServer, IProgressMonitor monitor) {
		return TomcatVersionHelper.localizeConfiguration(baseDir, deployDir, tomcatServer, monitor);
	}
}