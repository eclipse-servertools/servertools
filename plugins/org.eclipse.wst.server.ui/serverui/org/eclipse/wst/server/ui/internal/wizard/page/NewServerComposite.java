/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.viewers.ServerComposite;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * A wizard page used to select a server.
 */
public class NewServerComposite extends Composite {
	protected IWizardHandle wizard;
	protected TaskModel taskModel;
	protected IModule module;
	protected IModuleType moduleType;
	protected String serverTypeId;
	protected String launchMode;
	
	protected static final byte MODE_EXISTING = WizardTaskUtil.MODE_EXISTING;
	protected static final byte MODE_DETECT = WizardTaskUtil.MODE_DETECT;
	protected static final byte MODE_MANUAL = WizardTaskUtil.MODE_MANUAL;
	protected byte mode;

	protected Composite detectComp2;
	protected NewDetectServerComposite detectComp;
	protected HostnameComposite detectHostComp;
	protected Composite manualComp2;
	protected NewManualServerComposite manualComp;
	protected HostnameComposite manualHostComp;
	protected ServerComposite existingComp;

	protected Composite stack;
	protected StackLayout stackLayout; 

	protected String lastHostname;

	protected IServerWorkingCopy existingWC;

	/**
	 * Create a new NewServerComposite.
	 * 
	 * @param parent a parent composite
	 * @param wizard a wizard handle
	 * @param moduleType a module type, or null
	 * @param serverTypeId a server type id, or null
	 * @param launchMode a launch mode
	 */
	public NewServerComposite(Composite parent, IWizardHandle wizard, IModuleType moduleType, String serverTypeId, String launchMode) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.moduleType = moduleType;
		this.serverTypeId = serverTypeId;
		this.launchMode = launchMode;
		
		wizard.setTitle(Messages.wizNewServerTitle);
		wizard.setDescription(Messages.wizNewServerDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER));
		
		createControl();
	}

	/**
	 * Create a new NewServerComposite.
	 * 
	 * @param parent a parent composite
	 * @param wizard a wizard handle
	 * @param module a module
	 * @param launchMode a launch mode
	 */
	public NewServerComposite(Composite parent, IWizardHandle wizard, IModule module, String launchMode) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		this.module = module;
		this.launchMode = launchMode;
		
		wizard.setTitle(Messages.wizNewServerTitle);
		wizard.setDescription(Messages.wizNewServerDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_SERVER));
		
		createControl();
	}

	protected Label createLabel(Composite parent, String text, int span) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		label.setLayoutData(data);
		return label;
	}

	protected Label createLabel(Composite parent, String text) {
		return createLabel(parent, text, 1);
	}

	protected Button createRadioButton(Composite parent, String text, int span) {
		Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		data.horizontalIndent = 10;
		button.setLayoutData(data);
		return button;
	}
	
	protected Text createText(Composite parent, String text2, int span) {
		Text text = new Text(parent, SWT.NONE);
		text.setText(text2);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = span;
		text.setLayoutData(data);
		return text;
	}

	/**
	 * Creates the UI of the page.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		setLayout(layout);
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(this, ContextIds.NEW_SERVER_WIZARD);
		
		if (module != null) {
			if (ILaunchManager.DEBUG_MODE.equals(launchMode))
				wizard.setTitle(Messages.wizDebugOnServerTitle);
			else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
				wizard.setTitle(Messages.wizProfileOnServerTitle);
			else
				wizard.setTitle(Messages.wizRunOnServerTitle);
			wizard.setDescription(Messages.wizNewServerRunOnServerDescription);
			createLabel(this, Messages.wizNewServerSelect, 1);
		}
		
		Button existing = null;
		if (module != null) {
			final Button predefined = createRadioButton(this, Messages.wizNewServerExisting, 1);
			predefined.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (predefined.getSelection())
						toggleMode(MODE_EXISTING);
				}
			});
			existing = predefined;
		}
		
		/*final Button auto = createRadioButton(this, Messages.wizNewServerDetect"), 1);
		auto.setEnabled(false);
		auto.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (auto.getSelection())
					toggleMode(MODE_DETECT);
			}
		});*/
	
		Button manual = null;
		if (module != null) {
			final Button manualButton = createRadioButton(this, Messages.wizNewServerManual, 1);
			manualButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (manualButton.getSelection())
						toggleMode(MODE_MANUAL);
				}
			});
			manual = manualButton;
		}
		
		stack = new Composite(this, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		stack.setLayoutData(data);
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		stack.setLayout(stackLayout);
		
		if (module != null)
			createExistingComposite(stack);
		createAutoComposite(stack);
		createManualComposite(stack);
		
		if (existingComp != null && existing != null) {
			if (isExistingServer()) {
				mode = MODE_EXISTING;
				stackLayout.topControl = existingComp;
				existing.setSelection(true);
			} else {
				mode = MODE_MANUAL;
				stackLayout.topControl = manualComp2;
				manualComp.setVisible(true);
				if (manual != null)
					manual.setSelection(true);
				existing.setEnabled(false);
				existingComp.setEnabled(false);
			}
		} else {
			mode = MODE_MANUAL;
			stackLayout.topControl = manualComp2;
			manualComp.setVisible(true);
			if (manual != null)
				manual.setSelection(true);
		}
		
		if (module != null) {
			// preferred server button
			final Button pref = new Button(this, SWT.CHECK | SWT.WRAP);
			pref.setText(Messages.wizSelectServerPreferred);
			data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_END);
			pref.setLayoutData(data);
			pref.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					taskModel.putObject(WizardTaskUtil.TASK_DEFAULT_SERVER, new Boolean(pref.getSelection()));
				}
			});
			PlatformUI.getWorkbench().getHelpSystem().setHelp(pref, ContextIds.SELECT_SERVER_PREFERENCE);
		}
		
		Dialog.applyDialogFont(this);
	}

	protected void toggleMode(byte newMode) {
		if (!isVisible())
			return;
		
		if (newMode == mode)
			return;
		
		mode = newMode;
		wizard.setMessage(null, IMessageProvider.NONE);
		
		if (mode == MODE_EXISTING) {
			stackLayout.topControl = existingComp;
			existingComp.setSelection(existingComp.getSelectedServer());
		} else if (mode == MODE_DETECT) {
			stackLayout.topControl = detectComp2;
			detectComp.setVisible(true);
		} else {
			stackLayout.topControl = manualComp2;
			manualComp.setVisible(true);
		}
		stack.layout();
		if (taskModel != null) {
			taskModel.putObject(WizardTaskUtil.TASK_MODE, new Byte(mode));
			updateTaskModel();
		}
	}

	protected HostnameComposite createHostComposite(Composite comp) {
		HostnameComposite hostComp = new HostnameComposite(comp, new HostnameComposite.IHostnameSelectionListener() {
			public void hostnameSelected(String host) {
				lastHostname = host;
				if (detectComp != null)
					detectComp.setHost(host);
				if (manualComp != null)
					manualComp.setHost(host);
			}
		});
		
		if (lastHostname != null)
			hostComp.setHostname(lastHostname);
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		hostComp.setLayoutData(data);
		return hostComp;
	}

	protected void createAutoComposite(Composite comp) {
		detectComp2 = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		detectComp2.setLayout(layout);
		
		detectHostComp = createHostComposite(detectComp2);
		
		detectComp = new NewDetectServerComposite(detectComp2, new NewDetectServerComposite.IServerSelectionListener() {
			public void serverSelected(IServerAttributes server) {
				// do nothing
			}
		});

		if (lastHostname != null)
			detectComp.setHost(lastHostname);
		else
			detectComp.setHost("localhost");
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.heightHint = 150;
		detectComp.setLayoutData(data);
	}

	protected void createExistingComposite(Composite comp) {
		existingComp = new ServerComposite(comp, new ServerComposite.ServerSelectionListener() {
			public void serverSelected(IServer server) {
				wizard.setMessage(null, IMessageProvider.NONE);
				
				// check for compatibility
				if (server != null && module != null) {
					IStatus status = isSupportedModule(server, module);
					if (status != null) {
						if (status.getSeverity() == IStatus.ERROR) {
							wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
                            server = null;
						} else if (status.getSeverity() == IStatus.WARNING)
							wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
						else if (status.getSeverity() == IStatus.INFO)
							wizard.setMessage(status.getMessage(), IMessageProvider.INFORMATION);
					}
				}
				
				if (existingWC != null) {
					if (server != null && server.equals(existingWC.getOriginal()))
						return;
					existingWC = null;
				}
				if (server != null)
					existingWC = server.createWorkingCopy();
				updateTaskModel();
			}
		}, module, launchMode);
		existingComp.setIncludeIncompatibleVersions(true);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.heightHint = 150;
		existingComp.setLayoutData(data);
	}

	/**
	 * Returns the status of whether the given module could be added to the server.
	 * 
	 * @param server a server
	 * @param module a module
	 * @return an IStatus representing the error or warning, or null if there are no problems
	 */
	protected static IStatus isSupportedModule(IServerAttributes server, IModule module) {
		if (server != null && module != null) {
			IServerType serverType = server.getServerType();
			IModuleType mt = module.getModuleType();
			if (!ServerUtil.isSupportedModule(serverType.getRuntimeType().getModuleTypes(), mt)) {
				String type = mt.getName();
				return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, NLS.bind(Messages.errorVersionLevel, new Object[] { type, mt.getVersion() }));
			}
			
			IModule[] rootModules = null;
			try {
				rootModules = server.getRootModules(module, null);
			} catch (CoreException ce) {
				return ce.getStatus();
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "Could not find root module", e);
				}
			}
			if (rootModules != null) {
				if (rootModules.length == 0)
					return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, Messages.errorRootModule);
				
				int size = rootModules.length;
				IStatus status = null;
				boolean found = false;
				for (int i = 0; i < size; i++) {
					try {
						if (server != null)
							status = server.canModifyModules(new IModule[] {rootModules[i]}, null, null);
						if (status != null && status.isOK())
							found = true;
					} catch (Exception e) {
						if (Trace.WARNING) {
							Trace.trace(Trace.STRING_WARNING, "Could not find root module", e);
						}
					}
				}
				if (!found && status != null)
					return status;
			}
		}
		return null;
	}

	protected boolean isExistingServer() {
		if (module == null || launchMode == null)
			return false;
		
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				IModuleType mt = module.getModuleType();
				if (ServerUIPlugin.isCompatibleWithLaunchMode(servers[i], launchMode) &&
					ServerUtil.isSupportedModule(servers[i].getServerType().getRuntimeType().getModuleTypes(), mt))
						return true;
			}
		}
		return false;
	}

	protected void createManualComposite(Composite comp) {
		manualComp2 = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		manualComp2.setLayout(layout);
		manualComp2.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		IModuleType mt = moduleType;
		boolean includeIncompatible = true;
		if (moduleType != null)
			includeIncompatible = false;
		
		if (module != null)
			mt = module.getModuleType();
		
		manualComp = new NewManualServerComposite(manualComp2, new NewManualServerComposite.IWizardHandle2() {
			public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InterruptedException, InvocationTargetException {
				wizard.run(fork, cancelable, runnable);
			}
			public void update() {
				wizard.update();
			}
			public void setMessage(String newMessage, int newType) {
				wizard.setMessage(newMessage, newType);
			}
		}, mt, module, serverTypeId, includeIncompatible, new NewManualServerComposite.ServerSelectionListener() {
			public void serverSelected(IServerAttributes server) {
				updateTaskModel();
			}
			public void runtimeSelected(IRuntime runtime) {
				updateTaskModel();
			}
		});
		
		if (lastHostname != null)
			manualComp.setHost(lastHostname);
		else
			manualComp.setHost("localhost");

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		data.heightHint = 360;
		manualComp.setLayoutData(data);
	}
	
	public NewManualServerComposite getNewManualServerComposite (){
		return manualComp;
	}
	
	protected void updateTaskModel() {
		if (taskModel != null) {
			IServerWorkingCopy server = getServer();
			if (server != null) {
				taskModel.putObject(TaskModel.TASK_SERVER, server);
				taskModel.putObject(TaskModel.TASK_RUNTIME, server.getRuntime());
			} else {
				taskModel.putObject(TaskModel.TASK_SERVER, null);
				taskModel.putObject(TaskModel.TASK_RUNTIME, null);
			}
		}
		wizard.update();
	}

	public void setTaskModel(TaskModel model) {
		taskModel = model;
		taskModel.putObject(WizardTaskUtil.TASK_MODE, new Byte(mode));
		updateTaskModel();
	}

	public IServerWorkingCopy getServer() {
		if (mode == MODE_EXISTING)
			return existingWC; //existingComp.getSelectedServer();
		else if (mode == MODE_DETECT)
			return detectComp.getServer();
		else
			return manualComp.getServer();
	}

	public IRuntime getRuntime() {
		if (mode == MODE_EXISTING) {
			IServer server = existingComp.getSelectedServer();
			if (server != null)
				return server.getRuntime();
			return null;
		} else if (mode == MODE_DETECT)
			return null;
		else
			return manualComp.getRuntime();
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		Control[] c = getChildren();
		if (c != null) {
			int size = c.length;
			for (int i = 0; i < size; i++)
				if (c[i] != null)
					c[i].setVisible(visible);
		}
	}
}