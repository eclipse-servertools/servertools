/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.resources.IFolder;
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
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.ServerType;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.command.*;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * Server general editor page.
 */
public class OverviewEditorPart extends ServerEditorPart {
	protected Text serverName;
	protected Text serverConfiguration;
	protected Text hostname;
	protected Combo runtimeCombo;
	protected Button browse;
	protected Button autoPublishDefault;
	protected Button autoPublishDisable;
	protected Button autoPublishOverride;
	protected Spinner autoPublishTime;

	protected boolean updating;

	protected IRuntime[] runtimes;

	protected PropertyChangeListener listener;

	protected IRuntimeLifecycleListener runtimeListener;

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
					validate();
				} else if (event.getPropertyName().equals(Server.PROP_AUTO_PUBLISH_SETTING)) {
					Integer autoPublishSetting = (Integer)event.getNewValue();
					int setting = autoPublishSetting.intValue();
					autoPublishDefault.setSelection(setting == Server.AUTO_PUBLISH_DEFAULT);
					autoPublishOverride.setSelection(setting == Server.AUTO_PUBLISH_OVERRIDE);
					autoPublishDisable.setSelection(setting == Server.AUTO_PUBLISH_DISABLE);
					autoPublishTime.setEnabled(setting == Server.AUTO_PUBLISH_OVERRIDE);
					validate();
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
		IManagedForm mForm = new ManagedForm(parent);
		setManagedForm(mForm);
		ScrolledForm form = mForm.getForm();
		FormToolkit toolkit = mForm.getToolkit();
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
		
		createAutoPublishSection(rightColumnComp, toolkit);
		
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
			createLabel(toolkit, composite, Messages.serverEditorOverviewServerHostname);
			
			hostname = toolkit.createText(composite, server.getHost());
			final ControlDecoration hostnameDecoration = new ControlDecoration(hostname, SWT.TOP | SWT.LEAD);
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
			final IRuntime runtime = server.getRuntime();
			Hyperlink link = toolkit.createHyperlink(composite, Messages.serverEditorOverviewRuntime, SWT.NONE);
			link.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					editRuntime(runtime);
				}
			});
			
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
	}

	protected void createAutoPublishSection(Composite rightColumnComp, FormToolkit toolkit) {
		Section section = toolkit.createSection(rightColumnComp, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(Messages.serverEditorOverviewAutoPublishSection);
		section.setDescription(Messages.serverEditorOverviewAutoPublishDescription);
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
			autoPublishDefault = toolkit.createButton(composite, Messages.serverEditorOverviewAutoPublishDefault, SWT.RADIO);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			autoPublishDefault.setLayoutData(data);
			autoPublishDefault.setSelection(publishSetting == Server.AUTO_PUBLISH_DEFAULT);
			whs.setHelp(autoPublishDefault, ContextIds.EDITOR_AUTOPUBLISH_DEFAULT);
			
			Hyperlink editDefaults = toolkit.createHyperlink(composite, Messages.serverEditorOverviewAutoPublishDefaultEdit, SWT.NONE);
			editDefaults.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					showPreferencePage();
				}
			});
			editDefaults.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
			whs.setHelp(editDefaults, ContextIds.EDITOR_AUTOPUBLISH_DEFAULT);
			
			autoPublishDisable = toolkit.createButton(composite, Messages.serverEditorOverviewAutoPublishDisable, SWT.RADIO);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			autoPublishDisable.setLayoutData(data);
			autoPublishDisable.setSelection(publishSetting == Server.AUTO_PUBLISH_DISABLE);
			whs.setHelp(autoPublishDisable, ContextIds.EDITOR_AUTOPUBLISH_DISABLE);
			
			autoPublishOverride = toolkit.createButton(composite, Messages.serverEditorOverviewAutoPublishOverride, SWT.RADIO);
			autoPublishOverride.setSelection(publishSetting == Server.AUTO_PUBLISH_OVERRIDE);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			autoPublishOverride.setLayoutData(data);
			whs.setHelp(autoPublishOverride, ContextIds.EDITOR_AUTOPUBLISH_OVERRIDE);
			
			final Label autoPublishTimeLabel = toolkit.createLabel(composite, Messages.serverEditorOverviewAutoPublishOverrideInterval);
			data = new GridData();
			data.horizontalIndent = 20;
			autoPublishTimeLabel.setLayoutData(data);
			autoPublishTimeLabel.setEnabled(autoPublishOverride.getSelection());
			
			autoPublishTime = new Spinner(composite, SWT.BORDER);
			autoPublishTime.setMinimum(0);
			autoPublishTime.setMaximum(120);
			autoPublishTime.setSelection(svr.getAutoPublishTime());
			data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			data.widthHint = 60;
			autoPublishTime.setLayoutData(data);
			autoPublishTime.setEnabled(autoPublishOverride.getSelection());
			whs.setHelp(autoPublishTime, ContextIds.EDITOR_AUTOPUBLISH_OVERRIDE);
			
			autoPublishOverride.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (updating || !autoPublishOverride.getSelection())
						return;
					updating = true;
					execute(new SetServerAutoPublishDefaultCommand(getServer(), Server.AUTO_PUBLISH_OVERRIDE));
					updating = false;
					autoPublishTimeLabel.setEnabled(autoPublishOverride.getSelection());
					autoPublishTime.setEnabled(autoPublishOverride.getSelection());
					validate();
				}
			});
			
			autoPublishDefault.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (updating || !autoPublishDefault.getSelection())
						return;
					updating = true;
					execute(new SetServerAutoPublishDefaultCommand(getServer(), Server.AUTO_PUBLISH_DEFAULT));
					updating = false;
					autoPublishTimeLabel.setEnabled(autoPublishOverride.getSelection());
					autoPublishTime.setEnabled(autoPublishOverride.getSelection());
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
					autoPublishTimeLabel.setEnabled(autoPublishOverride.getSelection());
					autoPublishTime.setEnabled(autoPublishOverride.getSelection());
					validate();
				}
			});
			
			autoPublishTime.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					try {
						execute(new SetServerAutoPublishTimeCommand(getServer(), autoPublishTime.getSelection()));
					} catch (Exception ex) {
						// ignore
					}
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

	protected boolean showPreferencePage() {
		String id = "org.eclipse.wst.server.ui.preferencePage";
		final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getSite().getShell(), id, new String[] { id }, null);
		return (dialog.open() == Window.OK);
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
		ClosableWizardDialog dialog = new ClosableWizardDialog(getEditorSite().getShell(), wizard);
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
			autoPublishDefault.setSelection(publishSetting == Server.AUTO_PUBLISH_DEFAULT);
			autoPublishDisable.setSelection(publishSetting == Server.AUTO_PUBLISH_DISABLE);
			autoPublishOverride.setSelection(publishSetting == Server.AUTO_PUBLISH_OVERRIDE);
			autoPublishTime.setSelection(svr.getAutoPublishTime());
			
			if (readOnly) {
				autoPublishDefault.setEnabled(false);
				autoPublishDisable.setEnabled(false);
				autoPublishOverride.setEnabled(false);
				autoPublishTime.setEnabled(false);
			} else {
				autoPublishDefault.setEnabled(true);
				autoPublishDisable.setEnabled(true);
				autoPublishOverride.setEnabled(true);
				autoPublishTime.setEnabled(true);
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
				mForm.getMessageManager().addMessage("name", Messages.errorDuplicateName, null, IMessageProvider.WARNING, serverName);
		}
		
		if (serverConfiguration != null) {
			mForm.getMessageManager().removeMessage("config", serverConfiguration);
			if (server != null && server.getServerType() != null && server.getServerType().hasServerConfiguration()) {
				IFolder folder = getServer().getServerConfiguration();
				if (folder == null || !folder.exists())
					mForm.getMessageManager().addMessage("config", Messages.errorMissingConfiguration, null, IMessageProvider.WARNING, serverConfiguration);
			}
		}
		
		mForm.getMessageManager().removeMessage("auto-publish", autoPublishTime);
		if (autoPublishTime != null && autoPublishTime.isEnabled() && autoPublishOverride.getSelection()) {
			int i = autoPublishTime.getSelection();
			if (i < 1)
				mForm.getMessageManager().addMessage("auto-publish", Messages.serverEditorOverviewAutoPublishInvalid, null, IMessageProvider.WARNING, autoPublishTime);
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
}