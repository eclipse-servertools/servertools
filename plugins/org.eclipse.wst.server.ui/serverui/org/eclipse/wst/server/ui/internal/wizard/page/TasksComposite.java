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
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.wst.server.core.model.ModuleTaskDelegate;
import org.eclipse.wst.server.core.model.ServerTaskDelegate;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * A wizard page used to select server and module tasks.
 */
public class TasksComposite extends Composite {
	protected IWizardHandle wizard;
	
	protected Composite comp;

	// the list of elements to select from
	protected List tasks;

	/**
	 * Create a new TasksWizardPage.
	 *
	 * @param elements java.util.List
	 */
	public TasksComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.wizard = wizard;
	
		wizard.setTitle(ServerUIPlugin.getResource("%wizTaskTitle"));
		wizard.setDescription(ServerUIPlugin.getResource("%wizTaskDescription"));
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_SELECT_SERVER));
	}
	
	public void setTasks(List tasks) {
		this.tasks = tasks;
		
		Control[] children = getChildren();
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				children[i].dispose();
			}
		}
		
		createControl();
		layout(true);
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
		WorkbenchHelp.setHelp(this, ContextIds.SELECT_TASK_WIZARD);
		
		int size = 0;
		if (tasks != null)
			size = tasks.size();
		
		Object cont = null;
		Group group = null;
		int count = 0;
	
		for (int i = 0; i < size; i++) {
			Object obj = tasks.get(i);
			if (obj instanceof TasksWizardFragment.ServerTaskInfo) {
				final TasksWizardFragment.ServerTaskInfo sti = (TasksWizardFragment.ServerTaskInfo) obj;
				if (cont != sti.server) {
					if (group != null && count == 0)
						group.setEnabled(false);

					if (cont != null) {
						Label spacer = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
						GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
						data.horizontalSpan = 3;
						spacer.setLayoutData(data);
					}
					
					group = new Group(this, SWT.SHADOW_NONE);
					group.setText(ServerUIPlugin.getResource("%wizTaskDetail") + " " + ServerUICore.getLabelProvider().getText(sti.server));
					GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
					data.horizontalSpan = 3;
					group.setLayoutData(data);
					
					layout = new GridLayout();
					layout.horizontalSpacing = 0;
					layout.verticalSpacing = 3;
					layout.marginWidth = 5;
					layout.marginHeight = 5;
					group.setLayout(layout);

					cont = sti.server;
				}
				final Button checkbox = new Button(group, SWT.CHECK);
				checkbox.setText(sti.task2.getName());
				GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
				checkbox.setLayoutData(data);
				checkbox.setFocus();
			
				checkbox.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent event) {
						sti.setSelected(checkbox.getSelection());
					}
					public void widgetDefaultSelected(SelectionEvent event) {
						sti.setSelected(checkbox.getSelection());
					}
				});
				
				Label description = new Label(group, SWT.WRAP);
				data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
				data.heightHint = 50;
				data.horizontalIndent = 20;
				description.setLayoutData(data);
				description.setText(sti.task2.getDescription());
				
				byte status = sti.status;
				if (status == ServerTaskDelegate.TASK_COMPLETED) {
					checkbox.setEnabled(false);
					description.setEnabled(false);
				} else if (status == ServerTaskDelegate.TASK_PREFERRED) {
					checkbox.setSelection(true);
					count++;
				} else if (status == ServerTaskDelegate.TASK_MANDATORY) {
					checkbox.setSelection(true);
					checkbox.setEnabled(false);
					description.setEnabled(false);
				} else
					count++;
			} else if (obj instanceof TasksWizardFragment.ModuleTaskInfo) {
				final TasksWizardFragment.ModuleTaskInfo dti = (TasksWizardFragment.ModuleTaskInfo) obj;
				if (cont != dti.module) {
					if (group != null && count == 0)
						group.setEnabled(false);

					if (cont != null) {
						Label spacer = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
						GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
						data.horizontalSpan = 3;
						spacer.setLayoutData(data);
					}
					
					group = new Group(this, SWT.SHADOW_NONE);
					group.setText(ServerUIPlugin.getResource("%wizTaskDetail") + " " + ServerUICore.getLabelProvider().getText(dti.module));
					GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
					data.horizontalSpan = 3;
					group.setLayoutData(data);
					
					layout = new GridLayout();
					layout.horizontalSpacing = 0;
					layout.verticalSpacing = 3;
					layout.marginWidth = 5;
					layout.marginHeight = 5;
					group.setLayout(layout);

					cont = dti.module;
				}
				final Button checkbox = new Button(group, SWT.CHECK);
				checkbox.setText(dti.task2.getName());
				GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
				checkbox.setLayoutData(data);
				checkbox.setFocus();
				
				checkbox.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent event) {
						dti.setSelected(checkbox.getSelection());
					}
					public void widgetDefaultSelected(SelectionEvent event) {
						dti.setSelected(checkbox.getSelection());
					}
				});
				
				Label description = new Label(group, SWT.WRAP);
				data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
				data.heightHint = 50;
				data.horizontalIndent = 20;
				description.setLayoutData(data);
				description.setText(dti.task2.getDescription());
				
				byte status = dti.status;
				if (status == ModuleTaskDelegate.TASK_COMPLETED) {
					checkbox.setEnabled(false);
					description.setEnabled(false);
				} else if (status == ModuleTaskDelegate.TASK_PREFERRED) {
					checkbox.setSelection(true);
					count++;
				} else if (status == ModuleTaskDelegate.TASK_MANDATORY) {
					checkbox.setSelection(true);
					checkbox.setEnabled(false);
					description.setEnabled(false);
				} else
					count++;
			}
		}
		if (group != null && count == 0)
			group.setEnabled(false);

		if (size == 0) {
			Label label = new Label(this, SWT.NONE);
			label.setText(ServerUIPlugin.getResource("%wizTaskNone"));
		}
		
		Dialog.applyDialogFont(this);
	}
}
