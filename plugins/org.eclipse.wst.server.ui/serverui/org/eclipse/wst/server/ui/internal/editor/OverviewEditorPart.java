/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.util.SocketUtil;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.command.*;
/**
 * Server general editor page.
 */
public class OverviewEditorPart extends ServerEditorPart {
	protected Text serverName;
	protected Label serverConfigurationName;
	protected Text hostname;
	protected Combo runtimeCombo;
	protected Button autoPublishDefault;
	protected Button autoPublishOverride;
	protected Spinner autoPublishTime;

	protected boolean updating;

	protected IRuntime[] runtimes;

	protected PropertyChangeListener listener;

	/**
	 * OverviewEditorPart constructor comment.
	 */
	protected OverviewEditorPart() {
		super();
	}

	protected ICommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * 
	 */
	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
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
					int size = runtimes.length;
					for (int i = 0; i < size; i++) {
						if (runtimes[i].equals(runtime))
							runtimeCombo.select(i);
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
	public final void createPartControl(Composite parent) {
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText(ServerUIPlugin.getResource("%serverEditorOverviewPageTitle"));
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
		
		Section section = toolkit.createSection(leftColumnComp, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(ServerUIPlugin.getResource("%serverEditorOverviewSection"));
		section.setDescription(ServerUIPlugin.getResource("%serverEditorOverviewDescription"));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.EDITOR_OVERVIEW_PAGE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		// server name
		if (server != null) {
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerName"));
			
			serverName = toolkit.createText(composite, server.getName());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			serverName.setLayoutData(data);
			serverName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					getCommandManager().executeCommand(new SetServerNameCommand(getServer(), serverName.getText()));
					updating = false;
				}
			});
		}
		
		// hostname
		if (server != null) {
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerHostname"));
			
			hostname = toolkit.createText(composite, server.getHost());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			hostname.setLayoutData(data);
			hostname.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					getCommandManager().executeCommand(new SetServerHostnameCommand(getServer(), hostname.getText()));
					updating = false;
				}
			});
		}
		
		// runtime
		if (server != null && server.getServerType() != null && server.getServerType().hasRuntime()) {
			IRuntime runtime = server.getRuntime();
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewRuntime"));
			
			IRuntimeType runtimeType = server.getServerType().getRuntimeType();
			runtimes = ServerUIPlugin.getRuntimes(runtimeType);
			
			if (runtimes == null || runtimes.length == 0)
				toolkit.createLabel(composite, "");
			else if (runtimes.length == 1)
				toolkit.createLabel(composite, runtime.getName());
			else {
				runtimeCombo = new Combo(composite, SWT.READ_ONLY);
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
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
							getCommandManager().executeCommand(new SetServerRuntimeCommand(getServer(), newRuntime));
							updating = false;
						} catch (Exception ex) {
							// ignore
						}
					}
					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
			}
		}
		
		// server configuration path
		if (server != null && server.getServerType().hasServerConfiguration()) {
			createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerConfigurationPath"));
			
			IFolder folder = server.getServerConfiguration();
			if (folder == null)
				serverConfigurationName = toolkit.createLabel(composite, ServerUIPlugin.getResource("%elementUnknownName"));
			else
				serverConfigurationName = toolkit.createLabel(composite, "" + server.getServerConfiguration().getFullPath());
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			serverConfigurationName.setLayoutData(data);
		}
		
		// auto-publish
		if (server != null) {
			//Label label = createLabel(toolkit, composite, ServerUIPlugin.getResource("%serverEditorOverviewServerHostname"));
			//label.
			
			autoPublishDefault = toolkit.createButton(composite, ServerUIPlugin.getResource("%serverEditorOverviewAutoPublishDefault"), SWT.RADIO);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			autoPublishDefault.setLayoutData(data);
			Server svr = (Server) server;
			autoPublishDefault.setSelection(svr.getAutoPublishDefault());
			
			autoPublishOverride = toolkit.createButton(composite, ServerUIPlugin.getResource("%serverEditorOverviewAutoPublishOverride"), SWT.RADIO);
			autoPublishOverride.setSelection(!svr.getAutoPublishDefault());
			
			autoPublishOverride.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (updating)
						return;
					updating = true;
					getCommandManager().executeCommand(new SetServerAutoPublishDefaultCommand(getServer(), autoPublishDefault.getSelection()));
					updating = false;
					autoPublishTime.setEnabled(autoPublishOverride.getSelection());
				}
			});
			
			//autoPublishTime = toolkit.createText(composite, svr.getAutoPublishTime() + "");
			autoPublishTime = new Spinner(composite, SWT.BORDER);
			autoPublishTime.setMinimum(0);
			autoPublishTime.setMaximum(120);
			autoPublishTime.setSelection(svr.getAutoPublishTime());
			data = new GridData(GridData.FILL_HORIZONTAL);
			autoPublishTime.setLayoutData(data);
			autoPublishTime.setEnabled(autoPublishOverride.getSelection());
			autoPublishTime.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if (updating)
						return;
					updating = true;
					try {
						getCommandManager().executeCommand(new SetServerAutoPublishTimeCommand(getServer(), autoPublishTime.getSelection()));
					} catch (Exception ex) {
						// ignore
					}
					updating = false;
				}
			});
		}
		
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
		
		insertSections(rightColumnComp, "org.eclipse.wst.server.editor.overview.right");
		
		form.setContent(columnComp);
		form.reflow(true);

		initialize();
	}
	
	protected void updateRuntimeCombo() {
		IRuntimeType runtimeType = server.getServerType().getRuntimeType();
		runtimes = ServerUIPlugin.getRuntimes(runtimeType);
		
		if (SocketUtil.isLocalhost(server.getHost()) && runtimes != null) {
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
		
		int size = runtimes.length;
		String[] items = new String[size];
		for (int i = 0; i < size; i++)
			items[i] = runtimes[i].getName();
		
		runtimeCombo.setItems(items);
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		return label;
	}
	
	public void dispose() {
		super.dispose();
		
		if (server != null)
			server.removePropertyChangeListener(listener);
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
		}
		
		updating = false;
	}
	
	//protected void validate() { }

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (serverName != null)
			serverName.setFocus();
		else if (serverConfigurationName != null)
			serverConfigurationName.setFocus();
	}
}