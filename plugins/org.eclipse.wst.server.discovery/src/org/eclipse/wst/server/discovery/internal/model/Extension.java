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
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.provisional.p2.director.IPlanner;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.engine.DefaultPhaseSet;
import org.eclipse.equinox.internal.provisional.p2.engine.IEngine;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.engine.ProvisioningContext;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui.query.IUPropertyUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.discovery.internal.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class Extension implements IExtension {
	private ImageDescriptor imageDescriptor;
	private IInstallableUnit iu;
	
	private ProvisioningContext provContext;
	private ProvisioningPlan plan;

	public Extension() {
		// do nothing
	}

	public Extension(IInstallableUnit iu) {
		this.iu = iu;
	}

	public String getName() {
		//return iu.getProperty(IInstallableUnit.PROP_NAME);
		return IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_NAME);
	}

	public String getDescription() {
		//return iu.getProperty(IInstallableUnit.PROP_DESCRIPTION);
		return IUPropertyUtils.getIUProperty(iu, IInstallableUnit.PROP_DESCRIPTION);
	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLicense() {
		return iu.getLicense().getBody();
	}

	public String getProvider() {
		//return iu.getProperty(IInstallableUnit.PROP_PROVIDER);
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
		
		ProvisioningPlan plan = getProvisioningPlan(monitor);
		System.out.println("plan: " + plan.getStatus());
		//if (!plan.getStatus().isOK())
		//	return plan.getStatus();
		
		IProfileRegistry profileRegistry = (IProfileRegistry) ServiceHelper.getService(bundleContext, IProfileRegistry.class.getName());
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		
		//ProvisioningContext provContext = plan.
		IEngine engine = (IEngine) ServiceHelper.getService(bundleContext, IEngine.SERVICE_NAME);
		IStatus status = engine.perform(profile, new DefaultPhaseSet(), plan.getOperands(), provContext, monitor);
		return status;
	}

	public IInstallableUnit[] getIUs() {
		return new IInstallableUnit[] { iu };
	}

	public ProvisioningPlan getProvisioningPlan(IProgressMonitor monitor) {
		if (plan != null)
			return plan;
		
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		IPlanner planner = (IPlanner) ServiceHelper.getService(bundleContext, IPlanner.class.getName());
		
		IProfileRegistry profileRegistry = (IProfileRegistry) ServiceHelper.getService(bundleContext, IProfileRegistry.class.getName());
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		ProfileChangeRequest pcr = new ProfileChangeRequest(profile);
		pcr.addInstallableUnits(new IInstallableUnit[] { iu } );
		provContext = new ProvisioningContext();
		plan = planner.getProvisioningPlan(pcr, provContext, monitor);
		System.out.println(plan.getStatus());
		return plan;
	}
}