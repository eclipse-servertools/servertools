/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Version;

public interface IExtension {
	public String getId();

	public String getName();

	public String getProvider();

	public Version getVersion();

	public String getDescription();
	
	public Image getImage();
	
	public String getLicense();
	
	public IInstallableUnit[] getIUs();
	
	public ProvisioningPlan getProvisioningPlan(IProgressMonitor monitor);
	
	public IStatus install(IProgressMonitor monitor);
}