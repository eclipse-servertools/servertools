/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.query.*;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.discovery.internal.model.Extension;
import org.eclipse.wst.server.discovery.internal.model.ExtensionUpdateSite;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ExtensionUtility {
	private static ExtensionUpdateSite[] getExtensionUpdateSites(URL url) throws CoreException {
		InputStream in = null;
		try {
			in = url.openStream();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load URL " + url);
		}

		if (in == null)
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Could not load extensions", null));

		try {
			IMemento memento = XMLMemento.loadMemento(in);
			IMemento children[] = memento.getChildren("site");
			int size = children.length;
			List<ExtensionUpdateSite> list = new ArrayList<ExtensionUpdateSite>(size);
			for (int i = 0; i < size; i++) {
				String url2 = children[i].getString("url");
				ExtensionUpdateSite item = new ExtensionUpdateSite(url2, null, null);
				list.add(item);
			}

			ExtensionUpdateSite[] items = new ExtensionUpdateSite[list.size()];
			list.toArray(items);
			return items;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e));
		}
	}

	/**
	 * Returns an array of all known extension update sites.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of extensions items {@link ExtensionUpdateSite}
	 */
	private static ExtensionUpdateSite[] getExtensionUpdateSites() {
		URL url = Activator.getDefault().getBundle().getEntry("serverAdapterSites.xml");

		try {
			return getExtensionUpdateSites(url);
		} catch (CoreException ce) {
			Trace.trace(Trace.SEVERE, "Could not get extension items");
			return new ExtensionUpdateSite[0];
		}
	}

	/**
	 * Return true if the new feature is already installed, or a newer one is.
	 * 
	 * @param existing
	 * @param newFeature
	 * @return true if the new feature is already installed, or a newer one is.
	 */
	private static boolean alreadyExists(List<Extension> existing, Extension newFeature) {
		if (existing.contains(newFeature))
			return true;

		Version newV = newFeature.getVersion();

		Iterator<Extension> iterator = existing.iterator();
		while (iterator.hasNext()) {
			Extension feature = iterator.next();
			if (feature.getId().equals(newFeature.getId())) {
				if (feature.getVersion().compareTo(newV) >= 0)
					return true;
			}
		}

		return false;
	}

	private static void addExtension(List<Extension> list, List<Extension> existing, Extension newFeature, ExtensionListener listener) {
		if (alreadyExists(existing, newFeature))
			return;

		synchronized (list) {
			Version newV = newFeature.getVersion();
			Extension remove = null;

			Iterator<Extension> iterator = list.iterator();
			while (iterator.hasNext()) {
				Extension feature = iterator.next();
				if (feature.getId().equals(newFeature.getId())) {
					if (feature.getVersion().compareTo(newV) < 0) {
						remove = feature;
					} else
						// new feature is older
						return;
				}
			}
			if (remove != null) {
				list.remove(remove);
				listener.extensionRemoved(remove);
			}

			list.add(newFeature);
		}
		listener.extensionFound(newFeature);
	}

	protected static void addExtensions(List<Extension> list, List<Extension> existing, List<Extension> newFeatures, ExtensionListener listener) {
		Iterator<Extension> iterator = newFeatures.iterator();
		while (iterator.hasNext())
			addExtension(list, existing, iterator.next(), listener);
	}

	public interface ExtensionListener {
		public void extensionFound(Extension extension);

		public void extensionRemoved(Extension feature);

		public void siteFailure(String host);
	}

	private static List<Extension> getExistingFeatures(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.discoverLocalConfiguration, 100);

		IProfileRegistry profileRegistry = (IProfileRegistry) getService(Activator.getDefault().getBundle().getBundleContext(), IProfileRegistry.class.getName());
		IProfile[] profiles = profileRegistry.getProfiles();
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);

		IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();
		//Query query = new InstallableUnitQuery("org.eclipse.wst.server.core.serverAdapter");
		//List<String> list2 = new ArrayList();
		//Query query = new ExtensionInstallableUnitQuery(list2);
		IQueryResult<IInstallableUnit> collector = profile.query(query, monitor);

		List<Extension> list = new ArrayList<Extension>();
		Iterator<IInstallableUnit> iter = collector.iterator();
		while (iter.hasNext()) {
			IInstallableUnit iu = iter.next();
			if (!list.contains(iu))
				list.add(new Extension(iu, null));
		}

		monitor.done();

		return list;
	}

	public static Extension[] getAllExtensions(final String id, final ExtensionListener listener, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 1100);

		monitor.subTask(Messages.discoverLocalConfiguration);
		final List<Extension> existing = getExistingFeatures(ProgressUtil.getSubMonitorFor(monitor, 100));

		final ExtensionUpdateSite[] items = getExtensionUpdateSites();
		if (items == null || items.length == 0)
			return new Extension[0];
		final int x = 1000 / items.length;

		monitor.worked(50);
		final List<Extension> list = new ArrayList<Extension>();
		int size = items.length;

		Thread[] threads = new Thread[size];
		for (int i = 0; i < size; i++) {
			try {
				if (monitor.isCanceled())
					return null;

				monitor.subTask(NLS.bind(Messages.discoverSearching, items[i].getUrl()));
				final int ii = i;
				final IProgressMonitor monitor2 = monitor;
				threads[i] = new Thread("Extension Checker for " + items[i].getUrl()) {
					public void run() {
						try {
							List<Extension> list2 = items[ii].getExtensions(ProgressUtil.getSubMonitorFor(monitor2, x));
							addExtensions(list, existing, list2, listener);
						} catch (CoreException ce) {
							listener.siteFailure(ce.getLocalizedMessage());
							Trace.trace(Trace.WARNING, "Error downloading extension info", ce);
						}
					}
				};
				threads[i].setDaemon(true);
				threads[i].start();
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error downloading extension info 2", e);
			}
		}

		for (int i = 0; i < size; i++) {
			try {
				if (monitor.isCanceled())
					return null;

				if (threads[i].isAlive())
					threads[i].join();
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error downloading extension info 3", e);
			}
		}

		Extension[] ef = new Extension[list.size()];
		list.toArray(ef);
		monitor.done();
		return ef;
	}

	/**
	 * Returns the service described by the given arguments.  Note that this is a helper class
	 * that <b>immediately</b> ungets the service reference.  This results in a window where the
	 * system thinks the service is not in use but indeed the caller is about to use the returned 
	 * service object.  
	 * @param context
	 * @param name
	 * @return The requested service
	 */
	public static Object getService(BundleContext context, String name) {

		ServiceReference reference = context.getServiceReference(IProvisioningAgent.SERVICE_NAME);
		if (reference == null)
			return null;
		
		IProvisioningAgent result = getAgent(context);
		if (result == null)
			return null;
		try {
			return result.getService(name);
		} finally {
			context.ungetService(reference);
		}
	}

	public static IProvisioningAgent getAgent(BundleContext context) {
		ServiceReference reference = context.getServiceReference(IProvisioningAgent.SERVICE_NAME);
		if (reference == null)
			return null;
		IProvisioningAgent result = (IProvisioningAgent) context.getService(reference);
		
		return result;
	}
}