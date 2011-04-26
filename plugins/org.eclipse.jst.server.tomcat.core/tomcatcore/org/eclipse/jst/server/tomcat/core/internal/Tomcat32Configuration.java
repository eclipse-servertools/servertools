/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.*;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.util.PublishHelper;
/**
 * Tomcat v3.2 server configuration.
 */
public class Tomcat32Configuration extends TomcatConfiguration {
	protected static final String HTTP_HANDLER = "org.apache.tomcat.service.http.HttpConnectionHandler";
	protected static final String APACHE_HANDLER = "org.apache.tomcat.service.connector.Ajp12ConnectionHandler";
	protected static final String SSL_SOCKET_FACTORY = "org.apache.tomcat.net.SSLSocketFactory";

	protected Server server;
	protected ServerInstance serverInstance;
	protected Factory serverFactory;
	protected boolean isServerDirty;

	protected WebAppDocument webAppDocument;

	protected Document tomcatUsersDocument;

	protected String policyFile;

	/**
	 * Tomcat32Configuration constructor.
	 * 
	 * @param path a path
	 */
	public Tomcat32Configuration(IFolder path) {
		super(path);
	}

	/**
	 * Returns the main server port.
	 * @return TomcatServerPort
	 */
	public ServerPort getMainPort() {
		Iterator iterator = getServerPorts().iterator();
		while (iterator.hasNext()) {
			ServerPort port = (ServerPort) iterator.next();
			if (port.getName().equals("HTTP Connector"))
				return port;
		}
		return null;
	}

	/**
	 * Returns the prefix that is used in front of the
	 * web module path property. (e.g. "webapps")
	 *
	 * @return java.lang.String
	 */
	public String getDocBasePrefix() {
		return "webapps/";
	}

	/**
	 * Returns the mime mappings.
	 * @return java.util.List
	 */
	public List getMimeMappings() {
		if (webAppDocument == null)
			return new ArrayList(0);
		
		return webAppDocument.getMimeMappings();
	}

	/**
	 * Returns the server object (root of server.xml).
	 * @return org.eclipse.jst.server.tomcat.internal.xml.server32.Server
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Returns a list of ServerPorts that this configuration uses.
	 *
	 * @return java.util.List
	 */
	public List getServerPorts() {
		List<ServerPort> ports = new ArrayList<ServerPort>();
	
		try {
			Connector [] connectors = serverInstance.getConnectors();
			if (connectors != null) {
				for (int i = 0; i < connectors.length; i++) {
					Connector connector = connectors[i];
					int paramCount = connector.getParameterCount();
					String handler = null;
					String name = Messages.portUnknown;
					String socketFactory = null;
					String protocol = "TCPIP";
					boolean advanced = true;
					String[] contentTypes = null;
					int port = -1;
					for (int j = 0; j < paramCount; j++) {
						Parameter p = connector.getParameter(j);
						if ("port".equals(p.getName())) {
							try {
								port = Integer.parseInt(p.getValue());
							} catch (Exception e) {
								// ignore
							}
						} else if ("handler".equals(p.getName()))
							handler = p.getValue();
						else if ("socketFactory".equals(p.getName()))
							socketFactory = p.getValue();
					}
					if (HTTP_HANDLER.equals(handler)) {
						protocol = "HTTP";
						contentTypes = new String[] { "web", "webservices" };
						if (SSL_SOCKET_FACTORY.equals(socketFactory)) {
							protocol = "SSL";
							name = "SSL Connector";
						} else {
							name = "HTTP Connector";
							advanced = false;
						}
					} else if (APACHE_HANDLER.equals(handler))
						name = "Apache Connector";
					if (handler != null)
						ports.add(new ServerPort(i + "", name, port, protocol, contentTypes, advanced));
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error getting server ports", e);
		}
	
		return ports;
	}
	
	/**
	 * Returns the tomcat-users.xml document.
	 *
	 * @return org.w3c.dom.Document
	 */
	public Document getTomcatUsersDocument() {
		return tomcatUsersDocument;
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
			Trace.trace(Trace.SEVERE, "Error getting project refs", e);
		}
	
		return list;
	}
	
	/**
	 * @see TomcatConfiguration#getServerWorkDirectory(IPath)
	 */
	public IPath getServerWorkDirectory(IPath basePath) {
		return serverInstance.getServerWorkDirectory(basePath);
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
			monitor.beginTask(Messages.loadingTask, 5);
	
			// check for tomcat.policy to verify that this is a v3.2 config
			InputStream in = new FileInputStream(path.append("tomcat.policy").toFile());
			in.read();
			in.close();
			monitor.worked(1);
			
			// create server.xml
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server32");
			server = (Server) serverFactory.loadDocument(new FileInputStream(path.append("server.xml").toFile()));
			serverInstance = new ServerInstance(server);
			monitor.worked(1);
	
			webAppDocument = new WebAppDocument(path.append("web.xml"));
			monitor.worked(1);
			
			tomcatUsersDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(new FileInputStream(path.append("tomcat-users.xml").toFile())));
			monitor.worked(1);
	
			// load policy file
			policyFile = TomcatVersionHelper.getFileContents(new FileInputStream(path.append("tomcat.policy").toFile()));
			monitor.worked(1);
	
			if (monitor.isCanceled())
				return;
	
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load Tomcat v3.2 configuration from " + path.toOSString() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, path.toOSString()), e));
		}
	}
	
	/**
	 * @see TomcatConfiguration#importFromPath(IPath, boolean, IProgressMonitor)
	 */
	public void importFromPath(IPath path, boolean isTestEnv, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
		
		// for test environment, remove existing contexts since an instance
		// directory relative to server.xml will be used
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
			monitor.beginTask(Messages.loadingTask, 800);
	
			// check for tomcat.policy to verify that this is a v3.2 config
			IFile file = folder.getFile("tomcat.policy");
			if (!file.exists())
				throw new CoreException(new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), null));
	
			// load server.xml
			file = folder.getFile("server.xml");
			InputStream in = file.getContents();
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server32");
			server = (Server) serverFactory.loadDocument(in);
			serverInstance = new ServerInstance(server);
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
	
			// load tomcat.policy
			file = folder.getFile("tomcat.policy");
			in = file.getContents();
			policyFile = TomcatVersionHelper.getFileContents(in);
			monitor.worked(200);
	
			if (monitor.isCanceled())
				throw new Exception("Cancelled");
	
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load Tomcat v3.2 configuration from: " + folder.getFullPath() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotLoadConfiguration, folder.getFullPath().toOSString()), e));
		}
	}
	
	/**
	 * Save the information held by this object to the given directory.
	 * 
	 * @param path a path
	 * @param forceDirty if true, the files will be saved, regardless
	 *  of whether they have been modified
	 * @param monitor a progress monitor
	 * @throws CoreException
	 */
	protected void save(IPath path, boolean forceDirty, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.savingTask, 5);
			
			// make sure directory exists
			if (!path.toFile().exists()) {
				forceDirty = true;
				path.toFile().mkdir();
			}
			monitor.worked(1);
			
			// save files
			if (forceDirty || isServerDirty)
				serverFactory.save(path.append("server.xml").toOSString());
			monitor.worked(1);
			
			webAppDocument.save(path.append("web.xml").toOSString(), forceDirty);
			monitor.worked(1);
			
			if (forceDirty)
				XMLUtil.save(path.append("tomcat-users.xml").toOSString(), tomcatUsersDocument);
			monitor.worked(1);
			
			if (forceDirty) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(path.append("tomcat.policy").toFile()));
				bw.write(policyFile);
				bw.close();
			}
			monitor.worked(1);
			isServerDirty = false;
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v3.2 configuration to " + path, e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCouldNotSaveConfiguration, new String[] {e.getLocalizedMessage()}), e));
		}
	}

	/**
	 * Save the information held by this object to the given directory.
	 * All files are forced to be saved.
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
			
			if (!folder.exists())
				folder.create(true, true, ProgressUtil.getSubMonitorFor(monitor, 100));
			else
				monitor.worked(100);
			
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
			file = folder.getFile("web.xml");
			webAppDocument.save(file, ProgressUtil.getSubMonitorFor(monitor, 200));
			
			// save tomcat-users.xml
			data = XMLUtil.getContents(tomcatUsersDocument);
			in = new ByteArrayInputStream(data);
			file = folder.getFile("tomcat-users.xml");
			if (file.exists())
				monitor.worked(200);
				//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			
			// save tomcat.policy
			in = new ByteArrayInputStream(policyFile.getBytes());
			file = folder.getFile("tomcat.policy");
			if (file.exists())
				monitor.worked(200);
				//file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save Tomcat v3.2 configuration to " + folder.getFullPath(), e);
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

			context.setPath(module.getPath());
			context.setDocBase(module.getDocumentBase());
			context.setReloadable(module.isReloadable() ? "true" : "false");
			if (module.getMemento() != null && module.getMemento().length() > 0)
				context.setSource(module.getMemento());
			isServerDirty = true;
			firePropertyChangeEvent(ADD_WEB_MODULE_PROPERTY, null, module);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error adding web module", e);
		}
	}

	/**
	 * Localize the web projects in this configuration.
	 *
	 * @param baseDir runtime base directory for the server
	 * @param deployDir deployment directory for the server
	 * @param server2 a server type
	 * @param monitor a progress monitor
	 * @return result of operation
	 */
	public IStatus localizeConfiguration(IPath baseDir, IPath deployDir, TomcatServer server2, IProgressMonitor monitor) {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.updatingConfigurationTask, 100);
			IPath confDir = baseDir.append("conf");
			
			Tomcat32Configuration config = new Tomcat32Configuration(null);
			config.load(confDir, ProgressUtil.getSubMonitorFor(monitor, 300));
			
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			if (server2.isTestEnvironment()) {
				config.server.getContextManager().setHome(baseDir.toOSString());
				config.isServerDirty = true;
			}

			// Only add root module if running in a test env (i.e. not on the installation)
			boolean addRootWebapp = server2.isTestEnvironment();
			
			// If not deploying to "webapps", context docBase attributes need updating
			boolean deployingToWebapps = "webapps".equals(server2.getDeployDirectory());
			
			Map<String, String> pathMap = new HashMap<String, String>();
			
			MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, 
					NLS.bind(Messages.errorPublishServer, server2.getServer().getName()), null);
			Context [] contexts = config.serverInstance.getContexts();
			if (contexts != null) {
				for (int i = 0; i < contexts.length; i++) {
					Context context = contexts[i];

					// Normalize path and check for duplicates
					String path = context.getPath();
					if (path != null) {
						// Save a copy of original in case it's "/"
						String origPath = path;
						// Normalize "/" to ""
						if ("/".equals(path)) {
							if (Trace.isTraceEnabled())
								Trace.trace(Trace.FINER, "Context path is being changed from \"/\" to \"\".");
							path = "";
							context.setPath(path);
							config.isServerDirty = true;
						}

						// Context paths that are the same or differ only in case are not allowed
						String lcPath = path.toLowerCase();
						if (!pathMap.containsKey(lcPath)) {
							pathMap.put(lcPath, origPath);
						}
						else {
							String otherPath = pathMap.get(lcPath);
							IStatus s = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID,
									origPath.equals(otherPath) ? NLS.bind(Messages.errorPublishPathDup, origPath) 
											: NLS.bind(Messages.errorPublishPathConflict, origPath, otherPath));
							ms.add(s);
						}
					}
					else {
						IStatus s = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID,
								Messages.errorPublishPathMissing);
						ms.add(s);
					}

					// If default webapp has not been found, check this one
					// TODO Need to add a root context if deploying to webapps but with auto-deploy off
					if (addRootWebapp && "".equals(context.getPath())) {
						// A default webapp is being deployed, don't add one
						addRootWebapp = false;
					}

					// If not deploying to "webapps", convert to absolute path under deploy dir
					if (!deployingToWebapps) {
						String source = context.getSource();
						if (source != null && source.length() > 0 )	{
							String name = context.getDocBase();
							// Update docBase only if name begins with the expected prefix
							if (name.startsWith(getDocBasePrefix())) {
								name = name.substring(getDocBasePrefix().length());
								context.setDocBase(deployDir.append(name).toOSString());
								config.isServerDirty = true;
							}
						}
					}
				}
			}
			// If errors are present, return status
			if (!ms.isOK())
				return ms;

			if (addRootWebapp) {
				// Add a context for the default webapp
				Context rootContext = config.serverInstance.createContext(0);
				rootContext.setPath("");
				rootContext.setDocBase(deployDir.append("ROOT").toOSString());
				rootContext.setReloadable("false");
				config.isServerDirty = true;
			}
			monitor.worked(100);

			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			config.save(confDir, false, ProgressUtil.getSubMonitorFor(monitor, 30));
			monitor.worked(100);
			
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error localizing configuration", e);
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
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
			int con = Integer.parseInt(id);
			Connector connector = serverInstance.getConnector(con);
	
			int size = connector.getParameterCount();
			for (int i = 0; i < size; i++) {
				Parameter p = connector.getParameter(i);
				if ("port".equals(p.getName())) {
					p.setValue(port + "");
					isServerDirty = true;
					firePropertyChangeEvent(MODIFY_PORT_PROPERTY, id, new Integer(port));
					return;
				}
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
			context.setPath(path);
			context.setDocBase(docBase);
			context.setReloadable(reloadable ? "true" : "false");
			isServerDirty = true;
			WebModule module = new WebModule(path, docBase, null, reloadable);
			firePropertyChangeEvent(MODIFY_WEB_MODULE_PROPERTY, new Integer(index), module);
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
			Trace.trace(Trace.SEVERE, "Error removing web module " + index, e);
		}
	}

	protected IStatus cleanupServer(IPath confDir, IPath installDir, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, Messages.cleanupServerTask, null);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(Messages.cleanupServerTask, 200);

		try {
			monitor.subTask(Messages.detectingRemovedProjects);

			// Try to read old server configuration
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server32");
			File serverFile = confDir.append("conf").append("server.xml").toFile();
			if (serverFile.exists()) {
				Server oldServer = (Server) factory.loadDocument(new FileInputStream(serverFile));
				ServerInstance oldInstance = new ServerInstance(oldServer);
				
				// Collect paths of old web modules managed by WTP
				Set<String> oldPaths = new HashSet<String>();
				Context [] contexts = oldInstance.getContexts();
				if (contexts != null) {
					for (int i = 0; i < contexts.length; i++) {
						String source = contexts[i].getSource();
						if (source != null && source.length() > 0 )	{
							oldPaths.add(contexts[i].getPath());
						}
					}
				}

				// Remove paths for web modules that are staying around
				List modules = getWebModules();
				int size = modules.size();
				for (int i = 0; i < size; i++) {
					WebModule module = (WebModule) modules.get(i);
					oldPaths.remove(module.getPath());
				}
				monitor.worked(100);

				// Delete work directories for managed web modules that have gone away
				if (oldPaths.size() > 0 ) {
					IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 100);
					subMonitor.beginTask(Messages.deletingContextFilesTask, oldPaths.size() * 100);
					
					Iterator iter = oldPaths.iterator();
					while (iter.hasNext()) {
						String oldPath = (String)iter.next();
						
						// Delete work directory associated with the removed context if it is within confDir.
						// If it is outside of confDir, assume user is going to manage it.
						Context ctx = oldInstance.getContext(oldPath);
						IPath ctxWorkPath = oldInstance.getContextWorkDirectory(confDir, ctx);
						if (confDir.isPrefixOf(ctxWorkPath)) {
							File ctxWorkDir = ctxWorkPath.toFile();
							if (ctxWorkDir.exists() && ctxWorkDir.isDirectory()) {
								IStatus [] results = PublishHelper.deleteDirectory(ctxWorkDir, ProgressUtil.getSubMonitorFor(monitor, 100));
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
			// Else no server.xml.  Assume first publish to new temp directory
			else {
				monitor.worked(200);
			}
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Server cleaned");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not cleanup server at " + confDir.toOSString() + ": " + e.getMessage());
			ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
					NLS.bind(Messages.errorCleanupServer, new String[] {e.getLocalizedMessage()}), e));
		}
		
		monitor.done();
		return ms;
	}
	
}
