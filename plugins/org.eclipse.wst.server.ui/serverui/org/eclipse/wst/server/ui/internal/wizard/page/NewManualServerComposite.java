/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.ProgressUtil;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.viewers.ServerTypeComposite;
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
	
	protected Label runtimeLabel;
	protected Combo runtimeCombo;
	protected IRuntime[] runtimes;

	protected IRuntime runtime;
	protected IServerWorkingCopy server;
	protected ServerSelectionListener listener;
	
	protected String host;
	
	protected String type;
	protected String version;

	protected ElementCreationCache cache = new ElementCreationCache();

	/**
	 * Creates a new server and server configuration.  If the initial
	 * resource selection contains exactly one container resource then it will be
	 * used as the default container resource.
	 *
	 * @param org.eclipse.jface.wizard.IWizard parent
	 */
	public NewManualServerComposite(Composite parent, IWizardHandle2 wizard, String type, String version, ServerSelectionListener listener) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.listener = listener;
		
		this.type = type;
		this.version = version;

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
		
		serverTypeComposite = new ServerTypeComposite(this, SWT.NONE, type, version, new ServerTypeComposite.ServerTypeSelectionListener() {
			public void serverTypeSelected(IServerType type2) {
				handleTypeSelection(type2);
				//WizardUtil.defaultSelect(parent, CreateServerWizardPage.this);
			}
		});
		serverTypeComposite.setIncludeTestEnvironments(false);
		serverTypeComposite.setIncludeIncompatibleVersions(true);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 2;
		serverTypeComposite.setLayoutData(data);
		WorkbenchHelp.setHelp(serverTypeComposite, ContextIds.NEW_SERVER_INSTANCE_FACTORY);
		
		runtimeLabel = new Label(this, SWT.NONE);
		runtimeLabel.setText(ServerUIPlugin.getResource("%wizNewServerRuntime"));
		
		runtimeCombo = new Combo(this, SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		runtimeCombo.setLayoutData(data);
		runtimeCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				try {
					runtime = runtimes[runtimeCombo.getSelectionIndex()];
					if (server != null)
						server.setRuntime(runtime);
				} catch (Exception ex) {
					// ignore
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
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
			server.setHost(host);
			ServerUtil.setServerDefaultName(server);
		}
	}
	
	/**
	 * Return the current editable element.
	 * @return org.eclipse.wst.server.core.model.IServer
	 */
	protected void loadServerImpl(final IServerType serverType) {
		//runtime = null;
		server = null;
		
		if (serverType == null)
			return;
	
		server = cache.getCachedServer(serverType, host);
		if (server != null) {
			server.setHost(host);
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
					monitor.beginTask(ServerUIPlugin.getResource("%loadingTask", serverType.getName()), ticks);
	
					server = cache.getServer(serverType, host, ProgressUtil.getSubMonitorFor(monitor, 200));
					if (server != null) {
						server.setHost(host);
						ServerUtil.setServerDefaultName(server);
					
						if (serverType.hasRuntime() && server.getRuntime() == null)
							server.setRuntime(runtime);
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
	protected IRuntime getDefaultRuntime() {
		if (runtimes == null || runtimes.length == 0)
			return null;
		
		if (runtimes != null) {
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (runtimes[i].isTestEnvironment())
					return runtimes[i];
		
				if (!runtimes[i].isStub())
					return runtime;
			}
		}
		return runtimes[0];
	}
	
	protected void updateRuntimeCombo(IServerType serverType) {
		runtime = null;
		
		if (serverType == null || !serverType.hasRuntime()) {
			if (runtimeLabel != null) {
				runtimeLabel.setEnabled(false);
				runtimeCombo.setItems(new String[0]);
				runtimeCombo.setEnabled(false);
				runtimeLabel.setVisible(false);
				runtimeCombo.setVisible(false);
			}
			return;
		}

		IRuntimeType runtimeType = serverType.getRuntimeType();
		runtimes = ServerCore.getResourceManager().getRuntimes(runtimeType);
		
		if (server != null && SocketUtil.isLocalhost(server.getHost()) && runtimes != null) {
			List runtimes2 = new ArrayList();
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				IRuntime runtime2 = runtimes[i];
				if (!runtime2.isStub())
					runtimes2.add(runtime2);
			}
			runtimes = new IRuntime[runtimes2.size()];
			runtimes2.toArray(runtimes);
		}
		
		if (runtimes.length == 0) {
			// create new runtime
			try {
				IRuntimeWorkingCopy runtimeWC = runtimeType.createRuntime(null, null);
				ServerUtil.setRuntimeDefaultName(runtimeWC);
				runtimes = new IRuntime[1];
				runtimes[0] = runtimeWC;
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Couldn't create runtime", e);
			}
		}
		
		int size = runtimes.length;
		String[] items = new String[size];
		for (int i = 0; i < size; i++)
			items[i] = runtimes[i].getName();
		
		runtime = getDefaultRuntime();
		if (runtimeCombo != null) {
			runtimeCombo.setItems(items);
			if (runtimes.length > 0) {
				for (int i = 0; i < size; i++) {
					if (runtimes[i].equals(runtime))
						runtimeCombo.select(i);
				}
			}
			runtimeCombo.setEnabled(size > 1);
			runtimeLabel.setEnabled(size > 1);
			runtimeLabel.setVisible(size > 1);
			runtimeCombo.setVisible(size > 1);
		}
	}

	/**
	 * Handle the server type selection.
	 */
	protected void handleTypeSelection(IServerType serverType) {
		boolean wrong = false;
		if (serverType != null && type != null) {
			IRuntimeType runtimeType = serverType.getRuntimeType();
			if (!ServerUtil.isSupportedModule(runtimeType.getModuleTypes(), type, version)) {
				serverType = null;
				wrong = true;
				//wizard.setMessage("Not the right spec level2", IMessageProvider.ERROR);
			}
		}
		
		updateRuntimeCombo(serverType);
		if (wrong) {
			IModuleType mk = ServerCore.getModuleType(type);
			String type2 = null;
			if (mk != null)
				type2 = mk.getName();
			wizard.setMessage(ServerUIPlugin.getResource("%errorVersionLevel", new Object[] { type2, version }), IMessageProvider.ERROR);
		} else if (serverType == null)
			wizard.setMessage("", IMessageProvider.ERROR);
		else {
			wizard.setMessage(null, IMessageProvider.NONE);
			loadServerImpl(serverType);
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
				if (c[i] != null && c[i] instanceof ServerTypeComposite)
					c[i].setVisible(visible);
		}
		if (visible)
			handleTypeSelection(serverTypeComposite.getSelectedServerType());
	}
	
	public IRuntime getRuntime() {
		return runtime;
	}
	
	public IServerWorkingCopy getServer() {
		return server;
	}
}