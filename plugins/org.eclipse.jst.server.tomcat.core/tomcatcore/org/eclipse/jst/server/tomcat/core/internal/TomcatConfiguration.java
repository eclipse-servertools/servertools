/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.jst.server.tomcat.core.ITomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.WebModule;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.model.IServerPort;
import org.eclipse.wst.server.core.model.ServerConfigurationDelegate;
import org.eclipse.wst.server.core.util.FileUtil;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * Generic Tomcat server configuration.
 */
public abstract class TomcatConfiguration extends ServerConfigurationDelegate implements ITomcatConfiguration {
	public static final String NAME_PROPERTY = "name";
	public static final String PORT_PROPERTY = "port";
	public static final String MODIFY_PORT_PROPERTY = "modifyPort";
	public static final String ADD_MAPPING_PROPERTY = "addMapping";
	public static final String REMOVE_MAPPING_PROPERTY = "removeMapping";
	public static final String MODIFY_MAPPING_PROPERTY = "modifyMapping";
	
	public static final String MODIFY_WEB_MODULE_PROPERTY = "modifyWebModule";
	public static final String ADD_WEB_MODULE_PROPERTY = "addWebModule";
	public static final String REMOVE_WEB_MODULE_PROPERTY = "removeWebModule";

	/**
	 * TomcatConfiguration constructor comment.
	 */
	public TomcatConfiguration() {
		super();
	}

	/**
	 * Copies all files from the given directory in the workbench
	 * to the given location.
	 *
	 * @param from java.io.File
	 * @param to java.io.File
	 * @return org.eclipse.core.runtime.IStatus
	 */
	protected IStatus backupAndPublish(IPath confDir, boolean doBackup, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%publishConfigurationTask"), null);
		Trace.trace("Backup and publish");
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
	
			IPath backup = null;
			if (doBackup) {
				// create backup directory
				backup = confDir.append("backup");
				if (!backup.toFile().exists())
					backup.toFile().mkdir();
			}
			
			confDir = confDir.append("conf");
	
			IServerConfiguration config = getServerConfiguration();
			IFolder folder = config.getConfigurationDataFolder();
			if (folder != null)
				backupFolder(folder, confDir, backup, ms, monitor);
			else {
				IPath path = config.getConfigurationDataPath();
				backupPath(path, confDir, backup, ms, monitor);
			}
			
		} catch (Exception e) {
			Trace.trace("backupAndPublish() error", e);
			IStatus s = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorPublishConfiguration", new String[] {e.getLocalizedMessage()}), e);
			ms.add(s);
		}
		monitor.done();
		return ms;
	}
	
	protected void backupFolder(IFolder folder, IPath confDir, IPath backup, MultiStatus ms, IProgressMonitor monitor) throws CoreException {
		IResource[] children = folder.members();
		if (children == null)
			return;
		
		int size = children.length;
		monitor.beginTask(TomcatPlugin.getResource("%publishConfigurationTask"), size * 100);
		for (int i = 0; i < size; i++) {
			if (children[i] instanceof IFile) {
				try {
					IFile file = (IFile) children[i];
					String name = file.getName();
					monitor.subTask(TomcatPlugin.getResource("%publisherPublishTask", new String[] {name}));
					Trace.trace(Trace.FINEST, "Publishing " + name);

					// backup and copy file
					boolean copy = true;
					if (backup != null && !(backup.append(name).toFile().exists())) {
						IStatus status = FileUtil.copyFile(confDir.append(name).toOSString(), backup + File.separator + name);
						ms.add(status);
						if (!status.isOK())
							copy = false;
					}
					
					if (copy) {
						InputStream in = file.getContents();
						ms.add(FileUtil.copyFile(in, confDir.append(name).toOSString()));
					}
				} catch (Exception e) {
					Trace.trace("backupAndPublish() error", e);
					ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorPublishConfiguration", new String[] {e.getLocalizedMessage()}), e));
				}
			}
			monitor.worked(100);
		}
	}
	
	protected void backupPath(IPath path, IPath confDir, IPath backup, MultiStatus ms, IProgressMonitor monitor) {
		File[] files = path.toFile().listFiles();
		if (files == null)
			return;
			
		int size = files.length;
		monitor.beginTask(TomcatPlugin.getResource("%publishConfigurationTask"), size * 100);
		for (int i = 0; i < size; i++) {
			try {
				File file = files[i];
				String name = file.getName();
				monitor.subTask(TomcatPlugin.getResource("%publisherPublishTask", new String[] {name}));
				Trace.trace(Trace.FINEST, "Publishing " + name);

				// backup and copy file
				boolean copy = true;
				if (backup != null && !(backup.append(name).toFile().exists())) {
					IStatus status = FileUtil.copyFile(confDir.append(name).toOSString(), backup + File.separator + name);
					ms.add(status);
					if (!status.isOK())
						copy = false;
				}
				
				if (copy)
					ms.add(FileUtil.copyFile(file.getAbsolutePath(), confDir.append(name).toOSString()));
			} catch (Exception e) {
				Trace.trace("backupAndPublish() error", e);
				ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorPublishConfiguration", new String[] {e.getLocalizedMessage()}), e));
			}
			monitor.worked(100);
		}
	}

	/**
	 * Returns the root of the docbase parameter.
	 *
	 * @return java.lang.String
	 */
	protected abstract String getDocBaseRoot();

	/**
	 * Returns the main server port.
	 * @return IServerPort
	 */
	public abstract IServerPort getMainPort();

	/**
	 * Returns the prefix that is used in front of the
	 * web module path property. (e.g. "webapps")
	 *
	 * @return java.lang.String
	 */
	public abstract String getPathPrefix();

	/**
	 * Returns the partial URL applicable to this module.
	 *
	 * @return java.lang.String
	 * @param module IWebModule
	 */
	protected String getWebModuleURL(IWebModule webModule) {
		WebModule module = getWebModule(webModule);
		if (module != null)
			return module.getPath();
		
		return webModule.getContextRoot();
	}

	/**
	 * Returns the partial URL applicable to this project.
	 *
	 * @return java.lang.String
	 * @param project org.eclipse.core.resources.IProject
	 */
	protected WebModule getWebModule(IWebModule webModule) {
		if (webModule == null)
			return null;
	
		String memento = webModule.getFactoryId() + ":" + webModule.getId();
	
		List modules = getWebModules();
		int size = modules.size();
		for (int i = 0; i < size; i++) {
			WebModule module = (WebModule) modules.get(i);
			if (memento.equals(module.getMemento())) {
				return module;
			}
		}
		return null;
	}

	/**
	 * Return the docBase of the ROOT web module.
	 *
	 * @return java.lang.String
	 */
	protected abstract String getROOTModuleDocBase();

	/**
	 * Save to the given directory.
	 * @param f java.io.File
	 * @param forceSave boolean
	 * @exception java.io.IOException
	 */
	protected abstract void save(IPath path, boolean forceSave, IProgressMonitor monitor) throws CoreException;
	
	protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		getServerConfiguration().createWorkingCopy().firePropertyChangeEvent(propertyName, oldValue, newValue);
	}
	
	public void importFromPath(IPath path, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
	}

	public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		load(runtime.getLocation().append("conf"), monitor);
	}

	/**
	 * Return a string representation of this object.
	 * @return java.lang.String
	 */
	public String toString() {
		return "TomcatConfiguration[" + this + "]";
	}
}