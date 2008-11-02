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
package org.eclipse.wst.server.discovery.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.updatesite.metadata.UpdateSiteMetadataRepositoryFactory;
import org.eclipse.equinox.internal.provisional.p2.director.*;
import org.eclipse.equinox.internal.provisional.p2.engine.*;
import org.eclipse.equinox.internal.provisional.p2.metadata.*;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.osgi.framework.BundleContext;

public class P2Test {

	public static void test() {
		try {
			System.out.println("---p2---");
			//SimpleMetadataRepositoryFactory mrf = new SimpleMetadataRepositoryFactory();
			UpdateSiteMetadataRepositoryFactory mrf = new UpdateSiteMetadataRepositoryFactory();
			URL url = new URL("http://www.apache.org/dist/geronimo/eclipse/updates/");
			IMetadataRepository repo = mrf.load(url, null);
			System.out.println("Repo: " + repo);
			//Query query = new InstallableUnitQuery(null);
			Query query = new InstallableUnitQuery("org.eclipse.wst.server.core.serverAdapter");
			Collector collector = new Collector(); 
			repo.query(query, collector, null);
			
			listCollector(collector);
			
			List<IInstallableUnit> list = new ArrayList<IInstallableUnit>();
			Iterator iter = collector.iterator();
			while (iter.hasNext()) {
				IInstallableUnit iu = (IInstallableUnit) iter.next();
				RequiredCapability[] req = iu.getRequiredCapabilities();
				if (req != null) {
					for (RequiredCapability rc : req) {
						query = new InstallableUnitQuery(rc.getName(), rc.getRange());
						Collector collector2 = new Collector();
						repo.query(query, collector2, null);
						
						Iterator iter2 = collector2.iterator();
						while (iter2.hasNext()) {
							IInstallableUnit iu2 = (IInstallableUnit) iter2.next();
							if (!list.contains(iu2))
								list.add(iu2);
						}
					}
				}
			}
			
			IInstallableUnit install = null;
			iter = list.iterator();
			while (iter.hasNext()) {
				IInstallableUnit iu = (IInstallableUnit) iter.next();
				if (install == null || install.getVersion().compareTo(iu.getVersion()) < 0)
					install = iu;
				
				System.out.println(iu.getId() + " " + iu.getVersion() + " " + iu.getProperty(IInstallableUnit.PROP_NAME) + " " + iu.getProperty(IInstallableUnit.PROP_DESCRIPTION));
			}
			
			//if (true)
			//	return;
			
			//IInstallableUnit install = iu;
			
			IProgressMonitor monitor = new NullProgressMonitor();
			
			BundleContext bundleContext = Activator.getDefault().getBundle().getBundleContext();
			IPlanner planner = (IPlanner) ServiceHelper.getService(bundleContext, IPlanner.class.getName());
			
			IProfileRegistry profileRegistry = (IProfileRegistry) ServiceHelper.getService(bundleContext, IProfileRegistry.class.getName());
			IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
			ProfileChangeRequest pcr = new ProfileChangeRequest(profile);
			pcr.addInstallableUnits(new IInstallableUnit[] { install } );
			ProvisioningContext provContext = new ProvisioningContext();
			ProvisioningPlan plan = planner.getProvisioningPlan(pcr, provContext, monitor);
			System.out.println("plan: " + plan.getStatus());
			//if (!plan.getStatus().isOK())
			//	return plan.getStatus();
			
			IEngine engine = (IEngine) ServiceHelper.getService(bundleContext, IEngine.SERVICE_NAME);
			IStatus status = engine.perform(profile, new DefaultPhaseSet(), plan.getOperands(), provContext, monitor);
			System.out.println("install: " + status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listCollector(Collector collector) {
		Iterator iter = collector.iterator();
		while (iter.hasNext()) {
			IInstallableUnit iu = (IInstallableUnit) iter.next();
			System.out.println("  " + iu + " " + iu.getVersion());
			Map map = iu.getProperties();
			if (map != null) {
				Iterator iter2 = map.keySet().iterator();
				while (iter2.hasNext()) {
					Object obj = iter2.next();
					String s = map.get(obj) + "";
					if (s.contains("\n"))
						s = s.substring(0, s.indexOf("\n"));
					//if (s.length() > 20)
					//	s = s.substring(0, 20);
					System.out.println("     prop: " + obj + " = " + s);
				}
			}
			RequiredCapability[] req = iu.getRequiredCapabilities();
			if (req != null) {
				for (RequiredCapability rc : req) {
					System.out.println("     req: " + rc.getName() + " " + rc.getRange() + " " + rc.getNamespace());
				}
			}
			ProvidedCapability[] pro = iu.getProvidedCapabilities();
			if (pro != null) {
				for (ProvidedCapability pc : pro) {
					System.out.println("     pro: " + pc.getName());
				}
			}
		}
	}
}
