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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
/**
 * Dialog that prompts a user to change the target runtime.
 * 
 * TODO - must support multiple modules per project (remove [0] from getModules(IProject))
 */
public class RuntimeTargetComposite {
	protected IProject project;
	protected IProjectProperties props;
	protected IRuntime currentRuntime;
	protected IRuntime newRuntime;
	protected IRuntime[] targets;
	protected String[] items;
	
	protected List childProjects;
	protected boolean setChildren = true;
	protected int offset = 0;

	/**
	 * RuntimeTargetComposite constructor comment.
	 * @param parent Composite
	 * @param project IProject
	 */
	protected RuntimeTargetComposite(Composite parent, IProject project) {
		this.project = project;
		props = ServerCore.getProjectProperties(project);
		currentRuntime = props.getRuntimeTarget();
		if (currentRuntime == null)
			offset = 1;
		
		// get child modules
		IModule projectModule = ServerUtil.getModules(project)[0];
		childProjects = new ArrayList();
		if (projectModule != null) {
			List children = new ArrayList();
			IModule[] child = projectModule.getChildModules(null);
			if (child != null) {
				int size = child.length;
				for (int i = 0; i < size; i++)
					children.add(child[i]);
				int a = 0;
				while (a < children.size()) {
					IModule module = (IModule) children.get(a);
					IModule[] child2 = module.getChildModules(null);
					if (child2 != null) {
						size = child2.length;
						for (int i = 0; i < size; i++)
							children.add(child2[i]);
					}
					a++;
				}
			}
			
			Iterator iterator = children.iterator();
			while (iterator.hasNext()) {
				IModule module = (IModule) iterator.next();
				if (module.getProject() != null)
					childProjects.add(module);
			}
		}
		
		createContents(parent);
	}

	/**
	 * 
	 */
	protected void createContents(final Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%runtimeTargetCombo"));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		label.setLayoutData(data);
		
		final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(data);
		
		int sel = updateRuntimes();
		combo.setItems(items);
		if (items.length == 0)
			combo.setEnabled(false);
		else {
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int select = combo.getSelectionIndex();
					if (offset > 0 && select == 0)
						newRuntime = null;
					else
						newRuntime = targets[select - offset];
				}
			});
			if (sel >= 0) {
				combo.select(sel);
				if (offset == 0 || sel > 0)
					newRuntime = targets[sel - offset];
			} else
				combo.select(0);
		}

		final IModule projectModule = ServerUtil.getModules(project)[0];
		
		Button newButton = SWTUtil.createButton(parent, ServerUIPlugin.getResource("%runtimeTargetNewRuntime"));
		newButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentRuntime2 = combo.getText();
				String type = null;
				String version = null;
				if (projectModule != null) {
					IModuleType mt = projectModule.getModuleType();
					type = mt.getId();
					version = mt.getVersion();
				}
				if (ServerUIUtil.showNewRuntimeWizard(parent.getShell(), type, version)) {
					int sel2 = updateRuntimes();
					combo.setItems(items);
					combo.setText(currentRuntime2);
					if (combo.getSelectionIndex() == -1)
						combo.select(sel2);
				}
			}
		});
		
		// child module selection
		if (!childProjects.isEmpty()) {
			final Button includeChildren = new Button(parent, SWT.CHECK);
			includeChildren.setText(ServerUIPlugin.getResource("%runtimeTargetChildren"));
			data = new GridData();
			data.horizontalSpan = 2;
			includeChildren.setLayoutData(data);
			includeChildren.setSelection(true);
			
			includeChildren.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					setChildren = includeChildren.getSelection();
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
		} else {
			new Label(parent, SWT.NONE);
			new Label(parent, SWT.NONE);
		}
		
		Button prefsButton = SWTUtil.createButton(parent, ServerUIPlugin.getResource("%runtimeTargetRuntimePreferences"));
		prefsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentRuntime2 = combo.getText();
				if (showRuntimePreferencePage(parent.getShell())) {
					int sel2 = updateRuntimes();
					combo.setItems(items);
					combo.setText(currentRuntime2);
					if (combo.getSelectionIndex() == -1)
						combo.select(sel2);
				}
			}
		});
	}
	
	protected static boolean showRuntimePreferencePage(Shell shell) {
		PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
		IPreferenceNode node = manager.find("org.eclipse.wst.server.ui.preferencePage").findSubNode("org.eclipse.wst.server.ui.runtime.preferencePage");
		PreferenceManager manager2 = new PreferenceManager();
		manager2.addToRoot(node);
		final PreferenceDialog dialog = new PreferenceDialog(shell, manager2);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				if (dialog.open() == Window.OK)
					result[0] = true;
			}
		});
		return result[0];
	}
	
	protected int updateRuntimes() {
		IModule pm = ServerUtil.getModules(project)[0];
		if (pm != null) {
			IModuleType mt = pm.getModuleType();
			targets = ServerUtil.getRuntimes(mt.getId(), mt.getVersion());
		}

		items = new String[0];
		int sel = -1;
		if (targets != null) {
			int size = targets.length;
			items = new String[size + offset];
			if (offset > 0) {
				items[0] = ServerUIPlugin.getResource("%runtimeTargetNone");
				sel = 0;
			}
			for (int i = 0; i < size; i++) {
				IRuntime target = targets[i];
				items[i+offset] = target.getName();
				if (target.equals(currentRuntime))
					sel = i;
			}
		}
		return sel;
	}
	
	public IRuntime getSelectedRuntime() {
		return newRuntime;
	}
	
	public boolean hasChanged() {
		if (!childProjects.isEmpty())
			return true;
		if (newRuntime == null)
			return false;
		if (newRuntime.equals(currentRuntime))
			return false;
		return true;
	}

	public void apply(IProgressMonitor monitor) throws CoreException {
		if (newRuntime == null || !newRuntime.equals(props.getRuntimeTarget()))
			props.setRuntimeTarget(newRuntime, monitor);
		
		if (setChildren) {
			Iterator iterator = childProjects.iterator();
			while (iterator.hasNext()) {
				IModule module = (IModule) iterator.next();
				IProject proj = module.getProject();
				props = ServerCore.getProjectProperties(proj);
				
				if (newRuntime == null || !newRuntime.equals(props.getRuntimeTarget()))
					props.setRuntimeTarget(newRuntime, monitor);
			}
		}
	}
}