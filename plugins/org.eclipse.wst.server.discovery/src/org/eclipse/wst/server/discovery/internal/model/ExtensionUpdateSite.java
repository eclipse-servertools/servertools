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
import java.util.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.updatesite.metadata.UpdateSiteMetadataRepositoryFactory;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.query.ExpressionQuery;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.wst.server.discovery.internal.PatternInstallableUnitQuery;
import org.eclipse.wst.server.discovery.internal.Trace;
/*
* From Kosta:
*   feature version (optional)
*   alternate name (optional) - defaults to feature name
*/
public class ExtensionUpdateSite {
	private static final List<String> EMPTY_LIST = new ArrayList<String>(0);

	private String url;
	private String featureId;
	private List<String> categories;

	public ExtensionUpdateSite() {
		// do nothing
	}

	public ExtensionUpdateSite(String url, String featureId, List<String> categories) {
		this.url = url;
		this.featureId = featureId;
		this.categories = categories;
	}

	public String getUrl() {
		return url;
	}

	public String getFeatureId() {
		return featureId;
	}

	public List<String> getCategories() {
		if (categories == null)
			return EMPTY_LIST;
		return categories;
	}

	public List<Extension> getExtensions(IProgressMonitor monitor) throws CoreException {
		try {
			UpdateSiteMetadataRepositoryFactory mrf = new UpdateSiteMetadataRepositoryFactory();
			URI url2 = new URI(url);
			IMetadataRepository repo = mrf.load(url2, IRepositoryManager.REPOSITORIES_ALL, monitor);
			//Query query = new InstallableUnitQuery("org.eclipse.wst.server.core.serverAdapter");
			//Query query = CompoundQuery.createCompoundQuery(new Query[] {new
			//		IUPropertyQuery(IInstallableUnit.PROP_TYPE_CATEGORY, Boolean.toString(true)),
			//		new IUPropertyQuery(IInstallableUnit.PROP_NAME,"org.eclipse.wst.server.core.serverAdapter")}, true);
			IQuery<IInstallableUnit> query = new PatternInstallableUnitQuery("org.eclipse.wst.server.core.serverAdapter");
			
			IQueryResult<IInstallableUnit> collector = repo.query(query, monitor);
			List<Extension> list = new ArrayList<Extension>();
			for (IInstallableUnit iu: collector.unmodifiableSet()) {
				Collection<IRequirement> req = iu.getRequiredCapabilities();
				if (req != null) {
					for (IRequirement requirement : req) {
					    query = new ExpressionQuery<IInstallableUnit>(IInstallableUnit.class, requirement.getMatches());
					    IQueryResult<IInstallableUnit> collector2 = repo.query(query, monitor);						
						Iterator<IInstallableUnit> iter2 = collector2.iterator();
						while (iter2.hasNext()) {
							IInstallableUnit iu2 = iter2.next();
							if (!list.contains(iu2)) {
								Extension ext = new Extension(iu2, url2);
								list.add(ext);
							}
						}
					}
				}
			}
			return list;
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error getting update info", e);
			return new ArrayList<Extension>(0);
		}
	}
}