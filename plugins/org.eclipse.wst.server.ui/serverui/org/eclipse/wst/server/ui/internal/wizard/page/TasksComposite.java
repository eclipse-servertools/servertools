/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.wizard.page;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.wst.server.ui.internal.wizard.fragment.TasksWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
/**
 * A wizard page used to select server and module tasks.
 */
public class TasksComposite extends ScrolledComposite {
	protected IWizardHandle wizard;

	// the list of elements to select from
	protected List tasks;

	protected boolean created;

	/**
	 * Create a new TasksComposite.
	 * 
	 * @param parent a parent composite
	 * @param wizard a wizard handle
	 */
	public TasksComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		this.wizard = wizard;
		
		wizard.setTitle(Messages.wizTaskTitle);
		wizard.setDescription(Messages.wizTaskDescription);
		wizard.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_SELECT_SERVER));
		
		//createControl();
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
		
		created = false;
	}

	/**
	 * Creates the UI of the page.
	 */
	public void createControl() {
		if (created)
			return;
		
		setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Composite comp = new Composite(this, SWT.NONE);
		setContent(comp);
		TasksLayout layout = new TasksLayout(SWTUtil.convertVerticalDLUsToPixels(this, 4));
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ContextIds.SELECT_TASK_WIZARD);
		
		int size = 0;
		if (tasks != null)
			size = tasks.size();
	
		for (int i = 0; i < size; i++) {
			Object obj = tasks.get(i);
			final TasksWizardFragment.TaskInfo sti = (TasksWizardFragment.TaskInfo) obj;
			final Button checkbox = new Button(comp, SWT.CHECK | SWT.WRAP);
			String label = sti.task2.getLabel();
			if (label != null)
				checkbox.setText(label);
			else
				checkbox.setText(Messages.elementUnknownName);
		
			checkbox.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					sti.setSelected(checkbox.getSelection());
				}
				public void widgetDefaultSelected(SelectionEvent event) {
					sti.setSelected(checkbox.getSelection());
				}
			});
			
			Label description = new Label(comp, SWT.WRAP);
			String desc = sti.task2.getDescription();
			if (desc != null)
				description.setText(desc);
			else
				description.setText(Messages.elementUnknownName);
			
			if (sti.kind == PublishOperation.REQUIRED) {
				checkbox.setSelection(true);
				checkbox.setEnabled(false);
				description.setEnabled(false);
			} else
				checkbox.setSelection(sti.isSelected());
		}
		
		if (size == 0)
			Trace.trace(Trace.SEVERE, "Task composite appeared with no tasks!");
		
		Dialog.applyDialogFont(this);
		
		setExpandHorizontal(true);
		setExpandVertical(true);
		
		setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		layout(true, true);
		
		created = true;
	}
}