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

import java.util.*;
import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
/**
 * Helper to obtain and store the global publish information.
 * (what files were published and when) Delegates to PublishState
 * for all server specific publishing information.
 */
public class PublishInfo {
	protected static PublishInfo instance;

	protected static final String PUBLISH_DIR = "publish";

	// map of server refs to IPaths
	protected Map servers;

	// map of control ref to PublishControl
	protected Map publishControls;

	// list of serverRefs that have been loaded
	protected List loadedServers;

	/**
	 * PublishInfo constructor comment.
	 */
	private PublishInfo() {
		super();
	
		servers = new HashMap();
		loadedServers = new ArrayList();
		publishControls = new HashMap();
		load();
	}

	/**
	 * Return the publish info.
	 * 
	 * @return org.eclipse.wst.server.core.internal.PublishInfo
	 */
	public static PublishInfo getPublishInfo() {
		if (instance == null)
			instance = new PublishInfo();
		return instance;
	}

	protected String getPublishControlRef(IServer server, IModule[] parents, IModule module) {
		StringBuffer sb = new StringBuffer();
		sb.append(server.getId());

		if (parents != null) {
			int size = parents.length;
			for (int i = 0; i < size; i++) {
				sb.append("#");
				sb.append(parents[i].getId());
			}
		}
		
		sb.append("#");
		sb.append(module.getId());
		return sb.toString();
	}
	
	protected String getPublishControlRef(String serverId, String parentsRef, String memento) {
		StringBuffer sb = new StringBuffer();
		sb.append(serverId);
		
		if (parentsRef != null && parentsRef.length() > 0) {
			sb.append("#");
			sb.append(parentsRef);
		}
		
		sb.append("#");
		sb.append(memento);
		return sb.toString();
	}
	
	protected String getParentsMemento(IModule[] parents) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		if (parents != null) {
			int size = parents.length;
			for (int i = 0; i < size; i++) {
				if (!first)
					sb.append("#");
				else
					first = false;
				sb.append(parents[i].getId());
			}
		}
		
		return sb.toString();
	}

	/**
	 * Return the publish state.
	 * 
	 * @return org.eclipse.wst.server.core.internal.PublishState
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public PublishControl getPublishControl(IServer server, IModule[] parents, IModule module) {
		String controlRef = getPublishControlRef(server, parents, module);
		
		// have we tried loading yet?
		String serverId = server.getId();
		if (servers.containsKey(serverId)) {
			if (!loadedServers.contains(serverId)) {
				loadServerPublishControls(serverId);
				loadedServers.add(serverId);
			} else {
				// already loaded
			}
		} else {
			// first time server is being used
			IPath path = ServerPlugin.getInstance().getStateLocation().append(PUBLISH_DIR);
			File file = new File(path.toOSString());
			if (!file.exists())
				file.mkdir();
	
			file = null;
			int i = 0;
			while (file == null || file.exists()) {
				path = ServerPlugin.getInstance().getStateLocation().append(PUBLISH_DIR).append("data" + i + ".xml");
				if (servers.get(path) == null)
					file = new File(path.toOSString());
				i++;
			}
			
			servers.put(serverId, path);
			loadedServers.add(serverId);
			save();
		}

		// check if it now exists
		if (publishControls.containsKey(controlRef)) {
			PublishControl control = (PublishControl) publishControls.get(controlRef);
			if (control != null)
				return control;
		}
	
		// have to create a new one
		PublishControl control = new PublishControl(getParentsMemento(parents), module.getId());
		publishControls.put(controlRef, control);
		return control;
	}

	/**
	 * 
	 */
	protected void load() {
		Trace.trace(Trace.FINEST, "Loading publish info");
		String filename = ServerPlugin.getInstance().getStateLocation().append("publish.xml").toOSString();
	
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
	
			IMemento[] serverChild = memento.getChildren("server");
			int size = serverChild.length;
			servers = new HashMap(size + 2);
	
			for (int i = 0; i < size; i++) {
				String id = serverChild[i].getString("id");
				String path = serverChild[i].getString("path");
	
				servers.put(id, new Path(path));
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load global publish info: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	protected void save() {
		String filename = ServerPlugin.getInstance().getStateLocation().append("publish.xml").toOSString();
	
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("publish-info");
	
			Iterator iterator = servers.keySet().iterator();
			while (iterator.hasNext()) {
				String serverId = (String) iterator.next();
				IPath path = (IPath) servers.get(serverId);
	
				IMemento server = memento.createChild("server");
				server.putString("id", serverId);
				server.putString("path", path.toString());
			}
	
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save global publish info", e);
		}
	}
	
	public void save(IServer server) {
		saveServerPublishControls(server.getId());
	}

	/**
	 * 
	 */
	protected void loadServerPublishControls(String serverRef) {
		IPath path = (IPath) servers.get(serverRef);
		String filename = path.toOSString();
		Trace.trace(Trace.FINEST, "Loading publish controls from " + filename);

		try {
			IMemento memento2 = XMLMemento.loadMemento(filename);
			IMemento[] children = memento2.getChildren("module");
	
			int size = children.length;
			for (int i = 0; i < size; i++) {
				PublishControl control = new PublishControl(children[i]);
				publishControls.put(getPublishControlRef(serverRef, control.getParentsRef(), control.getMemento()), control);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load publish control information: " + e.getMessage());
		}
	}
	
	/**
	 * 
	 */
	protected void saveServerPublishControls(String serverRef) {
		if (!servers.containsKey(serverRef))
			return;

		IPath path = (IPath) servers.get(serverRef);
		String filename = path.toOSString();
		Trace.trace(Trace.FINEST, "Saving publish controls to " + filename);
	
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("server");

			Iterator iterator = publishControls.keySet().iterator();
			while (iterator.hasNext()) {
				String controlRef = (String) iterator.next();
				if (controlRef.startsWith(serverRef + "#")) {
					PublishControl control = (PublishControl) publishControls.get(controlRef);
					IMemento child = memento.createChild("module");
					control.save(child);
				}
			}
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save publish control information", e);
		}
	}
}