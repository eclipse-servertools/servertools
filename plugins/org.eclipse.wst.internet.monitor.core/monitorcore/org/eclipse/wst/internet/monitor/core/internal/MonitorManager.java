/**********************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *     Tianchao Li (Tianchao.Li@gmail.com) - Start monitors by default 
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.ByteArrayInputStream;
import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.internet.monitor.core.internal.http.ResendHTTPRequest;
import org.eclipse.wst.internet.monitor.core.internal.provisional.*;
/**
 * 
 */
public class MonitorManager {
	private static final int ADD = 0;
	private static final int CHANGE = 1;
	private static final int REMOVE = 2;

	// monitors
	protected List<IMonitor> monitors;
	protected Map<IMonitor, AcceptThread> threads = new HashMap<IMonitor, AcceptThread>();
	
	protected List<IMonitorListener> monitorListeners = new ArrayList<IMonitorListener>();

	private Preferences.IPropertyChangeListener pcl;
	protected boolean ignorePreferenceChanges = false;
	
	protected Map<Request, List<ResendHTTPRequest>> resendMap = new HashMap<Request, List<ResendHTTPRequest>>();
	
	protected static MonitorManager instance;
	
	static {
		MonitorPlugin.getInstance().executeStartups();
	}
	
	/**
	 * Return a static instance.
	 * 
	 * @return the instance
	 */
	public static MonitorManager getInstance() {
		if (instance == null) {
			instance = new MonitorManager();
			instance.startMonitors();
		}
		
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
	
	/**
	 * Create a new monitor.
	 * 
	 * @return the new monitor
	 */
	public IMonitorWorkingCopy createMonitor() {
		return new MonitorWorkingCopy();
	}
	
	/**
	 * Return the list of monitors.
	 * 
	 * @return the list of monitors
	 */
	public List<IMonitor> getMonitors() {
		return new ArrayList<IMonitor>(monitors);
	}

	protected synchronized void addMonitor(IMonitor monitor) {
		if (!monitors.contains(monitor))
			monitors.add(monitor);
		fireMonitorEvent(monitor, ADD);
		saveMonitors();
	}
	
	protected boolean isRunning(IMonitor monitor) {
		return (threads.get(monitor) != null);
	}

	/**
	 * Start a monitor.
	 * 
	 * @param monitor the monitor
	 * @throws CoreException
	 */
	public void startMonitor(IMonitor monitor) throws CoreException {
		if (!monitors.contains(monitor))
			return;
		
		if (AcceptThread.isPortInUse(monitor.getLocalPort()))
			throw new CoreException(new Status(IStatus.ERROR, MonitorPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPortInUse, monitor.getLocalPort() + ""), null));
		
		AcceptThread thread = new AcceptThread(monitor);
		thread.startServer();
		threads.put(monitor, thread);
	}
	
	/**
	 * Stop a monitor.
	 * 
	 * @param monitor the monitor
	 */
	public void stopMonitor(IMonitor monitor) {
		if (!monitors.contains(monitor))
			return;
		
		AcceptThread thread = threads.get(monitor);
		if (thread != null) {
			thread.stopServer();
			threads.remove(monitor);
		}
	}
	
	protected synchronized void removeMonitor(IMonitor monitor) {
		if (monitor.isRunning())
			stopMonitor(monitor);
		monitors.remove(monitor);
		fireMonitorEvent(monitor, REMOVE);
		saveMonitors();
	}
	
	protected synchronized void monitorChanged(IMonitor monitor) {
		fireMonitorEvent(monitor, CHANGE);
		saveMonitors();
	}
	
	protected boolean exists(IMonitor monitor) {
		return (monitors.contains(monitor));
	}
	
	/**
	 * Add monitor listener.
	 * 
	 * @param listener
	 */
	public synchronized void addMonitorListener(IMonitorListener listener) {
		if (!monitorListeners.contains(listener))
			monitorListeners.add(listener);
	}

	/**
	 * Remove monitor listener.
	 * 
	 * @param listener
	 */
	public synchronized void removeMonitorListener(IMonitorListener listener) {
		if (monitorListeners.contains(listener))
			monitorListeners.remove(listener);
	}
	
	/**
	 * Fire a monitor event.
	 * 
	 * @param monitor the monitor
	 * @param type the type of event
	 */
	protected void fireMonitorEvent(IMonitor monitor, int type) {
		IMonitorListener[] obj = monitorListeners.toArray(new IMonitorListener[monitorListeners.size()]);
		
		for (IMonitorListener listener : obj) {
			if (type == ADD)
				listener.monitorAdded(monitor);
			else if (type == CHANGE)
				listener.monitorChanged(monitor);
			else if (type == REMOVE)
				listener.monitorRemoved(monitor);
		}
	}

	protected synchronized void loadMonitors() {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Loading monitors");
		}
		
		monitors = new ArrayList<IMonitor>();
		Preferences prefs = MonitorPlugin.getInstance().getPluginPreferences();
		String xmlString = prefs.getString("monitors");
		if (xmlString != null && xmlString.length() > 0) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
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
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not load monitors", e);
				}
			}
		}
	}
	
	protected synchronized void saveMonitors() {
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
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not save browsers", e);
			}
		}
		ignorePreferenceChanges = false;
	}
	
	/**
	 * Creates a new resend request from the given request.
	 * 
	 * @param request the request that is to be resent; may not be <code>null</code>
	 * @return a new resend request
	 */
	public static ResendHTTPRequest createResendRequest(Request request) {
		if (request == null)
			throw new IllegalArgumentException();
		return new ResendHTTPRequest((Monitor)request.getMonitor(), request);
	}

	/**
	 * Adds a resend request to this request.
	 * 
	 * @param request the resend request to add
	 * @param resendReq the resend request
	 */
	public void addResendRequest(Request request, ResendHTTPRequest resendReq) {
		if (request == null || resendReq == null)
			return;
		
		List<ResendHTTPRequest> list = null;
		try {
			list = resendMap.get(request);
		} catch (Exception e) {
			// ignore
		}
		
		if (list == null) {
			list = new ArrayList<ResendHTTPRequest>();
			resendMap.put(request, list);
		}
		list.add(resendReq);
	}

	/**
	 * Returns an array of resend requests based on this request. 
	 * 
	 * @param request a request
	 * @return the array of resend requests based on this request
	 */
	public ResendHTTPRequest[] getResendRequests(Request request) {
		List<ResendHTTPRequest> list = resendMap.get(request);
		if (list != null)
			return list.toArray(new ResendHTTPRequest[list.size()]);
		
		return new ResendHTTPRequest[0];
	}

	/**
	 * Start all monitors that are set to auto-start.
	 */
	public synchronized void startMonitors() {
		MonitorManager manager = MonitorManager.getInstance();
		List monitorList = manager.getMonitors();
		Iterator monitorIterator = monitorList.iterator();
		while (monitorIterator.hasNext()) {
			IMonitor monitor = (IMonitor) monitorIterator.next();
			if (monitor.isAutoStart())
				try {
					monitor.start();
				} catch (CoreException e) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Failed to start monitor:" + monitor.toString(), e);
					}
				}
		}
	}
}