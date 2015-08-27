/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.p2.ui.ProvisioningOperationRunner;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.discovery.ErrorMessage;
import org.eclipse.wst.server.discovery.RuntimeProxy;
import org.eclipse.wst.server.discovery.ServerProxy;
import org.eclipse.wst.server.discovery.internal.model.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ExtensionUtility {
	private static final String SERVER_ADAPTER_SITES_DETAILS = "serverAdapterSitesDetails.xml";
	private static Extension[] extensionList = null;
	private static Object tempLock = new Object();
    private static List<ServerProxy> serverExtension = new ArrayList<ServerProxy>();
    private static HashMap<String, Extension>extensionMap = new HashMap<String, Extension>();
    private static HashMap<String, ErrorMessage> extensionMapError = new HashMap<String, ErrorMessage>();
	
    
    private static List<IServerExtension> getExtensionUpdateSitesDetails(URL url, Extension[] extnList) throws CoreException {InputStream in = null;
		try {
			in = url.openStream();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load URL " + url);
		}
	
		if (in == null)
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Could not load extension details", null));
	
		try {
			IMemento memento = XMLMemento.loadMemento(in);
			List<IServerExtension> list = new ArrayList<IServerExtension>();
			IMemento children[] = memento.getChildren("feature");

			if (extnList != null){
				XMLMemento newMemento = XMLMemento.createWriteRoot("extensionDetails");

				for (int j = 0; j < extnList.length; j++) {
					IMemento childMemento = newMemento.createChild("feature");
					childMemento.putString("provider", extnList[j].getProvider());
					for (int i = 0; i < children.length; i++) {
						if (children[i].getString("id").equals(extnList[j].getId())){
							childMemento.putString("provider", children[i].getString("provider"));
							if(extnList[j].getServerId() != null){
								childMemento.putString("serverId", extnList[j].getServerId());
							}
							else if (children[i].getString("serverId") != null)
								childMemento.putString("serverId", children[i].getString("serverId"));
							
							break;
						}
					}

					childMemento.putString("id", extnList[j].getId());
					childMemento.putString("description", extnList[j].getDescription());
					childMemento.putString("name", extnList[j].getName());
					childMemento.putString("uri", extnList[j].getURI());
					childMemento.putString("version", extnList[j].getVersion().toString());
					ExtensionProxy item = new ExtensionProxy(childMemento.getString("id"), childMemento.getString("name"), childMemento.getString("description"), 
							childMemento.getString("provider"), childMemento.getString("uri"), childMemento.getString("version"), childMemento.getString("serverId"));
						list.add(item);
				}
				newMemento.saveToFile(url.toURI().getPath());
			}
			else{
				for (int i = 0; i < children.length; i++) {
					ExtensionProxy item = new ExtensionProxy(children[i].getString("id"), children[i].getString("name"), children[i].getString("description"), 
						children[i].getString("provider"), children[i].getString("uri"), children[i].getString("version"), children[i].getString("serverId"));
					list.add(item);
				}
			}
			return list;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e.getMessage(), e));
		}
    	
    }
    
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
	
	private static void copyFileUsingFileStreams(InputStream input, File dest) throws IOException {
		if (input == null) {
			throw new IOException("Input stream is null");
		}
		// dest is never null
		OutputStream output = null;
		try {
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			if (output != null) output.close();
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
	private static List<IServerExtension> getExtensionUpdateSitesDetails(Extension[] extenList) {
		try {
			URL cacheFileUrl = null;
			File workspaceFile = new File(Activator.getDefault().getStateLocation().toOSString() +  File.separator + SERVER_ADAPTER_SITES_DETAILS);
			// First look in workspace, then in configuration and finally fall back to master copy
			if (workspaceFile.exists()) {
				cacheFileUrl = workspaceFile.toURI().toURL();
			} else {
				// Use the configuration area
				File configDir = getConfigDir(); // It is never null
				File configFile = new File(configDir.getAbsolutePath() + File.separator + SERVER_ADAPTER_SITES_DETAILS);
				if (configFile.exists()) {
					cacheFileUrl = configFile.toURI().toURL();
				} else {
					// Create in configuration area
					URL masterCopyUrl = Activator.getDefault().getBundle().getEntry(SERVER_ADAPTER_SITES_DETAILS);
					boolean configFileAreaExists = configDir.exists();
					if (!configFileAreaExists) {
						try {
							configFileAreaExists = configDir.mkdirs();					
						} catch (Exception e) {
							// Could fail if user has no write permission
							printLog("Cannot write to configuration", e);
							configFileAreaExists = false;
						}
					}
					if (configFileAreaExists) {
						try {
							copyFileUsingFileStreams(masterCopyUrl.openStream(), configFile);
							cacheFileUrl = configFile.toURI().toURL();
						} catch (IOException e) {
							printLog("Cannot write to configuration", e);
						}
					}
					if (cacheFileUrl == null) {
						// Failed to create in configuration area; use workspace area
						try {
							File workspaceFileArea = workspaceFile.getParentFile();
							boolean workspaceFileAreaExists = workspaceFileArea.exists();
							if (!workspaceFileAreaExists) {
								try {
									workspaceFileAreaExists = workspaceFileArea.mkdirs();
								} catch (Exception e) {
									// Should never fail, yet better to be safe than sorry!
									Trace.trace(Trace.SEVERE, "Cannot write to workspace", e);
									workspaceFileAreaExists = false;
								}
							}
							if (workspaceFileAreaExists) {
								copyFileUsingFileStreams(masterCopyUrl.openStream(), workspaceFile);
								cacheFileUrl = workspaceFile.toURI().toURL();
							}
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Cannot write to workspace", e);
						}
					}
					if (cacheFileUrl == null) {
						// Use the master copy as the cache
						cacheFileUrl = masterCopyUrl;
					}
				}
			}
			printLog("cacheFile=" + cacheFileUrl, null);
			return getExtensionUpdateSitesDetails(cacheFileUrl, extenList);
		} catch (CoreException ce) {
			Trace.trace(Trace.SEVERE, "Could not get extension items details", ce);
		} catch (MalformedURLException e) {
			Trace.trace(Trace.SEVERE, "Could not get extension items details", e);
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Could not get extension items details", e);
		} catch (URISyntaxException e) {
			Trace.trace(Trace.SEVERE, "Could not get extension items details", e);
		}
		return  new ArrayList<IServerExtension>();
	}

	private static File getConfigDir() throws IOException, URISyntaxException {
		Location cfgLoc = Platform.getConfigurationLocation();
		URL cfgDataUrl = cfgLoc.getDataArea(Activator.PLUGIN_ID);
		return new File(cfgDataUrl.toExternalForm());
	}
	
	private static void printLog(String msg, Exception ex) {
		Activator.getDefault().getLog().log(new Status(Status.INFO, Activator.PLUGIN_ID, msg, ex));
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
	private static boolean alreadyExists(List<Extension> existing, IServerExtension newFeature) {
		if (existing.contains(newFeature))
			return true;

		Version newV = newFeature.getVersion();

		Iterator<Extension> iterator = existing.iterator();
		while (iterator.hasNext()) {
			IServerExtension feature = iterator.next();
			if (feature.getId().equals(newFeature.getId())) {
				if(newV == null)
					return true;
				if (feature.getVersion().compareTo(newV) >= 0)
					return true;
			}
		}
		return false;
	}

	private static void addExtension(List<IServerExtension> list, List<Extension> existing, IServerExtension newFeature, ExtensionListener listener) {
		if (alreadyExists(existing, newFeature))
			return;

		synchronized (list) {
			Version newV = newFeature.getVersion();
			IServerExtension remove = null;

			Iterator<IServerExtension> iterator = list.iterator();
			while (iterator.hasNext()) {
				IServerExtension feature = iterator.next();
				if (feature.getId().equals(newFeature.getId())) {
					if (newV == null)
						return; // don't add if already exists
					if (feature.getVersion().compareTo(newV) < 0) {
						remove = feature;
					} else
						// new feature is older
						return;
				}
			}
			if (remove != null) {
				list.remove(remove);
				if (listener!= null)
					listener.extensionRemoved((Extension)remove);
			}

			list.add(newFeature);
		}
		if (listener!= null){
			listener.extensionFound((Extension)newFeature);
		}
		if (listener == null && newFeature instanceof ExtensionProxy)
			serverExtension.add(createServerProxy((ExtensionProxy)newFeature));

	}
	
	private static ServerProxy createServerProxy(ExtensionProxy newFeature){
		RuntimeProxy runtimeProxy = new RuntimeProxy("com.eclipse.runtime.proxy", newFeature.getName(), newFeature.getDescription(), newFeature.getProvider());
		ServerProxy serverProxy = new ServerProxy("com.eclipse.server.proxy", newFeature.getName(), newFeature.getDescription(),  runtimeProxy, newFeature.getId(), newFeature.getURI(), newFeature.getServerId());
		return serverProxy;
	}

	protected static void addExtensions(List<IServerExtension> list, List<Extension> existing, List<IServerExtension> newFeatures, ExtensionListener listener) {
		Iterator<IServerExtension> iterator = newFeatures.iterator();
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
		if (profile == null ) {//it happens sometime , possibility of bug in profileRegistry
			for (int i = 0; i < profiles.length; i++) {
				if (profiles[i].getProfileId().equals(IProfileRegistry.SELF)){
					profile = profiles[i];
					break;
				}
				
			}
		}
		List<Extension> list = new ArrayList<Extension>();
		
		if (profile == null)
			return list;
		
		IQuery<IInstallableUnit> query = QueryUtil.createIUAnyQuery();
		//Query query = new InstallableUnitQuery("org.eclipse.wst.server.core.serverAdapter");
		//List<String> list2 = new ArrayList();
		//Query query = new ExtensionInstallableUnitQuery(list2);
		IQueryResult<IInstallableUnit> collector = profile.query(query, monitor);

		Iterator<IInstallableUnit> iter = collector.iterator();
		while (iter.hasNext()) {
			IInstallableUnit iu = iter.next();
			if (!list.contains(iu))
				list.add(new Extension(iu, null));
		}

		monitor.done();

		return list;
	}

	
	public static void setExtensionList(Extension[] extList){
		synchronized (tempLock) {
			extensionList = extList;
		}
	}
	
	public static Extension[] getAllExtensionsWithServer(final String id, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 1100);

		monitor.subTask(Messages.discoverLocalConfiguration);
		final List<Extension> existing = getExistingFeatures(ProgressUtil.getSubMonitorFor(monitor, 100));
		
		List <IServerExtension> extensionCached = getExtensionUpdateSitesDetails(null);
		final List<IServerExtension> list = new ArrayList<IServerExtension>();
		if (extensionCached != null && extensionCached.size() != 0){
			addExtensions(list, existing, extensionCached, null);
		}
		return null;
	}
	public static Extension[] getAllExtensions(final String id, final ExtensionListener listener, IProgressMonitor monitor) throws CoreException {
	
		synchronized(tempLock){
		if (extensionList != null && extensionList.length != 0 && listener != null){
			for (int i = 0; i < extensionList.length;i++){
				listener.extensionFound(extensionList[i]);
			}
			return extensionList;
		}
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.beginTask("", 1100);

		monitor.subTask(Messages.discoverLocalConfiguration);
		final List<Extension> existing = getExistingFeatures(ProgressUtil.getSubMonitorFor(monitor, 100));
		
		final ExtensionUpdateSite[] items = getExtensionUpdateSites();
		if (items == null || items.length == 0)
			return new Extension[0];
		final int x = 1000 / items.length;

		monitor.worked(50);
		final List<IServerExtension> list = new ArrayList<IServerExtension>();
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
							List<IServerExtension> list2 = items[ii].getExtensions(ProgressUtil.getSubMonitorFor(monitor2, x));
							addExtensions(list, existing, list2, listener);
						} catch (CoreException ce) {
							if (listener!= null)
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
		extensionList = ef;
		return ef;
		}
	}

	private static String SERVER_ADAPTER_ID = "org.eclipse.wst.server.core.serverAdapter"; 
	
	public static List<ServerProxy> getExtensionsWithServer(IProgressMonitor monitor){
		if (serverExtension == null || serverExtension.isEmpty()){
			try {
				getAllExtensionsWithServer(SERVER_ADAPTER_ID, monitor);
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Could not load server adapter list");
			}
		}
		return serverExtension;
		
	}
	
	public static String getLicenseText(String extensionId){
		Extension extension = extensionMap.get(extensionId);
		if (extension != null)
			return extension.getLicense();
		return null;
		
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
	
	public static boolean installExtension(final Extension extension){
		if (extension == null){
			return false;
		}
		String name = NLS.bind(Messages.installJobName, extension.getName());
		Job job = new Job(name) {
			public IStatus run(IProgressMonitor monitor) {
				return extension.install(monitor);
			}
		};

		// Request a restart when the installation is completed  (bugzilla# 314823)
		ProvisioningOperationRunner por = new ProvisioningOperationRunner(ProvisioningUI.getDefaultUI());
		por.manageJob(job, ProvisioningJob.RESTART_OR_APPLY);
		
		job.setUser(true);
		job.schedule();
		return true;
	}
	
	public static boolean installExtension(final String extensionId){
		return installExtension(extensionMap.get(extensionId));
	}
	
	public static ErrorMessage refreshExtension(final String extensionId, String uri, IProgressMonitor monitor){
		ErrorMessage errorMessageObj = null;
		if (extensionMap.get(extensionId) != null){
			if (extensionMapError.get(extensionId) == null)
			return null;
		}
		ExtensionUpdateSite site = new ExtensionUpdateSite(uri, null, null);
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask("", 1000);
			monitor.subTask(Messages.fetchingRepository);
			IProgressMonitor subMonitor =  ProgressUtil.getSubMonitorFor(monitor, 500);
			List<IServerExtension> list = site.getExtensions(subMonitor);
			monitor.worked(500);
			if (monitor.isCanceled())
				return null;
			boolean firstIteration = true;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				IServerExtension iServerExtension = (IServerExtension) iterator.next();
				if (iServerExtension.getId().equals(extensionId)){
					Extension extension = (Extension)iServerExtension;
					if (extensionMap.get(extensionId) != null && !firstIteration && extension.getVersion().compareTo(((Extension)extensionMap.get(extensionId)).getVersion()) <= 0)
						continue;
					subMonitor =  ProgressUtil.getSubMonitorFor(monitor, 500);
					monitor.subTask(Messages.validateInstall);
					final IProvisioningPlan plan = extension.getProvisioningPlan(true, subMonitor);
					if (monitor.isCanceled())
						return null;
					if (plan == null || !plan.getStatus().isOK()) {
						if (plan!= null){
							StringBuffer detailedErrorMsg= new StringBuffer();
							IStatus[] statusList = plan.getStatus().getChildren();
							for (int i = 0; i < statusList.length; i++) {
								detailedErrorMsg.append(statusList[i].getMessage());
							}
							errorMessageObj = new ErrorMessage(Messages.validateInstallError, detailedErrorMsg.toString());
							Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID,detailedErrorMsg.toString(), plan.getStatus().getException()));
						}
						else{
							errorMessageObj = new ErrorMessage(Messages.validateInstallError,Messages.fetchingRepositoryFailure);
							Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Could not get the provisioning plan",null));
						}
					}
					extensionMap.put(extensionId, extension);
					extensionMapError.put(extensionId, errorMessageObj);
					firstIteration = false;
				}
			}
			return errorMessageObj;
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Could not refresh server adapter node.");
			return new ErrorMessage(Messages.fetchingRepositoryFailure, e.getLocalizedMessage());
		}
		
	}
	
	public static void refreshServerAdapters(IProgressMonitor monitor){
		try {
			Extension[] extnList = getAllExtensions(SERVER_ADAPTER_ID, null, monitor);
			getExtensionUpdateSitesDetails(extnList);
			serverExtension = new ArrayList<ServerProxy>();
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE, "Could not refresh server adapter list.");
		}
	}
}