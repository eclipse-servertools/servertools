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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * A wizard page used to add and remove modules.
 */
public class ModifyModulesComposite extends Composite {
	protected static final ILabelProvider slp = ServerUICore.getLabelProvider();
	
	protected IWizardHandle wizard;
	
	protected IServer server;
	protected boolean disabled = false;

	protected Map childModuleMap = new HashMap();
	protected Map parentTreeItemMap = new HashMap();

	protected List originalModules = new ArrayList();
	protected List modules = new ArrayList();
	protected List deployed = new ArrayList();

	protected Tree availableTree;
	protected Tree deployedTree;
	
	protected Button add, addAll;
	protected Button remove, removeAll;

	/**
	 * Create a new ModifyModulesWizardPage.
	 *
	 */
	public ModifyModulesComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
		
		wizard.setTitle(ServerUIPlugin.getResource("%wizModuleTitle"));
		wizard.setDescription(ServerUIPlugin.getResource("%wizModuleDescription"));
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_SELECT_SERVER));
		
		createControl();
	}
	
	public void setServer(IServer server) {
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
				deployed.add(currentModules[i]);
				originalModules.add(currentModules[i]);
			}
		}

		// get remaining modules
		Iterator iterator = ServerUtil.getModules().iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (!deployed.contains(module)) {
				IStatus status = server.canModifyModules(new IModule[] { module }, null);
				if (status != null && status.isOK())
					modules.add(module);
			}
		}

		// build child map
		iterator = deployed.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			try {
				List children = server.getChildModules(module);
				childModuleMap.put(module, children);
			} catch (Exception e) { }
		}

		iterator = modules.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			try {
				List children = server.getChildModules(module);
				childModuleMap.put(module, children);
			} catch (Exception e) { }
		}
		
		if (server.isAWorkingCopyDirty()) {
			wizard.setMessage(ServerUIPlugin.getResource("%errorCloseEditor", server.getName()), IMessageProvider.ERROR);
			disabled = true;
		}
		
		if (availableTree != null) {
			parentTreeItemMap = new HashMap();
			fillTree(availableTree, modules);
			fillTree(deployedTree, deployed);
			setEnablement();
		}
	}

	/**
	 * Creates the UI of the page.
	 *
	 * @param org.eclipse.swt.widgets.Composite parent
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
		WorkbenchHelp.setHelp(this, ContextIds.MODIFY_MODULES_COMPOSITE);

		Label label = new Label(this, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		label.setText(ServerUIPlugin.getResource("%wizModuleMessage"));

		label = new Label(this, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%wizModuleAvailableList"));
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		
		label = new Label(this, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%wizModuleDeployedList"));

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
		add.setText(ServerUIPlugin.getResource("%wizModuleAdd"));
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(false);
			}
		});
		
		remove = new Button(comp, SWT.PUSH);
		remove.setText(ServerUIPlugin.getResource("%wizModuleRemove"));
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(false);
			}
		});
		
		label = new Label(comp, SWT.NONE);
		label.setText("");
		
		addAll = new Button(comp, SWT.PUSH);
		addAll.setText(ServerUIPlugin.getResource("%wizModuleAddAll"));
		addAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(true);
			}
		});
		
		removeAll = new Button(comp, SWT.PUSH);
		removeAll.setText(ServerUIPlugin.getResource("%wizModuleRemoveAll"));
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
		if (disabled) {
			add.setEnabled(false);
			addAll.setEnabled(false);
			remove.setEnabled(false);
			removeAll.setEnabled(false);
			return;
		}

		boolean enabled = false;
		if (availableTree.getItemCount() > 0) {
			try {
				TreeItem item = availableTree.getSelection()[0];
				item = (TreeItem) parentTreeItemMap.get(item);
				IModule module = (IModule) item.getData();
				if (modules.contains(module))
					enabled = true;
			} catch (Exception e) { }
		}
		add.setEnabled(enabled);
		addAll.setEnabled(availableTree.getItemCount() > 0);
		
		enabled = false;
		if (deployedTree.getItemCount() > 0) {
			try {
				TreeItem item = deployedTree.getSelection()[0];
				item = (TreeItem) parentTreeItemMap.get(item);
				IModule module = (IModule) item.getData();
				if (deployed.contains(module))
					enabled = true;
			} catch (Exception e) { }
		}
		remove.setEnabled(enabled);
		removeAll.setEnabled(deployedTree.getItemCount() > 0);
	}

	protected void addChildren(TreeItem item, IModule module) {
		try {
			List children = (List) childModuleMap.get(module);
			Iterator iterator = children.iterator();
			while (iterator.hasNext()) {
				IModule child = (IModule) iterator.next();
				TreeItem childItem = new TreeItem(item, SWT.NONE);
				childItem.setText(slp.getText(child));
				childItem.setImage(slp.getImage(child));
				childItem.setData(child);
				parentTreeItemMap.put(childItem, item);
				addChildren(childItem, child);
			}
		} catch (Exception e) { }
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
			addChildren(item, module);
		}
	}

	protected void add(boolean all) {
		if (all)
			moveAll(availableTree.getItems(), true);
		else
			moveAll(availableTree.getSelection(), true);
	}

	protected void remove(boolean all) {
		if (all)
			moveAll(deployedTree.getItems(), false);
		else
			moveAll(deployedTree.getSelection(), false);
	}

	protected void moveAll(TreeItem[] items, boolean add2) {
		int size = items.length;
		List list = new ArrayList();
		for (int i = 0; i < size; i++) {
			TreeItem item = (TreeItem) parentTreeItemMap.get(items[i]);
			IModule module = (IModule) item.getData();
			
			if (!list.contains(module))
				list.add(module);
		}

		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			IModule module = (IModule) iterator.next();
			if (add2) {
				modules.remove(module);
				deployed.add(module);
			} else {
				modules.add(module);
				deployed.remove(module);
			}
		}

		parentTreeItemMap = new HashMap();
		fillTree(availableTree, modules);
		fillTree(deployedTree, deployed);

		setEnablement();
	}

	/**
	 * Return true if this page is complete.
	 * @return boolean
	 */
	public boolean isPageComplete() {
		return (!disabled);
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
}