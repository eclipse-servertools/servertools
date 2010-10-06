/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
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
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
/**
 * The main server plugin class.
 */
public class ServerPlugin extends Plugin {
	public static final String PROJECT_PREF_FILE = ".serverPreference";
	public static final String EXCLUDE_SERVER_ADAPTERS = "excludeServerAdapters";

	private static final String SHUTDOWN_JOB_FAMILY = "org.eclipse.wst.server.core.family";
	//public static final String REGISTRY_JOB_FAMILY = "org.eclipse.wst.server.registry.family";

	protected static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	protected static int num = 0;

	// cached copy of all launchable adapters
	private static List<LaunchableAdapter> launchableAdapters;

	// cached copy of all launchable clients
	private static List<IClient> clients;

	// cached copy of all module factories
	private static List<ModuleFactory> moduleFactories;

	// singleton instance of this class
	private static ServerPlugin singleton;

	// cached copy of all publish tasks
	private static List<IPublishTask> publishTasks;

	// cached copy of all publishers
	private static List<Publisher> publishers;

	//	cached copy of all server monitors
	private static List<IServerMonitor> monitors;

	//	cached copy of all runtime locators
	private static List<IRuntimeLocator> runtimeLocators;

	// cached copy of all module artifact adapters
	private static List<ModuleArtifactAdapter> moduleArtifactAdapters;

	//	cached copy of all installable servers
	private static List<IInstallableServer> installableServers;

	//	cached copy of all installable runtimes
	private static List<IInstallableRuntime> installableRuntimes;

	// cached copy of SaveEditorPrompter
	private static SaveEditorPrompter saveEditorPrompter;
	
	// cached copy of isRunningInGUICache
	public static boolean isRunningInGUICache = false;

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
	protected Map<String, TempDir> tempDirHash;

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
			TempDir dir = tempDirHash.get(key);
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
			TempDir dir = tempDirHash.get(key);
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
		
		tempDirHash = new HashMap<String, TempDir>();
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
			Trace.trace(Trace.WARNING, "Could not load temporary directory information", e);
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
				TempDir d = tempDirHash.get(key);
	
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

	/**
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server Core plugin startup ----->-----");
		super.start(context);
		bundleContext = context;
		
		ServerPreferences.getInstance().setDefaults();

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
	 * Logs an error message caused by a missing or failed extension
	 * (server adapters, runtime, class-path provider, etc.). A single
	 * error is output to the .log (once per session) and the full
	 * exception is output to tracing.
	 * 
	 * @param id the id of the missing/failed extension
	 * @param t a throwable or exception
	 */
	public static void logExtensionFailure(String id, Throwable t) {
		Trace.trace(Trace.SEVERE, "Missing or failed server extension: " + id + ". Enable tracing for more information");
		Trace.trace(Trace.WARNING, "Exception in server delegate", t);
	}

	private static void addAll(List<Object> list, Object[] obj) {
		if (obj == null)
			return;
		
		int size = obj.length;
		for (int i = 0; i < size; i++) {
			list.add(obj[i]);
		}
	}

	/**
	 * Returns true if a server or runtime exists with the given name.
	 *
	 * @param element an object
	 * @param name a name
	 * @return <code>true</code> if the name is in use, and <code>false</code>
	 *    otherwise
	 */
	public static boolean isNameInUse(Object element, String name) {
		if (name == null)
			return true;
		
		List<Object> list = new ArrayList<Object>();
		
		if (element == null || element instanceof IRuntime)
			addAll(list, ServerCore.getRuntimes());
		if (element == null || element instanceof IServerAttributes)
			addAll(list, ServerCore.getServers());
		
		if (element != null) {
			if (element instanceof IRuntimeWorkingCopy)
				element = ((IRuntimeWorkingCopy) element).getOriginal();
			if (element instanceof IServerWorkingCopy)
				element = ((IServerWorkingCopy) element).getOriginal();
			if (list.contains(element))
				list.remove(element);
		}
		
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IServerAttributes && 
					(name.equalsIgnoreCase(((IServerAttributes)obj).getName()) || name.equalsIgnoreCase(((IServerAttributes)obj).getId()))) 
				return true;
			if (obj instanceof IRuntime && name.equalsIgnoreCase(((IRuntime)obj).getName()))
				return true;
		}
		
		return false;
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
		
		List<String> list = new ArrayList<String>();
		
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

	protected static List<org.eclipse.wst.server.core.IModuleType> getModuleTypes(IConfigurationElement[] elements) {
		List<IModuleType> list = new ArrayList<IModuleType>();
		if (elements == null)
			return list;
		
		int size = elements.length;
		for (int i = 0; i < size; i++) {
			String[] types = tokenize(elements[i].getAttribute("types"), ",");
			String[] versions = tokenize(elements[i].getAttribute("versions"), ",");
			int sizeT = types.length;
			int sizeV = versions.length;
			for (int j = 0; j < sizeT; j++) {
				for (int k = 0; k < sizeV; k++)
					list.add(ModuleType.getModuleType(types[j], versions[k]));
			}
		}
		return list;
	}

	/**
	 * Returns <code>true</code> if the array of strings contains a specific
	 * string, or <code>false</code> otherwise.
	 * 
	 * @param ids an array of strings, or <code>null</code>
	 * @param id a string
	 * @return <code>true</code> if the array of strings contains a specific
	 *    string, or <code>false</code> otherwise
	 */
	public static boolean contains(String[] ids, String id) {
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
	 * @param dir a folder
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
		List<LaunchableAdapter> list = new ArrayList<LaunchableAdapter>(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new LaunchableAdapter(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded launchableAdapter: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load launchableAdapter: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort by priority to put higher numbers first
		size = list.size();
		for (int i = 0; i < size-1; i++) {
			for (int j = i+1; j < size; j++) {
				LaunchableAdapter a = list.get(i);
				LaunchableAdapter b = list.get(j);
				if (a.getPriority() < b.getPriority()) {
					list.set(i, b);
					list.set(j, a);
				}
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
		List<IClient> list = new ArrayList<IClient>(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new Client(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded clients: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load clients: " + cf[i].getAttribute("id"), t);
			}
		}
		
		// sort by priority to put higher numbers first
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
		List<IPublishTask> list = new ArrayList<IPublishTask>(size);
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
	 * Returns an array of all known publishers.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of publisher instances {@link Publisher}
	 */
	public static Publisher[] getPublishers() {
		if (publishers == null)
			loadPublishers();
		Publisher[] pub = new Publisher[publishers.size()];
		publishers.toArray(pub);
		return pub;
	}

	/**
	 * Returns the publisher with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * publisher ({@link #getPublishers()}) for the one a matching
	 * publisher id ({@link Publisher#getId()}). The id may not be null.
	 *
	 * @param id the publisher id
	 * @return the publisher, or <code>null</code> if there is no publishers
	 *    with the given id
	 */
	public static Publisher findPublisher(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		
		if (publishers == null)
			loadPublishers();
		
		Iterator iterator = publishers.iterator();
		while (iterator.hasNext()) {
			Publisher pub = (Publisher) iterator.next();
			if (id.equals(pub.getId()))
				return pub;
		}
		return null;
	}

	/**
	 * Load the publisher extension point.
	 */
	private static synchronized void loadPublishers() {
		if (publishers != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .publishers extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "publishers");
		
		int size = cf.length;
		List<Publisher> list = new ArrayList<Publisher>(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new Publisher(cf[i]));
				Trace.trace(Trace.EXTENSION_POINT, "  Loaded publisher: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load publisher: " + cf[i].getAttribute("id"), t);
			}
		}
		publishers = list;
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .publishers extension point -<-");
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
	 *    with the given id
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
	 * Returns the launchable adapter with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * launchable adapters ({@link #getLaunchableAdapters()}) for the one a matching
	 * launchable adapter id ({@link ILaunchableAdapter#getId()}). The id may not be null.
	 *
	 * @param id the launchable adapter id
	 * @return the launchable adapter, or <code>null</code> if there is no launchable adapter
	 *    with the given id
	 */
	public static ILaunchableAdapter findLaunchableAdapter(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		
		if (launchableAdapters == null)
			loadLaunchableAdapters();
		
		Iterator iterator = launchableAdapters.iterator();
		while (iterator.hasNext()) {
			ILaunchableAdapter la = (ILaunchableAdapter) iterator.next();
			if (id.equals(la.getId()))
				return la;
		}
		return null;
	}

	/**
	 * Returns the client with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known
	 * clients ({@link #getClients()}) for the one a matching
	 * client id ({@link IClient#getId()}). The id may not be null.
	 *
	 * @param id the client id
	 * @return the client, or <code>null</code> if there is no client
	 *    with the given id
	 */
	public static IClient findClient(String id) {
		if (id == null)
			throw new IllegalArgumentException();
		
		if (clients == null)
			loadClients();
		
		Iterator iterator = clients.iterator();
		while (iterator.hasNext()) {
			IClient client = (IClient) iterator.next();
			if (id.equals(client.getId()))
				return client;
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
		List<ModuleFactory> list = new ArrayList<ModuleFactory>(size);
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
				ModuleFactory a = list.get(i);
				ModuleFactory b = list.get(j);
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
		List<IServerMonitor> list = new ArrayList<IServerMonitor>(size);
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
		List<IRuntimeLocator> list = new ArrayList<IRuntimeLocator>(size);
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
		List<ModuleArtifactAdapter> list = new ArrayList<ModuleArtifactAdapter>(size);
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
				ModuleArtifactAdapter a = list.get(i);
				ModuleArtifactAdapter b = list.get(j);
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
						Trace.trace(Trace.FINER, "ServerPlugin.hasModuleArtifact() - " + adapters[i].getId());
						if (adapters[i].isDelegateLoaded()) {
							long time = System.currentTimeMillis();
							IModuleArtifact[] ma = adapters[i].getModuleArtifacts(obj);
							Trace.trace(Trace.FINER, "Deep enabled time: " + (System.currentTimeMillis() - time));
							if (ma != null) {
								Trace.trace(Trace.FINER, "Deep enabled");
								return true;
							}
							Trace.trace(Trace.FINER, "Not enabled");
						} else {
							Trace.trace(Trace.FINER, "Enabled");
							return true;
						}
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
	public static IModuleArtifact[] getModuleArtifacts(Object obj) {
		Trace.trace(Trace.FINEST, "ServerPlugin.getModuleArtifact() " + obj);
		ModuleArtifactAdapter[] adapters = getModuleArtifactAdapters();
		if (adapters != null) {
			int size = adapters.length;
			for (int i = 0; i < size; i++) {
				try {
					if (adapters[i].isEnabled(obj)) {
						IModuleArtifact[] ma = adapters[i].getModuleArtifacts(obj);
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
		
		List<IInstallableServer> availableServers = new ArrayList<IInstallableServer>();
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
		//IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		//int size = runtimeTypes.length;
		while (iterator.hasNext()) {
			IInstallableRuntime runtime = (IInstallableRuntime) iterator.next();
			//for (int i = 0; i < size; i++) {
				if (runtime.getId().equals(runtimeTypeId))
					return runtime;
			//}
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
		List<IInstallableServer> list = new ArrayList<IInstallableServer>(size);
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
		List<IInstallableRuntime> list = new ArrayList<IInstallableRuntime>(size);
		for (int i = 0; i < size; i++) {
			try {
				if ("runtime".equals(cf[i].getName())) {
					String os = cf[i].getAttribute("os");
					if (os == null || os.contains(Platform.getOS()))
						list.add(new InstallableRuntime2(cf[i]));
				} else
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

	/**
	 * Returns <code>true</code> if a and b match, and <code>false</code> otherwise.
	 * Strings match if they are equal, if either is null, if either is "*", or if
	 * one ends with ".*" and the beginning matches the other string.
	 * 
	 * @param a a string to match
	 * @param b another string to match
	 * @return <code>true</code> if a and b match, and <code>false</code> otherwise
	 */
	public static boolean matches(String a, String b) {
		if (a == null || b == null || a.equals(b) || "*".equals(a) || "*".equals(b)
			|| (a.endsWith(".*") && b.startsWith(a.substring(0, a.length() - 1)))
			|| (b.endsWith(".*") && a.startsWith(b.substring(0, b.length() - 1))))
			return true;
		if (a.startsWith(b) || b.startsWith(a)) {
			Trace.trace(Trace.WARNING, "Invalid matching rules used: " + a + "/" + b);
			return true;
		}
		return false;
	}

	public static String[] getExcludedServerAdapters() {
		return tokenize(getProperty(EXCLUDE_SERVER_ADAPTERS), ",");
	}

	private static String getProperty(String key) {
		if (key == null)
			return null;
		String value = null;
		if (Platform.getProduct() != null)
			return Platform.getProduct().getProperty(key);
		return value;
	}
	
	public static boolean isRunningGUIMode(){
		// check only when the the plugin is not Bundle.ACTIVE
		if (isRunningInGUICache == true){
			return isRunningInGUICache;
		}

		Bundle swtEclipseUIbndl = Platform.getBundle("org.eclipse.ui");  //running in GUI mode if it is active.
		if(swtEclipseUIbndl != null){
			isRunningInGUICache= (swtEclipseUIbndl.getState() == Bundle.ACTIVE);
			return isRunningInGUICache; 
		}			
		return false;
	}	


	/**
	 * Transfer the control to the UI and prompts to save all the editors
	 */
	public static SaveEditorPrompter getSaveEditorHelper() {
		loadSaveEditorExtension();
		return saveEditorPrompter;
	}

	private static void loadSaveEditorExtension() {
		if (saveEditorPrompter != null)
			return;
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading .saveEditorPrompter extension point ->-");
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerPlugin.PLUGIN_ID, "saveEditorPrompter");
		
		int size = cf.length;
		try{
			saveEditorPrompter = (SaveEditorPrompter)cf[0].createExecutableExtension("class");
			Trace.trace(Trace.EXTENSION_POINT, "  Loaded saveEditorPrompter: " + cf[0].getAttribute("id"));
		} catch (CoreException ce){
			Trace.trace(Trace.SEVERE, "  Could not load saveEditorPrompter: " + cf[0].getAttribute("id"), ce);			
		}
		if (size < 1) {
			Trace.trace(Trace.WARNING, "  More than one .saveEditorPrompter found, only one loaded =>"+ cf[0].getAttribute("id"));
		}
		
		Trace.trace(Trace.EXTENSION_POINT, "-<- Done loading .saveEditorPrompter extension point -<-");
		
	}
	
	
}