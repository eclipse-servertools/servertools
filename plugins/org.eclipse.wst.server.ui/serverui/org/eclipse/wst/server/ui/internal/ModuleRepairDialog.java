package org.eclipse.wst.server.ui.internal;
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
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.TaskModel;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

/**
 * 
 */
public class ModuleRepairDialog extends Dialog {
	private static final String ROOT = "root";
	
	protected static final Object[] EMPTY = new Object[0];

	//protected List fixes;
	protected IServerLifecycleEvent[] events;
	protected List checkedList = new ArrayList();
	protected Map childMap = new HashMap();
	
	public class TaskInfo {
		ITask task;
		IServerLifecycleEvent parent;
		//boolean checked = true;
	}
	
	protected CheckboxTreeViewer viewer;
	
	// content and label provider for repair dialog
	public class RepairContentProvider implements ITreeContentProvider {
		public void dispose() { }
		
		public Object getParent(Object element) {
			if (element instanceof IServerLifecycleEvent)
				return ROOT;
			else if (element instanceof TaskInfo)
				return ((TaskInfo) element).parent;
			else
				return null;
		}
		
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
		
		public Object[] getChildren(Object element) {
			if (ROOT.equals(element)) {
				return events;
			} else if (element instanceof IServerLifecycleEvent) {
				IServerLifecycleEvent mse = (IServerLifecycleEvent) element;
				Object children = childMap.get(mse);
				if (children == null) {
					ITask[] tasks = mse.getTasks();
					int size = tasks.length;
					TaskInfo[] ti = new TaskInfo[size];
					for (int i = 0; i < size; i++) {
						ti[i] = new TaskInfo();
						ti[i].task = tasks[i];
						ti[i].parent = mse;
						
						ITaskModel taskModel = new TaskModel();
						taskModel.putObject(ITaskModel.TASK_SERVER, mse.getServer());
						IServerConfiguration config = mse.getServer().getServerConfiguration();
						if (config != null)
							taskModel.putObject(ITaskModel.TASK_SERVER_CONFIGURATION, config);
						tasks[i].setTaskModel(taskModel);
					}
					children = ti;
					childMap.put(mse, children);
				}
				return (Object[]) children;
			}
			return EMPTY;
		}
		
		public Object[] getElements(Object element) {
			return getChildren(element);
		}
		
		public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) { }
	}
	
	public class RepairLabelProvider implements ILabelProvider {
		public void addListener(ILabelProviderListener listener) { }
		
		public void removeListener(ILabelProviderListener listener) { }
		
		public Image getImage(Object element) {
			if (element instanceof IServerLifecycleEvent) {
				return ServerUICore.getLabelProvider().getImage(((IServerLifecycleEvent) element).getServer());
			} else if (element instanceof TaskInfo) {
				return ImageResource.getImage(ImageResource.IMG_REPAIR_CONFIGURATION);
			} else
				return null;
		}
		public String getText(Object element) {
			if (element instanceof IServerLifecycleEvent) {
				return ServerUICore.getLabelProvider().getText(((IServerLifecycleEvent) element).getServer());
			} else if (element instanceof TaskInfo) {
				return ((TaskInfo) element).task.getDescription();
			}
			return "";
		}
		
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		
		public void dispose() { }
	}

	public ModuleRepairDialog(Shell parent, IServerLifecycleEvent[] events) {
		super(parent);
		setShellStyle(SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL); // no close button
		setBlockOnOpen(true);
		
		this.events = events;
	}
	
	/* (non-Javadoc)
	 * Method declared in Window.
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ServerUIPlugin.getResource("%dialogRepairConfigurationTitle"));
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText(ServerUIPlugin.getResource("%dialogRepairConfigurationMessage"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		label.setLayoutData(data);
		
		Tree tree = new Tree(composite, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 140;
		data.widthHint = 400;
		tree.setLayoutData(data);
		
		viewer = new CheckboxTreeViewer(tree);
		viewer.setLabelProvider(new RepairLabelProvider());
		viewer.setContentProvider(new RepairContentProvider());
		viewer.setInput(ROOT);
		viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				checked(event.getElement(), event.getChecked());
				viewer.refresh();
			}
		});
		viewer.setSorter(new ViewerSorter() { });
		viewer.expandToLevel(4);
		
		int size = events.length;
		for (int i = 0; i < size; i++) {
			viewer.setSubtreeChecked(events[i], true);
			checked(events[i], true);
		}
		
		Dialog.applyDialogFont(composite);
		return composite;
	}
	
	protected void addRemove(Object obj, boolean add) {
		if (add) {
			if (!checkedList.contains(obj))
				checkedList.add(obj);
		} else {
			if (checkedList.contains(obj))
				checkedList.remove(obj);
		}
	}

	protected void checked(Object element, boolean checked) {
		if (element instanceof IServerLifecycleEvent) {
			addRemove(element, checked);
			viewer.setSubtreeChecked(element, checked);
			try {
				RepairContentProvider rcp = (RepairContentProvider) viewer.getContentProvider();
				Object[] children = rcp.getChildren(element);
				int size = children.length;
				for (int i = 0; i < size; i++)
					addRemove(((TaskInfo)children[i]).task, checked);
			} catch (Exception e) {
				// do nothing
			}
		} else if (element instanceof TaskInfo) {
			TaskInfo ti = (TaskInfo) element;
			addRemove(ti.task, checked);
			IServerLifecycleEvent mse = ti.parent;
			if (!checked) { // && anyChildren(info, !checked)) {
				addRemove(mse, checked);
				viewer.setChecked(mse, checked);
			} else if (checked && allChildren(mse, checked)) {
				addRemove(mse, checked);
				viewer.setChecked(mse, checked);
			}
		}
	}

	protected boolean allChildren(IServerLifecycleEvent mse, boolean b) {
		ITask[] tasks = mse.getTasks();
		if (tasks == null)
			return false;
		int size = tasks.length;
		for (int i = 0; i < size; i++) {
			if (b != checkedList.contains(tasks[i]))
				return false;
		}
		return true;
	}

	protected void okPressed() {
		super.okPressed();
		
		// perform commands
		int size = events.length;
		List list = new ArrayList();
		for (int i = 0; i < size; i++) {
			IServerLifecycleEvent mse = events[i];
			if (checkedList.contains(mse)) {
				IServer server = mse.getServer();
				if (EclipseUtil.validateEdit(getShell(), server)) {
					IServerWorkingCopy serverWC = server.getWorkingCopy();
					IServerConfigurationWorkingCopy configWC = null;
					if (server.getServerConfiguration() != null)
						configWC = server.getServerConfiguration().getWorkingCopy();
					
					ITaskModel taskModel = new TaskModel();
					taskModel.putObject(ITaskModel.TASK_SERVER, serverWC);
					if (configWC != null)
						taskModel.putObject(ITaskModel.TASK_SERVER_CONFIGURATION, configWC);
					
					try {
						//IServerConfiguration config = info.configuration;
						//IProgressMonitor monitor = new NullProgressMonitor();
						//IResource resource = ServerCore.getResourceManager().getServerResourceLocation(serverResource);
						//resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					} catch (Exception e) { }
					
					ITask[] tasks = mse.getTasks();
					int size2 = tasks.length;
					for (int j = 0; j < size2; j++) {
						if (checkedList.contains(tasks[j])) {
							try {
								tasks[j].setTaskModel(taskModel);
								tasks[j].execute(new NullProgressMonitor());
							} catch (CoreException ce) {
								Trace.trace(Trace.SEVERE, "Error executing repair task", ce);
							}
						}
					}
				
					try {
						serverWC.save(new NullProgressMonitor());
						if (configWC != null)
							configWC.save(new NullProgressMonitor());
					} catch (Exception e) { }
					
					if (!list.contains(server) && server.isWorkingCopiesExist())
						list.add(server);
				}
			}
		}
		
		// TODO reload affected editors
		/*size = list.size();
		for (int i = 0; i < size; i++) {
			IServerConfiguration config = (IServerConfiguration) list.get(i);
			GlobalCommandManager.getInstance().reload(config.getId(), new NullProgressMonitor());
			GlobalCommandManager.getInstance().resourceSaved(config.getId());
			GlobalCommandManager.getInstance().updateTimestamps(config.getId());
		}*/
	}
}