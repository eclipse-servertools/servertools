/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.internet.monitor.core.*;
/**
 * 
 */
public class MonitorManager {
	private static final int ADD = 0;
	private static final int CHANGE = 1;
	private static final int REMOVE = 2;

	// monitors
	protected List monitors;
	protected Map threads = new HashMap();
	
	protected List monitorListeners = new ArrayList();
	
	// requests
	protected List requests = new ArrayList();
	
	protected List requestListeners = new ArrayList();
	
	private Preferences.IPropertyChangeListener pcl;
	protected boolean ignorePreferenceChanges = false;
	
	protected static MonitorManager instance;
	
	public static MonitorManager getInstance() {
		if (instance == null)
			instance = new MonitorManager();
		return instance;
	}
	
	private MonitorManager() {
		loadMonitors();
		
		pcl = new Preferences.IPropertyChangeListener() {
			public void propertyChange(Preferences.PropertyChangeEvent event) {
				if (ignorePreferenceChanges)
					return;
				String property = event.getProperty();
				if (property.equals("monitors")) {
					loadMonitors();
				}
			}
		};
		
		MonitorPlugin.getInstance().getPluginPreferences().addPropertyChangeListener(pcl);
	}
	
	protected void dispose() {
		MonitorPlugin.getInstance().getPluginPreferences().removePropertyChangeListener(pcl);
	}
	
	public IMonitorWorkingCopy createMonitor() {
		return new MonitorWorkingCopy();
	}
	
	public List getMonitors() {
		return new ArrayList(monitors);
	}

	protected void addMonitor(IMonitor monitor) {
		if (!monitors.contains(monitor))
			monitors.add(monitor);
		fireMonitorEvent(monitor, ADD);
		saveMonitors();
	}
	
	protected boolean isRunning(IMonitor monitor) {
		return (threads.get(monitor) != null);
	}

	public void startMonitor(IMonitor monitor) throws Exception {
		if (!monitors.contains(monitor))
			return;
		
		if (AcceptThread.isPortInUse(monitor.getLocalPort()))
			throw new Exception(MonitorPlugin.getString("%errorPortInUse"));
		
		AcceptThread thread = new AcceptThread(monitor);
		thread.startServer();
		threads.put(monitor, thread);
	}
	
	public void stopMonitor(IMonitor monitor) {
		if (!monitors.contains(monitor))
			return;
		
		AcceptThread thread = (AcceptThread) threads.get(monitor);
		if (thread != null) {
			thread.stopServer();
			threads.remove(monitor);
		}
	}
	
	protected void removeMonitor(IMonitor monitor) {
		if (monitor.isRunning())
			stopMonitor(monitor);
		monitors.remove(monitor);
		fireMonitorEvent(monitor, REMOVE);
		saveMonitors();
	}
	
	protected void monitorChanged(IMonitor monitor) {
		fireMonitorEvent(monitor, CHANGE);
		saveMonitors();
	}
	
	/**
	 * Add monitor listener.
	 * 
	 * @param listener
	 */
	public void addMonitorListener(IMonitorListener listener) {
		monitorListeners.add(listener);
	}

	/**
	 * Remove monitor listener.
	 * 
	 * @param listener
	 */
	public void removeMonitorListener(IMonitorListener listener) {
		monitorListeners.remove(listener);
	}
	
	/**
	 * Fire a monitor event.
	 * @param rr
	 * @param type
	 */
	protected void fireMonitorEvent(IMonitor monitor, int type) {
		Object[] obj = monitorListeners.toArray();
		
		int size = obj.length;
		for (int i = 0; i < size; i++) {
			IMonitorListener listener = (IMonitorListener) obj[i];
			if (type == ADD)
				listener.monitorAdded(monitor);
			else if (type == CHANGE)
				listener.monitorChanged(monitor);
			else if (type == REMOVE)
				listener.monitorRemoved(monitor);
		}
	}
	
	/**
	 * Returns a list of the current requests.
	 *
	 * @return java.util.List
	 */
	public List getRequests() {
		return requests;
	}
	
	/**
	 * Add a new request response pair.
	 *
	 * @param pair org.eclipse.tcpip.monitor.RequestResponse
	 */
	public void addRequest(IRequest rr) {
		if (requests.contains(rr))
			return;

		requests.add(rr);
		fireRequestEvent(rr, ADD);
	}
	
	public void requestChanged(IRequest rr) {
		fireRequestEvent(rr, CHANGE);
	}
	
	public void removeRequest(IRequest rr) {
		if (!requests.contains(rr))
			return;

		requests.remove(rr);
		fireRequestEvent(rr, REMOVE);
	}
	
	public void removeAllRequests() {
		int size = requests.size();
		IRequest[] rrs = new IRequest[size];
		requests.toArray(rrs);
		
		for (int i = 0; i < size; i++) {
			removeRequest(rrs[i]);
		}
	}
	
	/**
	 * Add request listener.
	 * 
	 * @param listener
	 */
	public void addRequestListener(IRequestListener listener) {
		requestListeners.add(listener);
	}

	/**
	 * Remove request listener.
	 * 
	 * @param listener
	 */
	public void removeRequestListener(IRequestListener listener) {
		requestListeners.remove(listener);
	}

	/**
	 * Fire a request event.
	 * @param rr
	 * @param type
	 */
	protected void fireRequestEvent(IRequest rr, int type) {
		int size = requestListeners.size();
		IRequestListener[] xrl = MonitorPlugin.getInstance().getRequestListeners();
		int size2 = xrl.length;
		
		IRequestListener[] rl = new IRequestListener[size + size2];
		System.arraycopy(xrl, 0, rl, 0, size2);
		for (int i = 0; i < size; i++)
			rl[size2 + i] = (IRequestListener) requestListeners.get(i);

		for (int i = 0; i < size + size2; i++) {
			IRequestListener listener = rl[i];
			if (type == ADD)
				listener.requestAdded(rr);
			else if (type == CHANGE)
				listener.requestChanged(rr);
			else if (type == REMOVE)
				listener.requestRemoved(rr);
		}
	}
	
	protected void loadMonitors() {
		Trace.trace(Trace.FINEST, "Loading monitors");
		
		monitors = new ArrayList();
		Preferences prefs = MonitorPlugin.getInstance().getPluginPreferences();
		String xmlString = prefs.getString("monitors");
		if (xmlString != null && xmlString.length() > 0) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes());
				IMemento memento = XMLMemento.loadMemento(in);
		
				IMemento[] children = memento.getChildren("monitor");
				if (children != null) {
					int size = children.length;
					for (int i = 0; i < size; i++) {
						Monitor monitor = new Monitor();
						monitor.load(children[i]);
						monitors.add(monitor);
					}
				}
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not load monitors: " + e.getMessage());
			}
		}
	}
	
	protected void saveMonitors() {
		try {
			ignorePreferenceChanges = true;
			XMLMemento memento = XMLMemento.createWriteRoot("monitors");

			Iterator iterator = monitors.iterator();
			while (iterator.hasNext()) {
				Monitor monitor = (Monitor) iterator.next();
				IMemento child = memento.createChild("monitor");
				monitor.save(child);
			}
			
			String xmlString = memento.saveToString();
			Preferences prefs = MonitorPlugin.getInstance().getPluginPreferences();
			prefs.setValue("monitors", xmlString);
			MonitorPlugin.getInstance().savePluginPreferences();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save browsers", e);
		}
		ignorePreferenceChanges = false;
	}
}