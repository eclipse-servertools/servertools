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
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.ServerUICore;
/**
 * PropertyPage for IProjects. It shows the server and runtime preference for the project.
 */
public class ProjectPropertyPage extends PropertyPage {
	protected IProject project;
	protected IModule module;
	protected IServer server;

	/**
	 * ProjectPropertyPage constructor comment.
	 */
	public ProjectPropertyPage() {
		super();
	}

	/**
	 * Create the body of the page.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		try {
			IAdaptable element = getElement();
			if (element instanceof IProject)
				project = (IProject) element;

			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 4;
			layout.verticalSpacing = 10;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Label label = new Label(composite, SWT.WRAP);
			label.setText(Messages.prefProjectDescription);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 4;
			data.widthHint = 200;
			label.setLayoutData(data);
			
			module = ServerUtil.getModule(project);
			if (module == null) {
				label = new Label(composite, SWT.NONE);
				label.setText(Messages.prefProjectNotModule);
				data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				data.horizontalSpan = 4;
				label.setLayoutData(data);
			} else {
				IModuleType mt = module.getModuleType();
				if (mt != null) {
					label = new Label(composite, SWT.NONE);
					label.setText(Messages.prefProject);
					data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					label.setLayoutData(data);
				
					Label moduleKind = new Label(composite, SWT.NONE);
					data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					data.horizontalSpan = 3;
					moduleKind.setLayoutData(data);
					moduleKind.setText(module.getName() + " (" + mt.getName() + ")");
				}
				
				IServer prefServer = ServerCore.getDefaultServer(module);
	
				label = new Label(composite, SWT.NONE);
				label.setText(Messages.prefProjectDefaultServer);
				data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
				label.setLayoutData(data);
				
				final IServer[] servers = getServersBySupportedModule(module);
				if (servers == null || servers.length == 0) {
					label = new Label(composite, SWT.WRAP);
					label.setText(Messages.prefProjectNotConfigured);
					data = new GridData();
					data.horizontalSpan = 3;
					label.setLayoutData(data);
				} else {
					final Table table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
					data = new GridData(GridData.FILL_HORIZONTAL);
					data.horizontalSpan = 3;
					data.heightHint = 70;
					table.setLayoutData(data);
					
					// add none option
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(Messages.prefProjectNoServer);
					//item.setImage();
					
					int size2 = servers.length;
					int count = 0;
					for (int j = 0; j < size2; j++) {
						item = new TableItem(table, SWT.NONE);
						item.setText(ServerUICore.getLabelProvider().getText(servers[j]));
						item.setImage(ServerUICore.getLabelProvider().getImage(servers[j]));
						item.setData(servers[j]);
						if (servers[j].equals(prefServer))
							count = j + 1;
					}

					table.setSelection(count);

					table.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							int index = table.getSelectionIndex();
							if (index == 0) {
								server = null;
							} else if (index > 0) {
								server = servers[index-1];
							}
						}
					});
				}
			}
			
			Dialog.applyDialogFont(composite);

			return composite;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error creating project property page", e);
			return null;
		}
	}

	/**
	 * Returns a list of all servers that this module is configured on.
	 *
	 * @param module org.eclipse.wst.server.core.IModule
	 * @return java.util.List
	 */
	protected static IServer[] getServersBySupportedModule(IModule module) {
		if (module == null)
			return new IServer[0];
		
		// do it the slow way - go through all servers and
		// see if this module is configured in it
		List list = new ArrayList();
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (servers[i].getServerType() != null &&
					servers[i].getServerType().getRuntimeType() != null &&
					ServerUtil.isSupportedModule(servers[i].getServerType().getRuntimeType().getModuleTypes(), module.getModuleType()))
					list.add(servers[i]);
			}
		}
		
		IServer[] allServers = new IServer[list.size()];
		list.toArray(allServers);
		return allServers;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		if (module != null) {
			try {
				ServerCore.setDefaultServer(module, server, null);
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Error setting preferred server", e);
				EclipseUtil.openError(Messages.errorCouldNotSavePreference, e.getStatus());
				return false;
			}
		}
		return super.performOk();
	}
}