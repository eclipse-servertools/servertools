/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jst.server.tomcat.core.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.command.SetDebugModeCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetSecureCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetTestEnvironmentCommand;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.wst.server.ui.editor.*;
/**
 * Tomcat server general editor page.
 */
public class ServerGeneralEditorSection extends ServerEditorSection {
	protected ITomcatServerWorkingCopy tomcatServer;

	protected Button secure;
	protected Button debug;
	protected Button testEnvironment;
	protected boolean updating;

	protected PropertyChangeListener listener;

	/**
	 * ServerGeneralEditorPart constructor comment.
	 */
	protected ServerGeneralEditorSection() {
		// do nothing
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
				if (ITomcatServer.PROPERTY_SECURE.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.secure.setSelection(b.booleanValue());
				} else if (ITomcatServer.PROPERTY_DEBUG.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.debug.setSelection(b.booleanValue());
				} else if (ITomcatServer.PROPERTY_TEST_ENVIRONMENT.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.testEnvironment.setSelection(b.booleanValue());
				}
				updating = false;
			}
		};
		server.addPropertyChangeListener(listener);
	}
	
	/**
	 * Creates the SWT controls for this workbench part.
	 *
	 * @param parent the parent control
	 */
	public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR|Section.DESCRIPTION|ExpandableComposite.FOCUS_TITLE);
		section.setText(TomcatUIPlugin.getResource("%serverEditorGeneralSection"));
		section.setDescription(TomcatUIPlugin.getResource("%serverEditorGeneralDescription"));
		section.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		WorkbenchHelp.setHelp(composite, ContextIds.SERVER_EDITOR);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		// test environment
		testEnvironment = toolkit.createButton(composite, TomcatUIPlugin.getResource("%serverEditorTestEnvironment"), SWT.CHECK);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		testEnvironment.setLayoutData(data);
		testEnvironment.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				commandManager.executeCommand(new SetTestEnvironmentCommand(tomcatServer, testEnvironment.getSelection()));
				updating = false;
			}
		});
		WorkbenchHelp.setHelp(testEnvironment, ContextIds.SERVER_EDITOR_TEST_ENVIRONMENT);

		// security
		secure = toolkit.createButton(composite, TomcatUIPlugin.getResource("%serverEditorSecure"), SWT.CHECK);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		secure.setLayoutData(data);
		secure.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				commandManager.executeCommand(new SetSecureCommand(tomcatServer, secure.getSelection()));
				updating = false;
			}
		});
		WorkbenchHelp.setHelp(secure, ContextIds.SERVER_EDITOR_SECURE);
	
		// debug mode
		debug = toolkit.createButton(composite, TomcatUIPlugin.getResource("%serverEditorDebugMode"), SWT.CHECK);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		debug.setLayoutData(data);
		debug.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				commandManager.executeCommand(new SetDebugModeCommand(tomcatServer, debug.getSelection()));
				updating = false;
			}
		});
		WorkbenchHelp.setHelp(debug, ContextIds.SERVER_EDITOR_DEBUG_MODE);
	
		initialize();
	}

	public void dispose() {
		if (server != null)
			server.removePropertyChangeListener(listener);
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		if (server != null) {
			tomcatServer = (ITomcatServerWorkingCopy) server.getAdapter(ITomcatServerWorkingCopy.class);
			addChangeListener();
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
	}
}