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
package org.eclipse.wst.server.core.internal;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
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

	// Default sizes (infinite logarithmic progress will be used when default is employed)
	private int DEFAULT_DOWNLOAD_SIZE = 10000000;
	private int DEFAULT_FILE_COUNT = 1000;

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

	public int getArchiveSize() {
		try {
			String size = element.getAttribute("archiveSize");
			return Integer.parseInt(size);
		} catch (Exception e) {
			// ignore
		}
		return -1;
	}

	public int getFileCount() {
		try {
			String size = element.getAttribute("fileCount");
			return Integer.parseInt(size);
		} catch (Exception e) {
			// ignore
		}
		return -1;
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
			URLConnection connection = url.openConnection();
			String possibleNewURL = connection.getHeaderField("Location");
			while (possibleNewURL != null) {
				connection = new URL(possibleNewURL).openConnection();
				possibleNewURL = connection.getHeaderField("Location");
			}
			InputStream in = connection.getInputStream();
			out = new ByteArrayOutputStream();
			copyWithSize(in, out, null, 0);
			return new String(out.toByteArray());
		} catch (Exception e) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Error loading license", e);
			}
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

	private void copyWithSize(InputStream in, OutputStream out, IProgressMonitor monitor, int size) throws IOException {
		if (BUFFER == null)
			BUFFER = new byte[8192];
		SubMonitor progress = SubMonitor.convert(monitor, size);
		int r = in.read(BUFFER);
		while (r >= 0) {
			out.write(BUFFER, 0, r);
			progress.worked(r);
			r = in.read(BUFFER);
		}
	}

	private void download(InputStream in, OutputStream out, IProgressMonitor monitor, String name, int size) throws IOException {
		if (BUFFER == null)
			BUFFER = new byte[8192];
		
		String msg = NLS.bind((size > 0) ? Messages.taskDownloadSizeKnown : Messages.taskDownloadSizeUnknown,
				new Object [] { name, "{0}", Integer.toString(size / 1024) });
		SubMonitor progress = SubMonitor.convert(monitor, NLS.bind(msg, "0"), (size > 0) ? size : DEFAULT_DOWNLOAD_SIZE);
		
		int r = in.read(BUFFER);
		int total = 0;
		int lastTotal = 0;
		while (r >= 0) {
			out.write(BUFFER, 0, r);
			total += r;
			if (total >= lastTotal + 8192) {
				lastTotal = total;
				progress.subTask(NLS.bind(msg, Integer.toString(lastTotal / 1024)));
			}
			progress.worked(r);
			// if size is not known, use infinite logarithmic progress
			if (size <= 0)
				progress.setWorkRemaining(DEFAULT_DOWNLOAD_SIZE);
			
			if (progress.isCanceled())
				break;
			r = in.read(BUFFER);
		}
	}

	/*
	 * @see IInstallableRuntime#install(IPath, IProgressMonitor)
	 */
	public void install(IPath path, IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 1000);
		URL url = null;
		File temp = null;
		try {
			url = new URL(getArchiveUrl());
			temp = File.createTempFile("runtime", "");
			temp.deleteOnExit();
		} catch (IOException e) {
			if (monitor != null)
				monitor.done();
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Error creating url and temp file", e);
			}
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		}
		String name = (url.getQuery() != null) ? url.getQuery() : url.getPath();
		int slashIdx = name.lastIndexOf('/');
		if (slashIdx >= 0)
			name = name.substring(slashIdx + 1);
		
		int archiveSize = getArchiveSize();
		
		// download
		FileOutputStream fout = null;
		try {
			InputStream in = url.openStream();
			fout = new FileOutputStream(temp);
			download(in, fout, progress.newChild(500), name, archiveSize);
			progress.setWorkRemaining(500);
		} catch (Exception e) {
			if (monitor != null)
				monitor.done();
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Error downloading runtime", e);
			}
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
		if (progress.isCanceled())
			throw new CoreException(Status.CANCEL_STATUS);
		
		FileInputStream in = null;
		try {
			in = new FileInputStream(temp);
			if (name.endsWith("zip"))
				unzip(in, path, progress.newChild(500));
			else if (name.endsWith("tar"))
				untar(in, path, progress.newChild(500));
			else if (name.endsWith("tar.gz")) {
				File tarFile = File.createTempFile("runtime", ".tar");
				tarFile.deleteOnExit();
				String tarName = name;
				if (slashIdx >= 0)
					tarName = name.substring(0, name.length() - 3);
				
				progress.subTask(NLS.bind(Messages.taskUncompressing, tarName));
				int tempSize = Integer.MAX_VALUE;
				if (temp.length() < Integer.MAX_VALUE)
					tempSize = (int)temp.length();
				
				ungzip(in, tarFile, progress.newChild(250), tempSize);
				progress.setWorkRemaining(250);
				if (!progress.isCanceled()) {
					in = new FileInputStream(tarFile);
					untar(in, path, progress.newChild(250));
				}
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error uncompressing runtime", e);
			}
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				// ignore
			}
			progress.done();
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
		int fileCnt = getFileCount();
		SubMonitor progress = SubMonitor.convert(monitor, (fileCnt > 0) ? fileCnt : DEFAULT_FILE_COUNT);
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		ZipInputStream zin = new ZipInputStream(bin);
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			progress.subTask(NLS.bind(Messages.taskUncompressing, name));
			
			if (name != null && name.length() > 0) {
				if (entry.isDirectory())
					path.append(name).toFile().mkdirs();
				else {
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					copyWithSize(zin, fout, progress.newChild(1), (int)entry.getSize());
					fout.close();
					// if count is not known, use infinite logarithmic progress
					if (fileCnt <= 0)
						progress.setWorkRemaining(DEFAULT_FILE_COUNT);
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
		int fileCnt = getFileCount();
		SubMonitor progress = SubMonitor.convert(monitor, (fileCnt > 0) ? fileCnt : 500);
		String archivePath = getArchivePath();
		BufferedInputStream bin = new BufferedInputStream(in);
		TarInputStream zin = new TarInputStream(bin);
		TarEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			progress.subTask(NLS.bind(Messages.taskUncompressing, name));
			
			if (name != null && name.length() > 0) {
				if (entry.getFileType() == TarEntry.DIRECTORY)
					path.append(name).toFile().mkdirs();
				else {
					File dir = path.append(name).removeLastSegments(1).toFile();
					if (!dir.exists())
						dir.mkdirs();
					
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					copyWithSize(zin, fout, progress.newChild(1), (int)entry.getSize());
					fout.close();
					if (fileCnt <= 0)
						progress.setWorkRemaining(500);
				}
			}
			entry = zin.getNextEntry();
		}
		zin.close();
	}

	protected void ungzip(InputStream in, File tarFile, IProgressMonitor monitor, int size) throws IOException {
		GZIPInputStream gzin = null;
		FileOutputStream fout = null;
		try {
			gzin = new GZIPInputStream(in);
			fout = new FileOutputStream(tarFile);
			copyWithSize(gzin, fout, monitor, size);
		} finally {
			if (gzin != null) {
				try {
					gzin.close();
				} catch (IOException e) {
					// ignore
				}
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException e) {
						// ignore
					}
				}
			}
		}
	}

	public String toString() {
		return "InstallableRuntime2[" + getId() + "]";
	}
}