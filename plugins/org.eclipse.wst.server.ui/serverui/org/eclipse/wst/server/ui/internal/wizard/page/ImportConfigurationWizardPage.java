package org.eclipse.wst.server.ui.internal.wizard.page;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationType;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.internal.viewers.ServerConfigurationTypeComposite;

/**
 * Wizard page to import a configuration.
 */
public class ImportConfigurationWizardPage extends WizardPage {
	protected IServerConfigurationType selectedConfigType;
	protected IServerConfigurationWorkingCopy configuration;
	protected Combo serverProject;
	protected Button create;
	protected Text name;
	protected Text filename;
	protected ServerConfigurationTypeComposite configTypeComposite;
	private Label description;
	protected Button browse;
	private IContainer defaultContainer;
	
	private String[] validationErrors = new String[5];
	private static final int INVALID_NAME = 0;
	private static final int INVALID_FOLDER = 1;
	private static final int INVALID_TYPE = 2;
	private static final int INVALID_FILENAME = 3;
	private static final int INVALID_IMPORT = 4;
	
	protected LoadThread thread;
	protected boolean threadDone;

	class LoadThread extends Thread {
		final int DELAY = 800;
		String filename2;
		IFile file;
		public void run() {
			boolean b = true;
			while (b) {
				try {
					sleep(DELAY);
					b = false;
					thread = null;
				} catch (InterruptedException ie) { }
			}
			try {
				performLoadConfiguration(file, filename2);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						validatePage(INVALID_IMPORT);
					}
				});
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error importing configuration", e);
			}
			threadDone = true;
		}
	}

	/**
	 * ImportConfigurationWizardPage constructor comment.
	 */
	public ImportConfigurationWizardPage() {
		super("import configuration");
	
		setTitle(ServerUIPlugin.getResource("%wizImportConfigurationTitle"));
		setDescription(ServerUIPlugin.getResource("%wizImportConfigurationDescription"));
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_IMPORT_SERVER_CONFIGURATION));
	}
	
	/**
	 * Creates the UI part of the page. Subclasses must reimplement 
	 * this method to provide their own graphical page content.
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
	
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = convertHorizontalDLUsToPixels(5);
		layout.marginHeight = convertVerticalDLUsToPixels(5);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertHorizontalDLUsToPixels(500);
		data.heightHint = convertVerticalDLUsToPixels(470);
		WorkbenchHelp.setHelp(composite, ContextIds.IMPORT_CONFIGURATION_WIZARD);
	
		new Label(composite, SWT.NONE).setText(ServerUIPlugin.getResource("%serverEditorOverviewServerConfigurationName"));
	
		name = new Text(composite, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		name.setLayoutData(data);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				validateName();
				validatePage(INVALID_NAME);
			}
		});
		name.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				 validatePage(INVALID_NAME);
			}
		});
		WorkbenchHelp.setHelp(name, ContextIds.IMPORT_CONFIGURATION_NAME);
	
		// choose a server project
		new Label(composite, SWT.NONE).setText(ServerUIPlugin.getResource("%wizFolder"));
		serverProject = new Combo(composite, SWT.BORDER);
		WizardUtil.fillComboWithServerProjectFolders(serverProject);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		serverProject.setLayoutData(data);
		if (defaultContainer != null)
			serverProject.setText(WizardUtil.getContainerText(defaultContainer));
		else if (serverProject.getItemCount() == 0)
			serverProject.setText(ServerPlugin.getResource("%defaultServerProjectName", ""));
	
		serverProject.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				validateName();
				validateFolder();
				validatePage(INVALID_FOLDER);
			}
		});
		serverProject.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				 validatePage(INVALID_FOLDER);
			}
		});
		WorkbenchHelp.setHelp(serverProject, ContextIds.IMPORT_CONFIGURATION_FOLDER);
	
		configTypeComposite = new ServerConfigurationTypeComposite(composite, SWT.NONE, new ServerConfigurationTypeComposite.ServerConfigurationTypeSelectionListener() {
			public void configurationTypeSelected(IServerConfigurationType type) {
				handleFactorySelection(type);
				validateName();
				validatePage(INVALID_TYPE);
			}
		});
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 130;
		data.horizontalSpan = 3;
		configTypeComposite.setLayoutData(data);
		WorkbenchHelp.setHelp(configTypeComposite, ContextIds.IMPORT_CONFIGURATION_FACTORY);
	
		Label label = new Label(composite, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%wizDescription"));
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		label.setLayoutData(data);
	
		description = new Label(composite, SWT.WRAP);
		description.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 58;
		data.horizontalSpan = 2;
		description.setLayoutData(data);
	
		label = new Label(composite, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%wizImportConfigurationLocation"));
	
		filename = new Text(composite, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 300;
		filename.setLayoutData(data);
		filename.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				handleFileSelection();
				validatePage(INVALID_TYPE);
			}
		});
		WorkbenchHelp.setHelp(filename, ContextIds.IMPORT_CONFIGURATION_LOCATION);
	
		browse = SWTUtil.createButton(composite, ServerUIPlugin.getResource("%wizBrowse")); 
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (selectedConfigType == null)
					return;
				String[] filter = selectedConfigType.getImportFilterExtensions();
				if (filter != null) {
					int size = filter.length;
					String[] s = new String[size];
					for (int i = 0; i < size; i++) {
						if (!filter[i].startsWith("*."))
							s[i] = "*." + filter[i];
						else
							s[i] = filter[i];
					}
					FileDialog dialog = new FileDialog(getShell());
					dialog.setText(ServerUIPlugin.getResource("%wizImportConfigurationFile"));
					dialog.setFilterPath(filename.getText());
					dialog.setFilterExtensions(s);
					String selectedFile = dialog.open();
					if (selectedFile != null)
						filename.setText(selectedFile);
				} else {
					DirectoryDialog dialog = new DirectoryDialog(getShell());
					dialog.setMessage(ServerUIPlugin.getResource("%wizImportConfigurationDirectory"));
					dialog.setFilterPath(filename.getText());
					String selectedDirectory = dialog.open();
					if (selectedDirectory != null)
						filename.setText(selectedDirectory);
				}
			}
		});
		browse.setEnabled(false);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		//data.widthHint = 75;
		data.heightHint = 22;
		browse.setLayoutData(data);
		WorkbenchHelp.setHelp(browse, ContextIds.IMPORT_CONFIGURATION_LOCATION_BROWSE);
	
		//fillFactoryTree();
		validationErrors[INVALID_TYPE] = "";
		validationErrors[INVALID_FILENAME] = "";
		
		ServerUIPreferences sp = (ServerUIPreferences) ServerUICore.getPreferences();
		if (sp.getImportLocation() != null) {
			filename.setText(sp.getImportLocation());
			handleFileSelection();
			validatePage(INVALID_TYPE);
		}
	
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}
	
	protected void loadConfiguration() {
		if (thread != null) {
			try {
				thread.interrupt();
				thread.filename2 = filename.getText();
				IFile file = null;
				String theName = name.getText();
				if (theName != null && !theName.endsWith(IServerConfiguration.FILE_EXTENSION))
					theName += "." + IServerConfiguration.FILE_EXTENSION;
				String projectName = serverProject.getText();
				if (!"metadata".equals(projectName))
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(projectName).append(theName));
				thread.file = file;
			} catch (Exception e) { }
		} else {
			// try to avoid multiple threads
			if (!threadDone) {
				try {
					Thread.sleep(200);
				} catch (Exception e) { }
			}
			if (validationErrors[INVALID_TYPE] != null || validationErrors[INVALID_FILENAME] != null)
				return;
			thread = new LoadThread();
			thread.filename2 = filename.getText();
			IFile file = null;
			String theName = name.getText();
			if (theName != null && !theName.endsWith(IServerConfiguration.FILE_EXTENSION))
				theName += "." + IServerConfiguration.FILE_EXTENSION;
			String projectName = serverProject.getText();
			if (!"metadata".equals(projectName))
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(projectName).append(theName));
			thread.file = file;
			getContainer().updateButtons();
			threadDone = false;
			thread.start();
		}
	}
	
	/**
	 * Handle the server factory selection.
	 */
	protected void handleFactorySelection(IServerConfigurationType type) {
		validationErrors[INVALID_IMPORT] = null;
		configuration = null;
		selectedConfigType = type;
	
		validationErrors[INVALID_TYPE] = null;
		
		if (selectedConfigType == null) {
			browse.setEnabled(false);
			description.setText("");
			return;
		}
	
		try {
			String text = selectedConfigType.getDescription();
			if (text == null)
				text = "";
			description.setText(text);
			browse.setEnabled(true);
			
			loadConfiguration();
		} catch (Exception e) {
			validationErrors[INVALID_TYPE] = ServerUIPlugin.getResource("%wizErrorImport");
			Trace.trace(Trace.SEVERE, "Could not import from " + filename.getText(), e);
		}
	}
	
	protected void handleFileSelection() {
		validationErrors[INVALID_IMPORT] = null;
		String text = filename.getText();
		if (text == null || text.length() == 0)
			validationErrors[INVALID_FILENAME] = "";
		else {
			validationErrors[INVALID_FILENAME] = null;
			loadConfiguration();
		}
	}
	
	protected void performLoadConfiguration(IFile file, String filename2) {
		try {
			try {
				configuration = selectedConfigType.importFromPath(null, file, new Path(filename2), new NullProgressMonitor());
				if (configuration == null)
					validationErrors[INVALID_IMPORT] = ServerUIPlugin.getResource("%wizErrorImport");
				else
					validationErrors[INVALID_IMPORT] = null;
			} catch (CoreException ce) {
				IStatus status = ce.getStatus();
				if (status != null && status.getMessage() != null && status.getMessage().length() > 1)
					validationErrors[INVALID_IMPORT] = ce.getStatus().getMessage();
				else
					validationErrors[INVALID_IMPORT] = ServerUIPlugin.getResource("%wizErrorImport");
			}
		} catch (Exception e) {
			validationErrors[INVALID_IMPORT] = ServerUIPlugin.getResource("%wizErrorImport");
			Trace.trace(Trace.SEVERE, "Could not import from " + filename, e);
		}
	}
	
	/**
	 * Return true if this page is complete.
	 * @return boolean
	 */
	public boolean isPageComplete() {
		// check for validation first
		for (int i = 0; i < validationErrors.length; i++) {
			if (validationErrors[i] != null)
				return false;
		}
	
		if (configuration == null)
			return false;
			
		if (thread != null)
			return false;
	
		// otherwise, defer to superclass
		return super.isPageComplete();
	}
	
	/**
	 * Finish the wizard by saving the configuration into
	 * the selected folder.
	 * @return boolean
	 */
	public boolean performFinish() {
		if (configuration == null)
			return false;
			
		ServerUIPreferences sp = (ServerUIPreferences) ServerUICore.getPreferences();
		sp.setImportLocation(filename.getText());
	
		try {
			final String theName = name.getText();
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					saveConfiguration(configuration, theName, new NullProgressMonitor());
				}
			};
			
			getWizard().getContainer().run(true, true, new WorkspaceRunnableAdapter(runnable));

			return true;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error saving imported configuration", e);
			return false;
		}
	}
	
	/**
	 * Save the element to the given file name.
	 * @param element org.eclipse.wst.server.core.model.IServerResource
	 * @param name java.lang.String
	 * @param org.eclipse.core.runtime.IProgressMonitor monitor
	 */
	protected void saveConfiguration(IServerConfigurationWorkingCopy config, String theName, IProgressMonitor monitor) throws CoreException {
		// save the element
		try {
			IFile file = config.getFile();
			if (file != null && !file.getProject().exists()) {
				IProject project = file.getProject();
				ServerCore.createServerProject(project.getName(), null, monitor);
			}
			config.setName(theName);
			config.save(false, monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error saving created element", e);
			throw new CoreException(new Status(IStatus.ERROR, ServerUICore.PLUGIN_ID, 0, "Could not create server project", null));
		}
	}
	
	/**
	 * Sets the default container.
	 * @param org.eclipse.core.resources.IContainer
	 */
	public void setDefaultContainer(IContainer container) {
		defaultContainer = container;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	
		if (visible) {
			// force the focus to initially validate the fields
			validateName();
			validateFolder();
			handleFactorySelection(null);
			validatePage(INVALID_NAME);
	
			name.forceFocus();
		}
	}
	
	/**
	 * Validates the folder.
	 */
	protected void validateFolder() {
		String text = serverProject.getText();
		if (text == null || text.length() == 0) {
			validationErrors[INVALID_FOLDER] = "";
			return;
		}
	
		validationErrors[INVALID_FOLDER] = WizardUtil.validateContainer(text);
	}
	
	/**
	 * Validates the name.
	 */
	protected void validateName() {
		String text = name.getText();
		if (text == null || text.length() == 0) {
			validationErrors[INVALID_NAME] = "";
			return;
		}
	
		IStatus status = ResourcesPlugin.getWorkspace().validateName(text, IResource.FILE);
		if (status.isOK())
			status = ResourcesPlugin.getWorkspace().validateName(text, IResource.FOLDER);
		
		if (!status.isOK())
			validationErrors[INVALID_NAME] = status.getMessage();
		else {
			// check if file exists
			String fileName = text;
			if (selectedConfigType != null) {
				String ext = IServerConfiguration.FILE_EXTENSION;
				if (ext != null && !fileName.endsWith("." + ext))
					fileName += "." + ext;
			}
		
			IContainer container = WizardUtil.findContainer(serverProject.getText());
			if (container != null && container.getLocation().append(fileName).toFile().exists()) {
				validationErrors[INVALID_NAME] = ServerUIPlugin.getResource("%wizErrorResourceAlreadyExists");
			} else
				validationErrors[INVALID_NAME] = null;
		}
	}
	
	/**
	 * Display the correct error message and enable/disable
	 * the Finish or Next button.
	 * @param x the current control (error message gets precedence)
	 */
	protected void validatePage(int x) {
		if (x >= 0 && validationErrors[x] != null && validationErrors[x].length() > 0) {
			setErrorMessage(validationErrors[x]);
			getContainer().updateButtons();
			return;
		}
		int size = validationErrors.length;
		for (int i = 0; i < size; i++) {
			if (validationErrors[i] != null && validationErrors[i].length() > 0) {
				setErrorMessage(validationErrors[i]);
				getContainer().updateButtons();
				return;
			}
		}
		setErrorMessage(null);
		getContainer().updateButtons();
	}
}
