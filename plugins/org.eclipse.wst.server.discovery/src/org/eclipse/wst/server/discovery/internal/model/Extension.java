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

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.equinox.internal.provisional.p2.director.IPlanner;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.engine.DefaultPhaseSet;
import org.eclipse.equinox.internal.provisional.p2.engine.IEngine;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.engine.ProvisioningContext;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui.IUPropertyUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.discovery.internal.Activator;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;
import org.osgi.framework.BundleContext;

public class Extension {
	private ImageDescriptor imageDescriptor;
	private IInstallableUnit iu;
	private URI uri;

	private ProvisioningContext provContext;
	private ProvisioningPlan plan;

	public Extension(IInstallableUnit iu, URI uri) {
		this.iu = iu;
		this.uri = uri;
	}

	public String getName() {
		return IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_NAME);
	}

	public String getDescription() {
		return IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_DESCRIPTION);
	}

	public Image getImage() {
		// TODO no image support in p2 yet
		return null;
	}

	public String getLicense() {
		return iu.getLicense().getBody();
	}

	public String getProvider() {
		return IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_PROVIDER);
	}

	public String getId() {
		return iu.getId();
	}

	public Version getVersion() {
		return iu.getVersion();
	}

	public IStatus install(IProgressMonitor monitor) {
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		
		ProvisioningPlan plan = getProvisioningPlan(true, monitor);
		if (!plan.getStatus().isOK())
			return plan.getStatus();
		
		IProfileRegistry profileRegistry = (IProfileRegistry) ExtensionUtility.getService(bundleContext, IProfileRegistry.class.getName());
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		
		IEngine engine = (IEngine) ExtensionUtility.getService(bundleContext, IEngine.SERVICE_NAME);
		return engine.perform(profile, new DefaultPhaseSet(), plan.getOperands(), provContext, monitor);
	}

	public IInstallableUnit[] getIUs() {
		return new IInstallableUnit[] { iu };
	}

	public ProvisioningPlan getProvisioningPlan(boolean explain, IProgressMonitor monitor) {
		if (plan != null)
			return plan;
		
		//long time = System.currentTimeMillis();
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		IPlanner planner = (IPlanner) ExtensionUtility.getService(bundleContext, IPlanner.class.getName());
		
		IProfileRegistry profileRegistry = (IProfileRegistry) ExtensionUtility.getService(bundleContext, IProfileRegistry.class.getName());
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		ProfileChangeRequest pcr = new ProfileChangeRequest(profile);
		pcr.addInstallableUnits(new IInstallableUnit[] { iu } );
		provContext = new ProvisioningContext(new URI[] { uri });
		if (!explain)
			provContext.setProperty("org.eclipse.equinox.p2.director.explain", "false");
		//provContext = new ProvisioningContext();
		plan = planner.getProvisioningPlan(pcr, provContext, monitor);
		//System.out.println("Time: " + (System.currentTimeMillis() - time)); // TODO
		return plan;
	}
}