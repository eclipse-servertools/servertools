/*******************************************************************************
 * Copyright (c) 2007, 2021 IBM Corporation and others.
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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

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
			System.setProperty("org.mortbay.log.class", "org.eclipse.wst.server.preview.internal.WTPLogger");
			ServerConfig config = new ServerConfig(configPath);
			System.out.println("Starting preview server on port " + config.getPort());
			System.out.println();
			Module[] m = config.getModules();
			int size = m.length;
			if (size > 0) {
				System.out.println("Modules:");
				for (Module mm : m)
					System.out.println("  " + mm.getName() + " (" + mm.getContext() + ")");
				System.out.println();
			}
			
			server = new Server(config.getPort());
			server.setStopAtShutdown(true);
			
			WTPErrorHandler errorHandler = new WTPErrorHandler();

			HandlerList handlers = new HandlerList();
			for (Module module : m) {
				if (module.isStaticWeb()) {
					ContextResourceHandler resourceHandler = new ContextResourceHandler();
					resourceHandler.setResourceBase(module.getPath());
					resourceHandler.setContext(module.getContext());
					handlers.addHandler(resourceHandler);
				} else {
					WebAppContext wac = new WebAppContext();
					wac.setContextPath(module.getContext());
					wac.setWar(module.getPath());
					wac.setErrorHandler(errorHandler);
					handlers.addHandler(wac);
				}
			}
			
			handlers.addHandler(new WTPDefaultHandler(config.getPort(), m));
			server.setHandler(handlers);
			
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
