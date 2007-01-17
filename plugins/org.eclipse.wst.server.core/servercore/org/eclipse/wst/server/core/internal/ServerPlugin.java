/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.*;
import java.util.*;
import java.text.DateFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
/**
 * The main server plugin class.
 */
public class ServerPlugin extends Plugin {
	public static final String PROJECT_PREF_FILE = ".serverPreference";

	private static final String SHUTDOWN_JOB_FAMILY = "org.eclipse.wst.server.core.family";
	//public static final String REGISTRY_JOB_FAMILY = "org.eclipse.wst.server.registry.family";

	protected static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	protected static int num = 0;

	// cached copy of all launchable adapters
	private static List launchableAdapters;

	// cached copy of all launchable clients
	private static List clients;

	// cached copy of all module factories
	private static List moduleFactories;

	// singleton instance of this class
	private static ServerPlugin singleton;

	// cached copy of all publish tasks
	private static List publishTasks;

	//	cached copy of all server monitors
	private static List monitors;

	//	cached copy of all runtime locators
	private static List runtimeLocators;

	// cached copy of all module artifact adapters
	private static List moduleArtifactAdapters;

	//	cached copy of all installable servers
	private static List installableServers;

	//	cached copy of all installable runtimes
	private static List installableRuntimes;

	// registry listener
	private static IRegistryChangeListener registryListener;
	
	public static BundleContext bundleContext;

	// bundle listener
	private BundleListener bundleListener;

	private static final String TEMP_DATA_FILE = "tmp-data.xml";

	class TempDir {
		String path;
		int age;
	}

	// temp directories - String key to TempDir
	protected Map tempDirHash;

	/**
	 * server core plugin id
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.server.core";

	/**
	 * Create the ServerPlugin.
	 */
	public ServerPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return org.eclipse.wst.server.core.internal.plugin.ServerPlugin
	 */
	public static ServerPlugin getInstance() {
		return singleton;
	}

	/**
	 * Returns a temporary directory that the requestor can use
	 * throughout it's lifecycle. This is primary to be used by
	 * server instances for working directories, instance specific
	 * files, etc.
	 *
	 * <p>As long as the same key is used to call this method on
	 * each use of the workbench, this method directory will return
	 * the same directory. If the directory is not requested over a
	 * period of time, the directory may be deleted and a new one
	 * will be assigned on the next request. For this reason, a
	 * server instance should request the temp directory on startup
	 * if it wants to store files there. In all cases, the instance
	 * should have a backup plan anyway, as this directory may be
	 * deleted accidentally.</p>
	 *
	 * @param key
	 * @return java.io.File
	 */
	public IPath getTempDirectory(String key) {
		if (key == null)
			return null;
	
		// first, look through hash of current directories
		IPath statePath = ServerPlugin.getInstance().getStateLocation();
		try {
			TempDir dir = (TempDir) tempDirHash.get(key);
			if (dir != null) {
				dir.age = 0;
				return statePath.append(dir.path);
			}
		} catch (Exception e) {
			// ignore
		}
	
		// otherwise, create a new directory
	
		// find first free directory
		String path = null;
		File dir = null;
		int count = 0;
		while (dir == null || dir.exists()) {
			path = "tmp" + count;
			dir = statePath.append(path).toFile();
			count ++;
		}
	
		dir.mkdirs();
	
		TempDir d = new TempDir();
		d.path = path;
		tempDirHash.put(key, d);
		saveTempDirInfo();
		return statePath.append(path);
	}
	
	/**
	 * Remove a temp directory.
	 * @param key
	 */
	public void removeTempDirectory(String key) {
		if (key == null)
			return;
		
		IPath statePath = ServerPlugin.getInstance().getStateLocation();
		try {
			TempDir dir = (TempDir) tempDirHash.get(key);
			if (dir != null) {
				tempDirHash.remove(key);
				saveTempDirInfo();
				deleteDirectory(statePath.append(dir.path).toFile(), null);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not remove temp directory", e);
		}
	}

	/**
	 * Load the temporary directory information.
	 */
	private void loadTempDirInfo() {
		Trace.trace(Trace.FINEST, "Loading temporary directory information");
		IPath statePath = ServerPlugin.getInstance().getStateLocation();
		String filename = statePath.append(TEMP_DATA_FILE).toOSString();
		
		tempDirHash = new HashMap();
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("temp-directory");
			int size = children.length;
			for (int i = 0; i < size; i++) {
				String key = children[i].getString("key");
				
				TempDir d = new TempDir();
				d.path = children[i].getString("path");
				d.age = children[i].getInteger("age").intValue();
				d.age++;
				
				tempDirHash.put(key, d);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load temporary directory information: " + e.getMessage());
		}
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	/**
	 * Save the temporary directory information.
	 */
	private void saveTempDirInfo() {
		// save remaining directories
		IPath statePath = ServerPlugin.getInstance().getStateLocation();
		String filename = statePath.append(TEMP_DATA_FILE).toOSString();
	
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("temp-directories");
	
			Iterator iterator = tempDirHash.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				TempDir d = (TempDir) tempDirHash.get(key);
	
				if (d.age < 5) {
					IMemento child = memento.createChild("temp-directory");
					child.putString("key", key);
					child.putString("path", d.path);
					child.putInteger("age", d.age);
				} else
					deleteDirectory(statePath.append(d.path).toFile(), null);
			}
	
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save temporary directory information", e);
		}
	}

	protected void initializeDefaultPluginPreferences() {
		ServerPreferences.getInstance().setDefaults();
	}

	/**
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server Core plugin startup ----->-----");
		super.start(context);
		bundleContext = context;
		
		initializeDefaultPluginPreferences();

		// load temp directory information
		loadTempDirInfo();
		
		bundleListener = new BundleListener() {
			public void bundleChanged(BundleEvent event) {
				String bundleId = event.getBundle().getSymbolicName();
				//Trace.trace(Trace.INFO, event.getType() + " " + bundleId);
				if (BundleEvent.STOPPED == event.getType() && ResourceManager.getInstance().isActiveBundle(bundleId))
					stopBundle(bundleId);
			}
		};
		context.addBundleListener(bundleListener);
	}

	protected void stopBundle(final String bundleId) {
		class StopJob extends Job {
			public StopJob() {
				super("Disposing servers");
			}

			public boolean belongsTo(Object family) {
				return SHUTDOWN_JOB_FAMILY.equals(family);
			}

			public IStatus run(IProgressMonitor monitor2) {
				ResourceManager.getInstance().shutdownBundle(bundleId);
				return Status.OK_STATUS;
			}
		}
		
		try {
			StopJob job = new StopJob();
			job.setUser(false);
			job.schedule();
		} catch (Throwable t) {
			// ignore errors
		}
	}

	/**
	 * @see Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "-----<----- Server Core plugin shutdown -----<-----");
		super.stop(context);
		
		if (registryListener != null)
			Platform.getExtensionRegistry().removeRegistryChangeListener(registryListener);
		
		ResourceManager.shutdown();
		ServerMonitorManager.shutdown();
		
		try {
			Job.getJobManager().join(SHUTDOWN_JOB_FAMILY, null);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error waiting for shutdown job", e);
		}
		context.removeBundleListener(bundleListener);
	}

	/**
	 * Utility method to tokenize a string into an array.
	 * 
	 * @param str a string to be parsed
	 * @param delim the delimiters
	 * @return an array containing the tokenized string
	 */
	public static String[] tokenize(String str, String delim) {
		if (str == null)
			return new String[0];
		
		List list = new ArrayList();
		
		StringTokenizer st = new StringTokenizer(str, delim);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s != null && s.length() > 0)
				list.add(s.trim());
		}
		
		String[] s = new String[list.size()];
		list.toArray(s);
		return s;
	}

	protected static List getModuleTypes(IConfigurationElement[] elements) {
		List list = new ArrayList();
		if (elements == null)
			return list;
	
		int size = elements.length;
		for (int i = 0; i < size; i++) {
			String[] types = tokenize(elements[i].getAttribute("types"), ",");
			String[] versions = tokenize(elements[i].getAttribute("versions"), ",");
			int sizeT = types.length;
			int sizeV = versions.length;
			for (int j = 0; j < sizeT; j++) {
				for (int k = 0; k < sizeV; k++) {
					ModuleType module = new ModuleType(types[j], versions[k]);
					list.add(module);
				}
			}
		}
		return list;
	}
	
	public static String generateId() {
		String s = df.format(new Date()).toString() + num++;
		s = s.replace(' ', '_');
		s = s.replace(':', '_');
		s = s.replace('/', '_');
		s = s.replace('\\', '_');
		return s;
	}

	/**
	 * Returns true if ids contains id.
	 * 
	 * @param ids
	 * @param id
	 * @return true if the id is supported
	 */
	public static boolean supportsType(String[] ids, String id) {
		if (id == null || id.length() == 0)
			return false;

		if (ids == null)
			return true;
		
		int size = ids.length;
		for (int i = 0; i < size; i++) {
			if (ids[i].endsWith("*")) {
				if (id.length() >= ids[i].length() && id.startsWith(ids[i].substring(0, ids[i].length() - 1)))
					return true;
			} else if (id.equals(ids[i]))
				return true;
		}
		return false;
	}
	
	/**
	 * Recursively delete a directory.
	 *
	 * @param dir java.io.File
	 * @param monitor a progress monitor, or <code>null</code> if no progress
	 *    reporting is required
	 */
	public static void deleteDirectory(File dir, IProgressMonitor monitor) {
		try {
			if (!dir.exists() || !dir.isDirectory())
				return;
	
			File[] files = dir.listFiles();
			int size = files.length;
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(NLS.bind(Messages.deletingTask, new String[] {dir.getAbsolutePath()}), size * 10);
	
			// cycle through files
			for (int i = 0; i < size; i++) {
				File current = files[i];
				if (current.isFile()) {
					current.delete();
					monitor.worked(10);
				} else if (current.isDirectory()) {
					monitor.subTask(NLS.bind(Messages.deletingTask, new String[] {current.getAbsolutePath()}));
					deleteDirectory(current, ProgressUtil.getSubMonitorFor(monitor, 10));
				}
			}
			dir.delete();
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error deleting directory " + dir.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Returns an array of all known launchable adapters.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of launchable adapters {@link ILaunchableAdapter}
	 */
	public static ILaunchableAdapter[] getLaunchableAdapters() {
		if (launchableAdapters == null)
			loadLaunchableAdapters();
		ILaunchableAdapter[] la = new ILaunchableAdapter[launchableAdapters.size()];
		launchableAdapters.toArray(la);
		return la;
	}

	/**
	 * Returns an array of all known client instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of client instances {@link IClient}
	 */
	public static IClient[] getClients() {
		if (clients == null)
			loadClients();
		IClient[] c = new IClient[clients.size()];
		clients.toArray(c);
		return c;
	}
	
	/**
	 * Load the launchable adapters extension point.
	 */
	private static synchronized void loadLaunchableAdapters() {
		if (launchableAdapters != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .launchableAdapters extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "launchableAdapters");

		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new LaunchableAdapter(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded launchableAdapter: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load launchableAdapter: " + cf[i].getAttribute("id"), t);
			}
		}
		launchableAdapters = list;
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .launchableAdapters extension point -<-");
	}

	/**
	 * Load the client extension point.
	 */
	private static synchronized void loadClients() {
		if (clients != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .clients extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "clients");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new Client(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded clients: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load clients: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort by index to put lower numbers first in order
		size = list.size();
		for (int i = 0; i < size-1; i++) {
			for (int j = i+1; j < size; j++) {
				Client a = (Client) list.get(i);
				Client b = (Client) list.get(j);
				if (a.getPriority() < b.getPriority()) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
		clients = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .clients extension point -<-");
	}

	/**
	 * Returns an array of all known publish tasks.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of publish tasks instances {@link IPublishTask}
	 */
	public static IPublishTask[] getPublishTasks() {
		if (publishTasks == null)
			loadPublishTasks();
		IPublishTask[] st = new IPublishTask[publishTasks.size()];
		publishTasks.toArray(st);
		return st;
	}

	/**
	 * Load the publish task extension point.
	 */
	private static synchronized void loadPublishTasks() {
		if (publishTasks != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .publishTasks extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "publishTasks");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new PublishTask(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded publishTask: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load publishTask: " + cf[i].getAttribute("id"), t);
			}
		}
		publishTasks = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .publishTasks extension point -<-");
	}

	/**
	 * Returns an array of all known module module factories.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of module factories {@link ModuleFactory}
	 */
	public static ModuleFactory[] getModuleFactories() {
		if (moduleFactories == null)
			loadModuleFactories();
		
		ModuleFactory[] mf = new ModuleFactory[moduleFactories.size()];
		moduleFactories.toArray(mf);
		return mf;
	}

	/**
	 * Returns the module factory with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * module factories ({@link #getModuleFactories()}) for the one a matching
	 * module factory id ({@link ModuleFactory#getId()}). The id may not be null.
	 *
	 * @param id the module factory id
	 * @return the module factory, or <code>null</code> if there is no module factory
	 * with the given id
	 */
	public static ModuleFactory findModuleFactory(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		
		if (moduleFactories == null)
			loadModuleFactories();
		
		Iterator iterator = moduleFactories.iterator();
		while (iterator.hasNext()) {
			ModuleFactory factory = (ModuleFactory) iterator.next();
			if (id.equals(factory.getId()))
				return factory;
		}
		return null;
	}

	/**
	 * Load the module factories extension point.
	 */
	private static synchronized void loadModuleFactories() {
		if (moduleFactories != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleFactories extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "moduleFactories");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new ModuleFactory(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleFactories: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleFactories: " + cf[i].getAttribute("id"), t);
			}
		}
		
		size = list.size();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				ModuleFactory a = (ModuleFactory) list.get(i);
				ModuleFactory b = (ModuleFactory) list.get(j);
				if (a.getOrder() > b.getOrder()) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
		moduleFactories = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleFactories extension point -<-");
	}

	/**
	 * Returns an array of all known server monitor instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of server monitor instances {@link IServerMonitor}
	 */
	public static IServerMonitor[] getServerMonitors() {
		if (monitors == null)
			loadServerMonitors();
		IServerMonitor[] sm = new IServerMonitor[monitors.size()];
		monitors.toArray(sm);
		return sm;
	}

	/**
	 * Load the server monitor extension point.
	 */
	private static synchronized void loadServerMonitors() {
		if (monitors != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .serverMonitors extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "internalServerMonitors");

		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new ServerMonitor(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverMonitor: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverMonitor: " + cf[i].getAttribute("id"), t);
			}
		}
		monitors = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .serverMonitors extension point -<-");
	}

	/**
	 * Returns an array of all known runtime locator instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime locator instances {@link IRuntimeLocator}
	 */
	public static IRuntimeLocator[] getRuntimeLocators() {
		if (runtimeLocators == null)
			loadRuntimeLocators();
		IRuntimeLocator[] rl = new IRuntimeLocator[runtimeLocators.size()];
		runtimeLocators.toArray(rl);
		return rl;
	}

	/**
	 * Load the runtime locators.
	 */
	private static synchronized void loadRuntimeLocators() {
		if (runtimeLocators != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .runtimeLocators extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "runtimeLocators");

		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new RuntimeLocator(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded runtimeLocator: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeLocator: " + cf[i].getAttribute("id"), t);
			}
		}
		runtimeLocators = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .runtimeLocators extension point -<-");
	}

	/**
	 * Returns an array of all module artifact adapters.
	 *
	 * @return a possibly empty array of module artifact adapters
	 */
	protected static ModuleArtifactAdapter[] getModuleArtifactAdapters() {
		if (moduleArtifactAdapters == null)
			loadModuleArtifactAdapters();
		
		ModuleArtifactAdapter[] moa = new ModuleArtifactAdapter[moduleArtifactAdapters.size()];
		moduleArtifactAdapters.toArray(moa);
		return moa;
	}

	/**
	 * Load the module artifact adapters extension point.
	 */
	private static synchronized void loadModuleArtifactAdapters() {
		if (moduleArtifactAdapters != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .moduleArtifactAdapters extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "moduleArtifactAdapters");

		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new ModuleArtifactAdapter(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded moduleArtifactAdapter: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load moduleArtifactAdapter: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort by index to put lower numbers first in order
		size = list.size();
		for (int i = 0; i < size-1; i++) {
			for (int j = i+1; j < size; j++) {
				ModuleArtifactAdapter a = (ModuleArtifactAdapter) list.get(i);
				ModuleArtifactAdapter b = (ModuleArtifactAdapter) list.get(j);
				if (a.getPriority() < b.getPriority()) {
					list.set(i, b);
					list.set(j, a);
				}
			}
		}
		moduleArtifactAdapters = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .moduleArtifactAdapters extension point -<-");
	}

	/**
	 * Returns <code>true</code> if a module artifact may be available for the given object,
	 * and <code>false</code> otherwise.
	 *
	 * @param obj an object
	 * @return <code>true</code> if there is a module artifact adapter
	 */
	public static boolean hasModuleArtifact(Object obj) {
		Trace.trace(Trace.FINEST, "ServerPlugin.hasModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						Trace.trace(Trace.FINER, "Run On Server for " + obj + " is enabled by " + adapters[i].getId());
						return true;
					}
				} catch (CoreException ce) {
					Trace.trace(Trace.WARNING, "Could not use moduleArtifactAdapter", ce);
				}
			}
		}
		
		return false;
	}

	/**
	 * Returns a module artifact if one can be found without loading plugins.
	 * 
	 * @param obj
	 * @return a module artifact, or null
	 */
	public static IModuleArtifact getModuleArtifact(Object obj) {
		Trace.trace(Trace.FINEST, "ServerPlugin.getModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						IModuleArtifact ma = adapters[i].getModuleArtifact(obj);
						if (ma != null)
							return ma;
						/*if (Platform.getAdapterManager().hasAdapter(obj, MODULE_ARTIFACT_CLASS)) {
							return (IModuleArtifact) Platform.getAdapterManager().getAdapter(obj, MODULE_ARTIFACT_CLASS);
						}*/
					}
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not use moduleArtifactAdapter " + adapters[i], e);
				}
			}
		}
		
		return null;
	}

	/**
	 * Returns a module artifact if possible, loading any plugins required.
	 * 
	 * @param obj
	 * @return a module artifact, or null
	 */
	public static IModuleArtifact loadModuleArtifact(Object obj) {
		Trace.trace(Trace.FINEST, "ServerPlugin.loadModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						IModuleArtifact ma = adapters[i].getModuleArtifact(obj);
						if (ma != null)
							return ma;
						/*if (Platform.getAdapterManager().hasAdapter(obj, MODULE_ARTIFACT_CLASS)) {
							return (IModuleArtifact) Platform.getAdapterManager().loadAdapter(obj, MODULE_ARTIFACT_CLASS);
						}*/
					}
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not use moduleArtifactAdapter " + adapters[i], e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns an array of all known installable servers.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of installable servers {@link IInstallableServer}
	 */
	public static IInstallableServer[] getInstallableServers() {
		if (installableServers == null)
			loadInstallableServers();
		
		List availableServers = new ArrayList();
		Iterator iterator = installableServers.iterator();
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		int size = runtimeTypes.length;
		while (iterator.hasNext()) {
			IInstallableServer server = (IInstallableServer) iterator.next();
			boolean found = false;
			for (int i = 0; i < size; i++) {
				if (server.getId().equals(runtimeTypes[i].getId()))
					found = true;
			}
			if (!found)
				availableServers.add(server);
		}
		
		IInstallableServer[] is = new IInstallableServer[availableServers.size()];
		availableServers.toArray(is);
		return is;
	}

	/**
	 * Returns an array of all known installable runtimes.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of installable runtimes {@link IInstallableRuntime}
	 */
	public static IInstallableRuntime[] getInstallableRuntimes() {
		//if (installableRuntimes == null)
			loadInstallableRuntimes();
		
		IInstallableRuntime[] ir = new IInstallableRuntime[installableRuntimes.size()];
		installableRuntimes.toArray(ir);
		return ir;
	}

	/**
	 * Returns the installable runtime for the given runtime type, or <code>null</code>
	 * if none exists.
	 * 
	 * @param runtimeTypeId a runtime type id
	 * @return the installable runtime for the given runtime type, or <code>null</code>
	 *    if none exists {@link IInstallableRuntime}
	 */
	public static IInstallableRuntime findInstallableRuntime(String runtimeTypeId) {
		if (runtimeTypeId == null)
			throw new IllegalArgumentException();
		
		//if (installableRuntimes == null)
			loadInstallableRuntimes();
		
		Iterator iterator = installableRuntimes.iterator();
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		int size = runtimeTypes.length;
		while (iterator.hasNext()) {
			IInstallableRuntime runtime = (IInstallableRuntime) iterator.next();
			for (int i = 0; i < size; i++) {
				if (runtime.getId().equals(runtimeTypeId))
					return runtime;
			}
		}
		
		return null;
	}

	/**
	 * Load the installable servers.
	 */
	private static synchronized void loadInstallableServers() {
		if (installableServers != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .installableServers extension point ->-");
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "installableServers");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new InstallableServer(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded installableServer: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load installableServer: " + cf[i].getAttribute("id"), t);
			}
		}
		installableServers = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .installableServers extension point -<-");
	}

	/**
	 * Load the installable runtimes.
	 */
	private static synchronized void loadInstallableRuntimes() {
		//if (installableRuntimes != null)
		//	return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .installableRuntimes extension point ->-");
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "installableRuntimes");
		
		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new InstallableRuntime(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded installableRuntime: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load installableRuntime: " + cf[i].getAttribute("id"), t);
			}
		}
		installableRuntimes = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .installableRuntimes extension point -<-");
	}

	public static void setRegistryListener(IRegistryChangeListener listener) {
		registryListener = listener; 
	}

	/**
	 * Returns the preference information for the project. The project may not
	 * be null.
	 * 
	 * @param project a project
	 * @return the properties of the project
	 */
	public static ProjectProperties getProjectProperties(IProject project) {
		if (project == null)
			throw new IllegalArgumentException();
		return new ProjectProperties(project);
	}
}