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
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.WebModule;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.ServerUICore;
/**
 * Dialog to add or modify web modules.
 */
public class WebModuleDialog extends Dialog {
	protected WebModule module;
	protected boolean isEdit;
	protected boolean isProject;
	protected Text docBase;
	protected IServerAttributes server2;
	protected ITomcatServer server;
	protected ITomcatConfiguration config;

	protected Table projTable;

	/**
	 * WebModuleDialog constructor comment.
	 * 
	 * @param parentShell a shell
	 * @param server2 a server
	 * @param server a Tomcat server
	 * @param config a Tomcat server configuration
	 * @param module a module
	 */
	public WebModuleDialog(Shell parentShell, IServerAttributes server2, ITomcatServer server, ITomcatConfiguration config, WebModule module) {
		super(parentShell);
		this.module = module;
		this.server2 = server2;
		this.server = server;
		this.config = config;
		isEdit = true;
	}

	/**
	 * WebModuleDialog constructor comment.
	 * 
	 * @param parentShell a shell
	 * @param server2 a server
	 * @param server a Tomcat server
	 * @param config a Tomcat server configuration
	 * @param isProject true if it is a project
	 */
	public WebModuleDialog(Shell parentShell, IServerAttributes server2, ITomcatServer server, ITomcatConfiguration config, boolean isProject) {
		this(parentShell, server2, server, config, new WebModule("/", "", null, true));
		isEdit = false;
		this.isProject = isProject;
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (isEdit)
			newShell.setText(Messages.configurationEditorWebModuleDialogTitleEdit);
		else
			newShell.setText(Messages.configurationEditorWebModuleDialogTitleAdd);
	}

	/**
	 * Creates and returns the contents of the upper part 
	 * of this dialog (above the button bar).
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method
	 * creates and returns a new <code>Composite</code> with
	 * standard margins and spacing. Subclasses should override.
	 * </p>
	 *
	 * @param parent the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG);
		// add project field if we are adding a project
		if (!isEdit && isProject) {
			Label l = new Label(composite, SWT.NONE);
			l.setText(Messages.configurationEditorWebModuleDialogProjects);
			GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			l.setLayoutData(data);
			
			projTable = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
			data = new GridData();
			data.widthHint = 150;
			data.heightHint = 75;
			projTable.setLayoutData(data);
			whs.setHelp(projTable, ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PROJECT);
	
			// fill table with web module projects
			IModule[] modules = ServerUtil.getModules(server2.getServerType().getRuntimeType().getModuleTypes());
			if (modules != null) {
				int size = modules.length;
				for (int i = 0; i < size; i++) {
					IModule module3 = modules[i];
					IWebModule module2 = (IWebModule) module3.loadAdapter(IWebModule.class, null);
					if (module2 != null) {
						IStatus status = server2.canModifyModules(new IModule[] { module3 }, null, null);
						if (status != null && status.isOK()) {
							TableItem item = new TableItem(projTable, SWT.NONE);
							item.setText(0, ServerUICore.getLabelProvider().getText(module3));
							item.setImage(0, ServerUICore.getLabelProvider().getImage(module3));
							item.setData(module2);
						}
					}
				}
			}
			new Label(composite, SWT.NONE).setText(" ");
		}
	
		new Label(composite, SWT.NONE).setText(Messages.configurationEditorWebModuleDialogDocumentBase);
		docBase = new Text(composite, SWT.BORDER);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		docBase.setLayoutData(data);
		docBase.setText(module.getDocumentBase());
		whs.setHelp(docBase, ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_DOCBASE);
	
		// disable document base for project modules
		if (isProject || (module.getMemento() != null && module.getMemento().length() > 0))
			docBase.setEditable(false);
		else {
			docBase.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					module = new WebModule(module.getPath(), docBase.getText(), module.getMemento(), module.isReloadable());
					validate();
				}
			});
		}
	
		if (isEdit || isProject)
			new Label(composite, SWT.NONE).setText(" ");
		else {
			Button browse = new Button(composite, SWT.NONE);
			browse.setText(Messages.browse);
			browse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					try {
						DirectoryDialog dialog = new DirectoryDialog(getShell());
						dialog.setMessage(Messages.configurationEditorWebModuleDialogSelectDirectory);
						String selectedDirectory = dialog.open();
						if (selectedDirectory != null)
							docBase.setText(selectedDirectory);
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Error browsing", e);
					}
				}
			});
		}
	
		// path (context-root)
		new Label(composite, SWT.NONE).setText(Messages.configurationEditorWebModuleDialogPath);
		final Text path = new Text(composite, SWT.BORDER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 150;
		path.setLayoutData(data);
		path.setText(module.getPath());
		path.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				module = new WebModule(path.getText(), module.getDocumentBase(), module.getMemento(), module.isReloadable());
			}
		});
		whs.setHelp(path, ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PATH);
	
		new Label(composite, SWT.NONE).setText("");
		
		// auto reload
		new Label(composite, SWT.NONE).setText("");
		final Button reloadable = new Button(composite, SWT.CHECK);
		reloadable.setText(Messages.configurationEditorWebModuleDialogReloadEnabled);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		reloadable.setLayoutData(data);
		reloadable.setSelection(module.isReloadable());
		reloadable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				module = new WebModule(module.getPath(), module.getDocumentBase(), module.getMemento(), reloadable.getSelection());
			}
		});
		whs.setHelp(reloadable, ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_RELOAD);
	
		if (!isEdit && isProject) {
			projTable.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						IWebModule module2 = (IWebModule) projTable.getSelection()[0].getData();
						String contextRoot = module2.getContextRoot();
						if (contextRoot != null && !contextRoot.startsWith("/") && contextRoot.length() > 0)
							contextRoot = "/" + contextRoot;
						module = new WebModule(contextRoot, "[workspace]", module.getMemento(), module.isReloadable());
						docBase.setText("[workspace]");
					} catch (Exception e) {
						// ignore
					}
					validate();
				}
			});
			new Label(composite, SWT.NONE).setText("");
		}
	
		Dialog.applyDialogFont(composite);
		return composite;
	}
	
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		validate();

		return control;
	}

	protected void validate() {
		boolean ok = true;
		if (module.getDocumentBase() == null || module.getDocumentBase().length() < 1)
			ok = false;
		
		getButton(IDialogConstants.OK_ID).setEnabled(ok);
	}

	/**
	 * Return the mime mapping.
	 *
	 * @return org.eclipse.jst.server.tomcat.WebModule
	 */
	public WebModule getWebModule() {
		return module;
	}
}