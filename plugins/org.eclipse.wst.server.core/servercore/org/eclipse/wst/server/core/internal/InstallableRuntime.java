/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteWithMirrors;
import org.eclipse.update.core.IURLEntry;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.standalone.InstallCommand;
import org.osgi.framework.Bundle;
/**
 * 
 */
public class InstallableRuntime implements IInstallableRuntime {
	private IConfigurationElement element;

	public InstallableRuntime(IConfigurationElement element) {
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

	public String getFeatureVersion() {
		try {
			return element.getAttribute("featureVersion");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public String getFeatureId() {
		try {
			return element.getAttribute("featureId");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public String getBundleId() {
		try {
			return element.getAttribute("bundleId");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public String getPath() {
		try {
			return element.getAttribute("path");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public String getFromSite() {
		try {
			return element.getAttribute("featureSite");
		} catch (Exception e) {
			// ignore
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

	public static String getMirror(String fromSite, IProgressMonitor monitor) {
		//	 if the site is a site containing mirrors, set the fromSite to the first
		// mirror site since many mirror list generators will sort the mirrors to closest
		// geographic location
		String mirrorSite = null;
		try {
			URL siteURL = new URL(fromSite);
			ISite site = SiteManager.getSite(siteURL, monitor);
			if (site != null && site instanceof ISiteWithMirrors) {
				IURLEntry[] urlEntries = ((ISiteWithMirrors) site).getMirrorSiteEntries();
				if (urlEntries.length > 0)
					mirrorSite = urlEntries[0].getURL().toExternalForm();
			}
		} catch (MalformedURLException e) {
			Trace.trace(Trace.WARNING, "Could not find mirror site", e);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Could not find mirror site", e);
		}
		
		if (mirrorSite != null) 
			return mirrorSite;
		return fromSite;
	}

	/*
	 * @see IInstallableRuntime#install(IPath, IProgressMonitor)
	 */
	public void install(IPath path, IProgressMonitor monitor) throws CoreException {
		String featureId = getFeatureId();
		String featureVersion = getFeatureVersion();
		String fromSite = getFromSite();
		
		if (featureId == null || featureVersion == null || fromSite == null)
			return;
		
		fromSite = getMirror(fromSite, monitor);
		
		// download and install plugins
		Bundle bundle = Platform.getBundle(getBundleId());
		if (bundle == null) {
			try {
				monitor.setTaskName("Installing feature");
				InstallCommand command = new InstallCommand(featureId, featureVersion, fromSite, null, "false");
				command.run(monitor);
				command.applyChangesNow();
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error installing feature", e);
				return;
			}
		}
		
		// unzip from bundle into path
		try {
			byte[] buf = new byte[8192];
			bundle = Platform.getBundle(getBundleId());
			URL url = bundle.getEntry(getPath());
			url = Platform.resolve(url);
			InputStream in = url.openStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			ZipInputStream zin = new ZipInputStream(bin);
			ZipEntry entry = zin.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				monitor.setTaskName("Unzipping: " + name);
				
				if (entry.isDirectory()) {
					path.append(name).toFile().mkdirs();
				} else {
					FileOutputStream fout = new FileOutputStream(path.append(name).toFile());
					int r = zin.read(buf);
					while (r >= 0) {
						fout.write(buf, 0, r);
						r = zin.read(buf);
					}
				}
				zin.closeEntry();
				entry = zin.getNextEntry();
			}
			zin.close();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error installing feature", e);
		} 
	}

	public String toString() {
		return "InstallableRuntime[" + getId() + "]";
	}
}