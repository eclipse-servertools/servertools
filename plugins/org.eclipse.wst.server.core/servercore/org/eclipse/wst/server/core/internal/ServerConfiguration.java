/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.*;
import org.osgi.framework.Bundle;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.FileUtil;
/**
 * 
 */
public class ServerConfiguration extends Base implements IServerConfiguration {
	protected IServerConfigurationType configurationType;
	protected ServerConfigurationDelegate delegate;
	protected boolean isDataLoaded = false;
	
	// working copy, loaded resource
	public ServerConfiguration(IFile file) {
		super(file);
	}

	// creation (working copy)
	public ServerConfiguration(String id, IFile file, IServerConfigurationType type) {
		super(file, id);
		this.configurationType = type;
	}

	public ServerConfigurationDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate != null)
			return delegate;
		
		synchronized (this) {
			if (delegate == null) {
				try {
					long time = System.currentTimeMillis();
					ServerConfigurationType configType = (ServerConfigurationType) configurationType;
					delegate = (ServerConfigurationDelegate) configType.getElement().createExecutableExtension("class");
					delegate.initialize(this);
					loadData(monitor);
					Trace.trace(Trace.PERFORMANCE, "ServerConfiguration.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getServerConfigurationType().getId());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
				}
			}
		}
		return delegate;
	}
	
	/**
	 * Returns true if the delegate has been loaded.
	 * 
	 * @return
	 */
	public boolean isDelegateLoaded() {
		return delegate != null;
	}
	
	public void dispose() {
		if (delegate != null)
			delegate.dispose();
	}
	
	public boolean isDelegatePluginActivated() {
		IConfigurationElement element = ((ServerConfigurationType) configurationType).getElement();
		String pluginId = element.getDeclaringExtension().getNamespace();
		return Platform.getBundle(pluginId).getState() == Bundle.ACTIVE;
	}
	
	public IServerConfigurationWorkingCopy createWorkingCopy() {
		return new ServerConfigurationWorkingCopy(this); 
	}
	
	public void delete() throws CoreException {
		if (file != null) {
			file.delete(true, true, new NullProgressMonitor());
			if (getServerConfigurationType().isFolder()) {
				IFolder folder = getFolder(false);
				if (folder.exists())
					folder.delete(true, true, new NullProgressMonitor());
			}
		} else
			deleteFromMetadata();
	}
	
	public IServerConfigurationType getServerConfigurationType() {
		return configurationType;
	}

	public IFolder getConfigurationDataFolder() {
		if (file == null)
			return null;
		try {
			return getFolder(false);
		} catch (Exception e) {
			return null;
		}
	}
	
	public IPath getConfigurationDataPath() {
		if (file != null)
			return null;
		return getPath(false);
	}

	protected IFolder getFolder(boolean create) throws CoreException {
		IPath path = file.getProjectRelativePath();
		path = path.removeLastSegments(1).append(file.getName() + "-data");
		IFolder folder = file.getProject().getFolder(path);
		if (!folder.exists() && create)
			folder.create(true, true, new NullProgressMonitor());
		return folder;
	}
	
	protected IPath getPath(boolean create) {
		IPath path = ServerPlugin.getInstance().getStateLocation().append("configs");
		path = path.append(getId() + "-data");
		if (create) {
			File file2 = path.toFile();
			if (!file2.exists())
				file2.mkdirs();
		}
		return path;
	}
	
	protected void loadData(IProgressMonitor monitor) {
		if (isDataLoaded)
			return;
		isDataLoaded = true;
		if (!getServerConfigurationType().isFolder())
			return;

		try {
			if (file != null) {
				IFolder folder = getFolder(false);
				getDelegate(monitor).load(folder, new NullProgressMonitor());
			} else {
				IPath path = getPath(false);
				getDelegate(monitor).load(path, new NullProgressMonitor());
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load server configuration data", e);
		}
	}
	
	protected void saveData(boolean create, IProgressMonitor monitor) {
		if (!isDataLoaded || !getServerConfigurationType().isFolder())
			return;
		
		try {
			if (file != null) {
				IFolder folder = getFolder(create);
				getDelegate(monitor).save(folder, new NullProgressMonitor());
			} else {
				IPath path = getPath(create);
				getDelegate(monitor).save(path, new NullProgressMonitor());
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save server configuration data", e);
		}
	}

	protected void saveToFile(IProgressMonitor monitor) throws CoreException {
		super.saveToFile(monitor);
		
		saveData(true, monitor);
	}

	protected void loadFromFile(IProgressMonitor monitor) throws CoreException {
		super.loadFromFile(monitor);
		
		//loadData();
	}
	
	protected void deleteFromMetadata() {
		ResourceManager.getInstance().removeServerConfiguration(this);
		
		if (getServerConfigurationType().isFolder()) {
			try {
				IPath path = getPath(false);
				File file2 = path.toFile();
				if (file2.exists())
					FileUtil.deleteDirectory(file2, new NullProgressMonitor());
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not save server configuration", e);
			}
		}
	}

	protected void saveToMetadata(IProgressMonitor monitor) {
		super.saveToMetadata(monitor);
		ResourceManager.getInstance().addServerConfiguration(this);
		
		saveData(true, monitor);
	}
	
	protected String getXMLRoot() {
		return "server-configuration";
	}
	
	protected void setInternal(ServerConfigurationWorkingCopy wc) {
		map = wc.map;
		configurationType = wc.configurationType;
		isDataLoaded = false; //wc.isDataLoaded; let the wc save it
		delegate = wc.delegate;
		
		int timestamp = wc.getTimestamp();
		map.put("timestamp", Integer.toString(timestamp+1));
	}
	
	protected void loadFromMemento(IMemento memento, IProgressMonitor monitor) {
		super.loadFromMemento(memento, monitor);
		
		//loadData();
	}
	
	protected void loadState(IMemento memento) {
		String serverTypeId = memento.getString("server-configuration-type-id");
		configurationType = ServerCore.getServerConfigurationType(serverTypeId);
	}
	
	protected void saveState(IMemento memento) {
		if (configurationType != null)
			memento.putString("server-configuration-type-id", configurationType.getId());
	}
	
	public IStatus validateEdit(Object context) {
		if (file == null)
			return null;
		
		// TODO
		return file.getWorkspace().validateEdit(new IFile[] { file }, context);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		ServerConfigurationDelegate delegate2 = getDelegate(null);
		if (adapter.isInstance(delegate2))
			return delegate;
		return null;
	}
}