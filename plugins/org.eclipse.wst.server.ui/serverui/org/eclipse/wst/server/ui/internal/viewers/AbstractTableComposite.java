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
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
/**
 * 
 */
public abstract class AbstractTableComposite extends Composite {
	protected Table table;
	protected TableViewer tableViewer;

	public AbstractTableComposite(Composite parent, int style) {
		super(parent, style);
		
		createWidgets();
	}

	protected void createWidgets() {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		setLayoutData(data);
		
		createTable();
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		table.setLayoutData(data);
		table.setLinesVisible(true);
		createTableViewer();
	}
	
	protected void createTable() {
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
	}
	
	protected void createTableViewer() {
		tableViewer = new LockedTableViewer(table);
	}

	protected TableViewer getTableViewer() {
		return tableViewer;
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}
	
	public void refresh() {
		tableViewer.refresh();
	}
	
	public void refresh(Object obj) {
		tableViewer.refresh(obj);
	}

	public void remove(Object obj) {
		tableViewer.remove(obj);
	}
}