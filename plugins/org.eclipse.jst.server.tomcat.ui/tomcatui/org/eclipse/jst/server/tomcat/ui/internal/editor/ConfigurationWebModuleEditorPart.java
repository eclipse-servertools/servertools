/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
import org.eclipse.jst.server.tomcat.core.internal.command.AddModuleCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.AddWebModuleCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.ModifyWebModuleCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.RemoveModuleCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.RemoveWebModuleCommand;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;
/**
 * Tomcat configuration web module editor page.
 */
public class ConfigurationWebModuleEditorPart extends ServerEditorPart {
	protected ITomcatServerWorkingCopy server2;
	protected TomcatConfiguration configuration;

	protected Table webAppTable;
	protected int selection = -1;
	protected Button addProject;
	protected Button addExtProject;
	protected Button remove;
	protected Button edit;

	protected PropertyChangeListener listener;

	/**
	 * ConfigurationWebModuleEditorPart constructor comment.
	 */
	public ConfigurationWebModuleEditorPart() {
		super();
	}

	/**
	 * 
	 */
	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (TomcatConfiguration.MODIFY_WEB_MODULE_PROPERTY.equals(event.getPropertyName())) {
					initialize();
				} else if (TomcatConfiguration.ADD_WEB_MODULE_PROPERTY.equals(event.getPropertyName())) {
					initialize();
				} else if (TomcatConfiguration.REMOVE_WEB_MODULE_PROPERTY.equals(event.getPropertyName())) {
					initialize();
				}
			}
		};
		configuration.addPropertyChangeListener(listener);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		ScrolledForm form = toolkit.createScrolledForm(parent);
		toolkit.decorateFormHeading(form.getForm());
		form.setText(Messages.configurationEditorWebModulesPageTitle);
		form.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_WEB_MODULE));
		GridLayout layout = new GridLayout();
		layout.marginTop = 6;
		layout.marginLeft = 6;
		form.getBody().setLayout(layout);
		
		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.configurationEditorWebModulesSection);
		section.setDescription(Messages.configurationEditorWebModulesDescription);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = toolkit.createComposite(section);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.CONFIGURATION_EDITOR_WEBMODULES);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		webAppTable = toolkit.createTable(composite, SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		webAppTable.setHeaderVisible(true);
		webAppTable.setLinesVisible(true);
		whs.setHelp(webAppTable, ContextIds.CONFIGURATION_EDITOR_WEBMODULES_LIST);
		//toolkit.paintBordersFor(webAppTable);
		
		TableLayout tableLayout = new TableLayout();
	
		TableColumn col = new TableColumn(webAppTable, SWT.NONE);
		col.setText(Messages.configurationEditorPathColumn);
		ColumnWeightData colData = new ColumnWeightData(8, 85, true);
		tableLayout.addColumnData(colData);
	
		TableColumn col2 = new TableColumn(webAppTable, SWT.NONE);
		col2.setText(Messages.configurationEditorDocBaseColumn);
		colData = new ColumnWeightData(13, 135, true);
		tableLayout.addColumnData(colData);
	
		TableColumn col3 = new TableColumn(webAppTable, SWT.NONE);
		col3.setText(Messages.configurationEditorProjectColumn);
		colData = new ColumnWeightData(8, 85, true);
		tableLayout.addColumnData(colData);
	
		TableColumn col4 = new TableColumn(webAppTable, SWT.NONE);
		col4.setText(Messages.configurationEditorReloadColumn);
		colData = new ColumnWeightData(7, 75, true);
		tableLayout.addColumnData(colData);
		
		webAppTable.setLayout(tableLayout);
	
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 450;
		data.heightHint = 120;
		webAppTable.setLayoutData(data);
		webAppTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectWebApp();
			}
		});
	
		Composite rightPanel = toolkit.createComposite(composite);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		rightPanel.setLayout(layout);
		data = new GridData();
		rightPanel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		//toolkit.paintBordersFor(rightPanel);
	
		// buttons still to add:
		// add project, add external module, remove module
		addProject = toolkit.createButton(rightPanel, Messages.configurationEditorAddProjectModule, SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		addProject.setLayoutData(data);
		whs.setHelp(addProject, ContextIds.CONFIGURATION_EDITOR_WEBMODULES_ADD_PROJECT);
	
		// disable the add project module button if there are no
		// web projects in the workbench
		if (!canAddWebModule())
			addProject.setEnabled(false);
		else {
			addProject.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					WebModuleDialog dialog = new WebModuleDialog(getEditorSite().getShell(), getServer(), server2, configuration, true);
					dialog.open();
					if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
						execute(new AddModuleCommand(getServer(), dialog.module4));
					}
				}
			});
		}
	
		addExtProject = toolkit.createButton(rightPanel, Messages.configurationEditorAddExternalModule, SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		addExtProject.setLayoutData(data);
		addExtProject.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WebModuleDialog dialog = new WebModuleDialog(getEditorSite().getShell(), getServer(), server2, configuration, false);
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
					execute(new AddWebModuleCommand(configuration, dialog.getWebModule()));
				}
			}
		});
		whs.setHelp(addExtProject, ContextIds.CONFIGURATION_EDITOR_WEBMODULES_ADD_EXTERNAL);
		
		edit = toolkit.createButton(rightPanel, Messages.editorEdit, SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		edit.setLayoutData(data);
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (selection < 0)
					return;
				WebModule module = (WebModule) configuration.getWebModules().get(selection);
				WebModuleDialog dialog = new WebModuleDialog(getEditorSite().getShell(), getServer(), server2, configuration, module);
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
					execute(new ModifyWebModuleCommand(configuration, selection, dialog.getWebModule()));
				}
			}
		});
		whs.setHelp(edit, ContextIds.CONFIGURATION_EDITOR_WEBMODULES_EDIT);
	
		remove = toolkit.createButton(rightPanel, Messages.editorRemove, SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		remove.setLayoutData(data);
		remove.setEnabled(false);
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (selection < 0)
					return;
				TableItem item = webAppTable.getItem(selection);
				if (item.getData() != null) {
					IModule module = (IModule) item.getData();
					execute(new RemoveModuleCommand(getServer(), module));
				} else {
					execute(new RemoveWebModuleCommand(configuration, selection));
				}
				remove.setEnabled(false);
				edit.setEnabled(false);
				selection = -1;
			}
		});
		whs.setHelp(remove, ContextIds.CONFIGURATION_EDITOR_WEBMODULES_REMOVE);
		
		form.setContent(section);
		form.reflow(true);
		
		initialize();
	}

	protected boolean canAddWebModule() {
		IModule[] modules = ServerUtil.getModules(server.getServerType().getRuntimeType().getModuleTypes());
		if (modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				IWebModule webModule = (IWebModule) modules[i].loadAdapter(IWebModule.class, null);
				if (webModule != null) {
					IStatus status = server.canModifyModules(new IModule[] { modules[i] }, null, null);
					if (status != null && status.isOK())
						return true;
				}
			}
		}
		return false;
	}

	public void dispose() {
		super.dispose();
		
		if (configuration != null)
			configuration.removePropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * Initializes the editor part with a site and input.
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		TomcatServer ts = (TomcatServer) server.loadAdapter(TomcatServer.class, null);
		try {
			configuration = ts.getTomcatConfiguration();
		} catch (Exception e) {
			// ignore
		}
		if (configuration != null)
			addChangeListener();
		
		if (server != null)
			server2 = (ITomcatServerWorkingCopy) server.loadAdapter(ITomcatServerWorkingCopy.class, null);
		
		initialize();
	}

	/**
	 * 
	 */
	protected void initialize() {
		if (webAppTable == null)
			return;

		webAppTable.removeAll();
		setErrorMessage(null);
		
		ILabelProvider labelProvider = ServerUICore.getLabelProvider();
		List list = configuration.getWebModules();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			WebModule module = (WebModule) iterator.next();
			TableItem item = new TableItem(webAppTable, SWT.NONE);
			
			String memento = module.getMemento();
			String projectName = "";
			Image projectImage = null;
			if (memento != null && memento.length() > 0) {
				projectName = NLS.bind(Messages.configurationEditorProjectMissing, new String[] {memento});
				projectImage = TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_PROJECT_MISSING);
				IModule module2 = ServerUtil.getModule(memento);
				if (module2 != null) {
					projectName = labelProvider.getText(module2);
					projectImage = labelProvider.getImage(module2);
					item.setData(module2);
				}
			}
			
			String reload = module.isReloadable() ? Messages.configurationEditorReloadEnabled : Messages.configurationEditorReloadDisabled;
			String[] s = new String[] {module.getPath(), module.getDocumentBase(), projectName, reload};
			item.setText(s);
			item.setImage(0, TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_WEB_MODULE));
			if (projectImage != null)
				item.setImage(2, projectImage);
			
			if (!isDocumentBaseValid(module.getDocumentBase())) {
				item.setImage(1, TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_PROJECT_MISSING));
				setErrorMessage(NLS.bind(Messages.errorMissingWebModule, module.getDocumentBase()));
			}
		}
		labelProvider = null;
		
		if (readOnly) {
			addProject.setEnabled(false);
			addExtProject.setEnabled(false);
			edit.setEnabled(false);
			remove.setEnabled(false);
		} else {
			addProject.setEnabled(canAddWebModule());
			addExtProject.setEnabled(true);
		}
	}

	/**
	 * 
	 */
	protected void selectWebApp() {
		if (readOnly)
			return;

		try {
			selection = webAppTable.getSelectionIndex();
			remove.setEnabled(true);
			edit.setEnabled(true);
		} catch (Exception e) {
			selection = -1;
			remove.setEnabled(false);
			edit.setEnabled(false);
		}
	}
	
	protected boolean isDocumentBaseValid(String s) {
		if (s == null || s.length() < 2)
			return true;
		
		// check absolute path
		File f = new File(s);
		if (f.exists())
			return true;
		
		// check workspace
		try {
			if (ResourcesPlugin.getWorkspace().getRoot().getProject(s).exists())
				return true;
		} catch (Exception e) {
			// bad path
		}
		
		if (s.startsWith(configuration.getDocBasePrefix())) {
			try {
				String t = s.substring(configuration.getDocBasePrefix().length());
				if (ResourcesPlugin.getWorkspace().getRoot().getProject(t).exists())
					return true;
			} catch (Exception e) {
				// bad path
			}
		}
		
		// check server relative path
		try {
			f = server.getRuntime().getLocation().append(s).toFile();
			if (f.exists())
				return true;
		} catch (Exception e) {
			// bad path
		}
		
		return false;
	}
	
	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (webAppTable != null)
			webAppTable.setFocus();
	}
}
