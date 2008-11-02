/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.discovery.internal.model.Extension;
import org.eclipse.wst.server.discovery.internal.model.ExtensionUpdateSite;
import org.eclipse.wst.server.discovery.internal.model.IExtension;
import org.eclipse.wst.server.discovery.internal.wizard.ExtensionWizard;
import org.osgi.framework.Version;

public class ExtensionUtility {
	public static boolean launchExtensionWizard(Shell shell, String title, String message) {
		ExtensionWizard wizard2 = new ExtensionWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard2);
		if (dialog.open() != Window.CANCEL)
			return true;
		return false;
	}

	private static ExtensionUpdateSite[] getExtensionSites(URL url) throws CoreException {
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
	 * Returns an array of all known extension items.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of extensions items {@link ExtensionSite}
	 */
	private static ExtensionUpdateSite[] getExtensionSites() {
		URL url = Activator.getDefault().getBundle().getEntry("serverAdapterSites.xml");
		
		try {
			return getExtensionSites(url);
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
	private static boolean alreadyExists(List<IExtension> existing, IExtension newFeature) {
		if (existing.contains(newFeature))
			return true;
		
		Version newV = newFeature.getVersion();
		
		Iterator<IExtension> iterator = existing.iterator();
		while (iterator.hasNext()) {
			IExtension feature = iterator.next();
			if (feature.getId().equals(newFeature.getId())) {
				if (feature.getVersion().compareTo(newV) >= 0)
					return true;
			}
		}
		
		return false;
	}

	private static void addExtension(List<IExtension> list, List<IExtension> existing, IExtension newFeature, ExtensionListener listener) {
		if (alreadyExists(existing, newFeature))
			return;
		
		synchronized (list) {
			Version newV = newFeature.getVersion();
			IExtension remove = null;
			
			Iterator<IExtension> iterator = list.iterator();
			while (iterator.hasNext()) {
				IExtension feature = iterator.next(); 
				if (feature.getId().equals(newFeature.getId())) {
					if (feature.getVersion().compareTo(newV) < 0) {
						remove = feature;
					} else // new feature is older
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

	private static void addExtensions(List<IExtension> list, List<IExtension> existing, List<IExtension> newFeatures, ExtensionListener listener) {
		Iterator iterator = newFeatures.iterator();
		while (iterator.hasNext()) {
			addExtension(list, existing, (IExtension) iterator.next(), listener);
		}
	}

	public interface ExtensionListener {
		public void extensionFound(IExtension extension);
		public void extensionRemoved(IExtension feature);
		public void siteFailure(String host);
	}

	private static List<IExtension> getExistingFeatures(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(Messages.installableServerLocal, 100);
		
		IProfileRegistry profileRegistry = (IProfileRegistry) ServiceHelper.getService(Activator.getDefault().getBundle().getBundleContext(), IProfileRegistry.class.getName());
		IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
		
		Query query = new InstallableUnitQuery(null);
		Collector collector = new Collector(); 
		profile.query(query, collector, null);
		
		List<IExtension> list = new ArrayList<IExtension>();
		Iterator iter = collector.iterator();
		while (iter.hasNext()) {
			IInstallableUnit iu = (IInstallableUnit) iter.next();
			if (!list.contains(iu))
				list.add(new Extension(iu));
		}
		
		monitor.done();
		
		return list;
	}

	public static IExtension[] getAllExtensions(final String id, final ExtensionListener listener, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 1100);
		
		monitor.subTask(Messages.installableServerLocal);
		final List<IExtension> existing = getExistingFeatures(ProgressUtil.getSubMonitorFor(monitor, 100));
		
		final ExtensionUpdateSite[] items = getExtensionSites();
		if (items == null || items.length == 0)
			return new IExtension[0];
		final int x = 1000 / items.length;
		
		monitor.worked(50);
		final List<IExtension> list = new ArrayList<IExtension>();
		int size = items.length;
		
		Thread[] threads = new Thread[size];
		for (int i = 0; i < size; i++) {
			try {
				if (monitor.isCanceled())
					return null;
				
				monitor.subTask(NLS.bind(Messages.installableServerSearching, items[i].getUrl()));
				final int ii = i;
				final IProgressMonitor monitor2 = monitor;
				threads[i] = new Thread("Extension Checker") {
					public void run() {
						try {
							List<IExtension> list2 = items[ii].getExtensions(ProgressUtil.getSubMonitorFor(monitor2, x));
							addExtensions(list, existing, list2, listener);
						} catch (CoreException ce) {
							listener.siteFailure(ce.getLocalizedMessage());
							Trace.trace(Trace.WARNING, "Error downloading server adapter info", ce);
						}
					}
				};
				threads[i].setDaemon(true);
				threads[i].start();
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error downloading server adapter info 2", e);
			}
		}
		
		for (int i = 0; i < size; i++) {
			try {
				if (monitor.isCanceled())
					return null;
				
				if (threads[i].isAlive())
					threads[i].join();
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Error downloading server adapter info 3", e);
			}
		}
		
		IExtension[] ef = new IExtension[list.size()];
		list.toArray(ef);
		monitor.done();
		return ef;
	}
}