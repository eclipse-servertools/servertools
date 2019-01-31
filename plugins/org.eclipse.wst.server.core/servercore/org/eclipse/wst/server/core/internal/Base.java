/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
package org.eclipse.wst.server.core.internal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.osgi.util.NLS;
/**
 * Helper class for storing runtime and server attributes.
 */
public abstract class Base {
	protected static final String PROP_LOCKED = "locked";
	protected static final String PROP_PRIVATE = "private";
	protected static final String PROP_NAME = "name";
	protected static final String PROP_ID = "id";
	protected static final String PROP_ID_SET = "id-set";
	protected static final String PROP_TIMESTAMP = "timestamp";

	protected Map<String, Object> map = new HashMap<String, Object>();

	// file loaded from, or null if it is saved in metadata
	protected IFile file;

	/**
	 * Create a new object.
	 * 
	 * @param file
	 */
	public Base(IFile file) {
		this.file = file;
	}

	/**
	 * Create a new object.
	 * 
	 * @param file
	 * @param id
	 */
	public Base(IFile file, String id) {
		this.file = file;
		if (id != null && id.length() > 0) {
			map.put(PROP_ID, id);
			map.put(PROP_ID_SET, Boolean.toString(true));
		}
	}

	/**
	 * Returns the timestamp of this object.
	 * Timestamps are monotonically increased each time the object is saved
	 * and can be used to determine if any changes have been made on disk
	 * since the object was loaded.
	 * 
	 * @return the object's timestamp
	 */
	public int getTimestamp() {
		return getAttribute(PROP_TIMESTAMP, -1);
	}

	/**
	 * Returns the file where this server instance is serialized.
	 * 
	 * @return the file in the workspace where the server instance
	 * is serialized, or <code>null</code> if the information is
	 * instead to be persisted with the workspace but not with any
	 * particular workspace resource
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * Returns <code>true</code> if the attribute is currently set, and <code>false</code>
	 * otherwise.
	 * 
	 * @param attributeName
	 * @return <code>true</code> if the attribute is currently set, and <code>false</code>
	 *    otherwise
	 */
	public boolean isAttributeSet(String attributeName) {
		try {
			Object obj = map.get(attributeName);
			if (obj != null)
				return true;
		} catch (Exception e) {
			// ignore
		}
		return false;
	}

	public String getAttribute(String attributeName, String defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return (String) obj;
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public int getAttribute(String attributeName, int defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return Integer.parseInt((String) obj);
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return Boolean.valueOf((String) obj).booleanValue();
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAttribute(String attributeName, List<String> defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return (List<String>) obj;
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public Map getAttribute(String attributeName, Map defaultValue) {
		try {
			Object obj = map.get(attributeName);
			if (obj == null)
				return defaultValue;
			return (Map) obj;
		} catch (Exception e) {
			// ignore
		}
		return defaultValue;
	}

	public String getId() {
		return getAttribute(PROP_ID, "");
	}

	public String getName() {
		return getAttribute(PROP_NAME, "");
	}

	public boolean isReadOnly() {
		return getAttribute(PROP_LOCKED, false);
	}

	/**
	 * Returns <code>true</code> if this runtime is private (not shown
	 * in the UI to the users), and <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this runtime is private,
	 *    and <code>false</code> otherwise
	 */
	public boolean isPrivate() {
		return getAttribute(PROP_PRIVATE, false);
	}
	
	public boolean isWorkingCopy() {
		return false;
	}
	
	protected abstract String getXMLRoot();
	
	protected void save(IMemento memento) {
		//IMemento child = memento.createChild("properties");
		IMemento child = memento;
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object obj = map.get(key);
			if (obj instanceof String)
				child.putString(key, (String) obj);
			else if (obj instanceof Integer) {
				Integer in = (Integer) obj;
				child.putInteger(key, in.intValue());
			} else if (obj instanceof Boolean) {
				Boolean bool = (Boolean) obj;
				child.putBoolean(key, bool.booleanValue());
			} else if (obj instanceof List) {
				List list = (List) obj;
				saveList(child, key, list);
			} else if (obj instanceof Map) {
				Map map2 = (Map) obj;
				saveMap(child, key, map2);
				
			}
		}
		saveState(child);
	}
	protected void saveMap(IMemento memento, String key, Map map2) {
		IMemento child = memento.createChild("map");
		child.putString("key", key);
		Iterator iterator = map2.keySet().iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			child.putString(s, (String)map2.get(s));
		}
	}
	
	protected void saveList(IMemento memento, String key, List list) {
		IMemento child = memento.createChild("list");
		child.putString("key", key);
		int i = 0;
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			child.putString("value" + (i++), s);
		}
	}

	protected void saveToFile(IProgressMonitor monitor) throws CoreException {
		try {
			XMLMemento memento = XMLMemento.createWriteRoot(getXMLRoot());
			save(memento);

			InputStream in = memento.getInputStream();
			if (file.exists())
				file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 1000));
			else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 1000));
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not save " + getXMLRoot(), e);
			}
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorSaving, getFile().toString()), e));
		}
	}

	protected void doSave(IProgressMonitor monitor) throws CoreException {
		if (file != null)
			saveToFile(monitor);
		else
			saveToMetadata(monitor);
		ResourceManager.getInstance().resolveServers();
	}

	protected void saveToMetadata(IProgressMonitor monitor) {
		// do nothing
	}

	protected abstract void saveState(IMemento memento);

	protected void load(IMemento memento) {
		map = new HashMap<String, Object>();
		
		Iterator<String> iterator = memento.getNames().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			map.put(key, memento.getString(key));
		}
		IMemento[] children = memento.getChildren("list");
		if (children != null) {
			for (IMemento child : children)
				loadList(child);
		}
		IMemento[] maps = memento.getChildren("map");
		if (maps != null) {
			for (IMemento m : maps)
				loadMap(m);
		}
		
		loadState(memento);
	}

	protected void loadMap(IMemento memento) {
		String key = memento.getString("key");
		Map<String, String> vMap = new HashMap<String, String>();
		Iterator<String> iterator = memento.getNames().iterator();
		while(iterator.hasNext()) {
			String s = iterator.next();
			String v = memento.getString(s);
			vMap.put(s,v);
		}
		map.put(key, vMap);
	}

	protected void loadList(IMemento memento) {
		String key = memento.getString("key");
		List<String> list = new ArrayList<String>();
		int i = 0;
		String key2 = memento.getString("value" + (i++));
		while (key2 != null) {
			list.add(key2);
			key2 = memento.getString("value" + (i++));
		}
		map.put(key, list);
	}
	
	protected abstract void loadState(IMemento memento);
	
	protected void resolve() {
		// do nothing
	}
	
	public void delete() throws CoreException {
		if (isWorkingCopy())
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "Cannot delete a working copy", null));
		
		if (file != null)
			deleteFromFile();
		else
			deleteFromMetadata();
	}

	protected void deleteFromFile() throws CoreException {
		file.delete(true, true, new NullProgressMonitor());
	}

	protected void deleteFromMetadata() {
		// do nothing
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Base))
			return false;
		
		Base base = (Base) obj;
		if (getId() == null)
			return false;
		if (!getId().equals(base.getId()))
			return false;
		
		if (isWorkingCopy() != base.isWorkingCopy())
			return false;
		
		if (isWorkingCopy() && this != base)
			return false;
		
		return true;
	}

	/**
	 * 
	 */
	protected void loadFromFile(IProgressMonitor monitor) throws CoreException {
		InputStream in = null;
		try {
			in = file.getContents();
			IMemento memento = XMLMemento.loadMemento(in);
			load(memento);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not load from file", e);
			}
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorLoading, getFile().toString()), e));
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	protected void loadFromMemento(IMemento memento, IProgressMonitor monitor) {
		load(memento);
	}
	
	/**
	 * 
	 */
	protected void loadFromPath(IPath path, IProgressMonitor monitor) throws CoreException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(path.toFile()));
			IMemento memento = XMLMemento.loadMemento(in);
			load(memento);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not load from path", e);
			}
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorLoading, path.toString()), e));
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	public IStatus validateEdit(Object context) {
		if (file == null)
			return null;
	
		return file.getWorkspace().validateEdit(new IFile[] { file }, context);
	}
}