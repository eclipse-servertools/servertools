/**********************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.command.SetDeployDirectoryCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetInstanceDirectoryCommand;
import org.eclipse.jst.server.tomcat.core.internal.command.SetTestEnvironmentCommand;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.Messages;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IPublishListener;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;
/**
 * Tomcat server general editor page.
 */
public class ServerLocationEditorSection extends ServerEditorSection {
	protected Section section;
	protected TomcatServer tomcatServer;

	protected Hyperlink setDefaultDeployDir;
	
	protected boolean defaultDeployDirIsSet;
	
	protected Button serverDirMetadata;
	protected Button serverDirInstall;
	protected Button serverDirCustom;
	
	protected Text serverDir;
	protected Button serverDirBrowse;
	protected Text deployDir;
	protected Button deployDirBrowse;
	protected boolean updating;

	protected PropertyChangeListener listener;
	protected IPublishListener publishListener;
	protected IPath workspacePath;
	protected IPath defaultDeployPath;
	
	protected boolean allowRestrictedEditing;
	protected IPath tempDirPath;
	protected IPath installDirPath;

	// Avoid hardcoding this at some point
	private final static String METADATADIR = ".metadata";
	/**
	 * ServerGeneralEditorPart constructor comment.
	 */
	public ServerLocationEditorSection() {
		// do nothing
	}

	/**
	 * Add listeners to detect undo changes and publishing of the server.
	 */
	protected void addChangeListeners() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (updating)
					return;
				updating = true;
				if (ITomcatServer.PROPERTY_INSTANCE_DIR.equals(event.getPropertyName())
						|| ITomcatServer.PROPERTY_TEST_ENVIRONMENT.equals(event.getPropertyName())) {
					updateServerDirButtons();
					updateServerDirFields();
					validate();
				}
				else if (ITomcatServer.PROPERTY_DEPLOY_DIR.equals(event.getPropertyName())) {
					String s = (String) event.getNewValue();
					ServerLocationEditorSection.this.deployDir.setText(s);
					updateDefaultDeployLink();					
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
							boolean customServerDir = false;
							if (!ServerLocationEditorSection.this.serverDirCustom.isDisposed())
								customServerDir = ServerLocationEditorSection.this.serverDirCustom.getSelection();
							if (!ServerLocationEditorSection.this.serverDirMetadata.isDisposed())
								ServerLocationEditorSection.this.serverDirMetadata.setEnabled(allowRestrictedEditing);
							if (!ServerLocationEditorSection.this.serverDirInstall.isDisposed())
								ServerLocationEditorSection.this.serverDirInstall.setEnabled(allowRestrictedEditing);
							if (!ServerLocationEditorSection.this.serverDirCustom.isDisposed())
								ServerLocationEditorSection.this.serverDirCustom.setEnabled(allowRestrictedEditing);
							if (!ServerLocationEditorSection.this.serverDir.isDisposed())
								ServerLocationEditorSection.this.serverDir.setEnabled(allowRestrictedEditing && customServerDir);
							if (!ServerLocationEditorSection.this.serverDirBrowse.isDisposed())
								ServerLocationEditorSection.this.serverDirBrowse.setEnabled(allowRestrictedEditing && customServerDir);
							if (!ServerLocationEditorSection.this.setDefaultDeployDir.isDisposed())
								ServerLocationEditorSection.this.setDefaultDeployDir.setEnabled(allowRestrictedEditing);
							if (!ServerLocationEditorSection.this.deployDir.isDisposed())
								ServerLocationEditorSection.this.deployDir.setEnabled(allowRestrictedEditing);
							if (!ServerLocationEditorSection.this.deployDirBrowse.isDisposed())
								ServerLocationEditorSection.this.deployDirBrowse.setEnabled(allowRestrictedEditing);
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

		section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
			| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.serverEditorLocationsSection);
		section.setDescription(Messages.serverEditorLocationsDescription);
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
		whs.setHelp(section, ContextIds.SERVER_EDITOR);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		serverDirMetadata = toolkit.createButton(composite,
				NLS.bind(Messages.serverEditorServerDirMetadata, Messages.serverEditorDoesNotModify), SWT.RADIO);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		serverDirMetadata.setLayoutData(data);
		serverDirMetadata.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (updating || !serverDirMetadata.getSelection())
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, true));
				updateServerDirFields();
				updating = false;
				validate();
			}
		});

		serverDirInstall = toolkit.createButton(composite,
				NLS.bind(Messages.serverEditorServerDirInstall, Messages.serverEditorTakesControl), SWT.RADIO);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		serverDirInstall.setLayoutData(data);
		serverDirInstall.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (updating || !serverDirInstall.getSelection())
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, false));
				updateServerDirFields();
				updating = false;
				validate();
			}
		});

		serverDirCustom = toolkit.createButton(composite,
				NLS.bind(Messages.serverEditorServerDirCustom, Messages.serverEditorDoesNotModify), SWT.RADIO);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		serverDirCustom.setLayoutData(data);
		serverDirCustom.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (updating || !serverDirCustom.getSelection())
					return;
				updating = true;
				execute(new SetTestEnvironmentCommand(tomcatServer, true));
				updateServerDirFields();
				updating = false;
				validate();
			}
		});

		// server directory
		Label label = createLabel(toolkit, composite, Messages.serverEditorServerDir);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		serverDir = toolkit.createText(composite, null, SWT.SINGLE);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = 75;
		serverDir.setLayoutData(data);
		serverDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new SetInstanceDirectoryCommand(tomcatServer, getServerDir()));
				updating = false;
				validate();
			}
		});

		serverDirBrowse = toolkit.createButton(composite, Messages.editorBrowse, SWT.PUSH);
		serverDirBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(serverDir.getShell());
				dialog.setMessage(Messages.serverEditorBrowseDeployMessage);
				dialog.setFilterPath(serverDir.getText());
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null && !selectedDirectory.equals(serverDir.getText())) {
					updating = true;
					// Make relative if relative to the workspace
					IPath path = new Path(selectedDirectory);
					if (workspacePath.isPrefixOf(path)) {
						int cnt = path.matchingFirstSegments(workspacePath);
						path = path.removeFirstSegments(cnt).setDevice(null);
						selectedDirectory = path.toOSString();
					}
					execute(new SetInstanceDirectoryCommand(tomcatServer, selectedDirectory));
					updateServerDirButtons();
					updateServerDirFields();
					updating = false;
					validate();
				}
			}
		});
		serverDirBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		// deployment directory link
		setDefaultDeployDir = toolkit.createHyperlink(composite,
				NLS.bind(Messages.serverEditorSetDefaultDeployDirLink, ""), SWT.WRAP);
		setDefaultDeployDir.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				updating = true;
				execute(new SetDeployDirectoryCommand(tomcatServer, ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR));
				deployDir.setText(ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR);
				updateDefaultDeployLink();
				updating = false;
				validate();
			}
		});
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 3;
		setDefaultDeployDir.setLayoutData(data);

		// deployment directory
		label = createLabel(toolkit, composite, Messages.serverEditorDeployDir);
		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		label.setLayoutData(data);

		deployDir = toolkit.createText(composite, null);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		deployDir.setLayoutData(data);
		deployDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (updating)
					return;
				updating = true;
				execute(new SetDeployDirectoryCommand(tomcatServer, deployDir.getText().trim()));
				updateDefaultDeployLink();
				updating = false;
				validate();
			}
		});

		deployDirBrowse = toolkit.createButton(composite, Messages.editorBrowse, SWT.PUSH);
		deployDirBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(deployDir.getShell());
				dialog.setMessage(Messages.serverEditorBrowseDeployMessage);
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
		deployDirBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		initialize();
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
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
		
		// Cache workspace and default deploy paths
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		workspacePath = root.getLocation();
		defaultDeployPath = new Path(ITomcatServerWorkingCopy.DEFAULT_DEPLOYDIR);

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
		if (serverDir== null || tomcatServer == null)
			return;
		updating = true;

		IRuntime runtime = server.getRuntime();
		// If not Tomcat 3.2, update description to mention catalina.base
		if (runtime != null && runtime.getRuntimeType().getId().indexOf("32") < 0)
			section.setDescription(Messages.serverEditorLocationsDescription2);
		if (runtime != null)
			installDirPath = runtime.getLocation();

		// determine if editing of locations is allowed
		allowRestrictedEditing = false;
		IPath basePath = tomcatServer.getRuntimeBaseDirectory();
		if (!readOnly) {
			// If server has not been published, or server is published with no modules, allow editing
			// TODO Find better way to determine if server hasn't been published
			if ((basePath != null && !basePath.append("conf").toFile().exists())
					|| (server.getOriginal().getServerPublishState() == IServer.PUBLISH_STATE_NONE
							&& server.getOriginal().getModules().length == 0)) {
				allowRestrictedEditing = true;
			}
		}
		
		// Update server related fields
		updateServerDirButtons();
		updateServerDirFields();

		serverDirMetadata.setEnabled(allowRestrictedEditing);
		serverDirInstall.setEnabled(allowRestrictedEditing);
		serverDirCustom.setEnabled(allowRestrictedEditing);

		// Update deployment related fields
		updateDefaultDeployLink();
		
		deployDir.setText(tomcatServer.getDeployDirectory());

		setDefaultDeployDir.setEnabled(allowRestrictedEditing);
		deployDir.setEnabled(allowRestrictedEditing);
		deployDirBrowse.setEnabled(allowRestrictedEditing);

		updating = false;
		validate();
	}
	
	protected String getServerDir() {
		String dir = null;
		if (serverDir != null) {
			dir = serverDir.getText().trim();
			IPath path = new Path(dir);
			// Adjust if the temp dir is known and has been entered
			if (tempDirPath != null && tempDirPath.equals(path))
				dir = null;
			// If under the workspace, make relative
			else if (workspacePath.isPrefixOf(path)) {
				int cnt = path.matchingFirstSegments(workspacePath);
				path = path.removeFirstSegments(cnt).setDevice(null);
				dir = path.toOSString();
			}
		}
		return dir;
	}
	
	protected void updateServerDirButtons() {
		if (tomcatServer.getInstanceDirectory() == null) {
			IPath path = tomcatServer.getRuntimeBaseDirectory();
			if (path != null && path.equals(installDirPath)) {
				serverDirInstall.setSelection(true);
				serverDirMetadata.setSelection(false);
				serverDirCustom.setSelection(false);
			} else {
				serverDirMetadata.setSelection(true);
				serverDirInstall.setSelection(false);
				serverDirCustom.setSelection(false);
			}
		} else {
			serverDirCustom.setSelection(true);
			serverDirMetadata.setSelection(false);
			serverDirInstall.setSelection(false);
		}
	}
	
	protected void updateServerDirFields() {
		updateServerDir();
		boolean customServerDir = serverDirCustom.getSelection();
		serverDir.setEnabled(allowRestrictedEditing && customServerDir);
		serverDirBrowse.setEnabled(allowRestrictedEditing && customServerDir);
	}
	
	protected void updateServerDir() {
		IPath path = tomcatServer.getRuntimeBaseDirectory();
		if (path == null)
			serverDir.setText("");
		else if (workspacePath.isPrefixOf(path)) {
			int cnt = path.matchingFirstSegments(workspacePath);
			path = path.removeFirstSegments(cnt).setDevice(null);
			serverDir.setText(path.toOSString());
			// cache the relative temp dir path if that is what we have
			if (tempDirPath == null) {
				if (tomcatServer.isTestEnvironment() && tomcatServer.getInstanceDirectory() == null)
					tempDirPath = path;
			}
		} else
			serverDir.setText(path.toOSString());
	}
	
	protected void updateDefaultDeployLink() {
		boolean newState = defaultDeployPath.equals(new Path(tomcatServer.getDeployDirectory()));
		if (newState != defaultDeployDirIsSet) {
			setDefaultDeployDir.setText(
					newState ? Messages.serverEditorSetDefaultDeployDirLink2
							: Messages.serverEditorSetDefaultDeployDirLink);
			defaultDeployDirIsSet = newState;
		}
	}
	
	/**
	 * @see ServerEditorSection#getSaveStatus()
	 */
	public IStatus[] getSaveStatus() {
		if (tomcatServer != null) {
			// Check the instance directory
			String dir = tomcatServer.getInstanceDirectory();
			if (dir != null) {
				IPath path = new Path(dir);
				// Must not be the same as the workspace location
				if (dir.length() == 0 || workspacePath.equals(path)) {
					return new IStatus [] {
							new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID, Messages.errorServerDirIsRoot)};
				}
				// User specified value may not be under the ".metadata" folder of the workspace 
				else if (workspacePath.isPrefixOf(path)
						|| (!path.isAbsolute() && METADATADIR.equals(path.segment(0)))) {
					int cnt = path.matchingFirstSegments(workspacePath);
					if (METADATADIR.equals(path.segment(cnt))) {
						return new IStatus [] {
								new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID, NLS.bind(Messages.errorServerDirUnderRoot, METADATADIR))};
					}
				}
				else if (path.equals(installDirPath))
					return new IStatus [] {
						new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID,
								NLS.bind(Messages.errorServerDirCustomNotInstall,
										NLS.bind(Messages.serverEditorServerDirInstall, "").trim()))};
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();
				// If non-custom instance dir is not the install and metadata isn't the selection, return error
				if (!path.equals(installDirPath) && !serverDirMetadata.getSelection()) {
					return new IStatus [] {
							new Status(IStatus.ERROR, TomcatUIPlugin.PLUGIN_ID,
									NLS.bind(Messages.errorServerDirCustomNotMetadata, 
											NLS.bind(Messages.serverEditorServerDirMetadata, "").trim()))};
				}
			}

			// Check the deployment directory
			dir = tomcatServer.getDeployDirectory();
			// Deploy directory must be set
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
			// Validate instance directory
			String dir = tomcatServer.getInstanceDirectory();
			if (dir != null) {
				IPath path = new Path(dir);
				// Must not be the same as the workspace location
				if (dir.length() == 0 || workspacePath.equals(path)) {
					setErrorMessage(Messages.errorServerDirIsRoot);
					return;
				}
				// User specified value may not be under the ".metadata" folder of the workspace 
				else if (workspacePath.isPrefixOf(path)
						|| (!path.isAbsolute() && METADATADIR.equals(path.segment(0)))) {
					int cnt = path.matchingFirstSegments(workspacePath);
					if (METADATADIR.equals(path.segment(cnt))) {
						setErrorMessage(NLS.bind(Messages.errorServerDirUnderRoot, METADATADIR));
						return;
					}
				}
				else if (path.equals(installDirPath)) {
					setErrorMessage(NLS.bind(Messages.errorServerDirCustomNotInstall,
							NLS.bind(Messages.serverEditorServerDirInstall, "").trim()));
					return;
				}
			}
			else {
				IPath path = tomcatServer.getRuntimeBaseDirectory();
				// If non-custom instance dir is not the install and metadata isn't the selection, return error
				if (path != null && !path.equals(installDirPath) && !serverDirMetadata.getSelection()) {
					setErrorMessage(NLS.bind(Messages.errorServerDirCustomNotMetadata, 
							NLS.bind(Messages.serverEditorServerDirMetadata, "").trim()));
				}
			}

			// Check the deployment directory
			dir = tomcatServer.getDeployDirectory();
			// Deploy directory must be set
			if (dir == null || dir.length() == 0) {
				setErrorMessage(Messages.errorDeployDirNotSpecified);
				return;
			}
		}
		// All is okay, clear any previous error
		setErrorMessage(null);
	}
}