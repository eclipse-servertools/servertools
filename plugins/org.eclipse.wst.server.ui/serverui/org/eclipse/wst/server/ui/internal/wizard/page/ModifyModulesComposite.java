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
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.*;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * A wizard page used to add and remove modules.
 */
public class ModifyModulesComposite extends Composite {
	protected static final ILabelProvider slp = ServerUICore.getLabelProvider();
	
	protected IWizardHandle wizard;
	
	protected IServerAttributes server;

	protected Map childModuleMap = new HashMap();
	protected Map parentTreeItemMap = new HashMap();

	// original modules on the server
	protected List originalModules = new ArrayList();
	
	// modules available to be added to the server
	protected List modules = new ArrayList();
	
	// current modules on the server
	protected List deployed = new ArrayList();

	protected Tree availableTree;
	protected Tree deployedTree;
	
	protected Button add, addAll;
	protected Button remove, removeAll;
	
	protected TaskModel taskModel;
	protected IModule newModule;
	protected IModule origNewModule;
	
	protected Map errorMap;

	/**
	 * Create a new ModifyModulesComposite.
	 * 
	 * @param parent a parent composite
	 * @param wizard a wizard
	 * @param module the module that is being added
	 */
	public ModifyModulesComposite(Composite parent, IWizardHandle wizard, IModule module) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		origNewModule = module;
			
		wizard.setTitle(Messages.wizModuleTitle);
		wizard.setDescription(Messages.wizModuleDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_SELECT_SERVER));
		
		createControl();
	}
	
	public void setServer(IServerAttributes server) {
		if (server == this.server)
			return;

		this.server = server;
		originalModules = new ArrayList();
		deployed = new ArrayList();
		modules = new ArrayList();
		
		childModuleMap = new HashMap();
		
		if (server == null)
			return;

		// get currently deployed modules
		IModule[] currentModules = server.getModules();
		if (currentModules != null) {
			int size = currentModules.length;
			for (int i = 0; i < size; i++) {
				originalModules.add(currentModules[i]);
				deployed.add(currentModules[i]);
			}
		}
		
		// add new module
		newModule = null;
		if (origNewModule != null) {
			try {
				IModule[] parents = server.getRootModules(origNewModule, null);
				if (parents != null && parents.length > 0)
					newModule = parents[0];
				else
					newModule = origNewModule;
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Could not find parent module", e);
				newModule = null;
			}
		}
		if (newModule != null && !deployed.contains(newModule))
			deployed.add(newModule);

		// get remaining modules
		errorMap = new HashMap();
		IModule[] modules2 = ServerUtil.getModules(server.getServerType().getRuntimeType().getModuleTypes());
		if (modules2 != null) {
			int size = modules2.length;
			for (int i = 0; i < size; i++) {
				IModule module = modules2[i];
				if (!deployed.contains(module)) {
					try {
						IModule[] parents = server.getRootModules(module, null);
						if (parents != null) {
							int size2 = parents.length;
							for (int j = 0; j < size2; j++) {
								if (parents[j].equals(module)) {
									IStatus status = server.canModifyModules(new IModule[] { module }, null, null);
									if (status != null && !status.isOK())
										errorMap.put(module, status);
									modules.add(module);
								}
							}
						}
					} catch (CoreException ce) {
						errorMap.put(module, ce.getStatus());
						modules.add(module);
					}
				}
			}
		}

		// build child map
		Iterator iterator = deployed.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			try {
				IModule[] children = server.getChildModules(new IModule[] { module }, null);
				childModuleMap.put(module, children);
			} catch (Exception e) {
				// ignore
			}
		}

		iterator = modules.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			try {
				IModule[] children = server.getChildModules(new IModule[] { module }, null);
				childModuleMap.put(module, children);
			} catch (Exception e) {
				// ignore
			}
		}
		
		if (availableTree != null)
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try { // update trees if we can
						parentTreeItemMap = new HashMap();
						fillTree(availableTree, modules);
						fillTree(deployedTree, deployed);
						setEnablement();
					} catch (Exception e) {
						// ignore
					}
				}
			});
		updateTaskModel();
	}
	
	public void setTaskModel(TaskModel model) {
		this.taskModel = model;
	}

	/**
	 * Creates the UI of the page.
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		setLayout(layout);
		setFont(getParent().getFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ContextIds.MODIFY_MODULES_COMPOSITE);

		Label label = new Label(this, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		label.setText(Messages.wizModuleMessage);

		label = new Label(this, SWT.NONE);
		label.setText(Messages.wizModuleAvailableList);
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		label = new Label(this, SWT.NONE);
		label.setText(Messages.wizModuleDeployedList);

		availableTree = new Tree(this, SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.widthHint = 150;
		availableTree.setLayoutData(data);
		availableTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setEnablement();
			}
		});

		// slosh buttons
		Composite comp = new Composite(this, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 120;
		comp.setLayoutData(data);

		layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 25;
		layout.verticalSpacing = 20;
		comp.setLayout(layout);

		add = new Button(comp, SWT.PUSH);
		add.setText(Messages.wizModuleAdd);
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(false);
			}
		});
		
		remove = new Button(comp, SWT.PUSH);
		remove.setText(Messages.wizModuleRemove);
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(false);
			}
		});
		
		label = new Label(comp, SWT.NONE);
		label.setText("");
		
		addAll = new Button(comp, SWT.PUSH);
		addAll.setText(Messages.wizModuleAddAll);
		addAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(true);
			}
		});
		
		removeAll = new Button(comp, SWT.PUSH);
		removeAll.setText(Messages.wizModuleRemoveAll);
		removeAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(true);
			}
		});
		
		deployedTree = new Tree(this, SWT.BORDER);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 150;
		deployedTree.setLayoutData(data);
		deployedTree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setEnablement();
			}
		});
		
		parentTreeItemMap = new HashMap();
		fillTree(availableTree, modules);
		fillTree(deployedTree, deployed);
		
		setEnablement();
		
		availableTree.setFocus();
		
		Dialog.applyDialogFont(this);
	}
	
	protected void setEnablement() {
		boolean enabled = false;
		wizard.setMessage(null, IMessageProvider.NONE);
		if (availableTree.getItemCount() > 0) {
			try {
				TreeItem item = availableTree.getSelection()[0];
				item = (TreeItem) parentTreeItemMap.get(item);
				IModule module = (IModule) item.getData();
				
				IStatus status = (IStatus) errorMap.get(module);
				if (modules.contains(module)) {
					if (status == null)
						enabled = true;
					else if (status.getSeverity() == IStatus.ERROR)
						wizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
					else if (status.getSeverity() == IStatus.WARNING)
						wizard.setMessage(status.getMessage(), IMessageProvider.WARNING);
					else if (status.getSeverity() == IStatus.INFO)
						wizard.setMessage(status.getMessage(), IMessageProvider.INFORMATION);
				}
			} catch (Exception e) {
				// ignore
			}
		}
		add.setEnabled(enabled);
		addAll.setEnabled(availableTree.getItemCount() > 0);
		
		enabled = false;
		if (deployedTree.getItemCount() > 0) {
			try {
				TreeItem item = deployedTree.getSelection()[0];
				item = (TreeItem) parentTreeItemMap.get(item);
				IModule module = (IModule) item.getData();
				if (deployed.contains(module) && !module.equals(newModule))
					enabled = true;
			} catch (Exception e) {
				// ignore
			}
		}
		remove.setEnabled(enabled);
		if (newModule == null)
			removeAll.setEnabled(deployedTree.getItemCount() > 0);
		else
			removeAll.setEnabled(deployedTree.getItemCount() > 1);
	}

	protected void addChildren(TreeItem item, IModule[] module) {
		try {
			IModule[] children = (IModule[]) childModuleMap.get(module);
			if (children != null) {
				int size = children.length;
				for (int i = 0; i < size; i++) {
					IModule child = children[i];
					TreeItem childItem = new TreeItem(item, SWT.NONE);
					childItem.setText(slp.getText(child));
					childItem.setImage(slp.getImage(child));
					childItem.setData(child);
					parentTreeItemMap.put(childItem, item);
					
					int size2 = module.length;
					IModule[] module2 = new IModule[size2 + 1];
					System.arraycopy(module, 0, module2, 0, size2);
					module2[size2] = child;
					addChildren(childItem, module2);
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	protected void fillTree(Tree tree, List modules2) {
		tree.removeAll();

		Iterator iterator = modules2.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(slp.getText(module));
			item.setImage(slp.getImage(module));
			item.setData(module);
			parentTreeItemMap.put(item, item);
			addChildren(item, new IModule[] { module });
		}
	}

	protected void add(boolean all) {
		if (all)
			moveAll(availableTree.getItems(), true);
		else
			moveAll(availableTree.getSelection(), true);
		updateTaskModel();
	}

	protected void remove(boolean all) {
		if (all)
			moveAll(deployedTree.getItems(), false);
		else
			moveAll(deployedTree.getSelection(), false);
		updateTaskModel();
	}

	protected void moveAll(TreeItem[] items, boolean add2) {
		int size = items.length;
		List list = new ArrayList();
		for (int i = 0; i < size; i++) {
			TreeItem item = (TreeItem) parentTreeItemMap.get(items[i]);
			IModule module = (IModule) item.getData();
			IStatus status = (IStatus) errorMap.get(module);
			
			if (status == null && !list.contains(module))
				list.add(module);
		}

		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (add2) {
				modules.remove(module);
				deployed.add(module);
			} else if (!module.equals(newModule)) {
				modules.add(module);
				deployed.remove(module);
			}
		}

		parentTreeItemMap = new HashMap();
		fillTree(availableTree, modules);
		fillTree(deployedTree, deployed);

		setEnablement();
	}
	
	protected void updateTaskModel() {
		if (taskModel == null)
			return;

		taskModel.putObject(TaskModel.TASK_MODULES, getModuleMap());
		wizard.update();
	}

	/**
	 * Return true if this page is complete.
	 * @return boolean
	 */
	public boolean isPageComplete() {
		return true;
	}

	public List getModulesToRemove() {
		List list = new ArrayList();
		Iterator iterator = originalModules.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (!deployed.contains(module))
				list.add(module);
		}
		return list;
	}
	
	public List getModulesToAdd() {
		List list = new ArrayList();
		Iterator iterator = deployed.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (!originalModules.contains(module))
				list.add(module);
		}
		return list;
	}
	
	private void addChildMap(List map, IModule[] parents, IModule[] children) {
		if (children == null)
			return;
		
		int size = children.length;
		for (int i = 0; i < size; i++) {
			IModule module = children[i];
			
			int size2 = parents.length;
			IModule[] modules2 = new IModule[size2 + 1];
			System.arraycopy(parents, 0, modules2, 0, size2);
			modules2[size2] = module;
			map.add(modules2);
			
			IModule[] children2 = (IModule[]) childModuleMap.get(module);
			if (children2 != null)
				addChildMap(map, modules2, children2);
		}
	}

	public List getModuleMap() {
		final List map = new ArrayList();
	
		Iterator iterator = deployed.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			IModule[] moduleTree = new IModule[] { module };
			map.add(moduleTree);
			IModule[] children = (IModule[]) childModuleMap.get(module);
			if (children != null)
				addChildMap(map, moduleTree, children);
		}
		
		return map;
	}
}