/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;

import java.io.File;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.mortbay.http.HttpContext;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.*;
import org.mortbay.jetty.servlet.WebApplicationContext;

public class PreviewStarter {
	private static final String[] avertedLogs = new String[] {
		"org.mortbay.util.Container", "org.mortbay.http.HttpServer",
		"org.mortbay.util.Credential", "org.mortbay.http.SocketListener",
		"org.mortbay.http.HttpServer", "org.mortbay.jetty.Server"
	};

	protected String configPath;
	protected Server server;

	public PreviewStarter(String configPath) {
		this.configPath = configPath;
	}

	public static void main(String[] args) {
		PreviewStarter app = new PreviewStarter(args[0]);
		app.run();
	}

	protected void run() {
		System.out.println("Starting preview server");
		System.out.println();
		try {
			ServerConfig config = new ServerConfig(configPath);
			System.out.println("Port " + config.getPort());
			Module[] m = config.getModules();
			int size = m.length;
			if (size > 0) {
				System.out.println("Modules:");
				for (int i = 0; i < size; i++) {
					System.out.println("  " + m[i].getName());
				}
				System.out.println();
			}
			
			for (int i = 0; i < avertedLogs.length; i++) {
				Logger logger = Logger.getLogger(avertedLogs[i]);
				logger.setFilter(new Filter() {
					public boolean isLoggable(LogRecord record) {
						//Trace.trace(Trace.FINEST, "Averted Jetty log: " + record.getMessage());
						//System.out.println("averted: " + record.getLoggerName() + ": " + record.getMessage());
						return false;
					}
				});
			}
			
			// helper code to find jetty loggers
			/*Logger logger = Logger.getLogger("org.mortbay.http.HttpServer");
			logger.addHandler(new java.util.logging.Handler() {
				public void close() throws SecurityException {
					// ignore
				}

				public void flush() {
					// ignore
				}

				public void publish(LogRecord record) {
					System.out.println("Logger found: " + record.getLoggerName());
				}
			});*/
			
			server = new Server();
			server.addListener(":" + config.getPort());
			server.setTrace(false);
			server.setStatsOn(false);
			
			/*HttpContext context2 = new HttpContext();
			context2.setContextPath("/");
			context2.addHandler(new WTPErrorPageHandler());
			context2.setAttribute(HttpContext.__ErrorHandler, new WTPErrorPageHandler());
			server.addContext(context2);
			server.setRootWebApp("/");*/
			
			for (int i = 0; i < size; i++) {
				Module module = m[i];
				if (module.isStaticWeb()) {
					HttpContext context = new HttpContext();
					context.setContextPath(module.getContext());
					context.setResourceBase(module.getPath());
					context.addHandler(new ResourceHandler());
					context.addHandler(new WTPErrorPageHandler());
					context.setAttribute(HttpContext.__ErrorHandler, new WTPErrorPageHandler());
					server.addContext(context);
				} else {
					WebApplicationContext context = server.addWebApplication(module.getContext(), module.getPath());
					context.setResourceBase(module.getPath());
					//context.addHandler(new ResourceHandler());
					context.addHandler(new WTPErrorPageHandler());
					context.setAttribute(HttpContext.__ErrorHandler, new WTPErrorPageHandler());
					
					//context.setClassLoader(getClass().getClassLoader());
					//context.start();
					
					//WebApplicationContext context = server.addWebApplication("/", module.getPath() + "/");
					/*context.setConfigurationClassNames(new String[] { "org.mortbay.jetty.servlet.XMLConfiguration" });
					context.setResourceBase(module.getPath());
					context.setIgnoreWebJetty(true);*/
					//System.out.println("context: " + context.getContextPath());
					//System.out.println("resource: " + context.getResourceBase());
					//server.addContext(context);
				}
			}
			
			/*WebApplicationContext cont = new WebApplicationContext("D:\\dev\\wtp\\runtime-workspace5\\170228\\WebContent");
			cont.setTempDirectory(new File("C:/temp"));
			cont.setContextPath("/");*/
			//server.addContext(cont);
			//server.addWebApplication("test", "D:\\dev\\wtp\\runtime-workspace5\\170228\\WebContent");
			//WebApplicationContext cont = server.addWebApplication("/", "C:\\Temp\\Test.war");
			//cont.setClassLoader();
			//cont.setConfigurationClassNames(new String[] { "org.mortbay.jetty.servlet.XMLConfiguration" });
			//cont.setIgnoreWebJetty(true);
			//cont.setExtractWAR(true);
			
			//server.addListener(new SocketListener(new InetAddrPort(8080)));
			//ResourceHandler handler = new ResourceHandler();
			//server.addEventListener(handler);
			//server.setRootWebApp()
			//server.addContext(new HttpContext());
			/*HttpContext context = new HttpContext(server, "/");
			context.setClassLoader(new PreviewClassloader());*/
			
			//Context root = new Context(server,"/",Context.SESSIONS);
			//root.addServlet(new ServletHolder(new HelloServlet("Ciao")), "/*");
			//HttpContext context = server.getContext("/t");
			//ServletHandler handler = new ServletHandler();
			//handler.addServlet("Dump","/dump/*","org.mortbay.servlet.Dump");
			//context.addHandler(handler);
			//cont.addHandler(handler);
			
			//server.setStatsOn(true);
			try {
				server.start();
			} catch (Exception e) {
				// ??
			}
			/*System.out.println(cont.getHandlers().length);
			System.out.println(context.getHandlers().length);
			System.out.println(cont.getWelcomeFiles().length);
			
			WebApplicationHandler wah = (WebApplicationHandler) cont.getHandlers()[0];
			//System.out.println(wah.getResource("/test.jsp"));
			//wah.getServlets()[0].start();
			System.out.println(wah.getServlets()[0]);
			System.out.println(wah.getServlets()[1]);
			System.out.println(wah.getServlets()[2]);*/
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		//HttpServer server = new HttpServer();
		/*SocketListener listener = new SocketListener();
		listener.setPort(8080);
		server.addListener(listener);*/
	}

	public void stop() {
		try {
			System.out.println("Stop!");
			server.stop();
			//File contextWorkDir = new File(workDir, DIR_PREFIX + pid.hashCode());
			//deleteDirectory(contextWorkDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteDirectory is a convenience method to recursively delete a directory
	 * @param directory - the directory to delete.
	 * @return was the delete succesful
	 */
	protected static boolean deleteDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return directory.delete();
	}
}