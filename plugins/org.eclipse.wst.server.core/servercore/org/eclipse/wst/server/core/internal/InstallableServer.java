/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
import org.eclipse.core.runtime.Platform;
//import org.eclipse.update.standalone.InstallCommand;
import org.osgi.framework.Bundle;
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
		
		try {
			/*InstallCommand command = new InstallCommand(featureId, featureVersion, fromSite, null, "false");
			command.run(monitor);
			EnableCommand command2 = new EnableCommand(featureId, featureVersion, null, "false");
			command2.run(monitor);*/
			String id = "org.eclipse.jst.server.timcat.core";
			Bundle b = Platform.getBundle(id);
			/*if (b == null) {
				//id = "initial@reference:file:plugins/org.eclipse.jst.server.timcat.core_1.0.0/";
				id = "file:/D:/dev/wtp/eclipse/plugins/org.eclipse.jst.server.timcat.core_1.0.0/";
				b = ServerPlugin.bundleContext.installBundle(id);
			}*/
			System.out.println("state: " + b.getState());
			b.start();
			System.out.println("state2: " + b.getState());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error installing feature", e);
		}
	}

	public String toString() {
		return "InstallableServer[" + getId() + ", " + getName() + "]";
	}
}