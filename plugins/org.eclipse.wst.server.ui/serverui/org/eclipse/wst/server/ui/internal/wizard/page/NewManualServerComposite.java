package org.eclipse.wst.server.ui.internal.wizard.page;
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
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.ProgressUtil;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.viewers.ServerTypeComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * Wizard page used to create a server and configuration at the same time.
 */
public class NewManualServerComposite extends Composite {
	public interface ServerSelectionListener {
		public void serverSelected(IServer server);
	}

	public interface IWizardHandle2 {
		public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException;
		public void update();
		public void setMessage(String newMessage, int newType);
	}
	protected IWizardHandle2 wizard;

	//private IContainer defaultContainer;
	//private String defaultServerTypeId;
	private ServerTypeComposite serverTypeComposite;

	//protected IRuntimeWorkingCopy runtime;
	protected IRuntime runtime;
	protected IServerWorkingCopy server;
	protected ServerSelectionListener listener;
	
	protected String host;

	protected ElementCreationCache cache = new ElementCreationCache();

	/**
	 * Creates a new server and server configuration.  If the initial
	 * resource selection contains exactly one container resource then it will be
	 * used as the default container resource.
	 *
	 * @param org.eclipse.jface.wizard.IWizard parent
	 */
	public NewManualServerComposite(Composite parent, IWizardHandle2 wizard, ServerSelectionListener listener) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.listener = listener;

		createControl();
		wizard.setMessage("", IMessageProvider.ERROR);
	}

	/**
	 * Returns this page's initial visual components.
	 *
	 * @param parent a <code>Composite</code> that is to be used as the parent of this
	 *     page's collection of visual components
	 */
	protected void createControl() {
		// top level group
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		this.setFont(getParent().getFont());
		WorkbenchHelp.setHelp(this, ContextIds.NEW_SERVER_WIZARD);
		
		serverTypeComposite = new ServerTypeComposite(this, SWT.NONE, new ServerTypeComposite.ServerTypeSelectionListener() {
			public void serverTypeSelected(IServerType type) {
				handleTypeSelection(type);
				//WizardUtil.defaultSelect(parent, CreateServerWizardPage.this);
			}
		});
		serverTypeComposite.setIncludeTestEnvironments(false);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 2;
		serverTypeComposite.setLayoutData(data);
		WorkbenchHelp.setHelp(serverTypeComposite, ContextIds.NEW_SERVER_INSTANCE_FACTORY);
		
		Dialog.applyDialogFont(this);
	}

	public void setHost(String host) {
		this.host = host;
		if (serverTypeComposite == null)
			return;
		if (host == null) {
			serverTypeComposite.setHost(true);
		} else if (SocketUtil.isLocalhost(host))
			serverTypeComposite.setHost(true);
		else
			serverTypeComposite.setHost(false);
		if (server != null) {
			server.setHostname(host);
			ServerUtil.setServerDefaultName(server);
		}
	}
	
	/**
	 * Return the current editable element.
	 * @return org.eclipse.wst.server.core.model.IServer
	 */
	protected void loadServerImpl(final IServerType type) {
		runtime = null;
		server = null;
		
		if (type == null)
			return;
	
		server = cache.getCachedServer(type);
		if (server != null) {
			server.setHostname(host);
			ServerUtil.setServerDefaultName(server);
			runtime = server.getRuntime();
			return;
		}
	
		final CoreException[] ce = new CoreException[1];

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					monitor = ProgressUtil.getMonitorFor(monitor);
					int ticks = 200;
					monitor.beginTask(ServerUIPlugin.getResource("%loadingTask", type.getName()), ticks);
	
					server = cache.getServer(type, ProgressUtil.getSubMonitorFor(monitor, 200));
					if (server != null) {
						server.setHostname(host);
						ServerUtil.setServerDefaultName(server);
					
						if (type.hasRuntime() && server.getRuntime() == null) {
							// look for existing runtime
							IServerType serverType = server.getServerType();
							IRuntimeType runtimeType = serverType.getRuntimeType();
							runtime = getRuntime(runtimeType, serverType);
							if (runtime == null) {
								// create runtime
								try {
									IRuntimeWorkingCopy runtimeWC = runtimeType.createRuntime(null);
									runtimeWC.setName(server.getName());
									runtime = runtimeWC;
								} catch (Exception e) {
									Trace.trace(Trace.SEVERE, "Couldn't create runtime", e);
								}
							}
							server.setRuntime(runtime);
						}
					}
				} catch (CoreException cex) {
					ce[0] = cex;
				} catch (Throwable t) {
					Trace.trace(Trace.SEVERE, "Error creating element", t);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			wizard.run(true, false, runnable);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error with runnable", e);
		}
	
		if (ce[0] != null)
			wizard.setMessage(ce[0].getLocalizedMessage(), IMessageProvider.ERROR);
		else if (server == null)
			wizard.setMessage(ServerUIPlugin.getResource("%wizErrorServerCreationError"), IMessageProvider.ERROR);
	}

	/**
	 * Look for test environment runtime first. Otherwise, pick any runtime.
	 * 
	 * @param runtimeType
	 * @param serverType
	 * @return
	 */
	protected IRuntime getRuntime(IRuntimeType runtimeType, IServerType serverType) {
		List list = ServerCore.getResourceManager().getRuntimes(runtimeType);
		if (list.isEmpty())
			return null;
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IRuntime runtime2 = (IRuntime) iterator.next();
			if (runtime2.isTestEnvironment())
				return runtime2;
		}
		return (IRuntime) list.get(0);
	}

	/**
	 * Handle the server type selection.
	 */
	protected void handleTypeSelection(IServerType type) {
		if (type == null) {
			wizard.setMessage("", IMessageProvider.ERROR);
		} else {
			wizard.setMessage(null, IMessageProvider.NONE);
			loadServerImpl(type);
		}
		listener.serverSelected(server);
		wizard.update();
	}

	/**
	 * Sets the default container.
	 * @param org.eclipse.core.resources.IContainer
	 */
	/*public void setDefaultContainer(IContainer container) {
		defaultContainer = container;
	}*/
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	
		if (visible) {
			/*if (defaultServerFactory != null) {
				tree.setSelection(new TreeItem[] { defaultServerFactory });
				tree.showItem(tree.getItems()[0]);
			}*/
			// force the focus to initially validate the fields
			handleTypeSelection(null);
		}
		
		Control[] c = getChildren();
		if (c != null) {
			int size = c.length;
			for (int i = 0; i < size; i++)
				if (c[i] != null)
					c[i].setVisible(visible);
		}
	}
	
	public IRuntime getRuntime() {
		return runtime;
	}
	
	public IServerWorkingCopy getServer() {
		return server;
	}
}