/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.core.*;
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
	protected LifecycleListener listener;
	//protected IServerListener serverListener;
	protected IResourceChangeListener resourceChangeListener;

	class LifecycleListener implements IServerLifecycleListener {
		public void serverAdded(final IServer server) {
			//server.addServerListener(serverListener);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					handleServerResourceAdded(server);
				}
			});
		}
		public void serverRemoved(final IServer server) {
			//server.removeServerListener(serverListener);
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
	}

	/**
	 * 
	 */
	public ServerTreeContentProvider() {
		// add listeners
		addServerResourceListener();
		addServerListener();
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
		/*IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++)
				servers[i].removeServerListener(serverListener);
		}*/

		ServerCore.removeServerLifecycleListener(listener);
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
	private void addServerListener() {
		// add a listener for configuration child module changes
		/*serverListener = new IServerListener() {
			public void modulesChanged(final IServer server) {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						handleServerModulesChanged(server);
					}
				});
			}
		};

		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++)
				servers[i].addServerListener(serverListener);
		}*/
	}
	
	/**
	 * Add listeners for server resource changes.
	 */
	private void addServerResourceListener() {
		// add a listener for resources being added or removed
		listener = new LifecycleListener();
		ServerCore.addServerLifecycleListener(listener);
	}

	/**
	 * Updates a server in the tree.
	 *
	 * @param server2 a server
	 */
	protected void handleServerModulesChanged(IServer server2) {
		if (viewer != null) {
			viewer.refresh(new ServerElementAdapter(null, server2));
			IServer[] servers = ServerCore.getServers();
			if (servers != null) {
				int size = servers.length;
				for (int i = 0; i < size; i++) {
					if (server2.equals(servers[i])) {
						viewer.refresh(new ServerElementAdapter(null, servers[i]));
					}
				}
			}
		}
	}
	
	/**
	 * Handles the add of a new server resource.
	 *
	 * @param element
	 */
	protected void handleServerResourceAdded(Object element) {
		//Trace.trace("add: " + element);
		if (viewer == null)
			return;

		ServerElementAdapter adapter = new ServerElementAdapter(null, element);
		adapter.setFlags((byte) 1);
		if (element instanceof IServer) {
			viewer.add(new TextResourceAdapter(null, TextResourceAdapter.STYLE_SERVERS), adapter);
		}
	}

	/**
	 * Updates an element in the tree.
	 *
	 * @param element
	 */
	protected void handleServerResourceChanged(Object element) {
		//Trace.trace("change: " + element);
		if (viewer == null)
			return;

		if (element instanceof IServer) {
			ServerElementAdapter adapter = new ServerElementAdapter(null, element);
			adapter.setFlags((byte) 1);
			viewer.refresh(adapter);
		}
	}

	/**
	 * Handles the removal of a server resource.
	 *
	 * @param element
	 */
	protected void handleServerResourceRemoved(Object element) {
		//Trace.trace("remove: " + element);
		if (viewer == null)
			return;
		
		TextResourceAdapter.deleted = element;
		
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
				ServerLifecycleAdapter adapter2 = new ServerLifecycleAdapter(null, configuration);
				adapter2.setFlags((byte) 1);
				viewer.add(new TextResourceAdapter(null, TextResourceAdapter.STYLE_LOOSE_CONFIGURATIONS), adapter2);
			}*/
		}
	}
}