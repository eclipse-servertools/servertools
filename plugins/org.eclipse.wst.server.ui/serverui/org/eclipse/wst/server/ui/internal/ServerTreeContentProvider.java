package org.eclipse.wst.server.ui.internal;
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
import java.util.Iterator;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IModuleEvent;
import org.eclipse.wst.server.core.model.IModuleEventsListener;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
import org.eclipse.wst.server.core.model.IServerListener;
import org.eclipse.wst.server.core.model.IServerResourceListener;
import org.eclipse.wst.server.core.util.ServerAdapter;
import org.eclipse.wst.server.core.util.ServerResourceAdapter;
import org.eclipse.wst.server.ui.internal.view.tree.ConfigurationProxyResourceAdapter;
import org.eclipse.wst.server.ui.internal.view.tree.ModuleResourceAdapter;
import org.eclipse.wst.server.ui.internal.view.tree.ServerElementAdapter;
import org.eclipse.wst.server.ui.internal.view.tree.TextResourceAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Provides tree contents for objects that have the IWorkbenchAdapter
 * adapter registered. 
 */
public class ServerTreeContentProvider implements ITreeContentProvider {
	protected TreeViewer viewer;
	
	// listeners
	protected IServerResourceListener listener;
	protected IServerListener serverListener;
	protected IResourceChangeListener resourceChangeListener;
	protected IModuleEventsListener moduleEventsListener;

	/**
	 * 
	 */
	public ServerTreeContentProvider() {
		// add listeners
		addServerResourceListener();
		addServerConfigurationListener();
		addModuleEventsListener();
	}

	public Object[] getServerTreeRoots(Object parent) {
		/*looseConfig = getLooseConfigurations();
		if (looseConfig.length == 0)
			return new Object[] { new TextResourceAdapter(parent, TextResourceAdapter.STYLE_SERVERS) };
		else*/
			return new Object[] { new TextResourceAdapter(parent, TextResourceAdapter.STYLE_SERVERS) };
				//new TextResourceAdapter(parent, TextResourceAdapter.STYLE_LOOSE_CONFIGURATIONS) };
	}

	/* (non-Javadoc)
	 * Method declared on IContentProvider.
	 */
	public void dispose() {
		// remove listeners
		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			server.removeServerListener(serverListener);
		}

		ServerCore.getResourceManager().removeResourceListener(listener);
		
		if (moduleEventsListener != null)
			ServerCore.getResourceManager().removeModuleEventsListener(moduleEventsListener);
	}

	/**
	 * Returns the implementation of IWorkbenchAdapter for the given
	 * object.  Returns null if the adapter is not defined or the
	 * object is not adaptable.
	 */
	protected IWorkbenchAdapter getAdapter(Object o) {
		if (!(o instanceof IAdaptable))
			return null;

		return (IWorkbenchAdapter)((IAdaptable)o).getAdapter(IWorkbenchAdapter.class);
	}

	/* (non-Javadoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object[] getChildren(Object element) {
		if (element instanceof TextResourceAdapter) {
			TextResourceAdapter adapter = (TextResourceAdapter) element;
			Object[] children = adapter.getChildren(null);
			if (children != null) {
				int size = children.length;
				for (int i = 0; i < size; i++) {
					if (children[i] instanceof ServerElementAdapter) {
						ServerElementAdapter adapter2 = (ServerElementAdapter) children[i];
						adapter2.setFlags((byte) 1);
					}
				}
			}
			return children;
		} else if (element instanceof ModuleResourceAdapter) {
			ModuleResourceAdapter adapter = (ModuleResourceAdapter) element;
			return adapter.getChildren(null);
		} else if (element instanceof ServerElementAdapter) {
			ServerElementAdapter adapter = (ServerElementAdapter) element;
			return adapter.getChildren();
		}
		
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null)
		    return adapter.getChildren(element);

		return new Object[0];
	}
	
	/* (non-Javadoc)
	 * Method declared on IStructuredContentProvider.
	 */
	public Object[] getElements(Object element) {
		return getChildren(element);
	}
	
	/* (non-Javadoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object getParent(Object element) {
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter != null)
		    return adapter.getParent(element);

		return null;
	}
	
	/* (non-Javadoc)
	 * Method declared on ITreeContentProvider.
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) {
		if (newViewer instanceof TreeViewer)
			this.viewer = (TreeViewer) newViewer;
	}
	
	/**
	 * Add listeners for resource changes.
	 */
	private void addServerConfigurationListener() {
		// add a listener for configuration child module changes
		serverListener = new ServerAdapter() {
			public void modulesChanged(final IServer server) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerModulesChanged(server);
					}
				});
			}
		};

		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			server.addServerListener(serverListener);
		}
	}
	
	/**
	 * Add listeners for server resource changes.
	 */
	private void addServerResourceListener() {
		// add a listener for resources being added or removed
		listener = new ServerResourceAdapter() {
			public void serverAdded(final IServer server) {
				server.addServerListener(serverListener);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerResourceAdded(server);
					}
				});
			}
			public void serverRemoved(final IServer server) {
				server.removeServerListener(serverListener);
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerResourceRemoved(server);
					}
				});
			}
			public void serverChanged(final IServer server) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerResourceChanged(server);
					}
				});
			}
			public void serverConfigurationAdded(final IServerConfiguration configuration) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerResourceAdded(configuration);
					}
				});
			}
			public void serverConfigurationRemoved(final IServerConfiguration configuration) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerResourceRemoved(configuration);
					}
				});
			}
			public void serverConfigurationChanged(final IServerConfiguration configuration) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerResourceChanged(configuration);
					}
				});
			}
		};
		ServerCore.getResourceManager().addResourceListener(listener);
	}
	
	/**
	 * Add listener to refresh when modules are added.
	 */
	private void addModuleEventsListener() {
		moduleEventsListener = new IModuleEventsListener() {
			public void moduleEvents(IModuleFactoryEvent[] factoryEvent, IModuleEvent[] event) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						if (viewer != null)
							viewer.refresh(true);
					}
				});
			}
		};
		
		ServerCore.getResourceManager().addModuleEventsListener(moduleEventsListener);
	}

	/**
	 * Updates an element in the tree.
	 *
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerModulesChanged(IServer server2) {
		if (viewer != null) {
			viewer.refresh(new ServerElementAdapter(null, server2));
			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next(); 
				if (server2.equals(server)) {
					viewer.refresh(new ServerElementAdapter(null, server));
				}
			}
		}
	}
	
	/**
	 * Handles the add of a new server resource.
	 *
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerResourceAdded(IElement element) {
		//Trace.trace("add: " + element);
		if (viewer == null)
			return;

		ServerElementAdapter adapter = new ServerElementAdapter(null, element);
		adapter.setFlags((byte) 1);
		if (element instanceof IServer) {
			viewer.add(new TextResourceAdapter(null, TextResourceAdapter.STYLE_SERVERS), adapter);
		} else {
			IServerConfiguration configuration = (IServerConfiguration) element;
			//boolean used = false;

			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				IServerConfiguration cfg = server.getServerConfiguration();
				if (cfg != null && cfg.equals(configuration)) {
					ServerElementAdapter adapter2 = new ServerElementAdapter(null, server);
					adapter2.setFlags((byte) 1);
					viewer.refresh(adapter2);
					
					//used = true;
				}
			}
			
			/*if (!used) {
				ServerResourceAdapter adapter2 = new ServerResourceAdapter(null, configuration);
				adapter2.setFlags((byte) 1);
				viewer.add(new TextResourceAdapter(null, TextResourceAdapter.STYLE_LOOSE_CONFIGURATIONS), adapter2);
			}*/
		}
	}

	/**
	 * Updates an element in the tree.
	 *
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerResourceChanged(IElement element) {
		//Trace.trace("change: " + element);
		if (viewer == null)
			return;

		if (element instanceof IServer) {
			IServer server = (IServer) element;
			ServerElementAdapter adapter = new ServerElementAdapter(null, element);
			adapter.setFlags((byte) 1);
			viewer.refresh(adapter);
			
			IServerConfiguration cfg = server.getServerConfiguration();
			if (cfg != null) {
				ServerElementAdapter adapter3 = new ServerElementAdapter(null, cfg);
				adapter3.setFlags((byte) 1);
				viewer.remove(adapter3);
			}
		} else {
			IServerConfiguration configuration = (IServerConfiguration) element;
			
			// refresh servers
			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				IServerConfiguration cfg = server.getServerConfiguration();
				if (cfg != null && cfg.equals(configuration)) {
					ServerElementAdapter adapter2 = new ServerElementAdapter(null, server);
					adapter2.setFlags((byte) 1);
					viewer.refresh(adapter2);
				}
			}
			//viewer.refresh(new TextResourceAdapter(null, TextResourceAdapter.STYLE_LOOSE_CONFIGURATIONS));
		}
	}

	/**
	 * Handles the removal of a server resource.
	 *
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 */
	protected void handleServerResourceRemoved(IElement element) {
		//Trace.trace("remove: " + element);
		if (viewer == null)
			return;
		
		TextResourceAdapter.deleted = element;
		if (element instanceof IServerConfiguration)
			ConfigurationProxyResourceAdapter.deleted = (IServerConfiguration) element;

		if (element instanceof IServer) {
			//IServer server = (IServer) element;
			TextResourceAdapter adapter = new TextResourceAdapter(null, TextResourceAdapter.STYLE_SERVERS);
			viewer.refresh(adapter);
			/*IServerConfiguration configuration = Reference.getServerConfigurationByRef(server.getConfigurationRef());
			
			boolean used = false;
			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server2 = (IServer) iterator.next();
				if (!server.equals(server2)) {
					IServerConfiguration cfg = Reference.getServerConfigurationByRef(server2.getConfigurationRef());
					if (cfg != null && cfg.equals(configuration)) {
						used = true;
					}
				}
			}
			if (!used) {
				ServerResourceAdapter adapter2 = new ServerResourceAdapter(null, configuration);
				adapter2.setFlags((byte) 1);
				viewer.add(new TextResourceAdapter(null, TextResourceAdapter.STYLE_LOOSE_CONFIGURATIONS), adapter2);
			}*/
		} else {
			//TextResourceAdapter adapter = new TextResourceAdapter(null, TextResourceAdapter.STYLE_CONFIGURATIONS);
			//viewer.refresh(adapter);
	
			/*Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				ServerResourceAdapter adapter2 = new ServerResourceAdapter(null, server);
				adapter2.setFlags((byte) 1);
				viewer.refresh(adapter2);
			}*/
			IServerConfiguration configuration = (IServerConfiguration) element;
			
			// refresh servers
			//boolean used = false;
			Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				IServerConfiguration cfg = server.getServerConfiguration();
				if (cfg != null && cfg.equals(configuration)) {
					ServerElementAdapter adapter2 = new ServerElementAdapter(null, server);
					adapter2.setFlags((byte) 1);
					viewer.refresh(adapter2);
					
					//used = true;
				}
			}
			
			/*if (!used) {
				TextResourceAdapter adapter = new TextResourceAdapter(null, TextResourceAdapter.STYLE_LOOSE_CONFIGURATIONS);
				viewer.refresh(adapter);
			}*/
		}
		ConfigurationProxyResourceAdapter.deleted = null;
		TextResourceAdapter.deleted = null;
	}
	
	public static IServerConfiguration[] getLooseConfigurations() {
		java.util.List configs = ServerCore.getResourceManager().getServerConfigurations();
		
		Iterator iterator = ServerCore.getResourceManager().getServers().iterator();
		while (iterator.hasNext()) {
			IServer server = (IServer) iterator.next();
			if (!server.equals(TextResourceAdapter.deleted)) {
				IServerConfiguration cfg = server.getServerConfiguration();
				if (cfg != null && configs.contains(cfg))
					configs.remove(cfg);
			}
		}
		
		if (configs.contains(ConfigurationProxyResourceAdapter.deleted))
			configs.remove(ConfigurationProxyResourceAdapter.deleted);
		
		IServerConfiguration[] config = new IServerConfiguration[configs.size()];
		configs.toArray(config);
		return config;
	}
}
