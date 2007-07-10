/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.update.core.*;
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

	public String getBundleVersion() {
		try {
			return element.getAttribute("bundleVersion");
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
	 * @see IInstallableRuntime#getLicense(IProgressMonitor)
	 */
	public String getLicense(IProgressMonitor monitor) throws CoreException {
		String featureId = getFeatureId();
		String featureVersion = getFeatureVersion();
		String fromSite = getFromSite();
		
		if (featureId == null || featureVersion == null || fromSite == null)
			return null;
		
		ISite site = InstallableRuntime.getSite(fromSite, monitor);
		ISiteFeatureReference[] featureRefs = site.getFeatureReferences();
		for (int i = 0; i < featureRefs.length; i++) {
			String ver = featureRefs[i].getVersionedIdentifier().toString();
			int ind = ver.indexOf("_");
			if (ind >= 0)
				ver = ver.substring(ind+1);
			if (featureId.equals(featureRefs[i].getVersionedIdentifier().getIdentifier()) && featureVersion.equals(ver)) {
				IFeature feature = featureRefs[i].getFeature(monitor);
				IURLEntry license = feature.getLicense();
				if (license != null)
					return license.getAnnotation();
				return null;
			}
		}
		return null;
	}

	protected Bundle getBundleVersion(Bundle[] bundles, String version) {
		if (bundles == null)
			return null;
		
		int size = bundles.length;
		return bundles[size - 1];
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

	public static ISite getSite(String fromSite, IProgressMonitor monitor) {
		try {
			URL siteURL = new URL(fromSite);
			return SiteManager.getSite(siteURL, monitor);
		} catch (MalformedURLException e) {
			Trace.trace(Trace.WARNING, "Could not parse site", e);
		} catch (CoreException e) {
			Trace.trace(Trace.WARNING, "Could not parse site", e);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not parse site", e);
		}
		return null;
	}

	protected static String getMirror(String fromSite, ISite site, int mirror) {
		if (site != null) {
			String mirrorSite = getMirror(site, mirror);
			if (mirrorSite != null)
				return mirrorSite;
		}
		// only return fromSite if this is the 0th mirror
		if (mirror > 0)
			return null;
		return fromSite;
	}

	protected static String getMirror(ISite site, int mirror) {
		// if the site is a site containing mirrors, set the fromSite to the
		// mirrors in order site since many mirror list generators will sort the mirrors
		// to closest geographic location
		if (site != null && site instanceof ISiteWithMirrors) {
			try {
				IURLEntry[] urlEntries = ((ISiteWithMirrors) site).getMirrorSiteEntries();
				if (urlEntries.length > mirror)
					return urlEntries[mirror].getURL().toExternalForm();
			} catch (CoreException e) {
				Trace.trace(Trace.WARNING, "Could not find mirror site", e);
			}
		}
		return null;
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
		
		int mirror = 0;
		ISite site = getSite(fromSite, monitor);
		fromSite = getMirror(fromSite, site, mirror);
		
		boolean install = false;
		if (getBundleId() != null) {
			install = Platform.getBundles(getBundleId(), getBundleVersion()) == null;
		} else if (getPath() != null) {
			install = !new File(getFeatureArchivePath()).exists();
		}
		
		// download and install plugins
		if (install) {
			boolean complete = false;
			while (!complete) {
				try {
					monitor.setTaskName("Installing feature");
					InstallCommand command = new InstallCommand(featureId, featureVersion, fromSite, null, "false");
					boolean b = command.run(monitor);
					if (!b)
						throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
								Messages.errorInstallingServerFeature, null));
					command.applyChangesNow();
					complete = true;
				} catch (ConnectException ce) {
					mirror++;
					fromSite = getMirror(fromSite, site, mirror);
					if (fromSite == null)
						complete = true;
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error installing feature", e);
					throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
							NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
				}
			}
		}
		
		try {
			URL url = null;
			if (getBundleId() != null) {
				Bundle[] bundles = Platform.getBundles(getBundleId(), getBundleVersion());
				Bundle bundle = getBundleVersion(bundles, getBundleVersion());
				url = bundle.getEntry(getPath());
				url = FileLocator.resolve(url);
			} else {
				// data archive used so get the url of the runtime archive from inside the feature
				url = new File(getFeatureArchivePath()).toURL();
			}
			
			// unzip from bundle into path
			InputStream in = url.openStream();
			unzip(in, path, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error unzipping runtime", e);
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
	}

	private String getFeatureArchivePath() {
		String feature = getFeatureId() + "_" + getFeatureVersion();
		String platformLoc = Platform.getInstallLocation().getURL().getFile();
		return platformLoc.concat(File.separator + Site.DEFAULT_INSTALLED_FEATURE_PATH + feature + File.separator + getPath());
	}

	public String toString() {
		return "InstallableRuntime[" + getId() + "]";
	}
}