/**********************************************************************
 * Copyright (c) 2007 SAS Institute, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.PublishUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.xml.sax.SAXException;

/**
 * Utility class for methods that are used by more that one version
 * of Tomcat.  Use of these methods makes it clear that more than
 * one version will be impacted by changes.
 *
 */
public class TomcatVersionHelper {

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
	 * @return collection of Context paths that are not present in current modules
	 */
	public static Collection getRemovedCatalinaContexts(ServerInstance oldServerInstance, List modules) {
		// Determine which contexts are going away
		Set removedContextPaths = new HashSet();
		// Collect paths of old web modules managed by WTP
		Context [] contexts = oldServerInstance.getContexts();
		if (contexts != null) {
			for (int i = 0; i < contexts.length; i++) {
				String source = contexts[i].getSource();
				if (source != null && source.length() > 0 )	{
					removedContextPaths.add(contexts[i].getPath());
				}
			}
		}

		// Remove paths for web modules that are staying around
		int size = modules.size();
		for (int i = 0; i < size; i++) {
			WebModule module = (WebModule) modules.get(i);
			removedContextPaths.remove(module.getPath());
		}
		return removedContextPaths;
	}

	/**
	 * Cleanup server instance location in preparation for next server publish.
	 * This currently involves deleting work directories for currently
	 * existing Contexts which will not be included in the next publish.<br>
	 * <br>
	 * Note: This method is not used by Tomcat 5.0, because it may create
	 * Context XML files under &quot;conf/Catalina/localhost&quot; for Contexts
	 * in server.xml which requires additional cleanup.
	 * 
	 * @param baseDir path to server instance directory, i.e. catalina.base
	 * @param installDir path to server installation directory (not currently used)
	 * @param modules list of currently added modules
	 * @param monitor a progress monitor or null
	 * @return MultiStatus containing results of the cleanup operation
	 */
	public static IStatus cleanupCatalinaServer(IPath baseDir, IPath installDir, List modules, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, Messages.cleanupServerTask, null);
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(Messages.cleanupServerTask, 200);

		try {
			monitor.subTask(Messages.detectingRemovedProjects);

			IPath serverXml = baseDir.append("conf").append("server.xml");
			ServerInstance oldInstance = TomcatVersionHelper.getCatalinaServerInstance(serverXml, null, null);
			if (oldInstance != null) {
				Collection oldPaths = TomcatVersionHelper.getRemovedCatalinaContexts(oldInstance, modules);
				monitor.worked(100);
				if (oldPaths != null && oldPaths.size() > 0) {
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
									subMonitor.worked(100);
							}
							else
								subMonitor.worked(100);
						}
						subMonitor.done();
					}
				}
				monitor.worked(100);
			}
			// Else no server.xml.  Assume first publish to new temp directory
			else {
				monitor.worked(200);
			}
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
	 * Creates a Catalina instance directory at the specified
	 * path.  This involves creating the set of subdirectories
	 * uses by a Catalina instance.
	 * 
	 * @param baseDir directory at which to create Catalina instance
	 * directories.
	 * @param webxml web.xml content for ROOT web application
	 * @return Status.OK_STATUS 
	 */
	public static IStatus createCatalinaInstanceDirectory(IPath baseDir, String webxml) {
		Trace.trace(Trace.FINER, "Preparing runtime directory");
		// TODO Add error handling.
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
		IPath tempPath = baseDir.append("webapps/ROOT/WEB-INF");
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
		temp = baseDir.append("work").toFile();
		if (!temp.exists())
			temp.mkdirs();

		return Status.OK_STATUS;		
	}

	/**
	 * Add context configuration found in META-INF/context.xml files
	 * present in projects to published server.xml.  Used by
	 * Tomcat 4.1, 5.0, and 5.5 which support use of META-INF/context.xml
	 * in some form.
	 * 
	 * @param baseDir path to catalina instance directory
	 * @param monitor a progress monitor or null
	 * @return result of operation
	 */
	public static IStatus publishCatalinaContextConfig(IPath baseDir, IProgressMonitor monitor) {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(Messages.publishConfigurationTask, 300);

		Trace.trace(Trace.FINER, "Apply context configurations");
		IPath confDir = baseDir.append("conf");
		IPath webappsDir = baseDir.append("webapps");
		try {
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
			monitor.done();
			
			// If problem(s) occurred adding context configurations, return error status
			if (ms.getChildren().length > 0) {
				return ms;
			}
			Trace.trace(Trace.FINER, "Server.xml updated with context.xml configurations");
			return Status.OK_STATUS;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not apply context configurations to published Tomcat v5.0 configuration from " + confDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
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
			String docBase = context.getDocBase();
			try {
				Context contextConfig = loadCatalinaContextConfig(webappsDir.append(docBase));
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
	 * @param webappDir Path to the web application
	 * @return Context element created from context.xml, or null if not found.
	 * @throws SAXException If there is a error parsing the XML. 
	 * @throws IOException If there is an error reading the file.
	 */
	private static Context loadCatalinaContextConfig(IPath webappDir) throws IOException, SAXException {
		File contextXML = new File(webappDir.toOSString()+ File.separator + "META-INF" + File.separator + "context.xml");
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
}
