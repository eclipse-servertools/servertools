/**********************************************************************
 * Copyright (c) 2007, 2020 SAS Institute, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    SAS Institute, Inc - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.wst.ModuleTraverser;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Host;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.util.PublishHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utility class for methods that are used by more that one version
 * of Tomcat.  Use of these methods makes it clear that more than
 * one version will be impacted by changes.
 *
 */
public class TomcatVersionHelper {

	/**
	 * Sting containing contents for a default web.xml for Servlet 2.2.
	 */
	public static final String DEFAULT_WEBXML_SERVLET22 = 
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		"<!DOCTYPE web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN\" \"http://java.sun.com/j2ee/dtds/web-app_2_2.dtd\">\n" +
		"<web-app>\n</web-app>";

	/**
	 * Default web.xml contents for a Servlet 2.3 web application.
	 */
	public static final String DEFAULT_WEBXML_SERVLET23 = 
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		"<!DOCTYPE web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\" \"http://java.sun.com/dtd/web-app_2_3.dtd\">\n" +
		"<web-app>\n</web-app>";

	/**
	 * Default web.xml contents for a Servlet 2.4 web application.
	 */
	public static final String DEFAULT_WEBXML_SERVLET24 = 
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		"<web-app xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\" version=\"2.4\">\n" +
		"</web-app>";

	/**
	 * Default web.xml contents for a Servlet 2.5 web application.
	 */
	public static final String DEFAULT_WEBXML_SERVLET25 =
		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		"<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\" version=\"2.5\">\n" +
		"</web-app>";

	/**
	 * Map of server type ID to expected version string fragment for version checking.
	 */
	private static final Map<String, String> versionStringMap = new HashMap<String, String>();
	
	static {
		versionStringMap.put(TomcatPlugin.TOMCAT_41, "4.1.");
		versionStringMap.put(TomcatPlugin.TOMCAT_50, "5.0.");
		versionStringMap.put(TomcatPlugin.TOMCAT_55, "5.5.");
		versionStringMap.put(TomcatPlugin.TOMCAT_60, "6.0.");
		versionStringMap.put(TomcatPlugin.TOMCAT_70, "7.0.");
		versionStringMap.put(TomcatPlugin.TOMCAT_80, "8.0.");
		versionStringMap.put(TomcatPlugin.TOMCAT_85, "8.5.");
		versionStringMap.put(TomcatPlugin.TOMCAT_90, "9.0.");
		versionStringMap.put(TomcatPlugin.TOMCAT_100, "10.0.");
	}

	/**
	 * Reads the from the specified InputStream and returns
	 * the result as a String. Each line is terminated by
	 * &quot;\n&quot;.  Returns whatever is read regardless
	 * of any errors that occurs while reading.
	 * 
	 * @param stream InputStream for the contents to be read
	 * @return contents read
	 * @throws IOException if error occurs closing the stream
	 */
	public static String getFileContents(InputStream stream) throws IOException {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(stream));
			String temp = br.readLine();
			while (temp != null) {
				sb.append(temp).append("\n");
				temp = br.readLine();
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load file contents.", e);
		} finally {
			if (br != null)
				br.close();
		}
		return sb.toString();
	}
	
	/**
	 * Gets the base directory for this server. This directory
	 * is used as the "base" property for the server.
	 * 
	 * @param ts TomcatServer from which to derive the base directory 
	 * directory.  Only used to get the temp directory if needed.
	 * @return path to base directory
	 */
	public static IPath getStandardBaseDirectory(TomcatServer ts) {
		if (ts.isTestEnvironment()) {
			String baseDir = ts.getInstanceDirectory();
			// If test mode and no instance directory specified, use temporary directory
			if (baseDir == null) {
				TomcatServerBehaviour tsb = (TomcatServerBehaviour)ts.getServer().loadAdapter(TomcatServerBehaviour.class, null);
				return tsb.getTempDirectory();
			}
			IPath path = new Path(baseDir);
			if (!path.isAbsolute()) {
				IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
				path = rootPath.append(path);
			}
			// Return specified instance directory
			return path;
		}
		// Return runtime path
		return ts.getServer().getRuntime().getLocation();
	}

	/**
	 * Gets the startup VM arguments for the Catalina server.
	 * 
	 * @param installPath installation path for the server
	 * @param instancePath instance path for the server
	 * @param deployPath deploy path for the server
	 * @param isTestEnv test environment flag
	 * @return array of strings containing VM arguments
	 */
	public static String[] getCatalinaVMArguments(IPath installPath, IPath instancePath, IPath deployPath, boolean isTestEnv) {
		List<String> list = new ArrayList<String>();
		if (isTestEnv)
			list.add("-Dcatalina.base=\"" + instancePath.toOSString() + "\"");
		else 
			list.add("-Dcatalina.base=\"" + installPath.toOSString() + "\"");
		list.add("-Dcatalina.home=\"" + installPath.toOSString() + "\"");
		// Include a system property for the configurable deploy location
		list.add("-Dwtp.deploy=\"" + deployPath.toOSString() + "\"");
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}

	
	/**
	 * Gets a ServerInstance for the specified server.xml, Service name,
	 * and Host name.  Returns null if server.xml does not exist
	 * or an error occurs.
	 * 
	 * @param serverXml path to previously published server.xml 
	 * @param serviceName name of Service to be used by this instance or null
	 * @param hostName name of Host to be used by this instance or null
	 * @return ServerInstance for specified server.xml using specified
	 * Service and Host names.  null if server.xml does not exist.
	 * @throws FileNotFoundException should not occur since existence is tested
	 * @throws IOException if there is an error reading server.xml
	 * @throws SAXException if there is a syntax error in server.xml
	 */
	public static ServerInstance getCatalinaServerInstance(IPath serverXml, String serviceName, String hostName) throws FileNotFoundException, IOException, SAXException {
		ServerInstance serverInstance = null;
		Factory factory = new Factory();
		factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
		File serverFile = serverXml.toFile();
		if (serverFile.exists()) {
			Server server = (Server) factory.loadDocument(new FileInputStream(serverFile));
			serverInstance = new ServerInstance(server, serviceName, hostName);
			
			IPath contextPath = serverInstance.getContextXmlDirectory(serverXml.removeLastSegments(1));
			File contextDir = contextPath.toFile();
			if (contextDir.exists()) {
				Map<File, Context> projectContexts = new HashMap<File, Context>();
				loadSeparateContextFiles(contextPath.toFile(), factory, projectContexts);
				
				// add any separately saved contexts
				Host host = serverInstance.getHost();
				Collection contexts = projectContexts.values();
				Iterator iter = contexts.iterator();
				while (iter.hasNext()) {
					Context context = (Context)iter.next();
					host.importNode(context.getElementNode(), true);
				}
				// TODO Add handling for non-project contexts when there removal can be addressed  
			}
		}
		return serverInstance;
	}

	/**
	 * Gets the paths for Contexts that are being removed in the
	 * next server publish. Reads the old server.xml to determine
	 * what Contexts were previously servered and returns those
	 * that are not included in the specified list of modules.
	 * 
	 * @param oldServerInstance for server.xml from previous server publish
	 * @param modules list of currently added modules
	 * @param removedContextsMap Map to receive removed contexts mapped by path
	 * @param keptContextsMap Map to receive kept contexts mapped by path
	 */
	public static void getRemovedKeptCatalinaContexts(ServerInstance oldServerInstance,
			List modules, Map<String, Context> removedContextsMap, Map<String, Context> keptContextsMap) {
		// Collect paths of old web modules managed by WTP
		Context [] contexts = oldServerInstance.getContexts();
		if (contexts != null) {
			for (int i = 0; i < contexts.length; i++) {
				String source = contexts[i].getSource();
				if (source != null && source.length() > 0 )	{
					removedContextsMap.put(contexts[i].getPath(), contexts[i]);
				}
			}
		}

		// Remove paths for web modules that are staying around
		int size = modules.size();
		for (int i = 0; i < size; i++) {
			WebModule module = (WebModule) modules.get(i);

			String modulePath = module.getPath();
			// normalize "/" to ""
			if (modulePath.equals("/"))
				modulePath = "";

			Context context = removedContextsMap.remove(modulePath);
			if (context != null)
				keptContextsMap.put(context.getPath(), context);
		}
	}
	
	/**
	 * Cleanup server instance location in preparation for next server publish.
	 * This currently involves deleting work directories for currently
	 * existing Contexts which will not be included in the next publish.
	 * In addition, Context XML files which may have been created for these
	 * Contexts are also deleted. If requested, Context XML files for
	 * kept Contexts will be deleted since they will be kept in server.xml.
	 * 
	 * @param baseDir path to server instance directory, i.e. catalina.base
	 * @param installDir path to server installation directory (not currently used)
	 * @param removeKeptContextFiles true if kept contexts should have a separate
	 *  context XML file removed 
	 * @param modules list of currently added modules
	 * @param monitor a progress monitor or null
	 * @return MultiStatus containing results of the cleanup operation
	 */
	public static IStatus cleanupCatalinaServer(IPath baseDir, IPath installDir, boolean removeKeptContextFiles, List modules, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, Messages.cleanupServerTask, null);
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.cleanupServerTask, 200);
			monitor.subTask(Messages.detectingRemovedProjects);

			IPath serverXml = baseDir.append("conf").append("server.xml");
			ServerInstance oldInstance = TomcatVersionHelper.getCatalinaServerInstance(serverXml, null, null);
			if (oldInstance != null) {
				Map<String, Context> removedContextsMap = new HashMap<String, Context>();
				Map<String, Context> keptContextsMap = new HashMap<String, Context>();
				TomcatVersionHelper.getRemovedKeptCatalinaContexts(oldInstance, modules, removedContextsMap, keptContextsMap);
				monitor.worked(100);
				if (removedContextsMap.size() > 0) {
					// Delete context files and work directories for managed web modules that have gone away
					IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 100);
					subMonitor.beginTask(Messages.deletingContextFilesTask, removedContextsMap.size() * 200);
					
					Iterator iter = removedContextsMap.keySet().iterator();
					while (iter.hasNext()) {
						String oldPath = (String)iter.next();
						Context ctx = removedContextsMap.get(oldPath);
						
						// Delete the corresponding context file, if it exists
						IPath ctxFilePath = oldInstance.getContextFilePath(baseDir, ctx);
						if (ctxFilePath != null) {
							File ctxFile = ctxFilePath.toFile();
							if (ctxFile.exists()) {
								subMonitor.subTask(NLS.bind(Messages.deletingContextFile, ctxFile.getName()));
								if (ctxFile.delete()) {
									if (Trace.isTraceEnabled())
										Trace.trace(Trace.FINER, "Leftover context file " + ctxFile.getName() + " deleted.");
									ms.add(new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0,
											NLS.bind(Messages.deletedContextFile, ctxFile.getName()), null));
								} else {
									Trace.trace(Trace.SEVERE, "Could not delete obsolete context file " + ctxFilePath.toOSString());
									ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
											NLS.bind(Messages.errorCouldNotDeleteContextFile, ctxFilePath.toOSString()), null));
								}
							}
						}
						subMonitor.worked(100);
						
						// Delete work directory associated with the removed context if it is within confDir.
						// If it is outside of confDir, assume user is going to manage it.
						IPath ctxWorkPath = oldInstance.getContextWorkDirectory(baseDir, ctx);
						if (baseDir.isPrefixOf(ctxWorkPath)) {
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
								subMonitor.worked(100);
						}
						else
							subMonitor.worked(100);
					}
					subMonitor.done();
				}
				monitor.worked(100);
				
				// If requested, remove any separate context XML files for contexts being kept
				if (removeKeptContextFiles && keptContextsMap.size() > 0) {
					// Delete context files and work directories for managed web modules that have gone away
					IProgressMonitor subMonitor = ProgressUtil.getSubMonitorFor(monitor, 100);
					// TODO Improve task name
					subMonitor.beginTask(Messages.deletingContextFilesTask, keptContextsMap.size() * 100);
					
					Iterator iter = keptContextsMap.keySet().iterator();
					while (iter.hasNext()) {
						String keptPath = (String)iter.next();
						Context ctx = keptContextsMap.get(keptPath);
						
						// Delete the corresponding context file, if it exists
						IPath ctxFilePath = oldInstance.getContextFilePath(baseDir, ctx);
						if (ctxFilePath != null) {
							File ctxFile = ctxFilePath.toFile();
							if (ctxFile.exists()) {
								subMonitor.subTask(NLS.bind(Messages.deletingContextFile, ctxFile.getName()));
								if (ctxFile.delete()) {
									if (Trace.isTraceEnabled())
										Trace.trace(Trace.FINER, "Leftover context file " + ctxFile.getName() + " deleted.");
									ms.add(new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0,
											NLS.bind(Messages.deletedContextFile, ctxFile.getName()), null));
								} else {
									Trace.trace(Trace.SEVERE, "Could not delete obsolete context file " + ctxFilePath.toOSString());
									ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
											NLS.bind(Messages.errorCouldNotDeleteContextFile, ctxFilePath.toOSString()), null));
								}
							}
						}
						subMonitor.worked(100);
					}
					subMonitor.done();
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
		finally {
			monitor.done();
		}
		
		return ms;
	}

	/**
	 * Creates a Catalina instance directory at the specified
	 * path.  This involves creating the set of subdirectories
	 * uses by a Catalina instance.
	 * 
	 * @param baseDir directory at which to create Catalina instance
	 * directories.
	 * @return result status of the operation
	 */
	public static IStatus createCatalinaInstanceDirectory(IPath baseDir) {
		if (Trace.isTraceEnabled())
			Trace.trace(Trace.FINER, "Creating runtime directory at " + baseDir.toOSString());
		// TODO Add more error handling.
		// Prepare a catalina.base directory structure
		File temp = baseDir.append("conf").toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = baseDir.append("logs").toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = baseDir.append("temp").toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = baseDir.append("webapps").toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = baseDir.append("work").toFile();
		if (!temp.exists())
			temp.mkdirs();

		return Status.OK_STATUS;		
	}
	
	/**
	 * Creates the specified deployment directory if it does not already exist.
	 * It will include a default ROOT web application using the specified web.xml.
	 * 
	 * @param deployDir path to deployment directory to create
	 * @param webxml web.xml context to use for the ROOT web application.
	 * @return result status of the operation
	 */
	public static IStatus createDeploymentDirectory(IPath deployDir, String webxml) {
		if (Trace.isTraceEnabled())
			Trace.trace(Trace.FINER, "Creating deployment directory at " + deployDir.toOSString());

		// TODO Add more error handling.
		File temp = deployDir.toFile();
		if (!temp.exists())
			temp.mkdirs();

		IPath tempPath = deployDir.append("ROOT/WEB-INF");
		temp = tempPath.toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = tempPath.append("web.xml").toFile();
		if (!temp.exists()) {
			FileWriter fw;
			try {
				fw = new FileWriter(temp);
				fw.write(webxml);
				fw.close();
			} catch (IOException e) {
				Trace.trace(Trace.WARNING, "Unable to create web.xml for ROOT context.", e);
			}
		}
		
		return Status.OK_STATUS;		
	}

	/**
	 * Add context configuration found in META-INF/context.xml files
	 * present in projects to published server.xml.  Used by
	 * Tomcat 4.1, 5.0, and 5.5 which support use of META-INF/context.xml
	 * in some form.
	 * 
	 * @param baseDir absolute path to catalina instance directory
	 * @param webappsDir absolute path to deployment directory
	 * @param monitor a progress monitor or null
	 * @return result of operation
	 */
	public static IStatus publishCatalinaContextConfig(IPath baseDir, IPath webappsDir, IProgressMonitor monitor) {
		if (Trace.isTraceEnabled())
			Trace.trace(Trace.FINER, "Apply context configurations");
		IPath confDir = baseDir.append("conf");
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.publishConfigurationTask, 300);

			monitor.subTask(Messages.publishContextConfigTask);
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			Server publishedServer = (Server) factory.loadDocument(new FileInputStream(confDir.append("server.xml").toFile()));
			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);
			monitor.worked(100);
			
			boolean modified = false;

			MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, Messages.publishContextConfigTask, null);
			Context [] contexts = publishedInstance.getContexts();
			if (contexts != null) {
				for (int i = 0; i < contexts.length; i++) {
					Context context = contexts[i];
					monitor.subTask(NLS.bind(Messages.checkingContextTask,
							new String[] {context.getPath()}));
					if (addCatalinaContextConfig(webappsDir, context, ms)) {
						modified = true;
					}
				}
			}
			monitor.worked(100);
			if (modified) {
				monitor.subTask(Messages.savingContextConfigTask);
				factory.save(confDir.append("server.xml").toOSString());
			}
			
			// If problem(s) occurred adding context configurations, return error status
			if (ms.getChildren().length > 0) {
				return ms;
			}
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Server.xml updated with context.xml configurations");
			return Status.OK_STATUS;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not apply context configurations to published Tomcat configuration from " + confDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
			monitor.done();
		}
	}

	/**
	 * If the specified Context is linked to a project, try to
	 * update it with any configuration from a META-INF/context.xml found
	 * relative to the specified web applications directory and context docBase.
	 * 
	 * @param webappsDir Path to server's web applications directory.
	 * @param context Context object to receive context.xml contents.
	 * @param ms MultiStatus object to receive error status.
	 * @return Returns true if context is modified.
	 */
	private static boolean addCatalinaContextConfig(IPath webappsDir, Context context, MultiStatus ms) {
		boolean modified = false;
		String source = context.getSource();
		if (source != null && source.length() > 0 )
		{
			File docBase = new File(context.getDocBase());
			if (!docBase.isAbsolute())
				docBase = new File(webappsDir.toOSString(), docBase.getPath());
			try {
				Context contextConfig = loadCatalinaContextConfig(docBase);
				if (null != contextConfig) {
					if (context.hasChildNodes())
						context.removeChildren();
					contextConfig.copyChildrenTo(context);
					Map attrs = contextConfig.getAttributes();
					Iterator iter = attrs.keySet().iterator();
					while (iter.hasNext()) {
						String name = (String) iter.next();
						if (!name.equalsIgnoreCase("path")
								&& !name.equalsIgnoreCase("docBase")
								&& !name.equalsIgnoreCase("source")) {
							String value = (String)attrs.get(name);
							context.setAttributeValue(name, value);
						}
					}
					modified = true;
				}
			} catch (Exception e) {
				String contextPath = context.getPath();
				if (contextPath.startsWith("/")) {
					contextPath = contextPath.substring(1);
				}
				Trace.trace(Trace.SEVERE, "Error reading context.xml file for " + contextPath, e);
				IStatus s = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
						NLS.bind(Messages.errorCouldNotLoadContextXml, contextPath), e);
				ms.add(s);
			}
		}
		return modified;
	}
	
	/**
	 * Tries to read a META-INF/context.xml file relative to the
	 * specified web application path.  If found, it creates a Context object
	 * containing the contexts of that file.
	 * 
	 * @param docBase File with absolute path to the web application
	 * @return Context element created from context.xml, or null if not found.
	 * @throws SAXException If there is a error parsing the XML. 
	 * @throws IOException If there is an error reading the file.
	 */
	private static Context loadCatalinaContextConfig(File docBase) throws IOException, SAXException {
		File contextXML = new File(docBase, "META-INF" + File.separator + "context.xml");
		if (contextXML.exists()) {
			try {
				InputStream is = new FileInputStream(contextXML);
				Factory ctxFactory = new Factory();
				ctxFactory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
				Context ctx = (Context)ctxFactory.loadDocument(is);
				is.close();
				return ctx;
			} catch (FileNotFoundException e) {
				// Ignore, should never occur
			}
		}
		return null;
 	}
	
	/**
	 * If modules are not being deployed to the "webapps" directory, the
	 * context for the published modules is updated to contain the
	 * corrected docBase.
	 * 
	 * @param baseDir runtime base directory for the server
	 * @param deployDir deployment directory for the server
	 * @param server server being localized
	 * @param monitor a progress monitor
	 * @return result of operation
	 */
	public static IStatus localizeConfiguration(IPath baseDir, IPath deployDir, TomcatServer server, IProgressMonitor monitor) {
		try {
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Localizing configuration at " + baseDir);
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.publishConfigurationTask, 300);

			IPath serverXml = baseDir.append("conf/server.xml");
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			Server publishedServer = (Server)factory.loadDocument(
					new FileInputStream(serverXml.toFile()));
			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);
			monitor.worked(100);

			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			boolean modified = false;

			// Only add root module if running in a test env (i.e. not on the installation)
			boolean addRootWebapp = server.isTestEnvironment();
			
			// If not deploying to "webapps", context docBase attributes need updating
			// TODO Improve to compare with appBase value instead of hardcoded "webapps"
			boolean deployingToAppBase = "webapps".equals(server.getDeployDirectory());
			
			Map<String, String> pathMap = new HashMap<String, String>();
			
			MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, 
					NLS.bind(Messages.errorPublishServer, server.getServer().getName()), null);
			Context [] contexts = publishedInstance.getContexts();
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
							modified = true;
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

					// If not deploying to appBase, convert to absolute path under deploy dir
					if (!deployingToAppBase) {
						String source = context.getSource();
						if (source != null && source.length() > 0 )	{
							context.setDocBase(deployDir.append(context.getDocBase()).toOSString());
							modified = true;
						}
					}
				}
			}
			// If errors are present, return status
			if (!ms.isOK())
				return ms;
			
			if (addRootWebapp) {
				// Add a context for the default webapp
				Context rootContext = publishedInstance.createContext(0);
				rootContext.setPath("");
				rootContext.setDocBase(deployDir.append("ROOT").toOSString());
				rootContext.setReloadable("false");
				modified = true;
			}
			monitor.worked(100);

			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			
			if (modified) {
				monitor.subTask(Messages.savingContextConfigTask);
				factory.save(serverXml.toOSString());
			}
			monitor.worked(100);
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Context docBase settings updated in server.xml.");
		}
		catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not localize server configuration published to " + baseDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Copies the custom loader jar required to serve projects without
	 * publishing to the specified destination directory.
	 * 
	 * @param destDir destination directory for the loader jar
	 * @param serverId ID of the server receiving the jar
	 * @return result of copy operation
	 */
	public static IStatus copyLoaderJar(IPath destDir, String serverId, String tomcatVersion) {
		String loaderJar = getLoaderJarFile(serverId, tomcatVersion);
        URL installURL = TomcatPlugin.getInstance().getBundle().getEntry(loaderJar);
        if (installURL == null) {
			Trace.trace(Trace.SEVERE, "Loader jar not found for server ID " + serverId);
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishLoaderJarNotFound, serverId), null);
        }
        	
        URL localURL;
        try {
            localURL = FileLocator.toFileURL(installURL);
        } catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Could not convert " + installURL.toString() + " to file URL: " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishURLConvert,
					new String[] {installURL.toString(), e.getLocalizedMessage()}), e);
        }
		
        destDir.toFile().mkdirs();
        IStatus status = FileUtil.copyFile(localURL, destDir.append(loaderJar).toString());        
        
		return status;
	}
	
	/**
	 * Tries to delete the custom loader jar added to support serving projects directly
	 * without publishing.  Returns a warning if not successful.
	 *  
	 * @param destDir destination directory containing the loader jar
	 * @param serverId ID of the server from which to delete the jar
	 * @return result of copy operation
	 */
	public static IStatus removeLoaderJar(IPath destDir, String serverId, String tomcatVersion) {
		String loaderJar = getLoaderJarFile(serverId, tomcatVersion);
        File loaderFile = destDir.append(loaderJar).toFile();
		// If Tomcat 7, see if jar to remove exists.  If not, ensure default jar is not present
		if ("org.eclipse.jst.server.tomcat.runtime.70".equals(serverId) && tomcatVersion != null) {
			if (!loaderFile.exists()) {
				loaderJar = getLoaderJarFile(serverId, "");
				loaderFile = destDir.append(loaderJar).toFile();
			}
		}
        // If loader jar exists but is not successfully deleted, return warning
        if (loaderFile.exists() && !loaderFile.delete())
        	return new Status(IStatus.WARNING, TomcatPlugin.PLUGIN_ID, 0,
        			NLS.bind(Messages.errorPublishCantDeleteLoaderJar, loaderFile.getPath()), null);

        return Status.OK_STATUS;
	}

	public static String getLoaderJarFile(String serverId, String tomcatVersion) {
		String loaderJar = "/" + serverId + ".loader.jar";
		// If Tomcat 7.0, we need to determine if an older jar should be used
		if ("org.eclipse.jst.server.tomcat.runtime.70".equals(serverId) && tomcatVersion != null) {
			int index = tomcatVersion.indexOf('.');
			if (index >= 0 && tomcatVersion.length() > index + 1) {
				String versionStr = tomcatVersion.substring(0, index);
				try {
					int version = Integer.parseInt(versionStr);
					if (version == 7) {
						int index2 = tomcatVersion.indexOf('.', index + 1);
						if (index2 >= 0 && tomcatVersion.length() > index2 + 1) {
							versionStr = tomcatVersion.substring(index + 1, index2);
							try {
								version = Integer.parseInt(versionStr);
								if (version == 0) {
									int index3 = tomcatVersion.indexOf('.', index2 + 1);
									if (index3 >= 0 && tomcatVersion.length() > index3 + 1) {
										versionStr = tomcatVersion.substring(index2 + 1, index3);
									}
									else {
										versionStr = tomcatVersion.substring(index2 + 1);
										for (int i = 0; i < versionStr.length(); i++) {
											if (!Character.isDigit(versionStr.charAt(i))) {
												versionStr = versionStr.substring(0, i);
												break;
											}
										}
									}
									try {
										version = Integer.parseInt(versionStr);
										if (version <= 6) {
											// Use this jar for Tomcat 7.0.6 or earlier.
											loaderJar = "/" + serverId + "6.loader.jar";
										}
										else if (version <= 8) {
											// Use this jar for Tomcat 7.0.8 (7.0.7 didn't release)
											loaderJar = "/" + serverId + "8.loader.jar";
										}
									}
									catch (NumberFormatException e) {
										// Ignore and copy default jar
									}
								}
							}
							catch (NumberFormatException e) {
								// Ignore and copy default jar
							}
						}
					}
				}
				catch (NumberFormatException e) {
					// Ignore and copy default jar
				}
			}
		}
		return loaderJar;
	}

	/**
	 * Updates the catalina.properties file to include a extra entry in the
	 * specified loader property to pickup the loader jar.
	 * 
	 * @param baseDir directory where the Catalina instance is found
	 * @param jarLoc location of loader jar relative to baseDir
	 * @param loader loader in catalina.properties to use
	 * @return result of update operation
	 */
	public static IStatus updatePropertiesToServeDirectly(IPath baseDir, String jarLoc, String loader) {
            File catalinaProperties = baseDir.append(
                    "conf/catalina.properties").toFile();
            try {
            	CatalinaPropertiesUtil.addGlobalClasspath(catalinaProperties, loader,
            			new String[] { "${catalina.base}/" + jarLoc + "/*.jar" });

            } catch (IOException e) {
            	return new Status(IStatus.ERROR,TomcatPlugin.PLUGIN_ID,
            			NLS.bind(Messages.errorPublishCatalinaProps, e.getLocalizedMessage()), e);
            }
            return Status.OK_STATUS;
	}
	
	/**
	 * Update Contexts to serve web projects directly.
	 * 
	 * @param baseDir directory where the Catalina instance is found
	 * @param loader name of the catalina.properties loader to use for global
	 * classpath entries
	 * @param monitor a progress monitor
	 * @return result of update operation
	 */
	public static IStatus updateContextsToServeDirectly(IPath baseDir, String tomcatVersion, String loader, boolean enableMetaInfResources, IProgressMonitor monitor) {

		IPath confDir = baseDir.append("conf");
		IPath serverXml = confDir.append("server.xml");
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.publishConfigurationTask, 300);

			monitor.subTask(Messages.publishContextConfigTask);
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			Server publishedServer = (Server) factory.loadDocument(new FileInputStream(serverXml.toFile()));
			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);
			monitor.worked(100);

			boolean modified = false;

			boolean isTomcat80 = tomcatVersion.startsWith("8.0");
			boolean isTomcat85 = tomcatVersion.startsWith("8.5");
			boolean isTomcat9 = tomcatVersion.startsWith("9.");
			boolean isTomcat10 = tomcatVersion.startsWith("10.");
			// care about top-level modules only
			TomcatPublishModuleVisitor visitor;
			if (isTomcat80) {
				visitor = new Tomcat80PublishModuleVisitor(
						baseDir, tomcatVersion, publishedInstance, loader, enableMetaInfResources);
			}
			else if (isTomcat85) {
				visitor = new Tomcat85PublishModuleVisitor(
						baseDir, tomcatVersion, publishedInstance, loader, enableMetaInfResources);
			}
			else if (isTomcat9) {
				visitor = new Tomcat90PublishModuleVisitor(
						baseDir, tomcatVersion, publishedInstance, loader, enableMetaInfResources);
			}
			else if (isTomcat10) {
				visitor = new Tomcat100PublishModuleVisitor(
						baseDir, tomcatVersion, publishedInstance, loader, enableMetaInfResources);
			}
			else {
				visitor = new TomcatPublishModuleVisitor(
						baseDir, tomcatVersion, publishedInstance, loader, enableMetaInfResources);
			}
			Context [] contexts = publishedInstance.getContexts();
			for (int i = 0; i < contexts.length; i++) {
				String moduleId = contexts[i].getSource();
				if (moduleId != null && moduleId.length() > 0) {
					IModule module = ServerUtil.getModule(moduleId);
					ModuleTraverser.traverse(module, visitor, monitor);
					modified = true;
				}
			}

			if (modified) {
				monitor.subTask(Messages.savingContextConfigTask);
				factory.save(serverXml.toOSString());
			}
			monitor.worked(100);
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Context docBase settings updated in server.xml.");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not modify context configurations to serve directly for Tomcat configuration " + confDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Moves contexts out of current published server.xml and into individual
	 * context XML files.
	 * 
	 * @param baseDir directory where the Catalina instance is found
	 * @param noPath true if path attribute should be removed from the context
	 * @param serverStopped true if the server is stopped
	 * @param monitor a progress monitor
	 * @return result of operation
	 */
	public static IStatus moveContextsToSeparateFiles(IPath baseDir, boolean noPath, boolean serverStopped, IProgressMonitor monitor) {
		IPath confDir = baseDir.append("conf");
		IPath serverXml = confDir.append("server.xml");
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(Messages.publishConfigurationTask, 300);

			monitor.subTask(Messages.publishContextConfigTask);
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			Server publishedServer = (Server) factory.loadDocument(new FileInputStream(serverXml.toFile()));
			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);
			monitor.worked(100);

			boolean modified = false;

			Host host = publishedInstance.getHost();
			Context[] wtpContexts = publishedInstance.getContexts();
			if (wtpContexts != null && wtpContexts.length > 0) {
				IPath contextPath = publishedInstance.getContextXmlDirectory(serverXml.removeLastSegments(1));
				File contextDir = contextPath.toFile();
				if (!contextDir.exists()) {
					contextDir.mkdirs();
				}
				// Process in reverse order, since contexts may be removed
				for (int i = wtpContexts.length - 1; i >= 0; i--) {
					Context context = wtpContexts[i];
					// TODO Handle non-project contexts when their removal can be addressed
					if (context.getSource() == null)
						continue;
					
					String name = context.getPath();
					if (name.startsWith("/")) {
						name = name.substring(1);
					}
					// If the default context, adjust the file name
					if (name.length() == 0) {
						name = "ROOT";
					}
					// Update name if multi-level path.  For 5.5 and later the "#" has been
					// "reserved" as a legal file name placeholder for "/".  For Tomcat 5.0,
					// we just need a legal unique file name since "/" will fail.  Prior to
					// 5.0, this feature is not supported.
					name = name.replace('/', '#');

					// TODO Determine circumstances, if any, where setting antiResourceLocking true can cause the original docBase content to be deleted.
					if (Boolean.valueOf(context.getAttributeValue("antiResourceLocking")).booleanValue())
						context.setAttributeValue("antiResourceLocking", "false");
					
					File contextFile = new File(contextDir, name + ".xml");
					Context existingContext = loadContextFile(contextFile);
					// If server is stopped or if contexts are not the equivalent, write the context file
					if (serverStopped || !context.isEquivalent(existingContext)) {
						// If requested, remove path attribute
						if (noPath)
							context.removeAttribute("path");
						
						DocumentBuilder builder = XMLUtil.getDocumentBuilder();
						Document contextDoc = builder.newDocument();
						contextDoc.appendChild(contextDoc.importNode(context.getElementNode(), true));
						XMLUtil.save(contextFile.getAbsolutePath(), contextDoc);
					}

					host.removeElement("Context", i);
					modified = true;
				}
			}
			monitor.worked(100);
			if (modified) {
				monitor.subTask(Messages.savingContextConfigTask);
				factory.save(serverXml.toOSString());
			}
			monitor.worked(100);
			if (Trace.isTraceEnabled())
				Trace.trace(Trace.FINER, "Context docBase settings updated in server.xml.");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not modify context configurations to serve directly for Tomcat configuration " + confDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	private static void loadSeparateContextFiles(File contextDir, Factory factory, Map<File, Context> projectContexts) {
		File[] contextFiles = contextDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});

		for (int j = 0; j < contextFiles.length; j++) {
			File ctx = contextFiles[j];

			Context context = loadContextFile(ctx);
			if (context != null) {
				// TODO Handle non-project contexts when their removal can be addressed
				String memento = context.getSource();
				if (memento != null) {
					projectContexts.put(ctx, context);
				}
			}
		}
	}
	
	private static Context loadContextFile(File contextFile) {
		FileInputStream fis = null;
		Context context = null;
		if (contextFile != null && contextFile.exists()) {
			try {
				Factory factory = new Factory();
				factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
				fis = new FileInputStream(contextFile);
				context = (Context)factory.loadDocument(fis);
				if (context != null) {
					String path = context.getPath();
					// If path attribute is not set, derive from file name
					if (path == null) {
						String fileName = contextFile.getName();
						path = fileName.substring(0, fileName.length() - ".xml".length());
						if ("ROOT".equals(path))
							path = "";
						// Assuming this use for "#" since Tomcat has "reserved" this use of "#" since 5.5.
						path = path.replace('#', '/');
						context.setPath("/" + path);
					}
				}
			} catch (Exception e) {
				// may be a spurious xml file in the host dir?
				Trace.trace(Trace.FINER, "Unable to read context "
						+ contextFile.getAbsolutePath());
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return context;
	}

	private static Map<IPath, String> catalinaJarVersion = new ConcurrentHashMap<IPath, String>();
	private static Map<IPath, Long> catalinaJarLastModified = new ConcurrentHashMap<IPath, Long>();
	private static volatile long lastCheck = 0;

	/**
	 * Checks if the version of Tomcat installed at the specified location matches
	 * the specified server type.  The return status indicates if the version matches
	 * or not, or can't be determined.
	 * 
	 * Because this can get called repeatedly for certain operations, some caching
	 * is provided.  The first check for an installPath in the current Eclipse
	 * session will query the catalina.jar for its version.  Any additional
	 * checks will compare the catalina.jar's time stamp and will use the previously
	 * cached version if it didn't change.  Additional checks that occur within
	 * 2 seconds of the last check, regardless of Tomcat version, don't bother with
	 * checking the jar time stamp and just use the cached values.
	 * 
	 * @param installPath Path to Tomcat installation
	 * @param serverType The server type ID for the desired version of Tomcat
	 * @return Returns Status.OK_Status if check succeeds, or an error status
	 * if the check fails.  If the check can't determine if the version matches,
	 * Status.CANCEL_STATUS is returned.
	 */
	public static IStatus checkCatalinaVersion(IPath installPath, String serverType) {
		String versionSubString = null;
		IPath catalinaJarPath = null;
		File jarFile = null;
		
		if (TomcatPlugin.TOMCAT_60.equals(serverType) || TomcatPlugin.TOMCAT_70.equals(serverType) || TomcatPlugin.TOMCAT_80.equals(serverType)
				|| TomcatPlugin.TOMCAT_85.equals(serverType) || TomcatPlugin.TOMCAT_90.equals(serverType) || TomcatPlugin.TOMCAT_100.equals(serverType)) {
			catalinaJarPath = installPath.append("lib").append("catalina.jar");
			jarFile = catalinaJarPath.toFile();
			// If jar is not at expected location, try alternate location
			if (!jarFile.exists()) {
				catalinaJarPath = installPath.append("server/lib").append("catalina.jar");
				jarFile = catalinaJarPath.toFile();
				// If not here either, discard path
				if (!jarFile.exists()) {
					catalinaJarPath = null;
				}
			}
		}
		else if (TomcatPlugin.TOMCAT_50.equals(serverType) || TomcatPlugin.TOMCAT_55.equals(serverType)
				 || TomcatPlugin.TOMCAT_41.equals(serverType)) {
			catalinaJarPath = installPath.append("server/lib").append("catalina.jar");
			jarFile = catalinaJarPath.toFile();
			// If jar is not at expected location, try alternate location
			if (!jarFile.exists()) {
				catalinaJarPath = installPath.append("lib").append("catalina.jar");
				jarFile = catalinaJarPath.toFile();
				// If not here either, discard path
				if (!jarFile.exists()) {
					catalinaJarPath = null;
				}
			}
		}
		if (catalinaJarPath != null) {
			versionSubString = catalinaJarVersion.get(catalinaJarPath);
			long checkTime = System.currentTimeMillis();
			// Use some logic to try to determine if a cached value is stale
			// If last check was more than a couple of seconds ago, check the jar time stamp 
			if (versionSubString != null && (checkTime - lastCheck > 2000)) {
				long curLastModified = jarFile.lastModified();
				Long oldLastModified = catalinaJarLastModified.get(catalinaJarPath);
				// If jar time stamps differ, discard the cached version string
				if (oldLastModified == null || curLastModified != oldLastModified.longValue()) {
					versionSubString = null;
				}
			}
			lastCheck = checkTime;
			// If a version string needs to be acquired
			if (versionSubString == null) {
				InputStream is = null;
				JarFile jar = null;
				try {
					// Read version string from catalina.jar
					jar = new JarFile(jarFile);
					JarEntry entry = jar.getJarEntry("org/apache/catalina/util/ServerInfo.properties");
					if (entry != null) {
						is = jar.getInputStream(entry);
						if (is != null) {
							Properties props = new Properties();
							props.load(is);
							String serverVersion = props.getProperty("server.info");
							if (serverVersion != null) {
								int index = serverVersion.indexOf("/");
								if (index > 0) {
									versionSubString = serverVersion.substring(index + 1);
									catalinaJarVersion.put(catalinaJarPath, versionSubString);
									catalinaJarLastModified.put(catalinaJarPath, new Long(jarFile.lastModified()));
								}
							}
						}
					}
				} catch (IOException e) {
					// Ignore and handle as unknown version
				}
				finally {
					if (is != null) {
						try {
							is.close();
						}
						catch (IOException e) {
							// Ignore
						}
					}
					if (jar != null) {
						try {
							jar.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
			if (versionSubString != null) {
				// If we have an actual version, test the version
				if (versionSubString.length() > 0) {
					String versionTest = versionStringMap.get(serverType);
					if (versionTest != null && !versionSubString.startsWith(versionTest)) {
						return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID,
								NLS.bind(Messages.errorInstallDirWrongVersion2,
										versionSubString, versionTest.substring(0, versionTest.length() -1)));
					}
				}
				// Else we have an unknown version
				else {
					return Status.CANCEL_STATUS;
				}
			}
			else {
				// Cache blank version string for unknown version
				catalinaJarVersion.put(catalinaJarPath, "");
				catalinaJarLastModified.put(catalinaJarPath, new Long(jarFile.lastModified()));
				return Status.CANCEL_STATUS;
			}
		}
		// Else server type is not supported or jar doesn't exist
		else {
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}

	public static String getCatalinaVersion(IPath installPath, String serverType) {
		for (Map.Entry<IPath, String> entry : catalinaJarVersion.entrySet()) {
			IPath jarPath = entry.getKey();
			if (installPath.isPrefixOf(jarPath)) {
				return entry.getValue();
			}
		}
		// If not found, we need to initialize the data for this server
		IStatus result = checkCatalinaVersion(installPath, serverType);
		// If successful, search again
		if (result.isOK()) {
			for (Map.Entry<IPath, String> entry : catalinaJarVersion.entrySet()) {
				IPath jarPath = entry.getKey();
				if (installPath.isPrefixOf(jarPath)) {
					return entry.getValue();
				}
			}
		}
		// Return unknown version
		return "";
	}
}
