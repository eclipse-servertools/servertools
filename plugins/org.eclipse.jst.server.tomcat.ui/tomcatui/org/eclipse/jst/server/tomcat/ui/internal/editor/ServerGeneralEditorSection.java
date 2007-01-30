/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.command.SetDebugModeCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetDeployDirectoryCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetSecureCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetTestEnvironmentCommand;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
/**
 * Tomcat server general editor page.
 */
public class ServerGeneralEditorSection extends ServerEditorSection {
	protected TomcatServer tomcatServer;

	protected Button secure;
	protected Button debug;
	protected Button testEnvironment;
	protected Text deployDir;
	protected Button deployDirBrowse;
	protected boolean updating;

	protected PropertyChangeListener listener;
	protected IPublishListener publishListener;
	
	boolean allowRestrictedEditing;

	/**
	 * ServerGeneralEditorPart constructor comment.
	 */
	public ServerGeneralEditorSection() {
		// do nothing
	}

	/**
	 * 
	 */
	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (updating)
					return;
				updating = true;
				if (TomcatServer.PROPERTY_SECURE.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.secure.setSelection(b.booleanValue());
				} else if (TomcatServer.PROPERTY_DEBUG.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.debug.setSelection(b.booleanValue());
				} else if (ITomcatServer.PROPERTY_TEST_ENVIRONMENT.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.testEnvironment.setSelection(b.booleanValue());
				} else if (ITomcatServer.PROPERTY_DEPLOYDIR.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					ServerGeneralEditorSection.this.deployDir.setText(s);
					validate();
				}
				updating = false;
			}
		};
		server.addPropertyChangeListener(listener);
		
		publishListener = new PublishAdapter() {
			public void publishFinished(IServer server2, IStatus status) {
				boolean flag = false;
				if (status.isOK() && server2.getModules().length == 0)
					flag = true;
				if (flag != allowRestrictedEditing) {
					allowRestrictedEditing = flag;
					// Update the state of the fields
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (!ServerGeneralEditorSection.this.deployDir.isDisposed())
								ServerGeneralEditorSection.this.deployDir.setEnabled(allowRestrictedEditing);
							if (!ServerGeneralEditorSection.this.deployDirBrowse.isDisposed())
								ServerGeneralEditorSection.this.deployDirBrowse.setEnabled(allowRestrictedEditing);
						}
					});
				}
			}
		};
		server.getOriginal().addPublishListener(publishListener);
	}
	
	/**
	 * Creates the SWT controls for this workbench part.
	 *
	 * @param parent the parent control
	 */
	public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
			| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(Messages.serverEditorGeneralSection);
		section.setDescription(Messages.serverEditorGeneralDescription);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(composite, ContextIds.SERVER_EDITOR);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		// deployment directory
		Label label = toolkit.createLabel(composite, Messages.serverEditorDeployDir);
		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);
		
		deployDir = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		deployDir.setLayoutData(data);
		deployDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new SetDeployDirectoryCommand(tomcatServer, deployDir.getText()));
				updating = false;
				validate();
			}
		});
		
		deployDirBrowse = toolkit.createButton(composite, Messages.configurationEditorBrowseDeploy, SWT.PUSH);
		deployDirBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(deployDir.getShell());
				dialog.setMessage(Messages.configurationEditorBrowseDeployMessage);
				dialog.setFilterPath(deployDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null && !selectedDirectory.equals(deployDir.getText())) {
					updating = true;
					execute(new SetDeployDirectoryCommand(tomcatServer, selectedDirectory));
					deployDir.setText(selectedDirectory);
					updating = false;
					validate();
				}
			}
		});
		deployDirBrowse.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		label = toolkit.createLabel(composite, Messages.serverEditorSpecialFieldsLabel);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		label.setLayoutData(data);

		Label separator = toolkit.createSeparator(composite, SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		separator.setLayoutData(data);
		
		// test environment
		testEnvironment = toolkit.createButton(composite, Messages.serverEditorTestEnvironment, SWT.CHECK);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		testEnvironment.setLayoutData(data);
		testEnvironment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, testEnvironment.getSelection()));
				updating = false;
			}
		});
		whs.setHelp(testEnvironment, ContextIds.SERVER_EDITOR_TEST_ENVIRONMENT);

		// security
		secure = toolkit.createButton(composite, Messages.serverEditorSecure, SWT.CHECK);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		secure.setLayoutData(data);
		secure.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				execute(new SetSecureCommand(tomcatServer, secure.getSelection()));
				updating = false;
			}
		});
		whs.setHelp(secure, ContextIds.SERVER_EDITOR_SECURE);
	
		// debug mode
		debug = toolkit.createButton(composite, Messages.serverEditorDebugMode, SWT.CHECK);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		debug.setLayoutData(data);
		debug.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				execute(new SetDebugModeCommand(tomcatServer, debug.getSelection()));
				updating = false;
			}
		});
		whs.setHelp(debug, ContextIds.SERVER_EDITOR_DEBUG_MODE);
	
		initialize();
	}
	
	/**
	 * @see ServerEditorSection#dispose()
	 */
	public void dispose() {
		if (server != null) {
			server.removePropertyChangeListener(listener);
			if (server.getOriginal() != null)
				server.getOriginal().removePublishListener(publishListener);
		}
	}

	/**
	 * @see ServerEditorSection#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		if (server != null) {
			tomcatServer = (TomcatServer) server.loadAdapter(TomcatServer.class, null);
			addChangeListeners();
		}
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (secure == null || tomcatServer == null)
			return;
		updating = true;
		
		allowRestrictedEditing = false;
		if (server.getOriginal().getServerPublishState() == IServer.PUBLISH_STATE_NONE
				&& server.getOriginal().getModules().length == 0) {
			allowRestrictedEditing = true;
		}
		
		deployDir.setText(tomcatServer.getDeployDirectory());
		deployDir.setEnabled(allowRestrictedEditing);
		
		deployDirBrowse.setEnabled(allowRestrictedEditing);

		testEnvironment.setSelection(tomcatServer.isTestEnvironment());
		secure.setSelection(tomcatServer.isSecure());
		if (server.getRuntime() != null && server.getRuntime().getRuntimeType().getId().indexOf("32") >= 0 || readOnly)
			debug.setEnabled(false);
		else {
			debug.setEnabled(true);
			debug.setSelection(tomcatServer.isDebug());
		}
		
		if (readOnly)
			secure.setEnabled(false);
		else
			secure.setEnabled(true);
		
		updating = false;
		validate();
	}
	
	/**
	 * @see ServerEditorSection#getSaveStatus()
	 */
	public IStatus[] getSaveStatus() {
		if (tomcatServer != null) {
			String dir = tomcatServer.getDeployDirectory();
			if (dir == null || dir.length() == 0) {
				return new IStatus [] {
						new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID, Messages.errorDeployDirNotSpecified)};
			}
		}
		// use default implementation to return success
		return super.getSaveStatus();
	}
	
	protected void validate() {
		if (tomcatServer != null) {
			String dir = tomcatServer.getDeployDirectory();
			if (dir == null || dir.length() == 0) {
				setErrorMessage(Messages.errorDeployDirNotSpecified);
				return;
			}
		}
		setErrorMessage(null);
	}
}