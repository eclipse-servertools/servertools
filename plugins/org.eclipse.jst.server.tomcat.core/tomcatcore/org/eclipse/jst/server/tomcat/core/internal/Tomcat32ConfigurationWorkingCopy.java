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
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.Connector;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.ContextManager;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.Parameter;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * Tomcat v3.2 server configuration.
 */
public class Tomcat32ConfigurationWorkingCopy extends Tomcat32Configuration implements ITomcatConfigurationWorkingCopy {
	protected IServerConfigurationWorkingCopy wc;

	/**
	 * Tomcat32Configuration constructor comment.
	 */
	public Tomcat32ConfigurationWorkingCopy() {
		super();
	}
	
	public void initialize(IServerConfigurationWorkingCopy wc2) {
		this.wc = wc2;
	}
	
	public void setDefaults() { }
	
	public void handleSave(byte id, IProgressMonitor monitor) { }
	
	/**
	 * Adds a mime mapping.
	 * @param map MimeMapping
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
			ContextManager contextManager = server.getContextManager();
			Context context = (Context) contextManager.createElement(index, "Context");
	
			context.setPath(module.getPath());
			context.setDocBase(module.getDocumentBase());
			context.setReloadable(module.isReloadable() ? "true" : "false");
			if (module.getMemento() != null && module.getMemento().length() > 0)
				context.setSource(module.getMemento());
			isServerDirty = true;
			firePropertyChangeEvent(ADD_WEB_MODULE_PROPERTY, null, module);
		} catch (Exception e) {
			Trace.trace("Error adding web module", e);
		}
	}
	
	/**
	 * Localize the web projects in this configuration.
	 *
	 * @param file java.io.File
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void localizeConfiguration(IPath path, TomcatServer serverType, IRuntime runtime, IProgressMonitor monitor) {
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(TomcatPlugin.getResource("%updatingConfigurationTask"), 100);
	
			Tomcat32Configuration config = new Tomcat32Configuration();
			config.load(path, ProgressUtil.getSubMonitorFor(monitor, 30));
	
			if (monitor.isCanceled())
				return;
	
			if (serverType.isTestEnvironment()) {
				config.server.getContextManager().setHome(runtime.getLocation().toOSString());
				config.isServerDirty = true;
			} else {
				IServerConfigurationWorkingCopy scwc = config.getServerConfiguration().getWorkingCopy();
				((Tomcat32ConfigurationWorkingCopy) scwc.getDelegate()).localizeWebModules();
			}
	
			monitor.worked(40);
	
			if (monitor.isCanceled())
				return;

			config.save(path, false, ProgressUtil.getSubMonitorFor(monitor, 30));
	
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
			int con = Integer.parseInt(id);
			Connector connector = server.getContextManager().getConnector(con);
	
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
			ContextManager contextManager = server.getContextManager();
			Context context = contextManager.getContext(index);
			context.setPath(path);
			context.setDocBase(docBase);
			context.setReloadable(reloadable ? "true" : "false");
			isServerDirty = true;
			WebModule module = new WebModule(path, docBase, null, reloadable);
			firePropertyChangeEvent(MODIFY_WEB_MODULE_PROPERTY, new Integer(index), module);
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
			ContextManager contextManager = server.getContextManager();
			contextManager.removeElement("Context", index);
			isServerDirty = true;
			firePropertyChangeEvent(REMOVE_WEB_MODULE_PROPERTY, null, new Integer(index));
		} catch (Exception e) {
			Trace.trace("Error removing web module " + index, e);
		}
	}
	
	public void importFromPath(IPath path, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
	}

	public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		load(runtime.getLocation().append("conf"), monitor);
	}
}