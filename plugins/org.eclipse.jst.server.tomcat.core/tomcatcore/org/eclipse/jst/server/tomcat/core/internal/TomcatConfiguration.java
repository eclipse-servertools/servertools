/*******************************************************************************
 * Copyright (c) 2003, 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osgi.util.NLS;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerPort;
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
	
	public static final String DEFAULT_WEBXML_SERVLET23 = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<!DOCTYPE web-app PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\" \"http://java.sun.com/dtd/web-app_2_3.dtd\">\n" +
		"<web-app>\n</web-app>";
	
	public static final String DEFAULT_WEBXML_SERVLET24 = 
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<web-app id=\"WebApp_ID\" version=\"2.4\" xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\">\n" +
	"</web-app>";
	
	protected IFolder configPath;

	// property change listeners
	private transient List propertyListeners;

	/**
	 * TomcatConfiguration constructor.
	 * 
	 * @param path a path
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
	 * @param tomcatDir Destination tomcat directory.  Equivalent to catalina.base
	 *                  for Tomcat 4.x and up.
	 * @param doBackup Backup existing configuration files (true if not test mode).
	 * @param monitor Progress monitor to use
	 * @return org.eclipse.core.runtime.IStatus
	 */
	protected IStatus backupAndPublish(IPath tomcatDir, boolean doBackup, IProgressMonitor monitor) {
		MultiStatus ms = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, Messages.publishConfigurationTask, null);
		Trace.trace(Trace.FINER, "Backup and publish");
		monitor = ProgressUtil.getMonitorFor(monitor);

		try {
			IPath backup = null;
			if (doBackup) {
				// create backup directory
				backup = tomcatDir.append("backup");
				if (!backup.toFile().exists())
					backup.toFile().mkdir();
			}
			backupFolder(getFolder(), tomcatDir.append("conf"), backup, ms, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "backupAndPublish() error", e);
			IStatus s = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
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
		monitor.beginTask(Messages.publishConfigurationTask, size * 100);
		for (int i = 0; i < size; i++) {
			if (children[i] instanceof IFile) {
				try {
					IFile file = (IFile) children[i];
					String name = file.getName();
					monitor.subTask(NLS.bind(Messages.publisherPublishTask, new String[] {name}));
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
					Trace.trace(Trace.SEVERE, "backupAndPublish() error", e);
					ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e));
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
		monitor.beginTask(Messages.publishConfigurationTask, size * 100);
		for (int i = 0; i < size; i++) {
			try {
				File file = files[i];
				String name = file.getName();
				monitor.subTask(NLS.bind(Messages.publisherPublishTask, new String[] {name}));
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
				Trace.trace(Trace.SEVERE, "backupAndPublish() error", e);
				ms.add(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e));
			}
			monitor.worked(100);
		}
	}
	
	protected IStatus publishContextConfig(IPath baseDir, IProgressMonitor monitor) {
		// Default implementation assumes nothing to do
		return Status.OK_STATUS;
	}

	
	protected IStatus cleanupServer(IPath confDir, IPath installDir, IProgressMonitor monitor) {
		// Default implementation assumes nothing to clean
		return Status.OK_STATUS;
	}
	
	protected IStatus prepareRuntimeDirectory(IPath confDir) {
		File temp = confDir.append("conf").toFile();
		if (!temp.exists())
			temp.mkdirs();

		return Status.OK_STATUS;		
	}
	
	public void localizeConfiguration(IPath path, TomcatServer server, IProgressMonitor monitor) {
		// do nothing
	}

	/**
	 * Returns the main server port.
	 * @return ServerPort
	 */
	public abstract ServerPort getMainPort();

	/**
	 * Returns the prefix that is used in front of the
	 * web module path property. (e.g. "webapps")
	 *
	 * @return java.lang.String
	 */
	public String getDocBasePrefix() {
		return "";
	}

	/**
	 * Returns the partial URL applicable to this module.
	 * 
	 * @param webModule a web module
	 * @return the partial URL
	 */
	protected String getWebModuleURL(IModule webModule) {
		WebModule module = getWebModule(webModule);
		if (module != null)
			return module.getPath();
		
		IWebModule webModule2 = (IWebModule) webModule.loadAdapter(IWebModule.class, null);
		return "/" + webModule2.getContextRoot();
	}

	/**
	 * Returns the given module from the config.
	 *
	 * @param module a web module
	 * @return a web module
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
					Trace.trace(Trace.SEVERE, "Error firing property change event", e);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error in property event", e);
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

	public void importFromPath(IPath path, boolean isTestEnv, IProgressMonitor monitor) throws CoreException {
		load(path, monitor);
	}

	/*public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor) throws CoreException {
		load(runtime.getLocation().append("conf"), monitor);
	}*/
	
	protected abstract void load(IPath path, IProgressMonitor monitor) throws CoreException;
	
	protected abstract void load(IFolder folder, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * @see ITomcatConfigurationWorkingCopy#addWebModule(int, ITomcatWebModule)
	 */
	public abstract void addWebModule(int index, ITomcatWebModule module);
	
	/**
	 * @see ITomcatConfigurationWorkingCopy#removeWebModule(int)
	 */
	public abstract void removeWebModule(int index);

	/**
	 * Gets the work directory for the server.
	 * 
	 * @param basePath path to server runtime directory
	 * @return path for the server's work directory
	 */
	public abstract IPath getServerWorkDirectory(IPath basePath);

	/**
	 * Gets the work directory for the specified module on the
	 * server.
	 * 
	 * @param basePath path to server runtime directory
	 * @param module a Tomcat web module
	 * @return path for the module's work directory on the server
	 */
	public abstract IPath getContextWorkDirectory(IPath basePath, ITomcatWebModule module);

	/**
	 * Return a string representation of this object.
	 * @return java.lang.String
	 */
	public String toString() {
		return "TomcatConfiguration[" + getFolder() + "]";
	}
}