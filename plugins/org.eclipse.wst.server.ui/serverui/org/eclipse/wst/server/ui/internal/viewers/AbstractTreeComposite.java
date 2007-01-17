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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Tree;

import org.eclipse.wst.server.ui.internal.Messages;
/**
 * 
 */
public abstract class AbstractTreeComposite extends Composite {
	protected Tree tree;
	protected TreeViewer treeViewer;
	protected Label description;
	
	public AbstractTreeComposite(Composite parent, int style) {
		super(parent, style);
		
		createWidgets();
	}
	
	protected void createWidgets() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 3;
		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);
		
		String descriptionText = getDescriptionLabel();
		if (descriptionText != null) {
			Label label = new Label(this, SWT.WRAP);
			label.setText(descriptionText);
			GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 2;
			label.setLayoutData(data);
		}
		
		Label label = new Label(this, SWT.WRAP);
		label.setText(getTitleLabel());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		if (descriptionText != null)
			data.verticalIndent = 7;
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		String details = getDetailsLabel();
		if (details != null) {
			Link prefLink = new Link(this, SWT.NONE);
			data = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data.horizontalSpan = 2;
			prefLink.setLayoutData(data);
			prefLink.setText("<a>" + details + "</a>");
			prefLink.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					detailsSelected();
				}
			});
		}
		
		tree = new Tree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		tree.setLayoutData(data);
		
		treeViewer = new TreeViewer(tree);
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection s = (IStructuredSelection) event.getSelection();
				Object element = s.getFirstElement();
				if (treeViewer.isExpandable(element))
					treeViewer.setExpandedState(element, !treeViewer.getExpandedState(element));
		    }
		});
		
		label = new Label(this, SWT.NONE);
		label.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(data);
		
		// view composite
		Composite comp = new Composite(this, SWT.NONE);
		layout = new GridLayout();
		layout.horizontalSpacing = 3;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		comp.setLayout(layout);

		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		comp.setLayoutData(data);
		
		label = new Label(comp, SWT.NONE);
		label.setText(Messages.viewBy);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(data);
	
		final Combo combo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(getComboOptions());
		combo.select(1);
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int sel = combo.getSelectionIndex();
				viewOptionSelected((byte) sel);
			}
		});
		
		if (hasDescription()) {
			description = new Label(this, SWT.WRAP);
			description.setText("Multi\nLine\nMessage");
			Dialog.applyDialogFont(this);
			Point p = description.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			description.setText("");
			data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 2;
			if (p.y > 10)
				data.heightHint = p.y;
			else
				data.heightHint = 42;
			description.setLayoutData(data);
		}
		
		tree.forceFocus();
	}
	
	protected abstract String getDescriptionLabel();
	
	protected abstract String getTitleLabel();
	
	protected abstract String[] getComboOptions();
	
	protected boolean hasDescription() {
		return true;
	}

	protected void setDescription(String text) {
		if (description != null && text != null)
			description.setText(text);
	}

	protected abstract void viewOptionSelected(byte option);

	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}

	public void refresh() {
		treeViewer.refresh();
	}

	public void refresh(Object obj) {
		treeViewer.refresh(obj);
	}

	public void remove(Object obj) {
		treeViewer.remove(obj);
	}

	protected String getDetailsLabel() {
		return null;
	}

	protected void detailsSelected() {
		// do nothing
	}
}