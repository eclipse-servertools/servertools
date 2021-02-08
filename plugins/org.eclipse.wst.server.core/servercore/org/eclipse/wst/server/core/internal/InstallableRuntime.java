/*******************************************************************************
 * Copyright (c) 2005, 2021 IBM Corporation and others.
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

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;
/**
 * @deprecated (since 3.3) 
 * The support for InstallableRuntime has been moved to org.eclipse.wst.server.discovery
 * and is now supported through the p2 repository lookup APIs
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

	public String getName() {
		return getPath() + "";
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

	public String getArchivePath() {
		try {
			return element.getAttribute("archivePath");
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

	public String toString() {
		return "InstallableRuntime[" + getId() + "]";
	}

	public String getLicense(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void install(IPath path, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}
}