package org.eclipse.jst.server.tomcat.core.internal;
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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.tomcat.core.IMimeMapping;
import org.eclipse.jst.server.tomcat.core.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.ITomcatWebModule;
import org.eclipse.jst.server.tomcat.core.WebModule;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Connector;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Engine;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Host;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Service;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * Tomcat v4.1 server configuration.
 */
public class Tomcat41ConfigurationWorkingCopy extends Tomcat41Configuration implements ITomcatConfigurationWorkingCopy {
	protected IServerConfigurationWorkingCopy wc;

	/**
	 * Tomcat41Configuration constructor comment.
	 */
	public Tomcat41ConfigurationWorkingCopy() {
		super();
	}
	
	public void initialize(IServerConfigurationWorkingCopy wc2) {
		this.wc = wc2;
	}
	
	public void setDefaults() { }
	
	public void handleSave(byte id, IProgressMonitor monitor) { }
	
	/**
	 * Adds a mime mapping.
	 * @param extension java.lang.String
	 * @param mimeType java.lang.String
	 */
	public void addMimeMapping(int index, IMimeMapping map) {
		webAppDocument.addMimeMapping(index, map);
		firePropertyChangeEvent(ADD_MAPPING_PROPERTY, new Integer(index), map);
	}

	/**
	 * Add a web module.
	 * @param module org.eclipse.jst.server.tomcat.WebModule
	 */
	public void addWebModule(int index, ITomcatWebModule module) {
		try {
			int size = server.getServiceCount();
			for (int i = 0; i < size; i++) {
				Service service = server.getService(i);
				if (service.getName().equalsIgnoreCase(DEFAULT_SERVICE)) {
					Engine engine = service.getEngine();
					Host host = engine.getHost();
					Context context = (Context) host.createElement(index, "Context");
					context.setDocBase(module.getDocumentBase());
					context.setPath(module.getPath());
					context.setReloadable(module.isReloadable() ? "true" : "false");
					if (module.getMemento() != null && module.getMemento().length() > 0)
						context.setSource(module.getMemento());
					isServerDirty = true;
					firePropertyChangeEvent(ADD_WEB_MODULE_PROPERTY, null, module);
					return;
				}
			}
		} catch (Exception e) {
			Trace.trace("Error adding web module " + module.getPath(), e);
		}
	}

	/**
	 * Localize the web projects in this configuration.
	 *
	 * @param file java.io.File
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void localizeConfiguration(IPath path, TomcatServer server2, TomcatRuntime runtime, IProgressMonitor monitor) {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(TomcatPlugin.getResource("%updatingConfigurationTask"), 100);
			
			Tomcat41Configuration config = new Tomcat41Configuration();
			config.load(path, ProgressUtil.getSubMonitorFor(monitor, 40));
	
			if (monitor.isCanceled())
				return;
	
			if (!server2.isTestEnvironment()) {
				IServerConfigurationWorkingCopy scwc = config.getServerConfiguration().getWorkingCopy();
				((Tomcat41ConfigurationWorkingCopy) scwc.getDelegate()).localizeWebModules();
			}
			monitor.worked(20);
	
			if (monitor.isCanceled())
				return;
	
			config.save(path, false, ProgressUtil.getSubMonitorFor(monitor, 40));
	
			if (!monitor.isCanceled())
				monitor.done();
		} catch (Exception e) {
			Trace.trace("Error localizing configuration", e);
		}
	}
	
	/**
	 * Go through all of the web modules and make the document
	 * base "local" to the configuration.
	 */
	protected void localizeWebModules() {
		List modules = getWebModules();

		int size = modules.size();
		for (int i = 0; i < size; i++) {
			WebModule module = (WebModule) modules.get(i);
			String memento = module.getMemento();
			if (memento != null && memento.length() > 0) {
				// update document base to a relative ref
				String docBase = getPathPrefix() + module.getPath();
				if (docBase.startsWith("/") || docBase.startsWith("\\"))
					docBase = docBase.substring(1);
				modifyWebModule(i, docBase, module.getPath(), module.isReloadable());
			}
		}
	}

	/**
	 * Change the extension of a mime mapping.
	 * @param index int
	 * @param newExtension java.lang.String
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
			int servNum = Integer.parseInt(id.substring(0, i));
			int connNum = Integer.parseInt(id.substring(i + 1));
			
			Service service = server.getService(servNum);
			Connector connector = service.getConnector(connNum);
			connector.setPort(port + "");
			isServerDirty = true;
			firePropertyChangeEvent(MODIFY_PORT_PROPERTY, id, new Integer(port));
		} catch (Exception e) {
			Trace.trace("Error modifying server port " + id, e);
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
			int size = server.getServiceCount();
			for (int i = 0; i < size; i++) {
				Service service = server.getService(i);
				if (service.getName().equalsIgnoreCase(DEFAULT_SERVICE)) {
					Engine engine = service.getEngine();
					Host host = engine.getHost();
					Context context = host.getContext(index);
					context.setPath(path);
					context.setDocBase(docBase);
					context.setReloadable(reloadable ? "true" : "false");
					isServerDirty = true;
					WebModule module = new WebModule(path, docBase, null, reloadable);
					firePropertyChangeEvent(MODIFY_WEB_MODULE_PROPERTY, new Integer(index), module);
					return;
				}
			}
		} catch (Exception e) {
			Trace.trace("Error modifying web module " + index, e);
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
			int size = server.getServiceCount();
			for (int i = 0; i < size; i++) {
				Service service = server.getService(i);
				if (service.getName().equalsIgnoreCase(DEFAULT_SERVICE)) {
					Engine engine = service.getEngine();
					Host host = engine.getHost();
					host.removeElement("Context", index);
					isServerDirty = true;
					firePropertyChangeEvent(REMOVE_WEB_MODULE_PROPERTY, null, new Integer(index));
					return;
				}
			}
		} catch (Exception e) {
			Trace.trace("Error removing module ref " + index, e);
		}
	}
	
	public void importFromPath(IPath path, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
	}

	public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		load(runtime.getLocation().append("conf"), monitor);
	}
}
