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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.update.standalone.InstallCommand;
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
	 * @see IInstallableServer#install(IProgressMonitor)
	 */
	public void install(IProgressMonitor monitor) throws CoreException {
		String featureId = getFeatureId();
		String featureVersion = getFeatureVersion();
		String fromSite = getFromSite();
		
		if (featureId == null || featureVersion == null || fromSite == null)
			return;
		
		fromSite = InstallableRuntime.getMirror(fromSite, monitor);
		
		try {
			InstallCommand command = new InstallCommand(featureId, featureVersion, fromSite, null, "false");
			command.run(monitor);
			//command.applyChangesNow();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error installing feature", e);
		}
		
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// ignore
		}
	}

	public String toString() {
		return "InstallableServer[" + getId() + ", " + getName() + "]";
	}
}