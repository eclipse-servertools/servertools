package org.eclipse.jst.server.tomcat.internal.core;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.eclipse.jst.server.tomcat.core.WebModule;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Connector;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Engine;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Host;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Listener;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import org.eclipse.wst.server.core.model.IServerPort;
import org.eclipse.wst.server.core.util.ProgressUtil;
import org.eclipse.wst.server.core.util.ServerPort;
/**
 * Tomcat v4.1 server configuration.
 */
public class Tomcat41Configuration extends TomcatConfiguration {
	protected static final String DEFAULT_SERVICE = "Tomcat-Standalone";
	protected static final String HTTP_CONNECTOR = "org.apache.coyote.tomcat4.CoyoteConnector";
	protected static final String SSL_SOCKET_FACTORY = "org.apache.coyote.tomcat4.CoyoteServerSocketFactory";
	//protected static final String TEST_CONNECTOR = "org.apache.catalina.connector.test.HttpConnector";
	//org.apache.ajp.tomcat4.Ajp13Connector
	protected static final String APACHE_CONNECTOR = "org.apache.catalina.connector.warp.WarpConnector";

	protected Server server;
	protected Factory serverFactory;
	protected boolean isServerDirty;

	protected WebAppDocument webAppDocument;

	protected Document tomcatUsersDocument;

	protected String policyFile;
	protected boolean isPolicyDirty;

	/**
	 * Tomcat41Configuration constructor comment.
	 */
	public Tomcat41Configuration() {
		super();
	}
	
	/**
	 * Returns the root of the docbase parameter.
	 *
	 * @return java.lang.String
	 */
	protected String getDocBaseRoot() {
		return "webapps/";
	}

	/**
	 * Return the port number.
	 * @return int
	 */
	public IServerPort getMainPort() {
		Iterator iterator = getServerPorts().iterator();
		while (iterator.hasNext()) {
			IServerPort port = (IServerPort) iterator.next();
			if (port.getName().equals("HTTP Connector"))
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
	 * Returns the prefix that is used in front of the
	 * web module path property. (e.g. "webapps")
	 *
	 * @return java.lang.String
	 */
	public String getPathPrefix() {
		return "";
	}
	
	/**
	 * Return the docBase of the ROOT web module.
	 *
	 * @return java.lang.String
	 */
	protected String getROOTModuleDocBase() {
		return "ROOT";
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
			ports.add(new ServerPort("server", "Server port", port, "TCPIP"));
		} catch (Exception e) { }
	
		// add connectors
		try {
			int size = server.getServiceCount();
			for (int i = 0; i < size; i++) {
				Service service = server.getService(i);
				int size2 = service.getConnectorCount();
				for (int j = 0; j < size2; j++) {
					Connector connector = service.getConnector(j);
					String className = connector.getClassName();
					String name = className;
					String protocol = "TCPIP";
					boolean advanced = true;
					String[] contentTypes = null;
					int port = -1;
					try {
						port = Integer.parseInt(connector.getPort());
					} catch (Exception e) { }
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
						} catch (Exception e) { }
						if ("HTTP".equals(protocol))
							advanced = false;
					} else if (APACHE_CONNECTOR.equals(className))
						name = "Apache Connector";
					if (className != null && className.length() > 0)
						ports.add(new ServerPort(i + "/" + j, name, port, protocol, contentTypes, advanced));
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
			int size = server.getServiceCount();
			for (int i = 0; i < size; i++) {
				Service service = server.getService(i);
				if (service.getName().equalsIgnoreCase(DEFAULT_SERVICE)) {
					Engine engine = service.getEngine();
					Host host = engine.getHost();
					int size2 = host.getContextCount();
					for (int j = 0; j < size2; j++) {
						Context context = host.getContext(j);
						String reload = context.getReloadable();
						if (reload == null)
							reload = "false";
						WebModule module = new WebModule(context.getPath(), 
							context.getDocBase(), context.getSource(),
							reload.equalsIgnoreCase("true") ? true : false);
						list.add(module);
					}
				}
			}
		} catch (Exception e) {
			Trace.trace("Error getting project refs", e);
		}
		return list;
	}
	
	/**
	 *
	 * @return org.eclipse.jst.server.tomcat.internal.Tomcat40Configuration
	 */
	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(TomcatPlugin.getResource("%loadingTask"), 5);
			
			// check for catalina.policy to verify that this is a v4.0 config
			InputStream in = new FileInputStream(path.append("catalina.policy").toFile());
			in.read();
			in.close();
			monitor.worked(1);

			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			server = (Server) serverFactory.loadDocument(new FileInputStream(path.append("server.xml").toFile()));
			if (!TomcatConfigurationUtil.verifyConfiguration(this, TomcatConfigurationUtil.CONFIGURATION_V41))
				throw new CoreException(null);
			monitor.worked(1);

			webAppDocument = new WebAppDocument(path.append("web.xml"));
			monitor.worked(1);
	
			tomcatUsersDocument = XMLUtil.getDocumentBuilder().parse(new InputSource(new FileInputStream(path.append("tomcat-users.xml").toFile())));
			monitor.worked(1);
		
			// load policy file
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(path.append("catalina.policy").toFile())));
				String temp = br.readLine();
				policyFile = "";
				while (temp != null) {
					policyFile += temp + "\n";
					temp = br.readLine();
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not load policy file", e);
			} finally {
				if (br != null)
					br.close();
			}
			monitor.worked(1);
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load Tomcat v4.0 configuration from " + path.toOSString() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorCouldNotLoadConfiguration"), e));
		}
	}

	/**
	 * Reload the configuration.
	 */
	public void load(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(TomcatPlugin.getResource("%loadingTask"), 800);
	
			// check for catalina.policy to verify that this is a v4.0 config
			IFile file = folder.getFile("catalina.policy");
			if (!file.exists())
				throw new CoreException(new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorCouldNotLoadConfiguration"), null));
	
			// load server.xml
			file = folder.getFile("server.xml");
			InputStream in = file.getContents();
			serverFactory = new Factory();
			serverFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			server = (Server) serverFactory.loadDocument(in);
			if (!TomcatConfigurationUtil.verifyConfiguration(this, TomcatConfigurationUtil.CONFIGURATION_V41))
				throw new Exception("Not a Tomcat v4.1 configuration");
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
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				String temp = br.readLine();
				policyFile = "";
				while (temp != null) {
					policyFile += temp + "\n";
					temp = br.readLine();
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not load policy file", e);
			} finally {
				if (br != null)
					br.close();
			}
			monitor.worked(200);
	
			if (monitor.isCanceled())
				throw new Exception("Cancelled");
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not reload Tomcat v4.1 configuration from: " + folder.getFullPath() + ": " + e.getMessage());
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorCouldNotLoadConfiguration"), e));
		}
	}

	/**
	 * Save to the given directory.
	 * @param dir java.io.File
	 * @param forceDirty boolean
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @exception java.io.IOException
	 */
	protected void save(IPath path, boolean forceDirty, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(TomcatPlugin.getResource("%savingTask"), 3);
	
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
	
			//if (forceDirty || isWebAppDirty)
			//	webAppFactory.save(dirPath + "web.xml");
			//webAppDocument.save(path.toOSString(), forceDirty || isPolicyDirty);
			webAppDocument.save(path.append("web.xml").toOSString(), forceDirty);
			monitor.worked(1);
	
			if (forceDirty)
				XMLUtil.save(path.append("tomcat-users.xml").toOSString(), tomcatUsersDocument);
			monitor.worked(1);
	
			if (forceDirty || isPolicyDirty) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(path.append("catalina.policy").toFile()));
				bw.write(policyFile);
				bw.close();
			}
			monitor.worked(1);
			isServerDirty = false;
			isPolicyDirty = false;
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Could not save Tomcat v4.1 configuration to " + path, e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorCouldNotSaveConfiguration", new String[] {e.getLocalizedMessage()}), e));
		}
	}
	
	public void save(IPath path, IProgressMonitor monitor) throws CoreException {
		save(path, true, monitor);
	}

	/**
	 * Save the information held by this object to the given directory.
	 *
	 * @param dir
	 * @param org.eclipse.core.runtime.IProgressMonitor monitor
	 * @throws java.io.IOException
	 */
	public void save(IFolder folder, IProgressMonitor monitor) throws CoreException {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(TomcatPlugin.getResource("%savingTask"), 900);
	
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
			Trace.trace("Could not save Tomcat v4.1 configuration to " + folder.toString(), e);
			throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorCouldNotSaveConfiguration", new String[] {e.getLocalizedMessage()}), e));
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
}
