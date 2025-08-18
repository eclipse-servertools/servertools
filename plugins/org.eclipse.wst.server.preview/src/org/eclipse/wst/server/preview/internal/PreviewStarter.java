/*******************************************************************************
 * Copyright (c) 2007, 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;

import java.io.File;

import org.eclipse.jetty.ee8.nested.ContextHandler;
import org.eclipse.jetty.ee8.nested.ResourceHandler;
import org.eclipse.jetty.ee8.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;


public class PreviewStarter {
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
		try {
//			System.setProperty("slf4j.provider", "org.slf4j.simple.SimpleServiceProvider");
//			System.setProperty("org.mortbay.log.class", "org.apache.logging.log4j.simple.SimpleLogger");
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
			System.setProperty("VERBOSE", "true");
			ServerConfig config = new ServerConfig(configPath);
			System.out.println("Starting preview server on port " + config.getPort());
			System.out.println();

			server = new Server(config.getPort());
			server.setStopAtShutdown(true);

			System.out.println("Modules:");
			Module[] modules = config.getModules();
			ContextHandlerCollection handlers = new ContextHandlerCollection();
			for (Module module : modules) {
				System.out.println("  " + module.getName() + " (" + module.getContext() + ") ");
				if (module.isStaticWeb()) {
					ResourceHandler handler = new ResourceHandler();
					File f = new File(module.getPath());
					handler.setBaseResource(f.toPath());
					handler.setDirAllowed(true);
					handler.setDirectoriesListed(true);
					handler.setWelcomeFiles(new String[]{"index.html", "welcome.html"});
					handlers.addHandler(new ContextHandler(module.getContext(), handler));
				}
				else {
					WebAppContext handler = new WebAppContext();
					handler.setBaseResourceAsString(module.getPath());
					handler.setContextPath(module.getContext());
					handler.setParentLoaderPriority(true);
					handler.setLogUrlOnStart(true);
					handler.setWelcomeFiles(new String[]{"index.html", "welcome.html"});
					handlers.addHandler(handler);
				}
			}
			server.setHandler(handlers);
			handlers.setServer(server);
			System.out.println();

//			server.setDefaultHandler(new WTPDefaultHandler(config.getPort(), m));
//			server.setErrorHandler(new WTPErrorHandler());
			
			server.start();
			server.join();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			System.out.println("Stop!");
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * deleteDirectory is a convenience method to recursively delete a directory
	 * @param directory - the directory to delete.
	 * @return was the delete successful
	 */
	protected static boolean deleteDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return directory.delete();
	}
}
