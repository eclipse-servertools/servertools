package org.eclipse.wst.server.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerWorkingCopy;
/**
 * 
 */
public abstract class ServerResourceEditorSection implements IServerEditorSection {
	private String errorMessage = null;

	public IServerWorkingCopy server;
	public IServerConfigurationWorkingCopy serverConfiguration;
	public ICommandManager commandManager;
	protected boolean readOnly;
	protected Composite parentComp;
	protected ServerResourceEditorPart editor;

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.editor.IServerEditorSection#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) {
		if (input instanceof IServerEditorPartInput) {
			IServerEditorPartInput sepi = (IServerEditorPartInput) input;
			server = sepi.getServer();
			serverConfiguration = sepi.getServerConfiguration();
			commandManager = sepi.getServerCommandManager();
			readOnly = sepi.isServerReadOnly();
		}
	}

	public void createSection(Composite parent) {
		this.parentComp = parent;
	}

	public Shell getShell() {
		return parentComp.getShell();
	}
	
	/**
	 * Return the error message for this page.
	 * 
	 * @return java.lang.String
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns error or status messages that will be displayed when the
	 * server resource is saved. If there are any error messages, the
	 * user will be unable to save the editor.
	 * 
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public IStatus[] getSaveStatus() {
		return null;
	}
	
	public void setServerResourceEditorPart(ServerResourceEditorPart editor) {
		this.editor = editor;
	}

	/**
	 * Set an error message for this page.
	 * 
	 * @param error java.lang.String
	 */
	public void setErrorMessage(String error) {
		if (error == null && errorMessage == null)
			return;
		
		if (error != null && error.equals(errorMessage))
			return;
		
		errorMessage = error;
		if (editor != null)
			editor.updateErrorMessage();
	}

	/**
	 * Get a form toolkit to create widgets. It will automatically be disposed
	 * when the editor is disposed.
	 * 
	 * @param display
	 * @return FormToolkit
	 */
	public FormToolkit getFormToolkit(Display display) {
		return editor.getFormToolkit(display);
	}}
