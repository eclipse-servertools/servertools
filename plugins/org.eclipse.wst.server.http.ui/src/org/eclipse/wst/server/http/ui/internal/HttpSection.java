/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.ui.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.http.core.internal.HttpServer;
import org.eclipse.wst.server.http.core.internal.command.ModifyPortCommand;
import org.eclipse.wst.server.http.core.internal.command.ModifyPublishingCommand;
import org.eclipse.wst.server.http.core.internal.command.ModifyURLPrefixCommand;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

public class HttpSection extends ServerEditorSection {
	protected HttpServer httpServer;
	protected boolean updating;
	protected Text urlPrefixText;
	protected Button publishCheckBox;
	protected Spinner portSpinner;
	protected PropertyChangeListener listener;

	public HttpSection() {
		super();
	}

	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (updating)
					return;
				updating = true;
				if (HttpServer.PROPERTY_PORT.equals(event.getPropertyName())) {
					Integer i = (Integer) event.getNewValue();
					portSpinner.setSelection(i.intValue());
				} else if (HttpServer.PROPERTY_URL_PREFIX.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					if (s != null)
						urlPrefixText.setText(s);
				} else if (HttpServer.PROPERTY_IS_PUBLISHING.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					if (b != null)
						publishCheckBox.setSelection(b.booleanValue());
				}
				updating = false;
			}
		};
		server.addPropertyChangeListener(listener);
	}

	public void createSection(Composite parent) {
		super.createSection(parent);
		
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.editorSectionTitle);
		section.setDescription(Messages.editorSectionDescription);
		section.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 8;
		layout.marginWidth = 8;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		// URL prefix
		Label label = createLabel(toolkit, composite, Messages.editorURLPrefix);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		//data.horizontalSpan = 2;
		label.setLayoutData(data);

		urlPrefixText = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		//data.horizontalSpan = 2;
		urlPrefixText.setLayoutData(data);
		urlPrefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new ModifyURLPrefixCommand(httpServer, urlPrefixText.getText()));
				updating = false;
			}
		});
		
		// port
		createLabel(toolkit, composite, Messages.editorPort);
		
		portSpinner = new Spinner(composite, SWT.BORDER);
		portSpinner.setMinimum(0);
		portSpinner.setMinimum(999999);
		data = new GridData(GridData.FILL_HORIZONTAL);
		portSpinner.setLayoutData(data);
		portSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new ModifyPortCommand(httpServer, portSpinner.getSelection()));
				updating = false;
			}
		});
		
		// is publishing
		publishCheckBox = new Button(composite, SWT.CHECK);
		publishCheckBox.setText(Messages.editorShouldPublish);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		publishCheckBox.setLayoutData(data);
		publishCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				Button b = (Button) se.getSource();
				if (updating)
					return;
				updating = true;
				execute(new ModifyPublishingCommand(httpServer, b.getSelection()));
				updating = false;
			}
		});
		
		initialize();
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	public void dispose() {
		if (server != null)
			server.removePropertyChangeListener(listener);
	}

	/*
	 * (non-Javadoc) Initializes the editor part with a site and input.
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		httpServer = (HttpServer) server.loadAdapter(HttpServer.class, null);
		addChangeListener();
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (urlPrefixText == null)
			return;
		updating = true;
		
		String urlPrefix = httpServer.getURLPrefix();
		if (urlPrefix != null)
			urlPrefixText.setText(urlPrefix);
		urlPrefixText.setEnabled(!readOnly);
		
		portSpinner.setSelection(httpServer.getPort());
		portSpinner.setEnabled(!readOnly);
		
		publishCheckBox.setSelection(httpServer.isPublishing());
		publishCheckBox.setEnabled(!readOnly);
		updating = false;
	}
}