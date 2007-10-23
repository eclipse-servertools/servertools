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
package org.eclipse.wst.server.core.internal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.internal.tar.TarEntry;
import org.eclipse.wst.server.core.internal.tar.TarInputStream;
/**
 * 
 */
public class InstallableRuntime2 implements IInstallableRuntime {
	private IConfigurationElement element;

	public InstallableRuntime2(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 * @return the id
	 */
	public String getId() {
		try {
			return element.getAttribute("id");
		} catch (Exception e) {
			return null;
		}
	}

	public String getURL() {
		try {
			return element.getAttribute("url");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public String getLicenseURL() {
		try {
			return element.getAttribute("licenseUrl");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/*
	 * @see IInstallableRuntime#getLicense(IProgressMonitor)
	 */
	public String getLicense(IProgressMonitor monitor) throws CoreException {
		URL url = null;
		try {
			url = new URL(getLicenseURL());
			InputStream in = url.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			copy(buf, in, out);
			return new String(out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/*
	 * @see IInstallableRuntime#install(IPath)
	 */
	public void install(final IPath path) {
		Job installRuntimeJob = new Job(Messages.jobInstallingRuntime) {
			public boolean belongsTo(Object family) {
				return ServerPlugin.PLUGIN_ID.equals(family);
			}
			
			protected IStatus run(IProgressMonitor monitor) {
				try {
					install(path, monitor);
				} catch (CoreException ce) {
					return ce.getStatus();
				}
				
				return Status.OK_STATUS;
			}
		};
		
		installRuntimeJob.schedule();
	}

	private void copy(byte[] buf, InputStream in, OutputStream out) throws IOException {
		int r = in.read(buf);
		while (r >= 0) {
			out.write(buf, 0, r);
			r = in.read(buf);
		}
	}

	/*
	 * @see IInstallableRuntime#install(IPath, IProgressMonitor)
	 */
	public void install(IPath path, IProgressMonitor monitor) throws CoreException {
		URL url = null;
		File temp = null;
		try {
			url = new URL(getURL());
			temp = File.createTempFile("runtime", "");
			temp.deleteOnExit();
		} catch (IOException e) {
			throw new CoreException(null);
		}
		String name = url.getPath();
		
		// download
		try {
			InputStream in = url.openStream();
			FileOutputStream fout = new FileOutputStream(temp);
			byte[] buf = new byte[8192];
			copy(buf, in, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			FileInputStream in = new FileInputStream(temp);
			if (name.endsWith("zip"))
				unzip(in, path, monitor);
			else if (name.endsWith("tar"))
				untar(in, path, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error uncompressing runtime", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
					NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		}
	}

	private void unzip(InputStream in, IPath path, IProgressMonitor monitor) throws IOException {
		// unzip from bundle into path
		BufferedInputStream bin = new BufferedInputStream(in);
		ZipInputStream zin = new ZipInputStream(bin);
		ZipEntry entry = zin.getNextEntry();
		byte[] buf = new byte[8192];
		while (entry != null) {
			String name = entry.getName();
			monitor.setTaskName("Unzipping: " + name);
			
			if (entry.isDirectory())
				path.append(name).toFile().mkdirs();
			else {
				FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
				copy(buf, zin, fout);
			}
			zin.closeEntry();
			entry = zin.getNextEntry();
		}
		zin.close();
	}

	protected void untar(InputStream in, IPath path, IProgressMonitor monitor) throws IOException {
		// untar from bundle into path
		BufferedInputStream bin = new BufferedInputStream(in);
		TarInputStream zin = new TarInputStream(bin);
		TarEntry entry = zin.getNextEntry();
		byte[] buf = new byte[8192];
		while (entry != null) {
			String name = entry.getName();
			monitor.setTaskName("Untarring: " + name);
			
			if (entry.getFileType() == TarEntry.DIRECTORY)
				path.append(name).toFile().mkdirs();
			else {
				FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
				copy(buf, zin, fout);
			}
			zin.close();
			entry = zin.getNextEntry();
		}
		zin.close();
	}

	public String toString() {
		return "InstallableRuntime2[" + getId() + "]";
	}
}