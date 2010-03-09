/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.model;

import java.net.URI;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.engine.*;
import org.eclipse.equinox.p2.metadata.*;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.discovery.internal.Activator;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;
import org.osgi.framework.BundleContext;

public class Extension {
	private IInstallableUnit iu;
	private URI uri;

	private ProvisioningContext provContext;
	private IProvisioningPlan plan;

	public Extension(IInstallableUnit iu, URI uri) {
		this.iu = iu;
		this.uri = uri;
	}

	public String getName() {
		return iu.getProperty(IInstallableUnit.PROP_NAME, null);
	}

	public String getDescription() {
		return iu.getProperty(IInstallableUnit.PROP_DESCRIPTION, null);
	}

	public Image getImage() {
		// TODO no image support in p2 yet
		return null;
	}

	public String getLicense() {
		Collection<ILicense> licenses = iu.getLicenses(null);
		if (licenses == null || licenses.isEmpty())
			return "";
		// TODO support multiple licenses
		return licenses.iterator().next().getBody();
	}

	public String getProvider() {
		return iu.getProperty(IInstallableUnit.PROP_PROVIDER, null);
	}

	public String getId() {
		return iu.getId();
	}

	public Version getVersion() {
		return iu.getVersion();
	}

	public IStatus install(IProgressMonitor monitor) {
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		
		IProvisioningPlan plan = getProvisioningPlan(true, monitor);
		if (!plan.getStatus().isOK())
			return plan.getStatus();
		
		IEngine engine = (IEngine) ExtensionUtility.getService(bundleContext, IEngine.SERVICE_NAME);
		return engine.perform(plan, PhaseSetFactory.createDefaultPhaseSet(), monitor);
	}

	public IInstallableUnit[] getIUs() {
		return new IInstallableUnit[] { iu };
	}

	public IProvisioningPlan getProvisioningPlan(boolean explain, IProgressMonitor monitor) {
		if (plan != null)
			return plan;
		
		//long time = System.currentTimeMillis();
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		IPlanner planner = (IPlanner) ExtensionUtility.getService(bundleContext, IPlanner.SERVICE_NAME);
		
		IProfileRegistry profileRegistry = (IProfileRegistry) ExtensionUtility.getService(bundleContext, IProfileRegistry.SERVICE_NAME);
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		IProfileChangeRequest pcr = planner.createChangeRequest(profile);
		pcr.add(iu);
		provContext = new ProvisioningContext(new URI[] { uri });
		provContext.setArtifactRepositories(new URI[] { uri });
		if (!explain)
			provContext.setProperty("org.eclipse.equinox.p2.director.explain", "false");
		//provContext = new ProvisioningContext();
		plan = planner.getProvisioningPlan(pcr, provContext, monitor);
		//System.out.println("Time: " + (System.currentTimeMillis() - time)); // TODO
		return plan;
	}
}