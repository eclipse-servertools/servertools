/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.extension;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.update.core.ICategory;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.ISite;
import org.eclipse.update.core.ISiteFeatureReference;
import org.eclipse.wst.server.core.internal.IMemento;
import org.eclipse.wst.server.core.internal.InstallableRuntime;
import org.eclipse.wst.server.ui.internal.ProgressUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public class ExtensionSite {
	private IMemento memento;

	public ExtensionSite(IMemento memento) {
		super();
		this.memento = memento;
	}

	public String getUrl() {
		return memento.getString("url");
	}

	private List<IFeature> featureList;

	protected static boolean hasCategory(ISiteFeatureReference ref, String category) {
		ICategory[] cat = ref.getCategories();
		if (cat == null)
			return false;
		
		int size = cat.length;
		for (int i = 0; i < size; i++)
			if (category.equals(cat[i].getName()))
				return true;
		
		return false;
	}

	protected static boolean matches(String a, String b) {
		if (a == null || "*".equals(a))
			return true;
		
		if (b == null)
			return false;
		
		StringTokenizer st = new StringTokenizer(a, ",");
		while (st.hasMoreTokens()) {
			String c = st.nextToken().trim();
			if (b.equals(c))
				return true;
		}
		
		return false;
	}

	public synchronized List<IFeature> getFeatures(String category, IProgressMonitor monitor) throws CoreException {
		if (featureList != null)
			return featureList;
		
		String fromSite = getUrl();
		
		List<IFeature> list = new ArrayList<IFeature>();
		if (fromSite == null)
			return list;
		
		URL fromSiteURL = null;
		try {
			fromSiteURL = new URL(fromSite);
		} catch (MalformedURLException e) {
			return list;
		}
		
		monitor.beginTask("Searching " + fromSite, 100);
		ISite site = null;
		try {
			site = InstallableRuntime.getSite(fromSiteURL, ProgressUtil.getSubMonitorFor(monitor, 5));
		} catch (IOException e) {
			String host = fromSiteURL.getHost();
			throw new CoreException(new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, host, e));
		}
		// java.net.UnknownHostException
		
		ISiteFeatureReference[] featureRefs = site.getFeatureReferences();
		for (int i = 0; i < featureRefs.length; i++) {
			if (hasCategory(featureRefs[i], category)) {
				if (matches(featureRefs[i].getOS(), Platform.getOS()) &&
						matches(featureRefs[i].getOSArch(), Platform.getOSArch()) &&
						matches(featureRefs[i].getWS(), Platform.getWS())) {
					IFeature feature2 = featureRefs[i].getFeature(ProgressUtil.getSubMonitorFor(monitor, 5));
					if (feature2 != null)
						list.add(feature2);
				}
			}
		}
		monitor.done();
		
		featureList = list;
		return featureList;
	}

	public String toString() {
		return "ExtensionSite[" + getUrl() + "]";
	}
}