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

import java.net.ConnectException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.update.core.IURLEntry;
import org.eclipse.update.standalone.InstallCommand;
import org.osgi.framework.Version;
/**
 * 
 */
public class InstallableServer implements IInstallableServer {
	private IConfigurationElement element;

	public InstallableServer(IConfigurationElement element) {
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

	/**
	 * 
	 * @return the name
	 */
	public String getName() {
		try {
			return element.getAttribute("name");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return the description
	 */
	public String getDescription() {
		try {
			return element.getAttribute("description");
		} catch (Exception e) {
			return null;
		}
	}

	public String getVendor() {
		try {
			String vendor = element.getAttribute("vendor");
			if (vendor != null)
				return vendor;
		} catch (Exception e) {
			// ignore
		}
		return Messages.defaultVendor;
	}

	public String getVersion() {
		try {
			String version = element.getAttribute("version");
			if (version != null)
				return version;
		} catch (Exception e) {
			// ignore
		}
		return Messages.defaultVersion;
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

	public String getFromSite() {
		try {
			return element.getAttribute("featureSite");
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/*
	 * @see IInstallableServer#getLicense(IProgressMonitor)
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
			if (featureId.equals(featureRefs[i].getVersionedIdentifier().getIdentifier()) && featureVersion.equals(featureRefs[i].getVersionedIdentifier().getVersion().toString())) {
				IFeature feature = featureRefs[i].getFeature(monitor);
				IURLEntry license = feature.getLicense();
				if (license != null)
					return license.getAnnotation();
				return null;
			}
		}
		return null;
	}

	public IFeature getFeature(IProgressMonitor monitor) {
		String featureId = getFeatureId();
		String featureVersion = getFeatureVersion();
		String fromSite = getFromSite();
		
		if (featureId == null || featureVersion == null || fromSite == null)
			return null;
		
		monitor.beginTask("Searching " + fromSite, 100);
		int mirror = 0;
		ISite site = InstallableRuntime.getSite(fromSite, ProgressUtil.getSubMonitorFor(monitor, 50));
		if (site == null)
			return null;
		fromSite = InstallableRuntime.getMirror(fromSite, site, mirror);
		
		Version latestVersion = new Version(featureVersion);
		ISiteFeatureReference last = null;
		try {
			ISiteFeatureReference[] features = site.getFeatureReferences();
			for (int i = 0; i < features.length; i++) {
				if (features[i].getVersionedIdentifier().getIdentifier().equals(featureId)) {
					Version nextCand = new Version(features[i].getVersionedIdentifier().getVersion().toString());
					if (nextCand.compareTo(latestVersion) >= 0) {
						latestVersion = nextCand;
						last = features[i];
					}
				}
			}
			if (last != null)
				return last.getFeature(ProgressUtil.getSubMonitorFor(monitor, 50));
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error searching for latest feature version", e);
		} finally {
			monitor.done();
		}
		return null;
	}

	/*
	 * @see IInstallableServer#install(IProgressMonitor)
	 */
	public void install(IProgressMonitor monitor) throws CoreException {
		String featureId = getFeatureId();
		String featureVersion = getFeatureVersion();
		String fromSite = getFromSite();
		
		if (featureId == null || featureVersion == null || fromSite == null)
			return;
		
		int mirror = 0;
		ISite site = InstallableRuntime.getSite(fromSite, monitor);
		fromSite = InstallableRuntime.getMirror(fromSite, site, mirror);
		featureVersion = getLatestVersion(site, featureVersion, featureId);
		
		boolean complete = false;
		while (!complete) {
			try {
				InstallCommand command = new InstallCommand(featureId, featureVersion, fromSite, null, "false");
				boolean b = command.run(monitor);
				if (!b)
					throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
							Messages.errorInstallingServerFeature, null));
				//command.applyChangesNow();
			} catch (ConnectException ce) {
				mirror++;
				fromSite = InstallableRuntime.getMirror(fromSite, site, mirror);
				if (fromSite == null)
					complete = true;
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error installing feature", e);
				throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
						NLS.bind(Messages.errorInstallingServer, e.getLocalizedMessage()), e));
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore
		}
	}

	public static String getLatestVersion(ISite site, String version, String featureId) {
		Version latestVersion = new Version(version);
		try {
			ISiteFeatureReference[] features = site.getFeatureReferences();
			for (int i = 0; i < features.length; i++) {
				if (features[i].getVersionedIdentifier().getIdentifier().equals(featureId)) {
					Version nextCand = new Version(features[i].getVersionedIdentifier().getVersion().toString());
					if (nextCand.compareTo(latestVersion) > 0) {
						latestVersion = nextCand;
					}
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error searching for latest feature version", e);
		}
		
		return latestVersion.toString();
	}

	public String toString() {
		return "InstallableServer[" + getId() + ", " + getName() + "]";
	}
}