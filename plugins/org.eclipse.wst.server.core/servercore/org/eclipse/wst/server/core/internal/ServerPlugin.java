/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.MessageFormat;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.util.*;
import org.osgi.framework.BundleContext;
/**
 * The main server plugin class.
 */
public class ServerPlugin extends Plugin {
	public static final String PROJECT_PREF_FILE = ".serverPreference";
	
	protected static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	protected static int num = 0;

	// singleton instance of this class
	private static ServerPlugin singleton;

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
	 * Returns the translated String found with the given key.
	 *
	 * @param key java.lang.String
	 * @return java.lang.String
	 */
	public static String getResource(String key) {
		try {
			return Platform.getResourceString(getInstance().getBundle(), key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given arguments using java.text.MessageFormat.
	 *
	 * @param key java.lang.String
	 * @param arguments java.lang.Object[]
	 * @return java.lang.String
	 */
	public static String getResource(String key, Object[] arguments) {
		try {
			String text = getResource(key);
			return MessageFormat.format(text, arguments);
		} catch (Exception e) {
			return key;
		}
	}
	
	/**
	 * Returns the translated String found with the given key,
	 * and formatted with the given arguments using java.text.MessageFormat.
	 *
	 * @param key java.lang.String
	 * @param arguments java.lang.Object[]
	 * @return java.lang.String
	 */
	public static String getResource(String key, String arg) {
		return getResource(key, new String[] { arg });
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
	public void removeTempDirectory(String key, IProgressMonitor monitor) {
		if (key == null)
			return;
		
		IPath statePath = ServerPlugin.getInstance().getStateLocation();
		try {
			TempDir dir = (TempDir) tempDirHash.get(key);
			if (dir != null) {
				tempDirHash.remove(key);
				saveTempDirInfo();
				FileUtil.deleteDirectory(statePath.append(dir.path).toFile(), monitor);
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
			Trace.trace(Trace.SEVERE, "Could not load temporary directory information: " + e.getMessage());
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
				} else {
					FileUtil.deleteDirectory(statePath.append(d.path).toFile(), new NullProgressMonitor());
				}
			}
	
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save temporary directory information", e);
		}
	}
	
	protected void initializeDefaultPluginPreferences() {
		ServerPreferences.getServerPreferences().setDefaults();
	}

	/**
	 * Start up this plug-in.
	 */
	public void start(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "----->----- Server Core plugin startup ----->-----");
		super.start(context);
		
		initializeDefaultPluginPreferences();

		// load temp directory information
		loadTempDirInfo();
	}

	/**
	 * Shuts down this plug-in and saves all plug-in state.
	 */
	public void stop(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG, "-----<----- Server Core plugin shutdown -----<-----");
		super.stop(context);
		
		ResourceManager.shutdown();
		ServerMonitorManager.shutdown();
	}

	public static String[] tokenize(String param, String delim) {
		if (param == null)
			return new String[0];
		
		List list = new ArrayList();
		
		StringTokenizer st = new StringTokenizer(param, delim);
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (str != null && str.length() > 0)
				list.add(str.trim());
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
	 * @param ids
	 * @param id
	 * @return
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
}