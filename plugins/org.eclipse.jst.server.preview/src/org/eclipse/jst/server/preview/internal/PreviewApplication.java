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
package org.eclipse.jst.server.preview.internal;

import java.io.File;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.*;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.jetty.servlet.WebApplicationHandler;

public class PreviewApplication implements IApplication {
	//public static final String ARG_CONFIG = "config";

	protected Server server;

	public Object start(IApplicationContext appContext) throws Exception {
		System.out.println("Here");
		try {
			/*String config = (String) context.getArguments().get(ARG_CONFIG);
			
			Server2 server = new Server2(new Path(config));
			
			Hashtable map = new Hashtable();
			map.put("http.port", new Integer(8080));
			JettyConfigurator.startServer("preview", map);
			
			BundleContext context2 = PreviewServerPlugin.getInstance().context;
			ServiceReference sr = context2.getServiceReference("org.osgi.service.cm.ManagedServiceFactory");
			System.out.println("a " + sr);
			ServiceReference sr2 = context2.getServiceReference("org.eclipse.equinox.http.jetty.JettyConfigurator.preview");
			System.out.println("b " + sr2);
			Object obj = context2.getService(sr);*/
			
			//ManagedServiceFactory
			//factory.
			
			//System.out.println("c " + obj);
			
			//org.eclipse.equinox.http.jetty.internal.HttpServerManager 
			
			//HttpService service = (HttpService) obj; 
			//System.out.println("Here: " + service);
			
			server = new Server();
			/*WebApplicationContext cont = new WebApplicationContext("D:\\dev\\wtp\\runtime-workspace5\\170228\\WebContent");
			cont.setTempDirectory(new File("C:/temp"));
			cont.setContextPath("/");*/
			//server.addContext(cont);
			//server.addWebApplication("test", "D:\\dev\\wtp\\runtime-workspace5\\170228\\WebContent");
			WebApplicationContext cont = server.addWebApplication("/", "C:\\Temp\\Test.war");
			//cont.setClassLoader();
			cont.setConfigurationClassNames(new String[] { "org.mortbay.jetty.servlet.XMLConfiguration" });
			cont.setIgnoreWebJetty(true);
			cont.setExtractWAR(true);
			
			//server.addListener(new SocketListener(new InetAddrPort(8080)));
			//ResourceHandler handler = new ResourceHandler();
			//server.addEventListener(handler);
			//server.setRootWebApp()
			//server.addContext(new HttpContext());
			/*HttpContext context = new HttpContext(server, "/");
			context.setClassLoader(new PreviewClassloader());*/
			
			//Context root = new Context(server,"/",Context.SESSIONS);
			//root.addServlet(new ServletHolder(new HelloServlet("Ciao")), "/*");
			server.addListener(":8080");
			HttpContext context = server.getContext("/t");
			ServletHandler handler = new ServletHandler();
			handler.addServlet("Dump","/dump/*","org.mortbay.servlet.Dump");
			context.addHandler(handler);
			//cont.addHandler(handler);
			
			server.setStatsOn(true);
			try {
				server.start();
			} catch (Exception e) {
				// ??
			}
			System.out.println(cont.getHandlers().length);
			System.out.println(context.getHandlers().length);
			System.out.println(cont.getWelcomeFiles().length);
			
			WebApplicationHandler wah = (WebApplicationHandler) cont.getHandlers()[0];
			//System.out.println(wah.getResource("/test.jsp"));
			//wah.getServlets()[0].start();
			System.out.println(wah.getServlets()[0]);
			System.out.println(wah.getServlets()[1]);
			System.out.println(wah.getServlets()[2]);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(30000);
		} catch (Exception e) {
			// ignore
		}
		
		//System.out.println(cont.getRequests());
		//System.out.println(context.getRequests());
		
		System.out.println("Here2");
		try {
			System.out.println("Stop!");
			server.stop();
			//File contextWorkDir = new File(workDir, DIR_PREFIX + pid.hashCode());
			//deleteDirectory(contextWorkDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return EXIT_OK;
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