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
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.command.SetDebugModeCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetSecureCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetSaveSeparateContextFilesCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetServeModulesWithoutPublishCommand;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
/**
 * Tomcat server general editor page.
 */
public class ServerGeneralEditorSection extends ServerEditorSection {
	protected TomcatServer tomcatServer;

	protected Button secure;
	protected Button debug;
	protected Button noPublish;
	protected Button separateContextFiles;
	protected boolean updating;

	protected PropertyChangeListener listener;
	
	protected boolean noPublishChanged;
	protected boolean separateContextFilesChanged;

	/**
	 * ServerGeneralEditorPart constructor comment.
	 */
	public ServerGeneralEditorSection() {
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
				if (TomcatServer.PROPERTY_SECURE.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.secure.setSelection(b.booleanValue());
				} else if (TomcatServer.PROPERTY_DEBUG.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.debug.setSelection(b.booleanValue());
				} else if (ITomcatServer.PROPERTY_SERVE_MODULES_WITHOUT_PUBLISH.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.noPublish.setSelection(b.booleanValue());
					// Indicate this setting has changed
					noPublishChanged = true;
				} else if (ITomcatServer.PROPERTY_SAVE_SEPARATE_CONTEXT_FILES.equals(event.getPropertyName())) {
					Boolean b = (Boolean) event.getNewValue();
					ServerGeneralEditorSection.this.separateContextFiles.setSelection(b.booleanValue());
					// Indicate this setting has changed
					separateContextFilesChanged = true;
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
		
		// serve modules without publish
		noPublish = toolkit.createButton(composite, NLS.bind(Messages.serverEditorNoPublish, ""), SWT.CHECK);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		noPublish.setLayoutData(data);
		noPublish.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				execute(new SetServeModulesWithoutPublishCommand(tomcatServer, noPublish.getSelection()));
				// Indicate this setting has changed
				noPublishChanged = true;
				updating = false;
			}
		});
		// TODO Address help
//		whs.setHelp(noPublish, ContextIds.SERVER_EDITOR_SECURE);

		// save separate context XML files
		separateContextFiles = toolkit.createButton(composite, NLS.bind(Messages.serverEditorSeparateContextFiles, ""), SWT.CHECK);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		separateContextFiles.setLayoutData(data);
		separateContextFiles.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (updating)
					return;
				updating = true;
				execute(new SetSaveSeparateContextFilesCommand(tomcatServer, separateContextFiles.getSelection()));
				// Indicate this setting has changed
				separateContextFilesChanged = true;
				updating = false;
			}
		});
		// TODO Address help
//		whs.setHelp(separateContextFiles, ContextIds.SERVER_EDITOR_SECURE);
		
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
		debug = toolkit.createButton(composite, NLS.bind(Messages.serverEditorDebugMode, ""), SWT.CHECK);
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
		if (server != null)
			server.removePropertyChangeListener(listener);
	}

	/**
	 * @see ServerEditorSection#init(IEditorSite, IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		if (server != null) {
			tomcatServer = (TomcatServer) server.loadAdapter(TomcatServer.class, null);
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
		ITomcatVersionHandler tvh = tomcatServer.getTomcatVersionHandler();
		
		boolean supported = tvh.supportsServeModulesWithoutPublish();
		String label = NLS.bind(Messages.serverEditorNoPublish,
				supported ? "" : Messages.serverEditorNotSupported);
		noPublish.setText(label);
		noPublish.setSelection(tomcatServer.isServeModulesWithoutPublish());
		if (readOnly || !supported)
			noPublish.setEnabled(false);
		else
			noPublish.setEnabled(true);

		supported = tvh.supportsSeparateContextFiles();
		label = NLS.bind(Messages.serverEditorSeparateContextFiles,
				supported ? "" : Messages.serverEditorNotSupported);
		separateContextFiles.setText(label);
		separateContextFiles.setSelection(tomcatServer.isSaveSeparateContextFiles());
		if (readOnly || !supported)
			separateContextFiles.setEnabled(false);
		else
			separateContextFiles.setEnabled(true);

		secure.setSelection(tomcatServer.isSecure());
		
		supported = tvh.supportsDebugArgument();
		label = NLS.bind(Messages.serverEditorDebugMode,
				supported ? "" : Messages.serverEditorNotSupported);
		debug.setText(label);
		if (readOnly || !supported)
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

	/**
	 * @see ServerEditorSection#getSaveStatus()
	 */
	public IStatus[] getSaveStatus() {
		// If serve modules without publishing has changed, request clean publish to be safe
		if (noPublishChanged) {
			// If server is running, abort the save since clean publish won't succeed
			if (tomcatServer.getServer().getServerState() != IServer.STATE_STOPPED) {
				return new IStatus [] {
						new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID,
								NLS.bind(Messages.errorServerMustBeStopped,
										NLS.bind(Messages.serverEditorNoPublish, "").trim()))
				};
			}
			// Force a clean publish
			PublishServerJob publishJob = new PublishServerJob(tomcatServer.getServer(), IServer.PUBLISH_CLEAN, false);
			publishJob.schedule();
			noPublishChanged = false;
		}
		if (separateContextFilesChanged) {
			// If server is running, abort the save since contexts will be moving
			if (tomcatServer.getServer().getServerState() != IServer.STATE_STOPPED) {
				return new IStatus [] {
						new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID,
								NLS.bind(Messages.errorServerMustBeStopped,
										NLS.bind(Messages.serverEditorSeparateContextFiles, "").trim()))
				};
			}
		}
		// use default implementation to return success
		return super.getSaveStatus();
	}
}