/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.ITomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.ITomcatWebModule;
import org.eclipse.jst.server.tomcat.core.WebModule;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerPort;
import org.eclipse.wst.server.core.util.FileUtil;
/**
 * Generic Tomcat server configuration.
 */
public abstract class TomcatConfiguration implements ITomcatConfiguration, ITomcatConfigurationWorkingCopy {
	public static final String NAME_PROPERTY = "name";
	public static final String PORT_PROPERTY = "port";
	public static final String MODIFY_PORT_PROPERTY = "modifyPort";
	public static final String ADD_MAPPING_PROPERTY = "addMapping";
	public static final String REMOVE_MAPPING_PROPERTY = "removeMapping";
	public static final String MODIFY_MAPPING_PROPERTY = "modifyMapping";
	
	public static final String MODIFY_WEB_MODULE_PROPERTY = "modifyWebModule";
	public static final String ADD_WEB_MODULE_PROPERTY = "addWebModule";
	public static final String REMOVE_WEB_MODULE_PROPERTY = "removeWebModule";
	
	protected IFolder configPath;

	// property change listeners
	private transient List propertyListeners;

	/**
	 * TomcatConfiguration constructor comment.
	 */
	public TomcatConfiguration(IFolder path) {
		super();
		this.configPath = path;
		/*try {
			load(configPath, new NullProgressMonitor());
		} catch (Exception e) {
			// ignore
		}*/
	}
	
	protected IFolder getFolder() {
		return configPath;
	}

	/**
	 * Copies all files from the given directory in the workbench
	 * to the given location.  Can be overridden by version specific
	 * class to modify or enhance what publish does.
	 *
	 * @param from java.io.File
	 * @param to java.io.File
	 * @return org.eclipse.core.runtime.IStatus
	 */
	protected IStatus backupAndPublish(IPath confDir, boolean doBackup, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%publishConfigurationTask"), null);
		Trace.trace("Backup and publish");
		monitor = ProgressUtil.getMonitorFor(monitor);

		backupAndPublish(confDir, doBackup, ms, monitor, 0);

		monitor.done();
		return ms;
	}
	
	protected void backupAndPublish(IPath confDir, boolean doBackup, MultiStatus ms, IProgressMonitor monitor, int additionalWork) {
		try {
			IPath backup = null;
			if (doBackup) {
				// create backup directory
				backup = confDir.append("backup");
				if (!backup.toFile().exists())
					backup.toFile().mkdir();
			}
			
			confDir = confDir.append("conf");
	
			/*IServerConfiguration config = getServerConfiguration();
			IFolder folder = config.getConfigurationDataFolder();
			if (folder != null)
				backupFolder(folder, confDir, backup, ms, monitor);
			else {
				IPath path = config.getConfigurationDataPath();
				backupPath(configPath, confDir, backup, ms, monitor);*/
				backupFolder(getFolder(), confDir, backup, ms, monitor, additionalWork);
			//}
		} catch (Exception e) {
			Trace.trace("backupAndPublish() error", e);
			IStatus s = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%errorPublishConfiguration", new String[] {e.getLocalizedMessage()}), e);
			ms.add(s);
		}
	}
	
	protected void backupFolder(IFolder folder, IPath confDir, IPath backup, MultiStatus ms, IProgressMonitor monitor, int additionalWork) throws CoreException {
		IResource[] children = folder.members();
		if (children == null)
			return;
		
		int size = children.length;
		monitor.beginTask(TomcatPlugin.getResource("%publishConfigurationTask"), size * 100 + additionalWork);
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
	
	protected IStatus cleanupServer(IPath confDir, IPath installDir, IProgressMonitor monitor) {
		// Default implementation assumes nothing to clean
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.done();
		return Status.OK_STATUS;
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
	protected String getWebModuleURL(IModule webModule) {
		WebModule module = getWebModule(webModule);
		if (module != null)
			return module.getPath();
		
		IWebModule webModule2 = (IWebModule) webModule.getAdapter(IWebModule.class);
		return "/" + webModule2.getContextRoot();
	}

	/**
	 * Returns the partial URL applicable to this project.
	 *
	 * @return java.lang.String
	 * @param project org.eclipse.core.resources.IProject
	 */
	protected WebModule getWebModule(IModule module) {
		if (module == null)
			return null;
	
		String memento = module.getId();
	
		List modules = getWebModules();
		int size = modules.size();
		for (int i = 0; i < size; i++) {
			WebModule webModule = (WebModule) modules.get(i);
			if (memento.equals(webModule.getMemento())) {
				return webModule;
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
	//protected abstract void save(IPath path, boolean forceSave, IProgressMonitor monitor) throws CoreException;
	
	protected abstract void save(IFolder folder, IProgressMonitor monitor) throws CoreException;
	
	protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		if (propertyListeners == null)
			return;
	
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		try {
			Iterator iterator = propertyListeners.iterator();
			while (iterator.hasNext()) {
				try {
					PropertyChangeListener listener = (PropertyChangeListener) iterator.next();
					listener.propertyChange(event);
				} catch (Exception e) {
					Trace.trace("Error firing property change event", e);
				}
			}
		} catch (Exception e) {
			Trace.trace("Error in property event", e);
		}
	}

	/**
	 * Adds a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners == null)
			propertyListeners = new ArrayList();
		propertyListeners.add(listener);
	}

	/**
	 * Removes a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners != null)
			propertyListeners.remove(listener);
	}

	/*public void importFromPath(IPath path, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
	}

	public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		load(runtime.getLocation().append("conf"), monitor);
	}*/
	
	protected abstract void load(IPath path, IProgressMonitor monitor) throws CoreException;
	
	protected abstract void load(IFolder folder, IProgressMonitor monitor) throws CoreException;
	
	public abstract void addWebModule(int index, ITomcatWebModule module);
	
	public abstract void removeWebModule(int index);

	/**
	 * Return a string representation of this object.
	 * @return java.lang.String
	 */
	public String toString() {
		return "TomcatConfiguration[" + this + "]";
	}
}