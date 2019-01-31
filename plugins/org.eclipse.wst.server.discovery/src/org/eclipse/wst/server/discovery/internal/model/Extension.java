/*******************************************************************************
 * Copyright (c) 2008, 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery.internal.model;

import java.net.URI;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.*;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.discovery.internal.Activator;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;
import org.osgi.framework.BundleContext;

public class Extension implements IServerExtension{
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
		return new IInstallableUnit[] {iu};
	}

	public IProvisioningPlan getProvisioningPlan(boolean explain, IProgressMonitor monitor) {
		if (plan != null)
			return plan;

		//long time = System.currentTimeMillis();
		BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
		IPlanner planner = (IPlanner) ExtensionUtility.getService(bundleContext, IPlanner.SERVICE_NAME);

		IProfileRegistry profileRegistry = (IProfileRegistry) ExtensionUtility.getService(bundleContext, IProfileRegistry.SERVICE_NAME);
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		IProfile[] profiles = profileRegistry.getProfiles();
		if (profile == null ) {//it happens sometime , possibility of bug in profileRegistry
			for (int i = 0; i < profiles.length; i++) {
				if (profiles[i].getProfileId().equals(IProfileRegistry.SELF)){
					profile = profiles[i];
					break;
				}
				
			}
		}
		if (profile == null)
			return null;
		IProfileChangeRequest pcr = planner.createChangeRequest(profile);
		pcr.add(iu);
		IProvisioningAgent agent = ExtensionUtility.getAgent(bundleContext);
		if (agent == null) {
			return null;
		}
		
		// Get all the known repositories when installing the server adapter.
		// If these repositories are not added, it can cause install problems if 
		// the server adapter relies on the list of available software install sites
		URI[] knownRepositories = null;
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager != null){
			manager.addRepository(uri);
			// Note: IRepositoryManager.REPOSITORIES_ALL will exclude the deselected update sites
			knownRepositories = manager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL);
			
			// A fall back in case known repositories returns null
			if (knownRepositories == null){
				knownRepositories = new URI[] {uri};
			}
		}
		else {
			knownRepositories = new URI[] {uri};
		}		
		
		provContext = new ProvisioningContext(agent);
		
		// Add the new URLs to both the Metadata and Artifact repositories.
		// Note: only the IInstallableUnit that is passed into this class will be installed
		// as a server adapter. For example, if multiple update site URLs for discovery server
		// adapters are present, they will not be installed.
		provContext.setMetadataRepositories(knownRepositories);
		provContext.setArtifactRepositories(knownRepositories);
		if (!explain)
			provContext.setProperty("org.eclipse.equinox.p2.director.explain", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		
		provContext.setProperty(ProvisioningContext.FOLLOW_REPOSITORY_REFERENCES,"true"); //$NON-NLS-1$
		
		plan = planner.getProvisioningPlan(pcr, provContext, monitor);
		//System.out.println("Time: " + (System.currentTimeMillis() - time)); // TODO
		return plan;
	}

	public String getURI() {
		return uri.toString();
	}
	
	public String getServerId() {
		return iu.getProperty("serverId", null);
	}
	
	public String getRuntimeId() {
		return iu.getProperty("runtimeId", null);
	}
	
	public String getRuntimeVendor() {
		return iu.getProperty("vendor", null);
	}
}