package org.eclipse.wst.server.ui.internal.actions;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.server.core.IOrdered;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.actions.IServerAction;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 *
 */
public class ServerAction implements IOrdered {
	public static class RealServerAction extends Action {
		protected Shell shell;
		protected ServerAction action;
		protected IServer server;
		protected IServerConfiguration configuration;
		
		public RealServerAction(Shell shell, ServerAction action, IServer server, IServerConfiguration configuration) {
			super(action.getLabel());
			this.shell = shell;
			this.action = action;
			this.server = server;
			this.configuration = configuration;
			setImageDescriptor(action.getImageDescriptor());
			setEnabled(action.getDelegate().supports(server, configuration));
		}
		
		public void run() {
			action.getDelegate().run(shell, server, configuration);
		}
	}
	
	private static List serverActions;

	private IConfigurationElement element;
	private IServerAction delegate;
	private ImageDescriptor imageDescriptor;

	/**
	 * ServerAction constructor comment.
	 */
	public ServerAction(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 */
	public IConfigurationElement getConfigurationElement() {
		return element;
	}

	/**
	 * Returns the id.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the label.
	 *
	 * @return java.lang.String
	 */
	public String getLabel() {
		return element.getAttribute("label");
	}
	
	/**
	 * Returns the order.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			String o = element.getAttribute("order");
			return Integer.parseInt(o);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	/**
	 * Returns the icon.
	 *
	 * @return java.lang.String
	 */
	public String getIcon() {
		return element.getAttribute("icon");
	}
	
	public String getCategory() {
		return element.getAttribute("category");
	}
	
	protected ImageDescriptor getImageDescriptor() {
		if (imageDescriptor != null)
			return imageDescriptor;
		
		try {
			String pluginId = element.getDeclaringExtension().getNamespace();
			imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, getIcon());
		} catch (Exception e) {
			// ignore
		}
		return imageDescriptor;
	}

	/**
	 * Return the ids of the server resource factories (specified
	 * using Java-import style) that this page may support.
	 * 
	 * @return java.lang.String[]
	 */
	public String[] getTypeIds() {
		try {
			List list = new ArrayList();
			StringTokenizer st = new StringTokenizer(element.getAttribute("typeIds"), ",");
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				if (str != null && str.length() > 0)
					list.add(str.trim());
			}
			String[] s = new String[list.size()];
			list.toArray(s);
			return s;
		} catch (Exception e) {
			//Trace.trace("Could not get server resource from: " + element.getAttribute("serverResources"));
			return null;
		}
	}
	
	/**
	 * Returns true if the given server resource type (given by the
	 * id) can use this action. This result is based on
	 * the result of the getTypeIds() method.
	 *
	 * @return boolean
	 */
	public boolean supportsServerResource(String id) {
		if (id == null || id.length() == 0)
			return false;

		String[] s = getTypeIds();
		if (s == null)
			return false;
		
		int size = s.length;
		for (int i = 0; i < size; i++) {
			if (s[i].endsWith("*")) {
				if (id.length() >= s[i].length() && id.startsWith(s[i].substring(0, s[i].length() - 1)))
					return true;
			} else if (id.equals(s[i]))
				return true;
		}
		return false;
	}

	public IServerAction getDelegate() {
		if (delegate == null) {
			try {
				delegate = (IServerAction) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace("Could not create action delegate: " + getId(), e);
			}
		}
		return delegate;
	}
	
	/**
	 * Returns a List of all server actions.
	 *
	 * @return java.util.List
	 */
	public static List getServerActions() {
		if (serverActions == null)
			loadServerActions();
		return serverActions;
	}

	/**
	 * Load the server action extension point.
	 */
	private static void loadServerActions() {
		Trace.trace(Trace.CONFIG, "->- Loading .serverActions extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUICore.PLUGIN_ID, "serverActions");

		int size = cf.length;
		serverActions = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				serverActions.add(new ServerAction(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded serverAction: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverAction: " + cf[i].getAttribute("id"), t);
			}
		}

		// sort actions
		ServerUtil.sortOrderedList(serverActions);
		Trace.trace(Trace.CONFIG, "-<- Done loading .serverActions extension point -<-");
	}

	public static void addServerMenuItems(Shell shell, IMenuManager menu, IServer server) {
		addServerMenuItems(shell, menu, server, server.getServerConfiguration());
	}
	
	/**
	 * 
	 */
	public static void addServerMenuItems(Shell shell, IMenuManager menu, IServer server, IServerConfiguration configuration) {
		boolean addedSeparator = false;
		String category = null;
		
		Iterator iterator = getServerActions().iterator();
		while (iterator.hasNext()) {
			ServerAction serverAction = (ServerAction) iterator.next();
			if (category == null)
				category = serverAction.getCategory();
			else if (!category.equals(serverAction.getCategory())) {
				category = serverAction.getCategory();
				menu.add(new Separator());
			}
			long time = System.currentTimeMillis();
			if ((server != null && server.getServerType() != null && serverAction.supportsServerResource(server.getServerType().getId())) ||
					(configuration != null && serverAction.supportsServerResource(configuration.getServerConfigurationType().getId()))) {
				if (!addedSeparator) {
					addedSeparator = true;
					menu.add(new Separator());
				}
				try {
					Action action = new RealServerAction(shell, serverAction, server, configuration);
					Trace.trace(Trace.PERFORMANCE, "ServerAction.supports(): " + (System.currentTimeMillis() - time) + " " + serverAction.getId() + "/" + serverAction.getLabel());
					menu.add(action);
				} catch (Exception e) {
					// could not add menu item
				}
			}
		}
	}
}