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
package org.eclipse.wst.server.ui.editor;

import java.util.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.editor.IServerEditorPageSectionFactory;
import org.eclipse.wst.server.ui.internal.editor.IServerEditorPartFactory;
import org.eclipse.wst.server.ui.internal.editor.ServerEditorCore;
/**
 * An abstract server editor which implements the most common methods
 * from IEditorPart.
 * 
 * This class also provides each editor page with an error message which
 * will be displayed on the status bar of the editor.
 */
public abstract class ServerEditorPart extends EditorPart {
	public static final int PROP_ERROR = 5;
	
	private String errorMessage = null;
	
	private Map sectionToInsertionId = null;
	private List sections = null;
	
	protected IServerEditorPartFactory pageFactory;
	protected IServerWorkingCopy server;
	protected ICommandManager commandManager;
	protected boolean readOnly;
	
	private FormToolkit toolkit;

	public ServerEditorPart() {
		super();
	}

	public void setPageFactory(IServerEditorPartFactory pageFactory) {
		this.pageFactory = pageFactory;
	}

	/**
	 * @see IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		// do nothing
	}

	/**
	 * @see IEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		// do nothing
	}

	/**
	 * @see IEditorPart#gotoMarker(IMarker)
	 */
	public void gotoMarker(IMarker marker) {
		// do nothing
	}

	/**
	 * @see IEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * @see IEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
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
		super.firePropertyChange(PROP_ERROR);
	}
	
	public void updateErrorMessage() {
		super.firePropertyChange(PROP_ERROR);
	}

	/**
	 * Return the error message for this page.
	 * 
	 * @return java.lang.String
	 */
	public String getErrorMessage() {
		if (errorMessage == null) {
			Iterator iterator = getSections().iterator();
			while (iterator.hasNext()) {
				IServerEditorSection section = (IServerEditorSection) iterator.next();
				String error = section.getErrorMessage();
				if (error != null)
					return error;
			}
		}
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
		Iterator iterator = getSections().iterator();
		List list = new ArrayList();
		while (iterator.hasNext()) {
			IServerEditorSection section = (IServerEditorSection) iterator.next();
			IStatus[] status = section.getSaveStatus();
			if (status != null) {
				int size = status.length;
				for (int i = 0; i < size; i++)
					list.add(status[i]);
			}
		}
		
		int size = list.size();
		IStatus[] status = new IStatus[size];
		list.toArray(status);
		return status;
	}

	protected List getSections() {
		if (sections == null) {
			sections = new ArrayList();
			sectionToInsertionId = new HashMap();
			Iterator iterator = ServerEditorCore.getServerEditorPageSectionFactories().iterator();
			while (iterator.hasNext()) {
				IServerEditorPageSectionFactory factory = (IServerEditorPageSectionFactory) iterator.next();
				String insertionId = factory.getInsertionId();
				
				if (pageFactory.supportsInsertionId(insertionId)) {
					String serverTypeId = null;
					if (server != null) 
						serverTypeId = server.getServerType().getId();
					if (serverTypeId != null && factory.supportsType(serverTypeId)
							&& factory.shouldCreateSection(server)) {
						IServerEditorSection section = factory.createSection();
						if (section instanceof ServerEditorSection)
							((ServerEditorSection) section).setServerResourceEditorPart(this);
						sections.add(section);
						List list = null;
						try {
							list = (List) sectionToInsertionId.get(insertionId);
						} catch (Exception e) {
							// ignore
						}
						if (list == null)
							list = new ArrayList();
						list.add(section);
						sectionToInsertionId.put(insertionId, list);
					}
				}
			}
		}
		return sections;
	}
	
	protected List getSections(String insertionId) {
		if (insertionId == null)
			return null;
		
		getSections();
		List list = new ArrayList();
		try {
			List sections2 = (List) sectionToInsertionId.get(insertionId);
			Iterator iterator = sections2.iterator();
			while (iterator.hasNext()) {
				list.add(iterator.next());
			}
		} catch (Exception e) {
			// ignore
		}
		return list;
	}

	public void init(IEditorSite site, IEditorInput input) {
		setSite(site);
		setInput(input);
		if (input instanceof IServerEditorPartInput) {
			IServerEditorPartInput sepi = (IServerEditorPartInput) input;
			server = sepi.getServer();
			commandManager = sepi.getServerCommandManager();
			readOnly = sepi.isServerReadOnly();
		}
		
		Iterator iterator = getSections().iterator();
		while (iterator.hasNext()) {
			IServerEditorSection section = (IServerEditorSection) iterator.next();
			section.init(site, input);
		}
	}
	
	public IServerWorkingCopy getServer() {
		return server;
	}

	public void insertSections(Composite parent, String id) {
		if (id == null)
			return;
		
		Iterator iterator = getSections(id).iterator();
		while (iterator.hasNext()) {
			IServerEditorSection section = (IServerEditorSection) iterator.next();
			section.createSection(parent);
		}
	}
	
	public void dispose() {
		super.dispose();

		Iterator iterator = getSections().iterator();
		while (iterator.hasNext()) {
			IServerEditorSection section = (IServerEditorSection) iterator.next();
			section.dispose();
		}
		
		if (toolkit != null)
			toolkit.dispose();
	}

	/**
	 * Get a form toolkit to create widgets. It will automatically be disposed
	 * when the editor is disposed.
	 * 
	 * @param display
	 * @return FormToolkit
	 */
	public FormToolkit getFormToolkit(Display display) {
		if (toolkit == null)
			toolkit = new FormToolkit(display);
		return toolkit;
	}
}