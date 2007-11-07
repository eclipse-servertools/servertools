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
	private byte[] BUFFER = null;

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

	public String getName() {
		return getArchivePath();
	}

	public String getArchiveUrl() {
		try {
			return element.getAttribute("archiveUrl");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public String getArchivePath() {
		try {
			return element.getAttribute("archivePath");
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
		ByteArrayOutputStream out = null;
		try {
			String licenseURL = getLicenseURL();
			if (licenseURL == null)
				return null;
			
			url = new URL(licenseURL);
			InputStream in = url.openStream();
			out = new ByteArrayOutputStream();
			copy(in, out);
			return new String(out.toByteArray());
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error loading license", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
					NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				// ignore
			}
		}
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

	private void copy(InputStream in, OutputStream out) throws IOException {
		if (BUFFER == null)
			BUFFER = new byte[8192];
		int r = in.read(BUFFER);
		while (r >= 0) {
			out.write(BUFFER, 0, r);
			r = in.read(BUFFER);
		}
	}

	/*
	 * @see IInstallableRuntime#install(IPath, IProgressMonitor)
	 */
	public void install(IPath path, IProgressMonitor monitor) throws CoreException {
		URL url = null;
		File temp = null;
		try {
			url = new URL(getArchiveUrl());
			temp = File.createTempFile("runtime", "");
			temp.deleteOnExit();
		} catch (IOException e) {
			Trace.trace(Trace.WARNING, "Error creating url and temp file", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		}
		String name = url.getPath();
		
		// download
		FileOutputStream fout = null;
		try {
			InputStream in = url.openStream();
			fout = new FileOutputStream(temp);
			copy(in, fout);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error downloading runtime", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (fout != null)
					fout.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		FileInputStream in = null;
		try {
			in = new FileInputStream(temp);
			if (name.endsWith("zip"))
				unzip(in, path, monitor);
			else if (name.endsWith("tar"))
				untar(in, path, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error uncompressing runtime", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Unzip the input stream into the given path.
	 * 
	 * @param in
	 * @param path
	 * @param monitor
	 * @throws IOException
	 */
	private void unzip(InputStream in, IPath path, IProgressMonitor monitor) throws IOException {
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		ZipInputStream zin = new ZipInputStream(bin);
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			monitor.setTaskName("Unzipping: " + name);
			if (archivePath != null && name.startsWith(archivePath)) {
				name = name.substring(archivePath.length());
				if (name.length() > 1)
					name = name.substring(1);
			}
			
			if (name != null && name.length() > 0) {
				if (entry.isDirectory())
					path.append(name).toFile().mkdirs();
				else {
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					copy(zin, fout);
					fout.close();
				}
			}
			zin.closeEntry();
			entry = zin.getNextEntry();
		}
		zin.close();
	}

	/**
	 * Untar the input stream into the given path.
	 * 
	 * @param in
	 * @param path
	 * @param monitor
	 * @throws IOException
	 */
	protected void untar(InputStream in, IPath path, IProgressMonitor monitor) throws IOException {
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		TarInputStream zin = new TarInputStream(bin);
		TarEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			monitor.setTaskName("Untarring: " + name);
			if (archivePath != null && name.startsWith(archivePath)) {
				name = name.substring(archivePath.length());
				if (name.length() > 1)
					name = name.substring(1);
			}
			
			if (name != null && name.length() > 0) {
				if (entry.getFileType() == TarEntry.DIRECTORY)
					path.append(name).toFile().mkdirs();
				else {
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					copy(zin, fout);
					fout.close();
				}
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