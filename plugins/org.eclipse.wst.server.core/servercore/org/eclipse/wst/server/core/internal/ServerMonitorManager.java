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

import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.*;
/**
 * 
 */
public class ServerMonitorManager implements IServerMonitorManager {
	private static final String MONITOR_DATA_FILE = "monitors.xml";
	
	protected static ServerMonitorManager instance;

	protected List<MonitoredPort> ports = new ArrayList<MonitoredPort>(); 
	protected ServerMonitor monitor;
	
	class MonitoredPort implements IMonitoredServerPort {
		protected IServer server;
		protected ServerPort port;
		protected int newPort = -1;
		protected String[] content;
		public boolean started;
		
		public MonitoredPort(IServer server, ServerPort port, int newPort, String[] content) {
			this.server = server;
			this.port = port;
			this.newPort = newPort;
			this.content = content;
		}
		
		public MonitoredPort(IMemento memento, IProgressMonitor monitor) {
			load(memento, monitor);
		}
		
		public IServer getServer() {
			return server;
		}
		
		public ServerPort getServerPort() {
			return port;
		}
		
		public int getMonitorPort() {
			return newPort;
		}
		
		public void setMonitorPort(int p) {
			newPort = p;
		}
		
		public String[] getContentTypes() {
			if (content == null)
				return new String[0];
			return content;
		}
		
		public boolean isStarted() {
			return started;
		}
		
		protected void setStarted(boolean s) {
			started = s;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof MonitoredPort))
				return false;
			MonitoredPort mp = (MonitoredPort) obj;
			if (!mp.server.equals(server))
				return false;
			if (!mp.port.equals(port))
				return false;
			if (newPort != mp.newPort)
				return false;
			if (content == null && mp.content != null)
				return false;
			if (content != null && mp.content == null)
				return false;
			if (content != null) {
				int size = content.length;
				if (size != mp.content.length)
					return false;
				for (int i = 0; i < size; i++)
					if (!content[i].equals(mp.content[i]))
						return false;
			}
			return true;
		}
		
		protected boolean canSave() {
			return (port.getId() != null);
		}
		
		protected void save(IMemento memento) {
			memento.putString("serverId", server.getId());
			if (newPort != -1)
				memento.putString("port", newPort + "");
			memento.putString("portId", port.getId());
			memento.putBoolean("started", started);
			
			if (content != null) {
				StringBuffer sb = new StringBuffer();
				int size = content.length;
				for (int i = 0; i < size; i++) {
					if (i > 0)
						sb.append(",");
					sb.append(content[i]);
				}
				memento.putString("contentTypes", sb.toString());
			}
		}
		
		protected void load(IMemento memento, IProgressMonitor monitor2) {
			String serverId = memento.getString("serverId");
			server = null;
			if (serverId != null)
				server = ServerCore.findServer(serverId);
			if (server == null)
				throw new RuntimeException("Server could not be found: " + serverId + " " + server);
			String newPortStr = memento.getString("port");
			if (newPortStr != null && newPortStr.length() > 0)
				newPort = Integer.parseInt(newPortStr);
			String portId = memento.getString("portId");
			
			ServerPort[] ports2 = server.getServerPorts(monitor2);
			if (ports2 != null) {
				int size = ports2.length;
				for (int i = 0; port == null && i < size; i++) {
					ServerPort sp = ports2[i];
					if (sp.getId() != null && sp.getId().equals(portId))
						port = sp;
				}
			}
			if (port == null)
				throw new RuntimeException("Could not relocate port: " + serverId + " " + server + " " + portId);
			
			String s = memento.getString("contentTypes");
			if (s != null)
				content = ServerPlugin.tokenize(s, ",");
			
			Boolean b = memento.getBoolean("started");
			if (b != null && b.booleanValue()) {
				try {
					newPort = monitor.startMonitoring(server, port, newPort);
					started = true;
				} catch (CoreException e) {
					if (Trace.WARNING) {
						Trace.trace(Trace.STRING_WARNING, "Could not restart server monitor", e);
					}
				}
			}
		}
	}

	public ServerMonitorManager() {
		IServerMonitor[] monitors = ServerPlugin.getServerMonitors();
		if (monitors != null && monitors.length > 0)
			monitor = (ServerMonitor) monitors[0];
		
		instance = this;
		loadMonitors();
	}
	
	public static ServerMonitorManager getInstance() {
		if (instance == null)
			new ServerMonitorManager();
		return instance;
	}
	
	public static void shutdown() {
		if (instance == null)
			return;
		instance.saveMonitors();
	}

	/**
	 * Returns the monitor that is currently being used.
	 *  
	 * @return the current server monitor
	 */
	public IServerMonitor getCurrentServerMonitor() {
		return monitor;
	}

	/**
	 * Switch to use a different server monitor. All existing monitors will be
	 * removed from the current monitor and added to the new monitor.
	 * 
	 * @param newMonitor
	 * @throws org.eclipse.core.runtime.CoreException
	 */
	public void setServerMonitor(IServerMonitor newMonitor) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, "Not implemented yet", null));
	}

	/**
	 * Returns the list of ports that are currently being monitored.
	 *
	 * @param server a server
	 * @return a possibly empty array of monitored server ports
	 */
	public IMonitoredServerPort[] getMonitoredPorts(IServer server) {
		List<IMonitoredServerPort> list = new ArrayList<IMonitoredServerPort>();
		Iterator iterator = ports.iterator();
		while (iterator.hasNext()) {
			MonitoredPort mp = (MonitoredPort) iterator.next();
			if (mp.server.equals(server))
				list.add(mp);
		}
		
		return list.toArray(new IMonitoredServerPort[list.size()]);
	}

	/**
	 * Starts monitoring the given port, and returns the new port # to use that will
	 * route to the monitored port.
	 * 
	 * @param server a server
	 * @param port a port
	 * @param monitorPort the port used for monitoring
	 * @param content the content
	 * @return a monitored server port
	 */
	public IMonitoredServerPort createMonitor(IServer server, ServerPort port, int monitorPort, String[] content) {
		if (port == null || monitor == null)
			return null;
		
		MonitoredPort mp = new MonitoredPort(server, port, monitorPort, content);
		ports.add(mp);
		return mp;
	}

	/**
	 * Stop monitoring the given port. Throws a CoreException if there was a problem
	 * stopping the monitoring
	 *
	 * @param port
	 */
	public void removeMonitor(IMonitoredServerPort port) {
		if (port == null)
			return;

		try {
			ports.remove(port);
			if (port.isStarted())
				monitor.stopMonitoring(port.getServer(), port.getServerPort());
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not remove monitor", e);
			}
		}
	}

	/**
	 * Start the monitor. If the msp port is -1, it will be updated to the port that is actually in use.
	 * 
	 * @param msp
	 * @throws CoreException
	 */
	public void startMonitor(IMonitoredServerPort msp) throws CoreException {
		if (msp == null || msp.isStarted())
			return;
		
		MonitoredPort port = (MonitoredPort) msp;
		port.setMonitorPort(monitor.startMonitoring(msp.getServer(), msp.getServerPort(), msp.getMonitorPort()));
		port.setStarted(true);
	}

	/**
	 * Stop monitoring.
	 * 
	 * @param msp
	 */
	public void stopMonitor(IMonitoredServerPort msp) {
		if (msp == null || !msp.isStarted())
			return;
		MonitoredPort port = (MonitoredPort) msp;
		monitor.stopMonitoring(msp.getServer(), msp.getServerPort());
		port.setStarted(false);
	}

	/**
	 * Returns the mapped port to use when making requests to the given server
	 * and port number. Returns the existing port number if the port is not being
	 * monitored.
	 * 
	 * @param server a server
	 * @param port a port number
	 * @param content the content
	 * @return the port used for monitoring
	 */
	public int getMonitoredPort(IServer server, int port, String content) {
		try {
			Iterator iterator = ports.iterator();
			while (iterator.hasNext()) {
				MonitoredPort mp = (MonitoredPort) iterator.next();
				if (mp.isStarted() && mp.server.equals(server) && mp.port.getPort() == port) {
					String[] contentTypes = mp.getContentTypes();
					boolean found = false;
					if (content != null && contentTypes != null && contentTypes.length > 0) {
						int size = contentTypes.length;
						for (int i = 0; i < size; i++)
							if (content.equals(contentTypes[i]))
								found = true;
					} else
						found = true;
					if (found && mp.newPort != -1)
						return mp.newPort;
				}
			}
		} catch (Exception e) {
			// ignore
		}
		return port;
	}
	
	protected void saveMonitors() {
		String filename = ServerPlugin.getInstance().getStateLocation().append(MONITOR_DATA_FILE).toOSString();
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("monitors");

			Iterator iterator = ports.iterator();
			while (iterator.hasNext()) {
				MonitoredPort mp = (MonitoredPort) iterator.next();
				if (mp.canSave()) {
					IMemento child = memento.createChild("monitor");
					mp.save(child);
				}
			}
			
			memento.saveToFile(filename);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error saving monitor info", e);
			}
		}
	}
	
	protected void loadMonitors() {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Loading monitor info");
		}
		String filename = ServerPlugin.getInstance().getStateLocation().append(MONITOR_DATA_FILE).toOSString();
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("monitor");
			int size = children.length;
			
			for (int i = 0; i < size; i++) {
				try {
					MonitoredPort mp = new MonitoredPort(children[i], null);
					ports.add(mp);
				} catch (Exception e) {
					if (Trace.WARNING) {
						Trace.trace(Trace.STRING_WARNING, "Could not load monitor: " + e);
					}
				}
			}
		} catch (Exception e) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Could not load monitor info", e);
			}
		}
	}
}
