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
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.j2ee.IWebModule;

import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.resources.IModuleFile;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteResource;
import org.eclipse.wst.server.core.resources.RemoteFolder;
import org.eclipse.wst.server.core.resources.RemoteResource;
import org.eclipse.wst.server.core.util.FileUtil;
import org.eclipse.wst.server.core.util.ProgressUtil;
/**
 * The Tomcat publisher for local (out of the workbench) resources.
 */
public class TomcatWebModulePublisher implements IPublisher {
	protected IWebModule module;
	protected IPath installDir;

	protected static final Status publishStatus = new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "Published successfully", null);
	protected static final Status deleteStatus = new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "Deleted successfully", null);

	/**
	 * TomcatWebModulePublisher constructor comment.
	 */
	public TomcatWebModulePublisher(IWebModule module, IPath installDir) {
		super();
		this.module = module;
		this.installDir = installDir;
	}

	/**
	 * Delete the following files from the remote machine.
	 *
	 * @param file java.lang.String[]
	 * @return org.eclipse.core.runtime.IStatus[]
	 */
	public IStatus[] delete(IRemoteResource[] resource, IProgressMonitor monitor) {
		if (resource == null)
			return null;

		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(TomcatPlugin.getResource("%publishTask"), resource.length);

		IPath root = getRemoteRoot().append(module.getContextRoot());

		int size = resource.length;
		IStatus[] status = new Status[size];
		for (int i = 0; i < size; i++) {
			if (monitor.isCanceled())
				return status;

			IPath path = resource[i].getPath();
			IPath realPath = root.append(path);
			File file = new File(realPath.toOSString());

			monitor.subTask(TomcatPlugin.getResource("%publisherDeleteTask", new String[] {path.toString()}));
			Trace.trace("Deleting " + realPath);
			boolean b = file.delete();
			if (b)
				status[i] = new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "Delete " + realPath.toOSString() + " successfully", null);
			else
				status[i] = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, "Could not delete " + realPath.toOSString(), null);

			monitor.worked(1);
		}
		monitor.done();
		return status;
	}

	/**
	 * Returns the mapping of this file on the remote
	 * system. Return null if this file should not be
	 * copied to the remote server.
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 * @return org.eclipse.core.resources.IPath
	 */
	public IPath getMappedLocation(IModuleResource resource) {
		return resource.getPath();
	}

	/**
	 * Recursively builds a directory tree of the remote resources.
	 *
	 * @param path org.eclipse.core.runtime.IPath
	 * @return java.util.List
	 */
	protected IRemoteResource[] getRemoteResources(RemoteFolder parent, File dir) {
		List list = new ArrayList();

		if (!dir.exists())
			return null;

		if (!dir.isDirectory())
			return null;

		File[] files = dir.listFiles();
		if (files == null)
			return null;

		int size = files.length;
		for (int i = 0; i < size; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				RemoteFolder folder = new RemoteFolder(parent, file.getName(), file.lastModified());
				if (parent != null)
					parent.addChild(folder);
				getRemoteResources(folder, file);
				list.add(folder);
			} else {
				IRemoteResource remote = new RemoteResource(parent, file.getName(), file.lastModified());
				if (parent != null)
					parent.addChild(remote);
				list.add(remote);
			}
		}

		IRemoteResource[] resources = new IRemoteResource[list.size()];
		list.toArray(resources);
		return resources;
	}

	/**
	 * Returns a list of the remote resources at the root level.
	 * These may be folders or resources. This method should not
	 * return cached data.
	 *
	 * @return java.util.List
	 */
	public IRemoteResource[] getRemoteResources(IProgressMonitor monitor) {
		File rootFile = getRemoteRoot().append(module.getContextRoot()).toFile();
		if (rootFile.exists()) {
			return getRemoteResources(null, rootFile);
		}
		return new IRemoteResource[0];
	}

	/**
	 * Returns the root of the remote publishing location.
	 *
	 * @return org.eclipse.core.runtime.IPath
	 */
	protected IPath getRemoteRoot() {
		return installDir.append("webapps");
	}

	/**
	 * Returns true if there may be any files or folders within
	 * this container that should be mapped to the remote system.
	 * Returns false if files within this folder are never copied
	 * to the remote system.
	 *
	 * @param container org.eclipse.core.resources.IContainer
	 * @return boolean
	 */
	public boolean shouldMapMembers(IModuleFolder folder) {
		return true;
	}

	/**
	 * Publish the given files to the given location on the
	 * remote machine.
	 *
	 * @param file java.lang.String[]
	 * @return org.eclipse.core.runtime.IStatus[]
	 */
	public IStatus[] publish(IModuleResource[] resource, IProgressMonitor monitor) {
		if (resource == null)
			return null;

		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask(TomcatPlugin.getResource("%publishTask"), resource.length);

		IPath root = getRemoteRoot().append(module.getContextRoot());
	
		// create context root directory if necessary
		File temp = root.toFile();
		if (!temp.exists())
			temp.mkdirs();

		int size = resource.length;
		IStatus[] status = new Status[size];
		for (int i = 0; i < size; i++) {
			if (monitor.isCanceled())
				return status;

			//IPath fromPath = resource[i].getLocation();
			IPath toPath = getMappedLocation(resource[i]);
			IPath realToPath = root.append(toPath);

			// copy file
			monitor.subTask(TomcatPlugin.getResource("%publisherPublishTask", new String[] {toPath.toString()}));
			Trace.trace("Publishing " + resource[i] + " -> " + realToPath);
			if (resource[i] instanceof IModuleFolder) {
				File f = new File(realToPath.toOSString());
				if (f.exists() && f.isDirectory()) {
					status[i] = new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "Directory " + realToPath.toOSString() + " already exists", null);
				} else {
					boolean b = f.mkdir();

					if (b)
						status[i] = new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, "Created " + realToPath.toOSString() + " successfully", null);
					else
						status[i] = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, "Could not create directory " + realToPath.toOSString(), null);
				}
			} else if (resource[i] instanceof IModuleFile) {
				IModuleFile file = (IModuleFile) resource[i];
				InputStream in = null;
				try {
					in = file.getContents();
					status[i] = FileUtil.copyFile(in, realToPath.toOSString());
				} catch (Exception e) {
					// FIX-ME
				} finally {
					if (in != null)
						try {
							in.close();
						} catch (Exception e) { }
				}
			}
			monitor.worked(1);
		}

		monitor.done();
		return status;
	}
	
	/**
	 * Delete the entire module from the remote location.
	 *
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus deleteAll(IProgressMonitor monitor) {
		IPath path = getRemoteRoot().append(module.getContextRoot());

		FileUtil.deleteDirectory(path.toFile(), monitor);
		return new Status(IStatus.OK, TomcatPlugin.PLUGIN_ID, 0, TomcatPlugin.getResource("%projectCleanupSuccess", module.getContextRoot()), null);
	}
}
