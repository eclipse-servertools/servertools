/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.*;
import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IServer;
/**
 * Helper to obtain and store the global publish information.
 * (what files were published and when) Delegates to PublishState
 * for all server specific publishing information.
 */
public class PublishInfo {
	protected static PublishInfo instance;

	protected static final String PUBLISH_DIR = "publish";

	// map of server ids to Strings of filename containing publish data
	protected Map<String, String> serverIdToPath;

	// map of loaded serverIds to publish info
	protected Map<String, ServerPublishInfo> serverIdToPublishInfo;

	/**
	 * PublishInfo constructor comment.
	 */
	private PublishInfo() {
		super();
	
		serverIdToPath = new HashMap<String, String>();
		serverIdToPublishInfo = new HashMap<String, ServerPublishInfo>();
		load();
	}

	/**
	 * Return the publish info.
	 * 
	 * @return org.eclipse.wst.server.core.internal.PublishInfo
	 */
	public static PublishInfo getInstance() {
		if (instance == null) {
			synchronized (PUBLISH_DIR) {
				instance = new PublishInfo();
			}
		}
		return instance;
	}

	/**
	 * Return the publish state.
	 * 
	 * @return org.eclipse.wst.server.core.internal.PublishState
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public ServerPublishInfo getServerPublishInfo(IServer server) {
		// have we tried loading yet?
		String serverId = server.getId();
		if (serverIdToPath.containsKey(serverId)) {
			if (serverIdToPublishInfo.containsKey(serverId))
				return serverIdToPublishInfo.get(serverId);
			
			// force .dat extension
			String partialPath = serverIdToPath.get(serverId);
			partialPath = partialPath.substring(0, partialPath.length() - 3) + "dat";
			IPath path = ServerPlugin.getInstance().getStateLocation().append(PUBLISH_DIR).append(partialPath);
			ServerPublishInfo spi = new ServerPublishInfo(path);
			serverIdToPublishInfo.put(serverId, spi);
			return spi;
		}
		
		// first time server is being used
		IPath path = ServerPlugin.getInstance().getStateLocation().append(PUBLISH_DIR);
		File file = new File(path.toOSString());
		if (!file.exists())
			file.mkdir();
		
		file = null;
		int i = 0;
		String partialPath = null;
		ServerPublishInfo spi = null;
		synchronized (PUBLISH_DIR) {
			while (file == null || file.exists()) {
				partialPath = "publish" + i + ".dat";
				path = ServerPlugin.getInstance().getStateLocation().append(PUBLISH_DIR).append(partialPath);
				if (serverIdToPath.get(partialPath) == null)
					file = new File(path.toOSString());
				i++;
			}
			
			spi = new ServerPublishInfo(path);
			serverIdToPath.put(serverId, partialPath);
			serverIdToPublishInfo.put(serverId, spi);
			save();
		}
		return spi;
	}

	/**
	 * Remove the server publish state.
	 * 
	 * @param server org.eclipse.wst.server.core.IServer
	 */
	public void removeServerPublishInfo(IServer server) {
		IPath path = ServerPlugin.getInstance().getStateLocation().append(PUBLISH_DIR);
		File file = new File(path.toOSString());
		if (!file.exists())
			return;
		
		String serverId = server.getId();
		String path2 = serverIdToPath.get(serverId);
		synchronized (PUBLISH_DIR) {
			serverIdToPath.remove(serverId);
			serverIdToPublishInfo.remove(serverId);
			save();
		}
		if (path2 != null) {
			path.append(path2).toFile().delete();
			path2 = path2.substring(0, path2.length() - 3) + "xml";
			path.append(path2).toFile().delete();
			path2 = path2.substring(0, path2.length() - 3) + "dat";
			path.append(path2).toFile().delete();
		}
	}

	/**
	 * 
	 */
	protected void load() {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Loading publish info");
		}
		String filename = ServerPlugin.getInstance().getStateLocation().append("publish.xml").toOSString();
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] serverChild = memento.getChildren("server");
			int size = serverChild.length;
			serverIdToPath = new HashMap<String, String>(size + 2);
			
			for (int i = 0; i < size; i++) {
				String id = serverChild[i].getString("id");
				String partialPath = serverChild[i].getString("path");
				serverIdToPath.put(id, partialPath);
			}
		} catch (Exception e) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Could not load global publish info", e);
			}
		}
	}

	/**
	 * 
	 */
	protected void save() {
		String filename = ServerPlugin.getInstance().getStateLocation().append("publish.xml").toOSString();
		
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("publish-info");
			
			Iterator iterator = serverIdToPath.keySet().iterator();
			while (iterator.hasNext()) {
				String serverId = (String) iterator.next();
				String partialPath = serverIdToPath.get(serverId);
				
				IMemento server = memento.createChild("server");
				server.putString("id", serverId);
				server.putString("path", partialPath);
			}
			
			memento.saveToFile(filename);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not save global publish info", e);
			}
		}
	}
}
