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

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.updatesite.metadata.UpdateSiteMetadataRepositoryFactory;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.RequiredCapability;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.eclipse.wst.server.discovery.internal.Trace;
/*
* From Kosta:
*   feature version (optional)
*   alternate name (optional) - defaults to feature name
*/
public class ExtensionUpdateSite implements IExtensionSite {
	private static final List<String> EMPTY_LIST = new ArrayList<String>(0);

	private String url;
	private String featureId;
	private String homepage;
	private String supportUrl;
	private List<String> categories;

	public ExtensionUpdateSite() {
		// do nothing
	}

	public ExtensionUpdateSite(String url, String featureId, List<String> categories) {
		this.url = url;
		this.featureId = featureId;
		this.categories = categories;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.discovery.internal.model.IExtensionSite#getUrl()
	 */
	public String getUrl() {
		return url;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.discovery.internal.model.IExtensionSite#getFeatureId()
	 */
	public String getFeatureId() {
		return featureId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.discovery.internal.model.IExtensionSite#getCategories()
	 */
	public List<String> getCategories() {
		if (categories == null)
			return EMPTY_LIST;
		return categories;
	}

	public List<IExtension> getExtensions(IProgressMonitor monitor) throws CoreException {
		try {
			UpdateSiteMetadataRepositoryFactory mrf = new UpdateSiteMetadataRepositoryFactory();
			URL url2 = new URL(url);
			IMetadataRepository repo = mrf.load(url2, monitor);
			//System.out.println("Repo: " + repo);
			//Query query = new InstallableUnitQuery(null);
			Query query = new InstallableUnitQuery("org.eclipse.wst.server.core.serverAdapter");
			Collector collector = new Collector(); 
			repo.query(query, collector, monitor);
			
			List<IExtension> list = new ArrayList<IExtension>();
			Iterator iter = collector.iterator();
			while (iter.hasNext()) {
				IInstallableUnit iu = (IInstallableUnit) iter.next();
				RequiredCapability[] req = iu.getRequiredCapabilities();
				if (req != null) {
					for (RequiredCapability rc : req) {
						query = new InstallableUnitQuery(rc.getName(), rc.getRange());
						Collector collector2 = new Collector();
						repo.query(query, collector2, monitor);
						
						Iterator iter2 = collector2.iterator();
						while (iter2.hasNext()) {
							IInstallableUnit iu2 = (IInstallableUnit) iter2.next();
							if (!list.contains(iu2)) {
								Extension ext = new Extension(iu2);
								list.add(ext);
							}
						}
					}
				}
			}
			return list;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error get update info", e);
			return new ArrayList<IExtension>(0);
		}
	}
}