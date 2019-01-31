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
import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.updatesite.metadata.UpdateSiteMetadataRepositoryFactory;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.expression.IMatchExpression;
import org.eclipse.equinox.p2.query.*;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;
import org.eclipse.wst.server.discovery.internal.Trace;
import org.osgi.framework.BundleContext;
/*
* From Kosta:
*   feature version (optional)
*   alternate name (optional) - defaults to feature name
*/
public class ExtensionUpdateSite {
	private static final List<String> EMPTY_LIST = new ArrayList<String>(0);
	public static final String SERVER_ADAPTER_ID = "org.eclipse.wst.server.core.serverAdapter"; //$NON-NLS-1$

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

    public List<IServerExtension> getExtensions(IProgressMonitor monitor) throws CoreException , ProvisionException{
		URI url2 = null;
		IMetadataRepositoryManager manager = null;
		IProvisioningAgent agent = null;
		List<IServerExtension> list = new ArrayList<IServerExtension>();
		URI[] existingSites = null;
		try {
			/*
			 * To discovery the server adapter, there are three methods:
			 * 1. Looking at the site.xml (if it exists). This is the legacy method
			 * 2. Looking for the org.eclipse.wst.server.core.serverAdapter property in a p2
			 *    update site (that may not have a site.xml). The property is necessary to identify
			 *    the InstallableUnit as a server type. Otherwise, all the InstallableUnits will show
			 *    up regardless of whether it is a server or not
			 * 3. If the user created the p2 update site using a category.xml file (migrating old site.xml
			 *    to use category.xml)
			 */	
			BundleContext bd = org.eclipse.wst.server.discovery.internal.Activator.getDefault().getBundle().getBundleContext();			
			agent = ExtensionUtility.getAgent(bd);
			
			url2 = new URI(url);
			
			// Method 1: Looking at the site.xml
			UpdateSiteMetadataRepositoryFactory mrf = new UpdateSiteMetadataRepositoryFactory();
			mrf.setAgent(ExtensionUtility.getAgent(bd));
			manager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
			// Sites already existing for both enabled and disabled
			URI[] existingSitesAll = manager.getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_ALL);
			URI[] existingSitesDisabled = manager.getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_DISABLED);
			int existingSitesAllLen = existingSitesAll == null ? 0 : existingSitesAll.length;
			int existingSitesDisabledLen = existingSitesDisabled == null ? 0 : existingSitesDisabled.length;
			existingSites = new URI[existingSitesAllLen + existingSitesDisabledLen];
			if (existingSitesAllLen > 0) {
				System.arraycopy(existingSitesAll, 0, existingSites, 0, existingSitesAllLen);
			}
			if (existingSitesDisabledLen > 0) {
				System.arraycopy(existingSitesDisabled, 0, existingSites, existingSitesAllLen, existingSitesDisabledLen);
			}
			
			// If the site.xml does not exist, the load will throw a org.eclipse.equinox.p2.core.ProvisionException
			try {
				IMetadataRepository repo = mrf.load(url2, IRepositoryManager.REPOSITORIES_ALL, monitor);
				IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery("id ~=/*org.eclipse.wst.server.core.serverAdapter/"); //$NON-NLS-1$
				
				list = getInstallableUnits(repo,query,url2,monitor);			
			}
			catch (ProvisionException pe){
				Trace.trace(Trace.WARNING, "Error getting update site information", pe); //$NON-NLS-1$
			}

			// Call Method 2 if there are no results from Method 1 (e.g. if the site.xml exists without
			// specifying any server adapters there or no site.xml exists)
			if (list.isEmpty()){
				manager.addRepository(url2);
				// Need to query for all IUs
				IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();
				
				IMetadataRepository repo = manager.loadRepository(url2, monitor);				
				List<IServerExtension> list2 = getInstallableUnits(repo,query,url2,monitor);
				
				int size = list2.size();
				for (int i=0;i<size;i++){
					Extension e = (Extension)list2.get(i);
					IInstallableUnit[] iuArr = e.getIUs();
					if(iuArr != null && iuArr.length > 0){
						if (iuArr[0] != null){
							if (iuArr[0].getProperty(SERVER_ADAPTER_ID) != null){
								list.add(e);
							}
						}
						
					}
				}
			}
			
			// Call Method 3 if no results from Method 2. Creating the p2 update site using the category.xml will generate
			// a provider property with org.eclipse.wst.server.core.serverAdapter
			if (list.isEmpty()){
				manager = (IMetadataRepositoryManager) agent.getService(IMetadataRepositoryManager.SERVICE_NAME);
				manager.addRepository(url2);
				IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery("id ~=/*org.eclipse.wst.server.core.serverAdapter/"); //$NON-NLS-1$
				
				IMetadataRepository repo = manager.loadRepository(url2, monitor);				
				list = getInstallableUnits(repo,query,url2,monitor);
			}			
			
			return list;
		} catch (ProvisionException e) {
			Trace.trace(Trace.WARNING, "Error getting update info", e); //$NON-NLS-1$
			throw e;
		}catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error getting update info", e); //$NON-NLS-1$
			
			return new ArrayList<IServerExtension>(0);
		}
		finally {
			if (url2 != null && url2.getPath().length() != 0 && manager != null) {
				if (existingSites != null && existingSites.length > 0) {
					boolean urlExists = false;
					for (URI uri : existingSites) {
						if (uri.getPath().equals(url2.getPath())){
							urlExists = true;
							break;
						}
					} 
					// If site did not exist before, remove it as it was added with load
					if (!urlExists) {
						manager.removeRepository(url2);
					}
				}
			}
		}
	}
	
	// Get the list of InstallableUnits and all its requirements
	protected List<IServerExtension> getInstallableUnits(IMetadataRepository repo, IQuery<IInstallableUnit> query, URI url, IProgressMonitor monitor){
		List<IServerExtension> list = new ArrayList<IServerExtension>();
		IQueryResult<IInstallableUnit> collector = repo.query(query, monitor);

		for (IInstallableUnit iu: collector.toUnmodifiableSet()) {
			Collection<IRequirement> req = iu.getRequirements();
			if (req != null) {
				for (IRequirement requirement : req) {
					IMatchExpression<IInstallableUnit> matches = requirement.getMatches();
					query = new ExpressionMatchQuery<IInstallableUnit>(IInstallableUnit.class, matches);

				    IQueryResult<IInstallableUnit> collector2 = repo.query(query, monitor);						
					Iterator<IInstallableUnit> iter2 = collector2.iterator();
					while (iter2.hasNext()) {
						IInstallableUnit iu2 = iter2.next();
						if (!list.contains(iu2)) {
							Extension ext = new Extension(iu2, url);
							list.add(ext);
						}
					}
				}
			}
		}
		return list;
	}
}