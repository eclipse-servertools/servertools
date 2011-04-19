/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
import org.eclipse.jface.viewers.ILabelProvider;
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
 * PropertyPage for IProjects. It shows the server preference for the project.
 */
public class ProjectPropertyPage extends PropertyPage {
	protected IProject project;
	protected IModule module;
	protected IServer server;

	protected Table table;
	protected int count;
	protected IServer defaultServer;

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
			project = (IProject) element.getAdapter(IProject.class);
			//if (element instanceof IProject)
			//	project = (IProject) element;
			
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 2;
			layout.verticalSpacing = 5;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			/*Label label = new Label(composite, SWT.WRAP);
			label.setText(Messages.prefProjectDescription);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			data.widthHint = 200;
			label.setLayoutData(data);*/
			
			module = ServerUtil.getModule(project);
			if (module == null) {
				Label label = new Label(composite, SWT.NONE);
				label.setText(Messages.prefProjectNotModule);
				GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				data.horizontalSpan = 2;
				data.verticalIndent = 5;
				label.setLayoutData(data);
			} else {
				IModuleType mt = module.getModuleType();
				if (mt != null) {
					Label label = new Label(composite, SWT.NONE);
					label.setText(Messages.prefProject);
					GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					data.verticalIndent = 5;
					label.setLayoutData(data);
					
					Label moduleKind = new Label(composite, SWT.NONE);
					data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					data.verticalIndent = 5;
					moduleKind.setLayoutData(data);
					moduleKind.setText(module.getName() + " (" + mt.getName() + ")");
				}
				
				defaultServer = ServerCore.getDefaultServer(module);
				
				final IServer[] servers = getServersBySupportedModule(module);
				if (servers == null || servers.length == 0) {
					Label label = new Label(composite, SWT.WRAP);
					label.setText(Messages.prefProjectNotConfigured);
					GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					data.horizontalSpan = 2;
					data.verticalIndent = 5;
					label.setLayoutData(data);
				} else {
					Label label = new Label(composite, SWT.NONE);
					label.setText(Messages.prefProjectDefaultServer);
					GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
					data.horizontalSpan = 2;
					data.verticalIndent = 5;
					label.setLayoutData(data);
					
					table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
					data = new GridData(GridData.FILL_HORIZONTAL);
					data.horizontalSpan = 2;
					data.horizontalIndent = 15;
					data.heightHint = 90;
					table.setLayoutData(data);
					
					// add none option
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(Messages.prefProjectNoServer);
					//item.setImage();
					
					int size2 = servers.length;
					count = 0;
					ILabelProvider labelProvider = ServerUICore.getLabelProvider();
					for (int j = 0; j < size2; j++) {
						item = new TableItem(table, SWT.NONE);
						item.setText(labelProvider.getText(servers[j]));
						item.setImage(labelProvider.getImage(servers[j]));
						item.setData(servers[j]);
						if (servers[j].equals(defaultServer))
							count = j + 1;
					}
					labelProvider.dispose();
					
					table.setSelection(count);
					
					table.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							int index = table.getSelectionIndex();
							if (index == 0)
								server = null;
							else if (index > 0)
								server = servers[index-1];
						}
					});
				}
			}
			
			Dialog.applyDialogFont(composite);
			
			return composite;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error creating project property page", e);
			}
			return null;
		}
	}

	/**
	 * Returns a list of all servers that this module is configured on.
	 *
	 * @param module a module
	 * @return an array of servers
	 */
	protected static IServer[] getServersBySupportedModule(IModule module) {
		if (module == null)
			return new IServer[0];
		
		// do it the slow way - go through all servers and
		// see if this module is configured in it
		List<IServer> list = new ArrayList<IServer>();
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
		
		return list.toArray(new IServer[list.size()]);
	}

	protected void performDefaults() {
		if (table != null) {
			table.select(count);
			server = defaultServer;
		}
		
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		if (module != null) {
			try {
				ServerCore.setDefaultServer(module, server, null);
			} catch (CoreException e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Error setting preferred server", e);
				}
				EclipseUtil.openError(Messages.errorCouldNotSavePreference, e.getStatus());
				return false;
			}
		}
		return super.performOk();
	}
}