/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Publisher;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.AbstractUIControl;
import org.eclipse.wst.server.ui.AbstractUIControl.UIControlEntry;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.AbstractUIControl.IUIControlListener;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.command.*;
import org.eclipse.wst.server.ui.internal.viewers.BaseContentProvider;
import org.eclipse.wst.server.ui.internal.viewers.BaseLabelProvider;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;


/**
 * Server general editor page.
 */
public class OverviewEditorPart extends ServerEditorPart implements IUIControlListener {
	protected Text serverName;
	protected Text serverConfiguration;
	protected Text hostname;
	protected Label hostnameLabel;
	protected ControlDecoration hostnameDecoration;
	protected Combo runtimeCombo;
	protected Button browse;
	protected Button autoPublishDisable;
	protected Button autoPublishEnable;
	protected Spinner autoPublishTime;
	protected Table publishersTable;
	protected CheckboxTableViewer publishersViewer;
	protected Spinner startTimeoutSpinner;
	protected Spinner stopTimeoutSpinner;
	protected ManagedForm managedForm;

	protected boolean updating;

	protected IRuntime[] runtimes;

	protected PropertyChangeListener listener;

	protected IRuntimeLifecycleListener runtimeListener;

	private IServerType oldServerType;
	
	class PublisherContentProvider extends BaseContentProvider {
		protected Publisher[] pubs;
		public PublisherContentProvider(Publisher[] pubs) {
			this.pubs = pubs;
		}

		public Object[] getElements(Object inputElement) {
			return pubs;
		}
	}

	class PublishLabelProvider extends BaseLabelProvider {
		public String getText(Object element) {
			if (element instanceof Publisher) {
				Publisher pub = (Publisher) element;
				return pub.getName();
			}
			return "";
		}
	}

	/**
	 * OverviewEditorPart constructor comment.
	 */
	public OverviewEditorPart() {
		super();
	}

	/**
	 * 
	 */
	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals("configuration-id") && serverConfiguration != null)
					validate();
				
				// following code behaves poorly because there is no default local or remote
				// publishing time per server or server type. as a result it sets the value
				// to the default, which seems to be more harm than good until we revamp the
				// default publishing times
				/*if ("hostname".equals(event.getPropertyName())) {
					final String oldHostname = (String) event.getOldValue();
					final String newHostname = (String) event.getNewValue();
					final boolean isNewHostnameLocalhost = SocketUtil.isLocalhost(newHostname);
					if (isNewHostnameLocalhost != SocketUtil.isLocalhost(oldHostname)) {
						// run this code only if the hostname changed from
						// 'localhost' to a remote name, or vice-versa
						hostname.getDisplay().asyncExec(new Runnable() {
							public void run() {
								try {
									if (isNewHostnameLocalhost) {
										int autoPublishTime2 = ServerPreferences.getInstance().getAutoPublishLocalTime();
										((ServerWorkingCopy)getServer()).setAutoPublishTime(autoPublishTime2);
									} else {
										int autoPublishTime2 = ServerPreferences.getInstance().getAutoPublishRemoteTime();
										((ServerWorkingCopy)getServer()).setAutoPublishTime(autoPublishTime2);
									}
								} catch (Exception e) {
									Trace.trace(Trace.WARNING, "Could not update publish time to new host");
								}
							}
						});
					}
				}*/
				
				if (updating)
					return;
				updating = true;
				if (event.getPropertyName().equals("name"))
					updateNames();
				else if (event.getPropertyName().equals("hostname") && hostname != null) {
					hostname.setText((String) event.getNewValue());
				} else if (event.getPropertyName().equals("runtime-id")) {
					String runtimeId = (String) event.getNewValue();
					IRuntime runtime = null;
					if (runtimeId != null)
						runtime = ServerCore.findRuntime(runtimeId);
					if (runtimeCombo != null) {
						int size = runtimes.length;
						for (int i = 0; i < size; i++) {
							if (runtimes[i].equals(runtime))
								runtimeCombo.select(i);
						}
					}
				} else if (event.getPropertyName().equals("configuration-id") && serverConfiguration != null) {
					String path = (String) event.getNewValue();
					serverConfiguration.setText(path);
				} else if (event.getPropertyName().equals(Server.PROP_AUTO_PUBLISH_TIME)) {
					Integer curAutoPublishTime = (Integer)event.getNewValue();
					autoPublishTime.setSelection(curAutoPublishTime.intValue());
					SWTUtil.setSpinnerTooltip(autoPublishTime);
					validate();
				} else if (event.getPropertyName().equals(Server.PROP_AUTO_PUBLISH_SETTING)) {
					Integer autoPublishSetting = (Integer)event.getNewValue();
					int setting = autoPublishSetting.intValue();
					autoPublishEnable.setSelection(setting == Server.AUTO_PUBLISH_ENABLE);
					autoPublishDisable.setSelection(setting == Server.AUTO_PUBLISH_DISABLE);
					autoPublishTime.setEnabled(setting == Server.AUTO_PUBLISH_ENABLE);
					validate();
				} else if (event.getPropertyName().equals(Server.PROP_START_TIMEOUT)) {
					Integer time = (Integer)event.getNewValue();
					startTimeoutSpinner.setSelection(time.intValue());
					SWTUtil.setSpinnerTooltip(startTimeoutSpinner);
				} else if (event.getPropertyName().equals(Server.PROP_STOP_TIMEOUT)) {
					Integer time = (Integer)event.getNewValue();
					stopTimeoutSpinner.setSelection(time.intValue());
					SWTUtil.setSpinnerTooltip(stopTimeoutSpinner);
				} else if (Server.PROP_PUBLISHERS.equals(event.getPropertyName())) {
					if (publishersViewer == null)
						return;
					
					List<String> list = (List<String>) event.getNewValue();
					Iterator<String> iter = list.iterator();
					while (iter.hasNext()) {
						String id = iter.next();
						int ind = id.indexOf(":");
						boolean enabled = false;
						if ("true".equals(id.substring(ind+1)))
							enabled = true;
						id = id.substring(0, ind);
						Publisher pub = ServerPlugin.findPublisher(id);
						if (pub != null)
							publishersViewer.setChecked(pub, enabled);
					}
				}
				updating = false;
			}
		};
		if (server != null)
			server.addPropertyChangeListener(listener);
	}

	protected void updateNames() {
		if (serverName != null)
			serverName.setText(server.getName());
	}

	/**
	 * Creates the SWT controls for this workbench part.
	 *
	 * @param parent the parent control
	 */
	public final void createPartControl(final Composite parent) {
		managedForm = new ManagedForm(parent);
		setManagedForm(managedForm);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		toolkit.decorateFormHeading(form.getForm());
		form.setText(Messages.serverEditorOverviewPageTitle);
		form.setImage(ImageResource.getImage(ImageResource.IMG_SERVER));
		form.getBody().setLayout(new GridLayout());
		
		Composite columnComp = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		//layout.marginHeight = 10;
		//layout.marginWidth = 10;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		columnComp.setLayout(layout);
		columnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		// left column
		Composite leftColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		leftColumnComp.setLayout(layout);
		leftColumnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		createGeneralSection(leftColumnComp, toolkit);
		
		insertSections(leftColumnComp, "org.eclipse.wst.server.editor.overview.left");
		
		// right column
		Composite rightColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		rightColumnComp.setLayout(layout);
		rightColumnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		createPublishSection(rightColumnComp, toolkit);
		createTimeoutSection(rightColumnComp, toolkit);
		
		insertSections(rightColumnComp, "org.eclipse.wst.server.editor.overview.right");
		
		form.reflow(true);
		
		initialize();
	}

	protected void createGeneralSection(Composite leftColumnComp, FormToolkit toolkit) {
		Section section = toolkit.createSection(leftColumnComp, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.serverEditorOverviewGeneralSection);
		section.setDescription(Messages.serverEditorOverviewGeneralDescription);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.EDITOR_OVERVIEW_PAGE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		int decorationWidth = FieldDecorationRegistry.getDefault().getMaximumDecorationWidth(); 
		
		// server name
		if (server != null) {
			createLabel(toolkit, composite, Messages.serverEditorOverviewServerName);
			
			serverName = toolkit.createText(composite, server.getName());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			data.horizontalIndent = decorationWidth;
			serverName.setLayoutData(data);
			serverName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					execute(new SetServerNameCommand(getServer(), serverName.getText()));
					updating = false;
					validate();
				}
			});
			whs.setHelp(serverName, ContextIds.EDITOR_SERVER);
			
			// hostname
			hostnameLabel = createLabel(toolkit, composite, Messages.serverEditorOverviewServerHostname);
			
			hostname = toolkit.createText(composite, server.getHost());
			hostnameDecoration = new ControlDecoration(hostname, SWT.TOP | SWT.LEAD);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			data.horizontalIndent = decorationWidth;
			hostname.setLayoutData(data);
			hostname.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					execute(new SetServerHostnameCommand(getServer(), hostname.getText()));
					updating = false;
				}
			});
			whs.setHelp(hostname, ContextIds.EDITOR_HOSTNAME);
			
			FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
			FieldDecoration fd = registry.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
			hostnameDecoration.setImage(fd.getImage());
			hostnameDecoration.setDescriptionText(fd.getDescription());
			hostnameDecoration.hide();
			
			hostname.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					hostnameDecoration.show();
				}

				public void focusLost(FocusEvent e) {
					hostnameDecoration.hide();
				}
			});
			
			//updateDecoration(hostnameDecoration, new Status(IStatus.INFO, ServerUIPlugin.PLUGIN_ID, "Press Ctrl-Space"));
			
			List<String> hosts = ServerUIPlugin.getPreferences().getHostnames();
			String[] hosts2 = hosts.toArray(new String[hosts.size()]);
			new AutoCompleteField(hostname, new TextContentAdapter(), hosts2);
		}
		
		// runtime
		if (server != null && server.getServerType() != null && server.getServerType().hasRuntime()) {
			final Hyperlink link = toolkit.createHyperlink(composite, Messages.serverEditorOverviewRuntime, SWT.NONE);
			link.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					IRuntime runtime = server.getRuntime();
					if (runtime != null && ServerUIPlugin.hasWizardFragment(runtime.getRuntimeType().getId()))
						editRuntime(runtime);
				}
			});
			
			final IRuntime runtime = server.getRuntime();
			if (runtime == null || !ServerUIPlugin.hasWizardFragment(runtime.getRuntimeType().getId()))
				link.setEnabled(false);
			
			IRuntimeType runtimeType = server.getServerType().getRuntimeType();
			runtimes = ServerUIPlugin.getRuntimes(runtimeType);
			
			runtimeCombo = new Combo(composite, SWT.READ_ONLY);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalIndent = decorationWidth;
			data.horizontalSpan = 2;
			runtimeCombo.setLayoutData(data);
			updateRuntimeCombo();
			
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				if (runtimes[i].equals(runtime))
					runtimeCombo.select(i);
			}
			
			runtimeCombo.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					try {
						if (updating)
							return;
						updating = true;
						IRuntime newRuntime = runtimes[runtimeCombo.getSelectionIndex()];
						execute(new SetServerRuntimeCommand(getServer(), newRuntime));
						link.setEnabled(newRuntime != null && !ServerUIPlugin.hasWizardFragment(newRuntime.getRuntimeType().getId()));
						updating = false;
					} catch (Exception ex) {
						// ignore
					}
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			whs.setHelp(runtimeCombo, ContextIds.EDITOR_RUNTIME);
			
			// add runtime listener
			runtimeListener = new IRuntimeLifecycleListener() {
				public void runtimeChanged(final IRuntime runtime2) {
					// may be name change of current runtime
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (runtime2.equals(getServer().getRuntime())) {
								try {
									if (updating)
										return;
									updating = true;
									execute(new SetServerRuntimeCommand(getServer(), runtime2));
									updating = false;
								} catch (Exception ex) {
									// ignore
								}
							}
							
							if (runtimeCombo != null && !runtimeCombo.isDisposed()) {
								updateRuntimeCombo();
								
								int size2 = runtimes.length;
								for (int i = 0; i < size2; i++) {
									if (runtimes[i].equals(runtime))
										runtimeCombo.select(i);
								}
							}
						}
					});
				}

				public void runtimeAdded(final IRuntime runtime2) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (runtimeCombo != null && !runtimeCombo.isDisposed()) {
								updateRuntimeCombo();
								
								int size2 = runtimes.length;
								for (int i = 0; i < size2; i++) {
									if (runtimes[i].equals(runtime))
										runtimeCombo.select(i);
								}
							}
						}
					});
				}

				public void runtimeRemoved(IRuntime runtime2) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (runtimeCombo != null && !runtimeCombo.isDisposed()) {
								updateRuntimeCombo();
								
								int size2 = runtimes.length;
								for (int i = 0; i < size2; i++) {
									if (runtimes[i].equals(runtime))
										runtimeCombo.select(i);
								}
							}
						}
					});
				}
			};
			
			ServerCore.addRuntimeLifecycleListener(runtimeListener);
		}
		
		// server configuration path
		if (server != null && server.getServerType() != null && server.getServerType().hasServerConfiguration()) {
			createLabel(toolkit, composite, Messages.serverEditorOverviewServerConfigurationPath);
			
			IFolder folder = server.getServerConfiguration();
			if (folder == null)
				serverConfiguration = toolkit.createText(composite, Messages.elementUnknownName);
			else
				serverConfiguration = toolkit.createText(composite, "" + server.getServerConfiguration().getFullPath());
			
			serverConfiguration.setEditable(false);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalIndent = decorationWidth;
			data.widthHint = 75;
			serverConfiguration.setLayoutData(data);
			
			whs.setHelp(serverConfiguration, ContextIds.EDITOR_CONFIGURATION);
			
			final IFolder currentFolder = server.getServerConfiguration();
			browse = toolkit.createButton(composite, Messages.serverEditorOverviewServerConfigurationBrowse, SWT.PUSH);
			browse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(serverConfiguration.getShell(),
						currentFolder, true, Messages.serverEditorOverviewServerConfigurationBrowseMessage);
					dialog.showClosedProjects(false);
					
					if (dialog.open() != Window.CANCEL) {
						Object[] result = dialog.getResult();
						if (result != null && result.length == 1) {
							IPath path = (IPath) result[0];
							
							IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
							IResource resource = root.findMember(path);
							if (resource != null && resource instanceof IFolder) {
								IFolder folder2 = (IFolder) resource;
							
								if (updating)
									return;
								updating = true;
								execute(new SetServerConfigurationFolderCommand(getServer(), folder2));
								serverConfiguration.setText(folder2.getFullPath().toString());
								updating = false;
							}
						}
					}
				}
			});
			browse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		}
		
		if (server != null && server.getServerType() != null) {
			IServerType serverType = server.getServerType();
			if (serverType.supportsLaunchMode(ILaunchManager.RUN_MODE) || serverType.supportsLaunchMode(ILaunchManager.DEBUG_MODE)
					|| serverType.supportsLaunchMode(ILaunchManager.PROFILE_MODE)) {
				ILaunchConfigurationType launchType = ((ServerType) serverType).getLaunchConfigurationType();
				if (launchType != null && launchType.isPublic()) {
					final Hyperlink link = toolkit.createHyperlink(composite, Messages.serverEditorOverviewOpenLaunchConfiguration, SWT.NONE);
					GridData data = new GridData();
					data.horizontalSpan = 2;
					link.setLayoutData(data);
					link.addHyperlinkListener(new HyperlinkAdapter() {
						public void linkActivated(HyperlinkEvent e) {
							try {
								ILaunchConfiguration launchConfig = ((Server) getServer()).getLaunchConfiguration(true, null);
								// TODO: use correct launch group
								DebugUITools.openLaunchConfigurationPropertiesDialog(link.getShell(), launchConfig, "org.eclipse.debug.ui.launchGroup.run");
							} catch (CoreException ce) {
								Trace.trace(Trace.SEVERE, "Could not create launch configuration", ce);
							}
						}
					});
				}
			}
		}
		
		// Insertion of extension widgets. If the page modifier createControl is not implemented still 
		// add the modifier to the listeners list.
		List<ServerEditorOverviewPageModifier> pageModifiersLst = ServerUIPlugin.getServerEditorOverviewPageModifiers();
		for (ServerEditorOverviewPageModifier curPageModifier : pageModifiersLst) {
			if(server != null && server.getServerType() != null){
				curPageModifier.createControl(ServerEditorOverviewPageModifier.UI_LOCATION.OVERVIEW, composite);
				curPageModifier.setUIControlListener(this);
			}
		}
	}

	protected void createPublishSection(Composite rightColumnComp, FormToolkit toolkit) {
		Section section = toolkit.createSection(rightColumnComp, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(Messages.serverEditorOverviewPublishSection);
		section.setDescription(Messages.serverEditorOverviewPublishDescription);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.EDITOR_OVERVIEW_PAGE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		//	 auto-publish
		if (server != null) {
			final Server svr = (Server) server;
			int publishSetting = svr.getAutoPublishSetting();
			
			GridData data = new GridData(GridData.FILL_HORIZONTAL);															
			autoPublishDisable = toolkit.createButton(composite, Messages.serverEditorOverviewAutoPublishDisable, SWT.RADIO);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			autoPublishDisable.setLayoutData(data);
			autoPublishDisable.setSelection(publishSetting == Server.AUTO_PUBLISH_DISABLE);
			whs.setHelp(autoPublishDisable, ContextIds.EDITOR_AUTOPUBLISH_DISABLE);
			
			autoPublishEnable = toolkit.createButton(composite, Messages.serverEditorOverviewAutoPublishEnabled, SWT.RADIO);
			autoPublishEnable.setSelection(publishSetting == Server.AUTO_PUBLISH_ENABLE);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			autoPublishEnable.setLayoutData(data);
			whs.setHelp(autoPublishEnable, ContextIds.EDITOR_AUTOPUBLISH_ENABLE);
			
			final Label autoPublishTimeLabel = createLabel(toolkit,composite, Messages.serverEditorOverviewAutoPublishEnabledInterval);
			data = new GridData();
			data.horizontalIndent = 20;
			autoPublishTimeLabel.setLayoutData(data);
			autoPublishTimeLabel.setEnabled(autoPublishEnable.getSelection());
			
			autoPublishTime = new Spinner(composite, SWT.BORDER);
			autoPublishTime.setMinimum(0);
			autoPublishTime.setIncrement(5);
			autoPublishTime.setMaximum(120);
			autoPublishTime.setSelection(svr.getAutoPublishTime());
			data = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data.widthHint = 30;
			autoPublishTime.setLayoutData(data);
			autoPublishTime.setEnabled(autoPublishEnable.getSelection());
			SWTUtil.setSpinnerTooltip(autoPublishTime);
			whs.setHelp(autoPublishTime, ContextIds.EDITOR_AUTOPUBLISH_ENABLE);
			
			autoPublishEnable.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (updating || !autoPublishEnable.getSelection())
						return;
					updating = true;
					execute(new SetServerAutoPublishDefaultCommand(getServer(), Server.AUTO_PUBLISH_ENABLE));
					updating = false;
					autoPublishTimeLabel.setEnabled(autoPublishEnable.getSelection());
					autoPublishTime.setEnabled(autoPublishEnable.getSelection());
					validate();
				}
			});
						
			autoPublishDisable.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (updating || !autoPublishDisable.getSelection())
						return;
					updating = true;
					execute(new SetServerAutoPublishDefaultCommand(getServer(), Server.AUTO_PUBLISH_DISABLE));
					updating = false;
					autoPublishTimeLabel.setEnabled(autoPublishEnable.getSelection());
					autoPublishTime.setEnabled(autoPublishEnable.getSelection());
					validate();
				}
			});
			
			autoPublishTime.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					execute(new SetServerAutoPublishTimeCommand(getServer(), autoPublishTime.getSelection()));
					SWTUtil.setSpinnerTooltip(autoPublishTime);
					updating = false;
					validate();
				}
			});
			
			// publishers
			Publisher[] pubs = ((Server)server).getAllPublishers();
			if (pubs != null && pubs.length > 0) {
				Label label = toolkit.createLabel(composite, Messages.serverEditorOverviewPublishers);
				data = new GridData(GridData.FILL_HORIZONTAL);
				data.horizontalSpan = 2;
				label.setLayoutData(data);
				
				publishersTable = toolkit.createTable(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK);
				publishersTable.setHeaderVisible(false);
				publishersTable.setLinesVisible(false);
				whs.setHelp(publishersTable, ContextIds.EDITOR_PUBLISHTASKS_CONFIGURATION);
				
				data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
				data.horizontalSpan = 2;
				data.heightHint = 80;
				publishersTable.setLayoutData(data);
				
				publishersViewer = new CheckboxTableViewer(publishersTable);
				publishersViewer.setColumnProperties(new String[] {"name"});
				publishersViewer.setContentProvider(new PublisherContentProvider(pubs));
				publishersViewer.setLabelProvider(new PublishLabelProvider());
				publishersViewer.setInput("root");
				
				Publisher[] pubs2 = ((Server)server).getEnabledPublishers();
				for (Publisher p : pubs2)
					publishersViewer.setChecked(p, true);
				
				publishersViewer.addCheckStateListener(new ICheckStateListener() {
					public void checkStateChanged(CheckStateChangedEvent event) {
						Object element = event.getElement();
						if (element == null || !(element instanceof Publisher))
							return;
						if (updating)
							return;
						updating = true;
						execute(new SetPublisherEnablementCommand(getServer(), (Publisher) element, event.getChecked()));
						updating = false;
					}
				});
			}
		}
	}

	protected void createTimeoutSection(Composite rightColumnComp, FormToolkit toolkit) {
		Section section = toolkit.createSection(rightColumnComp, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(Messages.serverEditorOverviewTimeoutSection);
		section.setDescription(Messages.serverEditorOverviewTimeoutDescription);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.EDITOR_OVERVIEW_PAGE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		//	timeouts
		if (server != null) {
			final Server svr = (Server) server;
			
			// start timeout label
			final Label startTimeoutLabel = createLabel(toolkit, composite, Messages.serverEditorOverviewStartTimeout);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalIndent = 20;
			startTimeoutLabel.setLayoutData(data);			
			
			startTimeoutSpinner = new Spinner(composite, SWT.BORDER);
			startTimeoutSpinner.setEnabled(true);
			startTimeoutSpinner.setMinimum(1);
			startTimeoutSpinner.setMaximum(60*60*24); // 24 hours
			startTimeoutSpinner.setIncrement(5);
			startTimeoutSpinner.setSelection(svr.getStartTimeout());
			SWTUtil.setSpinnerTooltip(startTimeoutSpinner);
			data = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data.widthHint = 30;
			startTimeoutSpinner.setLayoutData(data);
			whs.setHelp(startTimeoutSpinner, ContextIds.EDITOR_TIMEOUT_START);
			
			// stop timeout label
			final Label stopTimeoutLabel = createLabel(toolkit, composite, Messages.serverEditorOverviewStopTimeout);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalIndent = 20;
			stopTimeoutLabel.setLayoutData(data);			
			
			stopTimeoutSpinner = new Spinner(composite, SWT.BORDER);
			stopTimeoutSpinner.setEnabled(true);
			stopTimeoutSpinner.setMinimum(1);
			stopTimeoutSpinner.setMaximum(60*60*24); // 24 hours
			stopTimeoutSpinner.setIncrement(5);
			stopTimeoutSpinner.setSelection(svr.getStopTimeout());
			SWTUtil.setSpinnerTooltip(stopTimeoutSpinner);
			data = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data.widthHint = 30;
			stopTimeoutSpinner.setLayoutData(data);
			whs.setHelp(stopTimeoutSpinner, ContextIds.EDITOR_TIMEOUT_STOP);
			
			startTimeoutSpinner.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					execute(new SetServerStartTimeoutCommand(getServer(), startTimeoutSpinner.getSelection()));
					SWTUtil.setSpinnerTooltip(startTimeoutSpinner);
					updating = false;
					validate();
				}
			});
			stopTimeoutSpinner.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					execute(new SetServerStopTimeoutCommand(getServer(), stopTimeoutSpinner.getSelection()));
					SWTUtil.setSpinnerTooltip(stopTimeoutSpinner);
					updating = false;
					validate();			
				}
			});
		}
	}

	protected void editRuntime(IRuntime runtime) {
		IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();
		if (showWizard(runtimeWorkingCopy) != Window.CANCEL) {
			try {
				runtimeWorkingCopy.save(false, null);
			} catch (Exception ex) {
				// ignore
			}
		}
	}

	protected int showWizard(final IRuntimeWorkingCopy runtimeWorkingCopy) {
		String title = Messages.wizEditRuntimeWizardTitle;
		final WizardFragment fragment2 = ServerUIPlugin.getWizardFragment(runtimeWorkingCopy.getRuntimeType().getId());
		if (fragment2 == null)
			return Window.CANCEL;
		
		TaskModel taskModel = new TaskModel();
		taskModel.putObject(TaskModel.TASK_RUNTIME, runtimeWorkingCopy);

		WizardFragment fragment = new WizardFragment() {
			protected void createChildFragments(List<WizardFragment> list) {
				list.add(fragment2);
				list.add(WizardTaskUtil.SaveRuntimeFragment);
			}
		};
		
		TaskWizard wizard = new TaskWizard(title, fragment, taskModel);
		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(getEditorSite().getShell(), wizard);
		return dialog.open();
	}

	protected void updateRuntimeCombo() {
		IRuntimeType runtimeType = server.getServerType().getRuntimeType();
		runtimes = ServerUIPlugin.getRuntimes(runtimeType);
		
		if (SocketUtil.isLocalhost(server.getHost()) && runtimes != null) {
			List<IRuntime> runtimes2 = new ArrayList<IRuntime>();
			int size = runtimes.length;
			for (int i = 0; i < size; i++) {
				IRuntime runtime2 = runtimes[i];
				if (!runtime2.isStub())
					runtimes2.add(runtime2);
			}
			runtimes = new IRuntime[runtimes2.size()];
			runtimes2.toArray(runtimes);
		}
		
		int size = runtimes.length;
		String[] items = new String[size];
		for (int i = 0; i < size; i++)
			items[i] = runtimes[i].getName();
		
		runtimeCombo.setItems(items);
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	public void dispose() {
		super.dispose();
		
		if (server != null)
			server.removePropertyChangeListener(listener);
		
		if (runtimeListener != null)
			ServerCore.removeRuntimeLifecycleListener(runtimeListener);
		
		if (managedForm != null) {
			managedForm.dispose();
			managedForm = null;
		}
	}

	/* (non-Javadoc)
	 * Initializes the editor part with a site and input.
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		addChangeListener();
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (serverName == null)
			return;
		updating = true;
		
		if (server != null) {
			serverName.setText(server.getName());
			if (readOnly)
				serverName.setEditable(false);
			else
				serverName.setEditable(true);
			
			hostname.setText(server.getHost());
			if (readOnly)
				hostname.setEditable(false);
			else
				hostname.setEditable(true);
			
			if (runtimeCombo != null) {
				updateRuntimeCombo();
				IRuntime runtime = server.getRuntime();
				int size2 = runtimes.length;
				for (int i = 0; i < size2; i++) {
					if (runtimes[i].equals(runtime))
						runtimeCombo.select(i);
				}
				if (readOnly)
					runtimeCombo.setEnabled(false);
				else
					runtimeCombo.setEnabled(true);
			}
			
			if (serverConfiguration != null) {
				IFolder folder = server.getServerConfiguration();
				if (folder == null)
					serverConfiguration.setText(Messages.elementUnknownName);
				else
					serverConfiguration.setText("" + server.getServerConfiguration().getFullPath());
				if (readOnly) {
					serverConfiguration.setEditable(false);
					browse.setEnabled(false);
				} else {
					serverConfiguration.setEditable(true);
					browse.setEnabled(true);
				}
			}
			
			Server svr = (Server) server;
			int publishSetting = svr.getAutoPublishSetting();
			autoPublishDisable.setSelection(publishSetting == Server.AUTO_PUBLISH_DISABLE);
			autoPublishEnable.setSelection(publishSetting == Server.AUTO_PUBLISH_ENABLE);
			autoPublishTime.setSelection(svr.getAutoPublishTime());
			
			if (readOnly) {
				autoPublishDisable.setEnabled(false);
				autoPublishEnable.setEnabled(false);
				autoPublishTime.setEnabled(false);
			} else {
				autoPublishDisable.setEnabled(true);
				autoPublishEnable.setEnabled(true);
				autoPublishTime.setEnabled(publishSetting == Server.AUTO_PUBLISH_ENABLE);
			}
			
			List<ServerEditorOverviewPageModifier> pageModifiersLst = ServerUIPlugin.getServerEditorOverviewPageModifiers();
			for (ServerEditorOverviewPageModifier curPageModifier : pageModifiersLst) {
				if(server != null && server.getServerType() != null)
					curPageModifier.handlePropertyChanged(new PropertyChangeEvent(this, AbstractUIControl.PROP_SERVER_TYPE, oldServerType, server.getServerType()));
			}
		}
		
		updating = false;
		validate();
	}

	protected void validate() {
		IManagedForm mForm = getManagedForm();
		if (mForm == null)
			return;
		
		mForm.getMessageManager().removeMessage("name", serverName);
		if (server != null && serverName != null) {
			if (ServerPlugin.isNameInUse(server, serverName.getText().trim()))
				mForm.getMessageManager().addMessage("name", Messages.errorDuplicateName, null, IMessageProvider.ERROR, serverName);
		}
		
		if (serverConfiguration != null) {
			mForm.getMessageManager().removeMessage("config", serverConfiguration);
			if (server != null && server.getServerType() != null && server.getServerType().hasServerConfiguration()) {
				IFolder folder = getServer().getServerConfiguration();
				
	 			if (folder == null || !folder.exists()) {
					IProject project = null;
					if (folder != null)
						project = folder.getProject();
					if (project != null && project.exists() && !project.isOpen())
						mForm.getMessageManager().addMessage("config", NLS.bind(Messages.errorConfigurationNotAccessible, project.getName()), null, IMessageProvider.ERROR, serverConfiguration);
					else
						mForm.getMessageManager().addMessage("config", Messages.errorMissingConfiguration, null, IMessageProvider.ERROR, serverConfiguration);
	 			}
			}
		}
		
		mForm.getMessageManager().update();
	}

	protected void updateDecoration(ControlDecoration decoration, IStatus status) {
		if (status != null) {
			Image newImage = null;
			FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
			switch (status.getSeverity()) {
				case IStatus.INFO:
					//newImage = registry.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
					newImage = registry.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED).getImage();
					break;
				case IStatus.WARNING:
					newImage = registry.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
					break;
				case IStatus.ERROR:
					newImage = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
			}
			decoration.setDescriptionText(status.getMessage());
			decoration.setImage(newImage);
			decoration.show();
		} else {
			decoration.setDescriptionText(null);
			decoration.hide();
		}
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (serverName != null)
			serverName.setFocus();
		else if (serverConfiguration != null)
			serverConfiguration.setFocus();
	}

	public String getControlStringValue(String controlId) {
		if (controlId != null && AbstractUIControl.PROP_HOSTNAME.equals(controlId))
			return hostname.getText();
		return null;
	}

	public void handleUIControlMapChanged(Map<String, UIControlEntry> controlMap) {
		if (controlMap == null)
			return;
		
		for (String curControlId : controlMap.keySet()) {
			if (AbstractUIControl.PROP_HOSTNAME.equals(curControlId)) {
				UIControlEntry curControlEntry = controlMap.get(curControlId);
				if (hostnameLabel != null)
					hostnameLabel.setEnabled(curControlEntry.isEnabled());
				
				if (hostname != null){
					if (curControlEntry.getNewTextValue() != null)
						hostname.setText(curControlEntry.getNewTextValue());
					
					hostname.setEnabled(curControlEntry.isEnabled());
				}
				
				if (hostnameDecoration != null){
					if(curControlEntry.isEnabled())
						hostnameDecoration.show();
					else
						hostnameDecoration.hide();
				}
			}
		}
	}
	
	protected void fireServerWorkingCopyChanged() {
		List<ServerEditorOverviewPageModifier> pageModifiersLst = ServerUIPlugin.getServerEditorOverviewPageModifiers();
		for (ServerEditorOverviewPageModifier curPageModifier : pageModifiersLst) {
			curPageModifier.setServerWorkingCopy(getServer());
		}
	}
}